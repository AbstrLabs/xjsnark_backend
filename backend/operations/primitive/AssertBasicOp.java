/*    */ package backend.operations.primitive;
/*    */ 
/*    */ import backend.config.Config;
/*    */ import backend.eval.Instruction;
/*    */ import backend.structure.Wire;
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AssertBasicOp
/*    */   extends BasicOp
/*    */ {
/*    */   public AssertBasicOp(Wire w1, Wire w2, Wire output, String... desc) {
/* 14 */     super(new Wire[] { w1, w2 }, new Wire[] { output }, desc);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void compute(BigInteger[] assignment) {
/* 19 */     BigInteger leftSide = assignment[this.inputs[0].getWireId()].multiply(
/* 20 */         assignment[this.inputs[1].getWireId()]).mod(
/* 21 */         Config.getFiniteFieldModulus());
/* 22 */     BigInteger rightSide = assignment[this.outputs[0].getWireId()];
/* 23 */     boolean check = leftSide.equals(rightSide);
/* 24 */     if (!check) {
/* 25 */       System.err.println("Error - Assertion Failed " + this);
/* 26 */       System.out.println(assignment[this.inputs[0].getWireId()] + "*" + 
/* 27 */           assignment[this.inputs[1].getWireId()] + "!=" + 
/* 28 */           assignment[this.outputs[0].getWireId()]);
/* 29 */       throw new RuntimeException("Error During Circuit Evaluation");
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void checkOutputs(BigInteger[] assignment) {}
/*    */ 
/*    */   
/*    */   public String getOpcode() {
/* 39 */     return "assert";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 45 */     if (this == obj)
/* 46 */       return true; 
/* 47 */     if (!(obj instanceof AssertBasicOp)) {
/* 48 */       return false;
/*    */     }
/* 50 */     AssertBasicOp op = (AssertBasicOp)obj;
/*    */     
/* 52 */     boolean check1 = (this.inputs[0].equals(op.inputs[0]) && 
/* 53 */       this.inputs[1].equals(op.inputs[1]));
/* 54 */     boolean check2 = (this.inputs[1].equals(op.inputs[0]) && 
/* 55 */       this.inputs[0].equals(op.inputs[1]));
/* 56 */     return ((check1 || check2) && this.outputs[0].equals(op.outputs[0]));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNumMulGates() {
/* 62 */     return 1;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Instruction copy(Wire[] wireArray) {
/* 68 */     Wire[] ws = new Wire[this.inputs.length];
/* 69 */     for (int i = 0; i < ws.length; i++)
/*    */     {
/* 71 */       ws[i] = wireArray[this.inputs[i].getWireId()];
/*    */     }
/*    */     
/* 74 */     Wire out = wireArray[this.outputs[0].getWireId()];
/*    */ 
/*    */     
/* 77 */     return new AssertBasicOp(ws[0], ws[1], out, new String[] { this.desc });
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\primitive\AssertBasicOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */