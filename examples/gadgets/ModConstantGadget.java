/*    */ package examples.gadgets;
/*    */ 
/*    */ import backend.eval.CircuitEvaluator;
/*    */ import backend.eval.Instruction;
/*    */ import backend.operations.Gadget;
/*    */ import backend.structure.Wire;
/*    */ import backend.structure.WireArray;
/*    */ import java.math.BigInteger;
/*    */ import util.Util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ModConstantGadget
/*    */   extends Gadget
/*    */ {
/*    */   private Wire a;
/*    */   private BigInteger b;
/*    */   private Wire r;
/*    */   private Wire q;
/*    */   private int aBitwidth;
/*    */   private boolean restrictRange;
/*    */   
/*    */   public ModConstantGadget(Wire a, int bitwidth, BigInteger b, boolean restrictRange, String... desc) {
/* 33 */     super(desc);
/* 34 */     this.a = a;
/* 35 */     this.b = b;
/* 36 */     this.aBitwidth = bitwidth;
/* 37 */     this.restrictRange = restrictRange;
/* 38 */     if (b.signum() != 1) {
/* 39 */       throw new IllegalArgumentException("b must be a positive constant. Signed operations not supported yet.");
/*    */     }
/* 41 */     if (bitwidth < b.bitLength()) {
/* 42 */       throw new IllegalArgumentException("a's bitwidth < b's bitwidth -- This gadget is not needed.");
/*    */     }
/* 44 */     buildCircuit();
/*    */   }
/*    */ 
/*    */   
/*    */   private void buildCircuit() {
/* 49 */     this.r = this.generator.__createProverWitnessWire(new String[] { "mod result" });
/* 50 */     this.q = this.generator.__createProverWitnessWire(new String[] { "division result" });
/*    */     
/* 52 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*    */         {
/*    */           public void evaluate(CircuitEvaluator evaluator) {
/* 55 */             BigInteger aValue = evaluator.getWireValue(ModConstantGadget.this.a);
/* 56 */             BigInteger rValue = aValue.mod(ModConstantGadget.this.b);
/* 57 */             evaluator.setWireValue(ModConstantGadget.this.r, rValue);
/* 58 */             BigInteger qValue = aValue.divide(ModConstantGadget.this.b);
/* 59 */             evaluator.setWireValue(ModConstantGadget.this.q, qValue);
/*    */           }
/*    */         });
/*    */ 
/*    */     
/* 64 */     int bBitwidth = this.b.bitLength();
/*    */ 
/*    */ 
/*    */     
/* 68 */     this.r.restrictBitLength(bBitwidth, new String[0]);
/* 69 */     this.q.restrictBitLength(this.aBitwidth - bBitwidth + 1, new String[0]);
/* 70 */     this.generator.__addEqualityAssertion(this.q.mul(this.b, new String[0]).add(this.r, new String[0]), this.a, new String[0]);
/*    */     
/* 72 */     if (this.restrictRange) {
/* 73 */       if (this.b.equals(Util.computeBound(bBitwidth - 1).add(BigInteger.ONE))) {
/* 74 */         WireArray bits = this.r.getBitWires(bBitwidth, new String[0]);
/* 75 */         Wire sum = this.generator.__getZeroWire();
/* 76 */         for (int i = 0; i < bits.size() - 1; i++) {
/* 77 */           sum = sum.add(bits.get(i), new String[0]);
/*    */         }
/* 79 */         this.generator.__addAssertion(sum, bits.get(bits.size() - 1), this.generator.__getZeroWire(), new String[0]);
/*    */       } else {
/*    */         
/* 82 */         this.generator.__addOneAssertion(this.r.isLessThan(this.b, bBitwidth, new String[0]), new String[0]);
/*    */       } 
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Wire[] getOutputWires() {
/* 90 */     return new Wire[] { this.r };
/*    */   }
/*    */   
/*    */   public Wire getRemainder() {
/* 94 */     return this.r;
/*    */   }
/*    */   
/*    */   public Wire getQuotient() {
/* 98 */     return this.q;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\ModConstantGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */