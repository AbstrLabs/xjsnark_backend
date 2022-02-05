/*    */ package examples.gadgets;
/*    */ 
/*    */ import backend.operations.Gadget;
/*    */ import backend.structure.Wire;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DotPorductGadget
/*    */   extends Gadget
/*    */ {
/*    */   private Wire[] a;
/*    */   private Wire[] b;
/*    */   private Wire output;
/*    */   
/*    */   public DotPorductGadget(Wire[] a, Wire[] b, String... desc) {
/* 16 */     super(desc);
/* 17 */     if (a.length != b.length) {
/* 18 */       throw new IllegalArgumentException();
/*    */     }
/* 20 */     this.a = a;
/* 21 */     this.b = b;
/* 22 */     buildCircuit();
/*    */   }
/*    */   
/*    */   private void buildCircuit() {
/* 26 */     this.output = this.generator.__getZeroWire();
/* 27 */     for (int i = 0; i < this.a.length; i++) {
/* 28 */       Wire product = this.a[i].mul(this.b[i], new String[] { "Multiply elements # " + i });
/* 29 */       this.output = this.output.add(product, new String[0]);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Wire[] getOutputWires() {
/* 35 */     return new Wire[] { this.output };
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\DotPorductGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */