/*     */ package backend.optimizer.arithmetic;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
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
/*     */ public class KCM
/*     */ {
/*     */   private int[][] matrix;
/*     */   private ArrayList<SerialRecord>[][] serialNums;
/*     */   private LinkedHashMap<Cube, Integer> coKernelIndexMap;
/*     */   private LinkedHashMap<Cube, Integer> kernelCubeIndexMap;
/*     */   private int[] rowCosts;
/*     */   private int[] colCosts;
/*     */   private boolean[] rowConstFlags;
/*     */   private boolean[] colConstFlags;
/*     */   private Cube[] rowCubes;
/*     */   private Cube[] colCubes;
/*     */   private int numRows;
/*     */   private int numCols;
/*     */   private ArrayList<ExpressionMatrix> exps;
/*     */   private ExpressionMinimizer minimizer;
/*     */   
/*     */   public class SerialRecord
/*     */   {
/*     */     int serialNum;
/*     */     int expIdx;
/*     */     
/*     */     SerialRecord(int expIdx, int serialNum) {
/*  41 */       this.expIdx = expIdx;
/*  42 */       this.serialNum = serialNum;
/*     */     }
/*     */     
/*     */     public String toString() {
/*  46 */       return String.valueOf(this.expIdx) + ":" + this.serialNum;
/*     */     }
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
/*     */   public KCM(ExpressionMinimizer expressionMinimizer, LinkedHashMap<KernelCoKernelPair, ArrayList<ExpressionMatrix>> kernelCoKernelMap, HashSet<Cube> distinctCoKernelSet, HashSet<Cube> distinctKernelCubeSet, ArrayList<ExpressionMatrix> exps) {
/*  60 */     this.exps = exps;
/*  61 */     this.minimizer = expressionMinimizer;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  66 */     this.matrix = 
/*  67 */       new int[distinctCoKernelSet.size()][distinctKernelCubeSet.size()];
/*  68 */     this.serialNums = 
/*  69 */       (ArrayList<SerialRecord>[][])new ArrayList[distinctCoKernelSet.size()][distinctKernelCubeSet.size()];
/*  70 */     this.coKernelIndexMap = new LinkedHashMap<>();
/*  71 */     this.kernelCubeIndexMap = new LinkedHashMap<>();
/*  72 */     int idx = 0;
/*     */     
/*  74 */     this.rowCosts = new int[distinctCoKernelSet.size()];
/*  75 */     this.colCosts = new int[distinctKernelCubeSet.size()];
/*  76 */     this.rowCubes = new Cube[distinctCoKernelSet.size()];
/*  77 */     this.colCubes = new Cube[distinctKernelCubeSet.size()];
/*     */     
/*  79 */     this.colConstFlags = new boolean[this.colCosts.length];
/*  80 */     this.rowConstFlags = new boolean[this.rowCosts.length];
/*     */     
/*  82 */     this.numRows = this.rowCubes.length;
/*  83 */     this.numCols = this.colCubes.length;
/*     */ 
/*     */     
/*  86 */     for (Cube cube : distinctCoKernelSet) {
/*  87 */       this.coKernelIndexMap.put(cube, Integer.valueOf(idx));
/*  88 */       this.rowCubes[idx] = cube;
/*     */       
/*  90 */       this.rowCosts[idx] = cube.getCost();
/*     */ 
/*     */       
/*  93 */       this.rowConstFlags[idx] = cube.isConstant();
/*  94 */       idx++;
/*     */     } 
/*  96 */     idx = 0;
/*     */     
/*  98 */     for (Cube cube : distinctKernelCubeSet) {
/*  99 */       this.kernelCubeIndexMap.put(cube, Integer.valueOf(idx));
/* 100 */       this.colCubes[idx] = cube;
/*     */       
/* 102 */       this.colCosts[idx] = cube.getCost();
/* 103 */       this.colConstFlags[idx] = cube.isConstant();
/*     */       
/* 105 */       idx++;
/*     */     } 
/*     */ 
/*     */     
/* 109 */     int c = 0, r = c;
/*     */     
/* 111 */     for (KernelCoKernelPair pair : kernelCoKernelMap.keySet()) {
/* 112 */       ArrayList<ExpressionMatrix> affectExpressions = kernelCoKernelMap
/* 113 */         .get(pair);
/* 114 */       Cube coKernel = pair.getCoKernel();
/* 115 */       r = ((Integer)this.coKernelIndexMap.get(coKernel)).intValue();
/* 116 */       for (Cube cube : pair.getKernel().getPowers()) {
/* 117 */         c = ((Integer)this.kernelCubeIndexMap.get(cube)).intValue();
/* 118 */         Cube c3 = Cube.add(coKernel, cube);
/*     */         
/* 120 */         for (ExpressionMatrix exp : affectExpressions) {
/* 121 */           if (exp.hasCube(c3)) {
/* 122 */             if (this.matrix[r][c] > 0) {
/* 123 */               this.serialNums[r][c].add(new SerialRecord(exp
/* 124 */                     .getIndex(), exp.serialOfCube(c3)));
/* 125 */               this.matrix[r][c] = this.matrix[r][c] + 1; continue;
/*     */             } 
/* 127 */             this.matrix[r][c] = this.matrix[r][c] + 1;
/* 128 */             this.serialNums[r][c] = new ArrayList<>();
/* 129 */             this.serialNums[r][c].add(new SerialRecord(exp
/* 130 */                   .getIndex(), exp.serialOfCube(c3)));
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
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
/*     */   public boolean extract() {
/* 169 */     int[][] checkMatrix = new int[this.numRows][this.numCols];
/* 170 */     for (int i = 0; i < this.numRows; i++) {
/* 171 */       System.arraycopy(this.matrix[i], 0, checkMatrix[i], 0, this.numCols);
/*     */     }
/*     */     
/* 174 */     ArrayList<Rectangle> selectedRectangles = new ArrayList<>();
/* 175 */     HashSet<Integer> coveredSerialNumbers = new HashSet<>();
/*     */ 
/*     */     
/*     */     while (true) {
/* 179 */       if (Thread.currentThread().isInterrupted()) {
/* 180 */         return false;
/*     */       }
/*     */ 
/*     */       
/* 184 */       ArrayList<Rectangle> primeRectangles = new ArrayList<>();
/* 185 */       HashSet<Integer> roundCoveredSerialNumbers = new HashSet<>();
/* 186 */       int[][] roundCheckMatrix = new int[this.numRows][this.numCols]; int m;
/* 187 */       for (m = 0; m < this.numRows; m++) {
/* 188 */         System.arraycopy(checkMatrix[m], 0, roundCheckMatrix[m], 0, 
/* 189 */             this.numCols);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 194 */       for (m = 0; m < this.numRows - 1; m++) {
/*     */ 
/*     */ 
/*     */         
/* 198 */         for (int n = 0; n < this.numCols; n++) {
/*     */           
/* 200 */           if (Thread.currentThread().isInterrupted()) {
/* 201 */             return false;
/*     */           }
/*     */           
/* 204 */           if (checkMatrix[m][n] > 0) {
/* 205 */             Rectangle bestRectangle = null;
/* 206 */             int maxSavings = 0;
/* 207 */             ArrayList<Integer> colAccum = new ArrayList<>();
/* 208 */             ArrayList<Integer> rowAccum = new ArrayList<>();
/* 209 */             colAccum.add(Integer.valueOf(n));
/* 210 */             int count = 0;
/* 211 */             HashSet<Integer> set = new HashSet<>(); int i1;
/* 212 */             for (i1 = n + 1; i1 < this.numCols; i1++) {
/* 213 */               if (checkMatrix[m][i1] > 0) {
/* 214 */                 count++;
/* 215 */                 set.add(Integer.valueOf(i1));
/* 216 */                 if (count > 5) {
/*     */                   break;
/*     */                 }
/*     */               } 
/*     */             } 
/*     */             
/* 222 */             if (count <= 5) {
/* 223 */               Set<Set<Integer>> sets = Util.powerSet(set);
/* 224 */               for (Set<Integer> s : sets) {
/* 225 */                 colAccum.clear();
/* 226 */                 colAccum.add(Integer.valueOf(n));
/* 227 */                 colAccum.addAll(s);
/* 228 */                 rowAccum = new ArrayList<>();
/* 229 */                 rowAccum.add(Integer.valueOf(m));
/* 230 */                 for (int l = m + 1; l < this.numRows; l++) {
/* 231 */                   boolean thingsOK = true;
/* 232 */                   for (Iterator<Integer> iterator = colAccum.iterator(); iterator.hasNext(); ) { int col = ((Integer)iterator.next()).intValue();
/* 233 */                     if (checkMatrix[l][col] == 0) {
/* 234 */                       thingsOK = false;
/*     */                     } }
/*     */                   
/* 237 */                   if (thingsOK) {
/* 238 */                     rowAccum.add(Integer.valueOf(l));
/*     */                   }
/*     */                 } 
/*     */                 
/* 242 */                 if (rowAccum.size() == 1 && 
/* 243 */                   colAccum.size() == 1)
/*     */                   continue; 
/* 245 */                 Rectangle tmp = new Rectangle(rowAccum, 
/* 246 */                     new ArrayList<>(colAccum));
/* 247 */                 int tmpSavings = tmp.getSavings(
/* 248 */                     coveredSerialNumbers, 
/* 249 */                     roundCoveredSerialNumbers);
/* 250 */                 if (tmpSavings > maxSavings) {
/* 251 */                   maxSavings = tmpSavings;
/* 252 */                   bestRectangle = tmp;
/*     */                 } 
/*     */               } 
/*     */             } else {
/* 256 */               for (i1 = n + 1; i1 < this.numCols; i1++) {
/* 257 */                 rowAccum = new ArrayList<>();
/* 258 */                 rowAccum.add(Integer.valueOf(m));
/* 259 */                 if (checkMatrix[m][i1] > 0) {
/* 260 */                   colAccum.add(Integer.valueOf(i1));
/* 261 */                   for (int l = m + 1; l < this.numRows; l++) {
/* 262 */                     boolean thingsOK = true;
/* 263 */                     for (Iterator<Integer> iterator = colAccum.iterator(); iterator.hasNext(); ) { int col = ((Integer)iterator.next()).intValue();
/* 264 */                       if (checkMatrix[l][col] == 0) {
/* 265 */                         thingsOK = false;
/*     */                       } }
/*     */                     
/* 268 */                     if (thingsOK) {
/* 269 */                       rowAccum.add(Integer.valueOf(l));
/*     */                     }
/*     */                   } 
/* 272 */                   Rectangle tmp = new Rectangle(rowAccum, 
/* 273 */                       new ArrayList<>(colAccum));
/* 274 */                   int tmpSavings = tmp.getSavings(
/* 275 */                       coveredSerialNumbers, 
/* 276 */                       roundCoveredSerialNumbers);
/*     */ 
/*     */                   
/* 279 */                   if (tmpSavings > maxSavings) {
/* 280 */                     maxSavings = tmpSavings;
/* 281 */                     bestRectangle = tmp;
/*     */                   } 
/*     */                 } 
/*     */               } 
/*     */             } 
/* 286 */             if (bestRectangle != null) {
/* 287 */               primeRectangles.add(bestRectangle);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 295 */       int max = 0;
/* 296 */       Rectangle best = null;
/* 297 */       for (Rectangle rect : primeRectangles) {
/*     */ 
/*     */         
/* 300 */         int s = rect.savings;
/*     */ 
/*     */         
/* 303 */         if (s > max) {
/* 304 */           max = s;
/* 305 */           best = rect;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 310 */       if (max == 0) {
/*     */         break;
/*     */       }
/* 313 */       selectedRectangles.add(best);
/*     */       
/* 315 */       coveredSerialNumbers.addAll(best.coveredSerialNumbers);
/* 316 */       best.markArea(checkMatrix);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 327 */     int prevVarsNum = ((Cube)((ExpressionMatrix)this.exps.get(0)).getPowers().get(0)).getNumOfVars();
/*     */     
/* 329 */     int numOfNewVariables = selectedRectangles.size();
/*     */     
/* 331 */     if (numOfNewVariables == 0) {
/* 332 */       return false;
/*     */     }
/* 334 */     String[] newVariables = new String[numOfNewVariables];
/* 335 */     for (int j = 0; j < newVariables.length; j++) {
/* 336 */       newVariables[j] = "d_" + this.minimizer.kcmIntermediateVarCounter++;
/*     */     }
/*     */     
/* 339 */     String[] allVars = 
/* 340 */       Util.<String>concatenate(((ExpressionMatrix)this.exps.get(0)).getVars(), newVariables);
/* 341 */     String[] allConsts = ((ExpressionMatrix)this.exps.get(0)).getConstStrs();
/*     */ 
/*     */ 
/*     */     
/* 345 */     for (ExpressionMatrix exp : this.exps)
/* 346 */       exp.extendTempVariables(newVariables);  byte b;
/*     */     int k;
/*     */     Cube[] arrayOfCube;
/* 349 */     for (k = (arrayOfCube = this.colCubes).length, b = 0; b < k; ) { Cube c = arrayOfCube[b];
/* 350 */       c.extendWithIntermediateVars(numOfNewVariables);
/*     */       
/*     */       b++; }
/*     */ 
/*     */     
/* 355 */     for (k = (arrayOfCube = this.rowCubes).length, b = 0; b < k; ) { Cube c = arrayOfCube[b];
/* 356 */       c.extendWithIntermediateVars(numOfNewVariables);
/*     */       
/*     */       b++; }
/*     */     
/* 360 */     int index = prevVarsNum;
/* 361 */     for (Rectangle r : selectedRectangles) {
/* 362 */       ArrayList<Replacement> replacements = r.getReplacements(index);
/* 363 */       for (ExpressionMatrix exp : this.exps) {
/* 364 */         exp.apply(replacements);
/*     */       }
/* 366 */       index++;
/*     */     } 
/* 368 */     index = 0;
/* 369 */     for (Rectangle r : selectedRectangles) {
/* 370 */       ExpressionMatrix expMat = r.getNewExpression(allVars, allConsts);
/* 371 */       expMat.setLabel(newVariables[index]);
/* 372 */       this.exps.add(expMat);
/* 373 */       expMat.setIndex(this.exps.size() - 1);
/* 374 */       index++;
/*     */     } 
/*     */ 
/*     */     
/* 378 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private class Rectangle
/*     */   {
/* 384 */     private ArrayList<Integer> rowList = new ArrayList<>();
/* 385 */     private ArrayList<Integer> colList = new ArrayList<>();
/*     */     private ExpressionMatrix newExpression;
/*     */     private ArrayList<ArrayList<Integer>> serialNumberPerRow;
/*     */     ArrayList<Integer>[] expIdsPerRow;
/*     */     ArrayList<Integer> coveredSerialNumbers;
/*     */     int[][] coveredCounts;
/* 391 */     int savings = -1;
/*     */ 
/*     */     
/*     */     public ExpressionMatrix getNewExpression(String[] allVars, String[] allConsts) {
/* 395 */       ArrayList<Cube> newCubes = new ArrayList<>();
/* 396 */       for (Iterator<Integer> iterator = this.colList.iterator(); iterator.hasNext(); ) { int col = ((Integer)iterator.next()).intValue();
/* 397 */         Cube c = new Cube(KCM.this.colCubes[col]);
/* 398 */         c.genSerial(KCM.this.minimizer);
/* 399 */         newCubes.add(c); }
/*     */       
/* 401 */       return new ExpressionMatrix(KCM.this.minimizer, new ArrayList<>(), newCubes, 
/* 402 */           allVars, allConsts);
/*     */     }
/*     */     
/*     */     public Rectangle(ArrayList<Integer> rowList, ArrayList<Integer> colList) {
/* 406 */       this.rowList = rowList;
/* 407 */       this.colList = colList;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int getSavings(HashSet<Integer> globalCoveredSerialNumbers, HashSet<Integer> roundCoveredSerialNumbers) {
/* 413 */       int[] rowCount = new int[this.rowList.size()];
/* 414 */       int[] colCount = new int[this.colList.size()];
/* 415 */       int s = 0;
/*     */       
/* 417 */       this.serialNumberPerRow = new ArrayList<>();
/* 418 */       this.expIdsPerRow = (ArrayList<Integer>[])new ArrayList[this.rowList.size()];
/* 419 */       this.coveredSerialNumbers = new ArrayList<>();
/* 420 */       this.coveredCounts = new int[this.rowList.size()][this.colList.size()];
/*     */       
/* 422 */       int numRows = this.rowList.size();
/* 423 */       int numCols = this.colList.size();
/*     */       
/* 425 */       for (int i = 0; i < numRows; i++) {
/* 426 */         this.serialNumberPerRow.add(new ArrayList<>());
/* 427 */         this.expIdsPerRow[i] = new ArrayList<>();
/* 428 */         boolean validRow = false;
/* 429 */         for (KCM.SerialRecord sr : KCM.this.serialNums[((Integer)this.rowList.get(i)).intValue()][((Integer)this.colList
/* 430 */             .get(0)).intValue()]) {
/* 431 */           int expId = sr.expIdx;
/* 432 */           boolean validExpId = true;
/*     */           
/* 434 */           int[] tmpCounts = new int[numCols];
/* 435 */           ArrayList<Integer> tmpCoveredSerialNumbers = new ArrayList<>();
/* 436 */           if (!globalCoveredSerialNumbers.contains(Integer.valueOf(sr.serialNum)) && 
/*     */             
/* 438 */             !roundCoveredSerialNumbers.contains(Integer.valueOf(sr.serialNum))) {
/*     */             
/* 440 */             tmpCoveredSerialNumbers.add(Integer.valueOf(sr.serialNum));
/* 441 */             tmpCounts[0] = tmpCounts[0] + 1;
/* 442 */             for (int j = 1; j < numCols; j++) {
/* 443 */               boolean found = false;
/* 444 */               for (KCM.SerialRecord sr2 : KCM.this.serialNums[((Integer)this.rowList.get(i)).intValue()][((Integer)this.colList
/* 445 */                   .get(j)).intValue()]) {
/* 446 */                 if (expId == sr2.expIdx && 
/*     */                   
/* 448 */                   !globalCoveredSerialNumbers.contains(Integer.valueOf(sr2.serialNum)) && 
/*     */                   
/* 450 */                   !roundCoveredSerialNumbers.contains(Integer.valueOf(sr2.serialNum))) {
/* 451 */                   found = true;
/* 452 */                   tmpCounts[j] = tmpCounts[j] + 1;
/* 453 */                   tmpCoveredSerialNumbers.add(Integer.valueOf(sr2.serialNum));
/*     */                   break;
/*     */                 } 
/*     */               } 
/* 457 */               if (!found) {
/* 458 */                 validExpId = false;
/*     */                 
/*     */                 break;
/*     */               } 
/*     */             } 
/*     */           } else {
/* 464 */             validExpId = false;
/*     */           } 
/*     */           
/* 467 */           if (validExpId) {
/* 468 */             validRow = true;
/* 469 */             this.expIdsPerRow[i].add(Integer.valueOf(expId));
/* 470 */             this.coveredSerialNumbers.addAll(tmpCoveredSerialNumbers);
/*     */             
/* 472 */             ((ArrayList<Integer>)this.serialNumberPerRow.get(i)).addAll(
/* 473 */                 tmpCoveredSerialNumbers);
/*     */             
/* 475 */             for (int j = 0; j < numCols; j++) {
/* 476 */               this.coveredCounts[i][j] = this.coveredCounts[i][j] + tmpCounts[j];
/*     */             }
/*     */           } 
/*     */         } 
/*     */         
/* 481 */         if (i == 0 && !validRow) {
/* 482 */           return 0;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 488 */       int rowIndex = 0, colIndex = 0;
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
/* 505 */       int numFullCells = 0; Iterator<Integer> iterator;
/* 506 */       for (iterator = this.rowList.iterator(); iterator.hasNext(); ) { int r = ((Integer)iterator.next()).intValue();
/*     */         
/* 508 */         int v = 0;
/* 509 */         int v2 = 0;
/* 510 */         colIndex = 0;
/* 511 */         for (Iterator<Integer> iterator1 = this.colList.iterator(); iterator1.hasNext(); ) { int c = ((Integer)iterator1.next()).intValue();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 518 */           v += this.coveredCounts[rowIndex][colIndex];
/* 519 */           numFullCells += (this.coveredCounts[rowIndex][colIndex] > 0) ? 1 : 0;
/* 520 */           if (!KCM.this.colConstFlags[c]) {
/* 521 */             v2 += this.coveredCounts[rowIndex][colIndex];
/*     */           }
/* 523 */           colIndex++; }
/*     */ 
/*     */         
/* 526 */         if (v != 0 && !KCM.this.rowConstFlags[r]) {
/* 527 */           s += v2 - 1;
/*     */         }
/* 529 */         rowCount[rowIndex++] = v;
/* 530 */         if (v != 0)
/* 531 */           s += v * KCM.this.rowCosts[r] - KCM.this.rowCosts[r];  }
/*     */       
/* 533 */       if (numFullCells <= 1) {
/* 534 */         return 0;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 539 */       colIndex = 0;
/* 540 */       for (iterator = this.colList.iterator(); iterator.hasNext(); ) { int c = ((Integer)iterator.next()).intValue();
/* 541 */         int v = 0;
/* 542 */         rowIndex = 0;
/* 543 */         for (Iterator<Integer> iterator1 = this.rowList.iterator(); iterator1.hasNext(); ) { int r = ((Integer)iterator1.next()).intValue();
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 548 */           v += this.coveredCounts[rowIndex][colIndex];
/* 549 */           rowIndex++; }
/*     */ 
/*     */         
/* 552 */         colCount[colIndex++] = v;
/* 553 */         if (v != 0)
/*     */         {
/* 555 */           s += v * KCM.this.colCosts[c] - KCM.this.colCosts[c];
/*     */         } }
/*     */ 
/*     */ 
/*     */       
/* 560 */       if (numRows != 1 || numCols == 1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 566 */       this.savings = s;
/*     */       
/* 568 */       return s;
/*     */     }
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
/*     */     public ArrayList<KCM.Replacement> getReplacements(int index) {
/* 589 */       ArrayList<KCM.Replacement> list = new ArrayList<>();
/* 590 */       int i = 0;
/* 591 */       for (Iterator<Integer> iterator = this.rowList.iterator(); iterator.hasNext(); ) { int r = ((Integer)iterator.next()).intValue();
/* 592 */         KCM.Replacement rep = new KCM.Replacement();
/* 593 */         int[] powers = Arrays.copyOf(KCM.this.rowCubes[r].getVarPowers(), (
/* 594 */             KCM.this.rowCubes[r].getVarPowers()).length);
/* 595 */         powers[index] = 1;
/* 596 */         rep.newCube = new Cube(powers, KCM.this.rowCubes[r].getConstPowers());
/* 597 */         rep.newCube.genSerial(KCM.this.minimizer);
/* 598 */         rep.snList = this.serialNumberPerRow.get(i);
/* 599 */         list.add(rep);
/* 600 */         i++; }
/*     */       
/* 602 */       return list;
/*     */     }
/*     */     
/*     */     public void markArea(int[][] checkMatrix) {
/* 606 */       int i = 0;
/* 607 */       for (Iterator<Integer> iterator = this.rowList.iterator(); iterator.hasNext(); ) { int r = ((Integer)iterator.next()).intValue();
/* 608 */         int j = 0;
/* 609 */         for (Iterator<Integer> iterator1 = this.colList.iterator(); iterator1.hasNext(); ) { int c = ((Integer)iterator1.next()).intValue();
/* 610 */           checkMatrix[r][c] = checkMatrix[r][c] - this.coveredCounts[i][j];
/* 611 */           j++; }
/*     */         
/* 613 */         i++; }
/*     */     
/*     */     }
/*     */     public void print() {
/*     */       Iterator<Integer> iterator;
/* 618 */       for (iterator = this.rowList.iterator(); iterator.hasNext(); ) { int r = ((Integer)iterator.next()).intValue();
/* 619 */         for (Iterator<Integer> iterator1 = this.colList.iterator(); iterator1.hasNext(); ) { int c = ((Integer)iterator1.next()).intValue();
/* 620 */           System.out.print("(" + r + "," + c + ") "); }
/*     */          }
/*     */       
/* 623 */       System.out.println();
/* 624 */       System.out.println("covered terms::");
/*     */       
/* 626 */       if (this.serialNumberPerRow != null) {
/* 627 */         for (ArrayList<Integer> lst : this.serialNumberPerRow) {
/* 628 */           for (Iterator<Integer> iterator1 = lst.iterator(); iterator1.hasNext(); ) { int sn = ((Integer)iterator1.next()).intValue();
/* 629 */             System.out.print(String.valueOf(sn) + ","); }
/*     */         
/*     */         } 
/*     */       }
/* 633 */       System.out.println();
/*     */       
/* 635 */       if (this.coveredSerialNumbers != null) {
/* 636 */         for (iterator = this.coveredSerialNumbers.iterator(); iterator.hasNext(); ) { int sn = ((Integer)iterator.next()).intValue();
/* 637 */           System.out.print(String.valueOf(sn) + ","); }
/*     */       
/*     */       }
/* 640 */       System.out.println();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public class Replacement
/*     */   {
/*     */     private ArrayList<Integer> snList;
/*     */     private Cube newCube;
/*     */     
/*     */     public Cube getNewCube() {
/* 651 */       return this.newCube;
/*     */     }
/*     */     
/*     */     public ArrayList<Integer> getSNList() {
/* 655 */       return this.snList;
/*     */     }
/*     */   }
/*     */   
/*     */   public void print() {
/* 660 */     System.out.print("\t\t"); int i;
/* 661 */     for (i = 0; i < this.kernelCubeIndexMap.size(); i++) {
/* 662 */       System.out.print("(" + i + ")\t");
/*     */     }
/* 664 */     System.out.println();
/*     */     
/* 666 */     System.out.print("\t\t");
/* 667 */     for (Cube c : this.kernelCubeIndexMap.keySet()) {
/* 668 */       System.out.print(c + "\t");
/*     */     }
/* 670 */     System.out.println();
/* 671 */     i = 0;
/* 672 */     for (Cube c : this.coKernelIndexMap.keySet()) {
/* 673 */       System.out.print("(" + i + ")\t");
/* 674 */       System.out.print(c + "\t");
/* 675 */       for (int j = 0; j < this.kernelCubeIndexMap.size(); j++) {
/*     */         
/* 677 */         print(this.serialNums[i][j]);
/* 678 */         System.out.print(",\t");
/*     */       } 
/* 680 */       System.out.println();
/* 681 */       i++;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void print(ArrayList<SerialRecord> arr) {
/* 686 */     if (arr == null) {
/* 687 */       System.out.print("null");
/*     */       return;
/*     */     } 
/* 690 */     for (SerialRecord a : arr)
/* 691 */       System.out.print(a + ","); 
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\KCM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */