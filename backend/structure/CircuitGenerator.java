/*     */ package backend.structure;
/*     */ 
/*     */ import backend.auxTypes.Bit;
/*     */ import backend.auxTypes.ConditionalScopeTracker;
/*     */ import backend.auxTypes.FieldElement;
/*     */ import backend.auxTypes.IAuxType;
/*     */ import backend.auxTypes.PackedValue;
/*     */ import backend.auxTypes.RuntimeStruct;
/*     */ import backend.auxTypes.SmartMemory;
/*     */ import backend.auxTypes.UnsignedInteger;
/*     */ import backend.auxTypes.VariableState;
/*     */ import backend.config.Config;
/*     */ import backend.eval.CircuitEvaluator;
/*     */ import backend.eval.Instruction;
/*     */ import backend.eval.SampleRun;
/*     */ import backend.operations.WireLabelInstruction;
/*     */ import backend.operations.primitive.AssertBasicOp;
/*     */ import backend.operations.primitive.BasicOp;
/*     */ import backend.operations.primitive.MulBasicOp;
/*     */ import backend.optimizer.CircuitOptimizer;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.lang.reflect.Method;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import util.Util;
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
/*     */ public abstract class CircuitGenerator
/*     */ {
/*  46 */   private static ConcurrentHashMap<Long, CircuitGenerator> __activeCircuitGenerators = new ConcurrentHashMap<>();
/*     */   
/*     */   private static CircuitGenerator __instance;
/*     */   
/*     */   protected int __currentWireId;
/*     */   
/*     */   private LinkedHashMap<Instruction, Instruction> __evaluationQueue;
/*     */   
/*     */   private LinkedHashMap<Instruction, Instruction> __nonOptimizedEvaluationQueue;
/*     */   
/*     */   private int __nonOptimalWireCount;
/*     */   
/*     */   protected Wire __zeroWire;
/*     */   
/*     */   protected Wire __oneWire;
/*     */   
/*     */   protected ArrayList<Wire> __inWires;
/*     */   
/*     */   protected ArrayList<Wire> __outWires;
/*     */   
/*     */   protected ArrayList<Wire> __proverWitnessWires;
/*     */   
/*     */   protected ArrayList<IAuxType> __inputAux;
/*     */   
/*     */   protected ArrayList<IAuxType> __proverAux;
/*     */   
/*     */   protected ArrayList<IAuxType> __verifiedProverAux;
/*     */   
/*     */   protected String __circuitName;
/*     */   
/*     */   protected HashMap<BigInteger, Wire> __knownConstantWires;
/*     */   
/*     */   private int __numOfConstraints;
/*     */   private int __phase;
/*     */   private int __stateCounter;
/*     */   private int __stateCounterPhase1;
/*     */   private int __conditionCounter;
/*     */   private ArrayList<SmartMemory<?>> __memoryList;
/*     */   private HashMap<Integer, VariableState> __varVariableStateTable;
/*     */   private ArrayList<Boolean> __conditionalStateList;
/*     */   private HashMap<Integer, SmartMemory.MemoryState> __memoryStateTable;
/*     */   private ArrayList<Class<? extends RuntimeStruct>> __rumtimeClassesList;
/*     */   private boolean __untrackedStateObjects = false;
/*     */   private CircuitEvaluator __circuitEvaluator;
/*     */   private Instruction __lastInstructionAdded;
/*     */   private CircuitOptimizer __circuitOptimizer;
/*     */   
/*     */   public CircuitGenerator(String circuitName) {
/*  94 */     this.__circuitName = circuitName;
/*     */     
/*  96 */     __instance = this;
/*  97 */     this.__inWires = new ArrayList<>();
/*  98 */     this.__outWires = new ArrayList<>();
/*  99 */     this.__proverWitnessWires = new ArrayList<>();
/* 100 */     this.__evaluationQueue = new LinkedHashMap<>();
/* 101 */     this.__nonOptimizedEvaluationQueue = this.__evaluationQueue;
/* 102 */     this.__knownConstantWires = new HashMap<>();
/* 103 */     this.__currentWireId = 0;
/* 104 */     this.__numOfConstraints = 0;
/* 105 */     this.__inputAux = new ArrayList<>();
/* 106 */     this.__proverAux = new ArrayList<>();
/* 107 */     this.__verifiedProverAux = new ArrayList<>();
/* 108 */     this.__varVariableStateTable = new HashMap<>();
/* 109 */     this.__conditionalStateList = new ArrayList<>();
/* 110 */     this.__memoryStateTable = new HashMap<>();
/* 111 */     this.__memoryList = new ArrayList<>();
/* 112 */     this.__rumtimeClassesList = new ArrayList<>();
/* 113 */     this.__stateCounter = 0;
/* 114 */     this.__conditionCounter = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CircuitGenerator __getActiveCircuitGenerator() {
/* 123 */     return __instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void outsource() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final void __generateCircuit() {
/* 142 */     System.out.println("[1st Phase] Running Initial Circuit Analysis for < " + this.__circuitName + " >");
/* 143 */     __phase1();
/* 144 */     System.out.println("[2nd Phase] Running Circuit Generator for < " + this.__circuitName + " >");
/* 145 */     __phase2();
/*     */ 
/*     */ 
/*     */     
/* 149 */     if (Config.multivariateExpressionMinimization) {
/* 150 */       System.out.println("Initial Circuit Generation Done for < " + this.__circuitName + " >  \n \t Current total number of constraints :  " + __getNumOfConstraints() + "\n");
/* 151 */       System.out.println("Now: attempting to apply multivariate expression minimization (might take time/require memory depending on how large the circuit is)");
/* 152 */       if (!Config.arithOptimizerIncrementalMode)
/*     */       {
/* 154 */         System.out.println("** Note: If the size of memory is a bottleneck, e.g., the circuit size is very large, enabling Config.arithOptimizerIncrementalMode could help.");
/*     */       }
/*     */     } else {
/*     */       
/* 158 */       System.out.println("Circuit Generation Done for < " + this.__circuitName + " >  \n \t Total Number of Constraints :  " + __getNumOfConstraints() + "\n");
/*     */     } 
/*     */     
/* 161 */     this.__nonOptimalWireCount = this.__currentWireId;
/* 162 */     if (Config.multivariateExpressionMinimization) {
/* 163 */       this.__evaluationQueue = __copyEvalSeq(this.__evaluationQueue);
/*     */       
/* 165 */       this.__circuitOptimizer = new CircuitOptimizer(this);
/*     */     } 
/*     */ 
/*     */     
/* 169 */     if (Config.writeCircuits) {
/* 170 */       __writeCircuitFile(Config.multivariateExpressionMinimization ? "_optimized" : "");
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void __phase2() {
/* 176 */     __declareGenericConstants();
/* 177 */     __init();
/* 178 */     __defineInputs();
/* 179 */     __defineVerifiedWitnesses();
/* 180 */     __defineWitnesses();
/*     */     
/* 182 */     for (Class<? extends RuntimeStruct> c : this.__rumtimeClassesList) {
/*     */       try {
/* 184 */         Method m = c.getMethod("____reset", new Class[0]);
/* 185 */         m.invoke(null, (Object[])new String[0]);
/*     */       }
/* 187 */       catch (Exception e) {
/* 188 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/* 191 */     outsource();
/* 192 */     __checkWitnesses();
/*     */     
/* 194 */     __defineOutputs();
/* 195 */     for (SmartMemory<?> mem : this.__memoryList) {
/* 196 */       mem.finalize();
/*     */     }
/*     */     
/* 199 */     if (this.__stateCounter != this.__stateCounterPhase1) {
/* 200 */       System.err.println("Internal Inconsistency Detected! -- Inconsistent State Counters [" + this.__stateCounterPhase1 + "," + 
/* 201 */           this.__stateCounter + "]");
/* 202 */       throw new RuntimeException("Inconsistent state counters.");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void __checkWitnesses() {
/* 209 */     for (IAuxType t : this.__verifiedProverAux) {
/* 210 */       t.verifyRange();
/*     */     }
/*     */   }
/*     */   
/*     */   private void __phase1() {
/* 215 */     this.__phase = 0;
/* 216 */     __declareGenericConstants();
/* 217 */     __init();
/* 218 */     __defineInputs();
/* 219 */     __defineVerifiedWitnesses();
/* 220 */     __defineWitnesses();
/* 221 */     outsource();
/* 222 */     __checkWitnesses();
/* 223 */     __defineOutputs();
/*     */     
/* 225 */     for (SmartMemory<?> mem : this.__memoryList) {
/* 226 */       mem.analyzeWorkload();
/*     */     }
/*     */ 
/*     */     
/* 230 */     this.__stateCounterPhase1 = this.__stateCounter;
/* 231 */     __clear();
/* 232 */     System.out.println("Phase 1: Analysis Completed!");
/*     */     
/* 234 */     this.__phase++;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private LinkedHashMap<Instruction, Instruction> __copyEvalSeq(LinkedHashMap<Instruction, Instruction> evaluationQueue) {
/* 240 */     LinkedHashMap<Instruction, Instruction> c = new LinkedHashMap<>();
/*     */ 
/*     */     
/* 243 */     this.__oneWire = this.__oneWire.copy();
/*     */     
/* 245 */     Wire[] wireList = new Wire[__getCurrentWireId()];
/* 246 */     wireList[0] = this.__oneWire;
/* 247 */     for (Instruction i : evaluationQueue.keySet()) {
/*     */       
/* 249 */       Instruction copiedInstruction = i.copy(wireList);
/* 250 */       if (copiedInstruction != null)
/*     */       {
/*     */         
/* 253 */         c.put(copiedInstruction, copiedInstruction);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 258 */     this.__zeroWire = wireList[1];
/* 259 */     this.__knownConstantWires.clear();
/* 260 */     this.__knownConstantWires.put(BigInteger.ONE, this.__oneWire);
/* 261 */     this.__knownConstantWires.put(BigInteger.ZERO, this.__zeroWire);
/*     */     
/* 263 */     return c;
/*     */   }
/*     */ 
/*     */   
/*     */   private void __clear() {
/* 268 */     this.__inWires.clear();
/* 269 */     this.__outWires.clear();
/* 270 */     this.__proverWitnessWires.clear();
/* 271 */     this.__evaluationQueue.clear();
/* 272 */     this.__nonOptimizedEvaluationQueue.clear();
/* 273 */     this.__knownConstantWires.clear();
/*     */     
/* 275 */     this.__inputAux.clear();
/* 276 */     this.__proverAux.clear();
/* 277 */     this.__verifiedProverAux.clear();
/* 278 */     this.__currentWireId = 0;
/* 279 */     this.__stateCounter = 0;
/* 280 */     this.__conditionCounter = 0;
/* 281 */     this.__numOfConstraints = 0;
/* 282 */     this.__memoryList.clear();
/* 283 */     SmartMemory.globalMemoryCounter = 0;
/*     */   }
/*     */   
/*     */   public String __getName() {
/* 287 */     return this.__circuitName;
/*     */   }
/*     */ 
/*     */   
/*     */   public void __generateSampleInput(CircuitEvaluator evaluator) {}
/*     */ 
/*     */   
/*     */   public Wire __createInputWire(String... desc) {
/* 295 */     Wire newInputWire = new VariableWire(this.__currentWireId++);
/* 296 */     __addToEvaluationQueue((Instruction)new WireLabelInstruction(WireLabelInstruction.LabelType.input, newInputWire, desc));
/* 297 */     this.__inWires.add(newInputWire);
/* 298 */     return newInputWire;
/*     */   }
/*     */   
/*     */   public Wire[] __createInputWireArray(int n, String... desc) {
/* 302 */     Wire[] list = new Wire[n];
/* 303 */     for (int i = 0; i < n; i++) {
/* 304 */       if (desc.length == 0) {
/* 305 */         list[i] = __createInputWire(new String[] { "" });
/*     */       } else {
/* 307 */         list[i] = __createInputWire(new String[] { String.valueOf(desc[0]) + " " + i });
/*     */       } 
/*     */     } 
/* 310 */     return list;
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire __createProverWitnessWire(String... desc) {
/* 315 */     Wire wire = new VariableWire(this.__currentWireId++);
/* 316 */     __addToEvaluationQueue((Instruction)new WireLabelInstruction(WireLabelInstruction.LabelType.nizkinput, wire, desc));
/* 317 */     this.__proverWitnessWires.add(wire);
/* 318 */     return wire;
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire[] __createProverWitnessWireArray(int n, String... desc) {
/* 323 */     Wire[] ws = new Wire[n];
/* 324 */     for (int k = 0; k < n; k++) {
/* 325 */       if (desc.length == 0) {
/* 326 */         ws[k] = __createProverWitnessWire(new String[] { "" });
/*     */       } else {
/* 328 */         ws[k] = __createProverWitnessWire(new String[] { String.valueOf(desc[0]) + " " + k });
/*     */       } 
/*     */     } 
/* 331 */     return ws;
/*     */   }
/*     */   
/*     */   public Wire[] __generateZeroWireArray(int n) {
/* 335 */     ConstantWire[] arrayOfConstantWire = new ConstantWire[n];
/* 336 */     Arrays.fill((Object[])arrayOfConstantWire, this.__zeroWire);
/* 337 */     return (Wire[])arrayOfConstantWire;
/*     */   }
/*     */   
/*     */   public Wire[] __generateOneWireArray(int n) {
/* 341 */     ConstantWire[] arrayOfConstantWire = new ConstantWire[n];
/* 342 */     Arrays.fill((Object[])arrayOfConstantWire, this.__oneWire);
/* 343 */     return (Wire[])arrayOfConstantWire;
/*     */   }
/*     */   
/*     */   public Wire __makeOutput(Wire wire, String... desc) {
/* 347 */     Wire outputWire = wire;
/* 348 */     if ((!(wire instanceof VariableWire) && !(wire instanceof VariableBitWire)) || this.__inWires.contains(wire)) {
/* 349 */       wire.packIfNeeded(desc);
/* 350 */       outputWire = __makeVariable(wire, desc);
/* 351 */     } else if (this.__inWires.contains(wire) || this.__proverWitnessWires.contains(wire)) {
/* 352 */       outputWire = __makeVariable(wire, desc);
/*     */     } else {
/* 354 */       wire.packIfNeeded(new String[0]);
/*     */     } 
/*     */     
/* 357 */     this.__outWires.add(outputWire);
/* 358 */     __addToEvaluationQueue((Instruction)new WireLabelInstruction(WireLabelInstruction.LabelType.output, outputWire, desc));
/* 359 */     return outputWire;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Wire __makeVariable(Wire wire, String... desc) {
/* 364 */     Wire outputWire = new VariableWire(this.__currentWireId++);
/* 365 */     MulBasicOp mulBasicOp = new MulBasicOp(wire, this.__oneWire, outputWire, desc);
/* 366 */     Wire[] cachedOutputs = __addToEvaluationQueue((Instruction)mulBasicOp);
/* 367 */     if (cachedOutputs == null) {
/* 368 */       return outputWire;
/*     */     }
/*     */     
/* 371 */     this.__currentWireId--;
/* 372 */     return cachedOutputs[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire[] __makeOutputArray(Wire[] wires, String... desc) {
/* 377 */     Wire[] outs = new Wire[wires.length];
/* 378 */     for (int i = 0; i < wires.length; i++) {
/* 379 */       if (desc.length == 0) {
/* 380 */         outs[i] = __makeOutput(wires[i], new String[] { "" });
/*     */       } else {
/* 382 */         outs[i] = __makeOutput(wires[i], new String[] { String.valueOf(desc[0]) + "[" + i + "]" });
/*     */       } 
/*     */     } 
/* 385 */     return outs;
/*     */   }
/*     */   
/*     */   public void __addDebugInstruction(Wire w, String... desc) {
/* 389 */     w.packIfNeeded(new String[0]);
/* 390 */     __addToEvaluationQueue((Instruction)new WireLabelInstruction(WireLabelInstruction.LabelType.debug, w, desc));
/*     */   }
/*     */ 
/*     */   
/*     */   public void __addDebugInstruction(IAuxType t, String... desc) {
/* 395 */     __addToEvaluationQueue((Instruction)new WireLabelInstruction(WireLabelInstruction.LabelType.debug, t.copy(), desc));
/*     */   }
/*     */   
/*     */   public void __addDebugInstruction(PackedValue v, String... desc) {
/* 399 */     __addToEvaluationQueue((Instruction)new WireLabelInstruction(WireLabelInstruction.LabelType.debug, v, desc));
/*     */   }
/*     */ 
/*     */   
/*     */   public void __addDebugInstruction(Wire[] wires, String... desc) {
/* 404 */     for (int i = 0; i < wires.length; i++) {
/* 405 */       wires[i].packIfNeeded(new String[0]);
/*     */       
/* 407 */       __addToEvaluationQueue(
/* 408 */           (Instruction)new WireLabelInstruction(WireLabelInstruction.LabelType.debug, wires[i], new String[] { (desc.length > 0) ? (String.valueOf(desc[0]) + " - " + i) : "" }));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void __writeCircuitFile(String arg) {
/*     */     try {
/* 414 */       PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(String.valueOf(Config.outputFilesPath) + (
/* 415 */               Config.outputFilesPath.isEmpty() ? "" : File.separator) + __getName() + arg + ".arith")));
/*     */       
/* 417 */       printWriter.println("total " + this.__currentWireId);
/* 418 */       for (Instruction e : this.__evaluationQueue.keySet()) {
/* 419 */         if (e.doneWithinCircuit()) {
/* 420 */           printWriter.print(e + "\n");
/*     */         }
/*     */       } 
/* 423 */       printWriter.close();
/* 424 */     } catch (Exception e) {
/* 425 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void __printCircuit() {
/* 431 */     for (Instruction e : this.__evaluationQueue.keySet()) {
/* 432 */       if (e.doneWithinCircuit()) {
/* 433 */         System.out.println(e);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void __declareGenericConstants() {
/* 440 */     this.__oneWire = new ConstantWire(this.__currentWireId++, BigInteger.ONE);
/* 441 */     this.__knownConstantWires.put(BigInteger.ONE, this.__oneWire);
/* 442 */     __addToEvaluationQueue((Instruction)new WireLabelInstruction(WireLabelInstruction.LabelType.input, this.__oneWire, new String[] { "The one-input wire." }));
/* 443 */     this.__inWires.add(this.__oneWire);
/* 444 */     this.__zeroWire = this.__oneWire.mul(0L, new String[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void __init() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void __evaluateSampleRun(SampleRun sampleRun) {
/* 454 */     if (!sampleRun.isEnabled()) {
/*     */       return;
/*     */     }
/* 457 */     System.out.println("Running Sample Run: " + sampleRun.getName());
/*     */     
/* 459 */     this.__knownConstantWires.clear();
/* 460 */     this.__knownConstantWires.put(BigInteger.ONE, this.__oneWire);
/* 461 */     this.__circuitEvaluator = new CircuitEvaluator(this.__nonOptimalWireCount);
/*     */     
/* 463 */     sampleRun.pre();
/*     */     
/* 465 */     System.out.println("Evaluating Input on the circuit " + (Config.multivariateExpressionMinimization ? "without multivariate optimizations attempts" : ""));
/* 466 */     this.__circuitEvaluator.evaluate(this.__nonOptimizedEvaluationQueue);
/* 467 */     sampleRun.post();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 472 */     System.out.println("Evaluation Done ");
/*     */     
/* 474 */     if (Config.multivariateExpressionMinimization) {
/* 475 */       System.out.println("Evaluating Input on the circuit after multivariate optimizations attempt");
/* 476 */       this.__knownConstantWires.clear();
/* 477 */       this.__knownConstantWires.put(BigInteger.ONE, this.__oneWire);
/* 478 */       this.__circuitEvaluator = this.__circuitOptimizer.mapFromOldEvaluationSeq(this.__circuitEvaluator);
/*     */ 
/*     */       
/* 481 */       System.out.println("Evaluation Done");
/* 482 */       System.out.println("[Pass] Output values after multivariate optimizations match the previous output of the circuit.");
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 487 */     System.out.println("Sample Run: " + sampleRun.getName() + " finished!");
/*     */     
/* 489 */     if (Config.writeCircuits) {
/* 490 */       __prepInputFile(String.valueOf(sampleRun.getName()) + (Config.multivariateExpressionMinimization ? "_optimized" : ""));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire __createConstantWire(BigInteger x, String... desc) {
/* 499 */     return this.__oneWire.mul(x, desc);
/*     */   }
/*     */   
/*     */   public Wire[] __createConstantWireArray(BigInteger[] a, String... desc) {
/* 503 */     Wire[] w = new Wire[a.length];
/* 504 */     for (int i = 0; i < a.length; i++) {
/* 505 */       w[i] = __createConstantWire(a[i], desc);
/*     */     }
/* 507 */     return w;
/*     */   }
/*     */   
/*     */   public Wire __createConstantWire(long x, String... desc) {
/* 511 */     return this.__oneWire.mul(x, desc);
/*     */   }
/*     */   
/*     */   public Wire[] __createConstantWireArray(long[] a, String... desc) {
/* 515 */     Wire[] w = new Wire[a.length];
/* 516 */     for (int i = 0; i < a.length; i++) {
/* 517 */       w[i] = __createConstantWire(a[i], desc);
/*     */     }
/* 519 */     return w;
/*     */   }
/*     */   
/*     */   public Wire __createNegConstantWire(BigInteger x, String... desc) {
/* 523 */     return this.__oneWire.mul(x.negate(), desc);
/*     */   }
/*     */   
/*     */   public Wire __createNegConstantWire(long x, String... desc) {
/* 527 */     return this.__oneWire.mul(-x, desc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void __specifyProverWitnessComputation(Instruction instruction) {
/* 537 */     __addToEvaluationQueue(instruction);
/*     */   }
/*     */   
/*     */   public final Wire __getZeroWire() {
/* 541 */     return this.__zeroWire;
/*     */   }
/*     */   
/*     */   public final Wire __getOneWire() {
/* 545 */     return this.__oneWire;
/*     */   }
/*     */   
/*     */   public LinkedHashMap<Instruction, Instruction> __getEvaluationQueue() {
/* 549 */     return this.__evaluationQueue;
/*     */   }
/*     */   
/*     */   public int __getNumWires() {
/* 553 */     return this.__currentWireId;
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
/*     */   public Wire[] __addToEvaluationQueue(Instruction e) {
/* 567 */     this.__lastInstructionAdded = e;
/* 568 */     if (this.__evaluationQueue.containsKey(e) && 
/* 569 */       e instanceof BasicOp)
/*     */     {
/* 571 */       return ((BasicOp)this.__evaluationQueue.get(e)).getOutputs();
/*     */     }
/*     */     
/* 574 */     if (e instanceof BasicOp) {
/*     */       
/* 576 */       this.__numOfConstraints += ((BasicOp)e).getNumMulGates();
/* 577 */       ((BasicOp)e).getNumMulGates();
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 582 */     this.__evaluationQueue.put(e, e);
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
/* 596 */     return null;
/*     */   }
/*     */   
/*     */   public void __printState(String message) {
/* 600 */     System.out.println("\nGenerator State @ " + message);
/* 601 */     System.out.println("\tCurrent Number of Multiplication Gates  :: " + this.__numOfConstraints + "\n");
/*     */   }
/*     */   
/*     */   public int __getNumOfConstraints() {
/* 605 */     return this.__numOfConstraints;
/*     */   }
/*     */   
/*     */   public ArrayList<Wire> __getInWires() {
/* 609 */     return this.__inWires;
/*     */   }
/*     */   
/*     */   public ArrayList<Wire> __getOutWires() {
/* 613 */     return this.__outWires;
/*     */   }
/*     */   
/*     */   public ArrayList<Wire> __getProverWitnessWires() {
/* 617 */     return this.__proverWitnessWires;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void __addAssertion(Wire w1, Wire w2, Wire w3, String... desc) {
/* 626 */     if (w1 instanceof ConstantWire && w2 instanceof ConstantWire && w3 instanceof ConstantWire) {
/* 627 */       BigInteger const1 = ((ConstantWire)w1).getConstant();
/* 628 */       BigInteger const2 = ((ConstantWire)w2).getConstant();
/* 629 */       BigInteger const3 = ((ConstantWire)w3).getConstant();
/* 630 */       if (!const3.equals(const1.multiply(const2).mod(Config.getFiniteFieldModulus()))) {
/* 631 */         throw new RuntimeException("This assertion can never work on the provided constant wires .. ");
/*     */       }
/*     */     } else {
/* 634 */       w1.packIfNeeded(new String[0]);
/* 635 */       w2.packIfNeeded(new String[0]);
/* 636 */       w3.packIfNeeded(new String[0]);
/*     */       
/* 638 */       if (ConditionalScopeTracker.getCurrentScopeId() > 0) {
/* 639 */         Wire active = ConditionalScopeTracker.getAccumActiveBit().getWire();
/*     */         
/* 641 */         if (w1 instanceof ConstantWire) {
/* 642 */           AssertBasicOp assertBasicOp = new AssertBasicOp(w1.mul(active, new String[0]), w2, w3.mul(active, new String[0]), desc);
/* 643 */           __addToEvaluationQueue((Instruction)assertBasicOp);
/*     */         } else {
/* 645 */           AssertBasicOp assertBasicOp = new AssertBasicOp(w1, w2.mul(active, new String[0]), w3.mul(active, new String[0]), desc);
/* 646 */           __addToEvaluationQueue((Instruction)assertBasicOp);
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 651 */         AssertBasicOp assertBasicOp = new AssertBasicOp(w1, w2, w3, desc);
/* 652 */         __addToEvaluationQueue((Instruction)assertBasicOp);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void __forceNativeConstraint(FieldElement a, FieldElement b, FieldElement c, String... desc) {
/* 658 */     if (!a.isNativeSnarkField() || !b.isNativeSnarkField() || !c.isNativeSnarkField()) {
/* 659 */       throw new IllegalArgumentException("Verifying native constraints works only on native field types.");
/*     */     }
/*     */     
/* 662 */     __addAssertion(a.getPackedWire().getArray()[0], b.getPackedWire().getArray()[0], c.getPackedWire().getArray()[0], desc);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void __addZeroAssertion(Wire w, String... desc) {
/* 668 */     __addAssertion(w, this.__oneWire, this.__zeroWire, desc);
/*     */   }
/*     */   
/*     */   public void __addOneAssertion(Wire w, String... desc) {
/* 672 */     __addAssertion(w, this.__oneWire, this.__oneWire, desc);
/*     */   }
/*     */   
/*     */   public void __addBinaryAssertion(Wire w, String... desc) {
/* 676 */     Wire inv = w.invAsBit(desc);
/* 677 */     __addAssertion(w, inv, this.__zeroWire, desc);
/*     */   }
/*     */   
/*     */   public void __addEqualityAssertion(Wire w1, Wire w2, String... desc) {
/* 681 */     __addAssertion(w1, this.__oneWire, w2, desc);
/*     */   }
/*     */   
/*     */   public void __addEqualityAssertion(Wire w1, BigInteger b, String... desc) {
/* 685 */     __addAssertion(w1, this.__oneWire, __createConstantWire(b, desc), desc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void __evalCircuit() {
/* 693 */     this.__knownConstantWires.clear();
/* 694 */     this.__knownConstantWires.put(BigInteger.ONE, this.__oneWire);
/* 695 */     this.__circuitEvaluator = new CircuitEvaluator(this.__nonOptimalWireCount);
/* 696 */     __generateSampleInput(this.__circuitEvaluator);
/*     */     
/* 698 */     this.__circuitEvaluator.evaluate(this.__nonOptimizedEvaluationQueue);
/* 699 */     if (Config.multivariateExpressionMinimization) {
/* 700 */       this.__knownConstantWires.clear();
/*     */       
/* 702 */       this.__circuitEvaluator = this.__circuitOptimizer.mapFromOldEvaluationSeq(this.__circuitEvaluator);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void __prepFiles() {
/* 708 */     __writeCircuitFile("");
/* 709 */     if (this.__circuitEvaluator == null) {
/* 710 */       throw new NullPointerException("evalCircuit() must be called before prepFiles()");
/*     */     }
/* 712 */     this.__circuitEvaluator.writeInputFile("");
/*     */   }
/*     */   
/*     */   public void __prepFiles(String arg) {
/* 716 */     __writeCircuitFile(arg);
/* 717 */     if (this.__circuitEvaluator == null) {
/* 718 */       throw new NullPointerException("evalCircuit() must be called before prepFiles()");
/*     */     }
/* 720 */     this.__circuitEvaluator.writeInputFile(arg);
/*     */   }
/*     */   
/*     */   public void __prepInputFile(String arg) {
/* 724 */     if (this.__circuitEvaluator == null) {
/* 725 */       throw new NullPointerException("evalCircuit() must be called before prepFiles()");
/*     */     }
/* 727 */     this.__circuitEvaluator.writeInputFile(arg);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CircuitEvaluator __getCircuitEvaluator() {
/* 755 */     if (this.__circuitEvaluator == null) {
/* 756 */       throw new NullPointerException("evalCircuit() must be called before getCircuitEvaluator()");
/*     */     }
/* 758 */     return this.__circuitEvaluator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void __defineInputs() {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void __defineWitnesses() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void __defineVerifiedWitnesses() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void __defineOutputs() {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int __getPhase() {
/* 782 */     return this.__phase;
/*     */   }
/*     */   public VariableState __retrieveVariableState() {
/*     */     VariableState variableState;
/* 786 */     if (this.__phase == 0) {
/* 787 */       variableState = new VariableState();
/* 788 */       if (!this.__untrackedStateObjects) {
/* 789 */         this.__varVariableStateTable.put(Integer.valueOf(this.__stateCounter), variableState);
/* 790 */         variableState.setId(this.__stateCounter);
/* 791 */         this.__stateCounter++;
/*     */       } 
/*     */       
/* 794 */       return variableState;
/*     */     } 
/*     */     
/* 797 */     if (!this.__untrackedStateObjects) {
/* 798 */       variableState = this.__varVariableStateTable.get(Integer.valueOf(this.__stateCounter));
/* 799 */       this.__stateCounter++;
/*     */     } else {
/* 801 */       variableState = new VariableState();
/*     */     } 
/*     */     
/* 804 */     return variableState;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean __checkConstantState(Bit b) {
/* 809 */     if (this.__phase == 0) {
/* 810 */       boolean isConstant = b.isConstant();
/* 811 */       this.__conditionalStateList.add(Boolean.valueOf(isConstant));
/* 812 */       this.__conditionCounter++;
/* 813 */       return isConstant;
/*     */     } 
/* 815 */     boolean recalledDecision = ((Boolean)this.__conditionalStateList.get(this.__conditionCounter)).booleanValue();
/* 816 */     this.__conditionCounter++;
/* 817 */     return recalledDecision;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void __setUntrackedStateObjects(boolean untrackedStateObjects) {
/* 824 */     this.__untrackedStateObjects = untrackedStateObjects;
/*     */   }
/*     */ 
/*     */   
/*     */   public int __getStateCounter() {
/* 829 */     return this.__stateCounter;
/*     */   }
/*     */ 
/*     */   
/*     */   public void __setEvaluationQueue(LinkedHashMap<Instruction, Instruction> evaluationQueue) {
/* 834 */     this.__knownConstantWires.clear();
/* 835 */     this.__knownConstantWires.put(BigInteger.ONE, this.__oneWire);
/* 836 */     this.__evaluationQueue = evaluationQueue;
/*     */   }
/*     */   
/*     */   public int __getCurrentWireId() {
/* 840 */     return this.__currentWireId;
/*     */   }
/*     */   
/*     */   public void __setCurrentWireId(int newWireCount) {
/* 844 */     this.__currentWireId = newWireCount;
/*     */   }
/*     */   
/*     */   public Instruction __getLastInstructionAdded() {
/* 848 */     return this.__lastInstructionAdded;
/*     */   }
/*     */   
/*     */   public ArrayList<IAuxType> __getInputAux() {
/* 852 */     return this.__inputAux;
/*     */   }
/*     */   
/*     */   public ArrayList<IAuxType> __getProverAux() {
/* 856 */     return this.__proverAux;
/*     */   }
/*     */   
/*     */   public ArrayList<IAuxType> __getProverVerifiedAux() {
/* 860 */     return this.__verifiedProverAux;
/*     */   }
/*     */   
/*     */   public void __generateRandomInput(CircuitEvaluator evaluator) {
/* 864 */     for (IAuxType t : this.__inputAux)
/*     */     {
/*     */       
/* 867 */       t.mapRandomValue(evaluator);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PackedValue __createConstantPackedValue(BigInteger constant, int bitWidth) {
/* 875 */     if (bitWidth <= UnsignedInteger.BITWIDTH_LIMIT_SHORT)
/*     */     {
/* 877 */       return new PackedValue(__createConstantWire(constant, new String[0]), constant);
/*     */     }
/*     */     
/* 880 */     constant = constant.mod((new BigInteger("2")).pow(bitWidth));
/*     */     
/* 882 */     int numChunks = (int)Math.ceil(constant.bitLength() * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */     
/* 884 */     BigInteger[] chunks = Util.split(constant, numChunks, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 885 */     Wire[] array = new Wire[numChunks];
/* 886 */     for (int i = 0; i < numChunks; i++) {
/* 887 */       array[i] = __createConstantWire(chunks[i], new String[0]);
/*     */     }
/*     */     
/* 890 */     return new PackedValue(array, chunks);
/*     */   }
/*     */ 
/*     */   
/*     */   public PackedValue __createConstantPackedValue(BigInteger constant, BigInteger modulus) {
/* 895 */     if (modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || modulus.equals(Config.getFiniteFieldModulus()))
/*     */     {
/* 897 */       return new PackedValue(__createConstantWire(constant, new String[0]), constant);
/*     */     }
/*     */     
/* 900 */     constant = constant.mod(modulus);
/*     */     
/* 902 */     int numChunks = (int)Math.ceil(constant.bitLength() * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/*     */     
/* 904 */     BigInteger[] chunks = Util.split(constant, numChunks, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 905 */     Wire[] array = new Wire[numChunks];
/* 906 */     for (int i = 0; i < numChunks; i++)
/*     */     {
/* 908 */       array[i] = __createConstantWire(chunks[i], new String[0]);
/*     */     }
/*     */ 
/*     */     
/* 912 */     return new PackedValue(array, chunks);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<SmartMemory<?>> __getMemoryList() {
/* 918 */     return this.__memoryList;
/*     */   }
/*     */   
/*     */   public HashMap<Integer, SmartMemory.MemoryState> __getMemoryStateTable() {
/* 922 */     return this.__memoryStateTable;
/*     */   }
/*     */   
/*     */   public ArrayList<Class<? extends RuntimeStruct>> __getRumtimeClassesList() {
/* 926 */     return this.__rumtimeClassesList;
/*     */   }
/*     */   
/*     */   public ArrayList<IAuxType> __getVerifiedProverAux() {
/* 930 */     return this.__verifiedProverAux;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\structure\CircuitGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */