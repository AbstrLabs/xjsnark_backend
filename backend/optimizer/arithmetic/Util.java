/*    */ package backend.optimizer.arithmetic;
/*    */ 
/*    */ import java.lang.reflect.Array;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashSet;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class Util
/*    */ {
/*    */   public static <T> T[] concatenate(Object[] a, Object[] b) {
/* 12 */     int aLen = a.length;
/* 13 */     int bLen = b.length;
/*    */     
/* 15 */     Object[] c = (Object[])Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
/* 16 */     System.arraycopy(a, 0, c, 0, aLen);
/* 17 */     System.arraycopy(b, 0, c, aLen, bLen);
/*    */     
/* 19 */     return (T[])c;
/*    */   }
/*    */   
/*    */   public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
/* 23 */     Set<Set<T>> sets = new HashSet<>();
/* 24 */     if (originalSet.isEmpty()) {
/* 25 */       sets.add(new HashSet<>());
/* 26 */       return sets;
/*    */     } 
/* 28 */     List<T> list = new ArrayList<>(originalSet);
/* 29 */     T head = list.get(0);
/* 30 */     Set<T> rest = new HashSet<>(list.subList(1, list.size()));
/* 31 */     for (Set<T> set : powerSet(rest)) {
/* 32 */       Set<T> newSet = new HashSet<>();
/* 33 */       newSet.add(head);
/* 34 */       newSet.addAll(set);
/* 35 */       sets.add(newSet);
/* 36 */       sets.add(set);
/*    */     } 
/* 38 */     return sets;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */