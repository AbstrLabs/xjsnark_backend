/*    */ package backend.structure;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LinearCombinationWire
/*    */   extends Wire
/*    */ {
/*    */   private WireArray bitWires;
/*    */   
/*    */   public LinearCombinationWire(int wireId) {
/* 11 */     super(wireId);
/*    */   }
/*    */   
/*    */   public LinearCombinationWire(WireArray bits) {
/* 15 */     super(bits);
/*    */   }
/*    */   
/*    */   WireArray getBitWires() {
/* 19 */     return this.bitWires;
/*    */   }
/*    */   
/*    */   void setBits(WireArray bitWires) {
/* 23 */     this.bitWires = bitWires;
/*    */   }
/*    */ 
/*    */   
/*    */   public Wire copy() {
/* 28 */     LinearCombinationWire newWire = new LinearCombinationWire(this.wireId);
/* 29 */     newWire.bitWires = this.bitWires;
/* 30 */     return newWire;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\structure\LinearCombinationWire.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */