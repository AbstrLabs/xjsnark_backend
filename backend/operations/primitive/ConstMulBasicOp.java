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
/*    */ 
/*    */ public class ConstMulBasicOp
/*    */   extends BasicOp
/*    */ {
/*    */   private BigInteger constInteger;
/*    */   private boolean inSign;
/*    */   
/*    */   public ConstMulBasicOp(Wire w, Wire out, BigInteger constInteger, String... desc) {
/* 19 */     super(new Wire[] { w }, new Wire[] { out }, desc);
/* 20 */     this.inSign = (constInteger.signum() == -1);
/* 21 */     if (!this.inSign) {
/* 22 */       constInteger = constInteger.mod(Config.getFiniteFieldModulus());
/* 23 */       this.constInteger = constInteger;
/*    */     } else {
/* 25 */       constInteger = constInteger.negate();
/* 26 */       constInteger = constInteger.mod(Config.getFiniteFieldModulus());
/* 27 */       this.constInteger = Config.getFiniteFieldModulus().subtract(constInteger);
/*    */     } 
/*    */   }
/*    */   
/*    */   public String getOpcode() {
/* 32 */     if (!this.inSign) {
/* 33 */       return "const-mul-" + this.constInteger.toString(16);
/*    */     }
/* 35 */     return "const-mul-neg-" + Config.getFiniteFieldModulus().subtract(this.constInteger).toString(16);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void compute(BigInteger[] assignment) {
/* 41 */     BigInteger result = assignment[this.inputs[0].getWireId()].multiply(this.constInteger);
/* 42 */     if (result.bitLength() >= Config.getNumBitsFiniteFieldModulus()) {
/* 43 */       result = result.mod(Config.getFiniteFieldModulus());
/*    */     }
/* 45 */     assignment[this.outputs[0].getWireId()] = result;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 51 */     if (cachingDisabledForLinearOps)
/* 52 */       return false; 
/* 53 */     if (this == obj)
/* 54 */       return true; 
/* 55 */     if (!(obj instanceof ConstMulBasicOp)) {
/* 56 */       return false;
/*    */     }
/* 58 */     ConstMulBasicOp op = (ConstMulBasicOp)obj;
/* 59 */     return (this.inputs[0].equals(op.inputs[0]) && this.constInteger.equals(op.constInteger));
/*    */   }
/*    */ 
/*    */   
/*    */   public int getNumMulGates() {
/* 64 */     return 0;
/*    */   }
/*    */   
/*    */   public BigInteger getConstInteger() {
/* 68 */     return this.constInteger;
/*    */   }
/*    */ 
/*    */   
/*    */   public Instruction copy(Wire[] wireArray) {
/* 73 */     Wire in = wireArray[this.inputs[0].getWireId()];
/*    */     
/* 75 */     Wire out = this.outputs[0].copy();
/* 76 */     wireArray[out.getWireId()] = out;
/* 77 */     return new ConstMulBasicOp(in, out, this.constInteger, new String[] { this.desc });
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 82 */     int h = this.constInteger.hashCode(); byte b; int i; Wire[] arrayOfWire;
/* 83 */     for (i = (arrayOfWire = this.inputs).length, b = 0; b < i; ) { Wire in = arrayOfWire[b];
/* 84 */       h += in.hashCode(); b++; }
/*    */     
/* 86 */     return h;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\primitive\ConstMulBasicOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */