/*     */ package examples.gadgets;
/*     */ 
/*     */ import backend.auxTypes.PackedValue;
/*     */ import backend.auxTypes.UnsignedInteger;
/*     */ import backend.eval.CircuitEvaluator;
/*     */ import backend.eval.Instruction;
/*     */ import backend.operations.Gadget;
/*     */ import backend.structure.Wire;
/*     */ import java.math.BigInteger;
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
/*     */ public class CustomShortFieldDivGadget
/*     */   extends Gadget
/*     */ {
/*     */   private PackedValue a;
/*     */   private PackedValue b;
/*     */   private PackedValue m;
/*     */   private PackedValue result;
/*     */   private PackedValue q;
/*     */   
/*     */   public CustomShortFieldDivGadget(PackedValue a, PackedValue b, PackedValue m, String... desc) {
/*  30 */     super(desc);
/*  31 */     this.a = a;
/*  32 */     this.b = b;
/*  33 */     this.m = m;
/*  34 */     buildCircuit();
/*     */   }
/*     */ 
/*     */   
/*     */   private void buildCircuit() {
/*  39 */     int bBitwidth = this.b.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength();
/*  40 */     int mBitwidth = this.m.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength();
/*     */     
/*  42 */     int aBitwidth = this.a.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength();
/*     */ 
/*     */     
/*  45 */     int resultBitwidth = mBitwidth;
/*     */     
/*  47 */     int qBitwidth = bBitwidth + 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  53 */     int resultChunkLength = 1;
/*  54 */     int qChunkLength = 1;
/*     */     
/*  56 */     Wire[] resultWires = this.generator.__createProverWitnessWireArray(resultChunkLength, new String[0]);
/*     */     
/*  58 */     Wire[] qWires = this.generator.__createProverWitnessWireArray(qChunkLength, new String[0]);
/*     */     
/*  60 */     int[] resultChunkBitwidths = new int[resultChunkLength];
/*  61 */     resultChunkBitwidths[0] = mBitwidth;
/*  62 */     int[] qChunkBitwidths = new int[qChunkLength];
/*  63 */     qChunkBitwidths[0] = qBitwidth;
/*     */ 
/*     */ 
/*     */     
/*  67 */     this.result = new PackedValue(resultWires, resultChunkBitwidths);
/*  68 */     this.q = new PackedValue(qWires, qChunkBitwidths);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  78 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */         {
/*     */           public void evaluate(CircuitEvaluator evaluator) {
/*  81 */             BigInteger aValue = evaluator.getWireValue(CustomShortFieldDivGadget.this.a, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  82 */             BigInteger bValue = evaluator.getWireValue(CustomShortFieldDivGadget.this.b, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  83 */             BigInteger mValue = evaluator.getWireValue(CustomShortFieldDivGadget.this.m, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  84 */             BigInteger rValue = bValue.modInverse(mValue).multiply(aValue).mod(mValue);
/*  85 */             BigInteger qValue = bValue.multiply(rValue).divide(mValue);
/*  86 */             evaluator.setWireValue(CustomShortFieldDivGadget.this.result.getArray()[0], rValue);
/*  87 */             evaluator.setWireValue(CustomShortFieldDivGadget.this.q.getArray()[0], qValue);
/*     */           }
/*     */         });
/*     */ 
/*     */     
/*  92 */     this.result.forceBitwidth();
/*  93 */     this.q.forceBitwidth();
/*     */     
/*  95 */     this.result.setWitnessIndicator(true);
/*  96 */     this.q.setWitnessIndicator(true);
/*     */     
/*  98 */     PackedValue res = this.q.mul(this.m).add(this.a);
/*  99 */     res.forceEquality2(this.b.mul(this.result));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire[] getOutputWires() {
/* 105 */     return this.result.getArray();
/*     */   }
/*     */   
/*     */   public PackedValue getResult() {
/* 109 */     return this.result;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\CustomShortFieldDivGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */