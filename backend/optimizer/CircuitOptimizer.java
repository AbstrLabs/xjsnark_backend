/*      */ package backend.optimizer;
/*      */ 
/*      */ import backend.config.Config;
/*      */ import backend.eval.CircuitEvaluator;
/*      */ import backend.eval.Instruction;
/*      */ import backend.operations.WireLabelInstruction;
/*      */ import backend.operations.primitive.BasicOp;
/*      */ import backend.operations.primitive.ConstMulBasicOp;
/*      */ import backend.operations.primitive.MulBasicOp;
/*      */ import backend.optimizer.arithmetic.ExpressionMinimizer;
/*      */ import backend.optimizer.arithmetic.poly.MultivariatePolynomial;
/*      */ import backend.optimizer.arithmetic.poly.OptVariable;
/*      */ import backend.optimizer.arithmetic.poly.Term;
/*      */ import backend.resource.ResourceBundle;
/*      */ import backend.structure.CircuitGenerator;
/*      */ import backend.structure.Wire;
/*      */ import backend.structure.WireArray;
/*      */ import java.math.BigInteger;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.LinkedList;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Map;
/*      */ import java.util.Queue;
/*      */ import java.util.Set;
/*      */ import util.Util;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class CircuitOptimizer
/*      */ {
/*   45 */   private int inVarCounter = 0;
/*   46 */   private int outVarCounter = 0;
/*   47 */   private int newWireCount = 0;
/*   48 */   private int numProblemsOptimized = 0;
/*      */   
/*   50 */   private LinkedHashMap<Wire, Integer> useCounters = new LinkedHashMap<>();
/*   51 */   private LinkedHashMap<Wire, Boolean> toOverride = new LinkedHashMap<>();
/*   52 */   private LinkedHashSet<OptVariable> allOptVariables = new LinkedHashSet<>();
/*   53 */   private LinkedHashMap<OptVarPair, ArrayList<Problem>> optVarProblemMap = new LinkedHashMap<>();
/*   54 */   private LinkedHashMap<OptVariable, ArrayList<Wire>> optVarDependenciesMap = new LinkedHashMap<>();
/*   55 */   private LinkedHashMap<OptVariable, Wire> optVarWireMap = new LinkedHashMap<>();
/*      */   
/*   57 */   private LinkedHashMap<Wire, Problem> problemMap = new LinkedHashMap<>();
/*   58 */   private LinkedHashMap<Wire, Instruction[]> originalEvalSequenceMap = (LinkedHashMap)new LinkedHashMap<>();
/*      */   
/*   60 */   private LinkedHashMap<Wire, MultivariatePolynomial> mvpMap = new LinkedHashMap<>();
/*      */   private LinkedHashMap<Instruction, Instruction> evalSequence;
/*      */   private LinkedHashMap<Instruction, Instruction> tmpEvalSequence;
/*      */   private LinkedHashMap<Instruction, Instruction> newEvalSequence;
/*   64 */   private HashMap<Integer, Integer> newToOldIndexMap = new HashMap<>(16, 1.0F);
/*      */   
/*   66 */   ArrayList<Problem> combinedProblems = new ArrayList<>();
/*      */   
/*      */   private CircuitGenerator generator;
/*      */   
/*      */   private int numOriginalMulGates;
/*      */   private int numOriginalIns;
/*      */   private int numOriginalOuts;
/*      */   private int numOriginalWitnesses;
/*      */   private int numOriginalSplits;
/*      */   private int numOriginalPacks;
/*      */   private int numOriginalAssertions;
/*      */   private int numOriginalNonzeroChecks;
/*      */   
/*      */   private class OptVarPair
/*      */   {
/*      */     OptVariable v1;
/*      */     OptVariable v2;
/*      */     
/*      */     public OptVarPair(OptVariable v1, OptVariable v2) {
/*   85 */       this.v1 = v1;
/*   86 */       this.v2 = v2;
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean equals(Object obj) {
/*   91 */       if (obj == this)
/*   92 */         return true; 
/*   93 */       if (!(obj instanceof OptVarPair)) {
/*   94 */         return false;
/*      */       }
/*   96 */       OptVarPair p = (OptVarPair)obj;
/*   97 */       if (p.v2 != null && this.v2 != null)
/*   98 */         return !((!p.v1.equals(this.v1) || !p.v2.equals(this.v2)) && (
/*   99 */           !p.v1.equals(this.v2) || !p.v2.equals(this.v1))); 
/*  100 */       if (this.v1 != null) {
/*  101 */         return p.v1.equals(this.v1);
/*      */       }
/*  103 */       return true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  110 */       if (this.v2 != null)
/*  111 */         return this.v1.hashCode() + this.v2.hashCode(); 
/*  112 */       if (this.v1 != null) {
/*  113 */         return this.v1.hashCode();
/*      */       }
/*  115 */       return 1;
/*      */     }
/*      */ 
/*      */     
/*      */     public String toString() {
/*  120 */       String s = "";
/*  121 */       if (this.v1 == null) {
/*  122 */         s = String.valueOf(s) + "null ,";
/*      */       } else {
/*  124 */         s = String.valueOf(s) + this.v1.toString() + ", ";
/*      */       } 
/*  126 */       if (this.v2 == null) {
/*  127 */         s = String.valueOf(s) + "null";
/*      */       } else {
/*  129 */         s = String.valueOf(s) + this.v2.toString();
/*      */       } 
/*  131 */       return s;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  138 */   private int totalSavings = 0;
/*  139 */   private int originalEvals = 0;
/*      */   
/*      */   public class Problem {
/*  142 */     private Problem superProblem = null;
/*      */     
/*      */     private ArrayList<OptVariable> variables;
/*      */     
/*      */     private ArrayList<CircuitOptimizer.OptVarPair> optVarPairs;
/*      */     
/*      */     private HashMap<Wire, MultivariatePolynomial> mvpList;
/*      */     
/*      */     private HashSet<Wire> keyWireList;
/*      */     
/*      */     private ArrayList<Problem> subProblems;
/*      */     
/*      */     private Wire key;
/*      */     
/*      */     private MultivariatePolynomial mvp;
/*      */     private LinkedHashMap<Instruction, Instruction> originalEvalSequence;
/*      */     private Instruction[] originalEvalSequenceArray;
/*  159 */     private int originalNumMulGates = 0;
/*  160 */     private int optimizedNumMulGates = -1;
/*      */     private HashMap<String, MultivariatePolynomial> solutions;
/*      */     private boolean integrated;
/*      */     private boolean dontSolve = false;
/*      */     
/*      */     Problem() {
/*  166 */       this.superProblem = this;
/*      */     }
/*      */ 
/*      */     
/*      */     Problem(Wire key, MultivariatePolynomial poly) {
/*  171 */       this.key = key;
/*  172 */       this.mvp = poly;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  180 */       HashSet<OptVariable> variableSet = new HashSet<>(1, 1.0F);
/*      */       
/*  182 */       for (Term t : poly.getTerms())
/*      */       {
/*  184 */         variableSet.addAll(t.getVars());
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  189 */       HashSet<CircuitOptimizer.OptVarPair> optVarPairsSet = new HashSet<>(1, 1.0F);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  196 */       if (variableSet.size() == 0) {
/*  197 */         CircuitOptimizer.OptVarPair optVarPair = new CircuitOptimizer.OptVarPair(null, null);
/*  198 */         optVarPairsSet.add(optVarPair);
/*  199 */         ArrayList<Problem> list = (ArrayList<Problem>)CircuitOptimizer.this.optVarProblemMap.get(optVarPair);
/*  200 */         if (list == null) {
/*  201 */           list = new ArrayList<>(1);
/*  202 */           CircuitOptimizer.this.optVarProblemMap.put(optVarPair, list);
/*      */         } 
/*  204 */         list.add(this);
/*  205 */         list.trimToSize();
/*      */       } 
/*      */       
/*  208 */       OptVariable tmp = null;
/*      */       
/*  210 */       for (Term t : poly.getTerms()) {
/*      */         
/*  212 */         Collection<OptVariable> s = t.getVars();
/*      */         
/*  214 */         variableSet.addAll(s);
/*  215 */         int to = 0;
/*  216 */         for (OptVariable v1 : s) {
/*  217 */           int index = 0;
/*  218 */           tmp = v1;
/*      */           
/*  220 */           if (t.getExponent(v1) > 1) {
/*  221 */             CircuitOptimizer.OptVarPair optVarPair = new CircuitOptimizer.OptVarPair(v1, v1);
/*  222 */             optVarPairsSet.add(optVarPair);
/*  223 */             ArrayList<Problem> list = (ArrayList<Problem>)CircuitOptimizer.this.optVarProblemMap
/*  224 */               .get(optVarPair);
/*  225 */             if (list == null) {
/*  226 */               list = new ArrayList<>(1);
/*  227 */               CircuitOptimizer.this.optVarProblemMap.put(optVarPair, list);
/*      */             } 
/*  229 */             list.add(this);
/*  230 */             list.trimToSize();
/*      */           } 
/*  232 */           for (OptVariable v2 : s) {
/*  233 */             if (index++ >= to) {
/*      */               break;
/*      */             }
/*  236 */             CircuitOptimizer.OptVarPair optVarPair = new CircuitOptimizer.OptVarPair(v1, v2);
/*  237 */             optVarPairsSet.add(optVarPair);
/*  238 */             ArrayList<Problem> list = (ArrayList<Problem>)CircuitOptimizer.this.optVarProblemMap
/*  239 */               .get(optVarPair);
/*  240 */             if (list == null) {
/*  241 */               list = new ArrayList<>(1);
/*  242 */               CircuitOptimizer.this.optVarProblemMap.put(optVarPair, list);
/*      */             } 
/*  244 */             list.add(this);
/*  245 */             list.trimToSize();
/*      */           } 
/*  247 */           to++;
/*      */         } 
/*      */       } 
/*      */       
/*  251 */       if (optVarPairsSet.size() == 0 && tmp != null) {
/*      */ 
/*      */         
/*  254 */         CircuitOptimizer.OptVarPair optVarPair = new CircuitOptimizer.OptVarPair(tmp, null);
/*  255 */         optVarPairsSet.add(optVarPair);
/*  256 */         ArrayList<Problem> list = (ArrayList<Problem>)CircuitOptimizer.this.optVarProblemMap.get(optVarPair);
/*  257 */         if (list == null) {
/*  258 */           list = new ArrayList<>(1);
/*  259 */           CircuitOptimizer.this.optVarProblemMap.put(optVarPair, list);
/*      */         } 
/*  261 */         list.add(this);
/*  262 */         list.trimToSize();
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  292 */       this.variables = new ArrayList<>(variableSet);
/*  293 */       this.optVarPairs = new ArrayList<>(optVarPairsSet);
/*  294 */       this.superProblem = this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String toString() {
/*  317 */       String s = "";
/*  318 */       for (Wire w : this.mvpList.keySet()) {
/*  319 */         s = String.valueOf(s) + w + ":" + this.mvpList.get(w) + "\n";
/*      */       }
/*      */       
/*  322 */       return s;
/*      */     }
/*      */ 
/*      */     
/*      */     Problem(Collection<Problem> list) {
/*  327 */       this.mvpList = new LinkedHashMap<>();
/*  328 */       HashSet<OptVariable> variableSet = new HashSet<>();
/*      */ 
/*      */ 
/*      */       
/*  332 */       for (Problem p : list) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  340 */         variableSet.addAll(p.variables);
/*      */         
/*  342 */         if (p.mvpList == null) {
/*  343 */           if (p.key != null)
/*  344 */             this.mvpList.put(p.key, p.mvp); 
/*      */         } else {
/*  346 */           this.mvpList.putAll(p.mvpList);
/*      */         } 
/*      */         
/*  349 */         p.setSuperProblem(this);
/*  350 */         p.variables = null;
/*  351 */         p.mvpList = null;
/*  352 */         p.optVarPairs = null;
/*      */       } 
/*      */       
/*  355 */       this.superProblem = this;
/*      */       
/*  357 */       this.variables = new ArrayList<>(variableSet);
/*      */     }
/*      */ 
/*      */     
/*      */     Problem getSuperProblem() {
/*  362 */       Problem p = this;
/*  363 */       while (p.superProblem != p) {
/*  364 */         p = p.superProblem;
/*      */       }
/*  366 */       this.superProblem = p;
/*  367 */       return p;
/*      */     }
/*      */     
/*      */     void setSuperProblem(Problem p) {
/*  371 */       this.superProblem = p;
/*  372 */       if (this.subProblems != null) {
/*  373 */         for (Problem subProblem : this.subProblems) {
/*  374 */           subProblem.setSuperProblem(p);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */     boolean isEmpty() {
/*  380 */       return (this.mvpList.size() == 0);
/*      */     }
/*      */     
/*      */     public void setSolutions(HashMap<String, MultivariatePolynomial> sols) {
/*  384 */       if (sols == null) {
/*      */         return;
/*      */       }
/*  387 */       this.solutions = sols;
/*  388 */       this.optimizedNumMulGates = 0;
/*  389 */       for (MultivariatePolynomial mvp : this.solutions.values()) {
/*  390 */         this.optimizedNumMulGates += mvp.getCost();
/*      */       }
/*      */       
/*  393 */       if (this.optimizedNumMulGates < this.originalNumMulGates) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  415 */         CircuitOptimizer.this.totalSavings = CircuitOptimizer.this.totalSavings + this.originalNumMulGates - this.optimizedNumMulGates;
/*  416 */         this.originalEvalSequence = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  423 */         this.originalEvalSequenceArray = null;
/*  424 */         CircuitOptimizer.this.numProblemsOptimized = CircuitOptimizer.this.numProblemsOptimized + 1;
/*  425 */         this.keyWireList = new HashSet<>();
/*  426 */         for (Wire w : this.mvpList.keySet()) {
/*  427 */           this.keyWireList.add(w);
/*      */         }
/*  429 */         this.mvpList = null;
/*      */       } else {
/*  431 */         this.solutions = null;
/*  432 */         this.variables = null;
/*  433 */         this.optVarPairs = null;
/*      */         
/*  435 */         this.subProblems = null;
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public ExpressionMinimizer prep() {
/*  441 */       ArrayList<MultivariatePolynomial> list = new ArrayList<>();
/*  442 */       String[] inputVarsStrings = new String[this.variables.size()];
/*  443 */       String[] outVarsStrings = new String[this.mvpList.size()];
/*      */       
/*  445 */       int i = 0;
/*  446 */       for (Map.Entry<Wire, MultivariatePolynomial> e : this.mvpList.entrySet()) {
/*  447 */         list.add(e.getValue());
/*  448 */         outVarsStrings[i++] = "w" + ((Wire)e.getKey()).toString();
/*      */       } 
/*  450 */       i = 0;
/*  451 */       for (OptVariable var : this.variables) {
/*  452 */         inputVarsStrings[i++] = var.toString();
/*      */       }
/*      */       
/*  455 */       return new ExpressionMinimizer(inputVarsStrings, outVarsStrings, 
/*  456 */           list);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void solve() {
/*  463 */       long t1 = System.currentTimeMillis();
/*  464 */       ArrayList<MultivariatePolynomial> list = new ArrayList<>();
/*  465 */       String[] inputVarsStrings = new String[this.variables.size()];
/*  466 */       String[] outVarsStrings = new String[this.mvpList.size()];
/*      */       
/*  468 */       int i = 0;
/*  469 */       for (Map.Entry<Wire, MultivariatePolynomial> e : this.mvpList.entrySet()) {
/*  470 */         list.add(e.getValue());
/*  471 */         outVarsStrings[i++] = "w" + ((Wire)e.getKey()).toString();
/*      */       } 
/*      */       
/*  474 */       i = 0;
/*  475 */       for (OptVariable var : this.variables) {
/*  476 */         inputVarsStrings[i++] = var.toString();
/*      */       }
/*      */       
/*  479 */       this.solutions = (new ExpressionMinimizer(inputVarsStrings, 
/*  480 */           outVarsStrings, list, 1)).getSolution();
/*      */       
/*  482 */       this.optimizedNumMulGates = 0;
/*  483 */       for (MultivariatePolynomial mvp : this.solutions.values()) {
/*  484 */         this.optimizedNumMulGates += mvp.getCost();
/*      */       }
/*      */       
/*  487 */       if (this.optimizedNumMulGates < this.originalNumMulGates) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  494 */         CircuitOptimizer.this.totalSavings = CircuitOptimizer.this.totalSavings + this.originalNumMulGates - this.optimizedNumMulGates;
/*      */       }
/*      */       else {
/*      */         
/*  498 */         this.solutions = null;
/*  499 */         this.variables = null;
/*  500 */         this.mvpList = null;
/*  501 */         CircuitOptimizer.this.mvpMap = null;
/*      */       } 
/*  503 */       long t2 = System.currentTimeMillis();
/*  504 */       System.out.println("Time spent = " + (t2 - t1) + ", " + 
/*  505 */           this.mvpList.size() + " " + this.variables.size() + " " + 
/*  506 */           "savings: = " + (
/*  507 */           this.originalNumMulGates - this.optimizedNumMulGates));
/*      */       
/*  509 */       throw new RuntimeException(
/*  510 */           "Method should not be called in this release");
/*      */     }
/*      */ 
/*      */     
/*      */     public void integrateEvalSequence() {
/*  515 */       if (this.integrated) {
/*      */         return;
/*      */       }
/*      */ 
/*      */       
/*  520 */       if (this.optimizedNumMulGates == -1 || 
/*  521 */         this.optimizedNumMulGates >= this.originalNumMulGates) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  533 */         for (int i = this.originalEvalSequenceArray.length - 1; i >= 0; i--) {
/*  534 */           CircuitOptimizer.this.newEvalSequence.put(this.originalEvalSequenceArray[i], 
/*  535 */               this.originalEvalSequenceArray[i]);
/*      */         }
/*  537 */         CircuitOptimizer.this.originalEvals = CircuitOptimizer.this.originalEvals + 1;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  552 */         HashMap<String, Wire> wireRecord = new HashMap<>();
/*  553 */         HashMap<String, Boolean> state = new HashMap<>();
/*      */ 
/*      */ 
/*      */         
/*  557 */         for (OptVariable var : this.variables) {
/*  558 */           wireRecord.put(var.toString(), (Wire)CircuitOptimizer.this.optVarWireMap.get(var));
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  563 */           if (!this.keyWireList.contains(CircuitOptimizer.this.optVarWireMap.get(var)))
/*      */           {
/*  565 */             state.put(var.toString(), Boolean.valueOf(true));
/*      */           }
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  573 */         for (Wire w : this.keyWireList)
/*      */         {
/*      */ 
/*      */ 
/*      */           
/*  578 */           wireRecord.put("w" + w.toString(), w);
/*      */         }
/*      */ 
/*      */         
/*  582 */         int numConstraintsPrev = CircuitOptimizer.this.generator.__getNumOfConstraints();
/*      */         
/*  584 */         for (String key : this.solutions.keySet())
/*      */         {
/*  586 */           recursiveResolve(wireRecord, state, key);
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  593 */         int i = CircuitOptimizer.this.generator.__getNumOfConstraints();
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  598 */       this.integrated = true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void recursiveResolve(HashMap<String, Wire> wireRecord, HashMap<String, Boolean> state, String key) {
/*  606 */       if (state.get(key) == null || !((Boolean)state.get(key)).booleanValue()) {
/*      */         
/*  608 */         MultivariatePolynomial mvp = this.solutions.get(key);
/*      */         
/*  610 */         Wire[] termWires = new Wire[mvp.getTerms().size()];
/*      */ 
/*      */         
/*  613 */         int idx = 0;
/*      */         
/*  615 */         for (Term t : mvp.getTerms()) {
/*      */           
/*  617 */           for (OptVariable var : t.getVars()) {
/*  618 */             if (state.get(var.toString()) == null || 
/*  619 */               !((Boolean)state.get(var.toString())).booleanValue()) {
/*  620 */               recursiveResolve(wireRecord, state, var.toString());
/*      */             }
/*      */           } 
/*      */ 
/*      */           
/*  625 */           if (t.getCoeff().bitLength() > 
/*  626 */             Config.getNumBitsFiniteFieldModulus() - 2) {
/*  627 */             termWires[idx] = CircuitOptimizer.this.generator.__createConstantWire(
/*  628 */                 Config.getFiniteFieldModulus().subtract(t.getCoeff())
/*  629 */                 .negate(), new String[0]);
/*      */           } else {
/*      */             
/*  632 */             termWires[idx] = CircuitOptimizer.this.generator.__createConstantWire(t
/*  633 */                 .getCoeff(), new String[0]);
/*      */           } 
/*  635 */           Term.VarIterator it = t.getVarIterator();
/*  636 */           while (it.hasNext()) {
/*  637 */             OptVariable v = it.next();
/*  638 */             int power = it.getExponent();
/*  639 */             if (power <= 0)
/*  640 */               throw new IllegalArgumentException(
/*  641 */                   "Unexpected case (sanity check failed) - Please report this case."); 
/*  642 */             if (power == 1) {
/*      */               
/*  644 */               termWires[idx] = termWires[idx].mul(wireRecord
/*  645 */                   .get(v.toString()), new String[0]); continue;
/*      */             } 
/*  647 */             Wire powered = exp(wireRecord.get(v.toString()), 
/*  648 */                 power);
/*  649 */             termWires[idx] = termWires[idx].mul(powered, new String[0]);
/*      */           } 
/*      */ 
/*      */           
/*  653 */           idx++;
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  671 */         if (termWires.length > 1) {
/*      */ 
/*      */           
/*  674 */           Wire result = (new WireArray(termWires)).sumAllElements(new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  679 */           if (key.startsWith("w"))
/*      */           {
/*  681 */             Instruction ii = CircuitOptimizer.this.generator.__getLastInstructionAdded();
/*      */ 
/*      */             
/*  684 */             BasicOp op = (BasicOp)ii;
/*      */ 
/*      */ 
/*      */             
/*  688 */             op.getOutputs()[0] = wireRecord.get(key);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           }
/*      */           else
/*      */           {
/*      */ 
/*      */ 
/*      */             
/*  699 */             wireRecord.put(key, result);
/*      */           
/*      */           }
/*      */         
/*      */         }
/*  704 */         else if (key.startsWith("w")) {
/*      */ 
/*      */ 
/*      */           
/*  708 */           Instruction ii = CircuitOptimizer.this.generator.__getLastInstructionAdded();
/*      */           
/*  710 */           if (ii instanceof BasicOp) {
/*  711 */             BasicOp basicOp = (BasicOp)ii;
/*  712 */             if ((basicOp.getOutputs()).length != 1 || basicOp.getOutputs()[0] != termWires[0]) {
/*  713 */               throw new RuntimeException("Unexpected case (sanity check failed). Please report this case.");
/*      */             }
/*      */           } else {
/*  716 */             throw new RuntimeException("Unexpected case (sanity check failed). Please report this case.");
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  724 */           BasicOp op = (BasicOp)ii;
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  729 */           op.getOutputs()[0] = wireRecord.get(key);
/*      */         } else {
/*  731 */           Wire result = termWires[0];
/*  732 */           wireRecord.put(key, result);
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  745 */         state.put(key, Boolean.valueOf(true));
/*      */       } 
/*      */     }
/*      */     
/*      */     private Wire exp(Wire wire, int power) {
/*  750 */       if (power == 0)
/*  751 */         return CircuitOptimizer.this.generator.__getOneWire(); 
/*  752 */       if (power == 1)
/*  753 */         return wire; 
/*  754 */       if (power % 2 == 0) {
/*  755 */         return exp(wire.mul(wire, new String[0]), power / 2);
/*      */       }
/*  757 */       return exp(wire.mul(wire, new String[0]), power / 2).mul(wire, new String[0]);
/*      */     }
/*      */ 
/*      */     
/*      */     public void print() {
/*  762 */       System.out.println("Printing Problem");
/*  763 */       for (Map.Entry<Wire, MultivariatePolynomial> e : this.mvpList.entrySet()) {
/*  764 */         System.out.println("w" + ((Wire)e.getKey()).toString() + " = " + 
/*  765 */             e.getValue());
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void constructOriginalSubcircuit() {
/*  776 */       this.originalEvalSequence = new LinkedHashMap<>();
/*  777 */       Queue<Wire> traverseBackQueue = new LinkedList<>();
/*      */       
/*  779 */       if (this.mvpList == null) {
/*  780 */         this.mvpList = new HashMap<>();
/*  781 */         this.mvpList.put(this.key, this.mvp);
/*      */       } 
/*      */       
/*  784 */       for (Wire w : this.mvpList.keySet())
/*      */       {
/*  786 */         traverseBackQueue.add(w);
/*      */       }
/*      */ 
/*      */       
/*  790 */       while (!traverseBackQueue.isEmpty()) {
/*  791 */         Wire w = traverseBackQueue.poll();
/*  792 */         OptVariable v = new OptVariable("w", w.getWireId());
/*  793 */         if (!this.mvpList.containsKey(w) && this.variables.contains(v)) {
/*      */           continue;
/*      */         }
/*  796 */         if (CircuitOptimizer.this.allOptVariables.contains(v) && !this.variables.contains(v) && 
/*  797 */           !this.mvpList.containsKey(w))
/*      */         {
/*  799 */           this.dontSolve = true;
/*      */         }
/*      */ 
/*      */         
/*  803 */         if (!this.variables.contains(v) || (
/*  804 */           this.variables.contains(v) && this.mvpList.containsKey(w))) {
/*  805 */           Instruction instruction = w.getSrcInstruction();
/*      */ 
/*      */           
/*  808 */           if (instruction != null && 
/*  809 */             instruction instanceof BasicOp && 
/*  810 */             !(instruction instanceof backend.operations.primitive.SplitBasicOp) && 
/*  811 */             !(instruction instanceof backend.operations.primitive.PackBasicOp) && !(instruction instanceof backend.operations.primitive.NonZeroCheckBasicOp)) {
/*  812 */             BasicOp op = (BasicOp)instruction;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  818 */             if (!this.originalEvalSequence.containsKey(op)) {
/*      */               
/*  820 */               this.originalEvalSequence.put(op, op); byte b; int i; Wire[] arrayOfWire;
/*  821 */               for (i = (arrayOfWire = op.getInputs()).length, b = 0; b < i; ) { Wire w2 = arrayOfWire[b];
/*      */                 
/*  823 */                 traverseBackQueue.add(w2); b++; }
/*      */               
/*  825 */               if (!(op instanceof MulBasicOp) || (op.getInputs()[0] != CircuitOptimizer.this.generator
/*  826 */                 .__getOneWire() && op.getInputs()[1] != CircuitOptimizer.this.generator
/*  827 */                 .__getOneWire())) {
/*  828 */                 this.originalNumMulGates += op.getNumMulGates();
/*      */               }
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  874 */       ListIterator<Instruction> iterator = (new ArrayList<>(
/*  875 */           this.originalEvalSequence.keySet()))
/*  876 */         .listIterator(this.originalEvalSequence.size());
/*      */       
/*  878 */       this.originalEvalSequenceArray = 
/*  879 */         new Instruction[this.originalEvalSequence.size()];
/*  880 */       int idx = this.originalEvalSequenceArray.length - 1;
/*  881 */       while (iterator.hasPrevious()) {
/*  882 */         Instruction i = iterator.previous();
/*      */         
/*  884 */         this.originalEvalSequenceArray[idx--] = i;
/*      */       } 
/*  886 */       this.originalEvalSequence = null;
/*      */     }
/*      */     
/*      */     public boolean checkCompletedUsageIntermediateWires() {
/*  890 */       constructOriginalSubcircuit();
/*  891 */       boolean allFree = true; byte b; int i; Instruction[] arrayOfInstruction;
/*  892 */       for (i = (arrayOfInstruction = this.originalEvalSequenceArray).length, b = 0; b < i; ) { Instruction instruction = arrayOfInstruction[b]; byte b1; int j; Wire[] arrayOfWire;
/*  893 */         for (j = (arrayOfWire = instruction.getUsedWires()).length, b1 = 0; b1 < j; ) { Wire w = arrayOfWire[b1];
/*  894 */           if (!this.mvpList.containsKey(w) && (
/*  895 */             (Integer)CircuitOptimizer.this.useCounters.get(w)).intValue() != 0 && 
/*  896 */             !(w instanceof backend.structure.ConstantWire) && 
/*  897 */             !w.equals(CircuitOptimizer.this.generator.__getOneWire()))
/*      */           {
/*      */             
/*  900 */             allFree = false;
/*      */           }
/*      */           b1++; }
/*      */         
/*      */         b++; }
/*      */       
/*  906 */       this.originalEvalSequenceArray = null;
/*  907 */       return allFree;
/*      */     }
/*      */     
/*      */     public ArrayList<Wire> getIntermediateWires() {
/*  911 */       ArrayList<Wire> list = new ArrayList<>(); byte b; int i; Instruction[] arrayOfInstruction;
/*  912 */       for (i = (arrayOfInstruction = this.originalEvalSequenceArray).length, b = 0; b < i; ) { Instruction instruction = arrayOfInstruction[b]; byte b1; int j; Wire[] arrayOfWire;
/*  913 */         for (j = (arrayOfWire = instruction.getUsedWires()).length, b1 = 0; b1 < j; ) { Wire w = arrayOfWire[b1];
/*  914 */           if (!this.mvpList.containsKey(w) && !(w instanceof backend.structure.ConstantWire) && 
/*  915 */             !w.equals(CircuitOptimizer.this.generator.__getOneWire()))
/*  916 */             list.add(w);  b1++; }
/*      */         
/*      */         b++; }
/*      */       
/*  920 */       return list;
/*      */     }
/*      */     
/*      */     public Collection<OptVariable> getVariables() {
/*  924 */       return this.variables;
/*      */     }
/*      */     
/*      */     public HashMap<Wire, MultivariatePolynomial> getMvpList() {
/*  928 */       return this.mvpList;
/*      */     }
/*      */     
/*      */     public boolean isDontSolve() {
/*  932 */       return this.dontSolve;
/*      */     }
/*      */   }
/*      */   
/*      */   public CircuitOptimizer(CircuitGenerator generator) {
/*  937 */     this.evalSequence = generator.__getEvaluationQueue();
/*      */ 
/*      */ 
/*      */     
/*  941 */     this.generator = generator;
/*  942 */     this.numOriginalMulGates = generator.__getNumOfConstraints();
/*  943 */     this.tmpEvalSequence = new LinkedHashMap<>();
/*  944 */     run();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void run() {
/*  951 */     BasicOp.setDisableCachingForLinearOps(true);
/*  952 */     firstPass();
/*      */     
/*  954 */     System.out.println("[Arithmetic Optimizer] First Stage Done");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  963 */     secondPass();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  968 */     System.out.println("[Arithmetic Optimizer] Second Stage Done");
/*      */ 
/*      */     
/*  971 */     thirdPass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void groupProblems() {
/*  982 */     if (!Config.arithOptimizerIncrementalMode) {
/*  983 */       System.out.println("[Arithmetic Optimizer] Grouping Problems .. ");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  992 */     HashMap<OptVarPair, Boolean> state = new HashMap<>();
/*  993 */     for (OptVarPair p : this.optVarProblemMap.keySet())
/*      */     {
/*  995 */       state.put(p, Boolean.valueOf(false));
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1002 */     int numProcessed = 0;
/*      */     
/* 1004 */     HashSet<Problem> problemCollection = new LinkedHashSet<>();
/* 1005 */     HashSet<Problem> visitedProblems = new HashSet<>();
/*      */     
/* 1007 */     Queue<OptVarPair> varQueue = new LinkedList<>();
/*      */ 
/*      */     
/* 1010 */     for (OptVarPair pair : this.optVarProblemMap.keySet()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1019 */       if (!((Boolean)state.get(pair)).booleanValue()) {
/*      */         
/* 1021 */         state.put(pair, Boolean.valueOf(true));
/* 1022 */         numProcessed++;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1030 */         problemCollection.clear();
/* 1031 */         varQueue.clear();
/* 1032 */         ArrayList<Problem> list = this.optVarProblemMap.get(pair);
/* 1033 */         problemCollection.addAll(list);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1045 */         for (Problem p : list) {
/*      */           
/* 1047 */           if (!visitedProblems.contains(p)) {
/* 1048 */             visitedProblems.add(p);
/* 1049 */             varQueue.addAll(p.optVarPairs);
/*      */           } 
/*      */         } 
/*      */         
/* 1053 */         while (!varQueue.isEmpty()) {
/* 1054 */           OptVarPair pair2 = varQueue.poll();
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1059 */           if (!((Boolean)state.get(pair2)).booleanValue()) {
/*      */             
/* 1061 */             list = this.optVarProblemMap.get(pair2);
/* 1062 */             problemCollection.addAll(list);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1074 */             state.put(pair2, Boolean.valueOf(true));
/*      */ 
/*      */             
/* 1077 */             for (Problem p : list) {
/*      */               
/* 1079 */               if (visitedProblems.contains(p)) {
/*      */                 continue;
/*      */               }
/* 1082 */               visitedProblems.add(p);
/*      */ 
/*      */               
/* 1085 */               for (OptVarPair pair3 : p.optVarPairs) {
/* 1086 */                 if (!((Boolean)state.get(pair3)).booleanValue()) {
/* 1087 */                   varQueue.add(pair3);
/*      */                 }
/*      */               } 
/*      */             } 
/*      */             
/* 1092 */             numProcessed++;
/*      */           } 
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/* 1098 */         this.combinedProblems.add(new Problem(problemCollection));
/*      */       } 
/*      */     } 
/*      */     
/* 1102 */     if (!Config.arithOptimizerIncrementalMode) {
/* 1103 */       System.out
/* 1104 */         .println("[Arithmetic Optimizer] Done with Grouping Problems");
/* 1105 */       System.out
/* 1106 */         .println("[Arithmetic Optimizer] Number of problems after clustering: " + 
/* 1107 */           this.combinedProblems.size());
/*      */     } else {
/* 1109 */       System.out
/* 1110 */         .println("[Arithmetic Optimizer] Number of remaining problems: " + 
/* 1111 */           this.combinedProblems.size());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void firstPass() {
/* 1126 */     System.out
/* 1127 */       .println("[Arithmetic Optimizer] Starting First Optimization Stage");
/*      */     
/* 1129 */     int counter = 0;
/*      */     
/* 1131 */     int numProblems = 0;
/* 1132 */     int step = this.evalSequence.keySet().size() / 10;
/*      */ 
/*      */ 
/*      */     
/* 1136 */     for (Instruction e : this.evalSequence.keySet()) {
/* 1137 */       counter++;
/*      */       
/* 1139 */       if (counter % step == 0 && 
/* 1140 */         !Config.arithOptimizerDisableProgress) {
/* 1141 */         System.out.println("[Arithmetic Optimizer] Progress = " + 
/* 1142 */             Math.ceil((counter * 1.0F / this.evalSequence.size() * 
/* 1143 */               100.0F)) + "%");
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1162 */       if (e instanceof WireLabelInstruction) {
/* 1163 */         WireLabelInstruction labelInstruction = (WireLabelInstruction)e;
/* 1164 */         this.tmpEvalSequence.put(labelInstruction, labelInstruction);
/* 1165 */         if (labelInstruction.getType() == WireLabelInstruction.LabelType.input) {
/* 1166 */           this.numOriginalIns++;
/*      */         }
/* 1168 */         if (labelInstruction.getType() == WireLabelInstruction.LabelType.nizkinput) {
/* 1169 */           this.numOriginalWitnesses++;
/*      */         }
/* 1171 */         if (labelInstruction.getType() == WireLabelInstruction.LabelType.output) {
/* 1172 */           this.numOriginalOuts++;
/*      */           
/* 1174 */           Integer c = this.useCounters.get(labelInstruction.getWire());
/* 1175 */           if (c == null) c = Integer.valueOf(0); 
/* 1176 */           this.useCounters.put(labelInstruction.getWire(), Integer.valueOf(c.intValue() + 1));
/*      */           
/* 1178 */           this.toOverride.put(labelInstruction.getWire(), Boolean.valueOf(false));
/*      */         } 
/*      */         
/* 1181 */         if (labelInstruction.getType() == WireLabelInstruction.LabelType.input || 
/* 1182 */           labelInstruction.getType() == WireLabelInstruction.LabelType.nizkinput) {
/*      */           
/* 1184 */           if (labelInstruction.getWire() == this.generator.__getOneWire()) {
/*      */             
/* 1186 */             MultivariatePolynomial multivariatePolynomial = new MultivariatePolynomial(
/* 1187 */                 new Term(BigInteger.ONE));
/* 1188 */             this.mvpMap.put(labelInstruction.getWire(), multivariatePolynomial);
/*      */             
/*      */             continue;
/*      */           } 
/* 1192 */           OptVariable variable = new OptVariable("w", 
/* 1193 */               labelInstruction.getWire().getWireId());
/* 1194 */           this.allOptVariables.add(variable);
/* 1195 */           this.optVarWireMap.put(variable, labelInstruction.getWire());
/* 1196 */           MultivariatePolynomial mvp = new MultivariatePolynomial(
/* 1197 */               variable);
/* 1198 */           this.mvpMap.put(labelInstruction.getWire(), mvp);
/*      */         } 
/*      */         
/*      */         continue;
/*      */       } 
/* 1203 */       if (e instanceof BasicOp) {
/* 1204 */         BasicOp op = (BasicOp)e;
/* 1205 */         Wire[] inputs = op.getInputs();
/* 1206 */         Wire[] outputs = op.getOutputs(); byte b; int i; Wire[] arrayOfWire1;
/* 1207 */         for (i = (arrayOfWire1 = inputs).length, b = 0; b < i; ) { Wire w = arrayOfWire1[b];
/*      */           
/* 1209 */           Integer c = this.useCounters.get(w);
/* 1210 */           if (c == null)
/* 1211 */             c = Integer.valueOf(0); 
/* 1212 */           this.useCounters.put(w, Integer.valueOf(c.intValue() + 1));
/*      */ 
/*      */           
/* 1215 */           this.toOverride.put(w, Boolean.valueOf((c.intValue() + 1 == 1)));
/*      */           b++; }
/*      */         
/* 1218 */         if (op instanceof backend.operations.primitive.SplitBasicOp) {
/* 1219 */           this.numOriginalSplits++;
/*      */         }
/* 1221 */         if (op instanceof backend.operations.primitive.PackBasicOp) {
/* 1222 */           this.numOriginalPacks++;
/*      */         }
/* 1224 */         if (op instanceof backend.operations.primitive.NonZeroCheckBasicOp) {
/* 1225 */           this.numOriginalNonzeroChecks++;
/*      */         }
/*      */         
/* 1228 */         if (op instanceof backend.operations.primitive.SplitBasicOp || op instanceof backend.operations.primitive.PackBasicOp || 
/* 1229 */           op instanceof backend.operations.primitive.NonZeroCheckBasicOp) {
/* 1230 */           for (i = (arrayOfWire1 = outputs).length, b = 0; b < i; ) { Wire w = arrayOfWire1[b];
/* 1231 */             OptVariable variable = new OptVariable("w", 
/* 1232 */                 w.getWireId(), !(op instanceof backend.operations.primitive.PackBasicOp));
/* 1233 */             this.allOptVariables.add(variable);
/* 1234 */             this.optVarWireMap.put(variable, w);
/*      */             
/* 1236 */             MultivariatePolynomial mvp = new MultivariatePolynomial(
/* 1237 */                 variable);
/* 1238 */             this.mvpMap.put(w, mvp);
/*      */ 
/*      */ 
/*      */             
/*      */             b++; }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1247 */           for (i = (arrayOfWire1 = inputs).length, b = 0; b < i; ) { Wire wire = arrayOfWire1[b]; b++; }
/*      */ 
/*      */ 
/*      */           
/* 1251 */           this.tmpEvalSequence.put(op, op);
/*      */         } 
/* 1253 */         if (op instanceof backend.operations.primitive.AssertBasicOp) {
/* 1254 */           this.numOriginalAssertions++;
/*      */           
/* 1256 */           for (i = (arrayOfWire1 = inputs).length, b = 0; b < i; ) { Wire w = arrayOfWire1[b];
/*      */             
/* 1258 */             this.toOverride.put(w, Boolean.valueOf(false));
/*      */             b++; }
/*      */           
/* 1261 */           for (i = (arrayOfWire1 = outputs).length, b = 0; b < i; ) { Wire w = arrayOfWire1[b];
/*      */             
/* 1263 */             Integer c = this.useCounters.get(w);
/* 1264 */             if (c == null)
/* 1265 */               c = Integer.valueOf(0); 
/* 1266 */             this.useCounters.put(w, Integer.valueOf(c.intValue() + 1));
/* 1267 */             this.toOverride.put(w, Boolean.valueOf(false)); b++; }
/*      */           
/* 1269 */           this.tmpEvalSequence.put(op, op);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void secondPass() {
/* 1286 */     System.out
/* 1287 */       .println("[Arithmetic Optimizer] Starting Second Optimization Stage");
/* 1288 */     if (Config.arithOptimizerIncrementalMode) {
/* 1289 */       System.out
/* 1290 */         .println("[Arithmetic Optimizer] Incremental mode is activtated. This is a new option under testing.");
/*      */     }
/*      */ 
/*      */     
/* 1294 */     int counter = 0;
/* 1295 */     int step = this.evalSequence.keySet().size() / 10;
/* 1296 */     for (Instruction e : this.evalSequence.keySet()) {
/*      */ 
/*      */ 
/*      */       
/* 1300 */       counter++;
/* 1301 */       if (counter % step == 0) {
/* 1302 */         if (!Config.arithOptimizerDisableProgress && 
/* 1303 */           !Config.arithOptimizerIncrementalMode)
/* 1304 */           System.out.println("[Arithmetic Optimizer] Progress = " + 
/* 1305 */               Math.ceil((counter * 1.0F / this.evalSequence.size() * 
/* 1306 */                 100.0F)) + "%"); 
/* 1307 */         if (Config.arithOptimizerIncrementalMode) {
/* 1308 */           checkForSolvableProblems(counter * 1.0F / 
/* 1309 */               this.evalSequence.size() * 100.0F);
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1317 */       if (e instanceof WireLabelInstruction) {
/* 1318 */         WireLabelInstruction label = (WireLabelInstruction)e;
/*      */         
/* 1320 */         if (label.getType() != WireLabelInstruction.LabelType.input && 
/* 1321 */           label.getType() != WireLabelInstruction.LabelType.nizkinput)
/*      */         {
/* 1323 */           this.problemMap.put(label.getWire(), new Problem(
/* 1324 */                 label.getWire(), this.mvpMap.get(label.getWire()))); }  continue;
/*      */       } 
/* 1326 */       if (e instanceof BasicOp) {
/*      */ 
/*      */         
/* 1329 */         BasicOp op = (BasicOp)e;
/* 1330 */         Wire[] inputs = op.getInputs();
/* 1331 */         Wire[] outputs = op.getOutputs(); byte b; int i; Wire[] arrayOfWire1;
/* 1332 */         for (i = (arrayOfWire1 = inputs).length, b = 0; b < i; ) { Wire w = arrayOfWire1[b];
/* 1333 */           Integer c = this.useCounters.get(w);
/* 1334 */           this.useCounters.put(w, Integer.valueOf(c.intValue() - 1)); b++; }
/*      */         
/* 1336 */         if (op instanceof backend.operations.primitive.AssertBasicOp) {
/* 1337 */           for (i = (arrayOfWire1 = outputs).length, b = 0; b < i; ) { Wire w = arrayOfWire1[b];
/* 1338 */             Integer c = this.useCounters.get(w);
/* 1339 */             this.useCounters.put(w, Integer.valueOf(c.intValue() - 1));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*      */             b++; }
/*      */         
/*      */         }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1353 */         if (op instanceof backend.operations.primitive.SplitBasicOp || op instanceof backend.operations.primitive.PackBasicOp || 
/* 1354 */           op instanceof backend.operations.primitive.NonZeroCheckBasicOp) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1361 */           for (i = (arrayOfWire1 = inputs).length, b = 0; b < i; ) { Wire w = arrayOfWire1[b];
/*      */             
/* 1363 */             if (this.problemMap.get(w) == null)
/* 1364 */               this.problemMap.put(w, new Problem(w, this.mvpMap.get(w)));  b++; }
/*      */            continue;
/*      */         } 
/* 1367 */         if (op instanceof backend.operations.primitive.AssertBasicOp) {
/*      */ 
/*      */           
/* 1370 */           for (i = (arrayOfWire1 = inputs).length, b = 0; b < i; ) { Wire w = arrayOfWire1[b];
/*      */ 
/*      */             
/* 1373 */             if (this.problemMap.get(w) == null)
/* 1374 */               this.problemMap.put(w, new Problem(w, this.mvpMap.get(w))); 
/*      */             b++; }
/*      */           
/* 1377 */           for (i = (arrayOfWire1 = outputs).length, b = 0; b < i; ) { Wire w = arrayOfWire1[b];
/*      */ 
/*      */             
/* 1380 */             if (this.problemMap.get(w) == null)
/* 1381 */               this.problemMap.put(w, new Problem(w, this.mvpMap.get(w))); 
/*      */             b++; }
/*      */           
/*      */           continue;
/*      */         } 
/* 1386 */         if (op instanceof backend.operations.primitive.AddBasicOp) {
/*      */           MultivariatePolynomial mvp;
/* 1388 */           int idx = checkInputToOverride(inputs);
/* 1389 */           if (idx != -1) {
/*      */             
/* 1391 */             mvp = this.mvpMap.get(inputs[idx]);
/* 1392 */             this.mvpMap.put(inputs[idx], null);
/* 1393 */             if (mvp == null) {
/* 1394 */               System.out.println(op);
/* 1395 */               System.out.println(inputs[idx]);
/* 1396 */               throw new RuntimeException(
/* 1397 */                   "Unxpected Case : Please report this case.");
/*      */             } 
/* 1399 */             for (int j = 0; j < inputs.length; j++) {
/* 1400 */               if (idx != j) {
/* 1401 */                 mvp = mvp.addInPlace(this.mvpMap.get(inputs[j]));
/*      */               }
/*      */             } 
/*      */           } else {
/*      */             
/* 1406 */             mvp = new MultivariatePolynomial(); byte b1; int j; Wire[] arrayOfWire;
/* 1407 */             for (j = (arrayOfWire = inputs).length, b1 = 0; b1 < j; ) { Wire w = arrayOfWire[b1];
/* 1408 */               mvp = mvp.addInPlace(this.mvpMap.get(w));
/*      */               
/*      */               b1++; }
/*      */           
/*      */           } 
/*      */           
/* 1414 */           if (mvp.isCostly()) {
/*      */             
/* 1416 */             if (this.problemMap.get(outputs[0]) == null) {
/* 1417 */               this.problemMap.put(outputs[0], new Problem(outputs[0], 
/* 1418 */                     mvp));
/*      */             }
/*      */             
/* 1421 */             OptVariable variable = new OptVariable("w", 
/* 1422 */                 outputs[0].getWireId());
/* 1423 */             this.allOptVariables.add(variable);
/* 1424 */             this.optVarWireMap.put(variable, outputs[0]);
/* 1425 */             this.mvpMap.put(outputs[0], new MultivariatePolynomial(
/* 1426 */                   variable));
/*      */             continue;
/*      */           } 
/* 1429 */           this.mvpMap.put(outputs[0], mvp); continue;
/*      */         } 
/* 1431 */         if (op instanceof MulBasicOp) {
/*      */           MultivariatePolynomial mvp;
/*      */ 
/*      */           
/* 1435 */           int idx = checkInputToOverride(inputs);
/*      */           
/* 1437 */           if (idx != -1) {
/* 1438 */             mvp = this.mvpMap.get(inputs[idx]);
/* 1439 */             this.mvpMap.put(inputs[idx], null);
/* 1440 */             for (int j = 0; j < inputs.length; j++) {
/* 1441 */               if (idx != j) {
/* 1442 */                 mvp = mvp
/* 1443 */                   .multiplyInPlace(this.mvpMap.get(inputs[j]));
/*      */               }
/*      */             } 
/*      */           } else {
/* 1447 */             mvp = ((MultivariatePolynomial)this.mvpMap.get(inputs[0])).multiply(
/* 1448 */                 this.mvpMap.get(inputs[1]));
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/* 1453 */           if (mvp.isCostly()) {
/*      */             
/* 1455 */             if (this.problemMap.get(outputs[0]) == null)
/* 1456 */               this.problemMap.put(outputs[0], new Problem(outputs[0], 
/* 1457 */                     mvp)); 
/* 1458 */             OptVariable variable = new OptVariable("w", 
/* 1459 */                 outputs[0].getWireId());
/* 1460 */             this.allOptVariables.add(variable);
/* 1461 */             this.optVarWireMap.put(variable, outputs[0]);
/* 1462 */             this.mvpMap.put(outputs[0], new MultivariatePolynomial(
/* 1463 */                   variable));
/*      */             
/*      */             continue;
/*      */           } 
/* 1467 */           this.mvpMap.put(outputs[0], mvp);
/*      */           continue;
/*      */         } 
/* 1470 */         if (op instanceof backend.operations.primitive.XorBasicOp) {
/*      */           MultivariatePolynomial mvp;
/* 1472 */           int idx = checkInputToOverride(inputs);
/* 1473 */           if (idx != -1) {
/*      */             
/* 1475 */             mvp = this.mvpMap.get(inputs[idx]);
/* 1476 */             this.mvpMap.put(inputs[idx], null);
/*      */             
/* 1478 */             for (int j = 0; j < inputs.length; j++) {
/* 1479 */               if (idx != j) {
/* 1480 */                 MultivariatePolynomial tmp = mvp
/* 1481 */                   .multiply(this.mvpMap.get(inputs[j]));
/* 1482 */                 tmp = tmp
/* 1483 */                   .multiplyConstantInPlace(
/* 1484 */                     ResourceBundle.getInstance().getBigInteger(
/* 1485 */                       new BigInteger("-2")));
/*      */                 
/* 1487 */                 mvp = mvp.addInPlace(this.mvpMap.get(inputs[j]))
/* 1488 */                   .addInPlace(tmp);
/*      */               } 
/*      */             } 
/*      */           } else {
/*      */             
/* 1493 */             mvp = ((MultivariatePolynomial)this.mvpMap.get(inputs[1])).multiply(
/* 1494 */                 this.mvpMap.get(inputs[0]));
/* 1495 */             mvp = mvp.multiplyConstantInPlace(
/* 1496 */                 ResourceBundle.getInstance().getBigInteger(
/* 1497 */                   new BigInteger("-2")));
/* 1498 */             mvp = mvp.addInPlace(this.mvpMap.get(inputs[1])).addInPlace(
/* 1499 */                 this.mvpMap.get(inputs[0]));
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1505 */           if (mvp.isCostly()) {
/* 1506 */             this.problemMap.get(outputs[0]);
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1517 */           if (mvp.isCostly()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1523 */             if (this.problemMap.get(outputs[0]) == null)
/* 1524 */               this.problemMap.put(outputs[0], new Problem(outputs[0], 
/* 1525 */                     mvp)); 
/* 1526 */             OptVariable variable = new OptVariable("w", 
/* 1527 */                 outputs[0].getWireId(), true);
/* 1528 */             this.allOptVariables.add(variable);
/* 1529 */             this.optVarWireMap.put(variable, outputs[0]);
/* 1530 */             this.mvpMap.put(outputs[0], new MultivariatePolynomial(
/* 1531 */                   variable));
/*      */ 
/*      */             
/*      */             continue;
/*      */           } 
/*      */ 
/*      */           
/* 1538 */           this.mvpMap.put(outputs[0], mvp); continue;
/*      */         } 
/* 1540 */         if (op instanceof backend.operations.primitive.ORBasicOp) {
/*      */           MultivariatePolynomial mvp;
/* 1542 */           int idx = checkInputToOverride(inputs);
/* 1543 */           if (idx != -1) {
/* 1544 */             mvp = this.mvpMap.get(inputs[idx]);
/* 1545 */             this.mvpMap.put(inputs[idx], null);
/*      */             
/* 1547 */             for (int j = 0; j < inputs.length; j++) {
/* 1548 */               if (idx != j) {
/* 1549 */                 MultivariatePolynomial tmp = mvp
/* 1550 */                   .multiply(this.mvpMap.get(inputs[j]));
/* 1551 */                 tmp = tmp
/* 1552 */                   .multiplyConstantInPlace(
/* 1553 */                     ResourceBundle.getInstance().getBigInteger(
/* 1554 */                       new BigInteger("-1")));
/* 1555 */                 mvp = mvp.addInPlace(this.mvpMap.get(inputs[j]))
/* 1556 */                   .addInPlace(tmp);
/*      */               } 
/*      */             } 
/*      */           } else {
/* 1560 */             mvp = ((MultivariatePolynomial)this.mvpMap.get(inputs[1])).multiply(
/* 1561 */                 this.mvpMap.get(inputs[0]));
/* 1562 */             mvp = mvp.multiplyConstantInPlace(
/* 1563 */                 ResourceBundle.getInstance().getBigInteger(
/* 1564 */                   new BigInteger("-1")));
/* 1565 */             mvp = mvp.addInPlace(this.mvpMap.get(inputs[1])).addInPlace(
/* 1566 */                 this.mvpMap.get(inputs[0]));
/*      */           } 
/*      */           
/* 1569 */           if (mvp.isCostly()) {
/*      */             
/* 1571 */             if (this.problemMap.get(outputs[0]) == null)
/* 1572 */               this.problemMap.put(outputs[0], new Problem(outputs[0], 
/* 1573 */                     mvp)); 
/* 1574 */             OptVariable variable = new OptVariable("w", 
/* 1575 */                 outputs[0].getWireId(), true);
/* 1576 */             this.allOptVariables.add(variable);
/* 1577 */             this.optVarWireMap.put(variable, outputs[0]);
/* 1578 */             this.mvpMap.put(outputs[0], new MultivariatePolynomial(
/* 1579 */                   variable)); continue;
/*      */           } 
/* 1581 */           this.mvpMap.put(outputs[0], mvp); continue;
/*      */         } 
/* 1583 */         if (op instanceof ConstMulBasicOp) {
/*      */           MultivariatePolynomial mvp;
/*      */ 
/*      */           
/* 1587 */           int idx = checkInputToOverride(inputs);
/* 1588 */           if (idx != -1) {
/* 1589 */             mvp = this.mvpMap.get(inputs[idx]);
/* 1590 */             this.mvpMap.put(inputs[idx], null);
/*      */             
/* 1592 */             mvp.multiplyConstantInPlace(((ConstMulBasicOp)op)
/* 1593 */                 .getConstInteger());
/*      */           } else {
/*      */             
/* 1596 */             mvp = this.mvpMap.get(inputs[0]);
/* 1597 */             mvp = mvp.multiplyConstant(((ConstMulBasicOp)op)
/* 1598 */                 .getConstInteger());
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1604 */           this.mvpMap.put(outputs[0], mvp);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int checkInputToOverride(Wire[] inputs) {
/* 1614 */     int i = 0; byte b; int j; Wire[] arrayOfWire;
/* 1615 */     for (j = (arrayOfWire = inputs).length, b = 0; b < j; ) { Wire w = arrayOfWire[b];
/*      */ 
/*      */ 
/*      */       
/* 1619 */       if (((Boolean)this.toOverride.get(w)).booleanValue() && ((Integer)this.useCounters.get(w)).intValue() == 0)
/*      */       {
/* 1621 */         return i;
/*      */       }
/* 1623 */       i++; b++; }
/*      */     
/* 1625 */     return -1;
/*      */   }
/*      */ 
/*      */   
/*      */   private void checkForSolvableProblems(float f) {
/* 1630 */     ArrayList<Problem> combinedProblemsToSolve = new ArrayList<>();
/*      */ 
/*      */     
/* 1633 */     System.out
/* 1634 */       .print("[Arithmetic Optimizer - Incremental Mode (Progress: " + 
/* 1635 */         Math.ceil(f) + 
/* 1636 */         "%)] Checking for problems that can be solved at this stage:");
/* 1637 */     HashMap<OptVarPair, Boolean> state = new HashMap<>();
/* 1638 */     for (OptVarPair p : this.optVarProblemMap.keySet()) {
/* 1639 */       state.put(p, Boolean.valueOf(false));
/*      */     }
/*      */     
/* 1642 */     int numProcessed = 0;
/*      */     
/* 1644 */     HashSet<Problem> problemCollection = new LinkedHashSet<>();
/* 1645 */     HashSet<Problem> visitedProblems = new HashSet<>();
/* 1646 */     Queue<OptVarPair> varQueue = new LinkedList<>();
/* 1647 */     HashSet<OptVarPair> pairs = new HashSet<>();
/*      */ 
/*      */     
/* 1650 */     Set<OptVarPair> keySet = this.optVarProblemMap.keySet();
/* 1651 */     for (OptVarPair pair : keySet) {
/* 1652 */       if (!((Boolean)state.get(pair)).booleanValue()) {
/* 1653 */         state.put(pair, Boolean.valueOf(true));
/* 1654 */         numProcessed++;
/* 1655 */         problemCollection.clear();
/* 1656 */         varQueue.clear();
/* 1657 */         ArrayList<Problem> list = this.optVarProblemMap.get(pair);
/* 1658 */         problemCollection.addAll(list);
/*      */         
/* 1660 */         for (Problem p : list) {
/* 1661 */           if (!visitedProblems.contains(p)) {
/* 1662 */             visitedProblems.add(p);
/* 1663 */             varQueue.addAll(p.optVarPairs);
/*      */           } 
/*      */         } 
/*      */         
/* 1667 */         while (!varQueue.isEmpty()) {
/* 1668 */           OptVarPair pair2 = varQueue.poll();
/*      */           
/* 1670 */           if (!((Boolean)state.get(pair2)).booleanValue()) {
/* 1671 */             list = this.optVarProblemMap.get(pair2);
/* 1672 */             problemCollection.addAll(list);
/* 1673 */             state.put(pair2, Boolean.valueOf(true));
/* 1674 */             for (Problem p : list) {
/*      */               
/* 1676 */               if (visitedProblems.contains(p)) {
/*      */                 continue;
/*      */               }
/* 1679 */               visitedProblems.add(p);
/*      */ 
/*      */               
/* 1682 */               for (OptVarPair pair3 : p.optVarPairs) {
/* 1683 */                 if (!((Boolean)state.get(pair3)).booleanValue()) {
/* 1684 */                   varQueue.add(pair3);
/*      */                 }
/*      */               } 
/*      */             } 
/* 1688 */             numProcessed++;
/*      */           } 
/*      */         } 
/*      */         
/* 1692 */         boolean canBeSolvedNow = true;
/* 1693 */         for (Problem p : problemCollection) {
/* 1694 */           if (!p.checkCompletedUsageIntermediateWires()) {
/* 1695 */             canBeSolvedNow = false;
/*      */             break;
/*      */           } 
/* 1698 */           for (OptVariable v : p.variables) {
/* 1699 */             Wire w = this.optVarWireMap.get(v);
/* 1700 */             if (this.useCounters.get(w) == null || (
/* 1701 */               (Integer)this.useCounters.get(w)).intValue() != 0) {
/* 1702 */               canBeSolvedNow = false;
/*      */               break;
/*      */             } 
/*      */           } 
/*      */         } 
/* 1707 */         if (canBeSolvedNow) {
/* 1708 */           for (Problem p : problemCollection) {
/* 1709 */             pairs.addAll(p.optVarPairs);
/*      */           }
/* 1711 */           Problem superProblem = new Problem(problemCollection);
/* 1712 */           combinedProblemsToSolve.add(superProblem);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1720 */     for (OptVarPair optVarPair : pairs) {
/* 1721 */       this.optVarProblemMap.remove(optVarPair);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1730 */     this.numProblemsOptimized = 0;
/* 1731 */     System.out.println("Now solving " + combinedProblemsToSolve.size() + 
/* 1732 */         " problems.");
/*      */ 
/*      */     
/* 1735 */     for (Problem p : combinedProblemsToSolve) {
/* 1736 */       p.constructOriginalSubcircuit();
/* 1737 */       p.optVarPairs = null;
/* 1738 */       for (Wire w : p.getIntermediateWires()) {
/* 1739 */         this.mvpMap.remove(w);
/*      */       }
/*      */       
/* 1742 */       for (Wire w : p.mvpList.keySet()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1748 */         if (this.useCounters.get(w) != null && ((Integer)this.useCounters.get(w)).intValue() == 0) {
/* 1749 */           this.mvpMap.remove(w);
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1758 */     (new MultivariateMinimizer(combinedProblemsToSolve)).run();
/*      */     
/* 1760 */     for (Problem p : combinedProblemsToSolve) {
/* 1761 */       if (p.solutions == null) {
/* 1762 */         if (p.mvpList != null) {
/* 1763 */           for (Wire w : p.mvpList.keySet()) {
/* 1764 */             this.problemMap.remove(w);
/* 1765 */             this.originalEvalSequenceMap.put(w, 
/* 1766 */                 p.originalEvalSequenceArray);
/*      */           } 
/*      */         } else {
/* 1769 */           for (Wire w : p.keyWireList) {
/* 1770 */             this.problemMap.remove(w);
/* 1771 */             this.originalEvalSequenceMap.put(w, 
/* 1772 */                 p.originalEvalSequenceArray);
/*      */           } 
/*      */         } 
/*      */       }
/*      */       
/* 1777 */       p.mvpList = null;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1787 */     System.gc();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void thirdPass() {
/* 1794 */     groupProblems();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1800 */     if (!Config.arithOptimizerIncrementalMode) {
/* 1801 */       System.out
/* 1802 */         .println("[Arithmetic Optimizer] Minimizing Multivariate Expressions");
/*      */     } else {
/* 1804 */       System.out
/* 1805 */         .println("[Arithmetic Optimizer] Minimizing Remaining Multivariate Expressions");
/*      */     } 
/* 1807 */     int c = 0;
/* 1808 */     int max = 0;
/*      */ 
/*      */     
/* 1811 */     this.mvpMap.clear();
/* 1812 */     this.optVarProblemMap.clear();
/* 1813 */     this.useCounters.clear();
/*      */ 
/*      */     
/* 1816 */     for (Problem p : this.combinedProblems) {
/* 1817 */       p.constructOriginalSubcircuit();
/* 1818 */       p.optVarPairs = null;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1825 */     (new MultivariateMinimizer(this.combinedProblems)).run();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1838 */     this.newEvalSequence = new LinkedHashMap<>();
/* 1839 */     this.generator.__setEvaluationQueue(this.newEvalSequence);
/*      */     
/* 1841 */     int tempTotalCost = 0;
/*      */ 
/*      */     
/* 1844 */     for (Instruction e : this.evalSequence.keySet()) {
/*      */       
/* 1846 */       if (e instanceof WireLabelInstruction) {
/* 1847 */         WireLabelInstruction labelInstruction = (WireLabelInstruction)e;
/* 1848 */         if (labelInstruction.getType() == WireLabelInstruction.LabelType.debug || 
/* 1849 */           labelInstruction.getType() == WireLabelInstruction.LabelType.output) {
/*      */           
/* 1851 */           Problem p = this.problemMap.get(labelInstruction.getWire());
/*      */           
/* 1853 */           if (p != null) {
/* 1854 */             p.getSuperProblem().integrateEvalSequence();
/*      */           } else {
/* 1856 */             Instruction[] seq = this.originalEvalSequenceMap
/* 1857 */               .get(labelInstruction.getWire());
/* 1858 */             for (int j = seq.length - 1; j >= 0; j--) {
/* 1859 */               this.newEvalSequence.put(seq[j], seq[j]);
/*      */             }
/*      */           } 
/* 1862 */           this.newEvalSequence.put(e, e);
/*      */           continue;
/*      */         } 
/* 1865 */         this.newEvalSequence.put(e, e); continue;
/*      */       } 
/* 1867 */       if (e instanceof BasicOp) {
/* 1868 */         boolean addToEval = false;
/* 1869 */         BasicOp op = (BasicOp)e;
/* 1870 */         Wire[] inputs = op.getInputs();
/* 1871 */         Wire[] outputs = op.getOutputs();
/*      */         
/* 1873 */         if (e instanceof backend.operations.primitive.SplitBasicOp || e instanceof backend.operations.primitive.PackBasicOp || 
/* 1874 */           e instanceof backend.operations.primitive.NonZeroCheckBasicOp || 
/* 1875 */           e instanceof backend.operations.primitive.AssertBasicOp) {
/* 1876 */           byte b1; int k; Wire[] arrayOfWire; for (k = (arrayOfWire = inputs).length, b1 = 0; b1 < k; ) { Wire w = arrayOfWire[b1];
/* 1877 */             Problem p = this.problemMap.get(w);
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1882 */             if (p != null) {
/*      */               
/* 1884 */               p.getSuperProblem().integrateEvalSequence();
/* 1885 */               addToEval = true;
/*      */             }
/*      */             else {
/*      */               
/* 1889 */               Instruction[] seq = this.originalEvalSequenceMap.get(w);
/* 1890 */               if (seq != null) {
/* 1891 */                 addToEval = true;
/* 1892 */                 for (int m = seq.length - 1; m >= 0; m--) {
/* 1893 */                   this.newEvalSequence.put(seq[m], seq[m]);
/*      */                 }
/*      */               } 
/*      */             } 
/*      */ 
/*      */             
/*      */             b1++; }
/*      */ 
/*      */           
/* 1902 */           if (e instanceof backend.operations.primitive.AssertBasicOp)
/* 1903 */             for (k = (arrayOfWire = outputs).length, b1 = 0; b1 < k; ) { Wire w = arrayOfWire[b1];
/* 1904 */               Problem p = this.problemMap.get(w);
/* 1905 */               if (p != null) {
/* 1906 */                 addToEval = true;
/* 1907 */                 p.getSuperProblem().integrateEvalSequence();
/*      */               } else {
/*      */                 
/* 1910 */                 Instruction[] seq = this.originalEvalSequenceMap
/* 1911 */                   .get(w);
/* 1912 */                 if (seq != null) {
/* 1913 */                   addToEval = true;
/* 1914 */                   for (int m = seq.length - 1; m >= 0; m--) {
/* 1915 */                     this.newEvalSequence.put(seq[m], seq[m]);
/*      */                   }
/*      */                 } 
/*      */               } 
/*      */               b1++; }
/*      */              
/* 1921 */           if (addToEval)
/*      */           {
/* 1923 */             this.newEvalSequence.put(e, e); }  continue;
/*      */         } 
/*      */         byte b;
/*      */         int j;
/*      */         Wire[] arrayOfWire1;
/* 1928 */         for (j = (arrayOfWire1 = outputs).length, b = 0; b < j; ) { Wire w = arrayOfWire1[b];
/* 1929 */           Problem p = this.problemMap.get(w);
/*      */ 
/*      */           
/* 1932 */           if (p != null) {
/*      */             
/* 1934 */             p.getSuperProblem().integrateEvalSequence();
/*      */           } else {
/*      */             
/* 1937 */             Instruction[] seq = this.originalEvalSequenceMap.get(w);
/* 1938 */             if (seq != null) {
/* 1939 */               for (int k = seq.length - 1; k >= 0; k--)
/*      */               {
/* 1941 */                 this.newEvalSequence.put(seq[k], seq[k]);
/*      */               }
/*      */             }
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           b++; }
/*      */       
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1959 */     this.originalEvalSequenceMap = null;
/* 1960 */     this.problemMap = null;
/* 1961 */     System.gc();
/*      */     
/* 1963 */     for (Instruction ii : this.newEvalSequence.keySet()) {
/*      */       
/* 1965 */       if (ii instanceof BasicOp) {
/* 1966 */         tempTotalCost += ((BasicOp)ii).getNumMulGates();
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/* 1971 */     boolean[] wireDefined = new boolean[this.generator.__getCurrentWireId()];
/* 1972 */     boolean[] wireVisited = new boolean[this.generator.__getCurrentWireId()];
/* 1973 */     HashMap<Wire, ArrayList<Instruction>> wireInstructionDependencies = new HashMap<>();
/* 1974 */     HashMap<Instruction, ArrayList<Wire>> instructionWireDependencies = new HashMap<>();
/*      */ 
/*      */     
/* 1977 */     LinkedHashMap<Instruction, Instruction> sortedEvalSequence = new LinkedHashMap<>();
/* 1978 */     Queue<Instruction> iQueue = new LinkedList<>();
/*      */     
/* 1980 */     ArrayList<Wire> newOutputs = new ArrayList<>();
/* 1981 */     HashMap<Wire, Instruction> newOutputInstructions = new HashMap<>();
/*      */ 
/*      */     
/* 1984 */     Instruction debugInstruction = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1993 */     LinkedHashSet<Integer> notDefinedWires = new LinkedHashSet<>();
/* 1994 */     for (Instruction instruction : this.newEvalSequence.keySet()) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2001 */       while (!iQueue.isEmpty()) {
/*      */         
/* 2003 */         Instruction i2 = iQueue.poll();
/* 2004 */         sortedEvalSequence.put(i2, i2);
/* 2005 */         if (i2 instanceof BasicOp) {
/* 2006 */           BasicOp basicOp = (BasicOp)i2;
/* 2007 */           if (!(basicOp instanceof backend.operations.primitive.AssertBasicOp)) {
/* 2008 */             byte b; int j; Wire[] arrayOfWire; for (j = (arrayOfWire = basicOp.getOutputs()).length, b = 0; b < j; ) { Wire w = arrayOfWire[b];
/* 2009 */               wireDefined[w.getWireId()] = true;
/* 2010 */               wireVisited[w.getWireId()] = true;
/* 2011 */               notDefinedWires.remove(Integer.valueOf(w.getWireId()));
/* 2012 */               ArrayList<Instruction> ilist = wireInstructionDependencies
/* 2013 */                 .get(w);
/* 2014 */               if (ilist != null) {
/* 2015 */                 for (Instruction i3 : ilist) {
/* 2016 */                   ArrayList<Wire> list = instructionWireDependencies
/* 2017 */                     .get(i3);
/* 2018 */                   if (list != null) {
/* 2019 */                     list.remove(w);
/* 2020 */                     if (list.isEmpty()) {
/* 2021 */                       iQueue.add(i3);
/* 2022 */                       instructionWireDependencies
/* 2023 */                         .remove(i3);
/*      */                     } 
/*      */                   } 
/*      */                 } 
/*      */               }
/*      */               
/* 2029 */               wireInstructionDependencies.remove(w); b++; }
/*      */           
/*      */           }  continue;
/*      */         } 
/* 2033 */         throw new RuntimeException("Unexpected case (sanity check failed). Please report this case.");
/*      */       } 
/*      */ 
/*      */       
/* 2037 */       if (instruction instanceof WireLabelInstruction) {
/*      */ 
/*      */         
/* 2040 */         WireLabelInstruction labelInstruction = (WireLabelInstruction)instruction;
/* 2041 */         Wire w = labelInstruction.getWire();
/* 2042 */         if (labelInstruction.getType() == WireLabelInstruction.LabelType.input || 
/* 2043 */           labelInstruction.getType() == WireLabelInstruction.LabelType.nizkinput) {
/*      */ 
/*      */ 
/*      */           
/* 2047 */           sortedEvalSequence.put(instruction, instruction);
/* 2048 */           wireDefined[w.getWireId()] = true;
/* 2049 */           wireVisited[w.getWireId()] = true;
/* 2050 */           notDefinedWires.remove(Integer.valueOf(w.getWireId()));
/*      */           
/* 2052 */           ArrayList<Instruction> ilist = wireInstructionDependencies
/* 2053 */             .get(w);
/* 2054 */           if (ilist != null) {
/* 2055 */             for (Instruction instruction1 : ilist) {
/* 2056 */               ArrayList<Wire> list = instructionWireDependencies
/* 2057 */                 .get(instruction1);
/* 2058 */               list.remove(w);
/* 2059 */               if (list.isEmpty()) {
/* 2060 */                 iQueue.add(instruction1);
/* 2061 */                 instructionWireDependencies.remove(instruction1);
/*      */               } 
/*      */             } 
/* 2064 */             wireInstructionDependencies.remove(w);
/*      */           }  continue;
/*      */         } 
/* 2067 */         if (!wireDefined[w.getWireId()]) {
/* 2068 */           wireVisited[w.getWireId()] = true;
/*      */           
/* 2070 */           notDefinedWires.add(Integer.valueOf(w.getWireId()));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 2080 */           ArrayList<Instruction> ilist = wireInstructionDependencies
/* 2081 */             .get(w);
/* 2082 */           if (ilist == null) {
/* 2083 */             ilist = new ArrayList<>();
/* 2084 */             wireInstructionDependencies.put(w, ilist);
/*      */           } 
/* 2086 */           ilist.add(instruction);
/*      */           
/* 2088 */           ArrayList<Wire> wlist = instructionWireDependencies
/* 2089 */             .get(instruction);
/* 2090 */           if (wlist == null) {
/* 2091 */             wlist = new ArrayList<>();
/* 2092 */             instructionWireDependencies.put(instruction, wlist);
/*      */           } 
/* 2094 */           wlist.add(w);
/*      */           
/* 2096 */           instructionWireDependencies.put(instruction, wlist); continue;
/*      */         } 
/* 2098 */         wireVisited[w.getWireId()] = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2104 */         sortedEvalSequence.put(instruction, instruction);
/*      */ 
/*      */         
/*      */         continue;
/*      */       } 
/*      */       
/* 2110 */       if (instruction instanceof BasicOp) {
/*      */ 
/*      */         
/* 2113 */         BasicOp basicOp = (BasicOp)instruction;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2120 */         boolean allDefined = true;
/* 2121 */         Wire[] inList = basicOp.getInputs();
/* 2122 */         if (instruction instanceof backend.operations.primitive.AssertBasicOp)
/* 2123 */           inList = Util.concat(basicOp.getOutputs()[0], inList);  byte b; int j;
/*      */         Wire[] arrayOfWire1;
/* 2125 */         for (j = (arrayOfWire1 = inList).length, b = 0; b < j; ) { Wire w = arrayOfWire1[b];
/*      */           
/* 2127 */           if (!wireDefined[w.getWireId()]) {
/*      */             
/* 2129 */             notDefinedWires.add(Integer.valueOf(w.getWireId()));
/*      */             
/* 2131 */             allDefined = false;
/* 2132 */             ArrayList<Instruction> ilist = wireInstructionDependencies
/* 2133 */               .get(w);
/* 2134 */             if (ilist == null) {
/* 2135 */               ilist = new ArrayList<>();
/* 2136 */               wireInstructionDependencies.put(w, ilist);
/*      */             } 
/*      */             
/* 2139 */             ilist.add(instruction);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2145 */             ArrayList<Wire> wlist = instructionWireDependencies
/* 2146 */               .get(instruction);
/* 2147 */             if (wlist == null) {
/* 2148 */               wlist = new ArrayList<>();
/* 2149 */               instructionWireDependencies.put(instruction, wlist);
/*      */             } 
/* 2151 */             wlist.add(w);
/*      */           } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           b++; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2166 */         if (!(instruction instanceof backend.operations.primitive.AssertBasicOp)) {
/* 2167 */           for (j = (arrayOfWire1 = basicOp.getOutputs()).length, b = 0; b < j; ) { Wire w = arrayOfWire1[b];
/* 2168 */             wireVisited[w.getWireId()] = true;
/*      */             
/*      */             b++; }
/*      */         
/*      */         }
/* 2173 */         if (allDefined) {
/*      */           
/* 2175 */           sortedEvalSequence.put(instruction, instruction);
/* 2176 */           for (j = (arrayOfWire1 = basicOp.getOutputs()).length, b = 0; b < j; ) { Wire w = arrayOfWire1[b];
/*      */             
/* 2178 */             wireDefined[w.getWireId()] = true;
/* 2179 */             notDefinedWires.remove(Integer.valueOf(w.getWireId()));
/*      */             
/* 2181 */             ArrayList<Instruction> ilist = wireInstructionDependencies
/* 2182 */               .get(w);
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2187 */             if (ilist != null) {
/* 2188 */               for (Instruction instruction1 : ilist) {
/* 2189 */                 ArrayList<Wire> list = instructionWireDependencies
/* 2190 */                   .get(instruction1);
/* 2191 */                 if (list != null) {
/* 2192 */                   list.remove(w);
/* 2193 */                   if (list.isEmpty()) {
/* 2194 */                     iQueue.add(instruction1);
/*      */                     
/* 2196 */                     instructionWireDependencies.remove(instruction1);
/*      */                   } 
/*      */                 } 
/*      */               } 
/*      */             }
/* 2201 */             wireInstructionDependencies.remove(w);
/*      */ 
/*      */             
/*      */             b++; }
/*      */         
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 2211 */     while (!iQueue.isEmpty()) {
/*      */       
/* 2213 */       Instruction i2 = iQueue.poll();
/* 2214 */       sortedEvalSequence.put(i2, i2);
/* 2215 */       if (i2 instanceof BasicOp) {
/* 2216 */         BasicOp basicOp = (BasicOp)i2;
/* 2217 */         if (!(basicOp instanceof backend.operations.primitive.AssertBasicOp)) {
/* 2218 */           byte b; int j; Wire[] arrayOfWire; for (j = (arrayOfWire = basicOp.getOutputs()).length, b = 0; b < j; ) { Wire w = arrayOfWire[b];
/* 2219 */             wireDefined[w.getWireId()] = true;
/* 2220 */             wireVisited[w.getWireId()] = true;
/* 2221 */             notDefinedWires.remove(Integer.valueOf(w.getWireId()));
/* 2222 */             ArrayList<Instruction> ilist = wireInstructionDependencies
/* 2223 */               .get(w);
/* 2224 */             if (ilist != null) {
/* 2225 */               for (Instruction i3 : ilist) {
/* 2226 */                 ArrayList<Wire> list = instructionWireDependencies
/* 2227 */                   .get(i3);
/* 2228 */                 if (list != null) {
/* 2229 */                   list.remove(w);
/* 2230 */                   if (list.isEmpty()) {
/* 2231 */                     iQueue.add(i3);
/* 2232 */                     instructionWireDependencies
/* 2233 */                       .remove(i3);
/*      */                   } 
/*      */                 } 
/*      */               } 
/*      */             }
/*      */             
/* 2239 */             wireInstructionDependencies.remove(w); b++; }
/*      */         
/*      */         }  continue;
/*      */       } 
/* 2243 */       throw new RuntimeException("Unexpected case (sanity check failed). Please report this case.");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2283 */     this.newEvalSequence = sortedEvalSequence;
/*      */     
/* 2285 */     Wire[] wireMap = new Wire[this.generator.__getCurrentWireId()];
/*      */     
/* 2287 */     for (Instruction ii : this.newEvalSequence.keySet()) {
/*      */       byte b; int j; Wire[] arrayOfWire;
/* 2289 */       for (j = (arrayOfWire = ii.getUsedWires()).length, b = 0; b < j; ) { Wire w = arrayOfWire[b];
/* 2290 */         if (wireMap[w.getWireId()] == null) {
/* 2291 */           this.newWireCount++;
/* 2292 */           wireMap[w.getWireId()] = w;
/*      */         
/*      */         }
/* 2295 */         else if (!wireMap[w.getWireId()].equals(w)) {
/*      */           
/* 2297 */           throw new RuntimeException(
/* 2298 */               "Unexpected Case (sanity check failed)- Please report this case.");
/*      */         } 
/*      */ 
/*      */         
/*      */         b++; }
/*      */     
/*      */     } 
/*      */ 
/*      */     
/* 2307 */     int idx = 0;
/*      */     
/* 2309 */     for (int i = 0; i < wireMap.length; i++) {
/* 2310 */       if (wireMap[i] != null) {
/* 2311 */         this.newToOldIndexMap.put(Integer.valueOf(idx), Integer.valueOf(i));
/*      */         
/* 2313 */         wireMap[i].setWireId(idx);
/*      */ 
/*      */         
/* 2316 */         idx++;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2330 */     for (Instruction instruction : this.newEvalSequence.keySet()) {
/* 2331 */       if (instruction instanceof WireLabelInstruction && (
/* 2332 */         (WireLabelInstruction)instruction).getType() == WireLabelInstruction.LabelType.output) {
/* 2333 */         newOutputInstructions.put(((WireLabelInstruction)instruction).getWire(), instruction);
/* 2334 */         newOutputs.add(((WireLabelInstruction)instruction).getWire());
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 2339 */     ArrayList<Wire> outputsToPromote = new ArrayList<>();
/*      */     
/* 2341 */     for (Instruction instruction : this.newEvalSequence.keySet()) {
/* 2342 */       if (instruction instanceof BasicOp) {
/* 2343 */         BasicOp basicOp = (BasicOp)instruction;
/* 2344 */         if (basicOp instanceof backend.operations.primitive.AddBasicOp || basicOp instanceof backend.operations.primitive.PackBasicOp || basicOp instanceof ConstMulBasicOp) {
/* 2345 */           byte b; int j; Wire[] arrayOfWire; for (j = (arrayOfWire = basicOp.getOutputs()).length, b = 0; b < j; ) { Wire w = arrayOfWire[b];
/* 2346 */             if (newOutputs.contains(w))
/*      */             {
/* 2348 */               outputsToPromote.add(w);
/*      */             }
/*      */             
/*      */             b++; }
/*      */         
/*      */         } 
/*      */       } 
/*      */     } 
/* 2356 */     for (Wire w : outputsToPromote) {
/* 2357 */       this.newEvalSequence.remove(newOutputInstructions.get(w));
/* 2358 */       Wire promoted = new Wire(this.newWireCount++);
/* 2359 */       MulBasicOp mulBasicOp = new MulBasicOp(w, this.generator.__getOneWire(), promoted, new String[0]);
/* 2360 */       WireLabelInstruction wireLabelInstruction = new WireLabelInstruction(WireLabelInstruction.LabelType.output, promoted, new String[0]);
/* 2361 */       this.newEvalSequence.put(mulBasicOp, mulBasicOp);
/* 2362 */       this.newEvalSequence.put(wireLabelInstruction, wireLabelInstruction);
/*      */       
/* 2364 */       this.newToOldIndexMap.put(Integer.valueOf(promoted.getWireId()), this.newToOldIndexMap.get(Integer.valueOf(w.getWireId())));
/*      */     } 
/*      */ 
/*      */     
/* 2368 */     this.generator.__setCurrentWireId(this.newWireCount);
/* 2369 */     this.generator.__setEvaluationQueue(this.newEvalSequence);
/*      */     
/* 2371 */     int numNewIns = 0;
/* 2372 */     int numNewOuts = 0;
/* 2373 */     int numNewWitnesses = 0;
/* 2374 */     int numNewSplits = 0;
/* 2375 */     int numNewPacks = 0;
/* 2376 */     int numNewAssertions = 0;
/* 2377 */     int numNewNonzeroChecks = 0;
/*      */     
/* 2379 */     int numMulGates = 0;
/* 2380 */     for (Instruction instruction : this.newEvalSequence.keySet()) {
/*      */       
/* 2382 */       if (instruction instanceof WireLabelInstruction) {
/*      */         
/* 2384 */         WireLabelInstruction labelInstruction = (WireLabelInstruction)instruction;
/* 2385 */         if (labelInstruction.getType() != WireLabelInstruction.LabelType.input) {
/* 2386 */           labelInstruction.getType();
/*      */         }
/*      */         
/* 2389 */         if (labelInstruction.getType() == WireLabelInstruction.LabelType.input) {
/* 2390 */           numNewIns++;
/*      */         }
/* 2392 */         if (labelInstruction.getType() == WireLabelInstruction.LabelType.nizkinput) {
/* 2393 */           numNewWitnesses++;
/*      */         }
/* 2395 */         if (labelInstruction.getType() == WireLabelInstruction.LabelType.output)
/* 2396 */           numNewOuts++; 
/*      */         continue;
/*      */       } 
/* 2399 */       if (instruction instanceof BasicOp) {
/* 2400 */         BasicOp op = (BasicOp)instruction;
/* 2401 */         numMulGates += op.getNumMulGates();
/*      */         
/* 2403 */         if (instruction instanceof backend.operations.primitive.SplitBasicOp) {
/* 2404 */           numNewSplits++;
/*      */         }
/* 2406 */         if (instruction instanceof backend.operations.primitive.PackBasicOp) {
/* 2407 */           numNewPacks++;
/*      */         }
/* 2409 */         if (instruction instanceof backend.operations.primitive.AssertBasicOp) {
/* 2410 */           numNewAssertions++;
/*      */         }
/* 2412 */         if (instruction instanceof backend.operations.primitive.NonZeroCheckBasicOp) {
/* 2413 */           numNewNonzeroChecks++;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2421 */     System.out
/* 2422 */       .println("[Arithmetic Optimizer] Savings due to arithmetic minimization = " + 
/* 2423 */         this.totalSavings + "  (Note: Sometimes, the number of savings reported in this line could be higher than the actual total savings.)");
/*      */     
/* 2425 */     System.out
/* 2426 */       .println("[Arithmetic Optimizer] Number of total mul gates before arithmetic minimization =  " + 
/* 2427 */         this.numOriginalMulGates);
/* 2428 */     System.out
/* 2429 */       .println("[Arithmetic Optimizer] Number of total mul gates after  arithmetic minimization =  " + 
/* 2430 */         numMulGates);
/*      */ 
/*      */     
/* 2433 */     if (numNewOuts != this.numOriginalOuts || numNewIns != this.numOriginalIns || 
/* 2434 */       numNewWitnesses != this.numOriginalWitnesses || 
/* 2435 */       numNewSplits != this.numOriginalSplits || 
/* 2436 */       numNewAssertions != this.numOriginalAssertions || 
/* 2437 */       numNewPacks != this.numOriginalPacks || 
/* 2438 */       numNewNonzeroChecks != this.numOriginalNonzeroChecks) {
/*      */       
/* 2440 */       System.out.println("Mismatches Found: ");
/* 2441 */       System.out.println(String.valueOf(numNewIns) + "," + numNewOuts + "," + 
/* 2442 */           numNewWitnesses + "," + numNewSplits + "," + numNewPacks + 
/* 2443 */           "," + numNewAssertions + "," + numNewNonzeroChecks);
/* 2444 */       System.out.println(String.valueOf(this.numOriginalIns) + "," + this.numOriginalOuts + "," + 
/* 2445 */           this.numOriginalWitnesses + "," + this.numOriginalSplits + "," + 
/* 2446 */           this.numOriginalPacks + "," + this.numOriginalAssertions + "," + 
/* 2447 */           this.numOriginalNonzeroChecks);
/* 2448 */       throw new RuntimeException(
/* 2449 */           "Mismatches found in the new version of the circuit. Please report this case.");
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2465 */     if (notDefinedWires.size() > 0) {
/* 2466 */       throw new RuntimeException(
/* 2467 */           "Inconsistency found in the new version of the circuit. Please report this case.");
/*      */     }
/* 2469 */     System.out.println("Initial sanity checks passed.");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CircuitEvaluator mapFromOldEvaluationSeq(CircuitEvaluator oldEvaluator) {
/* 2489 */     BigInteger[] oldAssignment = oldEvaluator.getAssignment();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2498 */     BigInteger[] newAssignment = new BigInteger[this.newWireCount];
/* 2499 */     for (Instruction instruction : this.newEvalSequence.keySet()) {
/* 2500 */       if (instruction instanceof WireLabelInstruction) {
/*      */         
/* 2502 */         WireLabelInstruction labelInstruction = (WireLabelInstruction)instruction;
/* 2503 */         if (labelInstruction.getType() == WireLabelInstruction.LabelType.input || 
/* 2504 */           labelInstruction.getType() == WireLabelInstruction.LabelType.nizkinput) {
/* 2505 */           newAssignment[labelInstruction.getWire().getWireId()] = oldAssignment[((Integer)this.newToOldIndexMap
/* 2506 */               .get(Integer.valueOf(labelInstruction.getWire().getWireId()))).intValue()];
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/* 2511 */     CircuitEvaluator circuitEvaluator = new CircuitEvaluator(this.generator, 
/* 2512 */         newAssignment);
/*      */     
/* 2514 */     circuitEvaluator.evaluate();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2519 */     for (Instruction instruction : this.newEvalSequence.keySet()) {
/* 2520 */       if (instruction instanceof WireLabelInstruction) {
/* 2521 */         WireLabelInstruction labelInstruction = (WireLabelInstruction)instruction;
/* 2522 */         if (labelInstruction.getType() == WireLabelInstruction.LabelType.output && 
/*      */           
/* 2524 */           !newAssignment[labelInstruction.getWire().getWireId()].equals(oldAssignment[((Integer)this.newToOldIndexMap
/* 2525 */               .get(Integer.valueOf(labelInstruction.getWire().getWireId()))).intValue()])) {
/* 2526 */           throw new RuntimeException(
/* 2527 */               "Circuit output after multivariate optimization don't match the expected output");
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2540 */     return circuitEvaluator;
/*      */   }
/*      */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\CircuitOptimizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */