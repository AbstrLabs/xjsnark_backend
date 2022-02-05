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
/*    */ public class SplitBasicOp
/*    */   extends BasicOp
/*    */ {
/*    */   public SplitBasicOp(Wire w, Wire[] outs, String... desc) {
/* 15 */     super(new Wire[] { w }, outs, desc);
/*    */   }
/*    */   
/*    */   public String getOpcode() {
/* 19 */     return "split";
/*    */   }
/*    */   
/*    */   protected void checkInputs(BigInteger[] assignment) {
/* 23 */     super.checkInputs(assignment);
/* 24 */     if (this.outputs.length < assignment[this.inputs[0].getWireId()].bitLength()) {
/* 25 */       System.err
/* 26 */         .println("Error in Split --- The number of bits does not fit -- Input: " + 
/* 27 */           assignment[this.inputs[0].getWireId()].toString(16) + "\n\t" + this);
/*    */       
/* 29 */       throw new RuntimeException("Error During Evaluation -- " + this);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void compute(BigInteger[] assignment) {
/* 36 */     BigInteger inVal = assignment[this.inputs[0].getWireId()];
/* 37 */     if (inVal.compareTo(Config.getFiniteFieldModulus()) > 0) {
/* 38 */       inVal = inVal.mod(Config.getFiniteFieldModulus());
/*    */     }
/* 40 */     for (int i = 0; i < this.outputs.length; i++) {
/* 41 */       assignment[this.outputs[i].getWireId()] = inVal.testBit(i) ? BigInteger.ONE : 
/* 42 */         BigInteger.ZERO;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 49 */     if (this == obj)
/* 50 */       return true; 
/* 51 */     if (!(obj instanceof SplitBasicOp)) {
/* 52 */       return false;
/*    */     }
/* 54 */     SplitBasicOp op = (SplitBasicOp)obj;
/* 55 */     return (this.inputs[0].equals(op.inputs[0]) && this.outputs.length == op.outputs.length);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNumMulGates() {
/* 61 */     return this.outputs.length + 1;
/*    */   }
/*    */ 
/*    */   
/*    */   public Instruction copy(Wire[] wireArray) {
/* 66 */     Wire[] ws = new Wire[this.outputs.length];
/* 67 */     for (int i = 0; i < ws.length; i++) {
/*    */       
/* 69 */       ws[i] = this.outputs[i].copy();
/*    */       
/* 71 */       wireArray[ws[i].getWireId()] = ws[i];
/*    */     } 
/* 73 */     Wire in = wireArray[this.inputs[0].getWireId()];
/*    */     
/* 75 */     return new SplitBasicOp(in, ws, new String[] { this.desc });
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\primitive\SplitBasicOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */