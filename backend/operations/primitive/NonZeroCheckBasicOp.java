/*    */ package backend.operations.primitive;
/*    */ 
/*    */ import backend.eval.Instruction;
/*    */ import backend.structure.Wire;
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NonZeroCheckBasicOp
/*    */   extends BasicOp
/*    */ {
/*    */   public NonZeroCheckBasicOp(Wire w, Wire out1, Wire out2, String... desc) {
/* 15 */     super(new Wire[] { w }, new Wire[] { out1, out2 }, desc);
/*    */   }
/*    */   
/*    */   public String getOpcode() {
/* 19 */     return "zerop";
/*    */   }
/*    */ 
/*    */   
/*    */   public void compute(BigInteger[] assignment) {
/* 24 */     if (assignment[this.inputs[0].getWireId()].signum() == 0) {
/* 25 */       assignment[this.outputs[1].getWireId()] = BigInteger.ZERO;
/*    */     } else {
/* 27 */       assignment[this.outputs[1].getWireId()] = BigInteger.ONE;
/*    */     } 
/* 29 */     assignment[this.outputs[0].getWireId()] = BigInteger.ZERO;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 35 */     if (this == obj)
/* 36 */       return true; 
/* 37 */     if (!(obj instanceof NonZeroCheckBasicOp)) {
/* 38 */       return false;
/*    */     }
/* 40 */     NonZeroCheckBasicOp op = (NonZeroCheckBasicOp)obj;
/* 41 */     return this.inputs[0].equals(op.inputs[0]);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNumMulGates() {
/* 47 */     return 2;
/*    */   }
/*    */ 
/*    */   
/*    */   public Instruction copy(Wire[] wireArray) {
/* 52 */     Wire in = wireArray[this.inputs[0].getWireId()];
/*    */     
/* 54 */     Wire out = this.outputs[0].copy();
/*    */     
/* 56 */     wireArray[out.getWireId()] = out;
/*    */     
/* 58 */     Wire out2 = this.outputs[1].copy();
/*    */     
/* 60 */     wireArray[out2.getWireId()] = out2;
/* 61 */     return new NonZeroCheckBasicOp(in, out, out2, new String[] { this.desc });
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\primitive\NonZeroCheckBasicOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */