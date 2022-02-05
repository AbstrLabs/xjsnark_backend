/*     */ package examples.gadgets;
/*     */ 
/*     */ import backend.auxTypes.PackedValue;
/*     */ import backend.auxTypes.UnsignedInteger;
/*     */ import backend.eval.CircuitEvaluator;
/*     */ import backend.eval.Instruction;
/*     */ import backend.operations.Gadget;
/*     */ import backend.structure.Wire;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Arrays;
/*     */ import util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LongIntegerModConstantGadget
/*     */   extends Gadget
/*     */ {
/*     */   private PackedValue a;
/*     */   private PackedValue b;
/*     */   private PackedValue r;
/*     */   private PackedValue q;
/*     */   private boolean restrictRange;
/*     */   
/*     */   public LongIntegerModConstantGadget(PackedValue a, PackedValue b, boolean restrictRange, String... desc) {
/*  31 */     super(desc);
/*  32 */     this.a = a;
/*  33 */     this.b = b;
/*  34 */     this.restrictRange = restrictRange;
/*  35 */     buildCircuit();
/*     */   }
/*     */ 
/*     */   
/*     */   private void buildCircuit() {
/*  40 */     int aBitwidth = this.a.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength();
/*     */     
/*  42 */     int bBitwidth = this.b.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength();
/*  43 */     if (aBitwidth < bBitwidth) {
/*  44 */       throw new IllegalArgumentException("a's bitwidth < b's bitwidth -- This gadget is not needed.");
/*     */     }
/*     */ 
/*     */     
/*  48 */     int rBitwidth = bBitwidth;
/*     */     
/*  50 */     int qBitwidth = aBitwidth - bBitwidth + 1;
/*     */     
/*  52 */     int rChunkLength = (int)Math.ceil(rBitwidth * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  53 */     int qChunkLength = (int)Math.ceil(qBitwidth * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  60 */     Wire[] rWires = this.generator.__createProverWitnessWireArray(rChunkLength, new String[0]);
/*  61 */     Wire[] qWires = this.generator.__createProverWitnessWireArray(qChunkLength, new String[0]);
/*     */     
/*  63 */     int[] rChunkBitwidths = new int[rChunkLength];
/*  64 */     int[] qChunkBitwidths = new int[qChunkLength];
/*     */     
/*  66 */     Arrays.fill(rChunkBitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  67 */     Arrays.fill(qChunkBitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */     
/*  69 */     if (rBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/*  70 */       rChunkBitwidths[rChunkLength - 1] = rBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK;
/*     */     }
/*  72 */     if (qBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/*  73 */       qChunkBitwidths[qChunkLength - 1] = qBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK;
/*     */     }
/*     */     
/*  76 */     this.r = new PackedValue(rWires, rChunkBitwidths);
/*  77 */     this.q = new PackedValue(qWires, qChunkBitwidths);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */         {
/*     */           public void evaluate(CircuitEvaluator evaluator) {
/*  87 */             BigInteger aValue = evaluator.getWireValue(LongIntegerModConstantGadget.this.a, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  88 */             BigInteger bValue = evaluator.getWireValue(LongIntegerModConstantGadget.this.b, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*  94 */             BigInteger rValue = aValue.mod(bValue);
/*  95 */             BigInteger qValue = aValue.divide(bValue);
/*     */             
/*  97 */             evaluator.setWireValue(LongIntegerModConstantGadget.this.r.getArray(), Util.split(rValue, UnsignedInteger.BITWIDTH_PER_CHUNK));
/*  98 */             evaluator.setWireValue(LongIntegerModConstantGadget.this.q.getArray(), Util.split(qValue, UnsignedInteger.BITWIDTH_PER_CHUNK));
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 103 */     this.r.forceBitwidth();
/* 104 */     this.q.forceBitwidth();
/*     */     
/* 106 */     PackedValue res = this.q.mul(this.b).add(this.r);
/* 107 */     res.forceEquality2(this.a);
/*     */     
/* 109 */     if (this.restrictRange) {
/* 110 */       this.generator.__addOneAssertion(this.r.isLessThan(this.b, rBitwidth).getWire(), new String[0]);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire[] getOutputWires() {
/* 117 */     return this.r.getArray();
/*     */   }
/*     */   
/*     */   public PackedValue getRemainder() {
/* 121 */     return this.r;
/*     */   }
/*     */   
/*     */   public PackedValue getQuotient() {
/* 125 */     return this.q;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\LongIntegerModConstantGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */