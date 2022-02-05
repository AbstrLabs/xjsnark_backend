/*    */ package examples.generators;
/*    */ 
/*    */ import backend.eval.CircuitEvaluator;
/*    */ import backend.structure.CircuitGenerator;
/*    */ import backend.structure.Wire;
/*    */ import examples.gadgets.SHA256Gadget;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SHA2CircuitGenerator
/*    */   extends CircuitGenerator
/*    */ {
/*    */   private Wire[] inputWires;
/*    */   private SHA256Gadget sha2Gadget;
/*    */   
/*    */   public SHA2CircuitGenerator(String circuitName) {
/* 17 */     super(circuitName);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void outsource() {
/* 25 */     this.inputWires = __createInputWireArray(16, new String[0]);
/*    */ 
/*    */     
/* 28 */     this.sha2Gadget = new SHA256Gadget(this.inputWires, 32, 64, false, false, new String[0]);
/* 29 */     Wire[] digest = this.sha2Gadget.getOutputWires();
/* 30 */     __makeOutputArray(digest, new String[] { "digest" });
/*    */   }
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
/*    */   
/*    */   public void __generateSampleInput(CircuitEvaluator evaluator) {
/* 46 */     String inputStr = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijkl";
/* 47 */     for (int i = 0; i < this.inputWires.length; i++) {
/* 48 */       evaluator.setWireValue(this.inputWires[i], inputStr.charAt(i));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 54 */     SHA2CircuitGenerator generator = new SHA2CircuitGenerator("sha_256");
/* 55 */     generator.__generateCircuit();
/* 56 */     generator.__evalCircuit();
/* 57 */     generator.__prepFiles();
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\generators\SHA2CircuitGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */