/*    */ package backend.structure;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class VariableBitWire
/*    */   extends BitWire
/*    */ {
/*    */   public VariableBitWire(int wireId) {
/*  9 */     super(wireId);
/*    */   }
/*    */   
/*    */   public WireArray getBitWires() {
/* 13 */     return new WireArray(new Wire[] { this });
/*    */   }
/*    */   
/*    */   public Wire copy() {
/* 17 */     VariableBitWire newWire = new VariableBitWire(this.wireId);
/* 18 */     return newWire;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\structure\VariableBitWire.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */