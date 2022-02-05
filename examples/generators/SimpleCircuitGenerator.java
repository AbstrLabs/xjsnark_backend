/*    */ package examples.generators;
/*    */ 
/*    */ import backend.eval.CircuitEvaluator;
/*    */ import backend.structure.CircuitGenerator;
/*    */ import backend.structure.Wire;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SimpleCircuitGenerator
/*    */   extends CircuitGenerator
/*    */ {
/*    */   private Wire[] inputs;
/*    */   
/*    */   public SimpleCircuitGenerator(String circuitName) {
/* 15 */     super(circuitName);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void outsource() {
/* 22 */     this.inputs = __createInputWireArray(4, new String[0]);
/*    */ 
/*    */     
/* 25 */     Wire r1 = this.inputs[0].mul(this.inputs[1], new String[0]);
/*    */ 
/*    */     
/* 28 */     Wire r2 = this.inputs[2].add(this.inputs[3], new String[0]);
/*    */ 
/*    */     
/* 31 */     Wire result = r1.add(5L, new String[0]).mul(r2.mul(6L, new String[0]), new String[0]);
/*    */ 
/*    */     
/* 34 */     __makeOutput(result, new String[0]);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void __generateSampleInput(CircuitEvaluator circuitEvaluator) {
/* 40 */     for (int i = 0; i < 4; i++) {
/* 41 */       circuitEvaluator.setWireValue(this.inputs[i], (i + 1));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 47 */     SimpleCircuitGenerator generator = new SimpleCircuitGenerator("simple_example");
/* 48 */     generator.__generateCircuit();
/* 49 */     generator.__evalCircuit();
/* 50 */     generator.__prepFiles();
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\generators\SimpleCircuitGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */