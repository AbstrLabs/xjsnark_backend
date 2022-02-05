/*      */ package backend.auxTypes;
/*      */ 
/*      */ import backend.config.Config;
/*      */ import backend.eval.CircuitEvaluator;
/*      */ import backend.eval.Instruction;
/*      */ import backend.structure.CircuitGenerator;
/*      */ import backend.structure.ConstantWire;
/*      */ import backend.structure.Wire;
/*      */ import backend.structure.WireArray;
/*      */ import java.math.BigInteger;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import util.Util;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PackedValue
/*      */ {
/*      */   Wire[] array;
/*      */   int[] currentBitwidth;
/*      */   BigInteger[] currentMaxValues;
/*      */   WireArray bits;
/*      */   int shift;
/*      */   CircuitGenerator generator;
/*      */   boolean witnessIndicator;
/*      */   static boolean disableOverflowChecks = false;
/*      */   
/*      */   public PackedValue(CircuitGenerator generator, Wire[] array, BigInteger[] currentMaxValues, int shift) {
/*   29 */     this.generator = generator;
/*   30 */     this.array = array;
/*   31 */     this.currentMaxValues = currentMaxValues;
/*   32 */     this.currentBitwidth = new int[currentMaxValues.length];
/*   33 */     for (int i = 0; i < this.currentBitwidth.length; i++) {
/*   34 */       this.currentBitwidth[i] = currentMaxValues[i].bitLength();
/*      */     }
/*   36 */     this.shift = shift;
/*      */   }
/*      */   
/*      */   public PackedValue(Wire w, int currentBitwidth) {
/*   40 */     this.array = new Wire[] { w };
/*   41 */     this.currentBitwidth = new int[] { currentBitwidth };
/*      */ 
/*      */     
/*   44 */     this.currentMaxValues = new BigInteger[] {
/*   45 */         Util.computeMaxValue(currentBitwidth) };
/*   46 */     this.shift = 1;
/*   47 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*   48 */     if (currentBitwidth == 1) {
/*   49 */       this.bits = new WireArray(this.array);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PackedValue(WireArray bits, int bitwidthPerChunk) {
/*   57 */     if (bitwidthPerChunk >= bits.size()) {
/*      */       
/*   59 */       this.array = new Wire[] { bits.packAsBits(bits.size(), new String[0]) };
/*   60 */       this.currentMaxValues = new BigInteger[] {
/*   61 */           Util.computeMaxValue(bits.size())
/*      */         };
/*   63 */       this.shift = 1;
/*      */     } else {
/*   65 */       BigInteger maxChunkVal = Util.computeMaxValue(bitwidthPerChunk);
/*   66 */       BigInteger maxLastChunkVal = maxChunkVal;
/*   67 */       int size = bits.size();
/*   68 */       if (size % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/*   69 */         bits = bits.adjustLength(size + 
/*   70 */             UnsignedInteger.BITWIDTH_PER_CHUNK - size % 
/*   71 */             UnsignedInteger.BITWIDTH_PER_CHUNK);
/*   72 */         maxLastChunkVal = Util.computeMaxValue(size % 
/*   73 */             UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */       } 
/*   75 */       this.array = new Wire[bits.size() / bitwidthPerChunk];
/*   76 */       this.currentMaxValues = new BigInteger[this.array.length];
/*      */ 
/*      */       
/*   79 */       for (int j = 0; j < this.array.length; j++) {
/*   80 */         this.array[j] = (new WireArray(Arrays.<Wire>copyOfRange(
/*   81 */               bits.asArray(), j * bitwidthPerChunk, (j + 1) * 
/*   82 */               bitwidthPerChunk))).packAsBits(new String[0]);
/*      */         
/*   84 */         if (j == this.array.length - 1) {
/*   85 */           this.currentMaxValues[j] = maxLastChunkVal;
/*      */         } else {
/*   87 */           this.currentMaxValues[j] = maxChunkVal;
/*      */         } 
/*      */       } 
/*      */       
/*   91 */       this.shift = bitwidthPerChunk;
/*      */     } 
/*   93 */     this.bits = bits;
/*   94 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*   95 */     this.currentBitwidth = new int[this.array.length];
/*   96 */     for (int i = 0; i < this.array.length; i++) {
/*   97 */       this.currentBitwidth[i] = this.currentMaxValues[i].bitLength();
/*      */     }
/*      */   }
/*      */   
/*      */   public PackedValue(Wire w, BigInteger currentMaxValue) {
/*  102 */     this.array = new Wire[] { w };
/*  103 */     this.currentMaxValues = new BigInteger[] { currentMaxValue };
/*  104 */     this.currentBitwidth = new int[] { currentMaxValue.bitLength() };
/*  105 */     this.shift = 1;
/*  106 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public PackedValue(Wire[] w, int[] currentBitwidth) {
/*  112 */     this.array = w;
/*  113 */     this.currentBitwidth = currentBitwidth;
/*  114 */     this.currentMaxValues = new BigInteger[w.length];
/*  115 */     for (int i = 0; i < w.length; i++) {
/*  116 */       this.currentMaxValues[i] = Util.computeMaxValue(currentBitwidth[i]);
/*      */     }
/*      */     
/*  119 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PackedValue(BigInteger[] chunks) {
/*  135 */     this.currentMaxValues = chunks;
/*  136 */     this.currentBitwidth = new int[chunks.length];
/*      */     
/*  138 */     for (int i = 0; i < chunks.length; i++) {
/*  139 */       this.currentBitwidth[i] = this.currentMaxValues[i].bitLength();
/*      */     }
/*  141 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  142 */     this.array = this.generator.__createConstantWireArray(chunks, new String[0]);
/*      */   }
/*      */ 
/*      */   
/*      */   public PackedValue(Wire[] w, BigInteger[] currentMaxValues) {
/*  147 */     this.array = w;
/*  148 */     this.currentMaxValues = currentMaxValues;
/*  149 */     this.currentBitwidth = new int[w.length];
/*  150 */     for (int i = 0; i < w.length; i++) {
/*  151 */       this.currentBitwidth[i] = currentMaxValues[i].bitLength();
/*      */     }
/*  153 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*      */   }
/*      */   
/*      */   public boolean addOverflowCheck(PackedValue o) {
/*  157 */     int length = Math.min(this.array.length, o.array.length);
/*  158 */     boolean overflow = false;
/*  159 */     for (int i = 0; i < length; i++) {
/*  160 */       BigInteger max1 = (i < this.array.length) ? this.currentMaxValues[i] : 
/*  161 */         BigInteger.ZERO;
/*  162 */       BigInteger max2 = (i < o.array.length) ? o.currentMaxValues[i] : 
/*  163 */         BigInteger.ZERO;
/*  164 */       if (max1.add(max2).compareTo(Config.getFiniteFieldModulus()) >= 0) {
/*  165 */         overflow = true;
/*      */         break;
/*      */       } 
/*      */     } 
/*  169 */     return overflow;
/*      */   }
/*      */   
/*      */   public boolean mulOverflowCheck(PackedValue o) {
/*  173 */     int length = this.array.length + o.array.length - 1;
/*  174 */     BigInteger[] newMaxValues = new BigInteger[length];
/*  175 */     Arrays.fill((Object[])newMaxValues, BigInteger.ZERO);
/*  176 */     for (int i = 0; i < this.array.length; i++) {
/*  177 */       for (int k = 0; k < o.array.length; k++)
/*  178 */         newMaxValues[i + k] = newMaxValues[i + k]
/*  179 */           .add(this.currentMaxValues[i]
/*  180 */             .multiply(o.currentMaxValues[k])); 
/*      */     }  byte b; int j;
/*      */     BigInteger[] arrayOfBigInteger1;
/*  183 */     for (j = (arrayOfBigInteger1 = newMaxValues).length, b = 0; b < j; ) { BigInteger bigInteger = arrayOfBigInteger1[b];
/*  184 */       if (bigInteger.compareTo(Config.getFiniteFieldModulus()) >= 0)
/*  185 */         return true; 
/*      */       b++; }
/*      */     
/*  188 */     return false;
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean mulAddOverflowCheck(PackedValue p1, PackedValue p2) {
/*  193 */     int length = this.array.length + p1.array.length - 1;
/*  194 */     BigInteger[] newMaxValues = new BigInteger[length];
/*  195 */     Arrays.fill((Object[])newMaxValues, BigInteger.ZERO); int i;
/*  196 */     for (i = 0; i < this.array.length; i++) {
/*  197 */       for (int k = 0; k < p1.array.length; k++) {
/*  198 */         newMaxValues[i + k] = newMaxValues[i + k]
/*  199 */           .add(this.currentMaxValues[i]
/*  200 */             .multiply(p1.currentMaxValues[k]));
/*      */       }
/*      */     } 
/*  203 */     for (i = 0; i < length; i++) {
/*  204 */       BigInteger bigInteger = newMaxValues[i];
/*  205 */       if (i < p2.array.length)
/*  206 */         bigInteger = bigInteger.add(p2.currentMaxValues[i]); 
/*  207 */       if (bigInteger.compareTo(Config.getFiniteFieldModulus()) >= 0)
/*  208 */         return true; 
/*      */     }  byte b;
/*      */     int j;
/*      */     BigInteger[] arrayOfBigInteger1;
/*  212 */     for (j = (arrayOfBigInteger1 = newMaxValues).length, b = 0; b < j; ) { BigInteger bigInteger = arrayOfBigInteger1[b];
/*  213 */       if (bigInteger.compareTo(Config.getFiniteFieldModulus()) >= 0)
/*  214 */         return true; 
/*      */       b++; }
/*      */     
/*  217 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public PackedValue add(PackedValue o) {
/*  223 */     int length = Math.max(this.array.length, o.array.length);
/*  224 */     Wire[] w1 = (new WireArray(this.array)).adjustLength(length).asArray();
/*  225 */     Wire[] w2 = (new WireArray(o.array)).adjustLength(length).asArray();
/*  226 */     Wire[] result = new Wire[length];
/*  227 */     BigInteger[] newMaxValues = new BigInteger[length]; int i;
/*  228 */     for (i = 0; i < length; i++) {
/*  229 */       result[i] = w1[i].add(w2[i], new String[0]);
/*  230 */       BigInteger max1 = (i < this.array.length) ? this.currentMaxValues[i] : 
/*  231 */         BigInteger.ZERO;
/*  232 */       BigInteger max2 = (i < o.array.length) ? o.currentMaxValues[i] : 
/*  233 */         BigInteger.ZERO;
/*      */ 
/*      */       
/*  236 */       newMaxValues[i] = max1.add(max2);
/*  237 */       if (newMaxValues[i].compareTo(Config.getFiniteFieldModulus()) >= 0 && !disableOverflowChecks) {
/*  238 */         System.err.println("An unhandled case of possible overflow was detected in addition. (Sanity check failed)");
/*  239 */         throw new RuntimeException();
/*      */       } 
/*      */     } 
/*      */     
/*  243 */     for (i = 0; i < newMaxValues.length; i++) {
/*  244 */       if (newMaxValues[i].compareTo(Config.getFiniteFieldModulus()) >= 0) {
/*  245 */         newMaxValues[i] = Config.getFiniteFieldModulus().subtract(BigInteger.ONE);
/*      */       }
/*      */     } 
/*      */     
/*  249 */     return new PackedValue(result, newMaxValues);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PackedValue mul(PackedValue o) {
/*      */     final Wire[] result;
/*  260 */     int length = this.array.length + o.array.length - 1;
/*      */ 
/*      */ 
/*      */     
/*  264 */     if (o.array.length == 1 || this.array.length == 1 || isConstant() || 
/*  265 */       o.isConstant()) {
/*  266 */       result = new Wire[length];
/*  267 */       Arrays.fill((Object[])result, this.generator.__getZeroWire());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  274 */       for (int j = 0; j < this.array.length; j++) {
/*  275 */         for (int k = 0; k < o.array.length; k++) {
/*  276 */           if (j == 0 && k == 0) {
/*      */             
/*  278 */             result[0] = this.array[0].mul(o.array[0], new String[0]);
/*      */           } else {
/*  280 */             result[j + k] = result[j + k].add(this.array[j].mul(o.array[k], new String[0]), new String[0]);
/*      */           }
/*      */         
/*      */         }
/*      */       
/*      */       } 
/*      */     } else {
/*      */       
/*  288 */       result = this.generator.__createProverWitnessWireArray(length, new String[0]);
/*      */ 
/*      */       
/*  291 */       final Wire[] array1 = this.array;
/*  292 */       final Wire[] array2 = o.array;
/*  293 */       this.generator.__specifyProverWitnessComputation(new Instruction()
/*      */           {
/*      */             public void evaluate(CircuitEvaluator evaluator) {
/*  296 */               BigInteger[] a = evaluator
/*  297 */                 .getWiresValues(array1);
/*  298 */               BigInteger[] b = evaluator.getWiresValues(array2);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  308 */               BigInteger[] resultVals = PackedValue.this.multiplyPolys(a, b);
/*  309 */               evaluator.setWireValue(result, resultVals);
/*      */             }
/*      */           });
/*      */       
/*  313 */       Wire zeroWire = this.generator.__getZeroWire();
/*  314 */       for (int k = 0; k < length; k++) {
/*  315 */         BigInteger constant = new BigInteger((new StringBuilder(String.valueOf(k + 1))).toString());
/*  316 */         Wire v1 = zeroWire;
/*  317 */         Wire v2 = zeroWire;
/*  318 */         Wire v3 = zeroWire;
/*  319 */         BigInteger coeff = BigInteger.ONE;
/*      */         
/*  321 */         Wire[] vector1 = new Wire[this.array.length];
/*  322 */         Wire[] vector2 = new Wire[o.array.length];
/*  323 */         Wire[] vector3 = new Wire[length];
/*  324 */         for (int j = 0; j < length; j++) {
/*  325 */           if (j < this.array.length) {
/*  326 */             vector1[j] = this.array[j].mul(coeff, new String[0]);
/*      */           }
/*  328 */           if (j < o.array.length) {
/*  329 */             vector2[j] = o.array[j].mul(coeff, new String[0]);
/*      */           }
/*  331 */           vector3[j] = result[j].mul(coeff, new String[0]);
/*  332 */           coeff = coeff.multiply(constant).mod(Config.getFiniteFieldModulus());
/*      */         } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  345 */         v1 = (new WireArray(vector1)).sumAllElements(new String[0]);
/*  346 */         v2 = (new WireArray(vector2)).sumAllElements(new String[0]);
/*  347 */         v3 = (new WireArray(vector3)).sumAllElements(new String[0]);
/*  348 */         this.generator.__addAssertion(v1, v2, v3, new String[0]);
/*      */       } 
/*      */     } 
/*      */     
/*  352 */     BigInteger[] newMaxValues = new BigInteger[length];
/*  353 */     Arrays.fill((Object[])newMaxValues, BigInteger.ZERO); int i;
/*  354 */     for (i = 0; i < this.array.length; i++) {
/*  355 */       for (int j = 0; j < o.array.length; j++) {
/*  356 */         newMaxValues[i + j] = newMaxValues[i + j]
/*  357 */           .add(this.currentMaxValues[i]
/*  358 */             .multiply(o.currentMaxValues[j]));
/*      */         
/*  360 */         if (newMaxValues[i + j].compareTo(Config.getFiniteFieldModulus()) >= 0 && !disableOverflowChecks) {
/*  361 */           System.err.println("An unhandled case of possible overflow was detected in multiplication. (Sanity check failed)");
/*  362 */           throw new RuntimeException();
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  367 */     for (i = 0; i < newMaxValues.length; i++) {
/*  368 */       if (newMaxValues[i].compareTo(Config.getFiniteFieldModulus()) >= 0) {
/*  369 */         newMaxValues[i] = Config.getFiniteFieldModulus().subtract(BigInteger.ONE);
/*      */       }
/*      */     } 
/*      */     
/*  373 */     return new PackedValue(result, newMaxValues);
/*      */   }
/*      */ 
/*      */   
/*      */   public PackedValue mul2(final PackedValue o) {
/*      */     final Wire[] result;
/*  379 */     int length = this.array.length + o.array.length - 1;
/*      */     
/*  381 */     if (o.array.length == 1 || this.array.length == 1 || isConstant() || o.isConstant()) {
/*  382 */       result = new Wire[length];
/*  383 */       Arrays.fill((Object[])result, this.generator.__getZeroWire());
/*  384 */       for (int j = 0; j < this.array.length; j++) {
/*  385 */         for (int k = 0; k < o.array.length; k++) {
/*  386 */           result[j + k] = result[j + k].add(this.array[j].mul(o.array[k], new String[0]), new String[0]);
/*      */         }
/*      */       } 
/*      */     } else {
/*  390 */       result = this.generator.__createProverWitnessWireArray(length, new String[0]);
/*  391 */       for (int j = 0; j < result.length; j++);
/*      */ 
/*      */       
/*  394 */       this.generator.__specifyProverWitnessComputation(new Instruction()
/*      */           {
/*      */             public void evaluate(CircuitEvaluator evaluator) {
/*  397 */               BigInteger[] a = evaluator
/*  398 */                 .getWiresValues(PackedValue.this.array);
/*  399 */               BigInteger[] b = evaluator.getWiresValues(o.array);
/*  400 */               BigInteger[] resultVals = PackedValue.this.multiplyPolys(a, b);
/*  401 */               evaluator.setWireValue(result, resultVals);
/*      */             }
/*      */           });
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  408 */       Wire zeroWire = this.generator.__getZeroWire();
/*  409 */       for (int k = 0; k < length; k++) {
/*  410 */         BigInteger constant = new BigInteger((new StringBuilder(String.valueOf(k + 1))).toString());
/*  411 */         Wire v1 = zeroWire;
/*  412 */         Wire v2 = zeroWire;
/*  413 */         Wire v3 = zeroWire;
/*  414 */         BigInteger coeff = BigInteger.ONE;
/*  415 */         for (int v = 0; v < length; v++) {
/*  416 */           if (v < this.array.length)
/*  417 */             v1 = v1.add(this.array[v].mul(coeff, new String[0]), new String[0]); 
/*  418 */           if (v < o.array.length)
/*  419 */             v2 = v2.add(o.array[v].mul(coeff, new String[0]), new String[0]); 
/*  420 */           v3 = v3.add(result[v].mul(coeff, new String[0]), new String[0]);
/*  421 */           coeff = coeff.multiply(constant).mod(Config.getFiniteFieldModulus());
/*      */         } 
/*  423 */         this.generator.__addAssertion(v1, v2, v3, new String[0]);
/*      */       } 
/*      */     } 
/*      */     
/*  427 */     BigInteger[] newMaxValues = new BigInteger[length];
/*  428 */     Arrays.fill((Object[])newMaxValues, BigInteger.ZERO); int i;
/*  429 */     for (i = 0; i < this.array.length; i++) {
/*  430 */       for (int j = 0; j < o.array.length; j++) {
/*  431 */         newMaxValues[i + j] = newMaxValues[i + j]
/*  432 */           .add(this.currentMaxValues[i]
/*  433 */             .multiply(o.currentMaxValues[j]));
/*  434 */         if (newMaxValues[i + j].compareTo(Config.getFiniteFieldModulus()) >= 0 && !disableOverflowChecks) {
/*  435 */           System.err.println("An unhandled case of possible overflow was detected. (Sanity check failed)");
/*  436 */           throw new RuntimeException();
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/*  441 */     for (i = 0; i < newMaxValues.length; i++) {
/*  442 */       if (newMaxValues[i].compareTo(Config.getFiniteFieldModulus()) >= 0) {
/*  443 */         newMaxValues[i] = Config.getFiniteFieldModulus().subtract(BigInteger.ONE);
/*      */       }
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  451 */     return new PackedValue(result, newMaxValues);
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean isConstant() {
/*  456 */     boolean constant = true;
/*  457 */     if (this.array != null) {
/*  458 */       for (int i = 0; i < this.array.length; i++) {
/*  459 */         constant &= this.array[i] instanceof ConstantWire;
/*      */       }
/*      */     }
/*  462 */     return constant;
/*      */   }
/*      */   
/*      */   public int getSize() {
/*  466 */     return this.array.length;
/*      */   }
/*      */ 
/*      */   
/*      */   public PackedValue align(int totalNumChunks, int bitwidthPerChunk) {
/*  471 */     Wire[] newArray = Arrays.<Wire>copyOfRange(this.array, 0, totalNumChunks);
/*  472 */     for (int i = 0; i < newArray.length; i++) {
/*  473 */       if (newArray[i] == null) {
/*  474 */         newArray[i] = this.generator.__getZeroWire();
/*      */       }
/*      */     } 
/*  477 */     BigInteger[] newMaxValues = new BigInteger[totalNumChunks];
/*  478 */     Arrays.fill((Object[])newMaxValues, BigInteger.ZERO);
/*  479 */     int[] newBitwidths = new int[totalNumChunks];
/*  480 */     Arrays.fill(newBitwidths, 0);
/*      */     
/*  482 */     System.arraycopy(this.currentMaxValues, 0, newMaxValues, 0, Math.min(totalNumChunks, this.currentMaxValues.length));
/*      */ 
/*      */     
/*  485 */     for (int j = 0; j < totalNumChunks; j++) {
/*      */ 
/*      */       
/*  488 */       if (newMaxValues[j].bitLength() > bitwidthPerChunk) {
/*  489 */         Wire[] chunkBits = newArray[j].getBitWires(newMaxValues[j].bitLength(), new String[0])
/*  490 */           .asArray();
/*  491 */         newArray[j] = (new WireArray(Arrays.<Wire>copyOfRange(chunkBits, 0, 
/*  492 */               bitwidthPerChunk))).packAsBits(new String[0]);
/*  493 */         Wire rem = (new WireArray(Arrays.<Wire>copyOfRange(chunkBits, 
/*  494 */               bitwidthPerChunk, newMaxValues[j].bitLength()))).packAsBits(new String[0]);
/*  495 */         if (j != totalNumChunks - 1) {
/*  496 */           newMaxValues[j + 1] = newMaxValues[j].shiftRight(
/*  497 */               bitwidthPerChunk).add(newMaxValues[j + 1]);
/*  498 */           newArray[j + 1] = rem.add(newArray[j + 1], new String[0]);
/*      */         } 
/*  500 */         newMaxValues[j] = Util.computeMaxValue(bitwidthPerChunk);
/*  501 */         newBitwidths[j] = bitwidthPerChunk;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  507 */     return new PackedValue(this.generator, newArray, newMaxValues, this.shift);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public WireArray getBits(int totalBitwidth, int bitwidthPerChunk, String... desc) {
/*  583 */     if (this.bits != null)
/*  584 */       return this.bits.adjustLength(totalBitwidth); 
/*  585 */     if (totalBitwidth == bitwidthPerChunk) {
/*  586 */       if (this.array.length == 1) {
/*      */         
/*  588 */         this.bits = this.array[0].getBitWires(this.currentMaxValues[0].bitLength(), desc);
/*  589 */         this.bits = this.bits.adjustLength(totalBitwidth);
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */         
/*  595 */         return null;
/*      */       } 
/*      */     } else {
/*      */       Wire[] bitWires;
/*  599 */       int limit = totalBitwidth;
/*      */ 
/*      */       
/*  602 */       if (totalBitwidth != -1) {
/*  603 */         bitWires = new Wire[totalBitwidth];
/*      */       } else {
/*      */         
/*  606 */         BigInteger maxVal = getMaxVal(bitwidthPerChunk);
/*  607 */         bitWires = new Wire[maxVal.bitLength()];
/*  608 */         limit = maxVal.bitLength();
/*      */       } 
/*  610 */       Arrays.fill((Object[])bitWires, this.generator.__getZeroWire());
/*  611 */       int newLength = (int)Math.ceil(getMaxVal(bitwidthPerChunk).bitLength() * 1.0D / bitwidthPerChunk);
/*  612 */       if (newLength == 0)
/*  613 */         newLength++; 
/*  614 */       Wire[] newArray = new Wire[newLength];
/*  615 */       BigInteger[] newMaxValues = new BigInteger[newLength];
/*  616 */       Arrays.fill((Object[])newMaxValues, BigInteger.ZERO);
/*  617 */       Arrays.fill((Object[])newArray, this.generator.__getZeroWire());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  625 */       System.arraycopy(this.currentMaxValues, 0, newMaxValues, 0, 
/*  626 */           this.currentMaxValues.length);
/*  627 */       System.arraycopy(this.array, 0, newArray, 0, this.array.length);
/*      */       
/*  629 */       int idx = 0;
/*  630 */       int chunkIndex = 0;
/*  631 */       while (idx < limit && chunkIndex < newLength) {
/*      */         Wire[] alignedChunkBits;
/*  633 */         if (newMaxValues[chunkIndex].bitLength() > bitwidthPerChunk) {
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  638 */           Wire[] chunkBits = newArray[chunkIndex].getBitWires(
/*  639 */               newMaxValues[chunkIndex].bitLength(), new String[0]).asArray();
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  644 */           alignedChunkBits = Arrays.<Wire>copyOfRange(chunkBits, 0, 
/*  645 */               bitwidthPerChunk);
/*  646 */           Wire rem = (new WireArray(Arrays.<Wire>copyOfRange(chunkBits, 
/*  647 */                 bitwidthPerChunk, newMaxValues[chunkIndex].bitLength())))
/*  648 */             .packAsBits(new String[0]);
/*      */ 
/*      */ 
/*      */           
/*  652 */           if (chunkIndex != newArray.length - 1) {
/*  653 */             newMaxValues[chunkIndex + 1] = newMaxValues[chunkIndex]
/*  654 */               .shiftRight(bitwidthPerChunk).add(
/*  655 */                 newMaxValues[chunkIndex + 1]);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  661 */             newArray[chunkIndex + 1] = rem
/*  662 */               .add(newArray[chunkIndex + 1], new String[0]);
/*      */ 
/*      */           
/*      */           }
/*      */ 
/*      */ 
/*      */         
/*      */         }
/*      */         else {
/*      */ 
/*      */           
/*  673 */           alignedChunkBits = newArray[chunkIndex].getBitWires(bitwidthPerChunk, new String[0]).asArray();
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  678 */         System.arraycopy(alignedChunkBits, 0, bitWires, idx, 
/*  679 */             Math.min(alignedChunkBits.length, limit - idx));
/*  680 */         chunkIndex++;
/*  681 */         idx += alignedChunkBits.length;
/*      */       } 
/*      */       
/*  684 */       this.bits = new WireArray(bitWires);
/*      */     } 
/*      */     
/*  687 */     return this.bits;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public BigInteger getMaxVal(int bitwidth) {
/*  700 */     return Util.group(this.currentMaxValues, bitwidth);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private BigInteger[] multiplyPolys(BigInteger[] aiVals, BigInteger[] biVals) {
/*  707 */     BigInteger[] solution = new BigInteger[aiVals.length + biVals.length - 
/*  708 */         1];
/*  709 */     Arrays.fill((Object[])solution, BigInteger.ZERO);
/*  710 */     for (int i = 0; i < aiVals.length; i++) {
/*  711 */       for (int j = 0; j < biVals.length; j++) {
/*  712 */         solution[i + j] = solution[i + j].add(
/*  713 */             aiVals[i].multiply(biVals[j])).mod(Config.getFiniteFieldModulus());
/*      */       }
/*      */     } 
/*  716 */     return solution;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PackedValue muxBit(PackedValue other, Wire w) {
/*  724 */     int length = Math.max(this.array.length, other.array.length);
/*  725 */     Wire[] newArray = new Wire[length];
/*  726 */     BigInteger[] newMaxValues = new BigInteger[length];
/*      */     
/*  728 */     for (int i = 0; i < length; i++) {
/*      */       
/*  730 */       BigInteger b1 = (i < this.array.length) ? this.currentMaxValues[i] : 
/*  731 */         BigInteger.ZERO;
/*  732 */       BigInteger b2 = (i < other.array.length) ? other.currentMaxValues[i] : 
/*  733 */         BigInteger.ZERO;
/*  734 */       newMaxValues[i] = (b1.compareTo(b2) == 1) ? b1 : b2;
/*      */       
/*  736 */       Wire w1 = (i < this.array.length) ? this.array[i] : this.generator.__getZeroWire();
/*  737 */       Wire w2 = (i < other.array.length) ? other.array[i] : this.generator
/*  738 */         .__getZeroWire();
/*      */       
/*  740 */       newArray[i] = w1.add(w.mul(w2.sub(w1, new String[0]), new String[0]), new String[0]);
/*  741 */       if (newArray[i] instanceof ConstantWire) {
/*  742 */         newMaxValues[i] = ((ConstantWire)newArray[i]).getConstant();
/*      */       }
/*      */     } 
/*      */     
/*  746 */     return new PackedValue(newArray, newMaxValues);
/*      */   }
/*      */   
/*      */   public PackedValue mulWire(Wire wire) {
/*  750 */     Wire[] newArray = new Wire[this.array.length];
/*  751 */     for (int i = 0; i < this.array.length; i++) {
/*  752 */       newArray[i] = this.array[i].mul(wire, new String[0]);
/*      */     }
/*  754 */     return new PackedValue(newArray, this.currentBitwidth);
/*      */   }
/*      */ 
/*      */   
/*      */   public Wire[] getArray() {
/*  759 */     return this.array;
/*      */   }
/*      */   
/*      */   public int[] getCurrentBitwidth() {
/*  763 */     return this.currentBitwidth;
/*      */   }
/*      */   
/*      */   public BigInteger[] getCurrentMaxValues() {
/*  767 */     return this.currentMaxValues;
/*      */   }
/*      */   
/*      */   public WireArray getBits() {
/*  771 */     return this.bits;
/*      */   }
/*      */   
/*      */   public int getShift() {
/*  775 */     return this.shift;
/*      */   }
/*      */   
/*      */   public BigInteger getConstant(int bitwidth_per_chunk) {
/*  779 */     BigInteger[] constants = new BigInteger[this.array.length];
/*  780 */     for (int i = 0; i < this.array.length; i++) {
/*  781 */       if (!(this.array[i] instanceof ConstantWire)) {
/*  782 */         return null;
/*      */       }
/*  784 */       constants[i] = ((ConstantWire)this.array[i]).getConstant();
/*      */     } 
/*      */     
/*  787 */     return Util.group(constants, bitwidth_per_chunk);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PackedValue addsub(BigInteger[] a, PackedValue o) {
/*  796 */     int length = Math.max(this.array.length, Math.max(a.length, o.array.length));
/*      */     
/*  798 */     Wire[] w1 = (new WireArray(this.array)).adjustLength(length).asArray();
/*  799 */     Wire[] w2 = (new WireArray(o.array)).adjustLength(length).asArray();
/*      */     
/*  801 */     Wire[] result = new Wire[length];
/*  802 */     BigInteger[] newMaxValues = new BigInteger[length]; int i;
/*  803 */     for (i = 0; i < length; i++) {
/*      */       
/*  805 */       result[i] = w1[i].sub(w2[i], new String[0]);
/*  806 */       if (i < a.length)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  819 */         result[i] = result[i].add(a[i], new String[0]);
/*      */       }
/*  821 */       BigInteger max1 = (i < this.array.length) ? this.currentMaxValues[i] : 
/*  822 */         BigInteger.ZERO;
/*  823 */       BigInteger max2 = (i < a.length) ? a[i] : 
/*  824 */         BigInteger.ZERO;
/*      */       
/*  826 */       newMaxValues[i] = max1.add(max2);
/*  827 */       if (newMaxValues[i].compareTo(Config.getFiniteFieldModulus()) >= 0 && !disableOverflowChecks) {
/*  828 */         System.err.println("An unhandled case of possible overflow was detected in subtraction. (Sanity check failed)");
/*  829 */         throw new RuntimeException();
/*      */       } 
/*      */     } 
/*      */     
/*  833 */     for (i = 0; i < newMaxValues.length; i++) {
/*  834 */       if (newMaxValues[i].compareTo(Config.getFiniteFieldModulus()) >= 0) {
/*  835 */         newMaxValues[i] = Config.getFiniteFieldModulus().subtract(BigInteger.ONE);
/*      */       }
/*      */     } 
/*      */     
/*  839 */     return new PackedValue(result, newMaxValues);
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean addSubOverflowCheck(BigInteger[] o) {
/*  844 */     int length = Math.min(this.array.length, o.length);
/*  845 */     boolean overflow = false;
/*  846 */     for (int i = 0; i < length; i++) {
/*  847 */       BigInteger max1 = (i < this.array.length) ? this.currentMaxValues[i] : 
/*  848 */         BigInteger.ZERO;
/*  849 */       BigInteger max2 = (i < o.length) ? o[i] : 
/*  850 */         BigInteger.ZERO;
/*  851 */       if (max1.add(max2).compareTo(Config.getFiniteFieldModulus()) >= 0) {
/*  852 */         overflow = true;
/*      */         break;
/*      */       } 
/*      */     } 
/*  856 */     return overflow;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void forceBitwidth() {
/*  863 */     for (int i = 0; i < this.array.length; i++) {
/*  864 */       this.array[i].restrictBitLength(this.currentBitwidth[i], new String[0]);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void forceEquality(PackedValue a) {
/*  872 */     WireArray bits1 = a.getBits(a.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength(), UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]);
/*  873 */     WireArray bits2 = getBits(getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength(), UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]);
/*  874 */     PackedValue v1 = new PackedValue(bits1, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */     
/*  876 */     PackedValue v2 = new PackedValue(bits2, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */     
/*  878 */     for (int i = 0; i < v1.array.length; i++)
/*      */     {
/*      */       
/*  881 */       this.generator.__addEqualityAssertion(v1.array[i], v2.array[i], new String[0]);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void forceEquality2(PackedValue p) {
/*  892 */     PackedValue operand1 = this;
/*  893 */     PackedValue operand2 = p;
/*      */     
/*  895 */     if (checkEqualityAssertionOverflow(p)) {
/*      */ 
/*      */       
/*  898 */       operand1 = align((int)Math.ceil(getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength() * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK), UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */       
/*  900 */       operand2 = p.align((int)Math.ceil(p.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK).bitLength() * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK), UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */     } 
/*      */     
/*  903 */     if (operand1.checkEqualityAssertionOverflow(operand2)) {
/*  904 */       throw new RuntimeException("Unhandled overflow possibility detected in equality verification (Sanity Check Failed).");
/*      */     }
/*      */     
/*  907 */     final ArrayList<Wire> group1 = new ArrayList<>();
/*  908 */     ArrayList<BigInteger> group1_bound = new ArrayList<>();
/*  909 */     final ArrayList<Wire> group2 = new ArrayList<>();
/*  910 */     ArrayList<BigInteger> group2_bound = new ArrayList<>();
/*  911 */     final ArrayList<Integer> steps = new ArrayList<>();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  916 */     Wire[] a1 = operand1.array;
/*  917 */     Wire[] a2 = operand2.array;
/*  918 */     int limit = Math.max(a1.length, a2.length);
/*  919 */     BigInteger[] bounds1 = operand1.currentMaxValues;
/*  920 */     BigInteger[] bounds2 = operand2.currentMaxValues;
/*      */     
/*  922 */     if (operand2.array.length != limit) {
/*  923 */       a2 = (new WireArray(a2)).adjustLength(limit).asArray();
/*  924 */       bounds2 = new BigInteger[limit];
/*  925 */       Arrays.fill((Object[])bounds2, BigInteger.ZERO);
/*  926 */       System.arraycopy(operand2.currentMaxValues, 0, bounds2, 0, 
/*  927 */           operand2.currentMaxValues.length);
/*      */     } 
/*  929 */     if (operand1.array.length != limit) {
/*  930 */       a1 = (new WireArray(a1)).adjustLength(limit).asArray();
/*  931 */       bounds1 = new BigInteger[limit];
/*  932 */       Arrays.fill((Object[])bounds1, BigInteger.ZERO);
/*  933 */       System.arraycopy(operand1.currentMaxValues, 0, bounds1, 0, 
/*  934 */           operand1.currentMaxValues.length);
/*      */     } 
/*      */ 
/*      */     
/*  938 */     Config.debugVerbose = true;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  945 */     if (a1.length == a2.length && a1.length == 1) {
/*  946 */       this.generator.__addEqualityAssertion(a1[0], a2[0], new String[] {
/*  947 */             "Equality assertion of long elements | case 1" }); return;
/*      */     } 
/*  949 */     if (operand1.isAligned() && operand2.isAligned() && a1.length != 1 && a2.length != 1) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  954 */       for (int n = 0; n < limit; n++) {
/*      */         
/*  956 */         this.generator.__addEqualityAssertion(a1[n], a2[n], new String[] {
/*  957 */               "Equality assertion of long elements | case 2 | index " + 
/*  958 */               n
/*      */             });
/*      */       } 
/*      */       return;
/*      */     } 
/*  963 */     if (operand2.array.length != limit) {
/*  964 */       a2 = (new WireArray(a2)).adjustLength(limit).asArray();
/*  965 */       bounds2 = new BigInteger[limit];
/*  966 */       Arrays.fill((Object[])bounds2, BigInteger.ZERO);
/*  967 */       System.arraycopy(operand2.currentMaxValues, 0, bounds2, 0, operand2.currentMaxValues.length);
/*      */     } 
/*  969 */     if (operand1.array.length != limit) {
/*  970 */       a1 = (new WireArray(a1)).adjustLength(limit).asArray();
/*  971 */       bounds1 = new BigInteger[limit];
/*  972 */       Arrays.fill((Object[])bounds1, BigInteger.ZERO);
/*  973 */       System.arraycopy(operand1.currentMaxValues, 0, bounds1, 0, operand1.currentMaxValues.length);
/*      */     } 
/*  975 */     BigInteger shift = (new BigInteger("2")).pow(UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  976 */     int i = 0;
/*  977 */     while (i < limit) {
/*  978 */       int step = 1;
/*  979 */       Wire w1 = a1[i];
/*  980 */       Wire w2 = a2[i];
/*  981 */       BigInteger b1 = bounds1[i];
/*  982 */       BigInteger b2 = bounds2[i];
/*      */ 
/*      */       
/*  985 */       while (i + step <= limit - 1) {
/*  986 */         BigInteger delta = shift.pow(step);
/*  987 */         if (b1.add(bounds1[i + step].multiply(delta)).bitLength() < Config.getNumBitsFiniteFieldModulus() - 2 && 
/*  988 */           b2.add(bounds2[i + step].multiply(delta)).bitLength() < Config.getNumBitsFiniteFieldModulus() - 2) {
/*  989 */           w1 = w1.add(a1[i + step].mul(delta, new String[0]), new String[0]);
/*  990 */           w2 = w2.add(a2[i + step].mul(delta, new String[0]), new String[0]);
/*  991 */           b1 = b1.add(bounds1[i + step].multiply(delta));
/*  992 */           b2 = b2.add(bounds2[i + step].multiply(delta));
/*  993 */           step++;
/*      */           
/*      */           continue;
/*      */         } 
/*      */         break;
/*      */       } 
/*  999 */       group1.add(w1);
/* 1000 */       group1_bound.add(b1);
/* 1001 */       group2.add(w2);
/* 1002 */       group2_bound.add(b2);
/*      */       
/* 1004 */       steps.add(Integer.valueOf(step));
/* 1005 */       i += step;
/*      */     } 
/*      */     
/* 1008 */     if (group1.size() == 1) {
/* 1009 */       this.generator.__addEqualityAssertion(group1.get(0), group2.get(0), new String[] {
/* 1010 */             "Equality Assertion | Case 3 | Group Index 0"
/*      */           });
/*      */       return;
/*      */     } 
/* 1014 */     int newChunkSize = group1.size();
/* 1015 */     final Wire[] carries = this.generator.__createProverWitnessWireArray(newChunkSize - 1, new String[0]);
/* 1016 */     final BigInteger[] auxConstantChunks = new BigInteger[newChunkSize];
/* 1017 */     BigInteger auxConstant = BigInteger.ZERO;
/*      */     
/* 1019 */     int accumStep = 0;
/* 1020 */     int[] carriesBitBounds = new int[carries.length];
/* 1021 */     for (int j = 0; j < auxConstantChunks.length - 1; j++) {
/* 1022 */       auxConstantChunks[j] = (new BigInteger("2")).pow((
/* 1023 */           (BigInteger)group2_bound.get(j)).bitLength());
/* 1024 */       auxConstant = auxConstant.add(auxConstantChunks[j].multiply(shift.pow(accumStep)));
/* 1025 */       accumStep += ((Integer)steps.get(j)).intValue();
/*      */ 
/*      */       
/* 1028 */       carriesBitBounds[j] = Math.max(auxConstantChunks[j].bitLength(), ((BigInteger)group1_bound.get(j)).bitLength()) - ((Integer)steps.get(j)).intValue() * UnsignedInteger.BITWIDTH_PER_CHUNK + 1;
/*      */     } 
/* 1030 */     auxConstantChunks[auxConstantChunks.length - 1] = BigInteger.ZERO;
/*      */     
/* 1032 */     final BigInteger[] alignedCoeffs = new BigInteger[newChunkSize];
/* 1033 */     Arrays.fill((Object[])alignedCoeffs, BigInteger.ZERO);
/* 1034 */     BigInteger[] smallerAlignedCoeffs = Util.split(auxConstant, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1035 */     int idx = 0;
/*      */     int k;
/* 1037 */     label97: for (k = 0; k < newChunkSize; k++) {
/*      */       
/* 1039 */       for (int n = 0; n < ((Integer)steps.get(k)).intValue(); n++) {
/*      */         
/* 1041 */         alignedCoeffs[k] = alignedCoeffs[k].add(smallerAlignedCoeffs[idx].multiply(shift.pow(n)));
/* 1042 */         idx++;
/*      */         
/* 1044 */         if (idx == smallerAlignedCoeffs.length) {
/*      */           break label97;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1051 */     if (idx != smallerAlignedCoeffs.length) {
/* 1052 */       if (idx == smallerAlignedCoeffs.length - 1) {
/* 1053 */         alignedCoeffs[newChunkSize - 1] = alignedCoeffs[newChunkSize - 1]
/* 1054 */           .add(smallerAlignedCoeffs[idx].multiply(shift.pow(((Integer)steps
/* 1055 */                 .get(newChunkSize - 1)).intValue() + 0)));
/*      */       } else {
/* 1057 */         throw new RuntimeException(
/* 1058 */             "Case not expected. Pleas report.");
/*      */       } 
/*      */     }
/*      */     
/* 1062 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*      */         {
/*      */           
/*      */           public void evaluate(CircuitEvaluator evaluator)
/*      */           {
/* 1067 */             BigInteger prevCarry = BigInteger.ZERO;
/* 1068 */             for (int i = 0; i < carries.length; i++) {
/* 1069 */               BigInteger a = evaluator.getWireValue(group1.get(i));
/* 1070 */               BigInteger b = evaluator.getWireValue(group2.get(i));
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 1076 */               BigInteger carryValue = auxConstantChunks[i].add(a).subtract(b).subtract(alignedCoeffs[i]).add(prevCarry);
/*      */               
/* 1078 */               carryValue = carryValue.shiftRight(((Integer)steps.get(i)).intValue() * UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1079 */               evaluator.setWireValue(carries[i], carryValue);
/* 1080 */               prevCarry = carryValue;
/*      */             } 
/*      */           }
/*      */         });
/*      */ 
/*      */     
/* 1086 */     for (k = 0; k < carries.length; k++)
/*      */     {
/*      */       
/* 1089 */       carries[k].restrictBitLength(carriesBitBounds[k], new String[0]);
/*      */     }
/* 1091 */     Wire prevCarry = this.generator.__getZeroWire();
/* 1092 */     for (int m = 0; m < carries.length + 1; m++) {
/* 1093 */       Wire auxConstantChunkWire = this.generator.__createConstantWire(auxConstantChunks[m], new String[0]);
/* 1094 */       Wire alignedCoeffWire = this.generator.__createConstantWire(alignedCoeffs[m], new String[0]);
/* 1095 */       Wire currentCarry = (m == carries.length) ? this.generator.__getZeroWire() : carries[m];
/* 1096 */       this.generator.__addEqualityAssertion(auxConstantChunkWire.add(((Wire)group1.get(m)).sub(group2.get(m), new String[0]), new String[0]).add(prevCarry, new String[0]), alignedCoeffWire.add(currentCarry.mul(shift.pow(((Integer)steps.get(m)).intValue()), new String[0]), new String[0]), new String[] {
/* 1097 */             "Equality Assertion | Case 3 | Group Index " + m });
/* 1098 */       prevCarry = currentCarry;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean checkEqualityAssertionOverflow(PackedValue p) {
/* 1106 */     ArrayList<Wire> group1 = new ArrayList<>();
/* 1107 */     ArrayList<BigInteger> group1_bound = new ArrayList<>();
/* 1108 */     ArrayList<Wire> group2 = new ArrayList<>();
/* 1109 */     ArrayList<BigInteger> group2_bound = new ArrayList<>();
/* 1110 */     ArrayList<Integer> steps = new ArrayList<>();
/*      */     
/* 1112 */     Wire[] a1 = this.array;
/* 1113 */     Wire[] a2 = p.array;
/* 1114 */     int limit = Math.max(a1.length, a2.length);
/*      */     
/* 1116 */     BigInteger[] bounds1 = this.currentMaxValues;
/* 1117 */     BigInteger[] bounds2 = p.currentMaxValues;
/*      */     
/* 1119 */     if (p.array.length != limit) {
/* 1120 */       a2 = (new WireArray(a2)).adjustLength(limit).asArray();
/* 1121 */       bounds2 = new BigInteger[limit];
/* 1122 */       Arrays.fill((Object[])bounds2, BigInteger.ZERO);
/* 1123 */       System.arraycopy(p.currentMaxValues, 0, bounds2, 0, p.currentMaxValues.length);
/*      */     } 
/* 1125 */     if (this.array.length != limit) {
/* 1126 */       a1 = (new WireArray(a1)).adjustLength(limit).asArray();
/* 1127 */       bounds1 = new BigInteger[limit];
/* 1128 */       Arrays.fill((Object[])bounds1, BigInteger.ZERO);
/* 1129 */       System.arraycopy(this.currentMaxValues, 0, bounds1, 0, this.currentMaxValues.length);
/*      */     } 
/*      */ 
/*      */     
/* 1133 */     if (a1.length == a2.length && a1.length == 1)
/* 1134 */       return false; 
/* 1135 */     if (isAligned() && p.isAligned() && a1.length != 1 && a2.length != 1) {
/* 1136 */       return false;
/*      */     }
/*      */ 
/*      */     
/* 1140 */     BigInteger shift = (new BigInteger("2")).pow(UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1141 */     int i = 0;
/* 1142 */     while (i < limit) {
/* 1143 */       int step = 1;
/* 1144 */       Wire w1 = a1[i];
/* 1145 */       Wire w2 = a2[i];
/* 1146 */       BigInteger b1 = bounds1[i];
/* 1147 */       BigInteger b2 = bounds2[i];
/* 1148 */       while (i + step <= limit - 1) {
/* 1149 */         BigInteger delta = shift.pow(step);
/* 1150 */         if (b1.add(bounds1[i + step].multiply(delta)).bitLength() < Config.getNumBitsFiniteFieldModulus() - 2 && 
/* 1151 */           b2.add(bounds2[i + step].multiply(delta)).bitLength() < Config.getNumBitsFiniteFieldModulus() - 2) {
/* 1152 */           w1 = w1.add(a1[i + step].mul(delta, new String[0]), new String[0]);
/* 1153 */           w2 = w2.add(a2[i + step].mul(delta, new String[0]), new String[0]);
/* 1154 */           b1 = b1.add(bounds1[i + step].multiply(delta));
/* 1155 */           b2 = b2.add(bounds2[i + step].multiply(delta));
/* 1156 */           step++;
/*      */           
/*      */           continue;
/*      */         } 
/*      */         break;
/*      */       } 
/* 1162 */       group1.add(w1);
/* 1163 */       group1_bound.add(b1);
/* 1164 */       group2.add(w2);
/* 1165 */       group2_bound.add(b2);
/* 1166 */       steps.add(Integer.valueOf(step));
/* 1167 */       i += step;
/*      */     } 
/*      */     
/* 1170 */     if (group1.size() == 1) {
/* 1171 */       return false;
/*      */     }
/*      */     
/* 1174 */     int newChunkSize = group1.size();
/* 1175 */     BigInteger[] auxConstantChunks = new BigInteger[newChunkSize];
/* 1176 */     BigInteger auxConstant = BigInteger.ZERO;
/*      */ 
/*      */     
/* 1179 */     int accumStep = 0;
/* 1180 */     int[] carriesBitBounds = new int[newChunkSize - 1];
/* 1181 */     for (int j = 0; j < auxConstantChunks.length - 1; j++) {
/* 1182 */       auxConstantChunks[j] = (new BigInteger("2")).pow((
/* 1183 */           (BigInteger)group2_bound.get(j)).bitLength());
/* 1184 */       auxConstant = auxConstant.add(auxConstantChunks[j].multiply(shift.pow(accumStep)));
/* 1185 */       accumStep += ((Integer)steps.get(j)).intValue();
/* 1186 */       carriesBitBounds[j] = Math.max(auxConstantChunks[j].bitLength(), ((BigInteger)group1_bound.get(j)).bitLength()) - ((Integer)steps.get(j)).intValue() * UnsignedInteger.BITWIDTH_PER_CHUNK + 1;
/*      */     } 
/* 1188 */     auxConstantChunks[auxConstantChunks.length - 1] = BigInteger.ZERO;
/*      */     
/* 1190 */     BigInteger[] alignedCoeffs = new BigInteger[newChunkSize];
/* 1191 */     Arrays.fill((Object[])alignedCoeffs, BigInteger.ZERO);
/* 1192 */     BigInteger[] smallerAlignedCoeffs = Util.split(auxConstant, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1193 */     int idx = 0; int k;
/* 1194 */     label66: for (k = 0; k < newChunkSize; k++) {
/* 1195 */       for (int n = 0; n < ((Integer)steps.get(k)).intValue(); n++) {
/* 1196 */         alignedCoeffs[k] = alignedCoeffs[k].add(smallerAlignedCoeffs[idx].multiply(shift.pow(n)));
/* 1197 */         idx++;
/* 1198 */         if (idx == smallerAlignedCoeffs.length) {
/*      */           break label66;
/*      */         }
/*      */       } 
/*      */     } 
/* 1203 */     if (idx != smallerAlignedCoeffs.length) {
/* 1204 */       if (idx == smallerAlignedCoeffs.length - 1) {
/* 1205 */         alignedCoeffs[newChunkSize - 1] = alignedCoeffs[newChunkSize - 1]
/* 1206 */           .add(smallerAlignedCoeffs[idx].multiply(shift.pow(((Integer)steps
/* 1207 */                 .get(newChunkSize - 1)).intValue() + 0)));
/*      */       } else {
/* 1209 */         throw new RuntimeException(
/* 1210 */             "Case not expected. Investigate why!");
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 1215 */     BigInteger prevBound = BigInteger.ZERO;
/* 1216 */     for (int m = 0; m < carriesBitBounds.length + 1; m++) {
/*      */       
/* 1218 */       if (auxConstantChunks[m].add(group1_bound.get(m)).add(prevBound)
/* 1219 */         .compareTo(Config.getFiniteFieldModulus()) >= 0)
/*      */       {
/*      */ 
/*      */         
/* 1223 */         return true;
/*      */       }
/* 1225 */       if (m != carriesBitBounds.length)
/* 1226 */         prevBound = Util.computeMaxValue(carriesBitBounds[m]); 
/*      */     } 
/* 1228 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isAligned() {
/*      */     int j;
/* 1239 */     if (this.array.length <= 1) {
/* 1240 */       return true;
/*      */     }
/* 1242 */     boolean check = true;
/* 1243 */     for (int i = 0; i < this.array.length; i++) {
/* 1244 */       j = check & ((this.currentBitwidth[i] <= UnsignedInteger.BITWIDTH_PER_CHUNK) ? 1 : 0);
/*      */     }
/* 1246 */     return j;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void assertLessThan(final PackedValue other) {
/* 1261 */     if (!isAligned() || !other.isAligned()) {
/* 1262 */       throw new IllegalArgumentException("input chunks are not aligned");
/*      */     }
/*      */     
/* 1265 */     Wire[] a1 = getArray();
/* 1266 */     Wire[] a2 = other.getArray();
/* 1267 */     int length = Math.max(a1.length, a2.length);
/* 1268 */     final Wire[] paddedA1 = Util.padWireArray(a1, length);
/* 1269 */     final Wire[] paddedA2 = Util.padWireArray(a2, length);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1277 */     final Wire[] helperBits = this.generator.__createProverWitnessWireArray((other
/* 1278 */         .getArray()).length, new String[0]);
/*      */ 
/*      */     
/* 1281 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*      */         {
/*      */           public void evaluate(CircuitEvaluator evaluator)
/*      */           {
/* 1285 */             Wire[] otherArray = other.getArray();
/* 1286 */             Wire[] array = PackedValue.this.getArray();
/* 1287 */             boolean found = false;
/* 1288 */             for (int i = otherArray.length - 1; i >= 0; i--) {
/* 1289 */               BigInteger v1 = evaluator.getWireValue(paddedA1[i]);
/* 1290 */               BigInteger v2 = evaluator.getWireValue(paddedA2[i]);
/*      */               
/* 1292 */               boolean check = (v2.compareTo(v1) > 0 && !found);
/* 1293 */               evaluator.setWireValue(helperBits[i], 
/* 1294 */                   check ? BigInteger.ONE : BigInteger.ZERO);
/* 1295 */               if (check)
/* 1296 */                 found = true; 
/*      */             }  }
/*      */         });
/*      */     byte b;
/*      */     int j;
/*      */     Wire[] arrayOfWire1;
/* 1302 */     for (j = (arrayOfWire1 = helperBits).length, b = 0; b < j; ) { Wire w = arrayOfWire1[b];
/* 1303 */       this.generator.__addBinaryAssertion(w, new String[0]);
/*      */       b++; }
/*      */     
/* 1306 */     this.generator.__addOneAssertion((new WireArray(helperBits)).sumAllElements(new String[0]), new String[0]);
/*      */ 
/*      */     
/* 1309 */     Wire chunk1 = this.generator.__getZeroWire();
/* 1310 */     Wire chunk2 = this.generator.__getZeroWire();
/*      */ 
/*      */     
/* 1313 */     for (int i = 0; i < helperBits.length; i++) {
/* 1314 */       chunk1 = chunk1.add(paddedA1[i].mul(helperBits[i], new String[0]), new String[0]);
/* 1315 */       chunk2 = chunk2.add(paddedA2[i].mul(helperBits[i], new String[0]), new String[0]);
/*      */     } 
/*      */     
/* 1318 */     this.generator.__addOneAssertion(chunk1.isLessThan(chunk2, 
/* 1319 */           UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]), new String[0]);
/*      */ 
/*      */     
/* 1322 */     Wire[] helperBits2 = new Wire[helperBits.length];
/* 1323 */     helperBits2[0] = this.generator.__getZeroWire();
/* 1324 */     for (int k = 1; k < helperBits.length; k++) {
/* 1325 */       helperBits2[k] = helperBits2[k - 1].add(helperBits[k - 1], new String[0]);
/* 1326 */       this.generator.__addZeroAssertion(helperBits2[k].mul(paddedA1[k]
/* 1327 */             .sub(paddedA2[k], new String[0]), new String[0]), new String[0]);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Bit isLessThan(PackedValue packedWire, int bitwidth) {
/* 1334 */     return compare(packedWire, bitwidth, "<");
/*      */   }
/*      */   
/*      */   public Bit isLessThanOrEqual(PackedValue packedWire, int bitwidth) {
/* 1338 */     return compare(packedWire, bitwidth, "<=");
/*      */   }
/*      */   
/*      */   public Bit isGreaterThan(PackedValue packedWire, int bitwidth) {
/* 1342 */     return compare(packedWire, bitwidth, ">");
/*      */   }
/*      */   
/*      */   public Bit isGreaterThanOrEqual(PackedValue packedWire, int bitwidth) {
/* 1346 */     return compare(packedWire, bitwidth, ">=");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Bit compare(PackedValue packedWire, int bitwidth, String op) {
/* 1353 */     if (packedWire.array.length == 1 && this.array.length == 1) {
/*      */       Wire wire;
/* 1355 */       BigInteger max1 = getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1356 */       BigInteger max2 = packedWire.getMaxVal(UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1357 */       int b1 = max1.bitLength();
/* 1358 */       int b2 = max2.bitLength();
/* 1359 */       int maxB = Math.max(b1, b2);
/* 1360 */       if (maxB + 2 >= Config.getNumBitsFiniteFieldModulus()) {
/* 1361 */         System.err.println("Unhandled overflow possibility in comparison of integers. (Sanity Check Failed)");
/* 1362 */         throw new RuntimeException();
/*      */       } 
/*      */ 
/*      */       
/* 1366 */       if (op.equals("<=")) {
/* 1367 */         wire = this.array[0].isLessThanOrEqual(packedWire.array[0], maxB, new String[0]);
/* 1368 */       } else if (op.equals("<")) {
/* 1369 */         wire = this.array[0].isLessThan(packedWire.array[0], maxB, new String[0]);
/* 1370 */       } else if (op.equals(">=")) {
/* 1371 */         wire = this.array[0].isGreaterThanOrEqual(packedWire.array[0], maxB, new String[0]);
/* 1372 */       } else if (op.equals(">")) {
/* 1373 */         wire = this.array[0].isGreaterThan(packedWire.array[0], maxB, new String[0]);
/*      */       } else {
/* 1375 */         throw new IllegalArgumentException("Unknown Opcode");
/*      */       } 
/* 1377 */       return new Bit(wire);
/*      */     } 
/*      */     
/* 1380 */     int unitBitwidth = UnsignedInteger.BITWIDTH_PER_CHUNK;
/* 1381 */     if (packedWire.array.length == 1) {
/* 1382 */       int m = packedWire.currentMaxValues[0].bitLength();
/* 1383 */     } else if (this.array.length == 1) {
/* 1384 */       unitBitwidth = this.currentMaxValues[0].bitLength();
/*      */     } 
/* 1386 */     if (unitBitwidth == 0) {
/* 1387 */       unitBitwidth = 1;
/*      */     }
/*      */ 
/*      */     
/* 1391 */     int step = (int)Math.ceil(unitBitwidth * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1392 */     unitBitwidth = step * UnsignedInteger.BITWIDTH_PER_CHUNK;
/*      */     
/* 1394 */     if (unitBitwidth + 2 >= Config.getNumBitsFiniteFieldModulus()) {
/* 1395 */       System.err.println("Unhandled overflow possibility in comparison of long integers. (Sanity Check Failed)");
/* 1396 */       throw new RuntimeException();
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1401 */     Wire[] a1 = combine(unitBitwidth);
/* 1402 */     Wire[] a2 = packedWire.combine(unitBitwidth);
/*      */     
/* 1404 */     final int length = Math.max(a1.length, a2.length);
/* 1405 */     final Wire[] paddedA1 = Util.padWireArray(a1, length);
/* 1406 */     final Wire[] paddedA2 = Util.padWireArray(a2, length);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1411 */     final Wire[] helperBits = this.generator.__createProverWitnessWireArray(length, new String[0]);
/*      */ 
/*      */     
/* 1414 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*      */         {
/*      */           public void evaluate(CircuitEvaluator evaluator)
/*      */           {
/* 1418 */             boolean found = false;
/* 1419 */             for (int i = length - 1; i >= 0; i--) {
/* 1420 */               BigInteger v1 = evaluator.getWireValue(paddedA1[i]);
/* 1421 */               BigInteger v2 = evaluator.getWireValue(paddedA2[i]);
/*      */               
/* 1423 */               boolean check = (v2.compareTo(v1) != 0 && !found);
/* 1424 */               evaluator.setWireValue(helperBits[i], 
/* 1425 */                   check ? BigInteger.ONE : BigInteger.ZERO);
/* 1426 */               if (check)
/* 1427 */                 found = true; 
/*      */             }  }
/*      */         });
/*      */     byte b;
/*      */     int j;
/*      */     Wire[] arrayOfWire1;
/* 1433 */     for (j = (arrayOfWire1 = helperBits).length, b = 0; b < j; ) { Wire w = arrayOfWire1[b];
/* 1434 */       this.generator.__addBinaryAssertion(w, new String[0]);
/*      */       
/*      */       b++; }
/*      */ 
/*      */     
/* 1439 */     Wire sumBits = (new WireArray(helperBits)).sumAllElements(new String[0]);
/* 1440 */     this.generator.__addBinaryAssertion(sumBits, new String[0]);
/*      */ 
/*      */     
/* 1443 */     Wire chunk1 = this.generator.__getZeroWire();
/* 1444 */     Wire chunk2 = this.generator.__getZeroWire();
/*      */     
/* 1446 */     for (int i = 0; i < helperBits.length; i++) {
/* 1447 */       chunk1 = chunk1.add(paddedA1[i].mul(helperBits[i], new String[0]), new String[0]);
/* 1448 */       chunk2 = chunk2.add(paddedA2[i].mul(helperBits[i], new String[0]), new String[0]);
/*      */     } 
/*      */     
/* 1451 */     this.generator.__addEqualityAssertion(chunk1.sub(chunk2, new String[0]).isEqualTo(0L, new String[0]).invAsBit(new String[0]), sumBits, new String[0]);
/*      */     
/* 1453 */     Wire areEqual = sumBits.invAsBit(new String[0]);
/*      */ 
/*      */     
/* 1456 */     Wire[] helperBits2 = new Wire[helperBits.length];
/*      */     
/* 1458 */     helperBits2[0] = sumBits.invAsBit(new String[0]);
/* 1459 */     for (int k = 1; k < helperBits.length; k++) {
/* 1460 */       helperBits2[k] = helperBits2[k - 1].add(helperBits[k - 1], new String[0]);
/*      */ 
/*      */       
/* 1463 */       this.generator.__addAssertion(helperBits2[k], paddedA1[k]
/* 1464 */           .sub(paddedA2[k], new String[0]), this.generator.__getZeroWire(), new String[0]);
/*      */     } 
/*      */     
/* 1467 */     Wire comparisonResult = null;
/* 1468 */     if (op.equals("<=")) {
/* 1469 */       comparisonResult = chunk1.isLessThan(chunk2, unitBitwidth, new String[0]);
/* 1470 */       comparisonResult = comparisonResult.add(areEqual, new String[0]);
/*      */     }
/* 1472 */     else if (op.equals("<")) {
/* 1473 */       comparisonResult = chunk1.isLessThan(chunk2, unitBitwidth, new String[0]);
/* 1474 */     } else if (op.equals(">=")) {
/* 1475 */       comparisonResult = chunk1.isGreaterThan(chunk2, unitBitwidth, new String[0]);
/* 1476 */       comparisonResult = comparisonResult.add(areEqual, new String[0]);
/*      */     }
/* 1478 */     else if (op.equals(">")) {
/* 1479 */       comparisonResult = chunk1.isGreaterThan(chunk2, unitBitwidth, new String[0]);
/*      */     } else {
/* 1481 */       throw new IllegalArgumentException("Unknown Opcode");
/*      */     } 
/* 1483 */     return new Bit(comparisonResult);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean checkComparisonOverflow(PackedValue o) {
/* 1493 */     PackedValue op1 = this;
/* 1494 */     PackedValue op2 = o;
/* 1495 */     int length = Math.max(op1.array.length, op2.array.length);
/*      */ 
/*      */     
/* 1498 */     BigInteger max = BigInteger.ZERO;
/* 1499 */     boolean overflow = false;
/* 1500 */     if (length == 1) {
/* 1501 */       max = op1.currentMaxValues[0];
/* 1502 */       if (op2.currentMaxValues[0].compareTo(op1.currentMaxValues[0]) > 0) {
/* 1503 */         max = op2.currentMaxValues[0];
/*      */       }
/* 1505 */       if (max.bitLength() + 2 >= Config.getNumBitsFiniteFieldModulus()) {
/* 1506 */         overflow = true;
/*      */       }
/* 1508 */     } else if (op1.array.length == 1) {
/*      */       
/* 1510 */       int step = (int)Math.ceil(op1.currentMaxValues[0].bitLength() * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1511 */       if (step == 0)
/* 1512 */         step = 1; 
/* 1513 */       int tmp = step * UnsignedInteger.BITWIDTH_PER_CHUNK;
/* 1514 */       if (tmp + 2 >= Config.getNumBitsFiniteFieldModulus()) {
/* 1515 */         overflow = true;
/*      */       }
/* 1517 */     } else if (op2.array.length == 1) {
/* 1518 */       int step = (int)Math.ceil(op2.currentMaxValues[0].bitLength() * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1519 */       if (step == 0)
/* 1520 */         step = 1; 
/* 1521 */       int tmp = step * UnsignedInteger.BITWIDTH_PER_CHUNK;
/* 1522 */       if (tmp + 2 >= Config.getNumBitsFiniteFieldModulus()) {
/* 1523 */         overflow = true;
/*      */       }
/*      */     }
/* 1526 */     else if (!op1.isAligned() || !op2.isAligned()) {
/* 1527 */       System.err.println("Warning: unexpected case found during sanity checks of overflows.");
/* 1528 */       throw new RuntimeException("[Sanity Check Failed] unexpected case found during overflow checking.");
/*      */     } 
/*      */ 
/*      */     
/* 1532 */     return overflow;
/*      */   }
/*      */ 
/*      */   
/*      */   public Bit isEqualTo(PackedValue packedWire) {
/* 1537 */     if (packedWire.array.length == 1 && this.array.length == 1) {
/* 1538 */       return new Bit(packedWire.array[0].isEqualTo(this.array[0], new String[0]));
/*      */     }
/* 1540 */     Wire[] a1 = combineAligned();
/* 1541 */     Wire[] a2 = packedWire.combineAligned();
/* 1542 */     int min = Math.min(a1.length, a2.length);
/* 1543 */     Wire result = this.generator.__getOneWire();
/* 1544 */     for (int i = 0; i < min; i++) {
/* 1545 */       result = result.mul(a1[i].isEqualTo(a2[i], new String[0]), new String[0]);
/*      */     }
/* 1547 */     if (a1.length > a2.length) {
/* 1548 */       for (int j = min; j < a1.length; j++) {
/* 1549 */         result = result.mul(a1[j].isEqualTo(0L, new String[0]), new String[0]);
/*      */       }
/*      */     } else {
/* 1552 */       for (int j = min; j < a2.length; j++) {
/* 1553 */         result = result.mul(a2[j].isEqualTo(0L, new String[0]), new String[0]);
/*      */       }
/*      */     } 
/* 1556 */     return new Bit(result);
/*      */   }
/*      */ 
/*      */   
/*      */   private Wire[] combineAligned() {
/* 1561 */     if (!isAligned()) {
/* 1562 */       throw new IllegalArgumentException("Method should be called on aligned long integers");
/*      */     }
/* 1564 */     int step = Config.getNumBitsFiniteFieldModulus() / UnsignedInteger.BITWIDTH_PER_CHUNK;
/* 1565 */     int numChunks = (int)Math.ceil(this.array.length * 1.0D / step);
/* 1566 */     Wire[] result = new Wire[numChunks];
/* 1567 */     for (int i = 0; i < result.length; i++) {
/* 1568 */       Wire w = this.generator.__getZeroWire();
/* 1569 */       for (int j = 0; j < step && 
/* 1570 */         i * step + j < this.array.length; j++)
/*      */       {
/*      */         
/* 1573 */         w = w.add(this.array[i * step + j].mul(Util.computeBound(UnsignedInteger.BITWIDTH_PER_CHUNK * j), new String[0]), new String[0]);
/*      */       }
/* 1575 */       result[i] = w;
/*      */     } 
/* 1577 */     return result;
/*      */   }
/*      */   
/*      */   private Wire[] combine(int bitwidthLowerBound) {
/* 1581 */     if (!isAligned()) {
/* 1582 */       throw new IllegalArgumentException("Method should be called on aligned integers");
/*      */     }
/* 1584 */     int step = (int)Math.ceil(bitwidthLowerBound * 1.0D / UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1585 */     int numChunks = (int)Math.ceil(this.array.length * 1.0D / step);
/* 1586 */     Wire[] result = new Wire[numChunks];
/*      */     
/* 1588 */     for (int i = 0; i < result.length; i++) {
/* 1589 */       Wire w = this.generator.__getZeroWire();
/* 1590 */       for (int j = 0; j < step && 
/* 1591 */         i * step + j < this.array.length; j++)
/*      */       {
/*      */         
/* 1594 */         w = w.add(this.array[i * step + j].mul(Util.computeBound(UnsignedInteger.BITWIDTH_PER_CHUNK * j), new String[0]), new String[0]);
/*      */       }
/* 1596 */       result[i] = w;
/*      */     } 
/*      */     
/* 1599 */     return result;
/*      */   }
/*      */   
/*      */   public void forceNonZero() {
/* 1603 */     if (this.array.length == 1) {
/* 1604 */       final Wire w = this.generator.__createProverWitnessWire(new String[0]);
/* 1605 */       final Wire tmp = this.array[0];
/* 1606 */       this.generator.__specifyProverWitnessComputation(new Instruction()
/*      */           {
/*      */             
/*      */             public void evaluate(CircuitEvaluator evaluator)
/*      */             {
/* 1611 */               BigInteger v = evaluator.getWireValue(tmp);
/* 1612 */               evaluator.setWireValue(w, v.modInverse(Config.getFiniteFieldModulus()));
/*      */             }
/*      */           });
/*      */       
/* 1616 */       this.generator.__addAssertion(tmp, w, this.generator.__getOneWire(), new String[] { "Non-zero check" });
/*      */     } else {
/* 1618 */       if (!isAligned()) {
/* 1619 */         throw new RuntimeException("Unexpected case in non-zero checks of long integers");
/*      */       }
/*      */       
/* 1622 */       Wire sum = this.generator.__getZeroWire();
/* 1623 */       for (int i = 0; i < this.array.length; i++) {
/* 1624 */         sum = sum.add(this.array[i].checkNonZero(new String[0]), new String[0]);
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1638 */       this.generator.__addOneAssertion(sum.checkNonZero(new String[0]), new String[0]);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public boolean equals(Object o) {
/* 1644 */     if (o == null || !(o instanceof PackedValue)) {
/* 1645 */       return false;
/*      */     }
/* 1647 */     PackedValue v = (PackedValue)o;
/* 1648 */     if (v.array.length != this.array.length) {
/* 1649 */       return false;
/*      */     }
/* 1651 */     boolean check = true;
/* 1652 */     for (int i = 0; i < this.array.length; i++) {
/* 1653 */       if (!v.array[i].equals(this.array[i])) {
/* 1654 */         check = false;
/*      */         break;
/*      */       } 
/*      */     } 
/* 1658 */     return check;
/*      */   }
/*      */   
/*      */   public int getBitwidthOfLargestChunk() {
/* 1662 */     BigInteger max = BigInteger.ZERO; byte b; int i; BigInteger[] arrayOfBigInteger;
/* 1663 */     for (i = (arrayOfBigInteger = this.currentMaxValues).length, b = 0; b < i; ) { BigInteger f = arrayOfBigInteger[b];
/* 1664 */       if (f.compareTo(max) > 0)
/* 1665 */         max = f;  b++; }
/*      */     
/* 1667 */     return max.bitLength();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isWitness() {
/* 1674 */     return this.witnessIndicator;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setWitnessIndicator(boolean witnessIndicator) {
/* 1681 */     this.witnessIndicator = witnessIndicator;
/*      */   }
/*      */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\PackedValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */