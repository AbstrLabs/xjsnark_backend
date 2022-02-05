/*     */ package backend.auxTypes;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Stack;
/*     */ 
/*     */ public class ConditionalScopeTracker
/*     */ {
/*  11 */   private static int currentScopeId = 0;
/*     */   
/*     */   private static ConditionalStatementData currentConditionalStmtData;
/*  14 */   private static Hashtable<Integer, LinkedHashSet<ConditionalScopeImpactedType>> table = new Hashtable<>();
/*  15 */   private static Stack<ConditionalStatementData> condDataStack = new Stack<>();
/*     */ 
/*     */   
/*     */   public static class ConditionalStatementData
/*     */   {
/*  20 */     private ArrayList<Bit> activeIndividualBitList = new ArrayList<>();
/*     */     void push(Bit active) {
/*  22 */       this.activeIndividualBitList.add(active.copy());
/*     */     }
/*     */     
/*     */     ArrayList<Bit> getBitList() {
/*  26 */       return this.activeIndividualBitList;
/*     */     }
/*     */     
/*     */     Bit getCurrentActiveBit() {
/*  30 */       return this.activeIndividualBitList.get(this.activeIndividualBitList.size() - 1);
/*     */     }
/*     */     
/*     */     int getSubscopeId() {
/*  34 */       return this.activeIndividualBitList.size() - 1;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void push(Bit active) {
/*  42 */     currentConditionalStmtData.push(active.copy());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void pop() {
/*  56 */     HashSet<ConditionalScopeImpactedType> list = table.get(Integer.valueOf(currentScopeId));
/*     */ 
/*     */     
/*  59 */     for (ConditionalScopeImpactedType t : list)
/*     */     {
/*  61 */       t.pop(currentConditionalStmtData.getSubscopeId());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static Bit getIndividualActiveBit() {
/*  67 */     return currentConditionalStmtData.activeIndividualBitList.get(condDataStack.size() - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static Bit getAccumActiveBit() {
/*  73 */     Bit result = new Bit(true);
/*  74 */     for (ConditionalStatementData condData : condDataStack) {
/*  75 */       Bit activeCondition = new Bit(true);
/*  76 */       for (int i = 0; i < condData.activeIndividualBitList.size() - 1; i++) {
/*  77 */         activeCondition = activeCondition.mul(((Bit)condData.activeIndividualBitList.get(i)).inv());
/*     */       }
/*  79 */       activeCondition = activeCondition.mul(condData.getCurrentActiveBit());
/*     */       
/*  81 */       result = result.mul(activeCondition);
/*     */     } 
/*     */ 
/*     */     
/*  85 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static void pushMain() {
/*  91 */     currentConditionalStmtData = new ConditionalStatementData();
/*  92 */     condDataStack.push(currentConditionalStmtData);
/*     */     
/*  94 */     currentScopeId++;
/*  95 */     LinkedHashSet<ConditionalScopeImpactedType> list = new LinkedHashSet<>();
/*  96 */     table.put(Integer.valueOf(currentScopeId), list);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void popMain() {
/* 106 */     HashSet<ConditionalScopeImpactedType> list = table.get(Integer.valueOf(currentScopeId));
/* 107 */     condDataStack.pop();
/*     */     
/* 109 */     for (ConditionalScopeImpactedType t : list)
/*     */     {
/* 111 */       t.popMain();
/*     */     }
/* 113 */     currentConditionalStmtData = condDataStack.isEmpty() ? null : condDataStack.peek();
/* 114 */     table.remove(Integer.valueOf(currentScopeId));
/*     */ 
/*     */     
/* 117 */     currentScopeId--;
/*     */   }
/*     */   
/*     */   public static ConditionalStatementData getCurrentConditionalStmtData() {
/* 121 */     return currentConditionalStmtData;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getCurrentScopeId() {
/* 127 */     return currentScopeId;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void register(ConditionalScopeImpactedType v, int originalScopeId) {
/* 136 */     int s = currentScopeId;
/* 137 */     for (int i = s; i > originalScopeId; i--)
/* 138 */       ((LinkedHashSet<ConditionalScopeImpactedType>)table.get(Integer.valueOf(i))).add(v); 
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\ConditionalScopeTracker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */