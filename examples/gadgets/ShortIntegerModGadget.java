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
/*     */ public class ShortIntegerModGadget
/*     */   extends Gadget
/*     */ {
/*     */   private PackedValue a;
/*     */   private PackedValue modulus;
/*     */   private PackedValue inverse;
/*     */   private PackedValue q;
/*     */   
/*     */   public ShortIntegerModGadget(PackedValue a, PackedValue modulus, String... desc) {
/*  29 */     super(desc);
/*  30 */     this.a = a;
/*  31 */     this.modulus = modulus;
/*  32 */     buildCircuit();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void buildCircuit() {
/*  39 */     int modulusBitwidth = this.modulus.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength();
/*  40 */     int aBitwidth = this.a.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength();
/*     */     
/*  42 */     int inverseBitwidth = modulusBitwidth;
/*  43 */     int qBitwidth = aBitwidth + 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  52 */     Wire[] inverseWires = this.generator.__createProverWitnessWireArray(1, new String[0]);
/*  53 */     Wire[] qWires = this.generator.__createProverWitnessWireArray(1, new String[0]);
/*     */     
/*  55 */     int[] inverseChunkBitwidths = { inverseBitwidth };
/*  56 */     int[] qChunkBitwidths = { qBitwidth };
/*     */ 
/*     */ 
/*     */     
/*  60 */     this.inverse = new PackedValue(inverseWires, inverseChunkBitwidths);
/*  61 */     this.q = new PackedValue(qWires, qChunkBitwidths);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  71 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */         {
/*     */           public void evaluate(CircuitEvaluator evaluator) {
/*  74 */             BigInteger aValue = evaluator.getWireValue(ShortIntegerModGadget.this.a, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  75 */             BigInteger bValue = evaluator.getWireValue(ShortIntegerModGadget.this.modulus, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */             
/*  77 */             BigInteger rValue = aValue.modInverse(bValue);
/*  78 */             BigInteger qValue = aValue.multiply(rValue).divide(bValue);
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
/*  92 */             evaluator.setWireValue(ShortIntegerModGadget.this.inverse.getArray()[0], rValue);
/*     */             
/*  94 */             evaluator.setWireValue(ShortIntegerModGadget.this.q.getArray()[0], qValue);
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 104 */     this.inverse.forceBitwidth();
/* 105 */     this.q.forceBitwidth();
/*     */     
/* 107 */     PackedValue res = this.q.mul(this.modulus).add(new PackedValue(new BigInteger[] { BigInteger.ONE }));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 112 */     res.forceEquality2(this.a.mul(this.inverse));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire[] getOutputWires() {
/* 120 */     return this.inverse.getArray();
/*     */   }
/*     */   
/*     */   public PackedValue getInverse() {
/* 124 */     return this.inverse;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\ShortIntegerModGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */