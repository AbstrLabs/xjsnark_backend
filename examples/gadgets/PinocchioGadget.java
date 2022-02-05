/*     */ package examples.gadgets;
/*     */ 
/*     */ import backend.operations.Gadget;
/*     */ import backend.structure.Wire;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.Scanner;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PinocchioGadget
/*     */   extends Gadget
/*     */ {
/*     */   private Wire[] inputWires;
/*     */   private Wire[] proverWitnessWires;
/*     */   private Wire[] outputWires;
/*     */   
/*     */   public PinocchioGadget(Wire[] inputWires, String pathToArithFile, String... desc) {
/*  22 */     super(desc);
/*  23 */     this.inputWires = inputWires;
/*     */     try {
/*  25 */       buildCircuit(pathToArithFile);
/*  26 */     } catch (Exception e) {
/*  27 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void buildCircuit(String path) throws FileNotFoundException {
/*  33 */     ArrayList<Wire> proverWitnessWires = new ArrayList<>();
/*  34 */     ArrayList<Wire> outputWires = new ArrayList<>();
/*     */ 
/*     */     
/*  37 */     Scanner scanner = new Scanner(new File(path));
/*     */     
/*  39 */     if (!scanner.next().equals("total")) {
/*  40 */       scanner.close();
/*  41 */       throw new RuntimeException("Expected total %d in the first line");
/*     */     } 
/*  43 */     int numWires = scanner.nextInt();
/*  44 */     scanner.nextLine();
/*  45 */     Wire[] wireMapping = new Wire[numWires];
/*     */     
/*  47 */     int inputCount = 0;
/*  48 */     while (scanner.hasNext()) {
/*  49 */       String line = scanner.nextLine();
/*     */       
/*  51 */       if (line.contains("#")) {
/*  52 */         line = line.substring(0, line.indexOf("#"));
/*     */       }
/*  54 */       if (line.equals(""))
/*     */         continue; 
/*  56 */       if (line.startsWith("input")) {
/*  57 */         String[] tokens = line.split("\\s+");
/*  58 */         int wireIndex = Integer.parseInt(tokens[1]);
/*  59 */         if (wireMapping[wireIndex] != null) {
/*  60 */           throwParsingError(scanner, "Wire assigned twice! " + wireIndex);
/*     */         }
/*  62 */         if (inputCount < this.inputWires.length) {
/*  63 */           wireMapping[wireIndex] = this.inputWires[inputCount];
/*     */         } else {
/*     */           
/*  66 */           wireMapping[wireIndex] = this.generator.__getOneWire();
/*     */         } 
/*  68 */         inputCount++; continue;
/*  69 */       }  if (line.startsWith("output")) {
/*  70 */         String[] tokens = line.split("\\s+");
/*  71 */         int wireIndex = Integer.parseInt(tokens[1]);
/*  72 */         outputWires.add(wireMapping[wireIndex]); continue;
/*  73 */       }  if (line.startsWith("nizk")) {
/*  74 */         String[] tokens = line.split("\\s+");
/*  75 */         int wireIndex = Integer.parseInt(tokens[1]);
/*  76 */         if (wireMapping[wireIndex] != null) {
/*  77 */           throwParsingError(scanner, "Wire assigned twice! " + wireIndex);
/*     */         }
/*  79 */         Wire w = this.generator.__createProverWitnessWire(new String[0]);
/*  80 */         proverWitnessWires.add(w);
/*  81 */         wireMapping[wireIndex] = w; continue;
/*     */       } 
/*  83 */       ArrayList<Integer> ins = getInputs(line);
/*  84 */       for (Iterator<Integer> iterator = ins.iterator(); iterator.hasNext(); ) { int in = ((Integer)iterator.next()).intValue();
/*  85 */         if (wireMapping[in] == null) {
/*  86 */           throwParsingError(scanner, "Undefined input wire " + in + " at line " + line);
/*     */         } }
/*     */       
/*  89 */       ArrayList<Integer> outs = getOutputs(line);
/*  90 */       if (line.startsWith("mul ")) {
/*  91 */         wireMapping[((Integer)outs.get(0)).intValue()] = wireMapping[((Integer)ins.get(0)).intValue()].mul(wireMapping[((Integer)ins.get(1)).intValue()], new String[0]); continue;
/*  92 */       }  if (line.startsWith("add ")) {
/*  93 */         Wire result = wireMapping[((Integer)ins.get(0)).intValue()];
/*  94 */         for (int i = 1; i < ins.size(); i++) {
/*  95 */           result = result.add(wireMapping[((Integer)ins.get(i)).intValue()], new String[0]);
/*     */         }
/*  97 */         wireMapping[((Integer)outs.get(0)).intValue()] = result; continue;
/*  98 */       }  if (line.startsWith("zerop ")) {
/*  99 */         wireMapping[((Integer)outs.get(1)).intValue()] = wireMapping[((Integer)ins.get(0)).intValue()].checkNonZero(new String[0]); continue;
/* 100 */       }  if (line.startsWith("split ")) {
/* 101 */         Wire[] bits = wireMapping[((Integer)ins.get(0)).intValue()].getBitWires(outs.size(), new String[0]).asArray();
/* 102 */         for (int i = 0; i < outs.size(); i++)
/* 103 */           wireMapping[((Integer)outs.get(i)).intValue()] = bits[i];  continue;
/*     */       } 
/* 105 */       if (line.startsWith("const-mul-neg-")) {
/* 106 */         String constantStr = line.substring("const-mul-neg-".length(), line.indexOf(" "));
/* 107 */         BigInteger constant = new BigInteger(constantStr, 16);
/* 108 */         wireMapping[((Integer)outs.get(0)).intValue()] = wireMapping[((Integer)ins.get(0)).intValue()].mul(constant.negate(), new String[0]); continue;
/* 109 */       }  if (line.startsWith("const-mul-")) {
/* 110 */         String constantStr = line.substring("const-mul-".length(), line.indexOf(" "));
/* 111 */         BigInteger constant = new BigInteger(constantStr, 16);
/* 112 */         wireMapping[((Integer)outs.get(0)).intValue()] = wireMapping[((Integer)ins.get(0)).intValue()].mul(constant, new String[0]); continue;
/*     */       } 
/* 114 */       throwParsingError(scanner, "Unsupport Circuit Line " + line);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 120 */     scanner.close();
/*     */     
/* 122 */     this.proverWitnessWires = new Wire[proverWitnessWires.size()];
/* 123 */     proverWitnessWires.toArray(this.proverWitnessWires);
/* 124 */     this.outputWires = new Wire[outputWires.size()];
/* 125 */     outputWires.toArray(this.outputWires);
/*     */   }
/*     */   
/*     */   private ArrayList<Integer> getOutputs(String line) {
/* 129 */     Scanner scanner = new Scanner(line.substring(line.lastIndexOf("<") + 1, line.lastIndexOf(">")));
/* 130 */     ArrayList<Integer> outs = new ArrayList<>();
/* 131 */     while (scanner.hasNextInt()) {
/* 132 */       int v = scanner.nextInt();
/* 133 */       outs.add(Integer.valueOf(v));
/*     */     } 
/* 135 */     scanner.close();
/* 136 */     return outs;
/*     */   }
/*     */   
/*     */   private ArrayList<Integer> getInputs(String line) {
/* 140 */     Scanner scanner = new Scanner(line.substring(line.indexOf("<") + 1, line.indexOf(">")));
/* 141 */     ArrayList<Integer> ins = new ArrayList<>();
/* 142 */     while (scanner.hasNextInt()) {
/* 143 */       ins.add(Integer.valueOf(scanner.nextInt()));
/*     */     }
/* 145 */     scanner.close();
/* 146 */     return ins;
/*     */   }
/*     */ 
/*     */   
/*     */   public Wire[] getOutputWires() {
/* 151 */     return this.outputWires;
/*     */   }
/*     */   
/*     */   public Wire[] getProverWitnessWires() {
/* 155 */     return this.proverWitnessWires;
/*     */   }
/*     */   
/*     */   private void throwParsingError(Scanner s, String m) {
/* 159 */     s.close();
/* 160 */     throw new RuntimeException(m);
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\PinocchioGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */