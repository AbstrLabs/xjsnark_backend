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
/*    */ public class AddBasicOp
/*    */   extends BasicOp
/*    */ {
/*    */   public AddBasicOp(Wire[] ws, Wire output, String... desc) {
/* 15 */     super(ws, new Wire[] { output }, desc);
/*    */   }
/*    */   
/*    */   public String getOpcode() {
/* 19 */     return "add";
/*    */   }
/*    */ 
/*    */   
/*    */   public void compute(BigInteger[] assignment) {
/* 24 */     BigInteger s = BigInteger.ZERO; byte b; int i; Wire[] arrayOfWire;
/* 25 */     for (i = (arrayOfWire = this.inputs).length, b = 0; b < i; ) { Wire w = arrayOfWire[b];
/* 26 */       s = s.add(assignment[w.getWireId()]); b++; }
/*    */     
/* 28 */     assignment[this.outputs[0].getWireId()] = s.mod(Config.getFiniteFieldModulus());
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 33 */     if (cachingDisabledForLinearOps)
/* 34 */       return false; 
/* 35 */     if (this == obj)
/* 36 */       return true; 
/* 37 */     if (!(obj instanceof AddBasicOp)) {
/* 38 */       return false;
/*    */     }
/* 40 */     AddBasicOp op = (AddBasicOp)obj;
/* 41 */     if (op.inputs.length != this.inputs.length) {
/* 42 */       return false;
/*    */     }
/*    */     
/* 45 */     if (this.inputs.length == 2) {
/* 46 */       boolean check1 = (this.inputs[0].equals(op.inputs[0]) && 
/* 47 */         this.inputs[1].equals(op.inputs[1]));
/* 48 */       boolean check2 = (this.inputs[1].equals(op.inputs[0]) && 
/* 49 */         this.inputs[0].equals(op.inputs[1]));
/* 50 */       return !(!check1 && !check2);
/*    */     } 
/* 52 */     boolean check = true;
/* 53 */     for (int i = 0; i < this.inputs.length; i++) {
/* 54 */       check = (check && this.inputs[i].equals(op.inputs[i]));
/*    */     }
/* 56 */     return check;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getNumMulGates() {
/* 62 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public Instruction copy(Wire[] wireArray) {
/* 67 */     Wire[] ws = new Wire[this.inputs.length];
/* 68 */     for (int i = 0; i < ws.length; i++) {
/* 69 */       ws[i] = wireArray[this.inputs[i].getWireId()];
/*    */     }
/* 71 */     Wire out = this.outputs[0].copy();
/* 72 */     wireArray[out.getWireId()] = out;
/* 73 */     return new AddBasicOp(ws, out, new String[] { this.desc });
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\primitive\AddBasicOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */