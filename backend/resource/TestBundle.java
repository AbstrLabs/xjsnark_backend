/*    */ package backend.resource;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ public class TestBundle
/*    */ {
/*    */   public static void main(String[] args) {
/*  8 */     int s = 100000;
/*  9 */     BigInteger[] a = new BigInteger[s];
/* 10 */     for (int i = 0; i < a.length; i++)
/*    */     {
/*    */       
/* 13 */       a[i] = ResourceBundle.getInstance().getBigInteger((
/* 14 */           new BigInteger("2")).pow(32).subtract(
/* 15 */             BigInteger.ONE));
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\resource\TestBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */