/*    */ package backend.operations.primitive;
/*    */ 
/*    */ import backend.config.Config;
/*    */ import backend.eval.Instruction;
/*    */ import backend.structure.Wire;
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MulBasicOp
/*    */   extends BasicOp
/*    */ {
/*    */   public MulBasicOp(Wire w1, Wire w2, Wire output, String... desc) {
/* 15 */     super(new Wire[] { w1, w2 }, new Wire[] { output }, desc);
/*    */   }
/*    */   
/*    */   public String getOpcode() {
/* 19 */     return "mul";
/*    */   }
/*    */ 
/*    */   
/*    */   public void compute(BigInteger[] assignment) {
/* 24 */     BigInteger result = assignment[this.inputs[0].getWireId()]
/* 25 */       .multiply(assignment[this.inputs[1].getWireId()]);
/* 26 */     if (result.compareTo(Config.getFiniteFieldModulus()) > 0) {
/* 27 */       result = result.mod(Config.getFiniteFieldModulus());
/*    */     }
/* 29 */     assignment[this.outputs[0].getWireId()] = result;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 34 */     if (this == obj)
/* 35 */       return true; 
/* 36 */     if (!(obj instanceof MulBasicOp)) {
/* 37 */       return false;
/*    */     }
/* 39 */     MulBasicOp op = (MulBasicOp)obj;
/*    */     
/* 41 */     boolean check1 = (this.inputs[0].equals(op.inputs[0]) && 
/* 42 */       this.inputs[1].equals(op.inputs[1]));
/* 43 */     boolean check2 = (this.inputs[1].equals(op.inputs[0]) && 
/* 44 */       this.inputs[0].equals(op.inputs[1]));
/* 45 */     return !(!check1 && !check2);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNumMulGates() {
/* 51 */     return 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public Instruction copy(Wire[] wireArray) {
/* 56 */     Wire in = wireArray[this.inputs[0].getWireId()];
/* 57 */     Wire in2 = wireArray[this.inputs[1].getWireId()];
/*    */     
/* 59 */     Wire out = this.outputs[0].copy();
/*    */     
/* 61 */     wireArray[out.getWireId()] = out;
/* 62 */     return new MulBasicOp(in, in2, out, new String[] { this.desc });
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\primitive\MulBasicOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */