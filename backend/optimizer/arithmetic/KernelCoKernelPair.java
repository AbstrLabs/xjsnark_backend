/*    */ package backend.optimizer.arithmetic;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class KernelCoKernelPair
/*    */ {
/*    */   private ExpressionMatrix kernel;
/*    */   private Cube coKernel;
/*    */   
/*    */   public KernelCoKernelPair(ExpressionMatrix exp, Cube cube) {
/* 15 */     this.kernel = exp;
/* 16 */     this.coKernel = cube;
/*    */   }
/*    */   
/*    */   public int hashCode() {
/* 20 */     return this.coKernel.hashCode() + this.kernel.hashCode();
/*    */   }
/*    */   
/*    */   public boolean equals(Object o) {
/* 24 */     if (o == this)
/* 25 */       return true; 
/* 26 */     if (!(o instanceof KernelCoKernelPair)) {
/* 27 */       return false;
/*    */     }
/*    */     
/* 30 */     KernelCoKernelPair other = (KernelCoKernelPair)o;
/* 31 */     ArrayList<Cube> pwrs1 = this.kernel.getPowers();
/* 32 */     ArrayList<Cube> pwrs2 = other.kernel.getPowers();
/* 33 */     boolean checkLengths = (pwrs1.size() == pwrs2.size());
/*    */     
/* 35 */     boolean checkVars = Arrays.equals((Object[])this.kernel.getLiterals(), (Object[])other.kernel.getLiterals());
/* 36 */     if (checkLengths && checkVars) {
/* 37 */       boolean check = true;
/* 38 */       int size = pwrs1.size();
/* 39 */       for (int i = 0; i < size; i++) {
/* 40 */         check = (check && ((Cube)pwrs1.get(i)).equals(pwrs2.get(i)));
/*    */       }
/* 42 */       return (other.coKernel.equals(this.coKernel) && check);
/*    */     } 
/*    */     
/* 45 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public ExpressionMatrix getKernel() {
/* 51 */     return this.kernel;
/*    */   }
/*    */   
/*    */   public Cube getCoKernel() {
/* 55 */     return this.coKernel;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\KernelCoKernelPair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */