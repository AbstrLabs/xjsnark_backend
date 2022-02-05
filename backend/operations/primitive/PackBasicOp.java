/*    */ package backend.operations.primitive;
/*    */ 
/*    */ import backend.config.Config;
/*    */ import backend.eval.Instruction;
/*    */ import backend.structure.Wire;
/*    */ import java.math.BigInteger;
/*    */ import util.Util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PackBasicOp
/*    */   extends BasicOp
/*    */ {
/*    */   public PackBasicOp(Wire[] inBits, Wire out, String... desc) {
/* 16 */     super(inBits, new Wire[] { out }, desc);
/*    */   }
/*    */   
/*    */   public String getOpcode() {
/* 20 */     return "pack";
/*    */   }
/*    */ 
/*    */   
/*    */   public void checkInputs(BigInteger[] assignment) {
/* 25 */     super.checkInputs(assignment);
/* 26 */     boolean check = true;
/* 27 */     for (int i = 0; i < this.inputs.length; i++) {
/* 28 */       check &= Util.isBinary(assignment[this.inputs[i].getWireId()]);
/*    */     }
/* 30 */     if (!check) {
/* 31 */       System.err.println("Error - Input(s) to Pack are not binary. " + 
/* 32 */           this);
/* 33 */       throw new RuntimeException("Error During Evaluation");
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void compute(BigInteger[] assignment) {
/* 40 */     BigInteger sum = BigInteger.ZERO;
/* 41 */     for (int i = 0; i < this.inputs.length; i++) {
/* 42 */       sum = sum.add(assignment[this.inputs[i].getWireId()]
/* 43 */           .multiply((new BigInteger("2")).pow(i)));
/*    */     }
/* 45 */     assignment[this.outputs[0].getWireId()] = sum.mod(Config.getFiniteFieldModulus());
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 50 */     if (cachingDisabledForLinearOps)
/* 51 */       return false; 
/* 52 */     if (this == obj)
/* 53 */       return true; 
/* 54 */     if (!(obj instanceof PackBasicOp)) {
/* 55 */       return false;
/*    */     }
/* 57 */     PackBasicOp op = (PackBasicOp)obj;
/* 58 */     if (op.inputs.length != this.inputs.length) {
/* 59 */       return false;
/*    */     }
/* 61 */     boolean check = true;
/* 62 */     for (int i = 0; i < this.inputs.length; i++) {
/* 63 */       check = (check && this.inputs[i].equals(op.inputs[i]));
/*    */     }
/* 65 */     return check;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getNumMulGates() {
/* 70 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public Instruction copy(Wire[] wireArray) {
/* 75 */     Wire[] ws = new Wire[this.inputs.length];
/* 76 */     for (int i = 0; i < ws.length; i++) {
/* 77 */       ws[i] = wireArray[this.inputs[i].getWireId()];
/*    */     }
/*    */     
/* 80 */     Wire out = this.outputs[0].copy();
/*    */     
/* 82 */     wireArray[out.getWireId()] = out;
/* 83 */     return new PackBasicOp(ws, out, new String[] { this.desc });
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\primitive\PackBasicOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */