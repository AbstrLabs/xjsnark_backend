/*     */ package backend.auxTypes;
/*     */ 
/*     */ import backend.eval.CircuitEvaluator;
/*     */ import backend.structure.CircuitGenerator;
/*     */ import backend.structure.ConstantWire;
/*     */ import backend.structure.Wire;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Stack;
/*     */ import util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Bit
/*     */   implements IAuxType, ConditionalScopeImpactedType
/*     */ {
/*     */   protected Wire wire;
/*     */   protected BigInteger constant;
/*     */   protected CircuitGenerator generator;
/*  22 */   protected int conditionalScopeId = ConditionalScopeTracker.getCurrentScopeId();
/*  23 */   protected int scope = ConditionalScopeTracker.getCurrentScopeId();
/*     */   protected Stack<HashMap<Integer, Bit>> possibleValStack;
/*     */   protected Stack<Bit> prevValStack;
/*     */   protected boolean stateChanged;
/*     */   
/*     */   public Bit(Wire wire) {
/*  29 */     this.wire = wire;
/*  30 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  31 */     if (wire instanceof ConstantWire) {
/*  32 */       this.constant = ((ConstantWire)wire).getConstant();
/*     */     }
/*     */   }
/*     */   
/*     */   public Bit() {
/*  37 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*     */   }
/*     */   
/*     */   public Bit(BigInteger value) {
/*  41 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  42 */     if (!Util.isBinary(value)) {
/*  43 */       throw new IllegalArgumentException();
/*     */     }
/*  45 */     this.wire = this.generator.__createConstantWire(value, new String[0]);
/*  46 */     this.constant = value;
/*     */   }
/*     */ 
/*     */   
/*     */   public Bit(boolean b) {
/*  51 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  52 */     this.wire = this.generator.__createConstantWire((b ? 1L : 0L), new String[0]);
/*  53 */     this.constant = b ? BigInteger.ONE : BigInteger.ZERO;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void init() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void assign(Bit target) {
/*  67 */     if (this.generator.__getPhase() == 0) {
/*  68 */       this.constant = target.constant;
/*  69 */       if (this.scope != ConditionalScopeTracker.getCurrentScopeId()) {
/*  70 */         ConditionalScopeTracker.register(this, this.scope);
/*  71 */         if (this.possibleValStack == null) {
/*  72 */           this.possibleValStack = new Stack<>();
/*     */         }
/*  74 */         if (this.prevValStack == null) {
/*  75 */           this.prevValStack = new Stack<>();
/*     */         }
/*  77 */         int current = ConditionalScopeTracker.getCurrentScopeId();
/*     */         
/*  79 */         for (int i = 0; i < current - this.scope; i++) {
/*  80 */           Bit c = copy();
/*  81 */           this.prevValStack.push(c);
/*  82 */           this.possibleValStack.push(new HashMap<>());
/*     */         } 
/*  84 */         this.stateChanged = true;
/*  85 */         this.scope = ConditionalScopeTracker.getCurrentScopeId();
/*     */       
/*     */       }
/*     */     
/*     */     }
/*  90 */     else if (this.scope == ConditionalScopeTracker.getCurrentScopeId()) {
/*     */       
/*  92 */       this.wire = target.wire;
/*  93 */       this.constant = target.constant;
/*     */     } else {
/*  95 */       this.stateChanged = true;
/*  96 */       ConditionalScopeTracker.register(this, this.scope);
/*     */       
/*  98 */       if (this.possibleValStack == null) {
/*  99 */         this.possibleValStack = new Stack<>();
/*     */       }
/* 101 */       if (this.prevValStack == null) {
/* 102 */         this.prevValStack = new Stack<>();
/*     */       }
/* 104 */       int current = ConditionalScopeTracker.getCurrentScopeId();
/*     */       
/* 106 */       int size = this.prevValStack.size();
/* 107 */       while (size < current) {
/* 108 */         this.prevValStack.push(copy());
/* 109 */         this.possibleValStack.push(new HashMap<>());
/* 110 */         size++;
/*     */       } 
/*     */       
/* 113 */       this.wire = target.wire;
/* 114 */       this.scope = ConditionalScopeTracker.getCurrentScopeId();
/* 115 */       this.constant = target.constant;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void pop(int id) {
/* 122 */     if (!this.stateChanged) {
/*     */       return;
/*     */     }
/*     */     
/* 126 */     Bit copy = copy();
/*     */ 
/*     */     
/* 129 */     ((HashMap<Integer, Bit>)this.possibleValStack.peek()).put(Integer.valueOf(id), copy);
/* 130 */     this.scope--;
/* 131 */     Bit prev = this.prevValStack.peek();
/* 132 */     this.wire = prev.wire;
/*     */ 
/*     */     
/* 135 */     this.constant = prev.constant;
/* 136 */     this.stateChanged = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void popMain() {
/* 141 */     if (this.generator.__getPhase() == 0) {
/* 142 */       this.constant = null;
/* 143 */       this.stateChanged = true;
/*     */     }
/*     */     else {
/*     */       
/* 147 */       int tmp = this.scope;
/* 148 */       if (ConditionalScopeTracker.getCurrentScopeId() > tmp) {
/* 149 */         this.stateChanged = true;
/*     */       }
/* 151 */       ConditionalScopeTracker.ConditionalStatementData condData = 
/* 152 */         ConditionalScopeTracker.getCurrentConditionalStmtData();
/* 153 */       int numberOfValues = condData.getBitList().size();
/* 154 */       ArrayList<Bit> conditionList = condData.getBitList();
/* 155 */       Bit[] candidateList = new Bit[numberOfValues];
/* 156 */       HashMap<Integer, Bit> possibleVals = this.possibleValStack.pop();
/* 157 */       for (Integer idx : possibleVals.keySet())
/*     */       {
/* 159 */         candidateList[idx.intValue()] = possibleVals.get(idx);
/*     */       }
/* 161 */       for (int i = 0; i < numberOfValues; i++) {
/* 162 */         if (candidateList[i] == null) {
/* 163 */           candidateList[i] = copy();
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 170 */       Bit initial = candidateList[numberOfValues - 1];
/* 171 */       int startingIndex = -1;
/* 172 */       for (int j = numberOfValues - 2; j >= 0; j--) {
/* 173 */         if ((candidateList[j]).wire != initial.wire) {
/* 174 */           startingIndex = j;
/*     */ 
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/*     */       
/* 181 */       if (startingIndex == -1) {
/*     */         
/* 183 */         this.wire = initial.wire;
/*     */ 
/*     */         
/* 186 */         this.constant = initial.constant;
/*     */       } else {
/* 188 */         Bit current = initial;
/* 189 */         this.wire = initial.wire;
/* 190 */         for (int k = startingIndex; k >= 0; k--) {
/*     */           
/* 192 */           current = candidateList[k];
/* 193 */           Bit selectionBit = conditionList.get(k);
/* 194 */           this.wire = this.wire.add(selectionBit.wire.mul(current.wire
/* 195 */                 .sub(this.wire, new String[0]), new String[0]), new String[0]);
/*     */ 
/*     */ 
/*     */           
/* 199 */           this.constant = null;
/*     */         } 
/*     */       } 
/* 202 */       this.prevValStack.pop();
/* 203 */       init();
/*     */     } 
/*     */   }
/*     */   
/*     */   public Bit copy() {
/* 208 */     if (this.generator.__getPhase() == 0) {
/* 209 */       Bit bit = new Bit();
/* 210 */       bit.wire = this.wire;
/* 211 */       bit.constant = this.constant;
/* 212 */       bit.generator = this.generator;
/* 213 */       return bit;
/*     */     } 
/* 215 */     Bit v = new Bit();
/* 216 */     v.wire = this.wire;
/* 217 */     v.generator = this.generator;
/* 218 */     v.constant = this.constant;
/* 219 */     return v;
/*     */   }
/*     */ 
/*     */   
/*     */   public UnsignedInteger toUnsignedInteger() {
/* 224 */     return new UnsignedInteger(1, new PackedValue(this.wire, 1));
/*     */   }
/*     */ 
/*     */   
/*     */   public UnsignedInteger add(Bit o) {
/* 229 */     return toUnsignedInteger().add(o.toUnsignedInteger());
/*     */   }
/*     */   
/*     */   public UnsignedInteger add(boolean o) {
/* 233 */     return toUnsignedInteger().add((new Bit(o)).toUnsignedInteger());
/*     */   }
/*     */   
/*     */   public UnsignedInteger add(BigInteger o) {
/* 237 */     return toUnsignedInteger().add((new Bit(o)).toUnsignedInteger());
/*     */   }
/*     */ 
/*     */   
/*     */   public Bit mul(Bit o) {
/* 242 */     if (isConstant() && o.isConstant())
/* 243 */       return new Bit(getConstant().and(o.getConstant()).equals(
/* 244 */             BigInteger.ONE)); 
/* 245 */     if (isConstant() && getConstant().equals(BigInteger.ZERO))
/* 246 */       return new Bit(false); 
/* 247 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ZERO)) {
/* 248 */       return new Bit(false);
/*     */     }
/*     */     
/* 251 */     if (this.generator.__getPhase() == 0) {
/* 252 */       return new Bit(new Wire(-1));
/*     */     }
/* 254 */     return new Bit(this.wire.mul(o.wire, new String[0]));
/*     */   }
/*     */ 
/*     */   
/*     */   public Bit xor(Bit o) {
/* 259 */     if (isConstant() && o.isConstant()) {
/* 260 */       return new Bit(getConstant().xor(o.getConstant()).equals(
/* 261 */             BigInteger.ONE));
/*     */     }
/* 263 */     if (this.generator.__getPhase() == 0) {
/* 264 */       return new Bit(new Wire(-1));
/*     */     }
/* 266 */     return new Bit(this.wire.xor(o.wire, new String[0]));
/*     */   }
/*     */   
/*     */   public Bit or(Bit o) {
/* 270 */     if (isConstant() && o.isConstant())
/* 271 */       return new Bit(getConstant().or(o.getConstant()).equals(
/* 272 */             BigInteger.ONE)); 
/* 273 */     if (isConstant() && getConstant().equals(BigInteger.ONE))
/* 274 */       return new Bit(true); 
/* 275 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ONE)) {
/* 276 */       return new Bit(true);
/*     */     }
/* 278 */     if (this.generator.__getPhase() == 0) {
/* 279 */       return new Bit(new Wire(-1));
/*     */     }
/* 281 */     return new Bit(this.wire.or(o.wire, new String[0]));
/*     */   }
/*     */   
/*     */   public Bit and(Bit o) {
/* 285 */     if (isConstant() && o.isConstant())
/* 286 */       return new Bit(getConstant().and(o.getConstant()).equals(
/* 287 */             BigInteger.ONE)); 
/* 288 */     if (isConstant() && getConstant().equals(BigInteger.ZERO))
/* 289 */       return new Bit(false); 
/* 290 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ZERO)) {
/* 291 */       return new Bit(false);
/*     */     }
/* 293 */     if (this.generator.__getPhase() == 0) {
/* 294 */       return new Bit(new Wire(-1));
/*     */     }
/* 296 */     return new Bit(this.wire.mul(o.wire, new String[0]));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Bit inv() {
/* 307 */     if (isConstant()) {
/* 308 */       return new Bit(
/* 309 */           getConstant().equals(BigInteger.ZERO) ? BigInteger.ONE : 
/* 310 */           BigInteger.ZERO);
/*     */     }
/* 312 */     if (this.generator.__getPhase() == 0) {
/* 313 */       return new Bit(new Wire(-1));
/*     */     }
/* 315 */     return new Bit(this.wire.invAsBit(new String[0]));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConstant() {
/* 320 */     return (this.constant != null);
/*     */   }
/*     */   
/*     */   public BigInteger getConstant() {
/* 324 */     if (!isConstant()) {
/* 325 */       return null;
/*     */     }
/* 327 */     return getConstantValue().booleanValue() ? BigInteger.ONE : BigInteger.ZERO;
/*     */   }
/*     */   
/*     */   public Boolean getConstantValue() {
/* 331 */     if (this.constant == null)
/* 332 */       return null; 
/* 333 */     return Boolean.valueOf(this.constant.equals(BigInteger.ONE));
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
/*     */   public Wire getWire() {
/* 345 */     return this.wire;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCurrentBitWidth() {
/* 350 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire[] toWires() {
/* 355 */     return new Wire[] { this.wire };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void mapValue(BigInteger value, CircuitEvaluator evaluator) {
/* 361 */     evaluator.setWireValue(this.wire, value);
/*     */   }
/*     */ 
/*     */   
/*     */   public BigInteger getValueFromEvaluator(CircuitEvaluator evaluator) {
/* 366 */     return evaluator.getWireValue(this.wire);
/*     */   }
/*     */   
/*     */   public static Bit createInput(CircuitGenerator generator, String... desc) {
/* 370 */     Wire w = generator.__createInputWire(desc);
/* 371 */     Bit o = new Bit(w);
/* 372 */     generator.__getInputAux().add(o.copy());
/* 373 */     return o;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Bit[] createInputArray(CircuitGenerator generator, int size, String... desc) {
/* 378 */     Bit[] out = new Bit[size];
/* 379 */     for (int i = 0; i < size; i++) {
/* 380 */       out[i] = createInput(generator, desc);
/*     */     }
/*     */     
/* 383 */     return out;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Bit[] createZeroArray(CircuitGenerator generator, int size, String... desc) {
/* 388 */     Bit[] out = new Bit[size];
/* 389 */     for (int i = 0; i < size; i++) {
/* 390 */       out[i] = new Bit(false);
/*     */     }
/* 392 */     return out;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object createZeroArray(CircuitGenerator generator, int[] dims, String... desc) {
/* 398 */     if (dims.length == 1)
/* 399 */       return createZeroArray(generator, dims[0], desc); 
/* 400 */     if (dims.length == 2) {
/* 401 */       Bit[][] out = new Bit[dims[0]][];
/* 402 */       for (int i = 0; i < dims[0]; i++) {
/* 403 */         out[i] = createZeroArray(generator, dims[1], desc);
/*     */       }
/* 405 */       return out;
/* 406 */     }  if (dims.length == 3) {
/* 407 */       Bit[][][] out = new Bit[dims[0]][dims[1]][];
/* 408 */       for (int i = 0; i < dims[0]; i++) {
/* 409 */         for (int j = 0; j < dims[1]; j++) {
/* 410 */           out[i][j] = createZeroArray(generator, dims[2], desc);
/*     */         }
/*     */       } 
/* 413 */       return out;
/*     */     } 
/*     */     
/* 416 */     throw new IllegalArgumentException(
/* 417 */         "Initialization of higher dim arrays not supported at this point");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Bit createWitness(CircuitGenerator generator, String... desc) {
/* 423 */     Wire w = generator.__createInputWire(desc);
/* 424 */     Bit o = new Bit(w);
/* 425 */     generator.__getProverAux().add(o.copy());
/* 426 */     return o;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Bit[] createWitnessArray(CircuitGenerator generator, int size, String... desc) {
/* 431 */     Bit[] out = new Bit[size];
/* 432 */     for (int i = 0; i < size; i++) {
/* 433 */       out[i] = createWitness(generator, desc);
/*     */     }
/*     */     
/* 436 */     return out;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Bit createVerifiedWitness(CircuitGenerator generator, String... desc) {
/* 441 */     Wire w = generator.__createInputWire(desc);
/* 442 */     Bit o = new Bit(w);
/* 443 */     generator.__getProverVerifiedAux().add(o.copy());
/* 444 */     return o;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Bit[] createVerifiedWitnessArray(CircuitGenerator generator, int size, String... desc) {
/* 449 */     Bit[] out = new Bit[size];
/* 450 */     for (int i = 0; i < size; i++) {
/* 451 */       out[i] = createVerifiedWitness(generator, desc);
/*     */     }
/*     */     
/* 454 */     return out;
/*     */   }
/*     */ 
/*     */   
/*     */   public void makeOutput(String... desc) {
/* 459 */     this.generator.__makeOutput(this.wire, desc);
/*     */   }
/*     */ 
/*     */   
/*     */   public static void makeOutput(CircuitGenerator generator, Bit x, String... desc) {
/* 464 */     x.makeOutput(new String[0]);
/*     */   } public static void makeOutput(CircuitGenerator generator, Bit[] a, String... desc) {
/*     */     byte b;
/*     */     int i;
/*     */     Bit[] arrayOfBit;
/* 469 */     for (i = (arrayOfBit = a).length, b = 0; b < i; ) { Bit x = arrayOfBit[b];
/* 470 */       x.makeOutput(new String[0]);
/*     */       b++; }
/*     */   
/*     */   }
/*     */   
/*     */   public void mapRandomValue(CircuitEvaluator evaluator) {
/* 476 */     BigInteger rnd = Util.nextRandomBigInteger(1);
/* 477 */     evaluator.setWireValue(this.wire, rnd);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRequiredBitWidth() {
/* 482 */     return 1;
/*     */   }
/*     */   
/*     */   public UnsignedInteger mul(UnsignedInteger k) {
/* 486 */     return k.mul(this);
/*     */   }
/*     */   
/*     */   public UnsignedInteger add(UnsignedInteger k) {
/* 490 */     return k.add(this);
/*     */   }
/*     */   
/*     */   public FieldElement mul(FieldElement k) {
/* 494 */     return k.mul(this);
/*     */   }
/*     */   
/*     */   public GroupElement mul(GroupElement k) {
/* 498 */     return k.mul(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public FieldElement add(FieldElement k) {
/* 503 */     return k.add(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public Bit[] getBitElements() {
/* 508 */     return new Bit[] { this };
/*     */   }
/*     */ 
/*     */   
/*     */   public void forceEqual(IAuxType o) {
/* 513 */     if (!(o instanceof Bit)) {
/* 514 */       throw new IllegalArgumentException("not an instance of bit type");
/*     */     }
/* 516 */     Bit other = (Bit)o;
/* 517 */     if (getConstant() != null && other.getConstant() != null && 
/* 518 */       !getConstant().equals(other.getConstant())) {
/* 519 */       throw new RuntimeException("Constraint fails on constant bits");
/*     */     }
/*     */ 
/*     */     
/* 523 */     if (this.generator.__getPhase() != 0)
/*     */     {
/*     */       
/* 526 */       this.generator.__addEqualityAssertion(this.wire, ((Bit)o).wire, new String[0]);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public Bit isEqualTo(IAuxType o) {
/* 532 */     if (!(o instanceof Bit)) {
/* 533 */       throw new IllegalArgumentException("not an instance of bit");
/*     */     }
/* 535 */     Bit b = (Bit)o;
/* 536 */     if (b.isConstant() && o.isConstant()) {
/* 537 */       return new Bit(b.constant.equals(this.constant));
/*     */     }
/*     */     
/* 540 */     if (this.generator.__getPhase() == 0) {
/* 541 */       return new Bit(new Wire(-1));
/*     */     }
/* 543 */     return new Bit(this.wire.xor(b.wire, new String[0]).invAsBit(new String[0]));
/*     */   }
/*     */ 
/*     */   
/*     */   public Bit isNotEqualTo(IAuxType o) {
/* 548 */     return isEqualTo(o).inv();
/*     */   }
/*     */ 
/*     */   
/*     */   public static Class<?> __getClassRef() {
/* 553 */     return Bit.class;
/*     */   }
/*     */   
/*     */   public VariableState getState() {
/* 557 */     return new VariableState();
/*     */   }
/*     */   
/*     */   public PackedValue getPackedValue() {
/* 561 */     return new PackedValue(this.wire, 1);
/*     */   }
/*     */   
/*     */   public void verifyRange() {
/* 565 */     this.generator.__addBinaryAssertion(this.wire, new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Bit instantiateFrom(boolean v) {
/* 570 */     return new Bit(v);
/*     */   }
/*     */   
/*     */   public static Bit[] instantiateFrom(boolean[] v) {
/* 574 */     Bit[] a = new Bit[v.length];
/* 575 */     for (int i = 0; i < a.length; i++)
/* 576 */       a[i] = instantiateFrom(v[i]); 
/* 577 */     return a;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object createInputArray(CircuitGenerator generator, int[] dims, String... desc) {
/* 583 */     if (dims.length == 1)
/* 584 */       return createInputArray(generator, dims[0], desc); 
/* 585 */     if (dims.length == 2) {
/* 586 */       Bit[][] out = new Bit[dims[0]][];
/* 587 */       for (int i = 0; i < dims[0]; i++) {
/* 588 */         out[i] = createInputArray(generator, dims[1], desc);
/*     */       }
/* 590 */       return out;
/* 591 */     }  if (dims.length == 3) {
/* 592 */       Bit[][][] out = new Bit[dims[0]][dims[1]][];
/* 593 */       for (int i = 0; i < dims[0]; i++) {
/* 594 */         for (int j = 0; j < dims[1]; j++) {
/* 595 */           out[i][j] = createInputArray(generator, dims[2], 
/* 596 */               desc);
/*     */         }
/*     */       } 
/* 599 */       return out;
/*     */     } 
/* 601 */     throw new IllegalArgumentException(
/* 602 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object createWitnessArray(CircuitGenerator generator, int[] dims, String... desc) {
/* 610 */     if (dims.length == 1)
/* 611 */       return createWitnessArray(generator, dims[0], desc); 
/* 612 */     if (dims.length == 2) {
/* 613 */       Bit[][] out = new Bit[dims[0]][];
/* 614 */       for (int i = 0; i < dims[0]; i++) {
/* 615 */         out[i] = createWitnessArray(generator, dims[1], desc);
/*     */       }
/* 617 */       return out;
/* 618 */     }  if (dims.length == 3) {
/* 619 */       Bit[][][] out = new Bit[dims[0]][dims[1]][];
/* 620 */       for (int i = 0; i < dims[0]; i++) {
/* 621 */         for (int j = 0; j < dims[1]; j++) {
/* 622 */           out[i][j] = createWitnessArray(generator, dims[2], 
/* 623 */               desc);
/*     */         }
/*     */       } 
/* 626 */       return out;
/*     */     } 
/* 628 */     throw new IllegalArgumentException(
/* 629 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object createVerifiedWitnessArray(CircuitGenerator generator, int[] dims, String... desc) {
/* 637 */     if (dims.length == 1)
/* 638 */       return createVerifiedWitnessArray(generator, dims[0], desc); 
/* 639 */     if (dims.length == 2) {
/* 640 */       Bit[][] out = new Bit[dims[0]][];
/* 641 */       for (int i = 0; i < dims[0]; i++) {
/* 642 */         out[i] = createVerifiedWitnessArray(generator, dims[1], desc);
/*     */       }
/* 644 */       return out;
/* 645 */     }  if (dims.length == 3) {
/* 646 */       Bit[][][] out = new Bit[dims[0]][dims[1]][];
/* 647 */       for (int i = 0; i < dims[0]; i++) {
/* 648 */         for (int j = 0; j < dims[1]; j++) {
/* 649 */           out[i][j] = createVerifiedWitnessArray(generator, dims[2], 
/* 650 */               desc);
/*     */         }
/*     */       } 
/* 653 */       return out;
/*     */     } 
/* 655 */     throw new IllegalArgumentException(
/* 656 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void makeOutput(CircuitGenerator generator, Object a, String... desc) {
/* 664 */     if (a instanceof Bit[]) {
/* 665 */       Bit[] array = (Bit[])a;
/* 666 */       for (int i = 0; i < array.length; i++) {
/* 667 */         makeOutput(generator, array[i], desc);
/*     */       }
/* 669 */     } else if (a instanceof Bit[][]) {
/* 670 */       Bit[][] array = (Bit[][])a;
/* 671 */       for (int i = 0; i < array.length; i++) {
/* 672 */         makeOutput(generator, array[i], desc);
/*     */       }
/* 674 */     } else if (a instanceof Bit[][][]) {
/* 675 */       Bit[][][] array = (Bit[][][])a;
/* 676 */       for (int i = 0; i < array.length; i++) {
/* 677 */         makeOutput(generator, array[i], desc);
/*     */       }
/*     */     } else {
/* 680 */       throw new IllegalArgumentException("Declaring higher dimensional arrays as outputs not supported at this point. Only 3 dimensions are supported");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\Bit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */