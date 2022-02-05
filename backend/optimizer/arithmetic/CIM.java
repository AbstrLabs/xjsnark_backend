/*     */ package backend.optimizer.arithmetic;
/*     */ 
/*     */ import backend.optimizer.arithmetic.poly.MultivariatePolynomial;
/*     */ import backend.optimizer.arithmetic.poly.OptVariable;
/*     */ import backend.optimizer.arithmetic.poly.Term;
/*     */ import backend.resource.ResourceBundle;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Set;
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
/*     */ public class CIM
/*     */ {
/*     */   ArrayList<ArrayList<Integer>> powersMatrix;
/*     */   ArrayList<Cube> rowCubes;
/*     */   ArrayList<Integer> expressionIndex;
/*     */   ArrayList<ExpressionMatrix> exps;
/*     */   ArrayList<String> vars;
/*     */   ArrayList<String> constants;
/*     */   LinkedHashMap<String, ArrayList<Integer>> expressionMap;
/*     */   private int numOriginalExps;
/*     */   private int numOriginalTerms;
/*     */   private ExpressionMinimizer minimizer;
/*     */   
/*     */   public CIM(ExpressionMinimizer minimizer, ArrayList<ExpressionMatrix> exps) {
/*  41 */     this.minimizer = minimizer;
/*     */ 
/*     */     
/*  44 */     this.vars = new ArrayList<>();
/*  45 */     String[] variables = ((ExpressionMatrix)exps.get(0)).getVars(); byte b; int j; String[] arrayOfString1;
/*  46 */     for (j = (arrayOfString1 = variables).length, b = 0; b < j; ) { String variable = arrayOfString1[b];
/*  47 */       this.vars.add(variable); b++; }
/*  48 */      this.constants = new ArrayList<>();
/*  49 */     for (j = (arrayOfString1 = ((ExpressionMatrix)exps.get(0)).getConstStrs()).length, b = 0; b < j; ) { String constant = arrayOfString1[b];
/*  50 */       this.constants.add(constant); b++; }
/*     */     
/*  52 */     this.powersMatrix = new ArrayList<>();
/*  53 */     this.exps = exps;
/*  54 */     this.numOriginalExps = exps.size();
/*  55 */     this.rowCubes = new ArrayList<>();
/*  56 */     this.expressionIndex = new ArrayList<>();
/*     */     
/*  58 */     this.expressionMap = new LinkedHashMap<>();
/*     */ 
/*     */     
/*  61 */     for (int i = 0; i < this.numOriginalExps; i++) {
/*     */       
/*  63 */       ExpressionMatrix exp = exps.get(i);
/*  64 */       ArrayList<Integer> listOfTerms = new ArrayList<>();
/*  65 */       this.expressionMap.put(exp.getLabel(), listOfTerms);
/*     */       
/*  67 */       for (Cube c : exp.getPowers()) {
/*  68 */         this.rowCubes.add(c);
/*  69 */         ArrayList<Integer> row = new ArrayList<>();
/*  70 */         this.powersMatrix.add(row); byte b1; int k, arrayOfInt[];
/*  71 */         for (k = (arrayOfInt = c.getVarPowers()).length, b1 = 0; b1 < k; ) { int p = arrayOfInt[b1];
/*  72 */           row.add(Integer.valueOf(p)); b1++; }
/*     */         
/*  74 */         this.expressionIndex.add(Integer.valueOf(i));
/*  75 */         listOfTerms.add(Integer.valueOf(minimizer.cimExpressionCounter));
/*  76 */         minimizer.cimExpressionCounter++;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  82 */     this.numOriginalTerms = minimizer.cimExpressionCounter;
/*     */   }
/*     */ 
/*     */   
/*     */   public HashMap<String, MultivariatePolynomial> extract() {
/*  87 */     ArrayList<ArrayList<Integer>> checkMatrix = new ArrayList<>();
/*  88 */     for (ArrayList<Integer> a : this.powersMatrix) {
/*  89 */       checkMatrix.add(new ArrayList<>(a));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     while (true) {
/*  95 */       int numRows = checkMatrix.size();
/*  96 */       int numCols = ((ArrayList)checkMatrix.get(0)).size();
/*  97 */       ArrayList<Rectangle> selectedRectangles = new ArrayList<>();
/*     */       
/*     */       while (true) {
/* 100 */         ArrayList<Rectangle> primeRectangles = new ArrayList<>();
/*     */         
/* 102 */         ArrayList<ArrayList<Integer>> roundCheckMatrix = new ArrayList<>();
/* 103 */         for (ArrayList<Integer> a : checkMatrix) {
/* 104 */           roundCheckMatrix.add(new ArrayList<>(a));
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 115 */         for (int j = 0; j < numRows; j++) {
/* 116 */           for (int k = 0; k < numCols; k++) {
/* 117 */             if (((Integer)((ArrayList<Integer>)roundCheckMatrix.get(j)).get(k)).intValue() > 0) {
/* 118 */               Rectangle bestRectangle = null;
/* 119 */               int maxSavings = 0;
/* 120 */               ArrayList<Integer> colAccum = new ArrayList<>();
/* 121 */               ArrayList<Integer> rowAccum = new ArrayList<>();
/* 122 */               colAccum.add(Integer.valueOf(k));
/* 123 */               int count = 0;
/* 124 */               HashSet<Integer> set = new HashSet<>(); int m;
/* 125 */               for (m = k + 1; m < numCols; m++) {
/* 126 */                 if (((Integer)((ArrayList<Integer>)roundCheckMatrix.get(j)).get(m)).intValue() > 0) {
/* 127 */                   count++;
/* 128 */                   set.add(Integer.valueOf(m));
/* 129 */                   if (count > 5) {
/*     */                     break;
/*     */                   }
/*     */                 } 
/*     */               } 
/*     */               
/* 135 */               if (count <= 5) {
/* 136 */                 Set<Set<Integer>> sets = Util.powerSet(set);
/* 137 */                 for (Set<Integer> s : sets) {
/* 138 */                   colAccum.clear();
/* 139 */                   colAccum.add(Integer.valueOf(k));
/* 140 */                   colAccum.addAll(s);
/* 141 */                   rowAccum = new ArrayList<>();
/* 142 */                   rowAccum.add(Integer.valueOf(j));
/* 143 */                   for (int l = j + 1; l < numRows; l++) {
/* 144 */                     boolean thingsOK = true;
/* 145 */                     for (Iterator<Integer> iterator = colAccum.iterator(); iterator.hasNext(); ) { int col = ((Integer)iterator.next()).intValue();
/* 146 */                       if (((Integer)((ArrayList<Integer>)roundCheckMatrix.get(l))
/* 147 */                         .get(col)).intValue() == 0) {
/* 148 */                         thingsOK = false;
/*     */                       } }
/*     */                     
/* 151 */                     if (thingsOK) {
/* 152 */                       rowAccum.add(Integer.valueOf(l));
/*     */                     }
/*     */                   } 
/*     */ 
/*     */ 
/*     */ 
/*     */                   
/* 159 */                   Rectangle tmp = new Rectangle(rowAccum, 
/* 160 */                       new ArrayList<>(colAccum));
/* 161 */                   int tmpSavings = tmp
/* 162 */                     .getSavings(roundCheckMatrix);
/* 163 */                   if (tmpSavings > maxSavings) {
/* 164 */                     maxSavings = tmpSavings;
/* 165 */                     bestRectangle = tmp;
/*     */                   } 
/*     */                 } 
/*     */               } else {
/* 169 */                 for (m = k + 1; m < numCols; m++) {
/* 170 */                   rowAccum = new ArrayList<>();
/* 171 */                   rowAccum.add(Integer.valueOf(j));
/* 172 */                   if (((Integer)((ArrayList<Integer>)roundCheckMatrix.get(j)).get(m)).intValue() > 0) {
/* 173 */                     colAccum.add(Integer.valueOf(m));
/* 174 */                     for (int l = j + 1; l < numRows; l++) {
/* 175 */                       boolean thingsOK = true;
/* 176 */                       ArrayList<Integer> a3 = roundCheckMatrix
/* 177 */                         .get(l);
/* 178 */                       for (Iterator<Integer> iterator = colAccum.iterator(); iterator.hasNext(); ) { int col = ((Integer)iterator.next()).intValue();
/* 179 */                         if (((Integer)a3.get(col)).intValue() == 0) {
/* 180 */                           thingsOK = false;
/*     */                         } }
/*     */                       
/* 183 */                       if (thingsOK) {
/* 184 */                         rowAccum.add(Integer.valueOf(l));
/*     */                       }
/*     */                     } 
/* 187 */                     Rectangle tmp = new Rectangle(
/* 188 */                         rowAccum, 
/* 189 */                         new ArrayList<>(colAccum));
/* 190 */                     int tmpSavings = tmp
/* 191 */                       .getSavings(roundCheckMatrix);
/*     */ 
/*     */ 
/*     */                     
/* 195 */                     if (tmpSavings > maxSavings) {
/* 196 */                       maxSavings = tmpSavings;
/* 197 */                       bestRectangle = tmp;
/*     */                     } 
/*     */                   } 
/*     */                 } 
/*     */               } 
/* 202 */               if (bestRectangle != null) {
/* 203 */                 primeRectangles.add(bestRectangle);
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 211 */         int max = 0;
/* 212 */         Rectangle best = null;
/* 213 */         for (Rectangle rect : primeRectangles) {
/* 214 */           int s = rect.savings;
/* 215 */           if (s > max) {
/* 216 */             max = s;
/* 217 */             best = rect;
/*     */           } 
/*     */         } 
/*     */         
/* 221 */         if (max == 0) {
/*     */           break;
/*     */         }
/* 224 */         if (max == 1) {
/* 225 */           for (Rectangle rect : primeRectangles) {
/* 226 */             if (rect.getSavings(checkMatrix) == 1) {
/* 227 */               selectedRectangles.add(rect);
/* 228 */               rect.markArea(checkMatrix);
/*     */             } 
/*     */           } 
/*     */ 
/*     */           
/*     */           break;
/*     */         } 
/*     */         
/* 236 */         selectedRectangles.add(best);
/* 237 */         best.markArea(checkMatrix);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 244 */       int numOfOldVars = numCols;
/* 245 */       int numOfNewVars = selectedRectangles.size();
/* 246 */       if (numOfNewVars == 0) {
/*     */         break;
/*     */       }
/* 249 */       for (int i = 0; i < numOfNewVars; i++) {
/* 250 */         this.vars.add("tmp_" + this.minimizer.cimIntermediateVarCounter++);
/*     */       }
/*     */       
/* 253 */       for (ArrayList<Integer> a : checkMatrix) {
/* 254 */         for (int j = 0; j < numOfNewVars; j++) {
/* 255 */           a.add(Integer.valueOf(0));
/*     */         }
/*     */       } 
/*     */       
/* 259 */       int index = 0;
/* 260 */       for (Rectangle r : selectedRectangles) {
/*     */         
/* 262 */         ArrayList<Integer> newRecord = r.getNewRecord(index, 
/* 263 */             numOfOldVars, numOfNewVars);
/*     */         
/* 265 */         this.expressionIndex.add(Integer.valueOf(this.minimizer.cimExpressionCounter));
/*     */         
/* 267 */         ArrayList<Integer> listOfTerms = new ArrayList<>();
/* 268 */         listOfTerms.add(Integer.valueOf(this.minimizer.cimExpressionCounter));
/* 269 */         this.expressionMap.put(this.vars.get(numOfOldVars + index), listOfTerms);
/*     */         
/* 271 */         r.applyReplacement(numOfOldVars, index, checkMatrix);
/* 272 */         checkMatrix.add(newRecord);
/* 273 */         index++;
/* 274 */         this.minimizer.cimExpressionCounter++;
/*     */       } 
/*     */     } 
/*     */     
/* 278 */     HashMap<String, MultivariatePolynomial> solutions = new HashMap<>();
/* 279 */     for (String label : this.expressionMap.keySet()) {
/* 280 */       ArrayList<Integer> termIndecies = this.expressionMap.get(label);
/* 281 */       MultivariatePolynomial mvp = new MultivariatePolynomial();
/* 282 */       for (int k = 0; k < termIndecies.size(); k++) {
/* 283 */         int idx = ((Integer)termIndecies.get(k)).intValue();
/* 284 */         BigInteger constValue = BigInteger.ONE;
/*     */ 
/*     */         
/* 287 */         if (idx < this.numOriginalTerms) {
/* 288 */           Cube c = this.rowCubes.get(idx);
/* 289 */           int[] powers = c.getConstPowers();
/* 290 */           for (int j = 0; j < powers.length; j++) {
/* 291 */             int p = powers[j];
/* 292 */             if (p > 0) {
/*     */ 
/*     */               
/* 295 */               constValue = ResourceBundle.getInstance()
/* 296 */                 .getBigInteger(
/* 297 */                   new BigInteger(this.constants.get(j)));
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         } 
/* 303 */         ArrayList<Integer> term = checkMatrix.get(idx);
/* 304 */         Term t = new Term(constValue);
/* 305 */         for (int i = 0; i < term.size(); i++) {
/* 306 */           int p = ((Integer)term.get(i)).intValue();
/* 307 */           if (p > 0) {
/* 308 */             t = t.multiply(new Term(new OptVariable(this.vars.get(i)), 
/* 309 */                   (short)p));
/*     */           }
/*     */         } 
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
/* 328 */         mvp = mvp.addInPlace(new MultivariatePolynomial(t));
/*     */       } 
/*     */ 
/*     */       
/* 332 */       solutions.put(label, mvp);
/*     */     } 
/* 334 */     return solutions;
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
/*     */ 
/*     */   
/*     */   private class Rectangle
/*     */   {
/* 397 */     private ArrayList<Integer> rowList = new ArrayList<>();
/* 398 */     private ArrayList<Integer> colList = new ArrayList<>();
/*     */     private int[] newCube;
/* 400 */     int savings = -1;
/*     */     int[] minPowers;
/*     */     
/*     */     public Rectangle(ArrayList<Integer> rowList, ArrayList<Integer> colList) {
/* 404 */       this.rowList = rowList;
/* 405 */       this.colList = colList;
/*     */     }
/*     */ 
/*     */     
/*     */     public ArrayList<Integer> getNewRecord(int index, int numOfOldVars, int numOfNewVars) {
/* 410 */       ArrayList<Integer> newRecord = new ArrayList<>(); int i;
/* 411 */       for (i = 0; i < numOfOldVars + numOfNewVars; i++) {
/* 412 */         newRecord.add(Integer.valueOf(0));
/*     */       }
/* 414 */       i = 0;
/* 415 */       for (Iterator<Integer> iterator = this.colList.iterator(); iterator.hasNext(); ) { int c = ((Integer)iterator.next()).intValue();
/* 416 */         newRecord.set(c, Integer.valueOf(this.minPowers[i]));
/* 417 */         i++; }
/*     */       
/* 419 */       return newRecord;
/*     */     }
/*     */ 
/*     */     
/*     */     public void applyReplacement(int numOfOldVars, int index, ArrayList<ArrayList<Integer>> extendedCheckMatrix) {
/* 424 */       for (Iterator<Integer> iterator = this.rowList.iterator(); iterator.hasNext(); ) { int r = ((Integer)iterator.next()).intValue();
/* 425 */         ((ArrayList<Integer>)extendedCheckMatrix.get(r)).set(numOfOldVars + index, Integer.valueOf(1)); }
/*     */     
/*     */     }
/*     */     
/*     */     public void markArea(ArrayList<ArrayList<Integer>> checkMatrix) {
/* 430 */       int numRows = this.rowList.size();
/* 431 */       int numCols = this.colList.size();
/*     */       
/* 433 */       for (int i = 0; i < numRows; i++) {
/* 434 */         ArrayList<Integer> a = checkMatrix.get(((Integer)this.rowList.get(i)).intValue());
/* 435 */         for (int j = 0; j < numCols; j++) {
/* 436 */           int v = ((Integer)a.get(((Integer)this.colList.get(j)).intValue())).intValue();
/* 437 */           a.set(((Integer)this.colList.get(j)).intValue(), Integer.valueOf(v - this.minPowers[j]));
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public int getSavings(ArrayList<ArrayList<Integer>> roundCheckMatrix) {
/* 444 */       int s = 0;
/* 445 */       int numRows = this.rowList.size();
/* 446 */       int numCols = this.colList.size();
/*     */       
/* 448 */       this.minPowers = new int[numCols];
/* 449 */       Arrays.fill(this.minPowers, 2147483647);
/* 450 */       for (int i = 0; i < numRows; i++) {
/* 451 */         ArrayList<Integer> a = roundCheckMatrix.get(((Integer)this.rowList.get(i)).intValue());
/* 452 */         for (int k = 0; k < numCols; k++) {
/* 453 */           int v = ((Integer)a.get(((Integer)this.colList.get(k)).intValue())).intValue();
/* 454 */           if (v < this.minPowers[k])
/* 455 */             this.minPowers[k] = v; 
/*     */         } 
/*     */       } 
/*     */       byte b;
/*     */       int j, arrayOfInt[];
/* 460 */       for (j = (arrayOfInt = this.minPowers).length, b = 0; b < j; ) { int p = arrayOfInt[b];
/* 461 */         s += p;
/*     */         b++; }
/*     */       
/* 464 */       this.savings = (s - 1) * (numRows - 1);
/* 465 */       return this.savings;
/*     */     }
/*     */     
/*     */     public void print() {
/* 469 */       for (Iterator<Integer> iterator = this.rowList.iterator(); iterator.hasNext(); ) { int r = ((Integer)iterator.next()).intValue();
/* 470 */         for (Iterator<Integer> iterator1 = this.colList.iterator(); iterator1.hasNext(); ) { int c = ((Integer)iterator1.next()).intValue();
/* 471 */           System.out.print("(" + r + "," + c + ") "); }
/*     */          }
/*     */       
/* 474 */       System.out.println();
/*     */     }
/*     */   }
/*     */   
/*     */   public void print() {}
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\CIM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */