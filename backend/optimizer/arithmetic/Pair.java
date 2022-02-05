/*    */ package backend.optimizer.arithmetic;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class Pair
/*    */ {
/*    */   private int row;
/*    */   private int col;
/*    */   
/*    */   public Pair(int row, int col) {
/* 11 */     this.row = row;
/* 12 */     this.col = col;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 17 */     if (this == o)
/* 18 */       return true; 
/* 19 */     if (!(o instanceof Pair)) {
/* 20 */       return false;
/*    */     }
/* 22 */     Pair other = (Pair)o;
/* 23 */     return (other.row == this.row && other.col == this.col);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 28 */     return Arrays.hashCode(new int[] { this.row, this.col });
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\Pair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */