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
/*    */ public class ORBasicOp
/*    */   extends BasicOp
/*    */ {
/*    */   public ORBasicOp(Wire w1, Wire w2, Wire output, String... desc) {
/* 15 */     super(new Wire[] { w1, w2 }, new Wire[] { output }, desc);
/*    */   }
/*    */   
/*    */   public String getOpcode() {
/* 19 */     return "or";
/*    */   }
/*    */   
/*    */   public void checkInputs(BigInteger[] assignment) {
/* 23 */     super.checkInputs(assignment);
/* 24 */     boolean check = (Util.isBinary(assignment[this.inputs[0].getWireId()]) && 
/* 25 */       Util.isBinary(assignment[this.inputs[1].getWireId()]));
/* 26 */     if (!check) {
/* 27 */       System.err.println("Error - Input(s) to OR are not binary. " + 
/* 28 */           this);
/* 29 */       throw new RuntimeException("Error During Evaluation");
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void compute(BigInteger[] assignment) {
/* 36 */     assignment[this.outputs[0].getWireId()] = assignment[this.inputs[0].getWireId()].or(
/* 37 */         assignment[this.inputs[1].getWireId()]);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 43 */     if (this == obj)
/* 44 */       return true; 
/* 45 */     if (!(obj instanceof ORBasicOp)) {
/* 46 */       return false;
/*    */     }
/* 48 */     ORBasicOp op = (ORBasicOp)obj;
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
/*    */   public Instruction copy(Wire[] wireArray) {
/* 60 */     Wire in = wireArray[this.inputs[0].getWireId()];
/* 61 */     Wire in2 = wireArray[this.inputs[1].getWireId()];
/*    */     
/* 63 */     Wire out = this.outputs[0].copy();
/*    */     
/* 65 */     wireArray[out.getWireId()] = out;
/* 66 */     return new ORBasicOp(in, in2, out, new String[] { this.desc });
/*    */   }
/*    */ 
/*    */   
/*    */   public int getNumMulGates() {
/* 71 */     return 1;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\primitive\ORBasicOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */