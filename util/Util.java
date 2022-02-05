/*     */ package util;
/*     */ 
/*     */ import backend.config.Config;
/*     */ import backend.resource.ResourceBundle;
/*     */ import backend.structure.CircuitGenerator;
/*     */ import backend.structure.Wire;
/*     */ import java.lang.reflect.Array;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Random;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Util
/*     */ {
/*  20 */   static Random rand = new Random(1L);
/*     */   
/*  22 */   public static BigInteger[] powerTable = new BigInteger[Config.getNumBitsFiniteFieldModulus()];
/*  23 */   public static long[] powerTableLong = new long[60];
/*     */   
/*     */   static {
/*  26 */     powerTable[0] = BigInteger.ONE;
/*  27 */     powerTableLong[0] = 1L;
/*  28 */     for (int i = 1; i < powerTable.length; i++) {
/*  29 */       powerTable[i] = powerTable[i - 1].shiftLeft(1);
/*  30 */       if (i < 60)
/*  31 */         powerTableLong[i] = powerTableLong[i - 1] << 1L; 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static BigInteger[] split(BigInteger x, int numchunks, int chunksize) {
/*  36 */     BigInteger[] chunks = new BigInteger[numchunks];
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
/*  53 */     BigInteger mask = (new BigInteger("2")).pow(chunksize).subtract(
/*  54 */         BigInteger.ONE);
/*  55 */     for (int i = 0; i < numchunks; i++) {
/*  56 */       chunks[i] = x.shiftRight(chunksize * i).and(mask);
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
/* 135 */     return chunks;
/*     */   }
/*     */   
/*     */   public static BigInteger computeMaxValue(int numBits) {
/* 139 */     return ResourceBundle.getInstance().getBigInteger((
/* 140 */         new BigInteger("2")).pow(numBits).subtract(BigInteger.ONE));
/*     */   }
/*     */   
/*     */   public static BigInteger computeBound(int numBits) {
/* 144 */     return ResourceBundle.getInstance().getBigInteger((
/* 145 */         new BigInteger("2")).pow(numBits));
/*     */   }
/*     */ 
/*     */   
/*     */   public static BigInteger[] split(BigInteger x, int chunksize) {
/* 150 */     int numChunks = (int)Math.ceil(x.bitLength() * 1.0D / chunksize);
/* 151 */     BigInteger[] chunks = new BigInteger[numChunks];
/* 152 */     BigInteger mask = (new BigInteger("2")).pow(chunksize).subtract(
/* 153 */         BigInteger.ONE);
/* 154 */     for (int i = 0; i < numChunks; i++) {
/* 155 */       chunks[i] = x.shiftRight(chunksize * i).and(mask);
/*     */     }
/* 157 */     return chunks;
/*     */   }
/*     */   
/*     */   public void dieIf(boolean b) {
/* 161 */     if (b) {
/* 162 */       throw new IllegalArgumentException();
/*     */     }
/*     */   }
/*     */   
/*     */   public void dieIf(boolean b, String st) {
/* 167 */     if (b) {
/* 168 */       throw new IllegalArgumentException(st);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static BigInteger combine(BigInteger[] table, Wire[] blocks, int bitwidth) {
/* 174 */     BigInteger sum = BigInteger.ZERO;
/* 175 */     for (int i = 0; i < blocks.length; i++) {
/* 176 */       if (table[blocks[i].getWireId()] != null)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 181 */         sum = sum.add(table[blocks[i].getWireId()].multiply((new BigInteger(
/* 182 */                 "2")).pow(bitwidth * i))); } 
/*     */     } 
/* 184 */     return sum;
/*     */   }
/*     */   
/*     */   public static BigInteger group(BigInteger[] list, int width) {
/* 188 */     BigInteger x = BigInteger.ZERO;
/* 189 */     for (int i = 0; i < list.length; i++)
/*     */     {
/* 191 */       x = x.add(list[i].multiply(computeBound(width * i)));
/*     */     }
/* 193 */     return x;
/*     */   }
/*     */   
/*     */   public static int[] concat(int[] a1, int[] a2) {
/* 197 */     int[] all = new int[a1.length + a2.length];
/* 198 */     for (int i = 0; i < all.length; i++) {
/* 199 */       all[i] = (i < a1.length) ? a1[i] : a2[i - a1.length];
/*     */     }
/* 201 */     return all;
/*     */   }
/*     */   
/*     */   public static Wire[] concat(Wire[] a1, Wire[] a2) {
/* 205 */     Wire[] all = new Wire[a1.length + a2.length];
/* 206 */     for (int i = 0; i < all.length; i++) {
/* 207 */       all[i] = (i < a1.length) ? a1[i] : a2[i - a1.length];
/*     */     }
/* 209 */     return all;
/*     */   }
/*     */   
/*     */   public static Wire[] concat(Wire w, Wire[] a) {
/* 213 */     Wire[] all = new Wire[1 + a.length];
/* 214 */     for (int i = 0; i < all.length; i++) {
/* 215 */       all[i] = (i < 1) ? w : a[i - 1];
/*     */     }
/* 217 */     return all;
/*     */   }
/*     */   
/*     */   public static int[] concat(int[][] arrays) {
/* 221 */     int sum = 0;
/* 222 */     for (int i = 0; i < arrays.length; i++) {
/* 223 */       sum += (arrays[i]).length;
/*     */     }
/* 225 */     int[] all = new int[sum];
/* 226 */     int idx = 0;
/* 227 */     for (int j = 0; j < arrays.length; j++) {
/* 228 */       for (int k = 0; k < (arrays[j]).length; k++) {
/* 229 */         all[idx++] = arrays[j][k];
/*     */       }
/*     */     } 
/* 232 */     return all;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BigInteger[] randomBigIntegerArray(int num, BigInteger n) {
/* 237 */     BigInteger[] result = new BigInteger[num];
/* 238 */     for (int i = 0; i < num; i++) {
/* 239 */       result[i] = nextRandomBigInteger(n);
/*     */     }
/* 241 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BigInteger nextRandomBigInteger(BigInteger n) {
/* 246 */     BigInteger result = new BigInteger(n.bitLength(), rand);
/* 247 */     while (result.compareTo(n) >= 0) {
/* 248 */       result = new BigInteger(n.bitLength(), rand);
/*     */     }
/* 250 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BigInteger[] randomBigIntegerArray(int num, int numBits) {
/* 255 */     BigInteger[] result = new BigInteger[num];
/* 256 */     for (int i = 0; i < num; i++) {
/* 257 */       result[i] = nextRandomBigInteger(numBits);
/*     */     }
/* 259 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BigInteger nextRandomBigInteger(int numBits) {
/* 264 */     BigInteger result = new BigInteger(numBits, rand);
/* 265 */     return result;
/*     */   }
/*     */   
/*     */   public static String getDesc(String... desc) {
/* 269 */     if (desc.length == 0) {
/* 270 */       return "";
/*     */     }
/* 272 */     return desc[0];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ArrayList<Integer> parseSequenceLists(String s) {
/* 279 */     ArrayList<Integer> list = new ArrayList<>();
/* 280 */     String[] chunks = s.split(","); byte b; int i; String[] arrayOfString1;
/* 281 */     for (i = (arrayOfString1 = chunks).length, b = 0; b < i; ) { String chunk = arrayOfString1[b];
/* 282 */       if (!chunk.equals("")) {
/*     */         
/* 284 */         int lower = Integer.parseInt(chunk.split(":")[0]);
/* 285 */         int upper = Integer.parseInt(chunk.split(":")[1]);
/* 286 */         for (int j = lower; j <= upper; j++)
/* 287 */           list.add(Integer.valueOf(j)); 
/*     */       }  b++; }
/*     */     
/* 290 */     return list;
/*     */   }
/*     */   
/*     */   public static Wire[] reverseBytes(Wire[] inBitWires) {
/* 294 */     Wire[] outs = Arrays.<Wire>copyOf(inBitWires, inBitWires.length);
/* 295 */     int numBytes = inBitWires.length / 8;
/* 296 */     for (int i = 0; i < numBytes / 2; i++) {
/* 297 */       int other = numBytes - i - 1;
/* 298 */       for (int j = 0; j < 8; j++) {
/* 299 */         Wire temp = outs[i * 8 + j];
/* 300 */         outs[i * 8 + j] = outs[other * 8 + j];
/* 301 */         outs[other * 8 + j] = temp;
/*     */       } 
/*     */     } 
/* 304 */     return outs;
/*     */   }
/*     */   
/*     */   public static String arrayToString(int[] a, String separator) {
/* 308 */     StringBuilder s = new StringBuilder();
/* 309 */     for (int i = 0; i < a.length - 1; i++) {
/* 310 */       s.append(String.valueOf(a[i]) + separator);
/*     */     }
/* 312 */     s.append(a[a.length - 1]);
/* 313 */     return s.toString();
/*     */   }
/*     */   
/*     */   public static String arrayToString(Wire[] a, String separator) {
/* 317 */     StringBuilder s = new StringBuilder();
/* 318 */     for (int i = 0; i < a.length - 1; i++) {
/* 319 */       s.append(a[i] + separator);
/*     */     }
/* 321 */     s.append(a[a.length - 1]);
/* 322 */     return s.toString();
/*     */   }
/*     */   
/*     */   public static boolean isBinary(BigInteger v) {
/* 326 */     return !(!v.equals(BigInteger.ZERO) && !v.equals(BigInteger.ONE));
/*     */   }
/*     */   
/*     */   public static String padZeros(String s, int l) {
/* 330 */     return String.format("%" + l + "s", new Object[] { s }).replace(' ', '0');
/*     */   }
/*     */ 
/*     */   
/*     */   public static BigInteger prepConstant(BigInteger constant, BigInteger modulus) {
/* 335 */     boolean sign = (constant.signum() == -1);
/* 336 */     BigInteger r = sign ? constant.negate() : constant;
/* 337 */     if (r.compareTo(modulus) >= 0)
/* 338 */       r = r.mod(modulus); 
/* 339 */     if (sign) {
/* 340 */       r = modulus.subtract(r);
/*     */     }
/* 342 */     return r;
/*     */   }
/*     */   
/*     */   public static BigInteger prepConstant(BigInteger constant, int numBits) {
/* 346 */     boolean sign = (constant.signum() == -1);
/* 347 */     BigInteger r = sign ? constant.negate() : constant;
/* 348 */     BigInteger modulus = computeBound(numBits);
/* 349 */     r = r.mod(modulus);
/* 350 */     if (sign) {
/* 351 */       r = modulus.subtract(r);
/*     */     }
/* 353 */     return r;
/*     */   }
/*     */   
/*     */   public static Wire[] padWireArray(Wire[] a, int length) {
/* 357 */     if (a.length == length)
/* 358 */       return a; 
/* 359 */     if (a.length > length) {
/* 360 */       System.err.println("No padding needed!");
/* 361 */       return a;
/*     */     } 
/* 363 */     Wire[] newArray = new Wire[length];
/* 364 */     System.arraycopy(a, 0, newArray, 0, a.length);
/* 365 */     for (int k = a.length; k < length; k++) {
/* 366 */       newArray[k] = CircuitGenerator.__getActiveCircuitGenerator()
/* 367 */         .__getZeroWire();
/*     */     }
/* 369 */     return newArray;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BigInteger[] padBigIntegerArray(BigInteger[] a, int length) {
/* 374 */     if (a.length == length)
/* 375 */       return a; 
/* 376 */     if (a.length > length) {
/* 377 */       System.err.println("No padding needed!");
/* 378 */       return a;
/*     */     } 
/* 380 */     BigInteger[] newArray = new BigInteger[length];
/* 381 */     System.arraycopy(a, 0, newArray, 0, a.length);
/* 382 */     for (int k = a.length; k < length; k++) {
/* 383 */       newArray[k] = BigInteger.ZERO;
/*     */     }
/* 385 */     return newArray;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BigInteger invertBits(BigInteger constant, int bitLength) {
/* 390 */     BigInteger mask = computeBound(bitLength).subtract(
/* 391 */         BigInteger.ONE);
/* 392 */     return constant.xor(mask);
/*     */   }
/*     */   
/*     */   public static BigInteger min(BigInteger b1, BigInteger b2) {
/* 396 */     if (b1.compareTo(b2) < 0) {
/* 397 */       return b1;
/*     */     }
/* 399 */     return b2;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static int[] getArrayDimensions(Object a) {
/* 405 */     if (a == null) {
/* 406 */       return null;
/*     */     }
/* 408 */     ArrayList<Integer> dimensionList = new ArrayList<>();
/* 409 */     Object current = a;
/* 410 */     int n = 0;
/* 411 */     while (current.getClass().isArray()) {
/* 412 */       n++;
/* 413 */       dimensionList.add(Integer.valueOf(Array.getLength(current)));
/* 414 */       current = Array.get(current, 0);
/*     */     } 
/*     */     
/* 417 */     int[] dims = new int[n];
/* 418 */     for (int i = 0; i < n; i++) {
/* 419 */       dims[i] = ((Integer)dimensionList.get(i)).intValue();
/*     */     }
/* 421 */     return dims;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar\\util\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */