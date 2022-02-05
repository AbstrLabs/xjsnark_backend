/*     */ package backend.eval;
/*     */ 
/*     */ import backend.auxTypes.PackedValue;
/*     */ import backend.auxTypes.UnsignedInteger;
/*     */ import backend.config.Config;
/*     */ import backend.operations.WireLabelInstruction;
/*     */ import backend.operations.primitive.BasicOp;
/*     */ import backend.structure.CircuitGenerator;
/*     */ import backend.structure.Wire;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Scanner;
/*     */ import util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CircuitEvaluator
/*     */ {
/*     */   private CircuitGenerator circuitGenerator;
/*     */   private BigInteger[] valueAssignment;
/*     */   private HashMap<String, int[]> permutations;
/*  33 */   private HashMap<Integer, ArrayList<Instruction>> pendingInstructions = new HashMap<>();
/*     */   
/*     */   public CircuitEvaluator(CircuitGenerator circuitGenerator) {
/*  36 */     this.circuitGenerator = circuitGenerator;
/*  37 */     this.permutations = (HashMap)new HashMap<>();
/*  38 */     this.valueAssignment = new BigInteger[circuitGenerator.__getNumWires()];
/*  39 */     this.valueAssignment[circuitGenerator.__getOneWire().getWireId()] = BigInteger.ONE;
/*     */   }
/*     */ 
/*     */   
/*     */   public CircuitEvaluator(CircuitGenerator circuitGenerator, BigInteger[] valueAssignment) {
/*  44 */     this.circuitGenerator = circuitGenerator;
/*  45 */     this.valueAssignment = valueAssignment;
/*  46 */     this.permutations = (HashMap)new HashMap<>();
/*  47 */     valueAssignment[circuitGenerator.__getOneWire().getWireId()] = BigInteger.ONE;
/*     */   }
/*     */   
/*     */   public CircuitEvaluator(int wireCount) {
/*  51 */     this.valueAssignment = new BigInteger[wireCount];
/*  52 */     this.permutations = (HashMap)new HashMap<>();
/*  53 */     this.valueAssignment[0] = BigInteger.ONE;
/*  54 */     this.circuitGenerator = CircuitGenerator.__getActiveCircuitGenerator();
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
/*     */   public void setWireValue(Wire w, BigInteger v) {
/*  73 */     if (v.signum() < 0 || v.compareTo(Config.getFiniteFieldModulus()) >= 0) {
/*  74 */       throw new IllegalArgumentException("[Internal Error] Only positive values that are less than the modulus are allowed for wires.");
/*     */     }
/*  76 */     if (this.valueAssignment[w.getWireId()] != null) {
/*  77 */       throw new RuntimeException("Element has been assigned before!");
/*     */     }
/*     */     
/*  80 */     this.valueAssignment[w.getWireId()] = v;
/*     */ 
/*     */     
/*  83 */     if (this.pendingInstructions.containsKey(Integer.valueOf(w.getWireId()))) {
/*  84 */       label25: for (Instruction i : this.pendingInstructions.get(Integer.valueOf(w.getWireId()))) {
/*  85 */         if (i instanceof BasicOp) {
/*  86 */           Wire[] inputs = ((BasicOp)i).getInputs(); byte b; int j;
/*     */           Wire[] arrayOfWire1;
/*  88 */           for (j = (arrayOfWire1 = inputs).length, b = 0; b < j; ) { Wire w2 = arrayOfWire1[b];
/*  89 */             if (this.valueAssignment[w2.getWireId()] == null) {
/*     */               continue label25;
/*     */             }
/*     */             b++; }
/*     */         
/*     */         } 
/*  95 */         i.evaluate(this);
/*  96 */         i.emit(this);
/*     */       } 
/*  98 */       this.pendingInstructions.remove(Integer.valueOf(w.getWireId()));
/*     */     } 
/*     */   }
/*     */   
/*     */   public BigInteger getWireValue(Wire w) {
/* 103 */     return this.valueAssignment[w.getWireId()];
/*     */   }
/*     */   
/*     */   public BigInteger[] getWiresValues(Wire[] w) {
/* 107 */     BigInteger[] values = new BigInteger[w.length];
/* 108 */     for (int i = 0; i < w.length; i++) {
/* 109 */       values[i] = getWireValue(w[i]);
/*     */     }
/* 111 */     return values;
/*     */   }
/*     */   
/*     */   public void setWireValue(Wire wire, long v) {
/* 115 */     setWireValue(wire, new BigInteger((new StringBuilder(String.valueOf(v))).toString()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWireValue(Wire[] wires, BigInteger[] v) {
/*     */     int i;
/* 122 */     for (i = 0; i < v.length; i++)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/* 127 */       setWireValue(wires[i], v[i]);
/*     */     }
/* 129 */     if (wires.length > v.length) {
/* 130 */       for (i = v.length; i < wires.length; i++) {
/* 131 */         setWireValue(wires[i], BigInteger.ZERO);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void evaluate(LinkedHashMap<Instruction, Instruction> evalSequence) {
/* 139 */     for (Instruction e : evalSequence.keySet()) {
/*     */       
/* 141 */       boolean skip = false;
/* 142 */       if (e instanceof BasicOp) {
/* 143 */         Wire[] inputs = ((BasicOp)e).getInputs(); byte b; int j;
/*     */         Wire[] arrayOfWire1;
/* 145 */         for (j = (arrayOfWire1 = inputs).length, b = 0; b < j; ) { Wire w = arrayOfWire1[b];
/* 146 */           if (this.valueAssignment[w.getWireId()] == null) {
/* 147 */             if (this.pendingInstructions.get(Integer.valueOf(w.getWireId())) == null) {
/* 148 */               this.pendingInstructions.put(Integer.valueOf(w.getWireId()), 
/* 149 */                   new ArrayList<>());
/*     */             }
/*     */             
/* 152 */             ((ArrayList<Instruction>)this.pendingInstructions.get(Integer.valueOf(w.getWireId()))).add(e);
/*     */             
/* 154 */             skip = true;
/*     */           } 
/*     */           b++; }
/*     */       
/*     */       } 
/* 159 */       if (!skip) {
/* 160 */         e.evaluate(this);
/* 161 */         e.emit(this);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 166 */     for (int i = 0; i < this.valueAssignment.length; i++) {
/* 167 */       if (this.valueAssignment[i] == null) {
/* 168 */         throw new RuntimeException("Wire#" + i + "is without value");
/*     */       }
/*     */     } 
/* 171 */     if (this.pendingInstructions.size() != 0) {
/* 172 */       throw new RuntimeException("Internal Issue: Pending Instruction Sequenece is not empty after terminating");
/*     */     }
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
/*     */   public void evaluate() {
/* 185 */     evaluate(this.circuitGenerator.__getEvaluationQueue());
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
/*     */   public void writeInputFile(String arg) {
/*     */     try {
/* 204 */       LinkedHashMap<Instruction, Instruction> evalSequence = this.circuitGenerator
/* 205 */         .__getEvaluationQueue();
/*     */       
/* 207 */       PrintWriter printWriter = new PrintWriter(String.valueOf(Config.outputFilesPath) + (
/* 208 */           Config.outputFilesPath.isEmpty() ? "" : File.separator) + 
/* 209 */           this.circuitGenerator.__getName() + "_" + arg + ".in");
/* 210 */       for (Instruction e : evalSequence.keySet()) {
/* 211 */         if (e instanceof WireLabelInstruction && ((
/* 212 */           (WireLabelInstruction)e).getType() == WireLabelInstruction.LabelType.input || ((WireLabelInstruction)e)
/* 213 */           .getType() == WireLabelInstruction.LabelType.nizkinput)) {
/* 214 */           int id = ((WireLabelInstruction)e).getWire().getWireId();
/* 215 */           printWriter.println(String.valueOf(id) + " " + 
/* 216 */               this.valueAssignment[id].toString(16));
/*     */         } 
/*     */       } 
/* 219 */       printWriter.close();
/*     */     }
/* 221 */     catch (Exception e) {
/* 222 */       e.printStackTrace();
/*     */     } 
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
/*     */   public static void eval(String circuitFilePath, String inFilePath) throws Exception {
/* 237 */     Scanner circuitScanner = new Scanner(new BufferedInputStream(
/* 238 */           new FileInputStream(circuitFilePath)));
/* 239 */     Scanner inFileScanner = new Scanner(new File(inFilePath));
/*     */     
/* 241 */     int totalWires = Integer.parseInt(circuitScanner.nextLine().replace(
/* 242 */           "total ", ""));
/*     */     
/* 244 */     BigInteger[] assignment = new BigInteger[totalWires];
/*     */     
/* 246 */     ArrayList<Integer> wiresToReport = new ArrayList<>();
/* 247 */     HashSet<Integer> ignoreWires = new HashSet<>();
/*     */ 
/*     */     
/* 250 */     while (inFileScanner.hasNextInt()) {
/* 251 */       int wireNumber = inFileScanner.nextInt();
/* 252 */       String num = inFileScanner.next();
/* 253 */       assignment[wireNumber] = new BigInteger(num, 16);
/* 254 */       wiresToReport.add(Integer.valueOf(wireNumber));
/*     */     } 
/*     */ 
/*     */     
/* 258 */     BigInteger prime = new BigInteger(
/* 259 */         "21888242871839275222246405745257275088548364400416034343698204186575808495617");
/*     */     
/* 261 */     circuitScanner.nextLine();
/* 262 */     while (circuitScanner.hasNext()) {
/* 263 */       String line = circuitScanner.nextLine();
/* 264 */       if (line.contains("#")) {
/* 265 */         line = line.substring(0, line.indexOf("#"));
/* 266 */         line = line.trim();
/*     */       } 
/* 268 */       if (line.startsWith("input") || line.startsWith("nizkinput")) {
/*     */         continue;
/*     */       }
/* 271 */       if (line.startsWith("output ")) {
/* 272 */         line = line.replace("output ", "");
/* 273 */         System.out.println(String.valueOf(Integer.parseInt(line)) + "::" + 
/* 274 */             assignment[Integer.parseInt(line)].toString(16));
/* 275 */         wiresToReport.add(Integer.valueOf(Integer.parseInt(line))); continue;
/* 276 */       }  if (line.startsWith("DEBUG ")) {
/* 277 */         line = line.replace("DEBUG ", "");
/* 278 */         Scanner scanner = new Scanner(line);
/* 279 */         int id = Integer.parseInt(scanner.next());
/* 280 */         System.out.println(String.valueOf(id) + "::" + assignment[id].toString(16) + 
/* 281 */             " >> " + scanner.nextLine());
/* 282 */         scanner.close(); continue;
/*     */       } 
/* 284 */       ArrayList<Integer> ins = getInputs(line);
/* 285 */       for (Iterator<Integer> iterator1 = ins.iterator(); iterator1.hasNext(); ) { int in = ((Integer)iterator1.next()).intValue();
/* 286 */         if (assignment[in] == null) {
/* 287 */           System.err
/* 288 */             .println("Undefined value for a used wire, at line " + 
/* 289 */               line);
/*     */         } }
/*     */       
/* 292 */       ArrayList<Integer> outs = getOutputs(line);
/* 293 */       if (line.startsWith("mul ")) {
/* 294 */         BigInteger out = BigInteger.ONE;
/* 295 */         for (Iterator<Integer> iterator2 = ins.iterator(); iterator2.hasNext(); ) { int w = ((Integer)iterator2.next()).intValue();
/* 296 */           out = out.multiply(assignment[w]); }
/*     */         
/* 298 */         wiresToReport.add(outs.get(0));
/* 299 */         assignment[((Integer)outs.get(0)).intValue()] = out.mod(prime); continue;
/*     */       } 
/* 301 */       if (line.startsWith("add ")) {
/* 302 */         BigInteger out = BigInteger.ZERO;
/* 303 */         for (Iterator<Integer> iterator2 = ins.iterator(); iterator2.hasNext(); ) { int w = ((Integer)iterator2.next()).intValue();
/* 304 */           out = out.add(assignment[w]); }
/*     */         
/* 306 */         assignment[((Integer)outs.get(0)).intValue()] = out.mod(prime); continue;
/* 307 */       }  if (line.startsWith("xor ")) {
/* 308 */         BigInteger out = 
/* 309 */           assignment[((Integer)ins.get(0)).intValue()].equals(assignment[((Integer)ins.get(1)).intValue()]) ? BigInteger.ZERO : 
/* 310 */           BigInteger.ONE;
/* 311 */         assignment[((Integer)outs.get(0)).intValue()] = out;
/* 312 */         wiresToReport.add(outs.get(0)); continue;
/*     */       } 
/* 314 */       if (line.startsWith("zerop ")) {
/* 315 */         ignoreWires.add(outs.get(0));
/* 316 */         if (assignment[((Integer)ins.get(0)).intValue()].signum() == 0) {
/* 317 */           assignment[((Integer)outs.get(1)).intValue()] = BigInteger.ZERO;
/*     */         } else {
/*     */           
/* 320 */           assignment[((Integer)outs.get(1)).intValue()] = BigInteger.ONE;
/*     */         } 
/* 322 */         wiresToReport.add(outs.get(1)); continue;
/*     */       } 
/* 324 */       if (line.startsWith("split ")) {
/* 325 */         if (outs.size() < assignment[((Integer)ins.get(0)).intValue()].bitLength()) {
/*     */           
/* 327 */           System.err.println("Error in Split");
/* 328 */           System.out.println(assignment[((Integer)ins.get(0)).intValue()].toString(16));
/* 329 */           System.out.println(line);
/*     */         } 
/* 331 */         for (int j = 0; j < outs.size(); j++) {
/* 332 */           assignment[((Integer)outs.get(j)).intValue()] = 
/* 333 */             assignment[((Integer)ins.get(0)).intValue()].testBit(j) ? BigInteger.ONE : BigInteger.ZERO;
/* 334 */           wiresToReport.add(outs.get(j));
/*     */         } 
/*     */         continue;
/*     */       } 
/* 338 */       if (line.startsWith("pack ")) {
/*     */         
/* 340 */         BigInteger sum = BigInteger.ZERO;
/* 341 */         for (int j = 0; j < ins.size(); j++) {
/* 342 */           sum = sum.add(assignment[((Integer)ins.get(j)).intValue()]
/* 343 */               .multiply((new BigInteger("2")).pow(j)));
/*     */         }
/* 345 */         wiresToReport.add(outs.get(0));
/* 346 */         assignment[((Integer)outs.get(0)).intValue()] = sum; continue;
/* 347 */       }  if (line.startsWith("const-mul-neg-")) {
/* 348 */         String constantStr = line.substring(
/* 349 */             "const-mul-neg-".length(), line.indexOf(" "));
/* 350 */         BigInteger constant = prime.subtract(new BigInteger(
/* 351 */               constantStr, 16));
/* 352 */         assignment[((Integer)outs.get(0)).intValue()] = assignment[((Integer)ins.get(0)).intValue()].multiply(
/* 353 */             constant).mod(prime); continue;
/* 354 */       }  if (line.startsWith("const-mul-")) {
/* 355 */         String constantStr = line.substring("const-mul-".length(), 
/* 356 */             line.indexOf(" "));
/* 357 */         BigInteger constant = new BigInteger(constantStr, 16);
/* 358 */         assignment[((Integer)outs.get(0)).intValue()] = assignment[((Integer)ins.get(0)).intValue()].multiply(
/* 359 */             constant).mod(prime); continue;
/*     */       } 
/* 361 */       System.err.println("Unknown Circuit Statement");
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 367 */     for (int i = 0; i < totalWires; i++) {
/* 368 */       if (assignment[i] == null && !ignoreWires.contains(Integer.valueOf(i))) {
/* 369 */         System.out.println("Wire " + i + " is Null");
/*     */       }
/*     */     } 
/*     */     
/* 373 */     circuitScanner.close();
/* 374 */     inFileScanner.close();
/*     */     
/* 376 */     PrintWriter printWriter = new PrintWriter(String.valueOf(inFilePath) + ".full.2");
/* 377 */     for (Iterator<Integer> iterator = wiresToReport.iterator(); iterator.hasNext(); ) { int id = ((Integer)iterator.next()).intValue();
/* 378 */       printWriter.println(String.valueOf(id) + " " + assignment[id].toString(16)); }
/*     */     
/* 380 */     printWriter.close();
/*     */   }
/*     */ 
/*     */   
/*     */   private static ArrayList<Integer> getOutputs(String line) {
/* 385 */     Scanner scanner = new Scanner(line.substring(line.lastIndexOf("<") + 1, 
/* 386 */           line.lastIndexOf(">")));
/* 387 */     ArrayList<Integer> outs = new ArrayList<>();
/* 388 */     while (scanner.hasNextInt()) {
/* 389 */       int v = scanner.nextInt();
/*     */       
/* 391 */       outs.add(Integer.valueOf(v));
/*     */     } 
/* 393 */     scanner.close();
/* 394 */     return outs;
/*     */   }
/*     */   
/*     */   private static ArrayList<Integer> getInputs(String line) {
/* 398 */     Scanner scanner = new Scanner(line.substring(line.indexOf("<") + 1, 
/* 399 */           line.indexOf(">")));
/* 400 */     ArrayList<Integer> ins = new ArrayList<>();
/* 401 */     while (scanner.hasNextInt()) {
/* 402 */       ins.add(Integer.valueOf(scanner.nextInt()));
/*     */     }
/* 404 */     scanner.close();
/* 405 */     return ins;
/*     */   }
/*     */   
/*     */   public BigInteger[] getAssignment() {
/* 409 */     return this.valueAssignment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWireValue(PackedValue packedWire, BigInteger rnd, int bitWidth, int bitwidth_per_chunk) {
/* 417 */     Wire[] array = packedWire.getArray();
/* 418 */     if (bitWidth <= UnsignedInteger.BITWIDTH_LIMIT_SHORT && 
/* 419 */       array.length == 1) {
/*     */       
/* 421 */       setWireValue(array[0], rnd);
/*     */     } else {
/* 423 */       BigInteger[] chunks = Util.split(rnd, array.length, 
/* 424 */           bitwidth_per_chunk);
/* 425 */       for (int i = 0; i < array.length; i++) {
/* 426 */         setWireValue(array[i], chunks[i]);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BigInteger getWireValue(PackedValue packedWire, int bitwidth_per_chunk) {
/* 434 */     return Util.combine(this.valueAssignment, packedWire.getArray(), 
/* 435 */         bitwidth_per_chunk);
/*     */   }
/*     */ 
/*     */   
/*     */   public HashMap<String, int[]> getPermutations() {
/* 440 */     return this.permutations;
/*     */   }
/*     */   
/*     */   public void setPermutations(HashMap<String, int[]> permutations) {
/* 444 */     this.permutations = permutations;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\eval\CircuitEvaluator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */