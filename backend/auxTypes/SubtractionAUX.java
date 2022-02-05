/*     */ package backend.auxTypes;
/*     */ 
/*     */ import backend.structure.CircuitGenerator;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Arrays;
/*     */ import util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SubtractionAUX
/*     */ {
/*     */   public static BigInteger[] prepSub(PackedValue p2, BigInteger modulus, CircuitGenerator generator, int bitwidth) {
/*  14 */     BigInteger result[], max2 = p2.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */     
/*  16 */     BigInteger[] max2Chunks = p2.getCurrentMaxValues();
/*  17 */     BigInteger f = max2.divide(modulus);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  22 */     int i = 1; while (true) {
/*  23 */       BigInteger[] factorChunks, modChunks; if (i == 50)
/*  24 */         throw new RuntimeException("Exceeded iterations limit in subtraction circuit search."); 
/*  25 */       BigInteger factor = f.add(BigInteger.valueOf((i + 1)).shiftLeft((i - 1) * UnsignedInteger.BITWIDTH_PER_CHUNK));
/*     */ 
/*     */ 
/*     */       
/*  29 */       if (bitwidth > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  30 */         factorChunks = Util.split(factor, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  31 */         modChunks = Util.split(modulus, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */       } else {
/*     */         
/*  34 */         factorChunks = new BigInteger[] { factor };
/*  35 */         modChunks = new BigInteger[] { modulus };
/*     */       } 
/*     */       
/*  38 */       BigInteger[] base = mul(modChunks, factorChunks);
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
/*  49 */       int maxLength = Math.max(base.length, max2Chunks.length);
/*     */ 
/*     */       
/*  52 */       base = Util.padBigIntegerArray(base, maxLength);
/*  53 */       max2Chunks = Util.padBigIntegerArray(max2Chunks, maxLength);
/*     */       
/*  55 */       BigInteger prevCarry = BigInteger.ZERO;
/*  56 */       for (int j = 0; j < maxLength; j++) {
/*     */         
/*  58 */         if (j == maxLength - 1) {
/*     */ 
/*     */ 
/*     */           
/*  62 */           base[j] = base[j].subtract(prevCarry);
/*     */           
/*  64 */           if (base[j].subtract(max2Chunks[j]).signum() == -1) {
/*     */             i++;
/*     */             
/*     */             continue;
/*     */           } 
/*     */         } else {
/*  70 */           BigInteger delta = max2Chunks[j].add(prevCarry).divide(Util.computeBound(UnsignedInteger.BITWIDTH_PER_CHUNK));
/*  71 */           if (!max2Chunks[j].add(prevCarry).mod(Util.computeBound(UnsignedInteger.BITWIDTH_PER_CHUNK)).equals(BigInteger.ZERO)) {
/*  72 */             delta = delta.add(BigInteger.ONE);
/*     */           }
/*  74 */           base[j] = base[j].subtract(prevCarry).add(delta.multiply(Util.computeBound(UnsignedInteger.BITWIDTH_PER_CHUNK)));
/*  75 */           prevCarry = delta;
/*     */         } 
/*     */       } 
/*  78 */       result = base;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       break;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  94 */     return result;
/*     */   }
/*     */   
/*  97 */   static int chunkBitwidth = 64;
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 102 */     int numChunks = 1;
/*     */ 
/*     */ 
/*     */     
/* 106 */     BigInteger limit = (new BigInteger("2")).pow(numChunks * 64 - 1);
/* 107 */     System.out.println(limit.bitLength());
/*     */     
/* 109 */     BigInteger modulus = Util.nextRandomBigInteger(numChunks * 64);
/* 110 */     modulus = Util.nextRandomBigInteger(numChunks * 64 + 1);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 115 */     System.out.println(modulus.bitLength());
/* 116 */     System.out.println(limit.compareTo(modulus));
/*     */     
/* 118 */     BigInteger[] modChunks = Util.split(modulus, chunkBitwidth);
/*     */ 
/*     */     
/* 121 */     BigInteger[] p1 = new BigInteger[numChunks];
/* 122 */     Arrays.fill((Object[])p1, Util.computeMaxValue(chunkBitwidth));
/* 123 */     BigInteger[] p2 = new BigInteger[numChunks];
/* 124 */     Arrays.fill((Object[])p2, Util.computeMaxValue(chunkBitwidth));
/* 125 */     BigInteger[] p3 = mul(p1, p2);
/* 126 */     BigInteger[] p4 = add(p2, p3);
/*     */ 
/*     */ 
/*     */     
/* 130 */     BigInteger max = getMaxValue(p4);
/* 131 */     BigInteger factor = max.divide(limit).add(new BigInteger("2"));
/*     */ 
/*     */     
/* 134 */     BigInteger[] factorChunks = Util.split(factor, chunkBitwidth);
/*     */     
/* 136 */     BigInteger[] base = mul(modChunks, factorChunks);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 146 */     int maxLength = Math.max(base.length, p4.length);
/* 147 */     base = adjustLength(base, maxLength);
/* 148 */     p4 = adjustLength(p4, maxLength);
/*     */ 
/*     */     
/* 151 */     BigInteger prevCarry = BigInteger.ZERO;
/* 152 */     System.out.println(maxLength);
/*     */     
/* 154 */     System.out.println(factor.multiply(modulus));
/* 155 */     System.out.println(Util.group(base, chunkBitwidth));
/*     */     int i;
/* 157 */     for (i = 0; i < maxLength; i++) {
/* 158 */       if (i == maxLength - 1) {
/*     */         
/* 160 */         base[i] = base[i].subtract(prevCarry);
/*     */       } else {
/*     */         
/* 163 */         BigInteger delta = p4[i].add(prevCarry).divide(Util.computeBound(chunkBitwidth));
/* 164 */         if (!p4[i].add(prevCarry).mod(Util.computeBound(chunkBitwidth)).equals(BigInteger.ZERO)) {
/* 165 */           delta = delta.add(BigInteger.ONE);
/*     */         }
/*     */         
/* 168 */         base[i] = base[i].subtract(prevCarry).add(delta.multiply(Util.computeBound(chunkBitwidth)));
/* 169 */         prevCarry = delta;
/*     */       } 
/*     */     } 
/* 172 */     System.out.println("=================================================");
/* 173 */     print(base);
/* 174 */     print(p4);
/* 175 */     System.out.println("DIFFF;");
/* 176 */     for (i = 0; i < base.length; i++) {
/* 177 */       System.out.print(base[i].subtract(p4[i]) + ",");
/*     */     }
/*     */     
/* 180 */     System.out.println();
/*     */     
/* 182 */     System.out.println(factor.multiply(modulus));
/* 183 */     System.out.println(Util.group(base, chunkBitwidth));
/*     */   }
/*     */   
/*     */   static BigInteger[] adjustLength(BigInteger[] p, int size) {
/* 187 */     if (p.length >= size) {
/* 188 */       return p;
/*     */     }
/*     */     
/* 191 */     BigInteger[] tmp = new BigInteger[size];
/* 192 */     Arrays.fill((Object[])tmp, BigInteger.ZERO);
/* 193 */     System.arraycopy(p, 0, tmp, 0, p.length);
/* 194 */     return tmp;
/*     */   } static void print(BigInteger[] p) {
/*     */     byte b;
/*     */     int i;
/*     */     BigInteger[] arrayOfBigInteger;
/* 199 */     for (i = (arrayOfBigInteger = p).length, b = 0; b < i; ) { BigInteger pi = arrayOfBigInteger[b];
/* 200 */       System.out.print(pi + ","); b++; }
/*     */     
/* 202 */     System.out.println();
/*     */   }
/*     */   
/*     */   static BigInteger getMaxValue(BigInteger[] p) {
/* 206 */     return Util.group(p, chunkBitwidth);
/*     */   }
/*     */   
/*     */   static BigInteger[] mul(BigInteger[] p1, BigInteger[] p2) {
/* 210 */     BigInteger[] result = new BigInteger[p1.length + p2.length - 1];
/* 211 */     Arrays.fill((Object[])result, BigInteger.ZERO);
/* 212 */     for (int i = 0; i < p1.length; i++) {
/* 213 */       for (int j = 0; j < p2.length; j++) {
/* 214 */         result[i + j] = result[i + j].add(p1[i].multiply(p2[j]));
/*     */       }
/*     */     } 
/* 217 */     return result;
/*     */   }
/*     */   
/*     */   static BigInteger[] add(BigInteger[] p1, BigInteger[] p2) {
/* 221 */     BigInteger[] result = new BigInteger[Math.max(p1.length, p2.length)];
/* 222 */     Arrays.fill((Object[])result, BigInteger.ZERO);
/* 223 */     for (int i = 0; i < result.length; i++) {
/* 224 */       if (i < p1.length) {
/* 225 */         result[i] = result[i].add(p1[i]);
/*     */       }
/* 227 */       if (i < p2.length) {
/* 228 */         result[i] = result[i].add(p2[i]);
/*     */       }
/*     */     } 
/* 231 */     return result;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\SubtractionAUX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */