/*    */ package backend.operations;
/*    */ 
/*    */ import backend.structure.CircuitGenerator;
/*    */ import backend.structure.Wire;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class Gadget
/*    */ {
/*    */   protected String description;
/* 15 */   protected CircuitGenerator generator = CircuitGenerator.__getActiveCircuitGenerator(); public Gadget(String... desc) {
/* 16 */     if (desc.length > 0) {
/* 17 */       this.description = desc[0];
/*    */     } else {
/* 19 */       this.description = "";
/*    */     } 
/*    */   }
/*    */   public abstract Wire[] getOutputWires();
/*    */   
/*    */   public String toString() {
/* 25 */     return String.valueOf(getClass().getSimpleName()) + " " + this.description;
/*    */   }
/*    */   
/*    */   public String debugStr(String s) {
/* 29 */     return this + ":" + s;
/*    */   }
/*    */   
/*    */   public void defineWitnessWires() {}
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\Gadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */