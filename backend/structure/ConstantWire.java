/*     */ package backend.structure;
/*     */ 
/*     */ import backend.config.Config;
/*     */ import backend.eval.Instruction;
/*     */ import backend.operations.primitive.ConstMulBasicOp;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConstantWire
/*     */   extends Wire
/*     */ {
/*     */   protected BigInteger constant;
/*     */   
/*     */   public ConstantWire(int wireId, BigInteger value) {
/*  17 */     super(wireId);
/*  18 */     this.constant = value.mod(Config.getFiniteFieldModulus());
/*     */   }
/*     */   
/*     */   public BigInteger getConstant() {
/*  22 */     return this.constant;
/*     */   }
/*     */   
/*     */   public boolean isBinary() {
/*  26 */     return !(!this.constant.equals(BigInteger.ONE) && 
/*  27 */       !this.constant.equals(BigInteger.ZERO));
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire mul(Wire w, String... desc) {
/*  32 */     if (w instanceof ConstantWire)
/*     */     {
/*  34 */       return this.generator.__createConstantWire(
/*  35 */           this.constant.multiply(((ConstantWire)w).constant), desc);
/*     */     }
/*  37 */     return w.mul(this.constant, desc);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire mul(BigInteger b, String... desc) {
/*     */     BigInteger newConstant;
/*  44 */     boolean sign = (b.signum() == -1);
/*     */     
/*  46 */     if (this.constant.equals(BigInteger.ONE)) {
/*  47 */       if (b.signum() >= 0 && b.compareTo(Config.getFiniteFieldModulus()) < 0) {
/*  48 */         newConstant = b;
/*     */       } else {
/*  50 */         newConstant = b.mod(Config.getFiniteFieldModulus());
/*     */       } 
/*     */     } else {
/*     */       
/*  54 */       newConstant = this.constant.multiply(b).mod(Config.getFiniteFieldModulus());
/*     */     } 
/*     */     
/*  57 */     Wire out = this.generator.__knownConstantWires.get(newConstant);
/*  58 */     if (out == null) {
/*     */       
/*  60 */       if (!sign) {
/*  61 */         out = new ConstantWire(this.generator.__currentWireId++, newConstant);
/*     */       } else {
/*  63 */         out = new ConstantWire(this.generator.__currentWireId++, newConstant.subtract(Config.getFiniteFieldModulus()));
/*     */       } 
/*     */ 
/*     */       
/*  67 */       ConstMulBasicOp constMulBasicOp = new ConstMulBasicOp(this, out, 
/*  68 */           b, desc);
/*     */       
/*  70 */       Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)constMulBasicOp);
/*  71 */       if (cachedOutputs == null) {
/*  72 */         this.generator.__knownConstantWires.put(newConstant, out);
/*  73 */         return out;
/*     */       } 
/*     */       
/*  76 */       this.generator.__currentWireId--;
/*  77 */       this.generator.__knownConstantWires.put(newConstant, cachedOutputs[0]);
/*  78 */       return cachedOutputs[0];
/*     */     } 
/*     */     
/*  81 */     return out;
/*     */   }
/*     */   
/*     */   public Wire checkNonZero(Wire w, String... desc) {
/*  85 */     if (this.constant.equals(BigInteger.ZERO)) {
/*  86 */       return this.generator.__zeroWire;
/*     */     }
/*  88 */     return this.generator.__oneWire;
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire invAsBit(String... desc) {
/*  93 */     if (!isBinary()) {
/*  94 */       throw new RuntimeException(
/*  95 */           "Trying to invert a non-binary constant!");
/*     */     }
/*  97 */     if (this.constant.equals(BigInteger.ZERO)) {
/*  98 */       return this.generator.__oneWire;
/*     */     }
/* 100 */     return this.generator.__zeroWire;
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire or(Wire w, String... desc) {
/* 105 */     if (w instanceof ConstantWire) {
/* 106 */       ConstantWire cw = (ConstantWire)w;
/* 107 */       if (isBinary() && cw.isBinary()) {
/* 108 */         if (this.constant.equals(BigInteger.ZERO) && 
/* 109 */           cw.getConstant().equals(BigInteger.ZERO)) {
/* 110 */           return this.generator.__zeroWire;
/*     */         }
/* 112 */         return this.generator.__oneWire;
/*     */       } 
/*     */       
/* 115 */       throw new RuntimeException(
/* 116 */           "Trying to OR two non-binary constants");
/*     */     } 
/*     */     
/* 119 */     if (this.constant.equals(BigInteger.ONE)) {
/* 120 */       return this.generator.__oneWire;
/*     */     }
/* 122 */     return w;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire xor(Wire w, String... desc) {
/* 128 */     if (w instanceof ConstantWire) {
/* 129 */       ConstantWire cw = (ConstantWire)w;
/* 130 */       if (isBinary() && cw.isBinary()) {
/* 131 */         if (this.constant.equals(cw.getConstant())) {
/* 132 */           return this.generator.__zeroWire;
/*     */         }
/* 134 */         return this.generator.__oneWire;
/*     */       } 
/*     */       
/* 137 */       throw new RuntimeException(
/* 138 */           "Trying to XOR two non-binary constants");
/*     */     } 
/*     */     
/* 141 */     if (this.constant.equals(BigInteger.ONE)) {
/* 142 */       return w.invAsBit(desc);
/*     */     }
/* 144 */     return w;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WireArray getBitWires(int bitwidth, String... desc) {
/* 150 */     if (this.constant.bitLength() > bitwidth) {
/* 151 */       throw new RuntimeException("Trying to split a constant of " + 
/* 152 */           this.constant.bitLength() + " bits into " + bitwidth + "bits");
/*     */     }
/* 154 */     ConstantWire[] arrayOfConstantWire = new ConstantWire[bitwidth];
/* 155 */     for (int i = 0; i < bitwidth; i++) {
/* 156 */       arrayOfConstantWire[i] = this.constant.testBit(i) ? (ConstantWire)this.generator.__oneWire : (ConstantWire)this.generator.__zeroWire;
/*     */     }
/* 158 */     return new WireArray((Wire[])arrayOfConstantWire);
/*     */   }
/*     */ 
/*     */   
/*     */   public void restrictBitLength(int bitwidth, String... desc) {
/* 163 */     getBitWires(bitwidth, desc);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void pack(String... desc) {}
/*     */   
/*     */   public Wire copy() {
/* 170 */     ConstantWire newWire = new ConstantWire(this.wireId, this.constant);
/* 171 */     return newWire;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\structure\ConstantWire.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */