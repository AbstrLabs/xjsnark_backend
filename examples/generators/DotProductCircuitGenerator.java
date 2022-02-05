/*    */ package examples.generators;
/*    */ 
/*    */ import backend.eval.CircuitEvaluator;
/*    */ import backend.structure.CircuitGenerator;
/*    */ import backend.structure.Wire;
/*    */ import examples.gadgets.DotPorductGadget;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DotProductCircuitGenerator
/*    */   extends CircuitGenerator
/*    */ {
/*    */   private Wire[] a;
/*    */   private Wire[] b;
/*    */   private int dimension;
/*    */   
/*    */   public DotProductCircuitGenerator(String circuitName, int dimension) {
/* 18 */     super(circuitName);
/* 19 */     this.dimension = dimension;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void outsource() {
/* 25 */     this.a = __createInputWireArray(this.dimension, new String[] { "Input a" });
/* 26 */     this.b = __createInputWireArray(this.dimension, new String[] { "Input b" });
/*    */     
/* 28 */     DotPorductGadget dotPorductGadget = new DotPorductGadget(this.a, this.b, new String[0]);
/* 29 */     Wire[] result = dotPorductGadget.getOutputWires();
/* 30 */     __makeOutput(result[0], new String[] { "output of dot product a, b" });
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void __generateSampleInput(CircuitEvaluator circuitEvaluator) {
/* 36 */     for (int i = 0; i < this.dimension; i++) {
/* 37 */       circuitEvaluator.setWireValue(this.a[i], (10 + i));
/* 38 */       circuitEvaluator.setWireValue(this.b[i], (20 + i));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 44 */     DotProductCircuitGenerator generator = new DotProductCircuitGenerator("dot_product", 3);
/* 45 */     generator.__generateCircuit();
/* 46 */     generator.__evalCircuit();
/* 47 */     generator.__prepFiles();
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\generators\DotProductCircuitGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */