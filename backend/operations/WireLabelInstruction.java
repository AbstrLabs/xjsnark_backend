/*     */ package backend.operations;
/*     */ 
/*     */ import backend.auxTypes.IAuxType;
/*     */ import backend.auxTypes.PackedValue;
/*     */ import backend.auxTypes.UnsignedInteger;
/*     */ import backend.config.Config;
/*     */ import backend.eval.CircuitEvaluator;
/*     */ import backend.eval.Instruction;
/*     */ import backend.structure.Wire;
/*     */ 
/*     */ public class WireLabelInstruction
/*     */   implements Instruction
/*     */ {
/*     */   private LabelType type;
/*     */   
/*     */   public enum LabelType
/*     */   {
/*  18 */     input, output, nizkinput, debug;
/*     */   }
/*     */ 
/*     */   
/*  22 */   private Wire w = null;
/*     */   
/*     */   private IAuxType t;
/*     */   
/*     */   private PackedValue p;
/*     */   private String desc;
/*     */   
/*     */   public WireLabelInstruction(LabelType type, Wire w, String... desc) {
/*  30 */     this.type = type;
/*  31 */     this.w = w;
/*  32 */     if (desc.length > 0) {
/*  33 */       this.desc = desc[0];
/*     */     } else {
/*  35 */       this.desc = "";
/*     */     } 
/*     */   }
/*     */   
/*     */   public WireLabelInstruction(LabelType type, IAuxType t, String... desc) {
/*  40 */     this.type = type;
/*  41 */     this.t = t;
/*  42 */     if (desc.length > 0) {
/*  43 */       this.desc = desc[0];
/*     */     } else {
/*  45 */       this.desc = "";
/*     */     } 
/*     */   }
/*     */   
/*     */   public WireLabelInstruction(LabelType type, PackedValue p, String... desc) {
/*  50 */     this.type = type;
/*  51 */     this.p = p;
/*  52 */     if (desc.length > 0) {
/*  53 */       this.desc = desc[0];
/*     */     } else {
/*  55 */       this.desc = "";
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire getWire() {
/*  61 */     return this.w;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  65 */     return this.type + " " + this.w + ((this.desc.length() == 0) ? "" : ("\t\t\t # " + this.desc));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void evaluate(CircuitEvaluator evaluator) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void emit(CircuitEvaluator evaluator) {
/*  75 */     if ((this.type == LabelType.input && Config.inputVerbose) || (this.type == LabelType.output && Config.outputVerbose) || (this.type == LabelType.debug && Config.debugVerbose)) {
/*  76 */       if (this.w != null) {
/*  77 */         if (evaluator.getWireValue(this.w) == null && (this.type == LabelType.input || this.type == LabelType.nizkinput)) {
/*  78 */           throw new RuntimeException("Errors: Some inputs are without values  (" + this.desc + ")");
/*     */         }
/*  80 */         System.out.println("\t[" + this.type + "] Value of Wire # " + this.w + ((this.desc.length() > 0) ? (" (" + this.desc + ")") : "") + " :: " + 
/*  81 */             evaluator.getWireValue(this.w).toString(Config.hexOutputEnabled ? 16 : 10));
/*  82 */       } else if (this.t != null) {
/*  83 */         System.out.println("\t[" + this.type + "] Value of Object  " + ((this.desc.length() > 0) ? (" (" + this.desc + ")") : "") + " :: " + 
/*  84 */             this.t.getValueFromEvaluator(evaluator).toString(Config.hexOutputEnabled ? 16 : 10));
/*     */       } else {
/*  86 */         System.out.println("\t[" + this.type + "] Value of PackedValue  " + ((this.desc.length() > 0) ? (" (" + this.desc + ")") : "") + " :: " + 
/*  87 */             evaluator.getWireValue(this.p, UnsignedInteger.BITWIDTH_PER_CHUNK).toString(Config.hexOutputEnabled ? 16 : 10));
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public LabelType getType() {
/*  93 */     return this.type;
/*     */   }
/*     */   
/*     */   public boolean doneWithinCircuit() {
/*  97 */     return (this.type != LabelType.debug);
/*     */   }
/*     */   
/*     */   public Wire[] getUsedWires() {
/* 101 */     return new Wire[] { this.w };
/*     */   }
/*     */ 
/*     */   
/*     */   public Instruction copy(Wire[] wireArray) {
/* 106 */     if (this.w != null) {
/*     */       
/* 108 */       if (this.type == LabelType.input || this.type == LabelType.nizkinput) {
/*     */         
/* 110 */         if (this.w.getWireId() != 0) {
/* 111 */           Wire wire = this.w.copy();
/* 112 */           wireArray[wire.getWireId()] = wire;
/* 113 */           return new WireLabelInstruction(this.type, wire, new String[] { this.desc });
/*     */         } 
/* 115 */         return new WireLabelInstruction(this.type, wireArray[0], new String[] { this.desc });
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 120 */       Wire newWire = wireArray[this.w.getWireId()];
/* 121 */       return new WireLabelInstruction(this.type, newWire, new String[] { this.desc });
/*     */     } 
/*     */     
/* 124 */     return null;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\WireLabelInstruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */