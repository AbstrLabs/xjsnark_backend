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
/*     */ public class LongIntegerModNotStrictModulusGadget
/*     */   extends Gadget
/*     */ {
/*     */   private PackedValue a;
/*     */   private PackedValue b;
/*     */   private PackedValue r;
/*     */   private PackedValue q;
/*     */   private boolean restrictRange;
/*     */   
/*     */   public LongIntegerModNotStrictModulusGadget(PackedValue a, PackedValue b, boolean restrictRange, String... desc) {
/*  28 */     super(desc);
/*  29 */     this.a = a;
/*  30 */     this.b = b;
/*  31 */     this.restrictRange = restrictRange;
/*  32 */     buildCircuit();
/*     */   }
/*     */ 
/*     */   
/*     */   private void buildCircuit() {
/*  37 */     int aBitwidth = this.a.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength();
/*  38 */     int bBitwidth = this.b.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength();
/*     */     
/*  40 */     int rBitwidth = Math.min(aBitwidth, bBitwidth);
/*  41 */     int qBitwidth = aBitwidth;
/*     */     
/*  43 */     int rChunkLength = (int)Math.ceil(rBitwidth * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  44 */     int qChunkLength = (int)Math.ceil(qBitwidth * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */     
/*  46 */     Wire[] rWires = this.generator.__createProverWitnessWireArray(rChunkLength, new String[0]);
/*  47 */     Wire[] qWires = this.generator.__createProverWitnessWireArray(qChunkLength, new String[0]);
/*     */     
/*  49 */     int[] rChunkBitwidths = new int[rChunkLength];
/*  50 */     int[] qChunkBitwidths = new int[qChunkLength];
/*     */     
/*  52 */     Arrays.fill(rChunkBitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  53 */     Arrays.fill(qChunkBitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */     
/*  55 */     if (rBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/*  56 */       rChunkBitwidths[rChunkLength - 1] = rBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK;
/*     */     }
/*  58 */     if (qBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/*  59 */       qChunkBitwidths[qChunkLength - 1] = qBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK;
/*     */     }
/*     */     
/*  62 */     this.r = new PackedValue(rWires, rChunkBitwidths);
/*  63 */     this.q = new PackedValue(qWires, qChunkBitwidths);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  70 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */         {
/*     */           public void evaluate(CircuitEvaluator evaluator) {
/*  73 */             BigInteger aValue = evaluator.getWireValue(LongIntegerModNotStrictModulusGadget.this.a, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  74 */             BigInteger bValue = evaluator.getWireValue(LongIntegerModNotStrictModulusGadget.this.b, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  75 */             BigInteger rValue = aValue.mod(bValue);
/*  76 */             BigInteger qValue = aValue.divide(bValue);
/*  77 */             evaluator.setWireValue(LongIntegerModNotStrictModulusGadget.this.r.getArray(), Util.split(rValue, UnsignedInteger.BITWIDTH_PER_CHUNK));
/*  78 */             evaluator.setWireValue(LongIntegerModNotStrictModulusGadget.this.q.getArray(), Util.split(qValue, UnsignedInteger.BITWIDTH_PER_CHUNK));
/*     */           }
/*     */         });
/*     */     
/*  82 */     this.r.forceBitwidth();
/*  83 */     this.q.forceBitwidth();
/*     */     
/*  85 */     PackedValue res = this.q.mul(this.b).add(this.r);
/*  86 */     res.forceEquality2(this.a);
/*     */     
/*  88 */     if (this.restrictRange) {
/*  89 */       this.generator.__addOneAssertion(this.r.isLessThan(this.b, rBitwidth).getWire(), new String[0]);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire[] getOutputWires() {
/*  97 */     return this.r.getArray();
/*     */   }
/*     */   
/*     */   public PackedValue getRemainder() {
/* 101 */     return this.r;
/*     */   }
/*     */   
/*     */   public PackedValue getQuotient() {
/* 105 */     return this.q;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\LongIntegerModNotStrictModulusGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */