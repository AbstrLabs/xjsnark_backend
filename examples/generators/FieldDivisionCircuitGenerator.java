/*    */ package examples.generators;
/*    */ 
/*    */ import backend.eval.CircuitEvaluator;
/*    */ import backend.structure.CircuitGenerator;
/*    */ import backend.structure.Wire;
/*    */ import examples.gadgets.FieldDivisionGadget;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FieldDivisionCircuitGenerator
/*    */   extends CircuitGenerator
/*    */ {
/*    */   private Wire[] a;
/*    */   private Wire[] b;
/*    */   private FieldDivisionGadget[] gadgets;
/*    */   private int gatesNum;
/*    */   
/*    */   public FieldDivisionCircuitGenerator(String circuitName, int gatesNum) {
/* 20 */     super(circuitName);
/* 21 */     this.gatesNum = gatesNum;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void outsource() {
/* 27 */     this.a = __createInputWireArray(this.gatesNum, new String[] { "Input a" });
/* 28 */     this.b = __createInputWireArray(this.gatesNum, new String[] { "Input b" });
/* 29 */     this.gadgets = new FieldDivisionGadget[this.gatesNum];
/* 30 */     for (int i = 0; i < this.gatesNum; i++) {
/* 31 */       this.gadgets[i] = new FieldDivisionGadget(this.a[i], this.b[i], new String[] { "Divison Gagdet#" + i });
/* 32 */       Wire[] result = this.gadgets[i].getOutputWires();
/* 33 */       __makeOutput(result[0], new String[] { "Output of gate # " + i });
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void __generateSampleInput(CircuitEvaluator circuitEvaluator) {
/* 39 */     for (int i = 0; i < this.gatesNum; i++) {
/* 40 */       circuitEvaluator.setWireValue(this.a[i], (10 + i));
/* 41 */       circuitEvaluator.setWireValue(this.b[i], (20 + i));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 47 */     FieldDivisionCircuitGenerator generator = new FieldDivisionCircuitGenerator("division", 100);
/* 48 */     generator.__generateCircuit();
/* 49 */     generator.__evalCircuit();
/* 50 */     generator.__prepFiles();
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\generators\FieldDivisionCircuitGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */