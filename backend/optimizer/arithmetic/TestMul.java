/*    */ package backend.optimizer.arithmetic;
/*    */ 
/*    */ import java.util.Scanner;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TestMul
/*    */ {
/*    */   public static void main(String[] args) {
/* 11 */     Scanner scanner = new Scanner(System.in);
/*    */     
/* 13 */     long sum = 0L;
/* 14 */     int sum2 = 0;
/* 15 */     for (int k = 0; k < 1001; k++) {
/* 16 */       long t1 = System.currentTimeMillis();
/* 17 */       int v = 0;
/* 18 */       int v2 = 1;
/* 19 */       int v3 = 1;
/* 20 */       for (int i = 0; i < 1000000; i++) {
/* 21 */         int z1 = v * v;
/* 22 */         int x2 = 5 * z1;
/* 23 */         sum2 = sum2 + z1 + x2;
/* 24 */         v++;
/*    */       } 
/* 26 */       long t2 = System.currentTimeMillis();
/* 27 */       if (k != 0) {
/* 28 */         sum += t2 - t1;
/* 29 */         if (sum2 == 0)
/* 30 */           System.out.println(sum2); 
/*    */       } 
/*    */     } 
/* 33 */     System.out.println(sum / 1000.0D);
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\TestMul.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */