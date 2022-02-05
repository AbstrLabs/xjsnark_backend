/*    */ package backend.structure;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VariableWire
/*    */   extends Wire
/*    */ {
/*    */   private WireArray bitWires;
/*    */   
/*    */   public VariableWire(int wireId) {
/* 11 */     super(wireId);
/*    */   }
/*    */   
/*    */   public VariableWire(WireArray bits) {
/* 15 */     super(bits);
/*    */   }
/*    */ 
/*    */   
/*    */   WireArray getBitWires() {
/* 20 */     return this.bitWires;
/*    */   }
/*    */   
/*    */   void setBits(WireArray bitWires) {
/* 24 */     this.bitWires = bitWires;
/*    */   }
/*    */   
/*    */   public Wire copy() {
/* 28 */     VariableWire newWire = new VariableWire(this.wireId);
/* 29 */     newWire.bitWires = this.bitWires;
/* 30 */     return newWire;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\structure\VariableWire.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */