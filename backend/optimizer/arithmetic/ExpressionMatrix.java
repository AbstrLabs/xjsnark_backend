/*     */ package backend.optimizer.arithmetic;
/*     */ 
/*     */ import backend.optimizer.arithmetic.poly.MultivariatePolynomial;
/*     */ import backend.optimizer.arithmetic.poly.OptVariable;
/*     */ import backend.optimizer.arithmetic.poly.Term;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ public class ExpressionMatrix
/*     */ {
/*     */   private String[] vars;
/*     */   private String[] constStrs;
/*     */   private ArrayList<BigInteger> constants;
/*     */   private ArrayList<Cube> powers;
/*  19 */   private HashSet<Cube> cubeSet = new HashSet<>();
/*     */   
/*     */   private int numLiterals;
/*     */   private String label;
/*     */   private int index;
/*     */   private ExpressionMinimizer minimizer;
/*     */   private String[] allLiterals;
/*     */   
/*     */   public int hashCode() {
/*  28 */     int code = 0;
/*  29 */     for (Cube c : this.powers) {
/*  30 */       code += c.hashCode();
/*     */     }
/*  32 */     return code;
/*     */   }
/*     */   
/*     */   public void setLabel(String label) {
/*  36 */     this.label = label;
/*     */   }
/*     */   
/*     */   public String getLabel() {
/*  40 */     return this.label;
/*     */   }
/*     */ 
/*     */   
/*     */   public void extendTempVariables(String[] newVariables) {
/*  45 */     this.numLiterals += newVariables.length;
/*  46 */     this.vars = Util.<String>concatenate(this.vars, newVariables);
/*  47 */     for (Cube c : this.powers) {
/*  48 */       c.extendWithIntermediateVars(newVariables.length);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ExpressionMatrix(ExpressionMinimizer minimizer, ArrayList<BigInteger> constants, ArrayList<Cube> powers, String[] vars, String[] consts) {
/*  54 */     this.minimizer = minimizer;
/*  55 */     this.constants = constants;
/*  56 */     this.powers = powers;
/*  57 */     this.cubeSet.addAll(powers);
/*     */ 
/*     */     
/*  60 */     this.numLiterals = ((Cube)powers.get(0)).getNumOfLiterals();
/*  61 */     this.vars = vars;
/*  62 */     this.constStrs = consts;
/*     */   }
/*     */ 
/*     */   
/*     */   public ExpressionMatrix(ExpressionMatrix mat) {
/*  67 */     this.minimizer = mat.minimizer;
/*  68 */     this.constants = mat.constants;
/*     */     
/*  70 */     this.constStrs = mat.constStrs;
/*  71 */     this.vars = mat.vars;
/*  72 */     this.powers = mat.getPowersCopy();
/*  73 */     this.cubeSet.addAll(this.powers);
/*  74 */     this.numLiterals = ((Cube)this.powers.get(0)).getNumOfLiterals();
/*     */   }
/*     */ 
/*     */   
/*     */   public ExpressionMatrix(ExpressionMinimizer minimizer, MultivariatePolynomial p, String[] vars, String label) {
/*  79 */     this.minimizer = minimizer;
/*  80 */     this.vars = vars;
/*  81 */     this.constants = new ArrayList<>();
/*  82 */     this.powers = new ArrayList<>();
/*  83 */     constructMatrix(p);
/*  84 */     this.label = label;
/*  85 */     this.cubeSet.addAll(this.powers);
/*     */   }
/*     */ 
/*     */   
/*     */   private void constructMatrix(MultivariatePolynomial p) {
/*  90 */     HashMap<String, Integer> indexMap = new HashMap<>();
/*  91 */     int index = 0; byte b; int i; String[] arrayOfString;
/*  92 */     for (i = (arrayOfString = this.vars).length, b = 0; b < i; ) { String v = arrayOfString[b];
/*  93 */       indexMap.put(v, Integer.valueOf(index++)); b++; }
/*     */     
/*  95 */     for (Term t : p.getTerms()) {
/*  96 */       this.constants.add(t.getCoeff());
/*  97 */       int[] arr = new int[this.vars.length];
/*  98 */       Term.VarIterator it = t.getVarIterator();
/*  99 */       while (it.hasNext()) {
/* 100 */         OptVariable v = it.next();
/* 101 */         int power = it.getExponent();
/* 102 */         arr[((Integer)indexMap.get(v.getLabel())).intValue()] = power;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 109 */       Cube c = new Cube(arr, t.getCoeff());
/* 110 */       c.genSerial(this.minimizer);
/* 111 */       this.powers.add(c);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void extend(HashMap<BigInteger, Integer> constantIndices, boolean state) {
/* 119 */     if (!state) {
/* 120 */       int index = constantIndices.size();
/* 121 */       for (BigInteger c : this.constants) {
/* 122 */         if (!c.abs().equals(BigInteger.ONE) && 
/* 123 */           !constantIndices.containsKey(c)) {
/*     */           
/* 125 */           constantIndices.put(c, Integer.valueOf(index));
/* 126 */           index++;
/*     */         } 
/*     */       } 
/*     */     } else {
/* 130 */       int index = 0;
/* 131 */       for (Cube c : this.powers) {
/* 132 */         c.extendWithConstantPowers(constantIndices.size(), constantIndices);
/*     */       }
/* 134 */       int constSize = constantIndices.size();
/* 135 */       this.constStrs = new String[constSize];
/*     */       
/* 137 */       this.numLiterals += constSize;
/*     */ 
/*     */ 
/*     */       
/* 141 */       index = 0;
/* 142 */       for (BigInteger k : constantIndices.keySet()) {
/* 143 */         this.constStrs[index++] = k.toString();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<BigInteger> getConstants() {
/* 151 */     return this.constants;
/*     */   }
/*     */   
/*     */   public ArrayList<Cube> getPowers() {
/* 155 */     return this.powers;
/*     */   }
/*     */   
/*     */   public ArrayList<Cube> getPowersCopy() {
/* 159 */     ArrayList<Cube> result = new ArrayList<>();
/* 160 */     for (Cube c : this.powers) {
/* 161 */       result.add(new Cube(c));
/*     */     }
/* 163 */     return result;
/*     */   }
/*     */   
/*     */   public boolean isDivisible(int[] cube) {
/* 167 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public ExpressionMatrix divide(Cube c) {
/* 172 */     ArrayList<BigInteger> newConstants = new ArrayList<>();
/* 173 */     ArrayList<Cube> newPowers = new ArrayList<>();
/*     */     
/* 175 */     for (int i = 0; i < this.powers.size(); i++) {
/* 176 */       Cube tmp = ((Cube)this.powers.get(i)).divide(c);
/* 177 */       if (tmp != null)
/*     */       {
/* 179 */         newPowers.add(tmp);
/*     */       }
/*     */     } 
/* 182 */     return new ExpressionMatrix(this.minimizer, newConstants, newPowers, this.vars, this.constStrs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Cube largestDividingCube() {
/* 200 */     return Cube.largestDividingCube(this.powers);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasCube(Cube c) {
/* 222 */     return this.cubeSet.contains(c);
/*     */   }
/*     */   
/*     */   public int serialOfCube(Cube c) {
/* 226 */     int idx = this.powers.indexOf(c);
/* 227 */     return ((Cube)this.powers.get(idx)).getSerial();
/*     */   }
/*     */   
/*     */   public int numCubes() {
/* 231 */     return this.powers.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public ExpressionMatrix divide(int varIndex) {
/* 236 */     ArrayList<BigInteger> newConstants = new ArrayList<>();
/* 237 */     ArrayList<Cube> newPowers = new ArrayList<>();
/*     */     
/* 239 */     for (int i = 0; i < this.powers.size(); i++) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 248 */       Cube c = ((Cube)this.powers.get(i)).divide(varIndex);
/* 249 */       if (c != null) {
/* 250 */         newPowers.add(c);
/*     */       }
/*     */     } 
/* 253 */     return new ExpressionMatrix(this.minimizer, newConstants, newPowers, this.vars, this.constStrs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void print() {
/* 261 */     for (int i = 0; i < this.powers.size(); i++) {
/* 262 */       System.out.println(String.valueOf(((Cube)this.powers.get(i)).getSerial()) + ">>" + this.powers.get(i));
/*     */     }
/* 264 */     System.out.println("===================");
/*     */   }
/*     */   
/*     */   public void apply(ArrayList<KCM.Replacement> replacements) {
/* 268 */     for (KCM.Replacement replacement : replacements) {
/* 269 */       ArrayList<Integer> snList = replacement.getSNList();
/* 270 */       boolean found = false;
/* 271 */       ArrayList<Cube> toDelete = new ArrayList<>(); Iterator<Integer> iterator;
/* 272 */       for (iterator = snList.iterator(); iterator.hasNext(); ) { int sn = ((Integer)iterator.next()).intValue();
/* 273 */         for (Cube c : this.powers) {
/*     */           
/* 275 */           if (c.getSerial() == sn) {
/* 276 */             found = true;
/* 277 */             toDelete.add(c);
/*     */           } 
/*     */         }  }
/*     */       
/* 281 */       if (found) {
/* 282 */         for (Cube cube : toDelete) {
/* 283 */           this.powers.remove(cube);
/*     */         }
/* 285 */         Cube c = new Cube(replacement.getNewCube());
/* 286 */         c.genSerial(this.minimizer);
/* 287 */         this.powers.add(c);
/* 288 */         this.cubeSet.add(c);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public String[] getVars() {
/* 294 */     return this.vars;
/*     */   }
/*     */   
/*     */   public String[] getConstStrs() {
/* 298 */     return this.constStrs;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getLiterals() {
/* 304 */     if (this.allLiterals == null)
/*     */     {
/* 306 */       this.allLiterals = Util.<String>concatenate(this.vars, this.constStrs);
/*     */     }
/* 308 */     return this.allLiterals;
/*     */   }
/*     */   
/*     */   public void setIndex(int i) {
/* 312 */     this.index = i;
/*     */   }
/*     */   
/*     */   public int getIndex() {
/* 316 */     return this.index;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\ExpressionMatrix.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */