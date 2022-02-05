/*      */ package backend.auxTypes;
/*      */ 
/*      */ import backend.config.Config;
/*      */ import backend.eval.CircuitEvaluator;
/*      */ import backend.resource.ResourceBundle;
/*      */ import backend.structure.BitWire;
/*      */ import backend.structure.CircuitGenerator;
/*      */ import backend.structure.ConstantWire;
/*      */ import backend.structure.Wire;
/*      */ import backend.structure.WireArray;
/*      */ import examples.gadgets.LongIntegerModConstantGadget;
/*      */ import examples.gadgets.LongIntegerModNotStrictModulusGadget;
/*      */ import examples.gadgets.ModConstantGadget;
/*      */ import examples.gadgets.ModGadget;
/*      */ import java.math.BigInteger;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Stack;
/*      */ import util.Util;
/*      */ 
/*      */ 
/*      */ 
/*      */ public class UnsignedInteger
/*      */   implements IAuxType, ConditionalScopeImpactedType
/*      */ {
/*   27 */   public static int BITWIDTH_PER_CHUNK = 32;
/*   28 */   public static int BITWIDTH_LIMIT_SHORT = Config.getNumBitsFiniteFieldModulus() / 2 - 2;
/*      */   
/*      */   protected PackedValue packedWire;
/*      */   
/*      */   protected WireArray bitWires;
/*      */   
/*      */   protected int currentBitwidth;
/*      */   
/*      */   protected int bitWidth;
/*      */   
/*      */   protected BigInteger maxValue;
/*      */   
/*      */   protected BigInteger constant;
/*      */   protected CircuitGenerator generator;
/*      */   protected VariableState variableState;
/*      */   protected boolean packedAtCreationTime;
/*      */   protected boolean splittedAtCreationTime;
/*   45 */   protected int scope = ConditionalScopeTracker.getCurrentScopeId();
/*      */ 
/*      */   
/*      */   protected Stack<HashMap<Integer, UnsignedInteger>> possibleValStack;
/*      */ 
/*      */   
/*      */   protected Stack<UnsignedInteger> prevValStack;
/*      */ 
/*      */   
/*      */   private boolean stateChanged;
/*      */   
/*   56 */   protected int internalStateSerial = 0;
/*      */   
/*      */   public void setConditionalScopeId(int id) {
/*   59 */     this.scope = id;
/*      */   }
/*      */   
/*      */   public UnsignedInteger(UnsignedInteger o) {
/*   63 */     this.generator = o.generator;
/*   64 */     this.maxValue = o.maxValue;
/*   65 */     this.constant = o.constant;
/*   66 */     this.variableState = o.variableState;
/*   67 */     this.currentBitwidth = o.currentBitwidth;
/*   68 */     this.bitWires = o.bitWires;
/*   69 */     this.packedWire = o.packedWire;
/*   70 */     this.scope = o.scope;
/*   71 */     this.bitWidth = o.bitWidth;
/*      */   }
/*      */   
/*      */   public void assign(UnsignedInteger target, int expectedBitwidth) {
/*   75 */     this.internalStateSerial++;
/*   76 */     if (this.generator.__getPhase() == 0) {
/*   77 */       if (target.bitWidth < expectedBitwidth) {
/*   78 */         target.variableState.setConditionallySplittedAhead(true);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*   83 */       if (this.scope != ConditionalScopeTracker.getCurrentScopeId()) {
/*   84 */         ConditionalScopeTracker.register(this, this.scope);
/*   85 */         if (this.possibleValStack == null) {
/*   86 */           this.possibleValStack = new Stack<>();
/*      */         }
/*   88 */         if (this.prevValStack == null) {
/*   89 */           this.prevValStack = new Stack<>();
/*      */         }
/*   91 */         int current = ConditionalScopeTracker.getCurrentScopeId();
/*      */         
/*   93 */         for (int i = 0; i < current - this.scope; i++) {
/*   94 */           UnsignedInteger c = copy();
/*   95 */           c.variableState.setPackedAhead(true);
/*   96 */           this.prevValStack.push(c);
/*   97 */           this.possibleValStack.push(new HashMap<>());
/*      */         } 
/*   99 */         this.stateChanged = true;
/*  100 */         this.scope = ConditionalScopeTracker.getCurrentScopeId();
/*      */       } 
/*      */       
/*  103 */       this.constant = target.constant;
/*  104 */       this.variableState = target.variableState;
/*      */     
/*      */     }
/*  107 */     else if (this.scope == ConditionalScopeTracker.getCurrentScopeId()) {
/*      */       
/*  109 */       this.bitWires = target.bitWires;
/*  110 */       this.splittedAtCreationTime = target.splittedAtCreationTime;
/*  111 */       this.packedAtCreationTime = target.packedAtCreationTime;
/*  112 */       this.packedWire = target.packedWire;
/*  113 */       this.variableState = target.variableState;
/*  114 */       this.maxValue = target.maxValue;
/*  115 */       this.currentBitwidth = target.currentBitwidth;
/*  116 */       this.constant = target.constant;
/*      */     } else {
/*  118 */       this.stateChanged = true;
/*  119 */       ConditionalScopeTracker.register(this, this.scope);
/*      */       
/*  121 */       if (this.possibleValStack == null) {
/*  122 */         this.possibleValStack = new Stack<>();
/*      */       }
/*  124 */       if (this.prevValStack == null) {
/*  125 */         this.prevValStack = new Stack<>();
/*      */       }
/*  127 */       int current = ConditionalScopeTracker.getCurrentScopeId();
/*      */       
/*  129 */       int size = this.prevValStack.size();
/*  130 */       while (size < current) {
/*  131 */         this.prevValStack.push(copy());
/*  132 */         this.possibleValStack.push(new HashMap<>());
/*  133 */         size++;
/*      */       } 
/*      */       
/*  136 */       this.bitWidth = target.bitWidth;
/*  137 */       this.bitWires = target.bitWires;
/*  138 */       this.splittedAtCreationTime = target.splittedAtCreationTime;
/*  139 */       this.packedAtCreationTime = target.packedAtCreationTime;
/*  140 */       this.packedWire = target.packedWire;
/*  141 */       this.variableState = target.variableState;
/*      */       
/*  143 */       this.maxValue = target.maxValue;
/*  144 */       this.currentBitwidth = target.currentBitwidth;
/*  145 */       this.scope = ConditionalScopeTracker.getCurrentScopeId();
/*  146 */       this.constant = target.constant;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void pop(int id) {
/*  155 */     this.internalStateSerial++;
/*  156 */     if (!this.stateChanged) {
/*      */       return;
/*      */     }
/*      */     
/*  160 */     UnsignedInteger copy = copy();
/*      */     
/*  162 */     if (this.generator.__getPhase() == 0)
/*  163 */       copy.variableState.setPackedAhead(true); 
/*  164 */     ((HashMap<Integer, UnsignedInteger>)this.possibleValStack.peek()).put(Integer.valueOf(id), copy);
/*  165 */     this.scope--;
/*  166 */     UnsignedInteger prev = this.prevValStack.peek();
/*  167 */     this.packedWire = prev.packedWire;
/*  168 */     this.variableState = prev.variableState;
/*  169 */     this.bitWires = prev.bitWires;
/*  170 */     this.maxValue = prev.maxValue;
/*  171 */     this.constant = prev.constant;
/*  172 */     this.bitWidth = prev.bitWidth;
/*  173 */     this.currentBitwidth = prev.currentBitwidth;
/*  174 */     this.stateChanged = false;
/*      */   }
/*      */ 
/*      */   
/*      */   public void popMain() {
/*  179 */     this.internalStateSerial++;
/*  180 */     if (this.generator.__getPhase() == 0) {
/*  181 */       this.variableState = this.generator.__retrieveVariableState();
/*      */ 
/*      */ 
/*      */       
/*  185 */       this.variableState.setPackedAhead(true);
/*      */ 
/*      */       
/*  188 */       HashMap<Integer, UnsignedInteger> possibleVals = this.possibleValStack
/*  189 */         .pop();
/*  190 */       int mulIndex = ((UnsignedInteger)this.prevValStack.pop()).getVariableState().getMulIndex();
/*  191 */       for (Integer idx : possibleVals.keySet()) {
/*  192 */         int m = ((UnsignedInteger)possibleVals.get(idx)).variableState.getMulIndex();
/*  193 */         if (m > mulIndex) {
/*  194 */           mulIndex = m;
/*      */         }
/*      */       } 
/*  197 */       this.variableState.setMulIndex(mulIndex);
/*      */       
/*  199 */       this.bitWires = null;
/*  200 */       this.constant = null;
/*  201 */       this.stateChanged = true;
/*      */     } else {
/*      */       
/*  204 */       int tmp = this.scope;
/*  205 */       if (ConditionalScopeTracker.getCurrentScopeId() > tmp) {
/*  206 */         this.stateChanged = true;
/*      */       }
/*      */       
/*  209 */       this.variableState = null;
/*  210 */       ConditionalScopeTracker.ConditionalStatementData condData = 
/*  211 */         ConditionalScopeTracker.getCurrentConditionalStmtData();
/*  212 */       this.bitWires = null;
/*  213 */       int numberOfValues = condData.getBitList().size();
/*  214 */       ArrayList<Bit> conditionList = condData.getBitList();
/*  215 */       UnsignedInteger[] candidateList = new UnsignedInteger[numberOfValues];
/*      */       
/*  217 */       HashMap<Integer, UnsignedInteger> possibleVals = this.possibleValStack
/*  218 */         .pop();
/*  219 */       for (Integer idx : possibleVals.keySet()) {
/*  220 */         candidateList[idx.intValue()] = possibleVals.get(idx);
/*      */       }
/*  222 */       for (int i = 0; i < numberOfValues; i++) {
/*  223 */         if (candidateList[i] == null) {
/*  224 */           candidateList[i] = copy();
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  231 */       UnsignedInteger initial = candidateList[numberOfValues - 1];
/*  232 */       int startingIndex = -1;
/*  233 */       for (int j = numberOfValues - 2; j >= 0; j--) {
/*  234 */         if ((candidateList[j]).packedWire != initial.packedWire) {
/*  235 */           startingIndex = j;
/*      */           break;
/*      */         } 
/*      */       } 
/*  239 */       if (startingIndex == -1) {
/*      */         
/*  241 */         this.packedWire = initial.packedWire;
/*  242 */         this.maxValue = initial.maxValue;
/*  243 */         this.bitWires = initial.bitWires;
/*  244 */         this.currentBitwidth = initial.currentBitwidth;
/*      */         
/*  246 */         this.bitWidth = initial.bitWidth;
/*  247 */         this.constant = initial.constant;
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  252 */         UnsignedInteger current = initial;
/*  253 */         this.bitWidth = initial.bitWidth;
/*  254 */         this.packedWire = initial.packedWire;
/*  255 */         this.maxValue = initial.maxValue;
/*  256 */         this.bitWires = initial.bitWires;
/*  257 */         this.currentBitwidth = initial.currentBitwidth;
/*      */ 
/*      */ 
/*      */         
/*  261 */         for (int k = startingIndex; k >= 0; k--) {
/*      */           
/*  263 */           current = candidateList[k];
/*  264 */           Bit selectionBit = conditionList.get(k);
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
/*  276 */           this.packedWire = this.packedWire.muxBit(current.packedWire, 
/*  277 */               selectionBit.wire);
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  282 */           this.bitWires = null;
/*  283 */           this.constant = null;
/*      */           
/*  285 */           this.maxValue = (this.maxValue.compareTo(current.maxValue) > 0) ? this.maxValue : 
/*  286 */             current.maxValue;
/*      */           
/*  288 */           this.currentBitwidth = Math.max(this.currentBitwidth, 
/*  289 */               current.currentBitwidth);
/*      */         } 
/*      */       } 
/*  292 */       this.prevValStack.pop();
/*      */ 
/*      */       
/*  295 */       init();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UnsignedInteger copy() {
/*  304 */     return copy(this.bitWidth);
/*      */   }
/*      */ 
/*      */   
/*      */   public UnsignedInteger copy(int expectedBitwidth) {
/*  309 */     if (this.generator.__getPhase() == 0) {
/*  310 */       if (this.bitWidth < expectedBitwidth) {
/*  311 */         this.variableState.setConditionallySplittedAhead(true);
/*  312 */       } else if (expectedBitwidth < this.bitWidth) {
/*  313 */         this.variableState.setThresholdBitwidth(expectedBitwidth);
/*      */       } 
/*  315 */       UnsignedInteger unsignedInteger = new UnsignedInteger(this.generator, 
/*  316 */           expectedBitwidth, this.variableState);
/*  317 */       unsignedInteger.constant = this.constant;
/*  318 */       unsignedInteger.packedWire = this.packedWire;
/*  319 */       unsignedInteger.bitWires = this.bitWires;
/*  320 */       unsignedInteger.maxValue = this.maxValue;
/*  321 */       unsignedInteger.currentBitwidth = this.currentBitwidth;
/*  322 */       return unsignedInteger;
/*      */     } 
/*      */     
/*  325 */     UnsignedInteger v = new UnsignedInteger();
/*  326 */     v.bitWidth = expectedBitwidth;
/*  327 */     if (this.bitWires != null) {
/*  328 */       v.bitWires = this.bitWires.adjustLength(expectedBitwidth);
/*      */     }
/*  330 */     v.splittedAtCreationTime = this.splittedAtCreationTime;
/*  331 */     v.packedAtCreationTime = this.packedAtCreationTime;
/*  332 */     v.constant = this.constant;
/*      */     
/*  334 */     if (expectedBitwidth < this.bitWidth && this.bitWires != null) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  339 */       if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/*  340 */         v.packedWire = new PackedValue(this.bitWires.adjustLength(expectedBitwidth), expectedBitwidth);
/*      */       } else {
/*  342 */         v.packedWire = new PackedValue(this.bitWires.adjustLength(expectedBitwidth), 
/*  343 */             BITWIDTH_PER_CHUNK);
/*      */       } 
/*      */ 
/*      */       
/*  347 */       if (this.constant == null) {
/*  348 */         v.currentBitwidth = expectedBitwidth;
/*  349 */         v.maxValue = Util.computeMaxValue(expectedBitwidth);
/*      */       } else {
/*  351 */         v.currentBitwidth = this.constant.bitLength();
/*  352 */         v.maxValue = this.constant;
/*      */       } 
/*      */     } else {
/*      */       
/*  356 */       v.packedWire = this.packedWire;
/*  357 */       v.currentBitwidth = this.currentBitwidth;
/*  358 */       v.maxValue = this.maxValue;
/*      */     } 
/*  360 */     v.variableState = this.variableState;
/*  361 */     v.generator = this.generator;
/*  362 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UnsignedInteger(int bitWidth, PackedValue packedWire, BigInteger maxValue) {
/*  372 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  373 */     this.packedWire = packedWire;
/*  374 */     this.bitWidth = bitWidth;
/*  375 */     this.maxValue = maxValue;
/*  376 */     this.currentBitwidth = (maxValue == null) ? bitWidth : maxValue
/*  377 */       .bitLength();
/*      */     
/*  379 */     init();
/*      */   }
/*      */   
/*      */   public UnsignedInteger(int bitWidth, WireArray bitWires) {
/*  383 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  384 */     this.bitWires = bitWires;
/*  385 */     this.bitWidth = bitWidth;
/*  386 */     this.currentBitwidth = bitWires.size();
/*  387 */     if (this.currentBitwidth > bitWidth) {
/*  388 */       this.bitWires = new WireArray(Arrays.<Wire>copyOfRange(
/*  389 */             bitWires.asArray(), 0, bitWidth));
/*  390 */       this.currentBitwidth = bitWidth;
/*      */     } 
/*  392 */     init();
/*      */   }
/*      */   
/*      */   public UnsignedInteger(int bitWidth, PackedValue packedWire) {
/*  396 */     this(bitWidth, packedWire, (BigInteger)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public UnsignedInteger(int bitWidth, PackedValue packedWire, int initialNumOfBits) {
/*  401 */     this(bitWidth, packedWire, (BigInteger)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public UnsignedInteger(int bitWidth, BigInteger constant) {
/*  406 */     this(bitWidth, CircuitGenerator.__getActiveCircuitGenerator().__createConstantPackedValue(
/*  407 */           Util.prepConstant(constant, bitWidth), bitWidth));
/*      */   }
/*      */   
/*      */   public UnsignedInteger(BigInteger constant) {
/*  411 */     this(constant.bitLength() + ((constant.signum() == -1) ? 1 : 0), constant);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UnsignedInteger(int bitWidth, long constant) {
/*  420 */     this(bitWidth, BigInteger.valueOf(constant));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UnsignedInteger(CircuitGenerator generator, int bitWidth) {
/*  427 */     this.bitWidth = bitWidth;
/*  428 */     this.generator = generator;
/*  429 */     this.currentBitwidth = bitWidth;
/*  430 */     init();
/*      */   }
/*      */   
/*      */   public UnsignedInteger(int bitWidth) {
/*  434 */     this(CircuitGenerator.__getActiveCircuitGenerator(), bitWidth);
/*      */   }
/*      */ 
/*      */   
/*      */   private UnsignedInteger(CircuitGenerator generator, int bitWidth, VariableState st) {
/*  439 */     this.bitWidth = bitWidth;
/*  440 */     this.generator = generator;
/*  441 */     this.variableState = st;
/*  442 */     this.currentBitwidth = bitWidth;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private UnsignedInteger(CircuitGenerator generator, int bitWidth, BigInteger constant, VariableState st) {
/*  448 */     this.bitWidth = bitWidth;
/*  449 */     this.generator = generator;
/*  450 */     this.variableState = st;
/*  451 */     this.currentBitwidth = bitWidth;
/*  452 */     this.constant = constant;
/*      */   }
/*      */ 
/*      */   
/*      */   private void init() {
/*  457 */     checkConstant();
/*  458 */     if (this.variableState == null) {
/*  459 */       this.variableState = this.generator.__retrieveVariableState();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  465 */     if (this.maxValue == null) {
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
/*  482 */       this.maxValue = ResourceBundle.getInstance().getBigInteger((
/*  483 */           new BigInteger("2")).pow(this.bitWidth).subtract(
/*  484 */             BigInteger.ONE));
/*  485 */       this.currentBitwidth = this.bitWidth;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  490 */     if (this.generator.__getPhase() == 1)
/*      */     {
/*  492 */       if (this.constant != null) {
/*      */ 
/*      */         
/*  495 */         this.packedWire = this.generator.__createConstantPackedValue(this.maxValue, 
/*  496 */             this.bitWidth);
/*      */ 
/*      */         
/*  499 */         this.bitWires = this.packedWire.getBits(this.bitWidth, 
/*  500 */             BITWIDTH_PER_CHUNK, new String[0]);
/*  501 */       } else if (this.variableState != null) {
/*  502 */         if (this.variableState.isPackedAhead() && this.packedWire == null) {
/*  503 */           WireArray modified = this.bitWires.adjustLength(Math.min(
/*  504 */                 this.bitWires.size(), this.bitWidth));
/*  505 */           if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/*  506 */             this.packedWire = new PackedValue(modified, this.bitWidth);
/*      */           } else {
/*  508 */             this.packedWire = new PackedValue(modified, 
/*  509 */                 BITWIDTH_PER_CHUNK);
/*      */           } 
/*  511 */           this.maxValue = modified.computeTightUpperBoundOfBitWires(this.bitWidth);
/*  512 */           this.currentBitwidth = this.maxValue.bitLength();
/*  513 */         } else if (this.variableState.isSplittedAhead() && this.bitWires == null) {
/*      */           
/*  515 */           if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/*  516 */             this.bitWires = this.packedWire.getBits(this.bitWidth, this.bitWidth, new String[0]);
/*      */           } else {
/*  518 */             this.bitWires = this.packedWire.getBits(this.bitWidth, 
/*  519 */                 BITWIDTH_PER_CHUNK, new String[0]);
/*      */           } 
/*  521 */           if ((this.packedWire.getMaxVal(BITWIDTH_PER_CHUNK).bitLength() > this.bitWidth || 
/*  522 */             !this.packedWire.isAligned()) && this.variableState.isPackedAhead())
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  528 */             if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/*  529 */               this.packedWire = new PackedValue(this.bitWires, this.bitWidth);
/*      */             } else {
/*  531 */               this.packedWire = new PackedValue(this.bitWires, 
/*  532 */                   BITWIDTH_PER_CHUNK);
/*      */             } 
/*      */           }
/*  535 */           this.maxValue = this.bitWires.computeTightUpperBoundOfBitWires(this.bitWidth);
/*  536 */           this.currentBitwidth = this.maxValue.bitLength();
/*  537 */         } else if ((this.variableState.isConditionallySplittedAhead() || this.variableState
/*  538 */           .isConditionallySplittedAndAlignedAhead()) && 
/*  539 */           this.bitWires == null) {
/*      */           
/*  541 */           this.currentBitwidth = this.maxValue.bitLength();
/*  542 */           if (this.currentBitwidth > this.bitWidth) {
/*  543 */             if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/*  544 */               this.bitWires = this.packedWire.getBits(this.bitWidth, this.bitWidth, new String[0]);
/*      */             } else {
/*  546 */               this.bitWires = this.packedWire.getBits(this.bitWidth, 
/*  547 */                   BITWIDTH_PER_CHUNK, new String[0]);
/*      */             } 
/*      */             
/*  550 */             if (this.variableState.isPackedAhead())
/*      */             {
/*  552 */               if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/*  553 */                 this.packedWire = new PackedValue(this.bitWires, this.bitWidth);
/*      */               } else {
/*  555 */                 this.packedWire = new PackedValue(this.bitWires, 
/*  556 */                     BITWIDTH_PER_CHUNK);
/*      */               } 
/*      */             }
/*  559 */             this.maxValue = this.bitWires.computeTightUpperBoundOfBitWires(this.bitWidth);
/*  560 */             this.currentBitwidth = this.maxValue.bitLength();
/*      */ 
/*      */ 
/*      */           
/*      */           }
/*  565 */           else if (this.packedWire != null && !this.packedWire.isAligned() && 
/*  566 */             this.bitWidth > BITWIDTH_LIMIT_SHORT && 
/*  567 */             this.variableState
/*  568 */             .isConditionallySplittedAndAlignedAhead()) {
/*  569 */             this.packedWire = this.packedWire.align(
/*  570 */                 (int)Math.ceil(this.bitWidth * 1.0D / 
/*  571 */                   BITWIDTH_PER_CHUNK), 
/*  572 */                 BITWIDTH_PER_CHUNK);
/*      */           }
/*  574 */           else if (this.packedWire == null && this.variableState
/*  575 */             .isConditionallySplittedAndAlignedAhead()) {
/*  576 */             throw new RuntimeException("Case Unexpected");
/*      */           }
/*      */         
/*      */         }
/*  580 */         else if (this.bitWires != null && 
/*  581 */           this.bitWires.size() > this.bitWidth) {
/*  582 */           this.bitWires = new WireArray(Arrays.<Wire>copyOfRange(
/*  583 */                 this.bitWires.asArray(), 0, this.bitWidth));
/*      */ 
/*      */           
/*  586 */           if (this.variableState.isPackedAhead()) {
/*  587 */             if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/*  588 */               this.packedWire = new PackedValue(this.bitWires, this.bitWidth);
/*      */             } else {
/*      */               
/*  591 */               this.packedWire = new PackedValue(this.bitWires, 
/*  592 */                   BITWIDTH_PER_CHUNK);
/*      */             } 
/*      */           }
/*      */ 
/*      */ 
/*      */           
/*  598 */           this.maxValue = this.bitWires.computeTightUpperBoundOfBitWires(this.bitWidth);
/*  599 */           this.currentBitwidth = this.maxValue.bitLength();
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  604 */         if (this.variableState.getThresholdBitwidth() != -1 && 
/*  605 */           this.bitWires == null) {
/*      */           
/*  607 */           this.currentBitwidth = this.maxValue.bitLength();
/*  608 */           if (this.currentBitwidth > this.variableState.getThresholdBitwidth()) {
/*  609 */             if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/*  610 */               this.bitWires = this.packedWire.getBits(this.bitWidth, this.bitWidth, new String[0]);
/*      */             } else {
/*  612 */               this.bitWires = this.packedWire.getBits(this.bitWidth, 
/*  613 */                   BITWIDTH_PER_CHUNK, new String[0]);
/*      */             } 
/*  615 */             this.maxValue = this.bitWires.computeTightUpperBoundOfBitWires(this.bitWidth);
/*  616 */             this.currentBitwidth = this.maxValue.bitLength();
/*  617 */             if ((this.packedWire.getMaxVal(BITWIDTH_PER_CHUNK)
/*  618 */               .bitLength() > this.bitWidth || 
/*  619 */               !this.packedWire.isAligned()) && 
/*  620 */               this.variableState.isPackedAhead())
/*      */             {
/*      */ 
/*      */               
/*  624 */               if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/*  625 */                 this.packedWire = new PackedValue(this.bitWires, this.bitWidth);
/*      */               } else {
/*  627 */                 this.packedWire = new PackedValue(this.bitWires, 
/*  628 */                     BITWIDTH_PER_CHUNK);
/*      */               } 
/*      */             }
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkConstant() {
/*  640 */     if (this.constant == null) {
/*  641 */       if (this.packedWire != null) {
/*  642 */         this.constant = this.packedWire.getConstant(BITWIDTH_PER_CHUNK);
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*  648 */       else if (this.bitWires != null) {
/*  649 */         boolean allConstant = true;
/*  650 */         BigInteger v = BigInteger.ZERO;
/*  651 */         int i = 0; byte b; int j; Wire[] arrayOfWire;
/*  652 */         for (j = (arrayOfWire = this.bitWires.asArray()).length, b = 0; b < j; ) { Wire w = arrayOfWire[b];
/*  653 */           if (!(w instanceof ConstantWire)) {
/*  654 */             allConstant = false;
/*      */             break;
/*      */           } 
/*  657 */           ConstantWire constWire = (ConstantWire)w;
/*  658 */           if (!constWire.isBinary()) {
/*  659 */             throw new RuntimeException(
/*  660 */                 "Non-binary bit value used to construct an integer");
/*      */           }
/*  662 */           v = v.add(constWire.getConstant().multiply((
/*  663 */                 new BigInteger("2")).pow(i++)));
/*      */           
/*      */           b++; }
/*      */ 
/*      */         
/*  668 */         if (allConstant) {
/*  669 */           this.constant = v;
/*      */         }
/*      */       } 
/*      */     }
/*      */     
/*  674 */     if (this.constant != null) {
/*  675 */       this.constant = Util.prepConstant(this.constant, this.bitWidth);
/*  676 */       this.maxValue = this.constant;
/*  677 */       this.currentBitwidth = this.constant.bitLength();
/*      */     } 
/*      */   }
/*      */   
/*      */   public UnsignedInteger promote(int numBits) {
/*  682 */     if (this.bitWidth > numBits) {
/*  683 */       throw new IllegalArgumentException(
/*  684 */           "The given integer is less in bitwidth");
/*      */     }
/*  686 */     if (this.generator.__getPhase() == 0) {
/*  687 */       this.variableState.setConditionallySplittedAhead(true);
/*  688 */       this.variableState.setPackedAhead(true);
/*      */       
/*  690 */       return new UnsignedInteger(this.generator, numBits);
/*      */     } 
/*  692 */     return new UnsignedInteger(numBits, this.packedWire);
/*      */   }
/*      */ 
/*      */   
/*      */   public UnsignedInteger add(BigInteger b) {
/*  697 */     return add(new UnsignedInteger(b));
/*      */   }
/*      */   
/*      */   public UnsignedInteger mul(BigInteger b) {
/*  701 */     return mul(new UnsignedInteger(b));
/*      */   }
/*      */   
/*      */   public UnsignedInteger mul(Bit b) {
/*  705 */     return mul(new UnsignedInteger(1, new PackedValue(b.wire, 1)));
/*      */   }
/*      */   
/*      */   public UnsignedInteger add(Bit b) {
/*  709 */     return add(new UnsignedInteger(1, new PackedValue(b.wire, 1)));
/*      */   }
/*      */   
/*      */   public UnsignedInteger xorBitwise(BigInteger b) {
/*  713 */     return xorBitwise(new UnsignedInteger(b));
/*      */   }
/*      */ 
/*      */   
/*      */   public UnsignedInteger orBitwise(BigInteger b) {
/*  718 */     return orBitwise(new UnsignedInteger(b));
/*      */   }
/*      */ 
/*      */   
/*      */   public UnsignedInteger andBitwise(BigInteger b) {
/*  723 */     return andBitwise(new UnsignedInteger(b));
/*      */   }
/*      */ 
/*      */   
/*      */   public UnsignedInteger add(UnsignedInteger o) {
/*  728 */     int maxBitWidth = Math.max(this.bitWidth, o.bitWidth);
/*  729 */     if (isConstant() && o.isConstant()) {
/*  730 */       return new UnsignedInteger(maxBitWidth, getConstant().add(
/*  731 */             o.getConstant()));
/*      */     }
/*  733 */     if (isConstant() && getConstant().equals(BigInteger.ZERO)) {
/*  734 */       CircuitGenerator.__getActiveCircuitGenerator().__retrieveVariableState();
/*  735 */       return o.copy();
/*      */     } 
/*  737 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ZERO)) {
/*  738 */       CircuitGenerator.__getActiveCircuitGenerator().__retrieveVariableState();
/*  739 */       return copy();
/*      */     } 
/*      */     
/*  742 */     if (this.generator.__getPhase() == 0) {
/*  743 */       this.variableState.setPackedAhead(true);
/*  744 */       o.variableState.setPackedAhead(true);
/*      */       
/*  746 */       this.variableState.incAddUseCount();
/*  747 */       o.variableState.incAddUseCount();
/*      */       
/*  749 */       if (o.bitWidth < this.bitWidth) {
/*  750 */         o.variableState.setConditionallySplittedAhead(true);
/*  751 */       } else if (o.bitWidth > this.bitWidth) {
/*  752 */         this.variableState.setConditionallySplittedAhead(true);
/*      */       } 
/*  754 */       UnsignedInteger unsignedInteger = new UnsignedInteger(this.generator, Math.max(
/*  755 */             this.bitWidth, o.bitWidth));
/*  756 */       unsignedInteger.getVariableState().setMulIndex(
/*  757 */           Math.max(o.getVariableState().getMulIndex(), 
/*  758 */             this.variableState.getMulIndex()));
/*  759 */       unsignedInteger.getVariableState().setOptimizationAttributes(this.variableState, 
/*  760 */           o.variableState, false, maxBitWidth);
/*  761 */       return unsignedInteger;
/*      */     } 
/*  763 */     this.variableState.decAddUseCount();
/*  764 */     o.variableState.decAddUseCount();
/*  765 */     int newBitWidth = Math.max(this.bitWidth, o.bitWidth);
/*  766 */     boolean overflowCheck = this.packedWire.addOverflowCheck(o.packedWire);
/*  767 */     if (overflowCheck) {
/*  768 */       handleOverflow(this, o, false);
/*  769 */       overflowCheck = this.packedWire.addOverflowCheck(o.packedWire);
/*  770 */       if (overflowCheck)
/*  771 */         handleOverflow(this, o, false); 
/*      */     } 
/*  773 */     BigInteger outMaxValue = this.maxValue.add(o.maxValue);
/*  774 */     UnsignedInteger result = new UnsignedInteger(newBitWidth, 
/*  775 */         this.packedWire.add(o.packedWire), outMaxValue);
/*  776 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void handleOverflow(UnsignedInteger x1, UnsignedInteger x2, boolean isMul) {
/*  783 */     boolean longSetting = false;
/*  784 */     boolean adjusted = false;
/*      */     
/*  786 */     int maxBitWidth = Math.max(x1.bitWidth, x2.bitWidth);
/*  787 */     if (maxBitWidth > BITWIDTH_LIMIT_SHORT) {
/*  788 */       longSetting = true;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  793 */     int b1Max = x1.packedWire.getBitwidthOfLargestChunk();
/*  794 */     int b2Max = x2.packedWire.getBitwidthOfLargestChunk();
/*      */ 
/*      */     
/*  797 */     if (isMul) {
/*      */       
/*  799 */       if (longSetting) {
/*  800 */         if (b1Max + BITWIDTH_PER_CHUNK >= Config.getNumBitsFiniteFieldModulus()) {
/*  801 */           x1.adjustBitwidth();
/*  802 */           adjusted = true;
/*      */         } 
/*  804 */         if (b2Max + BITWIDTH_PER_CHUNK >= Config.getNumBitsFiniteFieldModulus()) {
/*  805 */           x2.adjustBitwidth();
/*  806 */           adjusted = true;
/*      */         } 
/*      */       } else {
/*      */         
/*  810 */         if (b1Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  811 */           x1.adjustBitwidth();
/*  812 */           adjusted = true;
/*      */         } 
/*  814 */         if (b2Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  815 */           x2.adjustBitwidth();
/*  816 */           adjusted = true;
/*      */         } 
/*      */       } 
/*      */     } else {
/*  820 */       if (b1Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  821 */         x1.adjustBitwidth();
/*  822 */         adjusted = true;
/*      */       } 
/*      */       
/*  825 */       if (b2Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  826 */         x2.adjustBitwidth();
/*  827 */         adjusted = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  832 */     if (adjusted) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  837 */     int excesss1 = x1.variableState.getMulUseCount() * (
/*  838 */       x1.currentBitwidth - x1.bitWidth);
/*  839 */     int excesss2 = x2.variableState.getMulUseCount() * (
/*  840 */       x2.currentBitwidth - x2.bitWidth);
/*      */     
/*  842 */     if (excesss1 > excesss2) {
/*  843 */       x1.adjustBitwidth();
/*  844 */     } else if (excesss2 < excesss1) {
/*  845 */       x2.adjustBitwidth();
/*      */     } else {
/*  847 */       excesss1 = x1.variableState.getAddUseCount() * (
/*  848 */         x1.currentBitwidth - x1.bitWidth);
/*  849 */       excesss2 = x2.variableState.getAddUseCount() * (
/*  850 */         x2.currentBitwidth - x2.bitWidth);
/*  851 */       if (excesss1 > excesss2) {
/*  852 */         x1.adjustBitwidth();
/*  853 */       } else if (excesss2 < excesss1) {
/*  854 */         x2.adjustBitwidth();
/*      */       }
/*  856 */       else if (x1.currentBitwidth - x1.bitWidth > 0) {
/*  857 */         x1.adjustBitwidth();
/*      */       } else {
/*  859 */         x2.adjustBitwidth();
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void adjustBitwidth() {
/*  869 */     if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/*  870 */       this.bitWires = this.packedWire.getBits(this.bitWidth, this.bitWidth, new String[0]);
/*      */     } else {
/*      */       
/*  873 */       this.bitWires = this.packedWire.getBits(this.bitWidth, BITWIDTH_PER_CHUNK, new String[0]);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  880 */     this.currentBitwidth = this.bitWidth;
/*  881 */     if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/*  882 */       this.packedWire = new PackedValue(this.bitWires, this.bitWidth);
/*      */     } else {
/*  884 */       this.packedWire = new PackedValue(this.bitWires, BITWIDTH_PER_CHUNK);
/*      */     } 
/*  886 */     this.maxValue = Util.computeMaxValue(this.currentBitwidth);
/*      */   }
/*      */   
/*      */   public UnsignedInteger mul(UnsignedInteger o) {
/*  890 */     int maxBitWidth = Math.max(this.bitWidth, o.bitWidth);
/*  891 */     if (isConstant() && o.isConstant()) {
/*  892 */       return new UnsignedInteger(maxBitWidth, getConstant()
/*  893 */           .multiply(o.getConstant()));
/*      */     }
/*  895 */     if (isConstant() && getConstant().equals(BigInteger.ONE)) {
/*  896 */       return o.copy();
/*      */     }
/*  898 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ONE)) {
/*  899 */       return copy();
/*      */     }
/*      */     
/*  902 */     if (isConstant() && getConstant().equals(BigInteger.ZERO)) {
/*  903 */       return new UnsignedInteger(maxBitWidth, 0L);
/*      */     }
/*      */     
/*  906 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ZERO)) {
/*  907 */       return new UnsignedInteger(maxBitWidth, 0L);
/*      */     }
/*      */     
/*  910 */     if (this.generator.__getPhase() == 0) {
/*      */       
/*  912 */       this.variableState.setPackedAhead(true);
/*  913 */       o.variableState.setPackedAhead(true);
/*  914 */       if (o.bitWidth < this.bitWidth) {
/*  915 */         o.variableState.setConditionallySplittedAhead(true);
/*  916 */       } else if (o.bitWidth > this.bitWidth) {
/*  917 */         this.variableState.setConditionallySplittedAhead(true);
/*      */       } 
/*  919 */       this.variableState.incMulUseCount();
/*  920 */       o.variableState.incMulUseCount();
/*      */       
/*  922 */       int idx1 = getVariableState().getMulIndex();
/*  923 */       int idx2 = o.getVariableState().getMulIndex();
/*      */       
/*  925 */       if (idx1 == 1 && this.bitWidth > BITWIDTH_LIMIT_SHORT) {
/*  926 */         this.variableState.setConditionallySplittedAhead(true);
/*      */       }
/*  928 */       if (idx2 == 1 && o.bitWidth > BITWIDTH_LIMIT_SHORT) {
/*  929 */         o.variableState.setConditionallySplittedAhead(true);
/*      */       }
/*      */       
/*  932 */       UnsignedInteger unsignedInteger = new UnsignedInteger(this.generator, maxBitWidth);
/*  933 */       unsignedInteger.getVariableState().incMulIndex();
/*  934 */       unsignedInteger.getVariableState().setOptimizationAttributes(this.variableState, 
/*  935 */           o.variableState, true, maxBitWidth);
/*  936 */       return unsignedInteger;
/*      */     } 
/*      */     
/*  939 */     this.variableState.decMulUseCount();
/*  940 */     o.variableState.decMulUseCount();
/*      */     
/*  942 */     boolean overflowCheck = this.packedWire.mulOverflowCheck(o.packedWire);
/*  943 */     if (overflowCheck) {
/*  944 */       handleOverflow(this, o, true);
/*  945 */       overflowCheck = this.packedWire.mulOverflowCheck(o.packedWire);
/*  946 */       if (overflowCheck)
/*  947 */         handleOverflow(this, o, true); 
/*      */     } 
/*  949 */     BigInteger outMaxValue = this.maxValue.multiply(o.maxValue);
/*  950 */     UnsignedInteger result = new UnsignedInteger(maxBitWidth, 
/*  951 */         this.packedWire.mul(o.packedWire), outMaxValue);
/*  952 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public UnsignedInteger subtract(UnsignedInteger o) {
/*  958 */     int maxBitWidth = Math.max(this.bitWidth, o.bitWidth);
/*  959 */     if (isConstant() && o.isConstant()) {
/*  960 */       return new UnsignedInteger(maxBitWidth, getConstant()
/*  961 */           .subtract(o.getConstant()));
/*      */     }
/*  963 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ZERO)) {
/*  964 */       CircuitGenerator.__getActiveCircuitGenerator().__retrieveVariableState();
/*  965 */       return copy();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  971 */     if (this.generator.__getPhase() == 0) {
/*  972 */       this.variableState.setPackedAhead(true);
/*  973 */       o.variableState.setPackedAhead(true);
/*      */       
/*  975 */       this.variableState.incAddUseCount();
/*  976 */       o.variableState.incAddUseCount();
/*      */       
/*  978 */       if (o.bitWidth < this.bitWidth) {
/*  979 */         o.variableState.setConditionallySplittedAhead(true);
/*  980 */       } else if (o.bitWidth > this.bitWidth) {
/*  981 */         this.variableState.setConditionallySplittedAhead(true);
/*      */       } 
/*  983 */       UnsignedInteger unsignedInteger = new UnsignedInteger(this.generator, Math.max(
/*  984 */             this.bitWidth, o.bitWidth));
/*  985 */       unsignedInteger.getVariableState().setMulIndex(
/*  986 */           Math.max(o.getVariableState().getMulIndex(), 
/*  987 */             this.variableState.getMulIndex()));
/*  988 */       unsignedInteger.getVariableState().setOptimizationAttributes(this.variableState, 
/*  989 */           o.variableState, false, maxBitWidth);
/*  990 */       return unsignedInteger;
/*      */     } 
/*  992 */     this.variableState.decAddUseCount();
/*  993 */     o.variableState.decAddUseCount();
/*  994 */     int newBitWidth = Math.max(this.bitWidth, o.bitWidth);
/*  995 */     BigInteger[] aux = SubtractionAUX.prepSub(o.packedWire, 
/*  996 */         Util.computeBound(newBitWidth), this.generator, newBitWidth);
/*  997 */     boolean overflowCheck = this.packedWire.addSubOverflowCheck(aux);
/*  998 */     if (overflowCheck) {
/*  999 */       handleOverflow(this, o, false);
/*      */       
/* 1001 */       aux = SubtractionAUX.prepSub(o.packedWire, 
/* 1002 */           Util.computeBound(newBitWidth), this.generator, newBitWidth);
/* 1003 */       overflowCheck = this.packedWire.addSubOverflowCheck(aux);
/* 1004 */       if (overflowCheck) {
/* 1005 */         handleOverflow(this, o, false);
/* 1006 */         aux = SubtractionAUX.prepSub(o.packedWire, 
/* 1007 */             Util.computeBound(newBitWidth), this.generator, 
/* 1008 */             newBitWidth);
/*      */       } 
/*      */     } 
/* 1011 */     BigInteger outMaxValue = this.maxValue.add(Util.group(aux, 
/* 1012 */           BITWIDTH_PER_CHUNK));
/* 1013 */     PackedValue p = this.packedWire.addsub(aux, o.packedWire);
/* 1014 */     UnsignedInteger result = new UnsignedInteger(newBitWidth, p, 
/* 1015 */         outMaxValue);
/*      */     
/* 1017 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public UnsignedInteger negate() {
/* 1022 */     if (isConstant()) {
/* 1023 */       return new UnsignedInteger(this.bitWidth, Util.computeBound(this.bitWidth)
/* 1024 */           .subtract(getConstant()));
/*      */     }
/* 1026 */     return (new UnsignedInteger(this.bitWidth, 0L)).subtract(this);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public UnsignedInteger xorBitwise(UnsignedInteger o) {
/* 1032 */     int maxBitWidth = Math.max(this.bitWidth, o.bitWidth);
/* 1033 */     if (isConstant() && o.isConstant()) {
/* 1034 */       return new UnsignedInteger(maxBitWidth, getConstant().xor(
/* 1035 */             o.getConstant()));
/*      */     }
/* 1037 */     if (this.generator.__getPhase() == 0) {
/* 1038 */       this.variableState.setSplittedAhead(true);
/* 1039 */       o.variableState.setSplittedAhead(true);
/* 1040 */       UnsignedInteger unsignedInteger = new UnsignedInteger(this.generator, maxBitWidth);
/* 1041 */       return unsignedInteger;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1047 */     UnsignedInteger result = new UnsignedInteger(maxBitWidth, 
/* 1048 */         this.bitWires.xorWireArray(o.bitWires, maxBitWidth, new String[0]));
/*      */     
/* 1050 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public UnsignedInteger orBitwise(UnsignedInteger o) {
/* 1056 */     int maxBitWidth = Math.max(this.bitWidth, o.bitWidth);
/* 1057 */     if (isConstant() && o.isConstant()) {
/* 1058 */       return new UnsignedInteger(maxBitWidth, getConstant().or(
/* 1059 */             o.getConstant()));
/*      */     }
/* 1061 */     if (this.generator.__getPhase() == 0) {
/* 1062 */       this.variableState.setSplittedAhead(true);
/* 1063 */       o.variableState.setSplittedAhead(true);
/* 1064 */       UnsignedInteger unsignedInteger = new UnsignedInteger(this.generator, maxBitWidth);
/*      */       
/* 1066 */       return unsignedInteger;
/*      */     } 
/*      */ 
/*      */     
/* 1070 */     UnsignedInteger result = new UnsignedInteger(maxBitWidth, 
/* 1071 */         this.bitWires.orWireArray(o.bitWires, maxBitWidth, new String[0]));
/*      */     
/* 1073 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public UnsignedInteger andBitwise(UnsignedInteger o) {
/* 1078 */     int maxBitWidth = Math.max(this.bitWidth, o.bitWidth);
/* 1079 */     if (isConstant() && o.isConstant()) {
/* 1080 */       return new UnsignedInteger(maxBitWidth, getConstant().and(
/* 1081 */             o.getConstant()));
/*      */     }
/*      */     
/* 1084 */     if (isConstant() && getConstant().equals(BigInteger.ZERO)) {
/* 1085 */       return new UnsignedInteger(maxBitWidth, 0L);
/*      */     }
/*      */     
/* 1088 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ZERO)) {
/* 1089 */       return new UnsignedInteger(maxBitWidth, 0L);
/*      */     }
/*      */ 
/*      */     
/* 1093 */     if (this.generator.__getPhase() == 0) {
/* 1094 */       this.variableState.setSplittedAhead(true);
/* 1095 */       o.variableState.setSplittedAhead(true);
/* 1096 */       UnsignedInteger unsignedInteger = new UnsignedInteger(this.generator, maxBitWidth);
/*      */       
/* 1098 */       return unsignedInteger;
/*      */     } 
/*      */     
/* 1101 */     UnsignedInteger result = new UnsignedInteger(maxBitWidth, 
/* 1102 */         this.bitWires.mulWireArray(o.bitWires, maxBitWidth, new String[0]));
/* 1103 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public UnsignedInteger invBits() {
/* 1108 */     if (isConstant()) {
/* 1109 */       return new UnsignedInteger(this.bitWidth, Util.invertBits(getConstant(), this.bitWidth));
/*      */     }
/*      */     
/* 1112 */     if (this.generator.__getPhase() == 0) {
/* 1113 */       this.variableState.setSplittedAhead(true);
/* 1114 */       UnsignedInteger unsignedInteger = new UnsignedInteger(this.generator, this.bitWidth);
/* 1115 */       return unsignedInteger;
/*      */     } 
/* 1117 */     UnsignedInteger result = new UnsignedInteger(this.bitWidth, 
/* 1118 */         this.bitWires.invAsBits(this.bitWidth, new String[0]));
/* 1119 */     return result;
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
/*      */   public UnsignedInteger shiftRight(int n) {
/* 1161 */     if (isConstant()) {
/* 1162 */       return new UnsignedInteger(this.bitWidth, getConstant().shiftRight(n));
/*      */     }
/* 1164 */     if (n == 0) {
/* 1165 */       return copy();
/*      */     }
/* 1167 */     if (this.generator.__getPhase() == 0) {
/* 1168 */       this.variableState.setSplittedAhead(true);
/* 1169 */       UnsignedInteger unsignedInteger = new UnsignedInteger(this.generator, this.bitWidth);
/* 1170 */       return unsignedInteger;
/*      */     } 
/*      */ 
/*      */     
/* 1174 */     UnsignedInteger result = new UnsignedInteger(this.bitWidth, 
/* 1175 */         this.bitWires.shiftRight(this.bitWidth, n, new String[] { "" }));
/*      */ 
/*      */ 
/*      */     
/* 1179 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public UnsignedInteger shiftLeft(int n) {
/* 1184 */     if (isConstant()) {
/* 1185 */       return new UnsignedInteger(this.bitWidth, getConstant().shiftLeft(n));
/*      */     }
/* 1187 */     if (n == 0)
/* 1188 */       return copy(); 
/* 1189 */     if (this.generator.__getPhase() == 0) {
/* 1190 */       this.variableState.setSplittedAhead(true);
/* 1191 */       UnsignedInteger unsignedInteger = new UnsignedInteger(this.generator, this.bitWidth);
/*      */       
/* 1193 */       return unsignedInteger;
/*      */     } 
/*      */ 
/*      */     
/* 1197 */     UnsignedInteger result = new UnsignedInteger(this.bitWidth, 
/* 1198 */         this.bitWires.shiftLeft(this.bitWidth, n, new String[0]));
/*      */     
/* 1200 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public WireArray getBitWires() {
/* 1205 */     if (this.generator.__getPhase() == 0) {
/* 1206 */       this.variableState.setSplittedAhead(true);
/* 1207 */       Wire[] result = new Wire[this.bitWidth];
/* 1208 */       if (this.constant == null) {
/* 1209 */         Arrays.fill((Object[])result, new BitWire(-1));
/*      */       } else {
/* 1211 */         for (int i = 0; i < this.bitWidth; i++) {
/* 1212 */           boolean b = this.constant.testBit(i);
/* 1213 */           result[i] = b ? this.generator.__getOneWire() : this.generator
/* 1214 */             .__getZeroWire();
/*      */         } 
/*      */       } 
/* 1217 */       return new WireArray(result);
/*      */     } 
/* 1219 */     return this.bitWires.adjustLength(this.bitWidth);
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
/*      */   public boolean isProbablyOverflowed() {
/* 1234 */     return (this.maxValue.bitLength() > this.bitWidth);
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
/*      */   public boolean isConstant() {
/* 1255 */     return (this.constant != null);
/*      */   }
/*      */   
/*      */   public PackedValue getPackedWire() {
/* 1259 */     if (this.packedWire == null && this.generator.__getPhase() == 0) {
/* 1260 */       this.variableState.setPackedAhead(true);
/* 1261 */       return new PackedValue(new Wire(-1), this.bitWidth);
/*      */     } 
/* 1263 */     return this.packedWire;
/*      */   }
/*      */   
/*      */   public BigInteger getMaxValue() {
/* 1267 */     return this.maxValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getRequiredBitWidth() {
/* 1272 */     return this.bitWidth;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getCurrentBitWidth() {
/* 1277 */     return this.currentBitwidth;
/*      */   }
/*      */ 
/*      */   
/*      */   public Wire[] toWires() {
/* 1282 */     if (this.packedWire != null) {
/* 1283 */       return this.packedWire.getArray();
/*      */     }
/* 1285 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void mapValue(BigInteger value, CircuitEvaluator evaluator) {
/* 1291 */     value = Util.prepConstant(value, this.bitWidth);
/* 1292 */     if (this.packedWire != null) {
/*      */       
/* 1294 */       evaluator.setWireValue(this.packedWire, value, this.bitWidth, 
/* 1295 */           BITWIDTH_PER_CHUNK);
/*      */     } else {
/* 1297 */       int length = this.bitWires.size();
/* 1298 */       for (int i = 0; i < length; i++) {
/* 1299 */         evaluator.setWireValue(this.bitWires.get(i), 
/* 1300 */             value.testBit(i) ? BigInteger.ONE : BigInteger.ZERO);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public BigInteger getValueFromEvaluator(CircuitEvaluator evaluator) {
/* 1307 */     if (this.packedWire != null) {
/* 1308 */       return evaluator.getWireValue(this.packedWire, BITWIDTH_PER_CHUNK);
/*      */     }
/*      */     
/* 1311 */     BigInteger s = BigInteger.ZERO;
/* 1312 */     BigInteger powerTwo = BigInteger.ONE;
/* 1313 */     int length = this.bitWires.size();
/* 1314 */     for (int i = 0; i < length; i++) {
/*      */       
/* 1316 */       s = s.add(powerTwo.multiply(evaluator.getWireValue(this.bitWires
/* 1317 */               .get(i))));
/* 1318 */       powerTwo = powerTwo.add(powerTwo);
/*      */     } 
/* 1320 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static UnsignedInteger createInput(CircuitGenerator generator, int bitwidth, String... desc) {
/*      */     PackedValue v;
/* 1328 */     if (bitwidth <= BITWIDTH_LIMIT_SHORT) {
/* 1329 */       Wire w = generator.__createInputWire(desc);
/* 1330 */       v = new PackedValue(w, bitwidth);
/*      */     } else {
/* 1332 */       int numChunks = 
/* 1333 */         (int)Math.ceil(bitwidth * 1.0D / BITWIDTH_PER_CHUNK);
/* 1334 */       Wire[] w = generator.__createInputWireArray(numChunks, new String[0]);
/* 1335 */       int[] bitwidths = new int[numChunks];
/* 1336 */       Arrays.fill(bitwidths, BITWIDTH_PER_CHUNK);
/* 1337 */       if (numChunks * BITWIDTH_PER_CHUNK != bitwidth) {
/* 1338 */         bitwidths[numChunks - 1] = bitwidth % BITWIDTH_PER_CHUNK;
/*      */       }
/* 1340 */       v = new PackedValue(w, bitwidths);
/*      */     } 
/* 1342 */     UnsignedInteger o = new UnsignedInteger(bitwidth, v);
/* 1343 */     generator.__getInputAux().add(o.copy(bitwidth));
/* 1344 */     return o;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static UnsignedInteger createWitness(CircuitGenerator generator, int bitwidth, String... desc) {
/*      */     PackedValue v;
/* 1351 */     if (bitwidth <= BITWIDTH_LIMIT_SHORT) {
/* 1352 */       Wire w = generator.__createProverWitnessWire(desc);
/* 1353 */       v = new PackedValue(w, bitwidth);
/*      */     } else {
/* 1355 */       int numChunks = 
/* 1356 */         (int)Math.ceil(bitwidth * 1.0D / BITWIDTH_PER_CHUNK);
/* 1357 */       Wire[] w = generator.__createProverWitnessWireArray(numChunks, new String[0]);
/* 1358 */       int[] bitwidths = new int[numChunks];
/* 1359 */       Arrays.fill(bitwidths, BITWIDTH_PER_CHUNK);
/* 1360 */       if (numChunks * BITWIDTH_PER_CHUNK != bitwidth) {
/* 1361 */         bitwidths[numChunks - 1] = bitwidth % BITWIDTH_PER_CHUNK;
/*      */       }
/* 1363 */       v = new PackedValue(w, bitwidths);
/*      */     } 
/*      */ 
/*      */     
/* 1367 */     UnsignedInteger o = new UnsignedInteger(bitwidth, v);
/* 1368 */     generator.__getProverAux().add(o.copy(bitwidth));
/* 1369 */     return o;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static UnsignedInteger createVerifiedWitness(CircuitGenerator generator, int bitwidth, String... desc) {
/*      */     PackedValue v;
/* 1376 */     if (bitwidth <= BITWIDTH_LIMIT_SHORT) {
/* 1377 */       Wire w = generator.__createProverWitnessWire(desc);
/* 1378 */       v = new PackedValue(w, bitwidth);
/*      */     } else {
/* 1380 */       int numChunks = 
/* 1381 */         (int)Math.ceil(bitwidth * 1.0D / BITWIDTH_PER_CHUNK);
/* 1382 */       Wire[] w = generator.__createProverWitnessWireArray(numChunks, new String[0]);
/* 1383 */       int[] bitwidths = new int[numChunks];
/* 1384 */       Arrays.fill(bitwidths, BITWIDTH_PER_CHUNK);
/* 1385 */       if (numChunks * BITWIDTH_PER_CHUNK != bitwidth) {
/* 1386 */         bitwidths[numChunks - 1] = bitwidth % BITWIDTH_PER_CHUNK;
/*      */       }
/* 1388 */       v = new PackedValue(w, bitwidths);
/*      */     } 
/* 1390 */     UnsignedInteger o = new UnsignedInteger(bitwidth, v);
/* 1391 */     generator.__getProverVerifiedAux().add(o.copy(bitwidth));
/* 1392 */     return o;
/*      */   }
/*      */ 
/*      */   
/*      */   public void verifyRange() {
/* 1397 */     if (this.bitWidth <= BITWIDTH_LIMIT_SHORT) {
/* 1398 */       this.packedWire.array[0].restrictBitLength(this.bitWidth, new String[0]);
/*      */     } else {
/* 1400 */       int numChunks = 
/* 1401 */         (int)Math.ceil(this.bitWidth * 1.0D / BITWIDTH_PER_CHUNK);
/* 1402 */       int[] bitwidths = new int[numChunks];
/* 1403 */       Arrays.fill(bitwidths, BITWIDTH_PER_CHUNK);
/* 1404 */       if (numChunks * BITWIDTH_PER_CHUNK != this.bitWidth) {
/* 1405 */         bitwidths[numChunks - 1] = this.bitWidth % BITWIDTH_PER_CHUNK;
/*      */       }
/* 1407 */       for (int i = 0; i < numChunks; i++) {
/* 1408 */         this.packedWire.array[i]
/* 1409 */           .restrictBitLength(bitwidths[numChunks - 1], new String[0]);
/*      */       }
/*      */     } 
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
/*      */   public static UnsignedInteger[] createInputArray(CircuitGenerator generator, int size, int bitwidth, String... desc) {
/* 1424 */     UnsignedInteger[] out = new UnsignedInteger[size];
/* 1425 */     for (int i = 0; i < size; i++) {
/* 1426 */       out[i] = createInput(generator, bitwidth, desc);
/*      */     }
/*      */     
/* 1429 */     return out;
/*      */   }
/*      */ 
/*      */   
/*      */   public static UnsignedInteger[] createZeroArray(CircuitGenerator generator, int size, int bitwidth, String... desc) {
/* 1434 */     UnsignedInteger[] out = new UnsignedInteger[size];
/* 1435 */     for (int i = 0; i < size; i++) {
/* 1436 */       out[i] = new UnsignedInteger(bitwidth, 0L);
/*      */     }
/* 1438 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createZeroArray(CircuitGenerator generator, int[] dims, int bitwidth, String... desc) {
/* 1444 */     if (dims.length == 1)
/* 1445 */       return createZeroArray(generator, dims[0], bitwidth, desc); 
/* 1446 */     if (dims.length == 2) {
/* 1447 */       UnsignedInteger[][] out = new UnsignedInteger[dims[0]][];
/* 1448 */       for (int i = 0; i < dims[0]; i++) {
/* 1449 */         out[i] = createZeroArray(generator, dims[1], bitwidth, desc);
/*      */       }
/* 1451 */       return out;
/* 1452 */     }  if (dims.length == 3) {
/* 1453 */       UnsignedInteger[][][] out = new UnsignedInteger[dims[0]][dims[1]][];
/* 1454 */       for (int i = 0; i < dims[0]; i++) {
/* 1455 */         for (int j = 0; j < dims[1]; j++) {
/* 1456 */           out[i][j] = createZeroArray(generator, dims[2], bitwidth, 
/* 1457 */               desc);
/*      */         }
/*      */       } 
/* 1460 */       return out;
/*      */     } 
/*      */     
/* 1463 */     throw new IllegalArgumentException(
/* 1464 */         "Initialization of higher dim arrays not supported at this point. Only 3 dimensions are supported.");
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
/*      */   public static UnsignedInteger[] createWitnessArray(CircuitGenerator generator, int size, int bitwidth, String... desc) {
/* 1481 */     UnsignedInteger[] out = new UnsignedInteger[size];
/* 1482 */     for (int i = 0; i < size; i++) {
/* 1483 */       out[i] = createWitness(generator, bitwidth, desc);
/*      */     }
/*      */     
/* 1486 */     return out;
/*      */   }
/*      */ 
/*      */   
/*      */   public static UnsignedInteger[] createVerifiedWitnessArray(CircuitGenerator generator, int size, int bitwidth, String... desc) {
/* 1491 */     UnsignedInteger[] out = new UnsignedInteger[size];
/* 1492 */     for (int i = 0; i < size; i++) {
/* 1493 */       out[i] = createVerifiedWitness(generator, bitwidth, desc);
/*      */     }
/*      */     
/* 1496 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void makeOutput(String... desc) {
/* 1506 */     if (this.generator.__getPhase() == 0) {
/*      */ 
/*      */       
/* 1509 */       this.variableState.setPackedAhead(true);
/*      */       
/* 1511 */       this.variableState.setConditionallySplittedAhead(true);
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */       
/* 1517 */       this.generator.__makeOutputArray(this.packedWire.array, desc);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static void makeOutput(CircuitGenerator generator, UnsignedInteger x, String... desc) {
/* 1523 */     x.makeOutput(new String[0]);
/*      */   } public static void makeOutput(CircuitGenerator generator, UnsignedInteger[] a, String... desc) {
/*      */     byte b;
/*      */     int i;
/*      */     UnsignedInteger[] arrayOfUnsignedInteger;
/* 1528 */     for (i = (arrayOfUnsignedInteger = a).length, b = 0; b < i; ) { UnsignedInteger x = arrayOfUnsignedInteger[b];
/*      */ 
/*      */       
/* 1531 */       x.makeOutput(new String[0]);
/*      */       b++; }
/*      */   
/*      */   }
/*      */ 
/*      */   
/*      */   public static Object createInputArray(CircuitGenerator generator, int[] dims, int bitwidth, String... desc) {
/* 1538 */     if (dims.length == 1)
/* 1539 */       return createInputArray(generator, dims[0], bitwidth, desc); 
/* 1540 */     if (dims.length == 2) {
/* 1541 */       UnsignedInteger[][] out = new UnsignedInteger[dims[0]][];
/* 1542 */       for (int i = 0; i < dims[0]; i++) {
/* 1543 */         out[i] = createInputArray(generator, dims[1], bitwidth, desc);
/*      */       }
/* 1545 */       return out;
/* 1546 */     }  if (dims.length == 3) {
/* 1547 */       UnsignedInteger[][][] out = new UnsignedInteger[dims[0]][dims[1]][];
/* 1548 */       for (int i = 0; i < dims[0]; i++) {
/* 1549 */         for (int j = 0; j < dims[1]; j++) {
/* 1550 */           out[i][j] = createInputArray(generator, dims[2], bitwidth, 
/* 1551 */               desc);
/*      */         }
/*      */       } 
/* 1554 */       return out;
/*      */     } 
/* 1556 */     throw new IllegalArgumentException(
/* 1557 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createWitnessArray(CircuitGenerator generator, int[] dims, int bitwidth, String... desc) {
/* 1565 */     if (dims.length == 1)
/* 1566 */       return createWitnessArray(generator, dims[0], bitwidth, desc); 
/* 1567 */     if (dims.length == 2) {
/* 1568 */       UnsignedInteger[][] out = new UnsignedInteger[dims[0]][];
/* 1569 */       for (int i = 0; i < dims[0]; i++) {
/* 1570 */         out[i] = createWitnessArray(generator, dims[1], bitwidth, desc);
/*      */       }
/* 1572 */       return out;
/* 1573 */     }  if (dims.length == 3) {
/* 1574 */       UnsignedInteger[][][] out = new UnsignedInteger[dims[0]][dims[1]][];
/* 1575 */       for (int i = 0; i < dims[0]; i++) {
/* 1576 */         for (int j = 0; j < dims[1]; j++) {
/* 1577 */           out[i][j] = createWitnessArray(generator, dims[2], bitwidth, 
/* 1578 */               desc);
/*      */         }
/*      */       } 
/* 1581 */       return out;
/*      */     } 
/* 1583 */     throw new IllegalArgumentException(
/* 1584 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createVerifiedWitnessArray(CircuitGenerator generator, int[] dims, int bitwidth, String... desc) {
/* 1592 */     if (dims.length == 1)
/* 1593 */       return createVerifiedWitnessArray(generator, dims[0], bitwidth, desc); 
/* 1594 */     if (dims.length == 2) {
/* 1595 */       UnsignedInteger[][] out = new UnsignedInteger[dims[0]][];
/* 1596 */       for (int i = 0; i < dims[0]; i++) {
/* 1597 */         out[i] = createVerifiedWitnessArray(generator, dims[1], bitwidth, desc);
/*      */       }
/* 1599 */       return out;
/* 1600 */     }  if (dims.length == 3) {
/* 1601 */       UnsignedInteger[][][] out = new UnsignedInteger[dims[0]][dims[1]][];
/* 1602 */       for (int i = 0; i < dims[0]; i++) {
/* 1603 */         for (int j = 0; j < dims[1]; j++) {
/* 1604 */           out[i][j] = createVerifiedWitnessArray(generator, dims[2], bitwidth, 
/* 1605 */               desc);
/*      */         }
/*      */       } 
/* 1608 */       return out;
/*      */     } 
/* 1610 */     throw new IllegalArgumentException(
/* 1611 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void makeOutput(CircuitGenerator generator, Object a, String... desc) {
/* 1619 */     if (a instanceof UnsignedInteger[]) {
/* 1620 */       UnsignedInteger[] array = (UnsignedInteger[])a;
/* 1621 */       for (int i = 0; i < array.length; i++) {
/* 1622 */         makeOutput(generator, array[i], desc);
/*      */       }
/* 1624 */     } else if (a instanceof UnsignedInteger[][]) {
/* 1625 */       UnsignedInteger[][] array = (UnsignedInteger[][])a;
/* 1626 */       for (int i = 0; i < array.length; i++) {
/* 1627 */         makeOutput(generator, array[i], desc);
/*      */       }
/* 1629 */     } else if (a instanceof UnsignedInteger[][][]) {
/* 1630 */       UnsignedInteger[][][] array = (UnsignedInteger[][][])a;
/* 1631 */       for (int i = 0; i < array.length; i++) {
/* 1632 */         makeOutput(generator, array[i], desc);
/*      */       }
/*      */     } else {
/* 1635 */       throw new IllegalArgumentException("Declaring higher dimensional arrays as outputs not supported at this point. Only 3 dimensions are supported");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void mapRandomValue(CircuitEvaluator evaluator) {
/* 1642 */     BigInteger rnd = Util.nextRandomBigInteger(this.bitWidth);
/* 1643 */     if (this.packedWire != null) {
/* 1644 */       evaluator.setWireValue(this.packedWire, rnd, this.bitWidth, 
/* 1645 */           BITWIDTH_PER_CHUNK);
/*      */     } else {
/* 1647 */       int length = this.bitWires.size();
/* 1648 */       for (int i = 0; i < length; i++) {
/* 1649 */         evaluator.setWireValue(this.bitWires.get(i), 
/* 1650 */             rnd.testBit(i) ? BigInteger.ONE : BigInteger.ZERO);
/*      */       }
/*      */     } 
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
/*      */   public Bit isEqualTo(BigInteger b) {
/* 1673 */     return isEqualTo(new UnsignedInteger(b));
/*      */   }
/*      */   
/*      */   public Bit isGreaterThan(UnsignedInteger o) {
/* 1677 */     if (o == this) {
/* 1678 */       return new Bit(false);
/*      */     }
/* 1680 */     if (o.isConstant() && isConstant()) {
/* 1681 */       return new Bit((getConstant().compareTo(o.getConstant()) > 0));
/*      */     }
/* 1683 */     if (this.generator.__getPhase() == 0) {
/* 1684 */       this.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 1685 */       this.variableState.setPackedAhead(true);
/*      */       
/* 1687 */       o.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 1688 */       o.variableState.setPackedAhead(true);
/*      */       
/* 1690 */       return new Bit(new Wire(-1));
/*      */     } 
/* 1692 */     int maxBitWidth = Math.max(this.bitWidth, o.bitWidth);
/*      */ 
/*      */     
/* 1695 */     handleComparisonOverflow(this, o);
/*      */     
/* 1697 */     return this.packedWire.isGreaterThan(o.packedWire, maxBitWidth);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Bit isGreaterThanOrEquals(UnsignedInteger o) {
/* 1708 */     if (o == this) {
/* 1709 */       return new Bit(true);
/*      */     }
/* 1711 */     if (o.isConstant() && isConstant()) {
/* 1712 */       return new Bit((getConstant().compareTo(o.getConstant()) >= 0));
/*      */     }
/* 1714 */     if (this.generator.__getPhase() == 0) {
/* 1715 */       this.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 1716 */       this.variableState.setPackedAhead(true);
/*      */       
/* 1718 */       o.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 1719 */       o.variableState.setPackedAhead(true);
/*      */       
/* 1721 */       return new Bit(new Wire(-1));
/*      */     } 
/* 1723 */     int maxBitWidth = Math.max(this.bitWidth, o.bitWidth);
/* 1724 */     handleComparisonOverflow(this, o);
/*      */     
/* 1726 */     return this.packedWire.isGreaterThanOrEqual(o.packedWire, 
/* 1727 */         maxBitWidth);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Bit isLessThan(UnsignedInteger o) {
/* 1738 */     if (o == this) {
/* 1739 */       return new Bit(false);
/*      */     }
/* 1741 */     if (o.isConstant() && isConstant()) {
/* 1742 */       return new Bit((getConstant().compareTo(o.getConstant()) < 0));
/*      */     }
/* 1744 */     if (this.generator.__getPhase() == 0) {
/*      */ 
/*      */ 
/*      */       
/* 1748 */       this.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 1749 */       this.variableState.setPackedAhead(true);
/*      */       
/* 1751 */       o.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 1752 */       o.variableState.setPackedAhead(true);
/*      */       
/* 1754 */       return new Bit(new Wire(-1));
/*      */     } 
/* 1756 */     int maxBitWidth = Math.max(this.bitWidth, o.bitWidth);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1761 */     handleComparisonOverflow(this, o);
/*      */ 
/*      */     
/* 1764 */     Bit result = this.packedWire.isLessThan(o.packedWire, maxBitWidth);
/*      */ 
/*      */     
/* 1767 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void handleComparisonOverflow(UnsignedInteger op1, UnsignedInteger op2) {
/* 1778 */     if (op1.packedWire.checkComparisonOverflow(op2.packedWire)) {
/* 1779 */       if (op1.packedWire.array.length == 1) {
/* 1780 */         op1.adjustBitwidth();
/*      */       }
/* 1782 */       if (op2.packedWire.array.length == 1) {
/* 1783 */         op2.adjustBitwidth();
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public Bit isLessThanOrEquals(UnsignedInteger o) {
/* 1790 */     if (o == this) {
/* 1791 */       return new Bit(true);
/*      */     }
/* 1793 */     if (o.isConstant() && isConstant()) {
/* 1794 */       return new Bit((getConstant().compareTo(o.getConstant()) <= 0));
/*      */     }
/* 1796 */     if (this.generator.__getPhase() == 0) {
/*      */ 
/*      */       
/* 1799 */       this.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 1800 */       this.variableState.setPackedAhead(true);
/*      */       
/* 1802 */       o.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 1803 */       o.variableState.setPackedAhead(true);
/*      */       
/* 1805 */       return new Bit(new Wire(-1));
/*      */     } 
/* 1807 */     int maxBitWidth = Math.max(this.bitWidth, o.bitWidth);
/* 1808 */     handleComparisonOverflow(this, o);
/* 1809 */     return this.packedWire.isLessThanOrEqual(o.packedWire, maxBitWidth);
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
/*      */   public Bit isNotEqualTo(BigInteger b) {
/* 1838 */     return isNotEqualTo(new UnsignedInteger(b));
/*      */   }
/*      */   
/*      */   public BigInteger getConstant() {
/* 1842 */     return this.constant;
/*      */   }
/*      */ 
/*      */   
/*      */   public Bit[] getBitElements() {
/* 1847 */     if (this.generator.__getPhase() == 0) {
/* 1848 */       this.variableState.setSplittedAhead(true);
/* 1849 */       Bit[] arrayOfBit = new Bit[this.bitWidth];
/* 1850 */       if (this.constant == null) {
/* 1851 */         Arrays.fill((Object[])arrayOfBit, new Bit(new Wire(-1)));
/*      */       } else {
/* 1853 */         for (int j = 0; j < this.bitWidth; j++) {
/* 1854 */           boolean b = this.constant.testBit(j);
/* 1855 */           Wire w = b ? this.generator.__getOneWire() : this.generator
/* 1856 */             .__getZeroWire();
/* 1857 */           arrayOfBit[j] = new Bit(w);
/*      */         } 
/*      */       } 
/* 1860 */       return arrayOfBit;
/*      */     } 
/* 1862 */     Bit[] result = new Bit[this.bitWidth];
/* 1863 */     WireArray array = this.bitWires.adjustLength(this.bitWidth);
/* 1864 */     for (int i = 0; i < this.bitWidth; i++) {
/* 1865 */       result[i] = new Bit(array.get(i));
/*      */     }
/* 1867 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public VariableState getVariableState() {
/* 1872 */     return this.variableState;
/*      */   }
/*      */ 
/*      */   
/*      */   public void forceEqual(IAuxType o) {
/* 1877 */     if (!(o instanceof UnsignedInteger)) {
/* 1878 */       throw new IllegalArgumentException("UnsignedINT expected");
/*      */     }
/*      */     
/* 1881 */     UnsignedInteger other = (UnsignedInteger)o;
/* 1882 */     if (getConstant() != null && other.getConstant() != null && 
/* 1883 */       !getConstant().equals(other.getConstant()))
/*      */     {
/* 1885 */       throw new RuntimeException("Constraint fails on constant uints");
/*      */     }
/*      */ 
/*      */     
/* 1889 */     if (this.generator.__getPhase() == 0) {
/*      */       
/* 1891 */       this.variableState.setPackedAhead(true);
/* 1892 */       other.variableState.setPackedAhead(true);
/* 1893 */       this.variableState.setConditionallySplittedAhead(true);
/* 1894 */       other.variableState.setConditionallySplittedAhead(true);
/*      */     }
/* 1896 */     else if (this.bitWidth > BITWIDTH_LIMIT_SHORT || 
/* 1897 */       other.bitWidth > BITWIDTH_LIMIT_SHORT) {
/* 1898 */       this.packedWire.forceEquality2(other.packedWire);
/*      */     } else {
/* 1900 */       this.generator.__addEqualityAssertion(this.packedWire.array[0], 
/* 1901 */           other.packedWire.array[0], new String[0]);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UnsignedInteger div(UnsignedInteger o) {
/* 1911 */     if (o.isConstant() && isConstant()) {
/* 1912 */       return new UnsignedInteger(this.bitWidth, getConstant().divide(
/* 1913 */             o.getConstant()));
/*      */     }
/* 1915 */     if (o.isConstant()) {
/* 1916 */       if (o.getConstant().equals(BigInteger.ZERO)) {
/* 1917 */         throw new RuntimeException("Error: Division by zero");
/*      */       }
/* 1919 */       if (o.getConstant().bitLength() > this.bitWidth) {
/* 1920 */         return new UnsignedInteger(this.bitWidth, BigInteger.ZERO);
/*      */       }
/* 1922 */       BigInteger modulus = o.getConstant();
/* 1923 */       int bitlength = modulus.bitLength();
/* 1924 */       if (modulus.equals(BigInteger.valueOf(2L).pow(bitlength - 1)))
/*      */       {
/* 1926 */         return shiftRight(bitlength - 1).copy(this.bitWidth);
/*      */       }
/*      */     } 
/*      */     
/* 1930 */     if (this.generator.__getPhase() == 0) {
/*      */       
/* 1932 */       this.variableState.setPackedAhead(true);
/* 1933 */       this.variableState.setConditionallySplittedAhead(true);
/* 1934 */       o.variableState.setPackedAhead(true);
/* 1935 */       o.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 1936 */       UnsignedInteger result = new UnsignedInteger(this.generator, this.bitWidth);
/* 1937 */       return result;
/*      */     } 
/* 1939 */     if (Config.enforceInternalDivisionNonZeroChecks) {
/* 1940 */       o.packedWire.forceNonZero();
/*      */     }
/* 1942 */     if (this.bitWidth > BITWIDTH_LIMIT_SHORT || 
/* 1943 */       o.bitWidth > BITWIDTH_LIMIT_SHORT) {
/*      */       
/* 1945 */       BigInteger aux = Util.computeMaxValue(this.bitWidth);
/* 1946 */       BigInteger[] auxChunks = Util.split(aux, 
/* 1947 */           BITWIDTH_PER_CHUNK);
/* 1948 */       boolean overflowCheck = o.packedWire
/* 1949 */         .mulAddOverflowCheck(new PackedValue(auxChunks), this.packedWire);
/* 1950 */       if (overflowCheck) {
/* 1951 */         o.adjustBitwidth();
/*      */       }
/*      */ 
/*      */       
/* 1955 */       if (o.isConstant()) {
/* 1956 */         PackedValue packedValue1 = (new LongIntegerModConstantGadget(
/* 1957 */             this.packedWire, o.packedWire, true, new String[0])).getQuotient();
/* 1958 */         int resultBitwidth = this.bitWidth - o.getConstant().bitLength() + 1;
/* 1959 */         return new UnsignedInteger(this.bitWidth, packedValue1, resultBitwidth);
/*      */       } 
/*      */       
/* 1962 */       PackedValue packedValue = (new LongIntegerModNotStrictModulusGadget(
/* 1963 */           this.packedWire, o.packedWire, true, new String[0])).getQuotient();
/* 1964 */       return new UnsignedInteger(this.bitWidth, packedValue);
/*      */     } 
/*      */     
/* 1967 */     if (o.isConstant()) {
/* 1968 */       Wire wire = (new ModConstantGadget(this.packedWire.array[0], 
/* 1969 */           this.bitWidth, o.getConstant(), true, new String[0])).getQuotient();
/* 1970 */       int resultBitwidth = this.bitWidth - o.getConstant().bitLength() + 1;
/* 1971 */       return new UnsignedInteger(this.bitWidth, new PackedValue(wire, 
/* 1972 */             resultBitwidth), Util.computeMaxValue(resultBitwidth));
/*      */     } 
/* 1974 */     Wire q = (new ModGadget(this.packedWire.array[0], this.bitWidth, 
/* 1975 */         o.packedWire.array[0], o.bitWidth, true, new String[0])).getQuotient();
/* 1976 */     return new UnsignedInteger(this.bitWidth, new PackedValue(q, 
/* 1977 */           this.bitWidth));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public UnsignedInteger mod(UnsignedInteger o) {
/* 1985 */     int minBitwidth = Math.min(this.bitWidth, o.bitWidth);
/*      */ 
/*      */     
/* 1988 */     if (o.isConstant() && isConstant()) {
/* 1989 */       return new UnsignedInteger(minBitwidth, getConstant().mod(
/* 1990 */             o.getConstant()));
/*      */     }
/* 1992 */     if (o.isConstant()) {
/* 1993 */       if (o.getConstant().equals(BigInteger.ZERO)) {
/* 1994 */         throw new RuntimeException("Error: Division by zero");
/*      */       }
/* 1996 */       if (o.getConstant().bitLength() > this.bitWidth) {
/* 1997 */         return copy();
/*      */       }
/* 1999 */       BigInteger modulus = o.getConstant();
/* 2000 */       int bitlength = modulus.bitLength();
/* 2001 */       if (modulus.equals(BigInteger.valueOf(2L).pow(bitlength - 1)))
/*      */       {
/* 2003 */         return andBitwise(modulus.subtract(BigInteger.ONE)).copy(
/* 2004 */             bitlength - 1);
/*      */       }
/*      */     } 
/*      */     
/* 2008 */     if (this.generator.__getPhase() == 0) {
/*      */       
/* 2010 */       this.variableState.setPackedAhead(true);
/* 2011 */       this.variableState.setConditionallySplittedAhead(true);
/* 2012 */       o.variableState.setPackedAhead(true);
/* 2013 */       o.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 2014 */       UnsignedInteger result = new UnsignedInteger(this.generator, minBitwidth);
/* 2015 */       return result;
/*      */     } 
/* 2017 */     if (Config.enforceInternalDivisionNonZeroChecks)
/* 2018 */       o.packedWire.forceNonZero(); 
/* 2019 */     if (this.bitWidth > BITWIDTH_LIMIT_SHORT || 
/* 2020 */       o.bitWidth > BITWIDTH_LIMIT_SHORT) {
/*      */       PackedValue packedValue;
/* 2022 */       BigInteger aux = Util.computeMaxValue(this.bitWidth);
/* 2023 */       BigInteger[] auxChunks = Util.split(aux, 
/* 2024 */           BITWIDTH_PER_CHUNK);
/* 2025 */       boolean overflowCheck = o.packedWire
/* 2026 */         .mulAddOverflowCheck(new PackedValue(auxChunks), this.packedWire);
/* 2027 */       if (overflowCheck) {
/* 2028 */         o.adjustBitwidth();
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 2033 */       if (o.isConstant()) {
/* 2034 */         packedValue = (new LongIntegerModConstantGadget(
/* 2035 */             this.packedWire, o.packedWire, true, new String[0])).getRemainder();
/*      */       } else {
/* 2037 */         packedValue = (new LongIntegerModNotStrictModulusGadget(
/* 2038 */             this.packedWire, o.packedWire, true, new String[0])).getRemainder();
/*      */       } 
/*      */       
/* 2041 */       return new UnsignedInteger(minBitwidth, packedValue);
/* 2042 */     }  if (o.isConstant()) {
/* 2043 */       Wire wire = (new ModConstantGadget(this.packedWire.array[0], 
/* 2044 */           this.bitWidth, o.getConstant(), true, new String[0])).getRemainder();
/* 2045 */       return new UnsignedInteger(minBitwidth, new PackedValue(wire, 
/* 2046 */             minBitwidth));
/*      */     } 
/* 2048 */     Wire r = (new ModGadget(this.packedWire.array[0], this.bitWidth, 
/* 2049 */         o.packedWire.array[0], o.bitWidth, true, new String[0])).getRemainder();
/* 2050 */     return new UnsignedInteger(minBitwidth, new PackedValue(r, 
/* 2051 */           minBitwidth));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Bit isEqualTo(IAuxType o) {
/* 2058 */     if (!(o instanceof UnsignedInteger)) {
/* 2059 */       throw new IllegalArgumentException("UnsignedINT expected");
/*      */     }
/* 2061 */     if (this == o) {
/* 2062 */       return new Bit(true);
/*      */     }
/* 2064 */     UnsignedInteger other = (UnsignedInteger)o;
/* 2065 */     if (getConstant() != null && other.getConstant() != null) {
/* 2066 */       return new Bit(getConstant().equals(other.getConstant()));
/*      */     }
/* 2068 */     if (this.generator.__getPhase() == 0) {
/* 2069 */       this.variableState.setPackedAhead(true);
/* 2070 */       other.variableState.setPackedAhead(true);
/* 2071 */       this.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 2072 */       other.variableState.setConditionallySplittedAndAlignedAhead(true);
/*      */ 
/*      */       
/* 2075 */       return new Bit(new Wire(-1));
/*      */     } 
/* 2077 */     if (this.bitWidth > BITWIDTH_LIMIT_SHORT || 
/* 2078 */       other.bitWidth > BITWIDTH_LIMIT_SHORT) {
/* 2079 */       return this.packedWire.isEqualTo(other.packedWire);
/*      */     }
/* 2081 */     return new Bit(
/* 2082 */         this.packedWire.array[0]
/* 2083 */         .isEqualTo(other.packedWire.array[0], new String[0]));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Bit isNotEqualTo(IAuxType o) {
/* 2089 */     return isEqualTo(o).inv();
/*      */   }
/*      */   
/*      */   public static Class<?> __getClassRef() {
/* 2093 */     return UnsignedInteger.class;
/*      */   }
/*      */   
/*      */   public VariableState getState() {
/* 2097 */     return this.variableState;
/*      */   }
/*      */ 
/*      */   
/*      */   public PackedValue getPackedValue() {
/* 2102 */     return this.packedWire;
/*      */   }
/*      */   
/*      */   public static UnsignedInteger instantiateFrom(int bitwidth, byte v) {
/* 2106 */     return new UnsignedInteger(bitwidth, v);
/*      */   }
/*      */   
/*      */   public static UnsignedInteger instantiateFrom(int bitwidth, int v) {
/* 2110 */     return new UnsignedInteger(bitwidth, v);
/*      */   }
/*      */   
/*      */   public static UnsignedInteger instantiateFrom(int bitwidth, long v) {
/* 2114 */     return new UnsignedInteger(bitwidth, v);
/*      */   }
/*      */   
/*      */   public static UnsignedInteger instantiateFrom(int bitwidth, BigInteger v) {
/* 2118 */     return new UnsignedInteger(bitwidth, v);
/*      */   }
/*      */   
/*      */   public static UnsignedInteger instantiateFrom(int bitwidth, String v) {
/* 2122 */     return new UnsignedInteger(bitwidth, new BigInteger(v));
/*      */   }
/*      */ 
/*      */   
/*      */   public static UnsignedInteger instantiateFrom(int bitwidth, UnsignedInteger v) {
/* 2127 */     return v.copy(bitwidth);
/*      */   }
/*      */   
/*      */   public static UnsignedInteger instantiateFrom(int bitwidth, Bit v) {
/* 2131 */     return v.toUnsignedInteger().copy(bitwidth);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static UnsignedInteger instantiateFrom(int bitwidth, FieldElement e) {
/* 2140 */     if (e.isConstant()) {
/* 2141 */       return new UnsignedInteger(e.getConstant().and(Util.computeMaxValue(bitwidth)));
/*      */     }
/*      */     
/* 2144 */     if (e.getModulus().bitLength() > bitwidth) {
/* 2145 */       return instantiateFrom(e.getModulus().bitLength() + 1, e).copy(
/* 2146 */           bitwidth);
/*      */     }
/* 2148 */     CircuitGenerator g = CircuitGenerator.__getActiveCircuitGenerator();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2154 */     if (g.__getPhase() == 0) {
/* 2155 */       e.getState().setConditionallySplittedAhead(true);
/*      */       
/* 2157 */       e.getState().setMustBeWithinRange(true);
/* 2158 */       e.getState().setPackedAhead(true);
/* 2159 */       g.__retrieveVariableState();
/* 2160 */       return new UnsignedInteger(g, bitwidth, e.getState());
/*      */     } 
/*      */     
/* 2163 */     UnsignedInteger v = new UnsignedInteger();
/* 2164 */     v.bitWidth = bitwidth;
/* 2165 */     v.constant = e.getConstant();
/* 2166 */     v.packedWire = e.packedWire;
/* 2167 */     v.bitWires = e.bitWires;
/* 2168 */     v.variableState = e.variableState;
/* 2169 */     v.generator = g;
/* 2170 */     v.maxValue = e.maxValue;
/* 2171 */     v.currentBitwidth = e.currentBitwidth;
/* 2172 */     g.__retrieveVariableState();
/*      */     
/* 2174 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static UnsignedInteger instantiateFrom(int bitwidth, GroupElement e) {
/* 2181 */     if (e.isConstant()) {
/* 2182 */       return new UnsignedInteger(e.getConstant().and(Util.computeMaxValue(bitwidth)));
/*      */     }
/*      */     
/* 2185 */     if (e.getModulus().bitLength() > bitwidth) {
/* 2186 */       return instantiateFrom(e.getModulus().bitLength() + 1, e).copy(
/* 2187 */           bitwidth);
/*      */     }
/* 2189 */     CircuitGenerator g = CircuitGenerator.__getActiveCircuitGenerator();
/*      */ 
/*      */     
/* 2192 */     if (g.__getPhase() == 0) {
/* 2193 */       e.getState().setConditionallySplittedAhead(true);
/*      */       
/* 2195 */       e.getState().setMustBeWithinRange(true);
/* 2196 */       e.getState().setPackedAhead(true);
/* 2197 */       g.__retrieveVariableState();
/* 2198 */       return new UnsignedInteger(g, bitwidth, e.getState());
/*      */     } 
/*      */     
/* 2201 */     UnsignedInteger v = new UnsignedInteger();
/* 2202 */     v.bitWidth = bitwidth;
/* 2203 */     v.constant = e.getConstant();
/* 2204 */     v.packedWire = e.packedWire;
/* 2205 */     v.bitWires = e.bitWires;
/* 2206 */     v.variableState = e.variableState;
/* 2207 */     v.generator = g;
/* 2208 */     v.maxValue = e.maxValue;
/* 2209 */     v.currentBitwidth = e.currentBitwidth;
/* 2210 */     g.__retrieveVariableState();
/*      */     
/* 2212 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static UnsignedInteger[] instantiateFrom(int bitwidth, int[] v) {
/* 2219 */     UnsignedInteger[] a = new UnsignedInteger[v.length];
/* 2220 */     for (int i = 0; i < a.length; i++)
/* 2221 */       a[i] = instantiateFrom(bitwidth, v[i]); 
/* 2222 */     return a;
/*      */   }
/*      */   
/*      */   public static UnsignedInteger[] instantiateFrom(int bitwidth, byte[] v) {
/* 2226 */     UnsignedInteger[] a = new UnsignedInteger[v.length];
/* 2227 */     for (int i = 0; i < a.length; i++)
/* 2228 */       a[i] = instantiateFrom(bitwidth, v[i]); 
/* 2229 */     return a;
/*      */   }
/*      */   
/*      */   public static UnsignedInteger[] instantiateFrom(int bitwidth, long[] v) {
/* 2233 */     UnsignedInteger[] a = new UnsignedInteger[v.length];
/* 2234 */     for (int i = 0; i < a.length; i++)
/* 2235 */       a[i] = instantiateFrom(bitwidth, v[i]); 
/* 2236 */     return a;
/*      */   }
/*      */ 
/*      */   
/*      */   public static UnsignedInteger[] instantiateFrom(int bitwidth, UnsignedInteger[] v) {
/* 2241 */     UnsignedInteger[] a = new UnsignedInteger[v.length];
/* 2242 */     for (int i = 0; i < a.length; i++)
/* 2243 */       a[i] = instantiateFrom(bitwidth, v[i]); 
/* 2244 */     return a;
/*      */   }
/*      */   
/*      */   public static UnsignedInteger[] instantiateFrom(int bitwidth, BigInteger[] v) {
/* 2248 */     UnsignedInteger[] a = new UnsignedInteger[v.length];
/* 2249 */     for (int i = 0; i < a.length; i++)
/* 2250 */       a[i] = instantiateFrom(bitwidth, v[i]); 
/* 2251 */     return a;
/*      */   }
/*      */   
/*      */   public static UnsignedInteger[] instantiateFrom(int bitwidth, String[] v) {
/* 2255 */     UnsignedInteger[] a = new UnsignedInteger[v.length];
/* 2256 */     for (int i = 0; i < a.length; i++)
/* 2257 */       a[i] = instantiateFrom(bitwidth, v[i]); 
/* 2258 */     return a;
/*      */   }
/*      */ 
/*      */   
/*      */   public static UnsignedInteger[] instantiateFrom(int bitwidth, FieldElement[] v) {
/* 2263 */     UnsignedInteger[] a = new UnsignedInteger[v.length];
/* 2264 */     for (int i = 0; i < a.length; i++)
/* 2265 */       a[i] = instantiateFrom(bitwidth, v[i]); 
/* 2266 */     return a;
/*      */   }
/*      */ 
/*      */   
/*      */   public static UnsignedInteger[] instantiateFrom(int bitwidth, GroupElement[] v) {
/* 2271 */     UnsignedInteger[] a = new UnsignedInteger[v.length];
/* 2272 */     for (int i = 0; i < a.length; i++)
/* 2273 */       a[i] = instantiateFrom(bitwidth, v[i]); 
/* 2274 */     return a;
/*      */   }
/*      */   
/*      */   public static UnsignedInteger[] instantiateFrom(int bitwidth, Bit[] v) {
/* 2278 */     UnsignedInteger[] a = new UnsignedInteger[v.length];
/* 2279 */     for (int i = 0; i < a.length; i++)
/* 2280 */       a[i] = instantiateFrom(bitwidth, v[i]); 
/* 2281 */     return a;
/*      */   }
/*      */   
/*      */   public UnsignedInteger() {}
/*      */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\UnsignedInteger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */