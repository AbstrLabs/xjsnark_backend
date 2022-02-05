/*     */ package backend.optimizer.arithmetic;
/*     */ 
/*     */ import backend.optimizer.arithmetic.poly.MultivariatePolynomial;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ExpressionMinimizer
/*     */ {
/*     */   private String[] inVarsStrings;
/*     */   private String[] outVarsStrings;
/*     */   private ArrayList<MultivariatePolynomial> list;
/*     */   private Cube oneCube;
/*  19 */   int kcmIntermediateVarCounter = 0;
/*  20 */   int cimIntermediateVarCounter = 0;
/*  21 */   int cimExpressionCounter = 0;
/*  22 */   int cubeGlobalCount = 1;
/*     */ 
/*     */   
/*     */   private HashMap<String, MultivariatePolynomial> solution;
/*     */ 
/*     */ 
/*     */   
/*     */   public ExpressionMinimizer(String[] inVarsStrings, String[] outVarsStrings, ArrayList<MultivariatePolynomial> list) {
/*  30 */     this.inVarsStrings = inVarsStrings;
/*  31 */     this.outVarsStrings = outVarsStrings;
/*  32 */     this.list = list;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HashMap<String, MultivariatePolynomial> run(int level) {
/*  38 */     ExpressionMinimizer m = new ExpressionMinimizer(this.inVarsStrings, 
/*  39 */         this.outVarsStrings, this.list, level);
/*     */     
/*  41 */     return m.getSolution();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExpressionMinimizer(String[] inVarsStrings, String[] outVarsStrings, ArrayList<MultivariatePolynomial> list, int level) {
/*  51 */     ArrayList<ExpressionMatrix> polys = new ArrayList<>();
/*  52 */     for (int i = 0; i < list.size(); i++) {
/*  53 */       ExpressionMatrix expMat = new ExpressionMatrix(this, list.get(i), inVarsStrings, 
/*  54 */           outVarsStrings[i]);
/*  55 */       polys.add(expMat);
/*  56 */       expMat.setIndex(i);
/*     */     } 
/*     */     
/*  59 */     HashMap<BigInteger, Integer> constantIndices = new LinkedHashMap<>();
/*  60 */     for (ExpressionMatrix exp : polys) {
/*  61 */       exp.extend(constantIndices, false);
/*     */     }
/*  63 */     for (ExpressionMatrix exp : polys) {
/*  64 */       exp.extend(constantIndices, true);
/*     */     }
/*  66 */     int j = 0;
/*  67 */     boolean tooLarge = false;
/*     */     
/*  69 */     while (j < 10 && 
/*  70 */       !Thread.currentThread().isInterrupted()) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  76 */       j++;
/*  77 */       this.oneCube = Cube.getOneCube((((ExpressionMatrix)polys.get(0)).getVars()).length, (((ExpressionMatrix)polys.get(0)).getConstStrs()).length);
/*     */       
/*  79 */       LinkedHashMap<KernelCoKernelPair, ArrayList<ExpressionMatrix>> kernelCoKernelMap = findKernels(
/*  80 */           polys, ((ExpressionMatrix)polys.get(0)).getLiterals());
/*     */ 
/*     */       
/*  83 */       HashSet<Cube> distinctCoKernelSet = new LinkedHashSet<>();
/*  84 */       HashSet<Cube> distinctKernelCubeSet = new LinkedHashSet<>();
/*     */       
/*  86 */       for (KernelCoKernelPair pair : kernelCoKernelMap.keySet()) {
/*  87 */         distinctCoKernelSet.add(new Cube(pair.getCoKernel()));
/*  88 */         for (Cube c : pair.getKernel().getPowers()) {
/*  89 */           if (!c.equals(this.oneCube)) {
/*  90 */             distinctKernelCubeSet.add(new Cube(c));
/*     */           }
/*     */         } 
/*     */       } 
/*     */       
/*  95 */       distinctKernelCubeSet.add(new Cube(this.oneCube));
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
/* 108 */       if (distinctCoKernelSet.size() * distinctKernelCubeSet
/* 109 */         .size() > 1000000L) {
/* 110 */         tooLarge = true;
/*     */         
/*     */         break;
/*     */       } 
/*     */       
/* 115 */       KCM kcm = new KCM(this, kernelCoKernelMap, distinctCoKernelSet, 
/* 116 */           distinctKernelCubeSet, polys);
/*     */       
/* 118 */       j++;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 127 */       boolean updated = kcm.extract();
/* 128 */       if (!updated) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 137 */     if (!Thread.currentThread().isInterrupted() && !tooLarge) {
/* 138 */       CIM cim = new CIM(this, polys);
/* 139 */       cim.print();
/* 140 */       this.solution = cim.extract();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private LinkedHashMap<KernelCoKernelPair, ArrayList<ExpressionMatrix>> findKernels(ArrayList<ExpressionMatrix> polys, String[] vars) {
/* 148 */     LinkedHashSet<KernelCoKernelPair> set = new LinkedHashSet<>();
/* 149 */     LinkedHashMap<KernelCoKernelPair, ArrayList<ExpressionMatrix>> map = new LinkedHashMap<>();
/* 150 */     for (ExpressionMatrix exp : polys) {
/*     */       
/* 152 */       HashSet<KernelCoKernelPair> s = findKernelsHelpers(0, exp, null);
/* 153 */       for (KernelCoKernelPair p : s) {
/* 154 */         if (map.containsKey(p)) {
/* 155 */           ((ArrayList<ExpressionMatrix>)map.get(p)).add(exp);
/*     */           continue;
/*     */         } 
/* 158 */         ArrayList<ExpressionMatrix> l = new ArrayList<>();
/* 159 */         l.add(exp);
/* 160 */         map.put(p, l);
/*     */       } 
/*     */ 
/*     */       
/* 164 */       set.addAll(findKernelsHelpers(0, exp, null));
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 170 */     for (ExpressionMatrix exp : polys) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 177 */       KernelCoKernelPair p = new KernelCoKernelPair(new ExpressionMatrix(exp), 
/* 178 */           new Cube(this.oneCube));
/* 179 */       if (map.containsKey(p)) {
/* 180 */         ((ArrayList<ExpressionMatrix>)map.get(p)).add(exp);
/*     */         continue;
/*     */       } 
/* 183 */       ArrayList<ExpressionMatrix> l = new ArrayList<>();
/* 184 */       l.add(exp);
/* 185 */       map.put(p, l);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 190 */     return map;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private HashSet<KernelCoKernelPair> findKernelsHelpers(int idx, ExpressionMatrix exp, Cube cube) {
/* 197 */     String[] literals = exp.getLiterals();
/* 198 */     ArrayList<Cube> terms = exp.getPowers();
/*     */     
/* 200 */     HashSet<KernelCoKernelPair> out = new HashSet<>();
/*     */     
/* 202 */     for (int i = idx; i < literals.length; i++) {
/* 203 */       int count = 0;
/* 204 */       for (Cube c : terms) {
/*     */         
/* 206 */         count++;
/* 207 */         if (c.get(i) > 0 && count == 2) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */       
/* 212 */       if (count > 1) {
/*     */         
/* 214 */         ExpressionMatrix F_t = exp.divide(i);
/*     */ 
/*     */ 
/*     */         
/* 218 */         Cube largestDividingCube = F_t.largestDividingCube();
/*     */ 
/*     */ 
/*     */         
/* 222 */         boolean visited = false;
/* 223 */         for (int k = 0; k < i; k++) {
/* 224 */           if (largestDividingCube.getPower(k) > 0) {
/* 225 */             visited = true;
/*     */             break;
/*     */           } 
/*     */         } 
/* 229 */         if (!visited) {
/*     */ 
/*     */           
/* 232 */           ExpressionMatrix F_l = F_t.divide(largestDividingCube);
/* 233 */           Cube merged = merge(cube, largestDividingCube, i);
/* 234 */           Cube c = new Cube(merged);
/* 235 */           out.add(new KernelCoKernelPair(F_l, c));
/* 236 */           out.addAll(findKernelsHelpers(i, F_l, merged));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 242 */     return out;
/*     */   }
/*     */   
/*     */   private Cube merge(Cube cube, Cube largestDividingCube, int varIdx) {
/* 246 */     int[] newCubeVars = new int[largestDividingCube.getNumOfVars()];
/* 247 */     int[] newCubeConsts = new int[largestDividingCube.getNumOfConstants()];
/*     */     
/* 249 */     if (varIdx < newCubeVars.length) {
/* 250 */       newCubeVars[varIdx] = newCubeVars[varIdx] + 1;
/*     */     } else {
/* 252 */       newCubeConsts[varIdx - newCubeVars.length] = newCubeConsts[varIdx - newCubeVars.length] + 1;
/*     */     } 
/*     */     int i;
/* 255 */     for (i = 0; i < newCubeVars.length; i++) {
/*     */       
/* 257 */       if (cube != null)
/* 258 */         newCubeVars[i] = newCubeVars[i] + cube.get(i); 
/* 259 */       newCubeVars[i] = newCubeVars[i] + largestDividingCube.get(i);
/*     */     } 
/* 261 */     for (i = 0; i < newCubeConsts.length; i++) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 266 */       if (cube != null)
/* 267 */         newCubeConsts[i] = newCubeConsts[i] + cube.get(i + newCubeVars.length); 
/* 268 */       newCubeConsts[i] = newCubeConsts[i] + largestDividingCube.get(i + newCubeVars.length);
/*     */     } 
/*     */     
/* 271 */     return new Cube(newCubeVars, newCubeConsts);
/*     */   }
/*     */   
/*     */   private int[] merge(int[] cube, int[] largestDividingCube, int varIdx) {
/* 275 */     int[] newCube = new int[largestDividingCube.length];
/* 276 */     newCube[varIdx] = 1;
/* 277 */     for (int i = 0; i < largestDividingCube.length; i++) {
/* 278 */       if (cube != null)
/* 279 */         newCube[i] = newCube[i] + cube[i]; 
/* 280 */       newCube[i] = newCube[i] + largestDividingCube[i];
/*     */     } 
/* 282 */     return newCube;
/*     */   }
/*     */   
/*     */   public HashMap<String, MultivariatePolynomial> getSolution() {
/* 286 */     return this.solution;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\ExpressionMinimizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */