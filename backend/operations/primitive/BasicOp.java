/*     */ package backend.operations.primitive;
/*     */ 
/*     */ import backend.eval.CircuitEvaluator;
/*     */ import backend.eval.Instruction;
/*     */ import backend.structure.CircuitGenerator;
/*     */ import backend.structure.Wire;
/*     */ import java.math.BigInteger;
/*     */ import util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class BasicOp
/*     */   implements Instruction
/*     */ {
/*     */   protected Wire[] inputs;
/*     */   protected Wire[] outputs;
/*     */   protected String desc;
/*     */   protected static boolean cachingDisabledForLinearOps = false;
/*     */   
/*     */   public static void setDisableCachingForLinearOps(boolean arg) {
/*  23 */     cachingDisabledForLinearOps = arg;
/*     */   }
/*     */   
/*     */   public BasicOp(Wire[] inputs, Wire[] outputs, String... desc) {
/*  27 */     this.inputs = inputs;
/*  28 */     this.outputs = outputs;
/*  29 */     if (desc.length > 0) {
/*  30 */       this.desc = desc[0];
/*     */     } else {
/*  32 */       this.desc = "";
/*     */     } 
/*  34 */     if (CircuitGenerator.__getActiveCircuitGenerator().__getPhase() == 1) {
/*     */       byte b1; int j; Wire[] arrayOfWire1;
/*  36 */       for (j = (arrayOfWire1 = inputs).length, b1 = 0; b1 < j; ) { Wire w = arrayOfWire1[b1];
/*  37 */         if (w == null) {
/*  38 */           System.err.println("One of the input wires is null: " + this);
/*  39 */           throw new NullPointerException("A null wire");
/*  40 */         }  if (w.getWireId() == -1) {
/*  41 */           System.err.println("One of the input wires is not packed: " + this);
/*  42 */           throw new IllegalArgumentException("A wire with a negative id");
/*     */         }  b1++; }
/*     */     
/*     */     }  byte b; int i; Wire[] arrayOfWire;
/*  46 */     for (i = (arrayOfWire = outputs).length, b = 0; b < i; ) { Wire w = arrayOfWire[b];
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  51 */       if (w == null && CircuitGenerator.__getActiveCircuitGenerator().__getPhase() == 1) {
/*  52 */         System.err.println("One of the output wires is null" + this);
/*  53 */         throw new NullPointerException("A null wire");
/*     */       } 
/*  55 */       if (!(this instanceof AssertBasicOp)) {
/*  56 */         w.setSrcInstruction(this);
/*     */       }
/*     */       b++; }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public BasicOp(Wire[] inputs, Wire[] outputs) {
/*  64 */     this(inputs, outputs, new String[] { "" });
/*     */   }
/*     */   
/*     */   public void evaluate(CircuitEvaluator evaluator) {
/*  68 */     BigInteger[] assignment = evaluator.getAssignment();
/*  69 */     checkInputs(assignment);
/*  70 */     checkOutputs(assignment);
/*  71 */     compute(assignment); } protected void checkInputs(BigInteger[] assignment) {
/*     */     byte b;
/*     */     int i;
/*     */     Wire[] arrayOfWire;
/*  75 */     for (i = (arrayOfWire = this.inputs).length, b = 0; b < i; ) { Wire w = arrayOfWire[b];
/*  76 */       if (assignment[w.getWireId()] == null) {
/*  77 */         System.err.println("Error - The inWire " + w + " has not been assigned\n" + this);
/*  78 */         throw new RuntimeException("Error During Evaluation");
/*     */       } 
/*     */       b++; }
/*     */   
/*     */   } protected abstract void compute(BigInteger[] paramArrayOfBigInteger); protected void checkOutputs(BigInteger[] assignment) {
/*     */     byte b;
/*     */     int i;
/*     */     Wire[] arrayOfWire;
/*  86 */     for (i = (arrayOfWire = this.outputs).length, b = 0; b < i; ) { Wire w = arrayOfWire[b];
/*  87 */       if (assignment[w.getWireId()] != null) {
/*  88 */         System.err.println("Error - The outWire " + w + " has already been assigned\n" + this);
/*  89 */         throw new RuntimeException("Error During Evaluation");
/*     */       } 
/*     */       b++; }
/*     */   
/*     */   }
/*     */   public abstract String getOpcode();
/*     */   
/*     */   public abstract int getNumMulGates();
/*     */   
/*     */   public String toString() {
/*  99 */     return String.valueOf(getOpcode()) + " in " + this.inputs.length + " <" + Util.arrayToString(this.inputs, " ") + "> out " + this.outputs.length + 
/* 100 */       " <" + Util.arrayToString(this.outputs, " ") + ">" + ((this.desc.length() > 0) ? (" \t\t# " + this.desc) : "");
/*     */   }
/*     */   
/*     */   public Wire[] getInputs() {
/* 104 */     return this.inputs;
/*     */   }
/*     */   
/*     */   public Wire[] getOutputs() {
/* 108 */     return this.outputs;
/*     */   }
/*     */   
/*     */   public boolean doneWithinCircuit() {
/* 112 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 117 */     int h = getOpcode().hashCode(); byte b; int i; Wire[] arrayOfWire;
/* 118 */     for (i = (arrayOfWire = this.inputs).length, b = 0; b < i; ) { Wire in = arrayOfWire[b];
/* 119 */       h += in.hashCode(); b++; }
/*     */     
/* 121 */     return h;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 128 */     if (this == obj) {
/* 129 */       return true;
/*     */     }
/* 131 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire[] getUsedWires() {
/* 138 */     return Util.concat(this.inputs, this.outputs);
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\operations\primitive\BasicOp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */