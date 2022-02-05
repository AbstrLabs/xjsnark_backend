/*     */ package backend.auxTypes;
/*     */ 
/*     */ 
/*     */ public class VariableState
/*     */ {
/*     */   private boolean packedAhead;
/*     */   private boolean splittedAhead;
/*     */   private boolean conditionallySplittedAhead;
/*     */   private boolean conditionallySplittedAndAlignedAhead;
/*     */   private boolean mustBeWithinRange;
/*     */   private int mulIndex;
/*     */   private int mulUseCount;
/*     */   private int addUseCount;
/*  14 */   private int thresholdBitwidth = -1;
/*     */   
/*     */   private VariableState[] prevStates;
/*     */   
/*     */   private boolean isMulOutput;
/*     */   
/*     */   private int id;
/*     */   
/*     */   private int expectedBitwidth;
/*     */ 
/*     */   
/*     */   public boolean isPackedAhead() {
/*  26 */     return this.packedAhead;
/*     */   }
/*     */   
/*     */   public void setPackedAhead(boolean packedAhead) {
/*  30 */     this.packedAhead = packedAhead;
/*     */   }
/*     */   
/*     */   public boolean isSplittedAhead() {
/*  34 */     return this.splittedAhead;
/*     */   }
/*     */   
/*     */   public void setSplittedAhead(boolean splittedAhead) {
/*  38 */     this.splittedAhead = splittedAhead;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isConditionallySplittedAhead() {
/*  58 */     return this.conditionallySplittedAhead;
/*     */   }
/*     */   
/*     */   public void setConditionallySplittedAhead(boolean conditionallySplittedAhead) {
/*  62 */     this.conditionallySplittedAhead = conditionallySplittedAhead;
/*     */   }
/*     */   
/*     */   public int getMulIndex() {
/*  66 */     return this.mulIndex;
/*     */   }
/*     */   
/*     */   public void setMulIndex(int mulIndex) {
/*  70 */     this.mulIndex = mulIndex;
/*     */   }
/*     */   
/*     */   public void incMulIndex() {
/*  74 */     this.mulIndex++;
/*     */   }
/*     */ 
/*     */   
/*     */   public void incMulUseCount() {
/*  79 */     this.mulUseCount++;
/*     */   }
/*     */   
/*     */   public void decMulUseCount() {
/*  83 */     this.mulUseCount--;
/*     */   }
/*     */   
/*     */   public void incAddUseCount() {
/*  87 */     this.addUseCount++;
/*     */   }
/*     */ 
/*     */   
/*     */   public void decAddUseCount() {
/*  92 */     this.addUseCount--;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isConditionallySplittedAndAlignedAhead() {
/*  97 */     return this.conditionallySplittedAndAlignedAhead;
/*     */   }
/*     */   
/*     */   public boolean isMustBeWithinRange() {
/* 101 */     return this.mustBeWithinRange;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setConditionallySplittedAndAlignedAhead(boolean conditionallySplittedAndAlignedAhead) {
/* 106 */     this.conditionallySplittedAndAlignedAhead = conditionallySplittedAndAlignedAhead;
/*     */   }
/*     */   
/*     */   public void setMustBeWithinRange(boolean mustBeWithinRange) {
/* 110 */     this.mustBeWithinRange = mustBeWithinRange;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setOptimizationAttributes(VariableState st1, VariableState st2, boolean isMulOutput, int bitwidth) {
/* 115 */     this.prevStates = new VariableState[2];
/* 116 */     this.prevStates[0] = st1;
/* 117 */     this.prevStates[1] = st2;
/* 118 */     this.isMulOutput = isMulOutput;
/* 119 */     this.expectedBitwidth = bitwidth;
/*     */   }
/*     */   
/*     */   public VariableState[] getPrevStates() {
/* 123 */     return this.prevStates;
/*     */   }
/*     */   
/*     */   public void setPrevStates(VariableState[] prevStates) {
/* 127 */     this.prevStates = prevStates;
/*     */   }
/*     */   
/*     */   public boolean isMulOutput() {
/* 131 */     return this.isMulOutput;
/*     */   }
/*     */   
/*     */   public void setMulOutput(boolean isMulOutput) {
/* 135 */     this.isMulOutput = isMulOutput;
/*     */   }
/*     */   
/*     */   public int getId() {
/* 139 */     return this.id;
/*     */   }
/*     */   
/*     */   public void setId(int id) {
/* 143 */     this.id = id;
/*     */   }
/*     */   
/*     */   public int getThresholdBitwidth() {
/* 147 */     return this.thresholdBitwidth;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setThresholdBitwidth(int thresholdBitwidth) {
/* 152 */     if (this.thresholdBitwidth == -1 || this.thresholdBitwidth > thresholdBitwidth)
/* 153 */       this.thresholdBitwidth = thresholdBitwidth; 
/*     */   }
/*     */   
/*     */   public int getExpectedBitwidth() {
/* 157 */     return this.expectedBitwidth;
/*     */   }
/*     */   
/*     */   public void setExpectedBitwidth(int expectedBitwidth) {
/* 161 */     this.expectedBitwidth = expectedBitwidth;
/*     */   }
/*     */   
/*     */   public int hashCode() {
/* 165 */     return this.id;
/*     */   }
/*     */   
/*     */   public int getMulUseCount() {
/* 169 */     return this.mulUseCount;
/*     */   }
/*     */   
/*     */   public void setMulUseCount(int mulUseCount) {
/* 173 */     this.mulUseCount = mulUseCount;
/*     */   }
/*     */   
/*     */   public int getAddUseCount() {
/* 177 */     return this.addUseCount;
/*     */   }
/*     */   
/*     */   public void setAddUseCount(int addUseCount) {
/* 181 */     this.addUseCount = addUseCount;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\VariableState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */