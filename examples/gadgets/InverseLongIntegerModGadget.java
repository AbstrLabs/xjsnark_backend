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
/*     */ public class InverseLongIntegerModGadget
/*     */   extends Gadget
/*     */ {
/*     */   private PackedValue a;
/*     */   private PackedValue modulus;
/*     */   private PackedValue inverse;
/*     */   private PackedValue q;
/*     */   
/*     */   public InverseLongIntegerModGadget(PackedValue a, PackedValue modulus, String... desc) {
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
/*     */     
/*  41 */     int aBitwidth = this.a.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength();
/*     */ 
/*     */     
/*  44 */     int inverseBitwidth = modulusBitwidth;
/*     */     
/*  46 */     int qBitwidth = aBitwidth + 1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  52 */     int inverseChunkLength = (int)Math.ceil(inverseBitwidth * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  53 */     int qChunkLength = (int)Math.ceil(qBitwidth * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */     
/*  55 */     Wire[] inverseWires = this.generator.__createProverWitnessWireArray(inverseChunkLength, new String[0]);
/*     */     
/*  57 */     Wire[] qWires = this.generator.__createProverWitnessWireArray(qChunkLength, new String[0]);
/*     */     
/*  59 */     int[] inverseChunkBitwidths = new int[inverseChunkLength];
/*  60 */     int[] qChunkBitwidths = new int[qChunkLength];
/*     */     
/*  62 */     Arrays.fill(inverseChunkBitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  63 */     Arrays.fill(qChunkBitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */     
/*  65 */     if (inverseBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/*  66 */       inverseChunkBitwidths[inverseChunkLength - 1] = inverseBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK;
/*     */     }
/*  68 */     if (qBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/*  69 */       qChunkBitwidths[qChunkLength - 1] = qBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK;
/*     */     }
/*     */     
/*  72 */     this.inverse = new PackedValue(inverseWires, inverseChunkBitwidths);
/*  73 */     this.q = new PackedValue(qWires, qChunkBitwidths);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  83 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */         {
/*     */           public void evaluate(CircuitEvaluator evaluator) {
/*  86 */             BigInteger aValue = evaluator.getWireValue(InverseLongIntegerModGadget.this.a, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  87 */             BigInteger bValue = evaluator.getWireValue(InverseLongIntegerModGadget.this.modulus, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */             
/*  89 */             BigInteger rValue = aValue.modInverse(bValue);
/*  90 */             BigInteger qValue = aValue.multiply(rValue).divide(bValue);
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
/* 104 */             evaluator.setWireValue(InverseLongIntegerModGadget.this.inverse.getArray(), Util.split(rValue, UnsignedInteger.BITWIDTH_PER_CHUNK));
/*     */             
/* 106 */             evaluator.setWireValue(InverseLongIntegerModGadget.this.q.getArray(), Util.split(qValue, UnsignedInteger.BITWIDTH_PER_CHUNK));
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 116 */     this.inverse.forceBitwidth();
/* 117 */     this.q.forceBitwidth();
/* 118 */     this.inverse.setWitnessIndicator(true);
/* 119 */     this.q.setWitnessIndicator(true);
/*     */ 
/*     */ 
/*     */     
/* 123 */     PackedValue res = this.q.mul(this.modulus).add(new PackedValue(new BigInteger[] { BigInteger.ONE }));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 128 */     res.forceEquality2(this.a.mul(this.inverse));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire[] getOutputWires() {
/* 136 */     return this.inverse.getArray();
/*     */   }
/*     */   
/*     */   public PackedValue getInverse() {
/* 140 */     return this.inverse;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\InverseLongIntegerModGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */