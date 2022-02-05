/*    */ package backend.resource;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import java.util.concurrent.ConcurrentMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ResourceBundle
/*    */ {
/* 13 */   public ConcurrentMap<BigInteger, BigInteger> bigIntegerSet = new ConcurrentHashMap<>();
/*    */   private static ResourceBundle instance;
/*    */   
/*    */   public static ResourceBundle getInstance() {
/* 17 */     if (instance == null) {
/* 18 */       instance = new ResourceBundle();
/*    */     }
/* 20 */     return instance;
/*    */   }
/*    */ 
/*    */   
/*    */   public BigInteger getBigInteger(BigInteger x) {
/* 25 */     this.bigIntegerSet.putIfAbsent(x, x);
/* 26 */     return this.bigIntegerSet.get(x);
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\resource\ResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */