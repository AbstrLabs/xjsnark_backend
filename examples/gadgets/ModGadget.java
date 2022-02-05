/*    */ package examples.gadgets;
/*    */ 
/*    */ import backend.eval.CircuitEvaluator;
/*    */ import backend.eval.Instruction;
/*    */ import backend.operations.Gadget;
/*    */ import backend.structure.Wire;
/*    */ import java.math.BigInteger;
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
/*    */ public class ModGadget
/*    */   extends Gadget
/*    */ {
/*    */   private Wire a;
/*    */   private Wire b;
/*    */   private Wire r;
/*    */   private Wire q;
/*    */   private int bitwidth1;
/*    */   private int bitwidth2;
/*    */   private boolean restrictRange;
/*    */   
/*    */   public ModGadget(Wire a, int bitwidth1, Wire b, int bitwidth2, boolean restrictRange, String... desc) {
/* 32 */     super(desc);
/* 33 */     this.a = a;
/* 34 */     this.b = b;
/* 35 */     this.bitwidth1 = bitwidth1;
/* 36 */     this.bitwidth2 = bitwidth2;
/* 37 */     this.restrictRange = restrictRange;
/*    */ 
/*    */ 
/*    */     
/* 41 */     buildCircuit();
/*    */   }
/*    */ 
/*    */   
/*    */   private void buildCircuit() {
/* 46 */     this.r = this.generator.__createProverWitnessWire(new String[] { "mod result" });
/* 47 */     this.q = this.generator.__createProverWitnessWire(new String[] { "division result" });
/*    */     
/* 49 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*    */         {
/*    */           public void evaluate(CircuitEvaluator evaluator) {
/* 52 */             BigInteger aValue = evaluator.getWireValue(ModGadget.this.a);
/* 53 */             BigInteger bValue = evaluator.getWireValue(ModGadget.this.b);
/* 54 */             BigInteger rValue = aValue.mod(bValue);
/* 55 */             evaluator.setWireValue(ModGadget.this.r, rValue);
/* 56 */             BigInteger qValue = aValue.divide(bValue);
/* 57 */             evaluator.setWireValue(ModGadget.this.q, qValue);
/*    */           }
/*    */         });
/*    */ 
/*    */ 
/*    */     
/* 63 */     this.r.restrictBitLength(Math.min(this.bitwidth1, this.bitwidth2), new String[0]);
/* 64 */     this.q.restrictBitLength(this.bitwidth1, new String[0]);
/* 65 */     this.generator.__addEqualityAssertion(this.q.mul(this.b, new String[0]).add(this.r, new String[0]), this.a, new String[0]);
/*    */     
/* 67 */     if (this.restrictRange) {
/* 68 */       this.generator.__addOneAssertion(this.r.isLessThan(this.b, this.bitwidth2, new String[0]), new String[0]);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public Wire[] getOutputWires() {
/* 74 */     return new Wire[] { this.r };
/*    */   }
/*    */   
/*    */   public Wire getRemainder() {
/* 78 */     return this.r;
/*    */   }
/*    */   
/*    */   public Wire getQuotient() {
/* 82 */     return this.q;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\ModGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */