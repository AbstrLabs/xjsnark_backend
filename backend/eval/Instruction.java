/*    */ package backend.eval;
/*    */ 
/*    */ import backend.structure.Wire;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface Instruction
/*    */ {
/*    */   void evaluate(CircuitEvaluator paramCircuitEvaluator);
/*    */   
/*    */   default void emit(CircuitEvaluator evaluator) {}
/*    */   
/*    */   default boolean doneWithinCircuit() {
/* 16 */     return false;
/*    */   }
/*    */   
/*    */   default Wire[] getUsedWires() {
/* 20 */     return new Wire[0];
/*    */   }
/*    */   
/*    */   default Instruction copy(Wire[] wireArray) {
/* 24 */     return null;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\eval\Instruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */