/*      */ package backend.auxTypes;
/*      */ 
/*      */ import backend.config.Config;
/*      */ import backend.eval.CircuitEvaluator;
/*      */ import backend.structure.BitWire;
/*      */ import backend.structure.CircuitGenerator;
/*      */ import backend.structure.ConstantWire;
/*      */ import backend.structure.Wire;
/*      */ import backend.structure.WireArray;
/*      */ import examples.gadgets.CustomLongFieldDivGadget;
/*      */ import examples.gadgets.CustomShortFieldDivGadget;
/*      */ import examples.gadgets.FieldDivisionGadget;
/*      */ import examples.gadgets.InverseLongIntegerModGadget;
/*      */ import examples.gadgets.LongIntegerModConstantGadget;
/*      */ import examples.gadgets.ModConstantGadget;
/*      */ import examples.gadgets.ShortIntegerModGadget;
/*      */ import java.math.BigInteger;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Stack;
/*      */ import util.Util;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class FieldElement
/*      */   implements IAuxType, ConditionalScopeImpactedType
/*      */ {
/*      */   protected PackedValue packedWire;
/*      */   protected WireArray bitWires;
/*      */   protected int currentBitwidth;
/*      */   protected BigInteger modulus;
/*      */   protected BigInteger maxValue;
/*      */   protected BigInteger constant;
/*      */   protected CircuitGenerator generator;
/*      */   protected VariableState variableState;
/*      */   protected boolean packedAtCreationTime;
/*      */   protected boolean splittedAtCreationTime;
/*   44 */   protected int conditionalScopeId = ConditionalScopeTracker.getCurrentScopeId();
/*      */   
/*   46 */   protected int scope = ConditionalScopeTracker.getCurrentScopeId();
/*      */   
/*      */   protected Stack<HashMap<Integer, FieldElement>> possibleValStack;
/*      */   protected Stack<FieldElement> prevValStack;
/*      */   private boolean stateChanged;
/*      */   protected boolean nativeSnarkField = false;
/*      */   
/*      */   public void setConditionalScopeId(int id) {
/*   54 */     this.conditionalScopeId = id;
/*      */   }
/*      */   
/*      */   public FieldElement(FieldElement o) {
/*   58 */     this.generator = o.generator;
/*   59 */     this.maxValue = o.maxValue;
/*   60 */     this.constant = o.constant;
/*   61 */     this.variableState = o.variableState;
/*   62 */     this.currentBitwidth = o.currentBitwidth;
/*   63 */     this.bitWires = o.bitWires;
/*   64 */     this.packedWire = o.packedWire;
/*   65 */     this.conditionalScopeId = o.conditionalScopeId;
/*   66 */     this.modulus = o.modulus;
/*   67 */     this.nativeSnarkField = o.nativeSnarkField;
/*   68 */     this.scope = o.scope;
/*      */   }
/*      */   
/*      */   public BigInteger getConstant() {
/*   72 */     return this.constant;
/*      */   }
/*      */   
/*      */   public void assign(FieldElement target) {
/*   76 */     if (this.generator.__getPhase() == 0) {
/*      */       
/*   78 */       if (this.scope != ConditionalScopeTracker.getCurrentScopeId()) {
/*   79 */         ConditionalScopeTracker.register(this, this.scope);
/*   80 */         if (this.possibleValStack == null) {
/*   81 */           this.possibleValStack = new Stack<>();
/*      */         }
/*   83 */         if (this.prevValStack == null) {
/*   84 */           this.prevValStack = new Stack<>();
/*      */         }
/*   86 */         int current = ConditionalScopeTracker.getCurrentScopeId();
/*      */         
/*   88 */         for (int i = 0; i < current - this.scope; i++) {
/*   89 */           FieldElement c = copy();
/*   90 */           c.variableState.setPackedAhead(true);
/*   91 */           this.prevValStack.push(c);
/*   92 */           this.possibleValStack.push(new HashMap<>());
/*      */         } 
/*   94 */         this.stateChanged = true;
/*   95 */         this.scope = ConditionalScopeTracker.getCurrentScopeId();
/*      */       } 
/*      */       
/*   98 */       this.constant = target.constant;
/*   99 */       this.nativeSnarkField = target.nativeSnarkField;
/*  100 */       this.variableState = target.variableState;
/*      */     
/*      */     }
/*  103 */     else if (this.scope == ConditionalScopeTracker.getCurrentScopeId()) {
/*      */       
/*  105 */       this.bitWires = target.bitWires;
/*  106 */       this.splittedAtCreationTime = target.splittedAtCreationTime;
/*  107 */       this.packedAtCreationTime = target.packedAtCreationTime;
/*  108 */       this.packedWire = target.packedWire;
/*  109 */       this.variableState = target.variableState;
/*  110 */       this.maxValue = target.maxValue;
/*  111 */       this.currentBitwidth = target.currentBitwidth;
/*  112 */       this.constant = target.constant;
/*      */     } else {
/*  114 */       this.stateChanged = true;
/*  115 */       ConditionalScopeTracker.register(this, this.scope);
/*      */       
/*  117 */       if (this.possibleValStack == null) {
/*  118 */         this.possibleValStack = new Stack<>();
/*      */       }
/*  120 */       if (this.prevValStack == null) {
/*  121 */         this.prevValStack = new Stack<>();
/*      */       }
/*  123 */       int current = ConditionalScopeTracker.getCurrentScopeId();
/*      */       
/*  125 */       int size = this.prevValStack.size();
/*  126 */       while (size < current) {
/*  127 */         this.prevValStack.push(copy());
/*  128 */         this.possibleValStack.push(new HashMap<>());
/*  129 */         size++;
/*      */       } 
/*      */       
/*  132 */       this.modulus = target.modulus;
/*  133 */       this.bitWires = target.bitWires;
/*  134 */       this.splittedAtCreationTime = target.splittedAtCreationTime;
/*  135 */       this.packedAtCreationTime = target.packedAtCreationTime;
/*  136 */       this.packedWire = target.packedWire;
/*  137 */       this.variableState = target.variableState;
/*  138 */       this.maxValue = target.maxValue;
/*  139 */       this.currentBitwidth = target.currentBitwidth;
/*  140 */       this.scope = ConditionalScopeTracker.getCurrentScopeId();
/*  141 */       this.constant = target.constant;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void pop(int id) {
/*  148 */     if (!this.stateChanged) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  153 */     FieldElement copy = copy();
/*  154 */     if (this.generator.__getPhase() == 0)
/*  155 */       copy.variableState.setPackedAhead(true); 
/*  156 */     ((HashMap<Integer, FieldElement>)this.possibleValStack.peek()).put(Integer.valueOf(id), copy);
/*  157 */     this.scope--;
/*  158 */     FieldElement prev = this.prevValStack.peek();
/*  159 */     this.packedWire = prev.packedWire;
/*  160 */     this.variableState = prev.variableState;
/*  161 */     this.bitWires = prev.bitWires;
/*  162 */     this.maxValue = prev.maxValue;
/*      */     
/*  164 */     this.constant = prev.constant;
/*  165 */     this.modulus = prev.modulus;
/*  166 */     this.currentBitwidth = prev.currentBitwidth;
/*  167 */     this.stateChanged = false;
/*      */   }
/*      */ 
/*      */   
/*      */   public void popMain() {
/*  172 */     if (this.generator.__getPhase() == 0) {
/*  173 */       this.variableState = this.generator.__retrieveVariableState();
/*  174 */       this.variableState.setPackedAhead(true);
/*      */       
/*  176 */       HashMap<Integer, FieldElement> possibleVals = this.possibleValStack
/*  177 */         .pop();
/*  178 */       int mulIndex = ((FieldElement)this.prevValStack.pop()).getVariableState().getMulIndex();
/*  179 */       for (Integer idx : possibleVals.keySet()) {
/*  180 */         int m = ((FieldElement)possibleVals.get(idx)).variableState.getMulIndex();
/*  181 */         if (m > mulIndex) {
/*  182 */           mulIndex = m;
/*      */         }
/*      */       } 
/*  185 */       this.variableState.setMulIndex(mulIndex);
/*  186 */       this.bitWires = null;
/*  187 */       this.constant = null;
/*  188 */       this.stateChanged = true;
/*      */     } else {
/*      */       
/*  191 */       int tmp = this.scope;
/*  192 */       if (ConditionalScopeTracker.getCurrentScopeId() > tmp) {
/*  193 */         this.stateChanged = true;
/*      */       }
/*      */       
/*  196 */       this.variableState = null;
/*  197 */       ConditionalScopeTracker.ConditionalStatementData condData = 
/*  198 */         ConditionalScopeTracker.getCurrentConditionalStmtData();
/*  199 */       this.bitWires = null;
/*  200 */       int numberOfValues = condData.getBitList().size();
/*  201 */       ArrayList<Bit> conditionList = condData.getBitList();
/*  202 */       FieldElement[] candidateList = new FieldElement[numberOfValues];
/*      */       
/*  204 */       HashMap<Integer, FieldElement> possibleVals = this.possibleValStack
/*  205 */         .pop();
/*  206 */       for (Integer idx : possibleVals.keySet()) {
/*  207 */         candidateList[idx.intValue()] = possibleVals.get(idx);
/*      */       }
/*  209 */       for (int i = 0; i < numberOfValues; i++) {
/*  210 */         if (candidateList[i] == null) {
/*  211 */           candidateList[i] = copy();
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  218 */       FieldElement initial = candidateList[numberOfValues - 1];
/*  219 */       int startingIndex = -1;
/*  220 */       for (int j = numberOfValues - 2; j >= 0; j--) {
/*  221 */         if ((candidateList[j]).packedWire != initial.packedWire) {
/*  222 */           startingIndex = j;
/*      */           break;
/*      */         } 
/*      */       } 
/*  226 */       if (startingIndex == -1) {
/*      */ 
/*      */         
/*  229 */         this.packedWire = initial.packedWire;
/*  230 */         this.maxValue = initial.maxValue;
/*  231 */         this.bitWires = initial.bitWires;
/*  232 */         this.currentBitwidth = initial.currentBitwidth;
/*      */         
/*  234 */         this.modulus = initial.modulus;
/*  235 */         this.constant = initial.constant;
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  240 */         FieldElement current = initial;
/*  241 */         this.modulus = initial.modulus;
/*  242 */         this.packedWire = initial.packedWire;
/*  243 */         this.maxValue = initial.maxValue;
/*  244 */         this.bitWires = initial.bitWires;
/*  245 */         this.currentBitwidth = initial.currentBitwidth;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  250 */         for (int k = startingIndex; k >= 0; k--) {
/*      */           
/*  252 */           current = candidateList[k];
/*  253 */           Bit selectionBit = conditionList.get(k);
/*  254 */           this.packedWire = this.packedWire.muxBit(current.packedWire, 
/*  255 */               selectionBit.wire);
/*  256 */           this.bitWires = null;
/*  257 */           this.constant = null;
/*  258 */           this.maxValue = (this.maxValue.compareTo(current.maxValue) > 0) ? this.maxValue : 
/*  259 */             current.maxValue;
/*      */           
/*  261 */           this.currentBitwidth = Math.max(this.currentBitwidth, 
/*  262 */               current.currentBitwidth);
/*      */         } 
/*      */       } 
/*  265 */       this.prevValStack.pop();
/*  266 */       init();
/*      */     } 
/*      */   }
/*      */   
/*      */   public FieldElement copy() {
/*  271 */     if (this.generator.__getPhase() == 0) {
/*  272 */       FieldElement e = new FieldElement(this.generator, this.modulus, this.variableState);
/*  273 */       e.constant = this.constant;
/*  274 */       e.packedWire = this.packedWire;
/*  275 */       e.bitWires = this.bitWires;
/*  276 */       e.maxValue = this.maxValue;
/*  277 */       return e;
/*      */     } 
/*  279 */     FieldElement v = new FieldElement();
/*  280 */     v.modulus = this.modulus;
/*  281 */     v.bitWires = this.bitWires;
/*  282 */     v.splittedAtCreationTime = this.splittedAtCreationTime;
/*  283 */     v.packedAtCreationTime = this.packedAtCreationTime;
/*  284 */     v.packedWire = this.packedWire;
/*  285 */     v.variableState = this.variableState;
/*  286 */     v.maxValue = this.maxValue;
/*  287 */     v.currentBitwidth = this.currentBitwidth;
/*  288 */     v.generator = this.generator;
/*  289 */     v.constant = this.constant;
/*  290 */     v.nativeSnarkField = this.nativeSnarkField;
/*  291 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FieldElement(BigInteger modulus, PackedValue packedWire, BigInteger maxValue) {
/*  302 */     this.maxValue = maxValue;
/*  303 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  304 */     this.packedWire = packedWire;
/*  305 */     this.modulus = modulus;
/*      */     
/*  307 */     this.currentBitwidth = (maxValue == null) ? modulus.bitLength() : maxValue
/*  308 */       .bitLength();
/*      */     
/*  310 */     init();
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
/*      */   public FieldElement(BigInteger modulus, PackedValue packedWire) {
/*  334 */     this(modulus, packedWire, (BigInteger)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public FieldElement(BigInteger modulus, BigInteger constant) {
/*  339 */     this(modulus, CircuitGenerator.__getActiveCircuitGenerator().__createConstantPackedValue(
/*  340 */           Util.prepConstant(constant, modulus), modulus));
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
/*      */   private FieldElement(CircuitGenerator generator, BigInteger modulus) {
/*  356 */     this.modulus = modulus;
/*  357 */     this.generator = generator;
/*  358 */     this.maxValue = modulus.subtract(BigInteger.ONE);
/*  359 */     this.currentBitwidth = this.maxValue.bitLength();
/*  360 */     init();
/*      */   }
/*      */ 
/*      */   
/*      */   private FieldElement(CircuitGenerator generator, BigInteger modulus, VariableState st) {
/*  365 */     this.modulus = modulus;
/*  366 */     this.generator = generator;
/*  367 */     this.variableState = st;
/*  368 */     this.maxValue = modulus.subtract(BigInteger.ONE);
/*  369 */     this.currentBitwidth = this.maxValue.bitLength();
/*      */   }
/*      */ 
/*      */   
/*      */   public FieldElement(BigInteger modulus) {
/*  374 */     this.modulus = modulus;
/*  375 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  376 */     this.currentBitwidth = modulus.bitLength();
/*  377 */     init();
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
/*      */   private void init() {
/*  393 */     if (this.modulus.equals(Config.getFiniteFieldModulus()))
/*      */     {
/*  395 */       this.nativeSnarkField = true;
/*      */     }
/*      */     
/*  398 */     checkConstant();
/*  399 */     if (this.variableState == null) {
/*  400 */       this.variableState = this.generator.__retrieveVariableState();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  405 */     if (this.maxValue == null) {
/*  406 */       this.maxValue = this.modulus.subtract(BigInteger.ONE);
/*  407 */       this.currentBitwidth = this.maxValue.bitLength();
/*      */     } 
/*      */     
/*  410 */     if (this.generator.__getPhase() == 1) {
/*      */ 
/*      */       
/*  413 */       if (this.constant != null) {
/*      */         
/*  415 */         this.maxValue = this.constant.mod(this.modulus);
/*  416 */         this.packedWire = this.generator.__createConstantPackedValue(this.maxValue, 
/*  417 */             this.modulus);
/*      */ 
/*      */         
/*  420 */         this.bitWires = this.packedWire.getBits(this.modulus.bitLength(), 
/*  421 */             UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  431 */       if (this.variableState != null) {
/*  432 */         if (this.variableState.isPackedAhead() && this.packedWire == null) {
/*  433 */           WireArray modified = this.bitWires.adjustLength(Math.min(
/*  434 */                 this.bitWires.size(), this.modulus.bitLength()));
/*  435 */           if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_PER_CHUNK || 
/*  436 */             this.nativeSnarkField) {
/*  437 */             this.packedWire = new PackedValue(modified, 
/*  438 */                 this.modulus.bitLength());
/*      */           } else {
/*  440 */             this.packedWire = new PackedValue(modified, 
/*  441 */                 UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */           } 
/*  443 */           this.maxValue = Util.min(this.bitWires
/*  444 */               .computeTightUpperBoundOfBitWires(this.modulus
/*  445 */                 .bitLength()), this.modulus
/*  446 */               .subtract(BigInteger.ONE));
/*  447 */           this.currentBitwidth = this.maxValue.bitLength();
/*      */         }
/*  449 */         else if (this.variableState.isSplittedAhead() && this.bitWires == null) {
/*      */           
/*  451 */           getBackInRange(true);
/*      */           
/*  453 */           if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/*  454 */             this.nativeSnarkField) {
/*  455 */             this.bitWires = this.packedWire.getBits(this.modulus.bitLength(), 
/*  456 */                 this.modulus.bitLength(), new String[0]);
/*      */           } else {
/*  458 */             this.bitWires = this.packedWire.getBits(this.modulus.bitLength(), 
/*  459 */                 UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]);
/*      */           } 
/*  461 */           this.maxValue = Util.min(this.bitWires
/*  462 */               .computeTightUpperBoundOfBitWires(this.modulus
/*  463 */                 .bitLength()), this.modulus
/*  464 */               .subtract(BigInteger.ONE));
/*  465 */           this.currentBitwidth = this.maxValue.bitLength();
/*  466 */           if (!this.nativeSnarkField && (
/*  467 */             this.maxValue.compareTo(this.modulus) >= 0 || 
/*  468 */             !this.packedWire.isAligned()) && this.variableState.isPackedAhead())
/*      */           {
/*  470 */             if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  471 */               this.packedWire = new PackedValue(this.bitWires, 
/*  472 */                   this.modulus.bitLength());
/*      */             } else {
/*  474 */               this.packedWire = new PackedValue(this.bitWires, 
/*  475 */                   UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */             }
/*      */           
/*      */           }
/*  479 */         } else if ((this.variableState.isConditionallySplittedAhead() || this.variableState
/*  480 */           .isConditionallySplittedAndAlignedAhead()) && 
/*  481 */           this.bitWires == null) {
/*  482 */           if (this.maxValue.compareTo(this.modulus) >= 0) {
/*  483 */             if (this.variableState.isMustBeWithinRange()) {
/*  484 */               getBackInRange(true);
/*  485 */               if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/*  486 */                 this.nativeSnarkField) {
/*  487 */                 this.bitWires = this.packedWire.getBits(
/*  488 */                     this.modulus.bitLength(), 
/*  489 */                     this.modulus.bitLength(), new String[0]);
/*      */               } else {
/*  491 */                 this.bitWires = this.packedWire.getBits(
/*  492 */                     this.modulus.bitLength(), 
/*  493 */                     UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]);
/*      */               } 
/*  495 */               this.maxValue = Util.min(this.bitWires
/*  496 */                   .computeTightUpperBoundOfBitWires(this.modulus
/*  497 */                     .bitLength()), this.modulus
/*  498 */                   .subtract(BigInteger.ONE));
/*  499 */               this.currentBitwidth = this.maxValue.bitLength();
/*  500 */               if (!this.nativeSnarkField && (
/*  501 */                 this.maxValue.compareTo(this.modulus) >= 0 || 
/*  502 */                 !this.packedWire.isAligned()) && 
/*  503 */                 this.variableState.isPackedAhead())
/*      */               {
/*  505 */                 if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  506 */                   this.packedWire = new PackedValue(this.bitWires, 
/*  507 */                       this.modulus.bitLength());
/*      */                 } else {
/*  509 */                   this.packedWire = new PackedValue(
/*  510 */                       this.bitWires, 
/*  511 */                       UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */                 } 
/*      */               }
/*      */             } else {
/*      */               
/*  516 */               getBackInRange(false);
/*      */             }
/*      */           
/*  519 */           } else if (!this.nativeSnarkField && 
/*  520 */             this.packedWire != null && 
/*  521 */             !this.packedWire.isAligned() && 
/*  522 */             this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT && 
/*  523 */             this.variableState
/*  524 */             .isConditionallySplittedAndAlignedAhead()) {
/*  525 */             this.packedWire = this.packedWire.align((int)Math.ceil(this.modulus
/*  526 */                   .bitLength() * 
/*  527 */                   1.0D / 
/*  528 */                   UnsignedInteger.BITWIDTH_PER_CHUNK), 
/*  529 */                 UnsignedInteger.BITWIDTH_LIMIT_SHORT);
/*  530 */           } else if (this.packedWire == null && 
/*  531 */             this.variableState
/*  532 */             .isConditionallySplittedAndAlignedAhead()) {
/*  533 */             throw new RuntimeException("Case Unexpected");
/*      */           }
/*      */         
/*      */         }
/*  537 */         else if (this.bitWires != null && 
/*  538 */           this.bitWires.size() > this.modulus.bitLength()) {
/*  539 */           this.bitWires = new WireArray(Arrays.<Wire>copyOfRange(
/*  540 */                 this.bitWires.asArray(), 0, this.modulus.bitLength()));
/*      */           
/*  542 */           if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_PER_CHUNK || 
/*  543 */             this.nativeSnarkField) {
/*  544 */             this.packedWire = new PackedValue(this.bitWires, 
/*  545 */                 this.modulus.bitLength());
/*      */           } else {
/*  547 */             this.packedWire = new PackedValue(this.bitWires, 
/*  548 */                 UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */           } 
/*  550 */           this.maxValue = Util.min(this.bitWires
/*  551 */               .computeTightUpperBoundOfBitWires(this.modulus
/*  552 */                 .bitLength()), this.modulus
/*  553 */               .subtract(BigInteger.ONE));
/*  554 */           this.currentBitwidth = this.maxValue.bitLength();
/*      */         } 
/*      */ 
/*      */         
/*  558 */         if (this.variableState.getThresholdBitwidth() != -1 && 
/*  559 */           this.bitWires == null) {
/*      */           
/*  561 */           this.currentBitwidth = this.maxValue.bitLength();
/*  562 */           if (this.currentBitwidth > this.variableState.getThresholdBitwidth()) {
/*  563 */             if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/*  564 */               this.nativeSnarkField) {
/*  565 */               this.bitWires = this.packedWire.getBits(this.modulus.bitLength(), 
/*  566 */                   this.modulus.bitLength(), new String[0]);
/*      */             } else {
/*  568 */               this.bitWires = this.packedWire.getBits(this.modulus.bitLength(), 
/*  569 */                   UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]);
/*      */             } 
/*  571 */             this.maxValue = Util.min(this.bitWires
/*  572 */                 .computeTightUpperBoundOfBitWires(this.modulus
/*  573 */                   .bitLength()), this.modulus
/*  574 */                 .subtract(BigInteger.ONE));
/*  575 */             this.currentBitwidth = this.maxValue.bitLength();
/*      */             
/*  577 */             if ((this.maxValue.compareTo(this.modulus) >= 0 || 
/*  578 */               !this.packedWire.isAligned()) && this.variableState.isPackedAhead()) {
/*  579 */               if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  580 */                 this.packedWire = new PackedValue(this.bitWires, 
/*  581 */                     this.modulus.bitLength());
/*      */               } else {
/*  583 */                 this.packedWire = new PackedValue(this.bitWires, 
/*  584 */                     UnsignedInteger.BITWIDTH_PER_CHUNK);
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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void checkConstant() {
/*  603 */     if (this.packedWire != null)
/*  604 */       if ((this.constant = this.packedWire
/*  605 */         .getConstant(UnsignedInteger.BITWIDTH_PER_CHUNK)) != null) {
/*      */         
/*  607 */         if (this.constant.signum() == -1)
/*  608 */           throw new IllegalArgumentException(
/*  609 */               "Unisgned Integer is being instantiated from a negative constant");  return;
/*      */       }  
/*  611 */     if (this.bitWires != null) {
/*  612 */       boolean allConstant = true;
/*  613 */       BigInteger v = BigInteger.ZERO;
/*  614 */       int i = 0; byte b; int j; Wire[] arrayOfWire;
/*  615 */       for (j = (arrayOfWire = this.bitWires.asArray()).length, b = 0; b < j; ) { Wire w = arrayOfWire[b];
/*  616 */         if (!(w instanceof ConstantWire)) {
/*  617 */           allConstant = false;
/*      */           break;
/*      */         } 
/*  620 */         ConstantWire constWire = (ConstantWire)w;
/*  621 */         if (!constWire.isBinary()) {
/*  622 */           throw new RuntimeException(
/*  623 */               "Non-binary bit value used to construct an integer");
/*      */         }
/*  625 */         v = v.add(constWire.getConstant().multiply((
/*  626 */               new BigInteger("2")).pow(i++)));
/*      */         
/*      */         b++; }
/*      */ 
/*      */       
/*  631 */       if (allConstant) {
/*  632 */         this.constant = v;
/*      */       }
/*      */     } 
/*      */   }
/*      */   
/*      */   public FieldElement add(BigInteger b) {
/*  638 */     return add(new FieldElement(this.modulus, b));
/*      */   }
/*      */ 
/*      */   
/*      */   public FieldElement mul(BigInteger b) {
/*  643 */     return mul(new FieldElement(this.modulus, b));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public FieldElement add(FieldElement o) {
/*  649 */     if (isConstant() && o.isConstant()) {
/*  650 */       return new FieldElement(this.modulus, getConstant().add(
/*  651 */             o.getConstant()));
/*      */     }
/*  653 */     if (isConstant() && getConstant().equals(BigInteger.ZERO)) {
/*  654 */       CircuitGenerator.__getActiveCircuitGenerator().__retrieveVariableState();
/*  655 */       return o.copy();
/*      */     } 
/*  657 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ZERO)) {
/*  658 */       CircuitGenerator.__getActiveCircuitGenerator().__retrieveVariableState();
/*      */       
/*  660 */       return copy();
/*      */     } 
/*      */     
/*  663 */     if (this.generator.__getPhase() == 0) {
/*  664 */       this.variableState.setPackedAhead(true);
/*  665 */       o.variableState.setPackedAhead(true);
/*  666 */       this.variableState.incAddUseCount();
/*  667 */       o.variableState.incAddUseCount();
/*      */       
/*  669 */       FieldElement fieldElement = new FieldElement(this.generator, this.modulus);
/*  670 */       fieldElement.getVariableState().setMulIndex(
/*  671 */           Math.max(o.getVariableState().getMulIndex(), 
/*  672 */             this.variableState.getMulIndex()));
/*      */       
/*  674 */       return fieldElement;
/*      */     } 
/*  676 */     this.variableState.decAddUseCount();
/*  677 */     o.variableState.decAddUseCount();
/*      */     
/*  679 */     if (!this.nativeSnarkField) {
/*  680 */       boolean overflowCheck = this.packedWire
/*  681 */         .addOverflowCheck(o.packedWire);
/*  682 */       if (overflowCheck) {
/*  683 */         handleOverflow(this, o, false);
/*  684 */         overflowCheck = this.packedWire.addOverflowCheck(o.packedWire);
/*  685 */         if (overflowCheck) {
/*  686 */           handleOverflow(this, o, false);
/*      */         }
/*      */       } 
/*      */     } 
/*  690 */     BigInteger outMaxValue = this.maxValue.add(o.maxValue);
/*  691 */     if (this.nativeSnarkField) {
/*  692 */       outMaxValue = Util.min(outMaxValue, 
/*  693 */           this.modulus.subtract(BigInteger.ONE));
/*      */     }
/*      */     
/*  696 */     PackedValue.disableOverflowChecks = true;
/*  697 */     FieldElement result = new FieldElement(this.modulus, 
/*  698 */         this.packedWire.add(o.packedWire), outMaxValue);
/*  699 */     PackedValue.disableOverflowChecks = false;
/*      */     
/*  701 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void handleOverflow(FieldElement x1, FieldElement x2, boolean isMul) {
/*  708 */     boolean longSetting = false;
/*  709 */     boolean adjusted = false;
/*      */     
/*  711 */     int maxBitWidth = x1.modulus.bitLength();
/*  712 */     if (maxBitWidth > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  713 */       longSetting = true;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  718 */     int b1Max = x1.packedWire.getBitwidthOfLargestChunk();
/*  719 */     int b2Max = x2.packedWire.getBitwidthOfLargestChunk();
/*      */ 
/*      */     
/*  722 */     if (isMul) {
/*      */       
/*  724 */       if (longSetting) {
/*  725 */         if (b1Max + UnsignedInteger.BITWIDTH_PER_CHUNK >= 
/*  726 */           Config.getNumBitsFiniteFieldModulus()) {
/*  727 */           x1.getBackInRange(false);
/*  728 */           adjusted = true;
/*      */         } 
/*  730 */         if (b2Max + UnsignedInteger.BITWIDTH_PER_CHUNK >= 
/*  731 */           Config.getNumBitsFiniteFieldModulus()) {
/*  732 */           x2.getBackInRange(false);
/*  733 */           adjusted = true;
/*      */         } 
/*      */       } else {
/*      */         
/*  737 */         if (b1Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  738 */           x1.getBackInRange(false);
/*  739 */           adjusted = true;
/*      */         } 
/*  741 */         if (b2Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  742 */           x2.getBackInRange(false);
/*  743 */           adjusted = true;
/*      */         } 
/*      */       } 
/*      */     } else {
/*  747 */       if (b1Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  748 */         x1.getBackInRange(false);
/*  749 */         adjusted = true;
/*      */       } 
/*      */       
/*  752 */       if (b2Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  753 */         x2.getBackInRange(false);
/*  754 */         adjusted = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  759 */     if (adjusted) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  764 */     int excesss1 = x1.variableState.getMulUseCount() * (
/*  765 */       x1.currentBitwidth - maxBitWidth);
/*  766 */     int excesss2 = x2.variableState.getMulUseCount() * (
/*  767 */       x2.currentBitwidth - maxBitWidth);
/*      */     
/*  769 */     if (excesss1 > excesss2) {
/*  770 */       x1.getBackInRange(false);
/*  771 */     } else if (excesss2 < excesss1) {
/*  772 */       x2.getBackInRange(false);
/*      */     } else {
/*  774 */       excesss1 = x1.variableState.getAddUseCount() * (
/*  775 */         x1.currentBitwidth - maxBitWidth);
/*  776 */       excesss2 = x2.variableState.getAddUseCount() * (
/*  777 */         x2.currentBitwidth - maxBitWidth);
/*  778 */       if (excesss1 > excesss2) {
/*  779 */         x1.getBackInRange(false);
/*  780 */       } else if (excesss2 < excesss1) {
/*  781 */         x2.getBackInRange(false);
/*      */       }
/*  783 */       else if (x1.currentBitwidth - maxBitWidth > 0) {
/*  784 */         x1.getBackInRange(false);
/*      */       } else {
/*  786 */         x2.getBackInRange(false);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private VariableState getVariableState() {
/*  794 */     return this.variableState;
/*      */   }
/*      */ 
/*      */   
/*      */   public FieldElement mul(FieldElement o) {
/*  799 */     if (isConstant() && o.isConstant()) {
/*  800 */       return new FieldElement(this.modulus, getConstant().multiply(
/*  801 */             o.getConstant()));
/*      */     }
/*  803 */     if (isConstant() && getConstant().equals(BigInteger.ONE)) {
/*  804 */       CircuitGenerator.__getActiveCircuitGenerator().__retrieveVariableState();
/*      */       
/*  806 */       return o.copy();
/*      */     } 
/*  808 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ONE)) {
/*  809 */       CircuitGenerator.__getActiveCircuitGenerator().__retrieveVariableState();
/*      */       
/*  811 */       return copy();
/*      */     } 
/*      */     
/*  814 */     if (isConstant() && getConstant().equals(BigInteger.ZERO)) {
/*  815 */       return new FieldElement(this.modulus, BigInteger.ZERO);
/*      */     }
/*      */     
/*  818 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ZERO)) {
/*  819 */       return new FieldElement(this.modulus, BigInteger.ZERO);
/*      */     }
/*      */     
/*  822 */     if (this.generator.__getPhase() == 0) {
/*  823 */       this.variableState.setPackedAhead(true);
/*  824 */       o.variableState.setPackedAhead(true);
/*  825 */       this.variableState.incMulUseCount();
/*  826 */       o.variableState.incMulUseCount();
/*      */       
/*  828 */       int idx1 = getVariableState().getMulIndex();
/*  829 */       int idx2 = o.getVariableState().getMulIndex();
/*      */       
/*  831 */       if (idx1 == 1 && 
/*  832 */         this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  833 */         this.variableState.setConditionallySplittedAhead(true);
/*      */       }
/*  835 */       if (idx2 == 1 && 
/*  836 */         o.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  837 */         o.variableState.setConditionallySplittedAhead(true);
/*      */       }
/*      */       
/*  840 */       FieldElement fieldElement = new FieldElement(this.generator, this.modulus);
/*  841 */       fieldElement.getVariableState().incMulIndex();
/*      */       
/*  843 */       return fieldElement;
/*      */     } 
/*      */     
/*  846 */     BigInteger outMaxValue = this.maxValue.multiply(o.maxValue);
/*  847 */     this.variableState.decMulUseCount();
/*  848 */     o.variableState.decMulUseCount();
/*      */     
/*  850 */     if (!this.nativeSnarkField) {
/*  851 */       boolean overflowCheck = this.packedWire
/*  852 */         .mulOverflowCheck(o.packedWire);
/*  853 */       if (overflowCheck) {
/*  854 */         handleOverflow(this, o, true);
/*  855 */         overflowCheck = this.packedWire.mulOverflowCheck(o.packedWire);
/*  856 */         if (overflowCheck) {
/*  857 */           handleOverflow(this, o, true);
/*      */         }
/*      */       } 
/*      */     } 
/*  861 */     outMaxValue = this.maxValue.multiply(o.maxValue);
/*  862 */     if (this.nativeSnarkField) {
/*  863 */       outMaxValue = Util.min(outMaxValue, 
/*  864 */           this.modulus.subtract(BigInteger.ONE));
/*      */     }
/*      */     
/*  867 */     PackedValue.disableOverflowChecks = true;
/*  868 */     FieldElement result = new FieldElement(this.modulus, 
/*  869 */         this.packedWire.mul(o.packedWire), outMaxValue);
/*  870 */     PackedValue.disableOverflowChecks = false;
/*      */     
/*  872 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public FieldElement subtract(BigInteger b) {
/*  877 */     return subtract(new FieldElement(this.modulus, b));
/*      */   }
/*      */ 
/*      */   
/*      */   public FieldElement subtract(FieldElement o) {
/*  882 */     if (isConstant() && o.isConstant()) {
/*  883 */       return new FieldElement(this.modulus, getConstant().subtract(
/*  884 */             o.getConstant()));
/*      */     }
/*  886 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ZERO)) {
/*  887 */       CircuitGenerator.__getActiveCircuitGenerator().__retrieveVariableState();
/*      */       
/*  889 */       return copy();
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  895 */     if (this.generator.__getPhase() == 0) {
/*  896 */       this.variableState.setPackedAhead(true);
/*  897 */       o.variableState.setPackedAhead(true);
/*      */       
/*  899 */       this.variableState.incAddUseCount();
/*  900 */       o.variableState.incAddUseCount();
/*      */       
/*  902 */       FieldElement fieldElement = new FieldElement(this.generator, this.modulus);
/*  903 */       fieldElement.getVariableState().setMulIndex(
/*  904 */           Math.max(o.getVariableState().getMulIndex(), 
/*  905 */             this.variableState.getMulIndex()));
/*  906 */       return fieldElement;
/*      */     } 
/*      */     
/*  909 */     this.variableState.decAddUseCount();
/*  910 */     o.variableState.decAddUseCount();
/*  911 */     if (this.nativeSnarkField) {
/*  912 */       return new FieldElement(this.modulus, new PackedValue(
/*  913 */             this.packedWire.array[0].sub(o.packedWire.array[0], new String[0]), 
/*  914 */             this.modulus.subtract(BigInteger.ONE)), 
/*  915 */           this.modulus.subtract(BigInteger.ONE));
/*      */     }
/*      */     
/*  918 */     BigInteger[] aux = SubtractionAUX.prepSub(o.packedWire, this.modulus, 
/*  919 */         this.generator, this.modulus.bitLength());
/*  920 */     boolean overflowCheck = this.packedWire.addSubOverflowCheck(aux);
/*  921 */     if (overflowCheck) {
/*      */       
/*  923 */       handleOverflow(this, o, false);
/*      */       
/*  925 */       aux = SubtractionAUX.prepSub(o.packedWire, this.modulus, this.generator, 
/*  926 */           this.modulus.bitLength());
/*  927 */       overflowCheck = this.packedWire.addSubOverflowCheck(aux);
/*  928 */       if (overflowCheck) {
/*  929 */         handleOverflow(this, o, false);
/*  930 */         aux = SubtractionAUX.prepSub(o.packedWire, this.modulus, 
/*  931 */             this.generator, this.modulus.bitLength());
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  936 */     BigInteger outMaxValue = this.maxValue.add(Util.group(aux, 
/*  937 */           UnsignedInteger.BITWIDTH_PER_CHUNK));
/*  938 */     BigInteger[] a = SubtractionAUX.prepSub(o.packedWire, this.modulus, 
/*  939 */         this.generator, this.modulus.bitLength());
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  947 */     FieldElement result = new FieldElement(this.modulus, this.packedWire.addsub(
/*  948 */           a, o.packedWire), outMaxValue);
/*  949 */     return result;
/*      */   }
/*      */ 
/*      */   
/*      */   public FieldElement negate() {
/*  954 */     if (isConstant()) {
/*  955 */       return new FieldElement(this.modulus, this.modulus.subtract(
/*  956 */             getConstant()));
/*      */     }
/*  958 */     return (new FieldElement(this.modulus, BigInteger.ZERO)).subtract(this);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public FieldElement div(FieldElement b) {
/*  964 */     if (isConstant() && b.isConstant()) {
/*  965 */       BigInteger bigInteger = b.getConstant().modInverse(this.modulus)
/*  966 */         .multiply(getConstant()).mod(this.modulus);
/*      */       
/*  968 */       return new FieldElement(this.modulus, bigInteger);
/*      */     } 
/*      */     
/*  971 */     if (this.generator.__getPhase() == 0) {
/*      */       
/*  973 */       this.variableState.setPackedAhead(true);
/*  974 */       int idx1 = getVariableState().getMulIndex();
/*  975 */       this.variableState.setConditionallySplittedAhead(true);
/*  976 */       this.variableState.setMustBeWithinRange(true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  986 */       if (Config.enforceInternalDivisionNonZeroChecks) {
/*  987 */         b.variableState.setConditionallySplittedAndAlignedAhead(true);
/*  988 */         b.variableState.setMustBeWithinRange(true);
/*      */       } 
/*  990 */       b.variableState.setPackedAhead(true);
/*  991 */       int idx2 = b.getVariableState().getMulIndex();
/*      */       
/*  993 */       if (idx2 == 1 && 
/*  994 */         b.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  995 */         b.variableState.setConditionallySplittedAhead(true);
/*      */       }
/*      */       
/*  998 */       FieldElement fieldElement = new FieldElement(this.generator, this.modulus);
/*  999 */       return fieldElement;
/*      */     } 
/* 1001 */     if (Config.enforceInternalDivisionNonZeroChecks) {
/* 1002 */       b.packedWire.forceNonZero();
/*      */     }
/* 1004 */     if (this.nativeSnarkField) {
/* 1005 */       PackedValue.disableOverflowChecks = true;
/* 1006 */       FieldDivisionGadget f = new FieldDivisionGadget(
/* 1007 */           this.packedWire.array[0], b.packedWire.array[0], new String[0]);
/* 1008 */       PackedValue.disableOverflowChecks = false;
/* 1009 */       return new FieldElement(this.modulus, 
/* 1010 */           new PackedValue(f.getOutputWires()[0], this.modulus
/* 1011 */             .subtract(BigInteger.ONE)), 
/* 1012 */           this.modulus.subtract(BigInteger.ONE));
/*      */     } 
/*      */     
/* 1015 */     if (this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*      */       
/* 1017 */       BigInteger bigInteger = Util.computeMaxValue(b.modulus.bitLength());
/* 1018 */       BigInteger[] arrayOfBigInteger = Util.split(bigInteger, 
/* 1019 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1020 */       boolean bool = b.packedWire
/* 1021 */         .mulAddOverflowCheck(new PackedValue(arrayOfBigInteger), this.packedWire);
/* 1022 */       if (bool) {
/* 1023 */         b.getBackInRange(false);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/* 1028 */       PackedValue packedValue = (new CustomLongFieldDivGadget(this.packedWire, 
/* 1029 */           b.packedWire, new PackedValue(Util.split(this.modulus, 
/* 1030 */               UnsignedInteger.BITWIDTH_PER_CHUNK)), new String[0]))
/* 1031 */         .getResult();
/*      */ 
/*      */       
/* 1034 */       FieldElement fieldElement = new FieldElement(this.modulus, packedValue, 
/* 1035 */           Util.computeMaxValue(this.modulus.bitLength()));
/*      */       
/* 1037 */       return fieldElement;
/*      */     } 
/* 1039 */     BigInteger aux = Util.computeMaxValue(this.modulus.bitLength());
/* 1040 */     BigInteger[] auxChunks = { aux };
/* 1041 */     boolean overflowCheck = b.packedWire
/* 1042 */       .mulAddOverflowCheck(new PackedValue(auxChunks), this.packedWire);
/* 1043 */     if (overflowCheck) {
/* 1044 */       b.getBackInRange(false);
/*      */     }
/* 1046 */     PackedValue v = (new CustomShortFieldDivGadget(this.packedWire, 
/* 1047 */         b.packedWire, new PackedValue(
/* 1048 */           new BigInteger[] { this.modulus }, ), new String[0])).getResult();
/*      */ 
/*      */     
/* 1051 */     FieldElement result = new FieldElement(this.modulus, v, 
/* 1052 */         Util.computeMaxValue(this.modulus.bitLength()));
/* 1053 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FieldElement inv() {
/* 1060 */     if (isConstant())
/*      */     {
/* 1062 */       return new FieldElement(this.modulus, getConstant().modInverse(
/* 1063 */             this.modulus));
/*      */     }
/*      */     
/* 1066 */     if (this.generator.__getPhase() == 0) {
/*      */       
/* 1068 */       this.variableState.setPackedAhead(true);
/* 1069 */       int idx1 = getVariableState().getMulIndex();
/*      */       
/* 1071 */       if (idx1 == 1 && 
/* 1072 */         this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/* 1073 */         this.variableState.setConditionallySplittedAhead(true);
/*      */       }
/*      */       
/* 1076 */       FieldElement fieldElement = new FieldElement(this.generator, this.modulus);
/*      */ 
/*      */       
/* 1079 */       return fieldElement;
/*      */     } 
/* 1081 */     if (this.nativeSnarkField) {
/* 1082 */       PackedValue.disableOverflowChecks = true;
/* 1083 */       FieldDivisionGadget f = new FieldDivisionGadget(
/* 1084 */           this.generator.__getOneWire(), this.packedWire.array[0], new String[0]);
/* 1085 */       PackedValue.disableOverflowChecks = false;
/*      */       
/* 1087 */       return new FieldElement(this.modulus, 
/* 1088 */           new PackedValue(f.getOutputWires()[0], this.modulus
/* 1089 */             .subtract(BigInteger.ONE)), 
/* 1090 */           this.modulus.subtract(BigInteger.ONE));
/*      */     } 
/*      */     
/* 1093 */     if (this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*      */       
/* 1095 */       BigInteger bigInteger = Util.computeMaxValue(this.modulus.bitLength());
/* 1096 */       BigInteger[] arrayOfBigInteger = Util.split(bigInteger, 
/* 1097 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1098 */       boolean bool = this.packedWire
/* 1099 */         .mulOverflowCheck(new PackedValue(arrayOfBigInteger));
/* 1100 */       if (bool) {
/* 1101 */         getBackInRange(false);
/*      */       }
/* 1103 */       PackedValue packedValue = (new InverseLongIntegerModGadget(this.packedWire, 
/* 1104 */           new PackedValue(Util.split(this.modulus, 
/* 1105 */               UnsignedInteger.BITWIDTH_PER_CHUNK)), new String[0]))
/* 1106 */         .getInverse();
/*      */ 
/*      */       
/* 1109 */       FieldElement fieldElement = new FieldElement(this.modulus, packedValue, 
/* 1110 */           Util.computeMaxValue(this.modulus.bitLength()));
/* 1111 */       return fieldElement;
/*      */     } 
/* 1113 */     BigInteger aux = Util.computeMaxValue(this.modulus.bitLength());
/* 1114 */     BigInteger[] auxChunks = { aux };
/* 1115 */     boolean overflowCheck = this.packedWire
/* 1116 */       .mulOverflowCheck(new PackedValue(auxChunks));
/* 1117 */     if (overflowCheck) {
/* 1118 */       getBackInRange(false);
/*      */     }
/* 1120 */     PackedValue v = (new ShortIntegerModGadget(this.packedWire, 
/* 1121 */         new PackedValue(new BigInteger[] { this.modulus
/* 1122 */           }, ), new String[0])).getInverse();
/*      */ 
/*      */     
/* 1125 */     FieldElement result = new FieldElement(this.modulus, v, 
/* 1126 */         Util.computeMaxValue(this.modulus.bitLength()));
/* 1127 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public WireArray getBitWires() {
/* 1133 */     if (this.generator.__getPhase() == 0) {
/* 1134 */       this.variableState.setSplittedAhead(true);
/* 1135 */       Wire[] result = new Wire[this.modulus.bitLength()];
/* 1136 */       Arrays.fill((Object[])result, new BitWire(-1));
/* 1137 */       return new WireArray(result);
/*      */     } 
/* 1139 */     return this.bitWires.adjustLength(this.modulus.bitLength());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Bit[] getBitElements() {
/* 1147 */     if (this.generator.__getPhase() == 0) {
/* 1148 */       this.variableState.setSplittedAhead(true);
/* 1149 */       Bit[] arrayOfBit = new Bit[this.modulus.bitLength()];
/* 1150 */       if (this.constant == null) {
/* 1151 */         Arrays.fill((Object[])arrayOfBit, new Bit(new Wire(-1)));
/*      */       } else {
/* 1153 */         for (int j = 0; j < this.modulus.bitLength(); j++) {
/* 1154 */           boolean b = this.constant.testBit(j);
/* 1155 */           Wire w = b ? this.generator.__getOneWire() : this.generator
/* 1156 */             .__getZeroWire();
/* 1157 */           arrayOfBit[j] = new Bit(w);
/*      */         } 
/*      */       } 
/* 1160 */       return arrayOfBit;
/*      */     } 
/* 1162 */     Bit[] result = new Bit[this.modulus.bitLength()];
/* 1163 */     WireArray array = this.bitWires.adjustLength(this.modulus.bitLength());
/* 1164 */     for (int i = 0; i < this.modulus.bitLength(); i++) {
/* 1165 */       result[i] = new Bit(array.get(i));
/*      */     }
/*      */ 
/*      */     
/* 1169 */     return result;
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
/*      */   public boolean isProbablyOverflowed() {
/* 1182 */     return (this.maxValue.compareTo(this.modulus) >= 0);
/*      */   }
/*      */ 
/*      */   
/*      */   private void getBackInRange(boolean strict) {
/* 1187 */     if (this.modulus != null && !this.nativeSnarkField) {
/*      */       
/* 1189 */       if (this.maxValue.compareTo(this.modulus) >= 0 && this.maxValue.compareTo(Util.computeMaxValue(this.modulus.bitLength())) <= 0 && this.packedWire.isWitness()) {
/* 1190 */         PackedValue modValue = new PackedValue(Util.split(this.modulus, 
/* 1191 */               UnsignedInteger.BITWIDTH_PER_CHUNK));
/* 1192 */         this.generator.__addOneAssertion(this.packedWire.isLessThan(modValue, this.maxValue.bitLength()).getWire(), new String[0]);
/*      */       }
/* 1194 */       else if (this.maxValue.compareTo(this.modulus) >= 0 && 
/* 1195 */         this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/* 1196 */         ModConstantGadget modConstantGadget = new ModConstantGadget(this.packedWire.array[0], 
/* 1197 */             this.maxValue.bitLength(), this.modulus, strict, new String[0]);
/*      */         
/* 1199 */         this.packedWire = new PackedValue(modConstantGadget.getOutputWires()[0], 
/* 1200 */             this.maxValue);
/*      */       
/*      */       }
/* 1203 */       else if (this.maxValue.compareTo(this.modulus) >= 0 && 
/* 1204 */         this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*      */         
/* 1206 */         PackedValue modValue = new PackedValue(Util.split(this.modulus, 
/* 1207 */               UnsignedInteger.BITWIDTH_PER_CHUNK));
/* 1208 */         LongIntegerModConstantGadget g = new LongIntegerModConstantGadget(
/* 1209 */             this.packedWire, modValue, strict, new String[0]);
/* 1210 */         this.packedWire = g.getRemainder();
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1217 */       if (strict) {
/* 1218 */         this.maxValue = this.modulus.subtract(BigInteger.ONE);
/* 1219 */         this.currentBitwidth = this.maxValue.bitLength();
/*      */       } else {
/* 1221 */         this.maxValue = Util.computeMaxValue(this.modulus.bitLength());
/* 1222 */         this.currentBitwidth = this.maxValue.bitLength();
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
/*      */   public boolean isConstant() {
/* 1244 */     return (this.constant != null);
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
/*      */   public PackedValue getPackedWire() {
/* 1256 */     if (this.packedWire == null && this.generator.__getPhase() == 0) {
/* 1257 */       this.variableState.setPackedAhead(true);
/* 1258 */       return new PackedValue(new Wire(-1), this.modulus.bitLength());
/*      */     } 
/* 1260 */     return this.packedWire;
/*      */   }
/*      */   
/*      */   public BigInteger getMaxValue() {
/* 1264 */     return this.maxValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getRequiredBitWidth() {
/* 1269 */     return this.modulus.bitLength();
/*      */   }
/*      */   
/*      */   public BigInteger getModulus() {
/* 1273 */     return this.modulus;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getCurrentBitWidth() {
/* 1278 */     return this.currentBitwidth;
/*      */   }
/*      */ 
/*      */   
/*      */   public Wire[] toWires() {
/* 1283 */     if (this.packedWire != null) {
/* 1284 */       return this.packedWire.getArray();
/*      */     }
/* 1286 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void mapValue(BigInteger value, CircuitEvaluator evaluator) {
/* 1292 */     if (this.packedWire != null) {
/*      */       
/* 1294 */       if (this.nativeSnarkField) {
/* 1295 */         evaluator.setWireValue(this.packedWire.array[0], value);
/*      */       } else {
/* 1297 */         evaluator.setWireValue(this.packedWire, value, this.modulus.bitLength(), 
/* 1298 */             UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */       } 
/*      */     } else {
/* 1301 */       int length = this.bitWires.size();
/* 1302 */       for (int i = 0; i < length; i++) {
/* 1303 */         evaluator.setWireValue(this.bitWires.get(i), 
/* 1304 */             value.testBit(i) ? BigInteger.ONE : BigInteger.ZERO);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public BigInteger getValueFromEvaluator(CircuitEvaluator evaluator) {
/* 1311 */     if (this.packedWire != null) {
/* 1312 */       return evaluator.getWireValue(this.packedWire, 
/* 1313 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */     }
/* 1315 */     BigInteger s = BigInteger.ZERO;
/* 1316 */     BigInteger powerTwo = BigInteger.ONE;
/* 1317 */     int length = this.bitWires.size();
/* 1318 */     for (int i = 0; i < length; i++) {
/* 1319 */       s = s.add(powerTwo.multiply(evaluator.getWireValue(this.bitWires
/* 1320 */               .get(i))));
/* 1321 */       powerTwo = powerTwo.add(powerTwo);
/*      */     } 
/* 1323 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static FieldElement createInput(CircuitGenerator generator, BigInteger modulus, String... desc) {
/*      */     PackedValue v;
/* 1330 */     if (modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/* 1331 */       modulus.equals(Config.getFiniteFieldModulus())) {
/* 1332 */       Wire w = generator.__createInputWire(desc);
/* 1333 */       v = new PackedValue(w, modulus.bitLength());
/*      */     } else {
/* 1335 */       int numChunks = (int)Math.ceil(modulus.bitLength() * 1.0D / 
/* 1336 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1337 */       Wire[] w = generator.__createInputWireArray(numChunks, new String[0]);
/*      */       
/* 1339 */       int[] bitwidths = new int[numChunks];
/* 1340 */       Arrays.fill(bitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1341 */       if (numChunks * UnsignedInteger.BITWIDTH_PER_CHUNK != modulus
/* 1342 */         .bitLength()) {
/* 1343 */         bitwidths[numChunks - 1] = modulus.bitLength() % 
/* 1344 */           UnsignedInteger.BITWIDTH_PER_CHUNK;
/*      */       }
/* 1346 */       v = new PackedValue(w, bitwidths);
/*      */     } 
/*      */ 
/*      */     
/* 1350 */     FieldElement o = new FieldElement(modulus, v, 
/* 1351 */         modulus.subtract(BigInteger.ONE));
/* 1352 */     generator.__getInputAux().add(o.copy());
/* 1353 */     return o;
/*      */   }
/*      */ 
/*      */   
/*      */   public static FieldElement createWitness(CircuitGenerator generator, BigInteger modulus, String... desc) {
/*      */     PackedValue v;
/* 1359 */     if (modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/* 1360 */       modulus.equals(Config.getFiniteFieldModulus())) {
/* 1361 */       Wire w = generator.__createProverWitnessWire(desc);
/* 1362 */       v = new PackedValue(w, modulus.bitLength());
/*      */     } else {
/* 1364 */       int numChunks = (int)Math.ceil(modulus.bitLength() * 1.0D / 
/* 1365 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1366 */       Wire[] w = generator.__createProverWitnessWireArray(numChunks, new String[0]);
/* 1367 */       int[] bitwidths = new int[numChunks];
/* 1368 */       Arrays.fill(bitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1369 */       if (numChunks * UnsignedInteger.BITWIDTH_PER_CHUNK != modulus
/* 1370 */         .bitLength()) {
/* 1371 */         bitwidths[numChunks - 1] = modulus.bitLength() % 
/* 1372 */           UnsignedInteger.BITWIDTH_PER_CHUNK;
/*      */       }
/* 1374 */       v = new PackedValue(w, bitwidths);
/*      */     } 
/* 1376 */     FieldElement o = new FieldElement(modulus, v, 
/* 1377 */         modulus.subtract(BigInteger.ONE));
/* 1378 */     generator.__getProverAux().add(o.copy());
/* 1379 */     return o;
/*      */   }
/*      */ 
/*      */   
/*      */   public static FieldElement createVerifiedWitness(CircuitGenerator generator, BigInteger modulus, String... desc) {
/*      */     PackedValue v;
/* 1385 */     if (modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/* 1386 */       modulus.equals(Config.getFiniteFieldModulus())) {
/* 1387 */       Wire w = generator.__createProverWitnessWire(desc);
/* 1388 */       v = new PackedValue(w, modulus.bitLength());
/*      */     } else {
/* 1390 */       int numChunks = (int)Math.ceil(modulus.bitLength() * 1.0D / 
/* 1391 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1392 */       Wire[] w = generator.__createProverWitnessWireArray(numChunks, new String[0]);
/* 1393 */       int[] bitwidths = new int[numChunks];
/* 1394 */       Arrays.fill(bitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1395 */       if (numChunks * UnsignedInteger.BITWIDTH_PER_CHUNK != modulus
/* 1396 */         .bitLength()) {
/* 1397 */         bitwidths[numChunks - 1] = modulus.bitLength() % 
/* 1398 */           UnsignedInteger.BITWIDTH_PER_CHUNK;
/*      */       }
/* 1400 */       v = new PackedValue(w, bitwidths);
/*      */     } 
/* 1402 */     FieldElement o = new FieldElement(modulus, v, 
/* 1403 */         modulus.subtract(BigInteger.ONE));
/* 1404 */     generator.__getVerifiedProverAux().add(o.copy());
/* 1405 */     return o;
/*      */   }
/*      */   
/*      */   public void verifyRange() {
/* 1409 */     if (this.modulus.equals(Config.getFiniteFieldModulus())) {
/*      */       return;
/*      */     }
/*      */     
/* 1413 */     if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/* 1414 */       this.packedWire.array[0].restrictBitLength(this.modulus.bitLength(), new String[0]);
/* 1415 */       this.generator.__addOneAssertion(this.packedWire.array[0].isLessThan(
/* 1416 */             this.modulus, this.modulus.bitLength() + 1, new String[0]), new String[0]);
/*      */     } else {
/*      */       
/* 1419 */       int numChunks = (int)Math.ceil(this.modulus.bitLength() * 1.0D / 
/* 1420 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1421 */       int[] bitwidths = new int[numChunks];
/* 1422 */       Arrays.fill(bitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1423 */       if (numChunks * UnsignedInteger.BITWIDTH_PER_CHUNK != this.modulus
/* 1424 */         .bitLength()) {
/* 1425 */         bitwidths[numChunks - 1] = this.modulus.bitLength() % 
/* 1426 */           UnsignedInteger.BITWIDTH_PER_CHUNK;
/*      */       }
/* 1428 */       for (int i = 0; i < numChunks; i++) {
/* 1429 */         this.packedWire.array[i]
/* 1430 */           .restrictBitLength(bitwidths[numChunks - 1], new String[0]);
/*      */       }
/* 1432 */       this.generator.__addOneAssertion((this.packedWire.isLessThan(
/* 1433 */             new PackedValue(Util.split(this.modulus, 
/* 1434 */                 UnsignedInteger.BITWIDTH_PER_CHUNK)), this.modulus
/* 1435 */             .bitLength() + 1)).wire, new String[0]);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static FieldElement[] createZeroArray(CircuitGenerator generator, int size, BigInteger modulus, String... desc) {
/* 1442 */     FieldElement[] out = new FieldElement[size];
/* 1443 */     for (int i = 0; i < size; i++) {
/* 1444 */       out[i] = new FieldElement(modulus, BigInteger.ZERO);
/*      */     }
/* 1446 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createZeroArray(CircuitGenerator generator, int[] dims, BigInteger modulus, String... desc) {
/* 1453 */     if (dims.length == 1)
/* 1454 */       return createZeroArray(generator, dims[0], modulus, desc); 
/* 1455 */     if (dims.length == 2) {
/* 1456 */       FieldElement[][] out = new FieldElement[dims[0]][];
/* 1457 */       for (int i = 0; i < dims[0]; i++) {
/* 1458 */         out[i] = createZeroArray(generator, dims[1], modulus, desc);
/*      */       }
/* 1460 */       return out;
/* 1461 */     }  if (dims.length == 3) {
/* 1462 */       FieldElement[][][] out = new FieldElement[dims[0]][dims[1]][];
/* 1463 */       for (int i = 0; i < dims[0]; i++) {
/* 1464 */         for (int j = 0; j < dims[1]; j++) {
/* 1465 */           out[i][j] = createZeroArray(generator, dims[2], modulus, 
/* 1466 */               desc);
/*      */         }
/*      */       } 
/* 1469 */       return out;
/*      */     } 
/* 1471 */     throw new IllegalArgumentException(
/* 1472 */         "Initialization of higher dim arrays not supported at this point");
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
/*      */   public static FieldElement[] createInputArray(CircuitGenerator generator, int size, BigInteger modulus, String... desc) {
/* 1487 */     FieldElement[] out = new FieldElement[size];
/* 1488 */     for (int i = 0; i < size; i++) {
/* 1489 */       out[i] = createInput(generator, modulus, desc);
/*      */     }
/* 1491 */     return out;
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
/*      */   public static FieldElement[] createWitnessArray(CircuitGenerator generator, int size, BigInteger modulus, String... desc) {
/* 1506 */     FieldElement[] out = new FieldElement[size];
/* 1507 */     for (int i = 0; i < size; i++) {
/* 1508 */       out[i] = createWitness(generator, modulus, desc);
/*      */     }
/* 1510 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static FieldElement[] createVerifiedWitnessArray(CircuitGenerator generator, int size, BigInteger modulus, String... desc) {
/* 1516 */     FieldElement[] out = new FieldElement[size];
/* 1517 */     for (int i = 0; i < size; i++) {
/* 1518 */       out[i] = createVerifiedWitness(generator, modulus, desc);
/*      */     }
/* 1520 */     return out;
/*      */   }
/*      */ 
/*      */   
/*      */   public void makeOutput(String... desc) {
/* 1525 */     if (this.generator.__getPhase() == 0) {
/* 1526 */       this.variableState.setPackedAhead(true);
/* 1527 */       this.variableState.setConditionallySplittedAhead(true);
/* 1528 */       this.variableState.setMustBeWithinRange(true);
/* 1529 */       this.variableState.setConditionallySplittedAndAlignedAhead(true);
/*      */     
/*      */     }
/*      */     else {
/*      */       
/* 1534 */       this.generator.__makeOutputArray(this.packedWire.array, desc);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void makeOutput(CircuitGenerator generator, FieldElement x, String... desc) {
/* 1541 */     x.makeOutput(new String[0]);
/*      */   } public static void makeOutput(CircuitGenerator generator, FieldElement[] a, String... desc) {
/*      */     byte b;
/*      */     int i;
/*      */     FieldElement[] arrayOfFieldElement;
/* 1546 */     for (i = (arrayOfFieldElement = a).length, b = 0; b < i; ) { FieldElement x = arrayOfFieldElement[b];
/* 1547 */       x.makeOutput(new String[0]);
/*      */       b++; }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createInputArray(CircuitGenerator generator, int[] dims, BigInteger modulus, String... desc) {
/* 1555 */     if (dims.length == 1)
/* 1556 */       return createInputArray(generator, dims[0], modulus, desc); 
/* 1557 */     if (dims.length == 2) {
/* 1558 */       FieldElement[][] out = new FieldElement[dims[0]][];
/* 1559 */       for (int i = 0; i < dims[0]; i++) {
/* 1560 */         out[i] = createInputArray(generator, dims[1], modulus, desc);
/*      */       }
/* 1562 */       return out;
/* 1563 */     }  if (dims.length == 3) {
/* 1564 */       FieldElement[][][] out = new FieldElement[dims[0]][dims[1]][];
/* 1565 */       for (int i = 0; i < dims[0]; i++) {
/* 1566 */         for (int j = 0; j < dims[1]; j++) {
/* 1567 */           out[i][j] = createInputArray(generator, dims[2], modulus, 
/* 1568 */               desc);
/*      */         }
/*      */       } 
/* 1571 */       return out;
/*      */     } 
/* 1573 */     throw new IllegalArgumentException(
/* 1574 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createWitnessArray(CircuitGenerator generator, int[] dims, BigInteger modulus, String... desc) {
/* 1581 */     if (dims.length == 1)
/* 1582 */       return createWitnessArray(generator, dims[0], modulus, desc); 
/* 1583 */     if (dims.length == 2) {
/* 1584 */       FieldElement[][] out = new FieldElement[dims[0]][];
/* 1585 */       for (int i = 0; i < dims[0]; i++) {
/* 1586 */         out[i] = createWitnessArray(generator, dims[1], modulus, desc);
/*      */       }
/* 1588 */       return out;
/* 1589 */     }  if (dims.length == 3) {
/* 1590 */       FieldElement[][][] out = new FieldElement[dims[0]][dims[1]][];
/* 1591 */       for (int i = 0; i < dims[0]; i++) {
/* 1592 */         for (int j = 0; j < dims[1]; j++) {
/* 1593 */           out[i][j] = createWitnessArray(generator, dims[2], modulus, 
/* 1594 */               desc);
/*      */         }
/*      */       } 
/* 1597 */       return out;
/*      */     } 
/* 1599 */     throw new IllegalArgumentException(
/* 1600 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createVerifiedWitnessArray(CircuitGenerator generator, int[] dims, BigInteger modulus, String... desc) {
/* 1608 */     if (dims.length == 1)
/* 1609 */       return createVerifiedWitnessArray(generator, dims[0], modulus, desc); 
/* 1610 */     if (dims.length == 2) {
/* 1611 */       FieldElement[][] out = new FieldElement[dims[0]][];
/* 1612 */       for (int i = 0; i < dims[0]; i++) {
/* 1613 */         out[i] = createVerifiedWitnessArray(generator, dims[1], modulus, desc);
/*      */       }
/* 1615 */       return out;
/* 1616 */     }  if (dims.length == 3) {
/* 1617 */       FieldElement[][][] out = new FieldElement[dims[0]][dims[1]][];
/* 1618 */       for (int i = 0; i < dims[0]; i++) {
/* 1619 */         for (int j = 0; j < dims[1]; j++) {
/* 1620 */           out[i][j] = createVerifiedWitnessArray(generator, dims[2], modulus, 
/* 1621 */               desc);
/*      */         }
/*      */       } 
/* 1624 */       return out;
/*      */     } 
/* 1626 */     throw new IllegalArgumentException(
/* 1627 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void makeOutput(CircuitGenerator generator, Object a, String... desc) {
/* 1635 */     if (a instanceof FieldElement[]) {
/* 1636 */       FieldElement[] array = (FieldElement[])a;
/* 1637 */       for (int i = 0; i < array.length; i++) {
/* 1638 */         makeOutput(generator, array[i], desc);
/*      */       }
/* 1640 */     } else if (a instanceof FieldElement[][]) {
/* 1641 */       FieldElement[][] array = (FieldElement[][])a;
/* 1642 */       for (int i = 0; i < array.length; i++) {
/* 1643 */         makeOutput(generator, array[i], desc);
/*      */       }
/* 1645 */     } else if (a instanceof FieldElement[][][]) {
/* 1646 */       FieldElement[][][] array = (FieldElement[][][])a;
/* 1647 */       for (int i = 0; i < array.length; i++) {
/* 1648 */         makeOutput(generator, array[i], desc);
/*      */       }
/*      */     } else {
/* 1651 */       throw new IllegalArgumentException("Declaring higher dimensional arrays as outputs not supported at this point. Only 3 dimensions are supported");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void mapRandomValue(CircuitEvaluator evaluator) {
/* 1661 */     BigInteger rnd = Util.nextRandomBigInteger(this.modulus);
/* 1662 */     if (this.packedWire != null) {
/*      */       
/* 1664 */       evaluator.setWireValue(this.packedWire, rnd, this.modulus.bitLength(), 
/* 1665 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */     } else {
/*      */       
/* 1668 */       int length = this.bitWires.size();
/* 1669 */       for (int i = 0; i < length; i++) {
/* 1670 */         evaluator.setWireValue(this.bitWires.get(i), 
/* 1671 */             rnd.testBit(i) ? BigInteger.ONE : BigInteger.ZERO);
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
/*      */   public FieldElement mul(Bit bit) {
/* 1718 */     return mul(new FieldElement(this.modulus, new PackedValue(bit.wire, 
/* 1719 */             BigInteger.ONE), BigInteger.ONE));
/*      */   }
/*      */   
/*      */   public FieldElement add(Bit bit) {
/* 1723 */     return add(new FieldElement(this.modulus, new PackedValue(bit.wire, 
/* 1724 */             BigInteger.ONE), BigInteger.ONE));
/*      */   }
/*      */ 
/*      */   
/*      */   public void forceEqual(IAuxType o) {
/* 1729 */     if (!(o instanceof FieldElement)) {
/* 1730 */       throw new IllegalArgumentException("FieldType expected");
/*      */     }
/*      */     
/* 1733 */     FieldElement other = (FieldElement)o;
/* 1734 */     if (getConstant() != null && other.getConstant() != null && 
/* 1735 */       !getConstant().equals(other.getConstant())) {
/* 1736 */       throw new RuntimeException("Constraint fails on constant uints");
/*      */     }
/*      */ 
/*      */     
/* 1740 */     if (this.generator.__getPhase() == 0) {
/* 1741 */       this.variableState.setPackedAhead(true);
/* 1742 */       other.variableState.setPackedAhead(true);
/* 1743 */       this.variableState.setConditionallySplittedAhead(true);
/* 1744 */       other.variableState.setConditionallySplittedAhead(true);
/* 1745 */       this.variableState.setMustBeWithinRange(true);
/* 1746 */       other.variableState.setMustBeWithinRange(true);
/*      */     
/*      */     }
/* 1749 */     else if (!this.nativeSnarkField && (
/* 1750 */       this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT || other.modulus
/* 1751 */       .bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT)) {
/* 1752 */       this.packedWire.forceEquality2(other.packedWire);
/*      */     } else {
/* 1754 */       this.generator.__addEqualityAssertion(this.packedWire.array[0], 
/* 1755 */           other.packedWire.array[0], new String[0]);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Class<?> __getClassRef() {
/* 1761 */     return FieldElement.class;
/*      */   }
/*      */   
/*      */   public VariableState getState() {
/* 1765 */     return this.variableState;
/*      */   }
/*      */ 
/*      */   
/*      */   public PackedValue getPackedValue() {
/* 1770 */     return this.packedWire;
/*      */   }
/*      */ 
/*      */   
/*      */   public Bit isEqualTo(IAuxType o) {
/* 1775 */     if (!(o instanceof FieldElement)) {
/* 1776 */       throw new IllegalArgumentException("UnsignedINT expected");
/*      */     }
/* 1778 */     if (this == o) {
/* 1779 */       return new Bit(true);
/*      */     }
/* 1781 */     FieldElement other = (FieldElement)o;
/* 1782 */     if (getConstant() != null && other.getConstant() != null) {
/* 1783 */       return new Bit(getConstant().equals(other.getConstant()));
/*      */     }
/* 1785 */     if (this.generator.__getPhase() == 0) {
/* 1786 */       this.variableState.setPackedAhead(true);
/* 1787 */       other.variableState.setPackedAhead(true);
/* 1788 */       this.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 1789 */       other.variableState.setConditionallySplittedAndAlignedAhead(true);
/*      */ 
/*      */       
/* 1792 */       return new Bit(new Wire(-1));
/*      */     } 
/* 1794 */     if (!this.nativeSnarkField && (
/* 1795 */       this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT || other.modulus
/* 1796 */       .bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT)) {
/* 1797 */       return this.packedWire.isEqualTo(other.packedWire);
/*      */     }
/* 1799 */     return new Bit(
/* 1800 */         this.packedWire.array[0]
/* 1801 */         .isEqualTo(other.packedWire.array[0], new String[0]));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Bit isNotEqualTo(IAuxType o) {
/* 1807 */     return isEqualTo(o).inv();
/*      */   }
/*      */   
/*      */   public static FieldElement instantiateFrom(BigInteger modulus, int v) {
/* 1811 */     return new FieldElement(modulus, BigInteger.valueOf(v));
/*      */   }
/*      */   
/*      */   public static FieldElement instantiateFrom(BigInteger modulus, long v) {
/* 1815 */     return new FieldElement(modulus, BigInteger.valueOf(v));
/*      */   }
/*      */   
/*      */   public static FieldElement instantiateFrom(BigInteger modulus, BigInteger v) {
/* 1819 */     return new FieldElement(modulus, v);
/*      */   }
/*      */ 
/*      */   
/*      */   public static FieldElement instantiateFrom(BigInteger modulus, FieldElement e) {
/* 1824 */     if (modulus.equals(e.getModulus())) {
/* 1825 */       return e.copy();
/*      */     }
/* 1827 */     return instantiateFrom(modulus, UnsignedInteger.instantiateFrom(e.getModulus().bitLength(), e));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static FieldElement instantiateFrom(BigInteger modulus, UnsignedInteger e) {
/* 1834 */     if (e.isConstant()) {
/* 1835 */       return new FieldElement(modulus, e.getConstant());
/*      */     }
/* 1837 */     CircuitGenerator generator = 
/* 1838 */       CircuitGenerator.__getActiveCircuitGenerator();
/* 1839 */     if (generator.__getPhase() == 0) {
/* 1840 */       e.getState().setConditionallySplittedAhead(true);
/* 1841 */       e.getState().setMustBeWithinRange(true);
/* 1842 */       e.getState().setPackedAhead(true);
/* 1843 */       FieldElement res = new FieldElement(generator, modulus);
/*      */ 
/*      */       
/* 1846 */       return res;
/*      */     } 
/* 1848 */     if (!modulus.equals(Config.getFiniteFieldModulus())) {
/* 1849 */       if (e.maxValue.compareTo(modulus) >= 0 && 
/* 1850 */         e.maxValue.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/* 1851 */         ModConstantGadget modConstantGadget = new ModConstantGadget(
/* 1852 */             e.packedWire.array[0], e.maxValue.bitLength(), 
/* 1853 */             modulus, true, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1858 */         FieldElement fieldElement1 = new FieldElement(modulus, new PackedValue(
/* 1859 */               modConstantGadget.getOutputWires()[0], 
/* 1860 */               modulus.subtract(BigInteger.ONE)));
/*      */ 
/*      */ 
/*      */         
/* 1864 */         return fieldElement1;
/*      */       } 
/*      */       
/* 1867 */       if (e.maxValue.compareTo(modulus) >= 0 && 
/* 1868 */         e.maxValue.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*      */         
/* 1870 */         PackedValue modValue = new PackedValue(Util.split(
/* 1871 */               modulus, UnsignedInteger.BITWIDTH_PER_CHUNK));
/* 1872 */         LongIntegerModConstantGadget g = new LongIntegerModConstantGadget(
/* 1873 */             e.packedWire, modValue, true, new String[0]);
/*      */         
/* 1875 */         PackedValue remainder = g.getRemainder();
/* 1876 */         BigInteger[] maxVals = new BigInteger[remainder.array.length];
/* 1877 */         Arrays.fill((Object[])maxVals, Util.computeMaxValue(UnsignedInteger.BITWIDTH_PER_CHUNK));
/* 1878 */         if (remainder.array.length * UnsignedInteger.BITWIDTH_PER_CHUNK != modulus
/* 1879 */           .bitLength()) {
/* 1880 */           maxVals[remainder.array.length - 1] = Util.computeMaxValue(modulus.bitLength() % 
/* 1881 */               UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */         }
/* 1883 */         if (maxVals.length == 1) {
/* 1884 */           maxVals[0] = modulus.subtract(BigInteger.ONE);
/*      */         }
/*      */         
/* 1887 */         FieldElement fieldElement1 = new FieldElement(modulus, new PackedValue(
/* 1888 */               (g.getRemainder()).array, 
/* 1889 */               maxVals), modulus.subtract(BigInteger.ONE));
/*      */ 
/*      */ 
/*      */         
/* 1893 */         return fieldElement1;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1900 */       FieldElement fieldElement = new FieldElement(modulus, e.packedWire, e.maxValue);
/*      */ 
/*      */ 
/*      */       
/* 1904 */       return fieldElement;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1910 */     Wire w = e.packedWire.array[0];
/* 1911 */     for (int i = 1; i < e.packedWire.array.length; i++) {
/* 1912 */       w = w.add(e.packedWire.array[i].mul(2L, i * UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]), new String[0]);
/*      */     }
/* 1914 */     BigInteger maxVal = modulus.subtract(BigInteger.ONE);
/* 1915 */     if (e.maxValue.compareTo(maxVal) < 0) {
/* 1916 */       maxVal = e.maxValue;
/*      */     }
/*      */     
/* 1919 */     FieldElement result = new FieldElement(modulus, new PackedValue(w, maxVal), maxVal);
/*      */ 
/*      */ 
/*      */     
/* 1923 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static FieldElement instantiateFrom(BigInteger modulus, String v) {
/* 1930 */     return new FieldElement(modulus, new BigInteger(v));
/*      */   }
/*      */   
/*      */   public static FieldElement[] instantiateFrom(BigInteger modulus, int[] v) {
/* 1934 */     FieldElement[] a = new FieldElement[v.length];
/* 1935 */     for (int i = 0; i < a.length; i++)
/* 1936 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1937 */     return a;
/*      */   }
/*      */   
/*      */   public static FieldElement[] instantiateFrom(BigInteger modulus, byte[] v) {
/* 1941 */     FieldElement[] a = new FieldElement[v.length];
/* 1942 */     for (int i = 0; i < a.length; i++)
/* 1943 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1944 */     return a;
/*      */   }
/*      */   
/*      */   public static FieldElement[] instantiateFrom(BigInteger modulus, long[] v) {
/* 1948 */     FieldElement[] a = new FieldElement[v.length];
/* 1949 */     for (int i = 0; i < a.length; i++)
/* 1950 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1951 */     return a;
/*      */   }
/*      */ 
/*      */   
/*      */   public static FieldElement[] instantiateFrom(BigInteger modulus, BigInteger[] v) {
/* 1956 */     FieldElement[] a = new FieldElement[v.length];
/* 1957 */     for (int i = 0; i < a.length; i++)
/* 1958 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1959 */     return a;
/*      */   }
/*      */   
/*      */   public static FieldElement[] instantiateFrom(BigInteger modulus, String[] v) {
/* 1963 */     FieldElement[] a = new FieldElement[v.length];
/* 1964 */     for (int i = 0; i < a.length; i++)
/* 1965 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1966 */     return a;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static FieldElement[] instantiateFrom(BigInteger modulus, FieldElement[] v) {
/* 1972 */     FieldElement[] a = new FieldElement[v.length];
/* 1973 */     for (int i = 0; i < a.length; i++)
/* 1974 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1975 */     return a;
/*      */   }
/*      */   
/*      */   public static FieldElement[] instantiateFrom(BigInteger modulus, UnsignedInteger[] v) {
/* 1979 */     FieldElement[] a = new FieldElement[v.length];
/* 1980 */     for (int i = 0; i < a.length; i++)
/* 1981 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1982 */     return a;
/*      */   }
/*      */   
/*      */   public boolean isNativeSnarkField() {
/* 1986 */     return this.nativeSnarkField;
/*      */   }
/*      */   
/*      */   public FieldElement() {}
/*      */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\FieldElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */