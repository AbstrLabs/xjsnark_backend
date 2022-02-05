/*     */ package backend.structure;
/*     */ 
/*     */ import backend.eval.Instruction;
/*     */ import backend.operations.primitive.ConstMulBasicOp;
/*     */ import backend.operations.primitive.MulBasicOp;
/*     */ import backend.operations.primitive.NonZeroCheckBasicOp;
/*     */ import backend.operations.primitive.ORBasicOp;
/*     */ import backend.operations.primitive.PackBasicOp;
/*     */ import backend.operations.primitive.SplitBasicOp;
/*     */ import backend.operations.primitive.XorBasicOp;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Wire
/*     */ {
/*  19 */   protected int wireId = -1;
/*     */   
/*     */   protected CircuitGenerator generator;
/*     */   protected Instruction srcInstruction;
/*     */   
/*     */   public Wire(int wireId) {
/*  25 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*     */ 
/*     */ 
/*     */     
/*  29 */     if (wireId < -1) {
/*  30 */       throw new IllegalArgumentException("wire id cannot be negative < -1");
/*     */     }
/*  32 */     this.wireId = wireId;
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire(int wireId, CircuitGenerator generator) {
/*  37 */     this.generator = generator;
/*  38 */     if (wireId < -1) {
/*  39 */       throw new IllegalArgumentException("wire id cannot be negative < -1");
/*     */     }
/*  41 */     this.wireId = wireId;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected Wire(WireArray bits) {
/*  47 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  48 */     setBits(bits);
/*     */   }
/*     */   
/*     */   public String toString() {
/*  52 */     return (new StringBuilder(String.valueOf(this.wireId))).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getWireId() {
/*  57 */     return this.wireId;
/*     */   }
/*     */   
/*     */   WireArray getBitWires() {
/*  61 */     return null;
/*     */   }
/*     */   
/*     */   void setBits(WireArray bits) {
/*  65 */     System.out.println(
/*  66 */         "Warning --  you are trying to set bits for either a constant or a bit wire. -- Action Ignored");
/*  67 */     throw new NullPointerException();
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire mul(BigInteger b, String... desc) {
/*  72 */     packIfNeeded(desc);
/*     */     
/*  74 */     if (b.equals(BigInteger.ONE))
/*  75 */       return this; 
/*  76 */     if (b.equals(BigInteger.ZERO))
/*  77 */       return this.generator.__zeroWire; 
/*  78 */     Wire out = new LinearCombinationWire(this.generator.__currentWireId++);
/*  79 */     ConstMulBasicOp constMulBasicOp = new ConstMulBasicOp(this, out, b, desc);
/*     */ 
/*     */     
/*  82 */     Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)constMulBasicOp);
/*  83 */     if (cachedOutputs == null) {
/*  84 */       return out;
/*     */     }
/*     */     
/*  87 */     this.generator.__currentWireId--;
/*  88 */     return cachedOutputs[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire mul(long l, String... desc) {
/*  93 */     return mul(new BigInteger((new StringBuilder(String.valueOf(l))).toString()), desc);
/*     */   }
/*     */   
/*     */   public Wire mul(long base, int exp, String... desc) {
/*  97 */     BigInteger b = new BigInteger((new StringBuilder(String.valueOf(base))).toString());
/*  98 */     b = b.pow(exp);
/*  99 */     return mul(b, desc);
/*     */   }
/*     */   
/*     */   public Wire mul(Wire w, String... desc) {
/* 103 */     if (w instanceof ConstantWire) {
/* 104 */       return mul(((ConstantWire)w).getConstant(), desc);
/*     */     }
/* 106 */     packIfNeeded(desc);
/* 107 */     w.packIfNeeded(desc);
/* 108 */     Wire output = new VariableWire(this.generator.__currentWireId++);
/* 109 */     MulBasicOp mulBasicOp = new MulBasicOp(this, w, output, desc);
/* 110 */     Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)mulBasicOp);
/* 111 */     if (cachedOutputs == null) {
/* 112 */       return output;
/*     */     }
/*     */     
/* 115 */     this.generator.__currentWireId--;
/* 116 */     return cachedOutputs[0];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire add(Wire w, String... desc) {
/* 122 */     packIfNeeded(desc);
/* 123 */     w.packIfNeeded(desc);
/* 124 */     if (w instanceof ConstantWire && ((ConstantWire)w).getConstant().equals(BigInteger.ZERO))
/* 125 */       return this; 
/* 126 */     if (this instanceof ConstantWire && ((ConstantWire)this).getConstant().equals(BigInteger.ZERO)) {
/* 127 */       return w;
/*     */     }
/* 129 */     return (new WireArray(new Wire[] { this, w })).sumAllElements(desc);
/*     */   }
/*     */   
/*     */   public Wire add(long v, String... desc) {
/* 133 */     return add(this.generator.__createConstantWire(v, desc), desc);
/*     */   }
/*     */   
/*     */   public Wire add(BigInteger b, String... desc) {
/* 137 */     return add(this.generator.__createConstantWire(b, desc), desc);
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire sub(Wire w, String... desc) {
/* 142 */     w.packIfNeeded(desc);
/* 143 */     Wire neg = w.mul(-1L, desc);
/* 144 */     return add(neg, desc);
/*     */   }
/*     */   
/*     */   public Wire sub(long v, String... desc) {
/* 148 */     return sub(this.generator.__createConstantWire(v, desc), desc);
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire sub(BigInteger b, String... desc) {
/* 153 */     return sub(this.generator.__createConstantWire(b, desc), desc);
/*     */   }
/*     */   
/*     */   public Wire checkNonZero(String... desc) {
/* 157 */     packIfNeeded(desc);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 162 */     Wire out1 = new Wire(this.generator.__currentWireId++);
/* 163 */     Wire out2 = new VariableBitWire(this.generator.__currentWireId++);
/* 164 */     NonZeroCheckBasicOp nonZeroCheckBasicOp = new NonZeroCheckBasicOp(this, out1, out2, desc);
/*     */     
/* 166 */     Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)nonZeroCheckBasicOp);
/* 167 */     if (cachedOutputs == null) {
/* 168 */       return out2;
/*     */     }
/*     */     
/* 171 */     this.generator.__currentWireId -= 2;
/* 172 */     return cachedOutputs[1];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire invAsBit(String... desc) {
/* 178 */     packIfNeeded(desc);
/* 179 */     Wire w1 = mul(-1L, desc);
/* 180 */     Wire out = this.generator.__oneWire.add(w1, desc);
/* 181 */     return out;
/*     */   }
/*     */   
/*     */   public Wire or(Wire w, String... desc) {
/* 185 */     if (w instanceof ConstantWire)
/* 186 */       return w.or(this, desc); 
/* 187 */     if (this == w) {
/* 188 */       return w;
/*     */     }
/* 190 */     packIfNeeded(desc);
/*     */     
/* 192 */     Wire out = new VariableWire(this.generator.__currentWireId++);
/* 193 */     ORBasicOp oRBasicOp = new ORBasicOp(this, w, out, desc);
/* 194 */     Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)oRBasicOp);
/* 195 */     if (cachedOutputs == null) {
/* 196 */       return out;
/*     */     }
/*     */     
/* 199 */     this.generator.__currentWireId--;
/* 200 */     return cachedOutputs[0];
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
/*     */   public Wire xor(Wire w, String... desc) {
/* 213 */     if (w instanceof ConstantWire)
/* 214 */       return w.xor(this, desc); 
/* 215 */     if (this == w) {
/* 216 */       return this.generator.__zeroWire;
/*     */     }
/* 218 */     packIfNeeded(desc);
/*     */     
/* 220 */     Wire out = new VariableWire(this.generator.__currentWireId++);
/* 221 */     XorBasicOp xorBasicOp = new XorBasicOp(this, w, out, desc);
/* 222 */     Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)xorBasicOp);
/* 223 */     if (cachedOutputs == null) {
/* 224 */       return out;
/*     */     }
/*     */     
/* 227 */     this.generator.__currentWireId--;
/* 228 */     return cachedOutputs[0];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire and(Wire w, String... desc) {
/* 234 */     return mul(w, desc);
/*     */   }
/*     */ 
/*     */   
/*     */   public WireArray getBitWires(int bitwidth, String... desc) {
/* 239 */     WireArray bitWires = getBitWires();
/* 240 */     if (bitWires == null) {
/* 241 */       bitWires = forceSplit(bitwidth, desc);
/* 242 */       setBits(bitWires);
/* 243 */       return bitWires;
/*     */     } 
/* 245 */     return bitWires.adjustLength(bitwidth);
/*     */   }
/*     */ 
/*     */   
/*     */   protected WireArray forceSplit(int bitwidth, String... desc) {
/* 250 */     VariableBitWire[] arrayOfVariableBitWire = new VariableBitWire[bitwidth];
/* 251 */     for (int i = 0; i < bitwidth; i++) {
/* 252 */       arrayOfVariableBitWire[i] = new VariableBitWire(this.generator.__currentWireId++);
/*     */     }
/* 254 */     SplitBasicOp splitBasicOp = new SplitBasicOp(this, (Wire[])arrayOfVariableBitWire, desc);
/*     */ 
/*     */ 
/*     */     
/* 258 */     Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)splitBasicOp);
/*     */     
/* 260 */     if (cachedOutputs == null) {
/* 261 */       WireArray bitWires = new WireArray((Wire[])arrayOfVariableBitWire);
/* 262 */       return bitWires;
/*     */     } 
/*     */     
/* 265 */     this.generator.__currentWireId -= bitwidth;
/* 266 */     return (new WireArray(cachedOutputs)).adjustLength(bitwidth);
/*     */   }
/*     */ 
/*     */   
/*     */   public void restrictBitLength(int bitWidth, String... desc) {
/* 271 */     WireArray bitWires = getBitWires();
/* 272 */     if (bitWires == null) {
/* 273 */       getBitWires(bitWidth, desc);
/*     */     }
/* 275 */     else if (bitWires.size() > bitWidth) {
/* 276 */       bitWires = forceSplit(bitWidth, desc);
/* 277 */       setBits(bitWires);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire xorBitwise(Wire w, int numBits, String... desc) {
/* 285 */     WireArray bits1 = getBitWires(numBits, desc);
/* 286 */     WireArray bits2 = w.getBitWires(numBits, desc);
/* 287 */     WireArray result = bits1.xorWireArray(bits2, numBits, desc);
/* 288 */     BigInteger v = result.checkIfConstantBits(desc);
/* 289 */     if (v == null) {
/* 290 */       return new LinearCombinationWire(result);
/*     */     }
/* 292 */     return this.generator.__createConstantWire(v, new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire xorBitwise(long v, int numBits, String... desc) {
/* 297 */     return xorBitwise(this.generator.__createConstantWire(v, desc), numBits, desc);
/*     */   }
/*     */   
/*     */   public Wire xorBitwise(BigInteger b, int numBits, String... desc) {
/* 301 */     return xorBitwise(this.generator.__createConstantWire(b, desc), numBits, desc);
/*     */   }
/*     */   
/*     */   public Wire andBitwise(Wire w, int numBits, String... desc) {
/* 305 */     WireArray bits1 = getBitWires(numBits, desc);
/* 306 */     WireArray bits2 = w.getBitWires(numBits, desc);
/* 307 */     WireArray result = bits1.andWireArray(bits2, numBits, desc);
/* 308 */     BigInteger v = result.checkIfConstantBits(desc);
/* 309 */     if (v == null) {
/* 310 */       return new LinearCombinationWire(result);
/*     */     }
/* 312 */     return this.generator.__createConstantWire(v, new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire andBitwise(long v, int numBits, String... desc) {
/* 317 */     return andBitwise(this.generator.__createConstantWire(v, desc), numBits, desc);
/*     */   }
/*     */   
/*     */   public Wire andBitwise(BigInteger b, int numBits, String... desc) {
/* 321 */     return andBitwise(this.generator.__createConstantWire(b, desc), numBits, desc);
/*     */   }
/*     */   
/*     */   public Wire orBitwise(Wire w, int numBits, String... desc) {
/* 325 */     WireArray bits1 = getBitWires(numBits, desc);
/* 326 */     WireArray bits2 = w.getBitWires(numBits, desc);
/* 327 */     WireArray result = bits1.orWireArray(bits2, numBits, desc);
/* 328 */     BigInteger v = result.checkIfConstantBits(desc);
/* 329 */     if (v == null) {
/* 330 */       return new LinearCombinationWire(result);
/*     */     }
/* 332 */     return this.generator.__createConstantWire(v, new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire orBitwise(long v, int numBits, String... desc) {
/* 337 */     return orBitwise(this.generator.__createConstantWire(v, desc), numBits, desc);
/*     */   }
/*     */   
/*     */   public Wire orBitwise(BigInteger b, int numBits, String... desc) {
/* 341 */     return orBitwise(this.generator.__createConstantWire(b, desc), numBits, desc);
/*     */   }
/*     */   
/*     */   public Wire isEqualTo(Wire w, String... desc) {
/* 345 */     packIfNeeded(desc);
/* 346 */     w.packIfNeeded(desc);
/* 347 */     Wire s = sub(w, desc);
/* 348 */     return s.checkNonZero(desc).invAsBit(desc);
/*     */   }
/*     */   
/*     */   public Wire isEqualTo(BigInteger b, String... desc) {
/* 352 */     return isEqualTo(this.generator.__createConstantWire(b, desc), new String[0]);
/*     */   }
/*     */   
/*     */   public Wire isEqualTo(long v, String... desc) {
/* 356 */     return isEqualTo(this.generator.__createConstantWire(v, desc), new String[0]);
/*     */   }
/*     */   
/*     */   public Wire isLessThanOrEqual(Wire w, int bitwidth, String... desc) {
/* 360 */     packIfNeeded(desc);
/* 361 */     w.packIfNeeded(desc);
/* 362 */     BigInteger p = (new BigInteger("2")).pow(bitwidth);
/* 363 */     Wire pWire = this.generator.__createConstantWire(p, desc);
/* 364 */     Wire sum = pWire.add(w, desc).sub(this, desc);
/* 365 */     WireArray bitWires = sum.getBitWires(bitwidth + 1, desc);
/* 366 */     return bitWires.get(bitwidth);
/*     */   }
/*     */   
/*     */   public Wire isLessThanOrEqual(long v, int bitwidth, String... desc) {
/* 370 */     return isLessThanOrEqual(this.generator.__createConstantWire(v, desc), bitwidth, desc);
/*     */   }
/*     */   
/*     */   public Wire isLessThanOrEqual(BigInteger b, int bitwidth, String... desc) {
/* 374 */     return isLessThanOrEqual(this.generator.__createConstantWire(b, desc), bitwidth, desc);
/*     */   }
/*     */   
/*     */   public Wire isLessThan(Wire w, int bitwidth, String... desc) {
/* 378 */     packIfNeeded(desc);
/* 379 */     w.packIfNeeded(desc);
/* 380 */     BigInteger p = (new BigInteger("2")).pow(bitwidth);
/* 381 */     Wire pWire = this.generator.__createConstantWire(p, desc);
/* 382 */     Wire sum = pWire.add(this, desc).sub(w, desc);
/* 383 */     WireArray bitWires = sum.getBitWires(bitwidth + 1, desc);
/* 384 */     return bitWires.get(bitwidth).invAsBit(desc);
/*     */   }
/*     */   
/*     */   public Wire isLessThan(long v, int bitwidth, String... desc) {
/* 388 */     return isLessThan(this.generator.__createConstantWire(v, desc), bitwidth, desc);
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire isLessThan(BigInteger b, int bitwidth, String... desc) {
/* 393 */     return isLessThan(this.generator.__createConstantWire(b, desc), bitwidth, desc);
/*     */   }
/*     */   
/*     */   public Wire isGreaterThanOrEqual(Wire w, int bitwidth, String... desc) {
/* 397 */     packIfNeeded(desc);
/* 398 */     w.packIfNeeded(desc);
/* 399 */     BigInteger p = (new BigInteger("2")).pow(bitwidth);
/* 400 */     Wire pWire = this.generator.__createConstantWire(p, desc);
/* 401 */     Wire sum = pWire.add(this, desc).sub(w, desc);
/*     */     
/* 403 */     WireArray bitWires = sum.getBitWires(bitwidth + 1, desc);
/* 404 */     return bitWires.get(bitwidth);
/*     */   }
/*     */   
/*     */   public Wire isGreaterThanOrEqual(long v, int bitwidth, String... desc) {
/* 408 */     return isGreaterThanOrEqual(this.generator.__createConstantWire(v, desc), bitwidth, desc);
/*     */   }
/*     */   
/*     */   public Wire isGreaterThanOrEqual(BigInteger b, int bitwidth, String... desc) {
/* 412 */     return isGreaterThanOrEqual(this.generator.__createConstantWire(b, desc), bitwidth, desc);
/*     */   }
/*     */   
/*     */   public Wire isGreaterThan(Wire w, int bitwidth, String... desc) {
/* 416 */     packIfNeeded(desc);
/* 417 */     w.packIfNeeded(desc);
/* 418 */     BigInteger p = (new BigInteger("2")).pow(bitwidth);
/* 419 */     Wire pWire = this.generator.__createConstantWire(p, desc);
/* 420 */     Wire sum = pWire.add(w, desc).sub(this, desc);
/* 421 */     WireArray bitWires = sum.getBitWires(bitwidth + 1, desc);
/* 422 */     return bitWires.get(bitwidth).invAsBit(desc);
/*     */   }
/*     */   
/*     */   public Wire isGreaterThan(long v, int bitwidth, String... desc) {
/* 426 */     return isGreaterThan(this.generator.__createConstantWire(v, desc), bitwidth, desc);
/*     */   }
/*     */   
/*     */   public Wire isGreaterThan(BigInteger b, int bitwidth, String... desc) {
/* 430 */     return isGreaterThan(this.generator.__createConstantWire(b, desc), bitwidth, desc);
/*     */   }
/*     */   
/*     */   public Wire rotateLeft(int numBits, int s, String... desc) {
/* 434 */     WireArray bits = getBitWires(numBits, desc);
/* 435 */     Wire[] rotatedBits = new Wire[numBits];
/* 436 */     for (int i = 0; i < numBits; i++) {
/* 437 */       if (i < s) {
/* 438 */         rotatedBits[i] = bits.get(i + numBits - s);
/*     */       } else {
/* 440 */         rotatedBits[i] = bits.get(i - s);
/*     */       } 
/* 442 */     }  WireArray result = new WireArray(rotatedBits);
/* 443 */     BigInteger v = result.checkIfConstantBits(desc);
/* 444 */     if (v == null) {
/* 445 */       return new LinearCombinationWire(result);
/*     */     }
/* 447 */     return this.generator.__createConstantWire(v, new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire rotateRight(int numBits, int s, String... desc) {
/* 452 */     WireArray bits = getBitWires(numBits, desc);
/* 453 */     Wire[] rotatedBits = new Wire[numBits];
/* 454 */     for (int i = 0; i < numBits; i++) {
/* 455 */       if (i >= numBits - s) {
/* 456 */         rotatedBits[i] = bits.get(i - numBits - s);
/*     */       } else {
/* 458 */         rotatedBits[i] = bits.get(i + s);
/*     */       } 
/* 460 */     }  WireArray result = new WireArray(rotatedBits);
/* 461 */     BigInteger v = result.checkIfConstantBits(desc);
/* 462 */     if (v == null) {
/* 463 */       return new LinearCombinationWire(result);
/*     */     }
/* 465 */     return this.generator.__createConstantWire(v, new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire shiftLeft(int numBits, int s, String... desc) {
/* 470 */     WireArray bits = getBitWires(numBits, desc);
/* 471 */     Wire[] shiftedBits = new Wire[numBits];
/* 472 */     for (int i = 0; i < numBits; i++) {
/* 473 */       if (i < s) {
/* 474 */         shiftedBits[i] = this.generator.__zeroWire;
/*     */       } else {
/* 476 */         shiftedBits[i] = bits.get(i - s);
/*     */       } 
/* 478 */     }  WireArray result = new WireArray(shiftedBits);
/* 479 */     BigInteger v = result.checkIfConstantBits(desc);
/* 480 */     if (v == null) {
/* 481 */       return new LinearCombinationWire(result);
/*     */     }
/* 483 */     return this.generator.__createConstantWire(v, new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire shiftRight(int numBits, int s, String... desc) {
/* 488 */     WireArray bits = getBitWires(numBits, desc);
/* 489 */     Wire[] shiftedBits = new Wire[numBits];
/* 490 */     for (int i = 0; i < numBits; i++) {
/* 491 */       if (i >= numBits - s) {
/* 492 */         shiftedBits[i] = this.generator.__zeroWire;
/*     */       } else {
/* 494 */         shiftedBits[i] = bits.get(i + s);
/*     */       } 
/* 496 */     }  WireArray result = new WireArray(shiftedBits);
/* 497 */     BigInteger v = result.checkIfConstantBits(desc);
/* 498 */     if (v == null) {
/* 499 */       return new LinearCombinationWire(result);
/*     */     }
/* 501 */     return this.generator.__createConstantWire(v, new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire invBits(int bitwidth, String... desc) {
/* 506 */     Wire[] bits = getBitWires(bitwidth, desc).asArray();
/* 507 */     Wire[] resultBits = new Wire[bits.length];
/* 508 */     for (int i = 0; i < resultBits.length; i++) {
/* 509 */       resultBits[i] = bits[i].invAsBit(desc);
/*     */     }
/* 511 */     WireArray result = new WireArray(resultBits);
/* 512 */     BigInteger v = result.checkIfConstantBits(desc);
/* 513 */     if (v == null) {
/* 514 */       return new LinearCombinationWire(result);
/*     */     }
/* 516 */     return this.generator.__createConstantWire(v, new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire trimBits(int currentNumOfBits, int desiredNumofBits, String... desc) {
/* 521 */     WireArray bitWires = getBitWires(currentNumOfBits, desc);
/* 522 */     WireArray result = bitWires.adjustLength(desiredNumofBits);
/* 523 */     BigInteger v = result.checkIfConstantBits(desc);
/* 524 */     if (v == null) {
/* 525 */       return new LinearCombinationWire(result);
/*     */     }
/* 527 */     return this.generator.__createConstantWire(v, new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void packIfNeeded(String... desc) {
/* 532 */     if (this.wireId == -1) {
/* 533 */       pack(new String[0]);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void pack(String... desc) {
/* 538 */     if (this.wireId == -1 && this.generator.__getPhase() > 0) {
/* 539 */       WireArray bits = getBitWires();
/* 540 */       if (bits == null && 
/* 541 */         this.generator.__getPhase() > 0) {
/* 542 */         throw new RuntimeException("A Pack operation is tried on a wire that has no bits.");
/*     */       }
/*     */       
/* 545 */       this.wireId = this.generator.__currentWireId++;
/*     */ 
/*     */ 
/*     */       
/* 549 */       PackBasicOp packBasicOp = new PackBasicOp(bits.array, this, desc);
/* 550 */       Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)packBasicOp);
/*     */       
/* 552 */       if (cachedOutputs != null) {
/*     */ 
/*     */         
/* 555 */         this.generator.__currentWireId--;
/* 556 */         this.wireId = cachedOutputs[0].getWireId();
/*     */       } 
/*     */     } else {
/* 559 */       this.wireId = this.generator.__currentWireId++;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 565 */     return this.wireId;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 570 */     if (this == obj) {
/* 571 */       return true;
/*     */     }
/* 573 */     if (!(obj instanceof Wire)) {
/* 574 */       return false;
/*     */     }
/*     */     
/* 577 */     Wire w = (Wire)obj;
/* 578 */     return (w.wireId == this.wireId && w.generator == this.generator);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Instruction getSrcInstruction() {
/* 584 */     return this.srcInstruction;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSrcInstruction(Instruction srcInstruction) {
/* 589 */     this.srcInstruction = srcInstruction;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWireId(int wireId) {
/* 594 */     this.wireId = wireId;
/*     */   }
/*     */   
/*     */   public Wire copy() {
/* 598 */     return new Wire(this.wireId);
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\structure\Wire.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */