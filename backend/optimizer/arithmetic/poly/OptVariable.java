/*    */ package backend.optimizer.arithmetic.poly;
/*    */ 
/*    */ public class OptVariable
/*    */ {
/*    */   String label;
/*  6 */   int id = -1;
/*    */   boolean isBit;
/*    */   
/*    */   public int hashCode() {
/* 10 */     return this.label.hashCode() + this.id;
/*    */   }
/*    */ 
/*    */   
/*    */   public OptVariable(String label, int id, boolean isBit) {
/* 15 */     this.label = label;
/* 16 */     this.id = id;
/* 17 */     this.isBit = isBit;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public OptVariable(String fullIdentifier) {
/* 23 */     StringBuilder b = new StringBuilder();
/* 24 */     int s = -1;
/* 25 */     for (int i = 0; i < fullIdentifier.length(); i++) {
/* 26 */       char c = fullIdentifier.charAt(i);
/* 27 */       if (c >= '0' && fullIdentifier.charAt(i) <= '9') {
/* 28 */         s = i;
/*    */         break;
/*    */       } 
/* 31 */       b.append(c);
/*    */     } 
/*    */ 
/*    */     
/* 35 */     this.label = b.toString();
/* 36 */     this.id = Integer.parseInt(fullIdentifier.substring(s));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public OptVariable(String label, int id) {
/* 44 */     this.label = label;
/* 45 */     this.id = id;
/*    */   }
/*    */   
/*    */   public boolean equals(Object o) {
/* 49 */     if (o == this)
/* 50 */       return true; 
/* 51 */     if (o instanceof OptVariable) {
/* 52 */       return (this.label.equals(((OptVariable)o).label) && this.id == ((OptVariable)o).id);
/*    */     }
/*    */     
/* 55 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 61 */     if (this.label == null)
/* 62 */       return "null"; 
/* 63 */     return String.valueOf(this.label) + this.id;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getLabel() {
/* 70 */     return String.valueOf(this.label) + this.id;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isBit() {
/* 77 */     return this.isBit;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\poly\OptVariable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */