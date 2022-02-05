/*    */ package examples.gadgets;
/*    */ 
/*    */ import backend.config.Config;
/*    */ import backend.eval.CircuitEvaluator;
/*    */ import backend.eval.Instruction;
/*    */ import backend.operations.Gadget;
/*    */ import backend.structure.Wire;
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FieldDivisionGadget
/*    */   extends Gadget
/*    */ {
/*    */   private Wire a;
/*    */   private Wire b;
/*    */   private Wire c;
/*    */   
/*    */   public FieldDivisionGadget(Wire a, Wire b, String... desc) {
/* 21 */     super(desc);
/* 22 */     this.a = a;
/* 23 */     this.b = b;
/* 24 */     buildCircuit();
/*    */   }
/*    */ 
/*    */   
/*    */   private void buildCircuit() {
/* 29 */     this.c = this.generator.__createProverWitnessWire(new String[] { debugStr("division result") });
/* 30 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*    */         {
/*    */           public void evaluate(CircuitEvaluator evaluator) {
/* 33 */             BigInteger aValue = evaluator.getWireValue(FieldDivisionGadget.this.a);
/* 34 */             BigInteger bValue = evaluator.getWireValue(FieldDivisionGadget.this.b);
/* 35 */             BigInteger cValue = aValue.multiply(bValue.modInverse(Config.getFiniteFieldModulus())).mod(Config.getFiniteFieldModulus());
/* 36 */             evaluator.setWireValue(FieldDivisionGadget.this.c, cValue);
/*    */           }
/*    */         });
/*    */     
/* 40 */     this.generator.__addAssertion(this.b, this.c, this.a, new String[] { debugStr("Assertion for division result") });
/*    */   }
/*    */ 
/*    */   
/*    */   public Wire[] getOutputWires() {
/* 45 */     return new Wire[] { this.c };
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\FieldDivisionGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */