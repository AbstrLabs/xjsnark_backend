/*    */ package backend.eval;
/*    */ 
/*    */ public class SampleRun
/*    */ {
/*    */   protected String name;
/*    */   protected boolean enabled;
/*    */   protected boolean exceptionThrown;
/*    */   
/*    */   public SampleRun(String name, boolean enabled) {
/* 10 */     this.name = name;
/* 11 */     this.enabled = enabled;
/* 12 */     this.exceptionThrown = false;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void pre() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void post() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 26 */     return this.name;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEnabled() {
/* 31 */     return this.enabled;
/*    */   }
/*    */   
/*    */   public boolean hasExceptionThrown() {
/* 35 */     return this.exceptionThrown;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\eval\SampleRun.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */