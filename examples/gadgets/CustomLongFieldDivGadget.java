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
/*     */ public class CustomLongFieldDivGadget
/*     */   extends Gadget
/*     */ {
/*     */   private PackedValue a;
/*     */   private PackedValue b;
/*     */   private PackedValue m;
/*     */   private PackedValue result;
/*     */   private PackedValue q;
/*     */   
/*     */   public CustomLongFieldDivGadget(PackedValue a, PackedValue b, PackedValue m, String... desc) {
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
/*  53 */     int resultChunkLength = (int)Math.ceil(resultBitwidth * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  54 */     int qChunkLength = (int)Math.ceil(qBitwidth * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */     
/*  56 */     Wire[] resultWires = this.generator.__createProverWitnessWireArray(resultChunkLength, new String[0]);
/*     */     
/*  58 */     Wire[] qWires = this.generator.__createProverWitnessWireArray(qChunkLength, new String[0]);
/*     */     
/*  60 */     int[] resultChunkBitwidths = new int[resultChunkLength];
/*  61 */     int[] qChunkBitwidths = new int[qChunkLength];
/*     */     
/*  63 */     Arrays.fill(resultChunkBitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  64 */     Arrays.fill(qChunkBitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */     
/*  66 */     if (resultBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/*  67 */       resultChunkBitwidths[resultChunkLength - 1] = resultBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK;
/*     */     }
/*  69 */     if (qBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/*  70 */       qChunkBitwidths[qChunkLength - 1] = qBitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK;
/*     */     }
/*     */     
/*  73 */     this.result = new PackedValue(resultWires, resultChunkBitwidths);
/*  74 */     this.q = new PackedValue(qWires, qChunkBitwidths);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  84 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */         {
/*     */           public void evaluate(CircuitEvaluator evaluator) {
/*  87 */             BigInteger aValue = evaluator.getWireValue(CustomLongFieldDivGadget.this.a, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  88 */             BigInteger bValue = evaluator.getWireValue(CustomLongFieldDivGadget.this.b, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  89 */             BigInteger mValue = evaluator.getWireValue(CustomLongFieldDivGadget.this.m, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  90 */             BigInteger rValue = bValue.modInverse(mValue).multiply(aValue).mod(mValue);
/*  91 */             BigInteger qValue = bValue.multiply(rValue).subtract(aValue).divide(mValue);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*  98 */             evaluator.setWireValue(CustomLongFieldDivGadget.this.result.getArray(), Util.split(rValue, UnsignedInteger.BITWIDTH_PER_CHUNK));
/*  99 */             evaluator.setWireValue(CustomLongFieldDivGadget.this.q.getArray(), Util.split(qValue, UnsignedInteger.BITWIDTH_PER_CHUNK));
/*     */           }
/*     */         });
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 106 */     this.result.forceBitwidth();
/* 107 */     this.q.forceBitwidth();
/* 108 */     this.result.setWitnessIndicator(true);
/* 109 */     this.q.setWitnessIndicator(true);
/*     */     
/* 111 */     PackedValue res = this.q.mul(this.m).add(this.a);
/* 112 */     res.forceEquality2(this.b.mul(this.result));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire[] getOutputWires() {
/* 118 */     return this.result.getArray();
/*     */   }
/*     */   
/*     */   public PackedValue getResult() {
/* 122 */     return this.result;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\CustomLongFieldDivGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */