/*     */ package backend.auxTypes;
/*     */ 
/*     */ import backend.config.Config;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Random;
/*     */ import util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StaticMemorySolver
/*     */ {
/*     */   public static void preprocess(BigInteger[] vals, int outputBitwidth, ArrayList<UnsignedInteger> indexes, SmartMemory.MemoryState state) {
/*     */     boolean useBits;
/*     */     int bitCount;
/*  18 */     boolean inputBitsSplit = false;
/*     */ 
/*     */     
/*  21 */     if (indexes == null)
/*     */       return; 
/*  23 */     if (vals.length <= 256) {
/*  24 */       useBits = true;
/*  25 */       int countSplitted = 0;
/*  26 */       for (UnsignedInteger u : indexes) {
/*  27 */         if (u.getState().isSplittedAhead())
/*  28 */           countSplitted++; 
/*     */       } 
/*  30 */       if (countSplitted > vals.length / 2) {
/*  31 */         inputBitsSplit = true;
/*     */       }
/*     */     } else {
/*  34 */       useBits = false;
/*     */     } 
/*     */     
/*  37 */     int n = vals.length;
/*  38 */     int sqrtN = (int)Math.ceil(Math.sqrt(n));
/*  39 */     int nearestSqr = sqrtN * sqrtN;
/*  40 */     int indexBitwidth = (int)Math.ceil(Math.log(n) / Math.log(2.0D));
/*     */     
/*  42 */     int[] indices = new int[nearestSqr];
/*  43 */     BigInteger[] paddVals = new BigInteger[nearestSqr]; int i;
/*  44 */     for (i = 0; i < n; i++) {
/*  45 */       indices[i] = i;
/*  46 */       paddVals[i] = vals[i];
/*     */     } 
/*  48 */     for (i = n; i < nearestSqr; i++) {
/*  49 */       indices[i] = i - n;
/*  50 */       paddVals[i] = vals[i - n];
/*     */     } 
/*     */     
/*  53 */     ArrayList<BigInteger[]> allCoeffSet = (ArrayList)new ArrayList<>();
/*  54 */     ArrayList<BigInteger> list = new ArrayList<>();
/*  55 */     for (int j = 0; j < nearestSqr; j++) {
/*  56 */       list.add(BigInteger.valueOf(indices[j])
/*  57 */           .multiply(Util.computeBound(outputBitwidth)).add(paddVals[j]));
/*     */     }
/*     */     
/*  60 */     boolean done = false;
/*  61 */     int seed = 1;
/*  62 */     int trialCounter = 0;
/*     */ 
/*     */     
/*  65 */     if (useBits) {
/*  66 */       bitCount = sqrtN - 1;
/*     */     } else {
/*  68 */       bitCount = 0;
/*     */     } 
/*     */     
/*  71 */     label104: while (bitCount >= 0) {
/*  72 */       trialCounter = 0;
/*  73 */       label102: while (!done) {
/*  74 */         trialCounter++;
/*  75 */         if (trialCounter == 10) {
/*  76 */           bitCount--;
/*     */           
/*     */           break;
/*     */         } 
/*  80 */         System.out
/*  81 */           .println("Attempting to solve linear systems for efficient Read-only memory access: Attempt#" + 
/*  82 */             trialCounter + " -- bitcount = " + bitCount);
/*  83 */         seed++;
/*  84 */         Collections.shuffle(list, new Random(seed));
/*  85 */         allCoeffSet.clear();
/*     */         
/*  87 */         for (int k = 0; k <= sqrtN - 1; k++) {
/*  88 */           BigInteger[][] mat = new BigInteger[sqrtN][sqrtN + 1];
/*  89 */           HashSet<BigInteger> memberValueSet = new HashSet<>();
/*     */           
/*  91 */           for (int m = 0; m < mat.length; m++) {
/*     */             
/*  93 */             BigInteger memberValue = list.get(m + k * sqrtN);
/*  94 */             memberValueSet.add(memberValue);
/*  95 */             mat[m][sqrtN] = BigInteger.ONE;
/*     */ 
/*     */             
/*  98 */             BigInteger v = memberValue.add(BigInteger.ONE);
/*  99 */             BigInteger product = v;
/* 100 */             if (bitCount != 0) {
/* 101 */               product = product.multiply(v).mod(
/* 102 */                   Config.getFiniteFieldModulus());
/*     */             }
/* 104 */             for (int i1 = 0; i1 <= sqrtN - 1; i1++) {
/* 105 */               int b = i1;
/* 106 */               if (i1 < bitCount) {
/* 107 */                 mat[m][i1] = memberValue.testBit(b) ? BigInteger.ONE : 
/* 108 */                   BigInteger.ZERO;
/*     */               } else {
/*     */                 
/* 111 */                 mat[m][i1] = product;
/* 112 */                 product = product.multiply(
/* 113 */                     memberValue.add(BigInteger.ONE)).mod(
/* 114 */                     Config.getFiniteFieldModulus());
/*     */               } 
/*     */             } 
/*     */           } 
/*     */           
/* 119 */           (new LinearSystemSolver(mat)).solveInPlace();
/* 120 */           if (checkIfProverCanCheat(mat, memberValueSet, sqrtN, bitCount, outputBitwidth + indexBitwidth)) {
/* 121 */             System.out.println("Invalid solution");
/* 122 */             for (int i1 = 0; i1 < sqrtN; i1++) {
/* 123 */               if (mat[i1][sqrtN].equals(BigInteger.ZERO)) {
/* 124 */                 System.out
/* 125 */                   .println("Possibly invalid due to having zero coefficient(s)");
/*     */                 
/*     */                 break;
/*     */               } 
/*     */             } 
/*     */             
/*     */             continue label102;
/*     */           } 
/* 133 */           BigInteger[] coeffs = new BigInteger[sqrtN];
/* 134 */           for (int ii = 0; ii < sqrtN; ii++) {
/* 135 */             coeffs[ii] = mat[ii][sqrtN];
/*     */           }
/*     */           
/* 138 */           allCoeffSet.add(coeffs);
/*     */         } 
/*     */         
/* 141 */         done = true;
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
/*     */         break label104;
/*     */       } 
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
/* 203 */     System.out.println("done with read only memory solutions!");
/* 204 */     state.bitCount = bitCount;
/* 205 */     state.allCoeffSet = allCoeffSet;
/*     */   }
/*     */ 
/*     */   
/*     */   private static BigInteger[] getVariableValues(BigInteger val, int sqrtN, int bitCount) {
/* 210 */     BigInteger[] vars = new BigInteger[sqrtN];
/*     */     
/* 212 */     BigInteger v = val.add(BigInteger.ONE);
/* 213 */     BigInteger product = v;
/* 214 */     if (bitCount != 0) {
/* 215 */       product = product.multiply(v).mod(Config.getFiniteFieldModulus());
/*     */     }
/* 217 */     for (int j = 0; j < sqrtN; j++) {
/* 218 */       if (j < bitCount) {
/* 219 */         int b = j;
/* 220 */         vars[j] = val.testBit(b) ? BigInteger.ONE : BigInteger.ZERO;
/*     */       } else {
/* 222 */         vars[j] = product;
/* 223 */         product = product.multiply(v).mod(Config.getFiniteFieldModulus());
/*     */       } 
/*     */     } 
/*     */     
/* 227 */     return vars;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean checkIfProverCanCheat(BigInteger[][] mat, HashSet<BigInteger> valueSet, int sqrtN, int bitCount, int totalBitwidth) {
/* 233 */     int nearestSqr = sqrtN * sqrtN;
/* 234 */     BigInteger[] coeffs = new BigInteger[sqrtN];
/* 235 */     for (int i = 0; i < sqrtN; i++) {
/* 236 */       coeffs[i] = mat[i][sqrtN];
/*     */     }
/*     */     
/* 239 */     int validResults = 0;
/* 240 */     int outsidePermissibleSet = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 248 */     BigInteger range = Util.computeBound(totalBitwidth);
/*     */ 
/*     */     
/* 251 */     for (int k = 0; k < range.intValue(); k++) {
/*     */ 
/*     */       
/* 254 */       BigInteger[] vars = getVariableValues(BigInteger.valueOf(k), sqrtN, bitCount);
/* 255 */       BigInteger result = BigInteger.ZERO;
/* 256 */       for (int j = 0; j < sqrtN; j++) {
/* 257 */         result = result.add(vars[j].multiply(coeffs[j]));
/*     */       }
/* 259 */       result = result.mod(Config.getFiniteFieldModulus());
/* 260 */       if (result.equals(BigInteger.ONE)) {
/*     */         
/* 262 */         validResults++;
/* 263 */         if (!valueSet.contains(BigInteger.valueOf(k))) {
/* 264 */           outsidePermissibleSet++;
/*     */         }
/*     */       } 
/*     */     } 
/* 268 */     if (validResults != sqrtN || outsidePermissibleSet != 0) {
/* 269 */       System.out.println("Prover can cheat with linear system solution");
/* 270 */       System.out.println("Num of valid values that the prover can use = " + 
/* 271 */           validResults);
/* 272 */       System.out.println("Num of valid values outside permissible set = " + 
/* 273 */           outsidePermissibleSet);
/* 274 */       return true;
/*     */     } 
/* 276 */     return false;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\StaticMemorySolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */