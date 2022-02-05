/*     */ package backend.structure;
/*     */ 
/*     */ import backend.eval.Instruction;
/*     */ import backend.operations.primitive.AddBasicOp;
/*     */ import backend.operations.primitive.ConstMulBasicOp;
/*     */ import backend.operations.primitive.MulBasicOp;
/*     */ import backend.operations.primitive.ORBasicOp;
/*     */ import backend.operations.primitive.XorBasicOp;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BitWire
/*     */   extends Wire
/*     */ {
/*     */   public BitWire(int wireId) {
/*  19 */     super(wireId);
/*     */   }
/*     */   public Wire mul(Wire w, String desc) {
/*     */     Wire output;
/*  23 */     if (w instanceof ConstantWire)
/*  24 */       return mul(((ConstantWire)w).getConstant(), new String[] { desc }); 
/*  25 */     if (w instanceof BitWire && this == w) {
/*  26 */       return w;
/*     */     }
/*     */     
/*  29 */     if (w instanceof BitWire) {
/*  30 */       output = new VariableBitWire(this.generator.__currentWireId++);
/*     */     } else {
/*  32 */       output = new VariableWire(this.generator.__currentWireId++);
/*  33 */     }  MulBasicOp mulBasicOp = new MulBasicOp(this, w, output, new String[] { desc });
/*  34 */     Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)mulBasicOp);
/*  35 */     if (cachedOutputs == null) {
/*  36 */       return output;
/*     */     }
/*     */     
/*  39 */     this.generator.__currentWireId--;
/*  40 */     return cachedOutputs[0];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire mul(BigInteger b, String... desc) {
/*  47 */     if (b.equals(BigInteger.ZERO))
/*  48 */       return this.generator.__zeroWire; 
/*  49 */     if (b.equals(BigInteger.ONE)) {
/*  50 */       return this;
/*     */     }
/*  52 */     Wire out = new LinearCombinationWire(this.generator.__currentWireId++);
/*  53 */     ConstMulBasicOp constMulBasicOp = new ConstMulBasicOp(this, out, b, desc);
/*     */ 
/*     */     
/*  56 */     Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)constMulBasicOp);
/*  57 */     if (cachedOutputs == null) {
/*  58 */       return out;
/*     */     }
/*     */     
/*  61 */     this.generator.__currentWireId--;
/*  62 */     return cachedOutputs[0];
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
/*     */   public Wire invAsBit(String... desc) {
/*  80 */     Wire neg = mul(-1L, desc);
/*  81 */     Wire out = new LinearCombinationBitWire(this.generator.__currentWireId++);
/*  82 */     AddBasicOp addBasicOp = new AddBasicOp(new Wire[] { this.generator.__oneWire, neg }, out, desc);
/*     */     
/*  84 */     Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)addBasicOp);
/*  85 */     if (cachedOutputs == null) {
/*  86 */       return out;
/*     */     }
/*     */     
/*  89 */     this.generator.__currentWireId--;
/*  90 */     return cachedOutputs[0];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire or(Wire w, String... desc) {
/*  96 */     if (w instanceof ConstantWire)
/*  97 */       return w.or(this, desc); 
/*  98 */     if (this == w) {
/*  99 */       return w;
/*     */     }
/*     */     
/* 102 */     if (w instanceof BitWire) {
/* 103 */       Wire out = new VariableBitWire(this.generator.__currentWireId++);
/* 104 */       ORBasicOp oRBasicOp = new ORBasicOp(this, w, out, desc);
/* 105 */       Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)oRBasicOp);
/* 106 */       if (cachedOutputs == null) {
/* 107 */         return out;
/*     */       }
/*     */       
/* 110 */       this.generator.__currentWireId--;
/* 111 */       return cachedOutputs[0];
/*     */     } 
/*     */     
/* 114 */     return super.or(w, desc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire xor(Wire w, String... desc) {
/* 121 */     if (w instanceof ConstantWire)
/* 122 */       return w.xor(this, desc); 
/* 123 */     if (this == w) {
/* 124 */       return this.generator.__zeroWire;
/*     */     }
/*     */     
/* 127 */     if (w instanceof BitWire) {
/* 128 */       Wire out = new VariableBitWire(this.generator.__currentWireId++);
/* 129 */       XorBasicOp xorBasicOp = new XorBasicOp(this, w, out, desc);
/* 130 */       Wire[] cachedOutputs = this.generator.__addToEvaluationQueue((Instruction)xorBasicOp);
/* 131 */       if (cachedOutputs == null) {
/* 132 */         return out;
/*     */       }
/*     */       
/* 135 */       this.generator.__currentWireId--;
/* 136 */       return cachedOutputs[0];
/*     */     } 
/*     */     
/* 139 */     return super.xor(w, desc);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WireArray getBits(Wire w, int bitwidth, String... desc) {
/* 145 */     return (new WireArray(new Wire[] { this })).adjustLength(bitwidth);
/*     */   }
/*     */   
/*     */   public Wire copy() {
/* 149 */     BitWire newWire = new BitWire(this.wireId);
/* 150 */     return newWire;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\structure\BitWire.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */