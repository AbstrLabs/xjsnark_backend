/*     */ package backend.optimizer;
/*     */ 
/*     */ import backend.auxTypes.VariableState;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Queue;
/*     */ 
/*     */ 
/*     */ public class BitwidthOptimizer
/*     */ {
/*     */   private Collection<VariableState> varStates;
/*     */   
/*     */   private class StateRecord
/*     */   {
/*     */     private VariableState state;
/*     */     private boolean additionBranch;
/*     */     
/*     */     public StateRecord(VariableState state, boolean additionBranch) {
/*  22 */       this.state = state;
/*  23 */       this.additionBranch = additionBranch;
/*     */     }
/*     */     
/*     */     public int hashCode() {
/*  27 */       return this.state.getId();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/*  32 */       if (!(o instanceof StateRecord)) {
/*  33 */         return false;
/*     */       }
/*  35 */       return (((StateRecord)o).state.getId() == this.state.getId());
/*     */     } }
/*     */   
/*     */   private class Cluster { HashSet<VariableState> set;
/*     */     
/*     */     private Cluster() {}
/*     */     
/*  42 */     Cluster superCluster = null; }
/*     */ 
/*     */ 
/*     */   
/*     */   public BitwidthOptimizer(Collection<VariableState> varStates) {
/*  47 */     this.varStates = varStates;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reduceBitwidthAdjustmentCost() {
/*  52 */     ArrayList<VariableState> initialSet = new ArrayList<>();
/*  53 */     for (VariableState st : this.varStates) {
/*  54 */       if ((st.isConditionallySplittedAhead() || 
/*  55 */         st.isConditionallySplittedAndAlignedAhead() || 
/*  56 */         st.isSplittedAhead()) && 
/*  57 */         st.getPrevStates() != null) {
/*  58 */         initialSet.add(st);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  63 */     ArrayList<VariableState> nextStates = new ArrayList<>();
/*  64 */     ArrayList<VariableState> currentStates = initialSet;
/*     */     
/*  66 */     while (!currentStates.isEmpty()) {
/*     */       
/*  68 */       HashMap<VariableState, ArrayList<StateRecord>> outputInputMap = new HashMap<>();
/*  69 */       for (VariableState st : currentStates) {
/*  70 */         ArrayList<StateRecord> list = new ArrayList<>();
/*  71 */         outputInputMap.put(st, list);
/*  72 */         if (st.isMulOutput()) {
/*  73 */           if (isUndecidedState(st.getPrevStates()[0])) {
/*  74 */             nextStates.add(st.getPrevStates()[0]);
/*  75 */             list.add(new StateRecord(st.getPrevStates()[0], false));
/*     */           } 
/*  77 */           if (isUndecidedState(st.getPrevStates()[1])) {
/*  78 */             nextStates.add(st.getPrevStates()[1]);
/*  79 */             list.add(new StateRecord(st.getPrevStates()[1], false));
/*     */           } 
/*     */           continue;
/*     */         } 
/*  83 */         Queue<VariableState> q = new LinkedList<>(); byte b; int i; VariableState[] arrayOfVariableState;
/*  84 */         for (i = (arrayOfVariableState = st.getPrevStates()).length, b = 0; b < i; ) { VariableState p = arrayOfVariableState[b];
/*  85 */           if (isUndecidedState(p))
/*  86 */             q.add(p);  b++; }
/*     */         
/*  88 */         while (!q.isEmpty()) {
/*  89 */           VariableState intemediateSt = q.poll();
/*  90 */           if (intemediateSt.isMulOutput()) {
/*     */             VariableState[] arrayOfVariableState2;
/*  92 */             int k = (arrayOfVariableState2 = intemediateSt.getPrevStates()).length; i = 0; for (; i < k; i++) { VariableState p = arrayOfVariableState2[i];
/*  93 */               if (isUndecidedState(p))
/*  94 */                 list.add(new StateRecord(p, false));  }
/*     */              continue;
/*     */           }  VariableState[] arrayOfVariableState1;
/*  97 */           for (int j = (arrayOfVariableState1 = st.getPrevStates()).length; i < j; ) { VariableState p = arrayOfVariableState1[i];
/*  98 */             if (isUndecidedState(p)) {
/*  99 */               q.add(p);
/*     */             }
/*     */             
/*     */             i++; }
/*     */         
/*     */         } 
/*     */       } 
/* 106 */       HashMap<VariableState, HashSet<VariableState>> inputOutputMap = new HashMap<>();
/* 107 */       for (VariableState outState : outputInputMap.keySet()) {
/* 108 */         for (StateRecord inStateRecord : outputInputMap.get(outState)) {
/* 109 */           if (!inputOutputMap.containsKey(inStateRecord.state)) {
/* 110 */             inputOutputMap
/* 111 */               .put(inStateRecord.state, new HashSet<>());
/*     */           }
/* 113 */           ((HashSet<VariableState>)inputOutputMap.get(inStateRecord.state)).add(outState);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 118 */       HashMap<VariableState, Cluster> clusters = new HashMap<>();
/* 119 */       HashSet<VariableState> visitedSet = new HashSet<>();
/*     */       
/* 121 */       for (VariableState inState : inputOutputMap.keySet()) {
/* 122 */         if (!visitedSet.contains(inState)) {
/* 123 */           Cluster superCluster = new Cluster(null);
/* 124 */           HashSet<VariableState> superSet = new HashSet<>();
/* 125 */           superCluster.set = superSet;
/* 126 */           for (VariableState outState : inputOutputMap.get(inState)) {
/* 127 */             Cluster cluster = clusters.get(outState);
/* 128 */             if (cluster != null) {
/* 129 */               superSet.addAll(cluster.set);
/* 130 */               cluster.set = superSet;
/* 131 */               cluster.superCluster = superCluster; continue;
/*     */             } 
/* 133 */             superSet.add(outState);
/* 134 */             clusters.put(outState, superCluster);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 140 */       HashSet<Cluster> superClusters = new HashSet<>();
/* 141 */       for (Cluster c : clusters.values()) {
/* 142 */         if (c.superCluster == null) {
/* 143 */           superClusters.add(c);
/*     */         }
/*     */       } 
/*     */       
/* 147 */       for (Cluster c : superClusters) {
/* 148 */         HashSet<StateRecord> inAdditionStates = new HashSet<>();
/* 149 */         HashMap<StateRecord, Integer> inMulStates = new HashMap<>();
/*     */         
/* 151 */         for (VariableState outState : c.set) {
/* 152 */           for (StateRecord r : outputInputMap.get(outState)) {
/* 153 */             if (!r.additionBranch) {
/* 154 */               if (inMulStates.containsKey(r)) {
/* 155 */                 inMulStates.put(r, Integer.valueOf(((Integer)inMulStates.get(r)).intValue() + 1)); continue;
/*     */               } 
/* 157 */               inMulStates.put(r, Integer.valueOf(1));
/*     */               continue;
/*     */             } 
/* 160 */             inAdditionStates.add(r);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 167 */       currentStates = nextStates;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean isUndecidedState(VariableState st) {
/* 173 */     if (st.isConditionallySplittedAhead() || 
/* 174 */       st.isConditionallySplittedAndAlignedAhead() || 
/* 175 */       st.isSplittedAhead())
/* 176 */       return false; 
/* 177 */     if (st.getPrevStates() == null)
/* 178 */       return false; 
/* 179 */     return true;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\BitwidthOptimizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */