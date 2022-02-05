/*    */ package backend.operations.primitive;
/*    */ 
/*    */ import backend.eval.Instruction;
/*    */ import backend.structure.Wire;
/*    */ import java.math.BigInteger;
/*    */ import util.Util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class XorBasicOp
/*    */   extends BasicOp
/*    */ {
/*    */   public XorBasicOp(Wire w1, Wire w2, Wire output, String... desc) {
/* 15 */     super(new Wire[] { w1, w2 }, new Wire[] { output }, desc);
/*    */   }
/*    */   
/*    */   public String getOpcode() {
/* 19 */     return "xor";
/*    */   }
/*    */   
/*    */   public void checkInputs(BigInteger[] assignment) {
/* 23 */     super.checkInputs(assignment);
/* 24 */     boolean check = (Util.isBinary(assignment[this.inputs[0].getWireId()]) && 
/* 25 */       Util.isBinary(assignment[this.inputs[1].getWireId()]));
/* 26 */     if (!check) {
/* 27 */       System.err.println("Error - Input(s) to XOR are not binary. " + 
/* 28 */           this);
/* 29 */       throw new RuntimeException("Error During Evaluation");
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void compute(BigInteger[] assignment) {
/* 36 */     assignment[this.outputs[0].getWireId()] = assignment[this.inputs[0].getWireId()].xor(
/* 37 */         assignment[this.inputs[1].getWireId()]);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 43 */     if (this == obj)
/* 44 */       return true; 
/* 45 */     if (!(obj instanceof XorBasicOp)) {
/* 46 */       return false;
/*    */     }
/* 48 */     XorBasicOp op = (XorBasicOp)obj;
/*    */     
/* 50 */     boolean check1 = (this.inputs[0].equals(op.inputs[0]) && 
/* 51 */       this.inputs[1].equals(op.inputs[1]));
/* 52 */     boolean check2 = (this.inputs[1].equals(op.inputs[0]) && 
/* 53 */       this.inputs[0].equals(op.inputs[1]));
/* 54 */     return !(!check1 && !check2);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNumMulGates() {
/* 60 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public Instruction copy(Wire[] wireArray) {
/* 65 */     Wire in = wireArray[this.inputs[0].getWireId()];
/* 66 */     Wire in2 = wireArray[this.inputs[1].getWireId()];
/*    */     
/* 68 */     Wire out = this.outputs[0].copy();
/* 69 */     wireArray[out.getWireId()] = out;
/* 70 */     return new XorBasicOp(in, in2, out, new String[] { this.desc });
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\primitive\XorBasicOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */