/*    */ package backend.structure;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LinearCombinationBitWire
/*    */   extends BitWire
/*    */ {
/*    */   public LinearCombinationBitWire(int wireId) {
/*  9 */     super(wireId);
/*    */   }
/*    */   
/*    */   public WireArray getBitWires() {
/* 13 */     return new WireArray(new Wire[] { this });
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\structure\LinearCombinationBitWire.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */