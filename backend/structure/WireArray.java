/*     */ package backend.structure;
/*     */ 
/*     */ import backend.eval.Instruction;
/*     */ import backend.operations.primitive.AddBasicOp;
/*     */ import backend.operations.primitive.PackBasicOp;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Arrays;
/*     */ import util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WireArray
/*     */ {
/*     */   protected Wire[] array;
/*     */   protected CircuitGenerator generator;
/*     */   
/*     */   public WireArray(int n) {
/*  21 */     this(n, CircuitGenerator.__getActiveCircuitGenerator());
/*     */   }
/*     */   
/*     */   public WireArray(int n, CircuitGenerator generator) {
/*  25 */     this.array = new Wire[n];
/*  26 */     this.generator = generator;
/*     */   }
/*     */   
/*     */   public WireArray(Wire[] wireArray) {
/*  30 */     this(wireArray, CircuitGenerator.__getActiveCircuitGenerator());
/*     */   }
/*     */   
/*     */   public WireArray(Wire[] wireArray, CircuitGenerator generator) {
/*  34 */     this.array = wireArray;
/*  35 */     this.generator = generator;
/*     */   }
/*     */   
/*     */   public Wire get(int i) {
/*  39 */     return this.array[i];
/*     */   }
/*     */   
/*     */   public void set(int i, Wire w) {
/*  43 */     this.array[i] = w;
/*     */   }
/*     */   
/*     */   public int size() {
/*  47 */     return this.array.length;
/*     */   }
/*     */   
/*     */   public Wire[] asArray() {
/*  51 */     return this.array;
/*     */   }
/*     */   
/*     */   public WireArray mulWireArray(WireArray v, int desiredLength, String... desc) {
/*  55 */     Wire[] ws1 = adjustLength(this.array, desiredLength);
/*  56 */     Wire[] ws2 = adjustLength(v.array, desiredLength);
/*  57 */     Wire[] out = new Wire[desiredLength];
/*  58 */     for (int i = 0; i < out.length; i++) {
/*  59 */       out[i] = ws1[i].mul(ws2[i], desc);
/*     */     }
/*  61 */     return new WireArray(out);
/*     */   }
/*     */   
/*     */   public Wire sumAllElements(String... desc) {
/*     */     Wire output;
/*  66 */     boolean allConstant = true;
/*     */     
/*  68 */     BigInteger sum = BigInteger.ZERO; byte b; int i; Wire[] arrayOfWire;
/*  69 */     for (i = (arrayOfWire = this.array).length, b = 0; b < i; ) { Wire w = arrayOfWire[b];
/*  70 */       if (!(w instanceof ConstantWire)) {
/*  71 */         allConstant = false;
/*     */         break;
/*     */       } 
/*  74 */       sum = sum.add(((ConstantWire)w).getConstant());
/*     */       b++; }
/*     */     
/*  77 */     if (allConstant) {
/*  78 */       output = this.generator.__createConstantWire(sum, desc);
/*     */     } else {
/*  80 */       output = new LinearCombinationWire(this.generator.__currentWireId++);
/*  81 */       AddBasicOp addBasicOp = new AddBasicOp(this.array, output, desc);
/*     */       
/*  83 */       Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)addBasicOp);
/*  84 */       if (cachedOutputs == null) {
/*  85 */         return output;
/*     */       }
/*     */       
/*  88 */       this.generator.__currentWireId--;
/*  89 */       return cachedOutputs[0];
/*     */     } 
/*     */     
/*  92 */     return output;
/*     */   }
/*     */ 
/*     */   
/*     */   public WireArray addWireArray(WireArray v, int desiredLength, String... desc) {
/*  97 */     Wire[] ws1 = adjustLength(this.array, desiredLength);
/*  98 */     Wire[] ws2 = adjustLength(v.array, desiredLength);
/*  99 */     Wire[] out = new Wire[desiredLength];
/* 100 */     for (int i = 0; i < out.length; i++) {
/* 101 */       out[i] = ws1[i].add(ws2[i], desc);
/*     */     }
/* 103 */     return new WireArray(out);
/*     */   }
/*     */   
/*     */   public WireArray xorWireArray(WireArray v, int desiredLength, String... desc) {
/* 107 */     Wire[] ws1 = adjustLength(this.array, desiredLength);
/* 108 */     Wire[] ws2 = adjustLength(v.array, desiredLength);
/* 109 */     Wire[] out = new Wire[desiredLength];
/* 110 */     for (int i = 0; i < out.length; i++) {
/* 111 */       out[i] = ws1[i].xor(ws2[i], desc);
/*     */     }
/* 113 */     return new WireArray(out);
/*     */   }
/*     */   
/*     */   public WireArray xorWireArray(WireArray v, String... desc) {
/* 117 */     if (size() != v.size()) {
/* 118 */       throw new IllegalArgumentException();
/*     */     }
/* 120 */     Wire[] ws1 = this.array;
/* 121 */     Wire[] ws2 = v.array;
/*     */     
/* 123 */     Wire[] out = new Wire[size()];
/* 124 */     for (int i = 0; i < out.length; i++) {
/* 125 */       out[i] = ws1[i].xor(ws2[i], desc);
/*     */     }
/* 127 */     return new WireArray(out);
/*     */   }
/*     */   
/*     */   public WireArray andWireArray(WireArray v, int desiredLength, String... desc) {
/* 131 */     Wire[] ws1 = adjustLength(this.array, desiredLength);
/* 132 */     Wire[] ws2 = adjustLength(v.array, desiredLength);
/* 133 */     Wire[] out = new Wire[desiredLength];
/* 134 */     for (int i = 0; i < out.length; i++) {
/* 135 */       out[i] = ws1[i].mul(ws2[i], desc);
/*     */     }
/* 137 */     return new WireArray(out);
/*     */   }
/*     */   
/*     */   public WireArray orWireArray(WireArray v, int desiredLength, String... desc) {
/* 141 */     Wire[] ws1 = adjustLength(this.array, desiredLength);
/* 142 */     Wire[] ws2 = adjustLength(v.array, desiredLength);
/* 143 */     Wire[] out = new Wire[desiredLength];
/* 144 */     for (int i = 0; i < out.length; i++) {
/* 145 */       out[i] = ws1[i].or(ws2[i], desc);
/*     */     }
/* 147 */     return new WireArray(out);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WireArray invAsBits(int desiredBitWidth, String... desc) {
/* 153 */     Wire[] out = new Wire[desiredBitWidth];
/* 154 */     for (int i = 0; i < desiredBitWidth; i++) {
/* 155 */       if (i < this.array.length) {
/* 156 */         out[i] = this.array[i].invAsBit(desc);
/*     */       } else {
/*     */         
/* 159 */         out[i] = this.generator.__oneWire;
/*     */       } 
/*     */     } 
/* 162 */     return new WireArray(out);
/*     */   }
/*     */ 
/*     */   
/*     */   private Wire[] adjustLength(Wire[] ws, int desiredLength) {
/* 167 */     if (ws.length == desiredLength) {
/* 168 */       return ws;
/*     */     }
/* 170 */     Wire[] newWs = new Wire[desiredLength];
/* 171 */     System.arraycopy(ws, 0, newWs, 0, Math.min(ws.length, desiredLength));
/* 172 */     if (ws.length < desiredLength) {
/* 173 */       for (int i = ws.length; i < desiredLength; i++) {
/* 174 */         newWs[i] = this.generator.__zeroWire;
/*     */       }
/*     */     }
/* 177 */     return newWs;
/*     */   }
/*     */   
/*     */   public WireArray adjustLength(int desiredLength) {
/* 181 */     if (this.array.length == desiredLength) {
/* 182 */       return this;
/*     */     }
/* 184 */     Wire[] newWs = new Wire[desiredLength];
/* 185 */     System.arraycopy(this.array, 0, newWs, 0, Math.min(this.array.length, desiredLength));
/* 186 */     if (this.array.length < desiredLength) {
/* 187 */       for (int i = this.array.length; i < desiredLength; i++) {
/* 188 */         newWs[i] = this.generator.__zeroWire;
/*     */       }
/*     */     }
/* 191 */     return new WireArray(newWs);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire packAsBits(int n, String... desc) {
/* 197 */     return packAsBits(0, n, desc);
/*     */   }
/*     */   
/*     */   public Wire packAsBits(String... desc) {
/* 201 */     return packAsBits(this.array.length, desc);
/*     */   }
/*     */   
/*     */   protected BigInteger checkIfConstantBits(String... desc) {
/* 205 */     boolean allConstant = true;
/* 206 */     BigInteger sum = BigInteger.ZERO;
/* 207 */     for (int i = 0; i < this.array.length; i++) {
/* 208 */       Wire w = this.array[i];
/* 209 */       if (w instanceof ConstantWire) {
/* 210 */         ConstantWire cw = (ConstantWire)w;
/* 211 */         BigInteger v = cw.constant;
/* 212 */         if (v.equals(BigInteger.ONE)) {
/* 213 */           sum = sum.add(v.shiftLeft(i));
/*     */         }
/* 215 */         else if (!v.equals(BigInteger.ZERO)) {
/* 216 */           System.err.println("Warning, one of the bit wires is constant but not binary : " + Util.getDesc(desc));
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 221 */         allConstant = false;
/*     */       } 
/*     */     } 
/* 224 */     if (allConstant) {
/* 225 */       return sum;
/*     */     }
/* 227 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire packAsBits(int from, int to, String... desc) {
/* 232 */     if (from > to || to > this.array.length) {
/* 233 */       throw new IllegalArgumentException("Invalid bounds: from > to");
/*     */     }
/* 235 */     Wire[] bits = Arrays.<Wire>copyOfRange(this.array, from, to);
/* 236 */     boolean allConstant = true;
/* 237 */     BigInteger sum = BigInteger.ZERO;
/* 238 */     for (int i = 0; i < bits.length; i++) {
/* 239 */       Wire w = bits[i];
/* 240 */       if (w instanceof ConstantWire) {
/* 241 */         ConstantWire cw = (ConstantWire)w;
/* 242 */         BigInteger v = cw.constant;
/* 243 */         if (v.equals(BigInteger.ONE)) {
/* 244 */           sum = sum.add(v.shiftLeft(i));
/*     */         }
/* 246 */         else if (!v.equals(BigInteger.ZERO)) {
/* 247 */           throw new RuntimeException("Trying to pack non-binary constant bits : " + Util.getDesc(desc));
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 252 */         allConstant = false;
/*     */       } 
/*     */     } 
/* 255 */     if (!allConstant) {
/* 256 */       Wire out = new LinearCombinationWire(this.generator.__currentWireId++);
/* 257 */       out.setBits(new WireArray(bits));
/* 258 */       PackBasicOp packBasicOp = new PackBasicOp(bits, out, desc);
/*     */ 
/*     */       
/* 261 */       Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)packBasicOp);
/*     */       
/* 263 */       if (cachedOutputs == null)
/*     */       {
/* 265 */         return out;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 270 */       this.generator.__currentWireId--;
/* 271 */       return cachedOutputs[0];
/*     */     } 
/*     */     
/* 274 */     return this.generator.__createConstantWire(sum, desc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WireArray rotateLeft(int numBits, int s, String... desc) {
/* 281 */     Wire[] bits = adjustLength(this.array, numBits);
/* 282 */     Wire[] rotatedBits = new Wire[numBits];
/* 283 */     for (int i = 0; i < numBits; i++) {
/* 284 */       if (i < s) {
/* 285 */         rotatedBits[i] = bits[i + numBits - s];
/*     */       } else {
/* 287 */         rotatedBits[i] = bits[i - s];
/*     */       } 
/* 289 */     }  return new WireArray(rotatedBits);
/*     */   }
/*     */   
/*     */   public WireArray rotateRight(int numBits, int s, String... desc) {
/* 293 */     Wire[] bits = adjustLength(this.array, numBits);
/* 294 */     Wire[] rotatedBits = new Wire[numBits];
/* 295 */     for (int i = 0; i < numBits; i++) {
/* 296 */       if (i >= numBits - s) {
/* 297 */         rotatedBits[i] = bits[i - numBits - s];
/*     */       } else {
/* 299 */         rotatedBits[i] = bits[i + s];
/*     */       } 
/* 301 */     }  return new WireArray(rotatedBits);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WireArray shiftLeft(int numBits, int s, String... desc) {
/* 307 */     Wire[] bits = adjustLength(this.array, numBits);
/* 308 */     Wire[] shiftedBits = new Wire[numBits];
/* 309 */     for (int i = 0; i < numBits; i++) {
/* 310 */       if (i < s) {
/* 311 */         shiftedBits[i] = this.generator.__zeroWire;
/*     */       } else {
/* 313 */         shiftedBits[i] = bits[i - s];
/*     */       } 
/* 315 */     }  return new WireArray(shiftedBits);
/*     */   }
/*     */   
/*     */   public WireArray shiftRight(int numBits, int s, String... desc) {
/* 319 */     Wire[] bits = adjustLength(this.array, numBits);
/* 320 */     Wire[] shiftedBits = new Wire[numBits];
/* 321 */     for (int i = 0; i < numBits; i++) {
/* 322 */       if (i >= numBits - s) {
/* 323 */         shiftedBits[i] = this.generator.__zeroWire;
/*     */       } else {
/* 325 */         shiftedBits[i] = bits[i + s];
/*     */       } 
/* 327 */     }  return new WireArray(shiftedBits);
/*     */   }
/*     */   
/*     */   public Wire[] packBitsIntoWords(int wordBitwidth, String... desc) {
/* 331 */     int numWords = (int)Math.ceil(this.array.length * 1.0D / wordBitwidth);
/* 332 */     Wire[] padded = adjustLength(this.array, wordBitwidth * numWords);
/* 333 */     Wire[] result = new Wire[numWords];
/* 334 */     for (int i = 0; i < numWords; i++) {
/* 335 */       result[i] = (new WireArray(Arrays.<Wire>copyOfRange(padded, i * wordBitwidth, (i + 1) * wordBitwidth))).packAsBits(new String[0]);
/*     */     }
/* 337 */     return result;
/*     */   }
/*     */   
/*     */   public Wire[] packWordsIntoLargerWords(int wordBitwidth, int numWordsPerLargerWord, String... desc) {
/* 341 */     int numLargerWords = (int)Math.ceil(this.array.length * 1.0D / numWordsPerLargerWord);
/* 342 */     Wire[] result = new Wire[numLargerWords];
/* 343 */     Arrays.fill((Object[])result, this.generator.__zeroWire);
/* 344 */     for (int i = 0; i < this.array.length; i++) {
/* 345 */       int subIndex = i % numWordsPerLargerWord;
/* 346 */       result[i / numWordsPerLargerWord] = result[i / numWordsPerLargerWord].add(this.array[i]
/* 347 */           .mul((new BigInteger("2")).pow(subIndex * wordBitwidth), new String[0]), new String[0]);
/*     */     } 
/* 349 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public WireArray getBits(int bitwidth, String... desc) {
/* 354 */     Wire[] bits = new Wire[bitwidth * this.array.length];
/* 355 */     int idx = 0;
/* 356 */     for (int i = 0; i < this.array.length; i++) {
/* 357 */       Wire[] tmp = this.array[i].getBitWires(bitwidth, desc).asArray();
/* 358 */       for (int j = 0; j < bitwidth; j++) {
/* 359 */         bits[idx++] = tmp[j];
/*     */       }
/*     */     } 
/* 362 */     return new WireArray(bits);
/*     */   }
/*     */   
/*     */   public BigInteger computeTightUpperBoundOfBitWires(int bitwidth) {
/* 366 */     BigInteger m = BigInteger.ZERO;
/* 367 */     for (int i = 0; i < Math.min(size(), bitwidth); i++) {
/* 368 */       Wire w = get(i);
/* 369 */       if (!(w instanceof ConstantWire) || 
/* 370 */         !((ConstantWire)w).getConstant().equals(
/* 371 */           BigInteger.ZERO))
/*     */       {
/*     */ 
/*     */         
/* 375 */         m = m.add(BigInteger.ONE.shiftLeft(i)); } 
/*     */     } 
/* 377 */     return m;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\structure\WireArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */