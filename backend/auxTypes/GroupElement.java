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
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class GroupElement
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
/*   50 */   protected int conditionalScopeId = ConditionalScopeTracker.getCurrentScopeId();
/*      */   
/*   52 */   protected int scope = ConditionalScopeTracker.getCurrentScopeId();
/*      */   
/*      */   protected Stack<HashMap<Integer, GroupElement>> possibleValStack;
/*      */   protected Stack<GroupElement> prevValStack;
/*      */   private boolean stateChanged;
/*      */   protected boolean nativeSnarkField = false;
/*      */   
/*      */   public void setConditionalScopeId(int id) {
/*   60 */     this.conditionalScopeId = id;
/*      */   }
/*      */   
/*      */   public GroupElement(GroupElement o) {
/*   64 */     this.generator = o.generator;
/*   65 */     this.maxValue = o.maxValue;
/*   66 */     this.constant = o.constant;
/*   67 */     this.variableState = o.variableState;
/*   68 */     this.currentBitwidth = o.currentBitwidth;
/*   69 */     this.bitWires = o.bitWires;
/*   70 */     this.packedWire = o.packedWire;
/*   71 */     this.conditionalScopeId = o.conditionalScopeId;
/*   72 */     this.modulus = o.modulus;
/*   73 */     this.nativeSnarkField = o.nativeSnarkField;
/*   74 */     this.scope = o.scope;
/*      */   }
/*      */   
/*      */   public BigInteger getConstant() {
/*   78 */     return this.constant;
/*      */   }
/*      */   
/*      */   public void assign(GroupElement target) {
/*   82 */     if (this.generator.__getPhase() == 0) {
/*      */       
/*   84 */       if (this.scope != ConditionalScopeTracker.getCurrentScopeId()) {
/*   85 */         ConditionalScopeTracker.register(this, this.scope);
/*   86 */         if (this.possibleValStack == null) {
/*   87 */           this.possibleValStack = new Stack<>();
/*      */         }
/*   89 */         if (this.prevValStack == null) {
/*   90 */           this.prevValStack = new Stack<>();
/*      */         }
/*   92 */         int current = ConditionalScopeTracker.getCurrentScopeId();
/*      */         
/*   94 */         for (int i = 0; i < current - this.scope; i++) {
/*   95 */           GroupElement c = copy();
/*   96 */           c.variableState.setPackedAhead(true);
/*   97 */           this.prevValStack.push(c);
/*   98 */           this.possibleValStack.push(new HashMap<>());
/*      */         } 
/*  100 */         this.stateChanged = true;
/*  101 */         this.scope = ConditionalScopeTracker.getCurrentScopeId();
/*      */       } 
/*      */       
/*  104 */       this.constant = target.constant;
/*  105 */       this.nativeSnarkField = target.nativeSnarkField;
/*  106 */       this.variableState = target.variableState;
/*      */     
/*      */     }
/*  109 */     else if (this.scope == ConditionalScopeTracker.getCurrentScopeId()) {
/*      */       
/*  111 */       this.bitWires = target.bitWires;
/*  112 */       this.splittedAtCreationTime = target.splittedAtCreationTime;
/*  113 */       this.packedAtCreationTime = target.packedAtCreationTime;
/*  114 */       this.packedWire = target.packedWire;
/*  115 */       this.variableState = target.variableState;
/*  116 */       this.maxValue = target.maxValue;
/*  117 */       this.currentBitwidth = target.currentBitwidth;
/*  118 */       this.constant = target.constant;
/*      */     } else {
/*  120 */       this.stateChanged = true;
/*  121 */       ConditionalScopeTracker.register(this, this.scope);
/*      */       
/*  123 */       if (this.possibleValStack == null) {
/*  124 */         this.possibleValStack = new Stack<>();
/*      */       }
/*  126 */       if (this.prevValStack == null) {
/*  127 */         this.prevValStack = new Stack<>();
/*      */       }
/*  129 */       int current = ConditionalScopeTracker.getCurrentScopeId();
/*      */       
/*  131 */       int size = this.prevValStack.size();
/*  132 */       while (size < current) {
/*  133 */         this.prevValStack.push(copy());
/*  134 */         this.possibleValStack.push(new HashMap<>());
/*  135 */         size++;
/*      */       } 
/*      */       
/*  138 */       this.modulus = target.modulus;
/*  139 */       this.bitWires = target.bitWires;
/*  140 */       this.splittedAtCreationTime = target.splittedAtCreationTime;
/*  141 */       this.packedAtCreationTime = target.packedAtCreationTime;
/*  142 */       this.packedWire = target.packedWire;
/*  143 */       this.variableState = target.variableState;
/*  144 */       this.maxValue = target.maxValue;
/*  145 */       this.currentBitwidth = target.currentBitwidth;
/*  146 */       this.scope = ConditionalScopeTracker.getCurrentScopeId();
/*  147 */       this.constant = target.constant;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void pop(int id) {
/*  154 */     if (!this.stateChanged) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  159 */     GroupElement copy = copy();
/*  160 */     if (this.generator.__getPhase() == 0)
/*  161 */       copy.variableState.setPackedAhead(true); 
/*  162 */     ((HashMap<Integer, GroupElement>)this.possibleValStack.peek()).put(Integer.valueOf(id), copy);
/*  163 */     this.scope--;
/*  164 */     GroupElement prev = this.prevValStack.peek();
/*  165 */     this.packedWire = prev.packedWire;
/*  166 */     this.variableState = prev.variableState;
/*  167 */     this.bitWires = prev.bitWires;
/*  168 */     this.maxValue = prev.maxValue;
/*      */     
/*  170 */     this.constant = prev.constant;
/*  171 */     this.modulus = prev.modulus;
/*  172 */     this.currentBitwidth = prev.currentBitwidth;
/*  173 */     this.stateChanged = false;
/*      */   }
/*      */ 
/*      */   
/*      */   public void popMain() {
/*  178 */     if (this.generator.__getPhase() == 0) {
/*  179 */       this.variableState = this.generator.__retrieveVariableState();
/*  180 */       this.variableState.setPackedAhead(true);
/*      */       
/*  182 */       HashMap<Integer, GroupElement> possibleVals = this.possibleValStack
/*  183 */         .pop();
/*  184 */       int mulIndex = ((GroupElement)this.prevValStack.pop()).getVariableState().getMulIndex();
/*  185 */       for (Integer idx : possibleVals.keySet()) {
/*  186 */         int m = ((GroupElement)possibleVals.get(idx)).variableState.getMulIndex();
/*  187 */         if (m > mulIndex) {
/*  188 */           mulIndex = m;
/*      */         }
/*      */       } 
/*  191 */       this.variableState.setMulIndex(mulIndex);
/*  192 */       this.bitWires = null;
/*  193 */       this.constant = null;
/*  194 */       this.stateChanged = true;
/*      */     } else {
/*      */       
/*  197 */       int tmp = this.scope;
/*  198 */       if (ConditionalScopeTracker.getCurrentScopeId() > tmp) {
/*  199 */         this.stateChanged = true;
/*      */       }
/*      */       
/*  202 */       this.variableState = null;
/*  203 */       ConditionalScopeTracker.ConditionalStatementData condData = 
/*  204 */         ConditionalScopeTracker.getCurrentConditionalStmtData();
/*  205 */       this.bitWires = null;
/*  206 */       int numberOfValues = condData.getBitList().size();
/*  207 */       ArrayList<Bit> conditionList = condData.getBitList();
/*  208 */       GroupElement[] candidateList = new GroupElement[numberOfValues];
/*      */       
/*  210 */       HashMap<Integer, GroupElement> possibleVals = this.possibleValStack
/*  211 */         .pop();
/*  212 */       for (Integer idx : possibleVals.keySet()) {
/*  213 */         candidateList[idx.intValue()] = possibleVals.get(idx);
/*      */       }
/*  215 */       for (int i = 0; i < numberOfValues; i++) {
/*  216 */         if (candidateList[i] == null) {
/*  217 */           candidateList[i] = copy();
/*      */         }
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  224 */       GroupElement initial = candidateList[numberOfValues - 1];
/*  225 */       int startingIndex = -1;
/*  226 */       for (int j = numberOfValues - 2; j >= 0; j--) {
/*  227 */         if ((candidateList[j]).packedWire != initial.packedWire) {
/*  228 */           startingIndex = j;
/*      */           break;
/*      */         } 
/*      */       } 
/*  232 */       if (startingIndex == -1) {
/*      */ 
/*      */         
/*  235 */         this.packedWire = initial.packedWire;
/*  236 */         this.maxValue = initial.maxValue;
/*  237 */         this.bitWires = initial.bitWires;
/*  238 */         this.currentBitwidth = initial.currentBitwidth;
/*      */         
/*  240 */         this.modulus = initial.modulus;
/*  241 */         this.constant = initial.constant;
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  246 */         GroupElement current = initial;
/*  247 */         this.modulus = initial.modulus;
/*  248 */         this.packedWire = initial.packedWire;
/*  249 */         this.maxValue = initial.maxValue;
/*  250 */         this.bitWires = initial.bitWires;
/*  251 */         this.currentBitwidth = initial.currentBitwidth;
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  256 */         for (int k = startingIndex; k >= 0; k--) {
/*      */           
/*  258 */           current = candidateList[k];
/*  259 */           Bit selectionBit = conditionList.get(k);
/*  260 */           this.packedWire = this.packedWire.muxBit(current.packedWire, 
/*  261 */               selectionBit.wire);
/*  262 */           this.bitWires = null;
/*  263 */           this.constant = null;
/*  264 */           this.maxValue = (this.maxValue.compareTo(current.maxValue) > 0) ? this.maxValue : 
/*  265 */             current.maxValue;
/*      */           
/*  267 */           this.currentBitwidth = Math.max(this.currentBitwidth, 
/*  268 */               current.currentBitwidth);
/*      */         } 
/*      */       } 
/*  271 */       this.prevValStack.pop();
/*  272 */       init();
/*      */     } 
/*      */   }
/*      */   
/*      */   public GroupElement copy() {
/*  277 */     if (this.generator.__getPhase() == 0) {
/*  278 */       GroupElement e = new GroupElement(this.generator, this.modulus, this.variableState);
/*  279 */       e.constant = this.constant;
/*  280 */       e.packedWire = this.packedWire;
/*  281 */       e.bitWires = this.bitWires;
/*  282 */       e.maxValue = this.maxValue;
/*  283 */       return e;
/*      */     } 
/*  285 */     GroupElement v = new GroupElement();
/*  286 */     v.modulus = this.modulus;
/*  287 */     v.bitWires = this.bitWires;
/*  288 */     v.splittedAtCreationTime = this.splittedAtCreationTime;
/*  289 */     v.packedAtCreationTime = this.packedAtCreationTime;
/*  290 */     v.packedWire = this.packedWire;
/*  291 */     v.variableState = this.variableState;
/*  292 */     v.maxValue = this.maxValue;
/*  293 */     v.currentBitwidth = this.currentBitwidth;
/*  294 */     v.generator = this.generator;
/*  295 */     v.constant = this.constant;
/*  296 */     v.nativeSnarkField = this.nativeSnarkField;
/*  297 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public GroupElement(BigInteger modulus, PackedValue packedWire, BigInteger maxValue) {
/*  308 */     this.maxValue = maxValue;
/*  309 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  310 */     this.packedWire = packedWire;
/*  311 */     this.modulus = modulus;
/*      */     
/*  313 */     this.currentBitwidth = (maxValue == null) ? modulus.bitLength() : maxValue
/*  314 */       .bitLength();
/*      */     
/*  316 */     init();
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
/*      */   public GroupElement(BigInteger modulus, PackedValue packedWire) {
/*  340 */     this(modulus, packedWire, (BigInteger)null);
/*      */   }
/*      */ 
/*      */   
/*      */   public GroupElement(BigInteger modulus, BigInteger constant) {
/*  345 */     this(modulus, CircuitGenerator.__getActiveCircuitGenerator().__createConstantPackedValue(
/*  346 */           Util.prepConstant(constant, modulus), modulus));
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
/*      */   private GroupElement(CircuitGenerator generator, BigInteger modulus) {
/*  362 */     this.modulus = modulus;
/*  363 */     this.generator = generator;
/*  364 */     this.maxValue = modulus.subtract(BigInteger.ONE);
/*  365 */     this.currentBitwidth = this.maxValue.bitLength();
/*  366 */     init();
/*      */   }
/*      */ 
/*      */   
/*      */   private GroupElement(CircuitGenerator generator, BigInteger modulus, VariableState st) {
/*  371 */     this.modulus = modulus;
/*  372 */     this.generator = generator;
/*  373 */     this.variableState = st;
/*  374 */     this.maxValue = modulus.subtract(BigInteger.ONE);
/*  375 */     this.currentBitwidth = this.maxValue.bitLength();
/*      */   }
/*      */ 
/*      */   
/*      */   public GroupElement(BigInteger modulus) {
/*  380 */     this.modulus = modulus;
/*  381 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  382 */     this.currentBitwidth = modulus.bitLength();
/*  383 */     init();
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
/*  399 */     if (this.modulus.equals(Config.getFiniteFieldModulus()))
/*      */     {
/*  401 */       this.nativeSnarkField = true;
/*      */     }
/*      */     
/*  404 */     checkConstant();
/*  405 */     if (this.variableState == null) {
/*  406 */       this.variableState = this.generator.__retrieveVariableState();
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  411 */     if (this.maxValue == null) {
/*  412 */       this.maxValue = this.modulus.subtract(BigInteger.ONE);
/*  413 */       this.currentBitwidth = this.maxValue.bitLength();
/*      */     } 
/*      */     
/*  416 */     if (this.generator.__getPhase() == 1) {
/*      */ 
/*      */       
/*  419 */       if (this.constant != null) {
/*      */         
/*  421 */         this.maxValue = this.constant.mod(this.modulus);
/*  422 */         this.packedWire = this.generator.__createConstantPackedValue(this.maxValue, 
/*  423 */             this.modulus);
/*      */ 
/*      */         
/*  426 */         this.bitWires = this.packedWire.getBits(this.modulus.bitLength(), 
/*  427 */             UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]);
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  437 */       if (this.variableState != null) {
/*  438 */         if (this.variableState.isPackedAhead() && this.packedWire == null) {
/*  439 */           WireArray modified = this.bitWires.adjustLength(Math.min(
/*  440 */                 this.bitWires.size(), this.modulus.bitLength()));
/*  441 */           if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_PER_CHUNK || 
/*  442 */             this.nativeSnarkField) {
/*  443 */             this.packedWire = new PackedValue(modified, 
/*  444 */                 this.modulus.bitLength());
/*      */           } else {
/*  446 */             this.packedWire = new PackedValue(modified, 
/*  447 */                 UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */           } 
/*  449 */           this.maxValue = Util.min(this.bitWires
/*  450 */               .computeTightUpperBoundOfBitWires(this.modulus
/*  451 */                 .bitLength()), this.modulus
/*  452 */               .subtract(BigInteger.ONE));
/*  453 */           this.currentBitwidth = this.maxValue.bitLength();
/*      */         }
/*  455 */         else if (this.variableState.isSplittedAhead() && this.bitWires == null) {
/*      */           
/*  457 */           getBackInRange(true);
/*      */           
/*  459 */           if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/*  460 */             this.nativeSnarkField) {
/*  461 */             this.bitWires = this.packedWire.getBits(this.modulus.bitLength(), 
/*  462 */                 this.modulus.bitLength(), new String[0]);
/*      */           } else {
/*  464 */             this.bitWires = this.packedWire.getBits(this.modulus.bitLength(), 
/*  465 */                 UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]);
/*      */           } 
/*  467 */           this.maxValue = Util.min(this.bitWires
/*  468 */               .computeTightUpperBoundOfBitWires(this.modulus
/*  469 */                 .bitLength()), this.modulus
/*  470 */               .subtract(BigInteger.ONE));
/*  471 */           this.currentBitwidth = this.maxValue.bitLength();
/*  472 */           if (!this.nativeSnarkField && (
/*  473 */             this.maxValue.compareTo(this.modulus) >= 0 || 
/*  474 */             !this.packedWire.isAligned()) && this.variableState.isPackedAhead())
/*      */           {
/*  476 */             if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  477 */               this.packedWire = new PackedValue(this.bitWires, 
/*  478 */                   this.modulus.bitLength());
/*      */             } else {
/*  480 */               this.packedWire = new PackedValue(this.bitWires, 
/*  481 */                   UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */             }
/*      */           
/*      */           }
/*  485 */         } else if ((this.variableState.isConditionallySplittedAhead() || this.variableState
/*  486 */           .isConditionallySplittedAndAlignedAhead()) && 
/*  487 */           this.bitWires == null) {
/*  488 */           if (this.maxValue.compareTo(this.modulus) >= 0) {
/*  489 */             if (this.variableState.isMustBeWithinRange()) {
/*  490 */               getBackInRange(true);
/*  491 */               if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/*  492 */                 this.nativeSnarkField) {
/*  493 */                 this.bitWires = this.packedWire.getBits(
/*  494 */                     this.modulus.bitLength(), 
/*  495 */                     this.modulus.bitLength(), new String[0]);
/*      */               } else {
/*  497 */                 this.bitWires = this.packedWire.getBits(
/*  498 */                     this.modulus.bitLength(), 
/*  499 */                     UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]);
/*      */               } 
/*  501 */               this.maxValue = Util.min(this.bitWires
/*  502 */                   .computeTightUpperBoundOfBitWires(this.modulus
/*  503 */                     .bitLength()), this.modulus
/*  504 */                   .subtract(BigInteger.ONE));
/*  505 */               this.currentBitwidth = this.maxValue.bitLength();
/*  506 */               if (!this.nativeSnarkField && (
/*  507 */                 this.maxValue.compareTo(this.modulus) >= 0 || 
/*  508 */                 !this.packedWire.isAligned()) && 
/*  509 */                 this.variableState.isPackedAhead())
/*      */               {
/*  511 */                 if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  512 */                   this.packedWire = new PackedValue(this.bitWires, 
/*  513 */                       this.modulus.bitLength());
/*      */                 } else {
/*  515 */                   this.packedWire = new PackedValue(
/*  516 */                       this.bitWires, 
/*  517 */                       UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */                 } 
/*      */               }
/*      */             } else {
/*      */               
/*  522 */               getBackInRange(false);
/*      */             }
/*      */           
/*  525 */           } else if (!this.nativeSnarkField && 
/*  526 */             this.packedWire != null && 
/*  527 */             !this.packedWire.isAligned() && 
/*  528 */             this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT && 
/*  529 */             this.variableState
/*  530 */             .isConditionallySplittedAndAlignedAhead()) {
/*  531 */             this.packedWire = this.packedWire.align((int)Math.ceil(this.modulus
/*  532 */                   .bitLength() * 
/*  533 */                   1.0D / 
/*  534 */                   UnsignedInteger.BITWIDTH_PER_CHUNK), 
/*  535 */                 UnsignedInteger.BITWIDTH_LIMIT_SHORT);
/*  536 */           } else if (this.packedWire == null && 
/*  537 */             this.variableState
/*  538 */             .isConditionallySplittedAndAlignedAhead()) {
/*  539 */             throw new RuntimeException("Case Unexpected");
/*      */           }
/*      */         
/*      */         }
/*  543 */         else if (this.bitWires != null && 
/*  544 */           this.bitWires.size() > this.modulus.bitLength()) {
/*  545 */           this.bitWires = new WireArray(Arrays.<Wire>copyOfRange(
/*  546 */                 this.bitWires.asArray(), 0, this.modulus.bitLength()));
/*      */           
/*  548 */           if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_PER_CHUNK || 
/*  549 */             this.nativeSnarkField) {
/*  550 */             this.packedWire = new PackedValue(this.bitWires, 
/*  551 */                 this.modulus.bitLength());
/*      */           } else {
/*  553 */             this.packedWire = new PackedValue(this.bitWires, 
/*  554 */                 UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */           } 
/*  556 */           this.maxValue = Util.min(this.bitWires
/*  557 */               .computeTightUpperBoundOfBitWires(this.modulus
/*  558 */                 .bitLength()), this.modulus
/*  559 */               .subtract(BigInteger.ONE));
/*  560 */           this.currentBitwidth = this.maxValue.bitLength();
/*      */         } 
/*      */ 
/*      */         
/*  564 */         if (this.variableState.getThresholdBitwidth() != -1 && 
/*  565 */           this.bitWires == null) {
/*      */           
/*  567 */           this.currentBitwidth = this.maxValue.bitLength();
/*  568 */           if (this.currentBitwidth > this.variableState.getThresholdBitwidth()) {
/*  569 */             if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/*  570 */               this.nativeSnarkField) {
/*  571 */               this.bitWires = this.packedWire.getBits(this.modulus.bitLength(), 
/*  572 */                   this.modulus.bitLength(), new String[0]);
/*      */             } else {
/*  574 */               this.bitWires = this.packedWire.getBits(this.modulus.bitLength(), 
/*  575 */                   UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]);
/*      */             } 
/*  577 */             this.maxValue = Util.min(this.bitWires
/*  578 */                 .computeTightUpperBoundOfBitWires(this.modulus
/*  579 */                   .bitLength()), this.modulus
/*  580 */                 .subtract(BigInteger.ONE));
/*  581 */             this.currentBitwidth = this.maxValue.bitLength();
/*      */             
/*  583 */             if ((this.maxValue.compareTo(this.modulus) >= 0 || 
/*  584 */               !this.packedWire.isAligned()) && this.variableState.isPackedAhead()) {
/*  585 */               if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  586 */                 this.packedWire = new PackedValue(this.bitWires, 
/*  587 */                     this.modulus.bitLength());
/*      */               } else {
/*  589 */                 this.packedWire = new PackedValue(this.bitWires, 
/*  590 */                     UnsignedInteger.BITWIDTH_PER_CHUNK);
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
/*  609 */     if (this.packedWire != null)
/*  610 */       if ((this.constant = this.packedWire
/*  611 */         .getConstant(UnsignedInteger.BITWIDTH_PER_CHUNK)) != null) {
/*      */         
/*  613 */         if (this.constant.signum() == -1)
/*  614 */           throw new IllegalArgumentException(
/*  615 */               "Unisgned Integer is being instantiated from a negative constant");  return;
/*      */       }  
/*  617 */     if (this.bitWires != null) {
/*  618 */       boolean allConstant = true;
/*  619 */       BigInteger v = BigInteger.ZERO;
/*  620 */       int i = 0; byte b; int j; Wire[] arrayOfWire;
/*  621 */       for (j = (arrayOfWire = this.bitWires.asArray()).length, b = 0; b < j; ) { Wire w = arrayOfWire[b];
/*  622 */         if (!(w instanceof ConstantWire)) {
/*  623 */           allConstant = false;
/*      */           break;
/*      */         } 
/*  626 */         ConstantWire constWire = (ConstantWire)w;
/*  627 */         if (!constWire.isBinary()) {
/*  628 */           throw new RuntimeException(
/*  629 */               "Non-binary bit value used to construct an integer");
/*      */         }
/*  631 */         v = v.add(constWire.getConstant().multiply((
/*  632 */               new BigInteger("2")).pow(i++)));
/*      */         
/*      */         b++; }
/*      */ 
/*      */       
/*  637 */       if (allConstant) {
/*  638 */         this.constant = v;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public GroupElement mul(BigInteger b) {
/*  646 */     return mul(new GroupElement(this.modulus, b));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void handleOverflow(GroupElement x1, GroupElement x2, boolean isMul) {
/*  654 */     boolean longSetting = false;
/*  655 */     boolean adjusted = false;
/*      */     
/*  657 */     int maxBitWidth = x1.modulus.bitLength();
/*  658 */     if (maxBitWidth > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  659 */       longSetting = true;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  664 */     int b1Max = x1.packedWire.getBitwidthOfLargestChunk();
/*  665 */     int b2Max = x2.packedWire.getBitwidthOfLargestChunk();
/*      */ 
/*      */     
/*  668 */     if (isMul) {
/*      */       
/*  670 */       if (longSetting) {
/*  671 */         if (b1Max + UnsignedInteger.BITWIDTH_PER_CHUNK >= 
/*  672 */           Config.getNumBitsFiniteFieldModulus()) {
/*  673 */           x1.getBackInRange(false);
/*  674 */           adjusted = true;
/*      */         } 
/*  676 */         if (b2Max + UnsignedInteger.BITWIDTH_PER_CHUNK >= 
/*  677 */           Config.getNumBitsFiniteFieldModulus()) {
/*  678 */           x2.getBackInRange(false);
/*  679 */           adjusted = true;
/*      */         } 
/*      */       } else {
/*      */         
/*  683 */         if (b1Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  684 */           x1.getBackInRange(false);
/*  685 */           adjusted = true;
/*      */         } 
/*  687 */         if (b2Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  688 */           x2.getBackInRange(false);
/*  689 */           adjusted = true;
/*      */         } 
/*      */       } 
/*      */     } else {
/*  693 */       if (b1Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  694 */         x1.getBackInRange(false);
/*  695 */         adjusted = true;
/*      */       } 
/*      */       
/*  698 */       if (b2Max > Config.getNumBitsFiniteFieldModulus() - 2) {
/*  699 */         x2.getBackInRange(false);
/*  700 */         adjusted = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  705 */     if (adjusted) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  710 */     int excesss1 = x1.variableState.getMulUseCount() * (
/*  711 */       x1.currentBitwidth - maxBitWidth);
/*  712 */     int excesss2 = x2.variableState.getMulUseCount() * (
/*  713 */       x2.currentBitwidth - maxBitWidth);
/*      */     
/*  715 */     if (excesss1 > excesss2) {
/*  716 */       x1.getBackInRange(false);
/*  717 */     } else if (excesss2 < excesss1) {
/*  718 */       x2.getBackInRange(false);
/*      */     } else {
/*  720 */       excesss1 = x1.variableState.getAddUseCount() * (
/*  721 */         x1.currentBitwidth - maxBitWidth);
/*  722 */       excesss2 = x2.variableState.getAddUseCount() * (
/*  723 */         x2.currentBitwidth - maxBitWidth);
/*  724 */       if (excesss1 > excesss2) {
/*  725 */         x1.getBackInRange(false);
/*  726 */       } else if (excesss2 < excesss1) {
/*  727 */         x2.getBackInRange(false);
/*      */       }
/*  729 */       else if (x1.currentBitwidth - maxBitWidth > 0) {
/*  730 */         x1.getBackInRange(false);
/*      */       } else {
/*  732 */         x2.getBackInRange(false);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private VariableState getVariableState() {
/*  740 */     return this.variableState;
/*      */   }
/*      */ 
/*      */   
/*      */   public GroupElement mul(GroupElement o) {
/*  745 */     if (isConstant() && o.isConstant()) {
/*  746 */       return new GroupElement(this.modulus, getConstant().multiply(
/*  747 */             o.getConstant()));
/*      */     }
/*  749 */     if (isConstant() && getConstant().equals(BigInteger.ONE)) {
/*  750 */       CircuitGenerator.__getActiveCircuitGenerator().__retrieveVariableState();
/*      */       
/*  752 */       return o.copy();
/*      */     } 
/*  754 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ONE)) {
/*  755 */       CircuitGenerator.__getActiveCircuitGenerator().__retrieveVariableState();
/*      */       
/*  757 */       return copy();
/*      */     } 
/*      */     
/*  760 */     if (isConstant() && getConstant().equals(BigInteger.ZERO)) {
/*  761 */       return new GroupElement(this.modulus, BigInteger.ZERO);
/*      */     }
/*      */     
/*  764 */     if (o.isConstant() && o.getConstant().equals(BigInteger.ZERO)) {
/*  765 */       return new GroupElement(this.modulus, BigInteger.ZERO);
/*      */     }
/*      */     
/*  768 */     if (this.generator.__getPhase() == 0) {
/*  769 */       this.variableState.setPackedAhead(true);
/*  770 */       o.variableState.setPackedAhead(true);
/*  771 */       this.variableState.incMulUseCount();
/*  772 */       o.variableState.incMulUseCount();
/*      */       
/*  774 */       int idx1 = getVariableState().getMulIndex();
/*  775 */       int idx2 = o.getVariableState().getMulIndex();
/*      */       
/*  777 */       if (idx1 == 1 && 
/*  778 */         this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  779 */         this.variableState.setConditionallySplittedAhead(true);
/*      */       }
/*  781 */       if (idx2 == 1 && 
/*  782 */         o.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  783 */         o.variableState.setConditionallySplittedAhead(true);
/*      */       }
/*      */       
/*  786 */       GroupElement groupElement = new GroupElement(this.generator, this.modulus);
/*  787 */       groupElement.getVariableState().incMulIndex();
/*      */       
/*  789 */       return groupElement;
/*      */     } 
/*      */     
/*  792 */     BigInteger outMaxValue = this.maxValue.multiply(o.maxValue);
/*  793 */     this.variableState.decMulUseCount();
/*  794 */     o.variableState.decMulUseCount();
/*      */     
/*  796 */     if (!this.nativeSnarkField) {
/*  797 */       boolean overflowCheck = this.packedWire
/*  798 */         .mulOverflowCheck(o.packedWire);
/*  799 */       if (overflowCheck) {
/*  800 */         handleOverflow(this, o, true);
/*  801 */         overflowCheck = this.packedWire.mulOverflowCheck(o.packedWire);
/*  802 */         if (overflowCheck) {
/*  803 */           handleOverflow(this, o, true);
/*      */         }
/*      */       } 
/*      */     } 
/*  807 */     outMaxValue = this.maxValue.multiply(o.maxValue);
/*  808 */     if (this.nativeSnarkField) {
/*  809 */       outMaxValue = Util.min(outMaxValue, 
/*  810 */           this.modulus.subtract(BigInteger.ONE));
/*      */     }
/*      */     
/*  813 */     PackedValue.disableOverflowChecks = true;
/*  814 */     GroupElement result = new GroupElement(this.modulus, 
/*  815 */         this.packedWire.mul(o.packedWire), outMaxValue);
/*  816 */     PackedValue.disableOverflowChecks = false;
/*      */     
/*  818 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public GroupElement div(GroupElement b) {
/*  824 */     if (isConstant() && b.isConstant()) {
/*  825 */       BigInteger bigInteger = b.getConstant().modInverse(this.modulus)
/*  826 */         .multiply(getConstant()).mod(this.modulus);
/*      */       
/*  828 */       return new GroupElement(this.modulus, bigInteger);
/*      */     } 
/*      */     
/*  831 */     if (this.generator.__getPhase() == 0) {
/*      */       
/*  833 */       this.variableState.setPackedAhead(true);
/*  834 */       int idx1 = getVariableState().getMulIndex();
/*  835 */       this.variableState.setConditionallySplittedAhead(true);
/*  836 */       this.variableState.setMustBeWithinRange(true);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  846 */       if (Config.enforceInternalDivisionNonZeroChecks) {
/*  847 */         b.variableState.setConditionallySplittedAndAlignedAhead(true);
/*  848 */         b.variableState.setMustBeWithinRange(true);
/*      */       } 
/*  850 */       b.variableState.setPackedAhead(true);
/*  851 */       int idx2 = b.getVariableState().getMulIndex();
/*      */       
/*  853 */       if (idx2 == 1 && 
/*  854 */         b.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  855 */         b.variableState.setConditionallySplittedAhead(true);
/*      */       }
/*      */       
/*  858 */       GroupElement groupElement = new GroupElement(this.generator, this.modulus);
/*  859 */       return groupElement;
/*      */     } 
/*  861 */     if (Config.enforceInternalDivisionNonZeroChecks) {
/*  862 */       b.packedWire.forceNonZero();
/*      */     }
/*  864 */     if (this.nativeSnarkField) {
/*  865 */       PackedValue.disableOverflowChecks = true;
/*  866 */       FieldDivisionGadget f = new FieldDivisionGadget(
/*  867 */           this.packedWire.array[0], b.packedWire.array[0], new String[0]);
/*  868 */       PackedValue.disableOverflowChecks = false;
/*  869 */       return new GroupElement(this.modulus, 
/*  870 */           new PackedValue(f.getOutputWires()[0], this.modulus
/*  871 */             .subtract(BigInteger.ONE)), 
/*  872 */           this.modulus.subtract(BigInteger.ONE));
/*      */     } 
/*      */     
/*  875 */     if (this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*      */       
/*  877 */       BigInteger bigInteger = Util.computeMaxValue(b.modulus.bitLength());
/*  878 */       BigInteger[] arrayOfBigInteger = Util.split(bigInteger, 
/*  879 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  880 */       boolean bool = b.packedWire
/*  881 */         .mulAddOverflowCheck(new PackedValue(arrayOfBigInteger), this.packedWire);
/*  882 */       if (bool) {
/*  883 */         b.getBackInRange(false);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  888 */       PackedValue packedValue = (new CustomLongFieldDivGadget(this.packedWire, 
/*  889 */           b.packedWire, new PackedValue(Util.split(this.modulus, 
/*  890 */               UnsignedInteger.BITWIDTH_PER_CHUNK)), new String[0]))
/*  891 */         .getResult();
/*      */ 
/*      */       
/*  894 */       GroupElement groupElement = new GroupElement(this.modulus, packedValue, 
/*  895 */           Util.computeMaxValue(this.modulus.bitLength()));
/*      */       
/*  897 */       return groupElement;
/*      */     } 
/*  899 */     BigInteger aux = Util.computeMaxValue(this.modulus.bitLength());
/*  900 */     BigInteger[] auxChunks = { aux };
/*  901 */     boolean overflowCheck = b.packedWire
/*  902 */       .mulAddOverflowCheck(new PackedValue(auxChunks), this.packedWire);
/*  903 */     if (overflowCheck) {
/*  904 */       b.getBackInRange(false);
/*      */     }
/*  906 */     PackedValue v = (new CustomShortFieldDivGadget(this.packedWire, 
/*  907 */         b.packedWire, new PackedValue(
/*  908 */           new BigInteger[] { this.modulus }, ), new String[0])).getResult();
/*      */ 
/*      */     
/*  911 */     GroupElement result = new GroupElement(this.modulus, v, 
/*  912 */         Util.computeMaxValue(this.modulus.bitLength()));
/*  913 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public GroupElement inv() {
/*  920 */     if (isConstant())
/*      */     {
/*  922 */       return new GroupElement(this.modulus, getConstant().modInverse(
/*  923 */             this.modulus));
/*      */     }
/*      */     
/*  926 */     if (this.generator.__getPhase() == 0) {
/*      */       
/*  928 */       this.variableState.setPackedAhead(true);
/*  929 */       int idx1 = getVariableState().getMulIndex();
/*      */       
/*  931 */       if (idx1 == 1 && 
/*  932 */         this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  933 */         this.variableState.setConditionallySplittedAhead(true);
/*      */       }
/*      */       
/*  936 */       GroupElement groupElement = new GroupElement(this.generator, this.modulus);
/*      */ 
/*      */       
/*  939 */       return groupElement;
/*      */     } 
/*  941 */     if (this.nativeSnarkField) {
/*  942 */       PackedValue.disableOverflowChecks = true;
/*  943 */       FieldDivisionGadget f = new FieldDivisionGadget(
/*  944 */           this.generator.__getOneWire(), this.packedWire.array[0], new String[0]);
/*  945 */       PackedValue.disableOverflowChecks = false;
/*      */       
/*  947 */       return new GroupElement(this.modulus, 
/*  948 */           new PackedValue(f.getOutputWires()[0], this.modulus
/*  949 */             .subtract(BigInteger.ONE)), 
/*  950 */           this.modulus.subtract(BigInteger.ONE));
/*      */     } 
/*      */     
/*  953 */     if (this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*      */       
/*  955 */       BigInteger bigInteger = Util.computeMaxValue(this.modulus.bitLength());
/*  956 */       BigInteger[] arrayOfBigInteger = Util.split(bigInteger, 
/*  957 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  958 */       boolean bool = this.packedWire
/*  959 */         .mulOverflowCheck(new PackedValue(arrayOfBigInteger));
/*  960 */       if (bool) {
/*  961 */         getBackInRange(false);
/*      */       }
/*  963 */       PackedValue packedValue = (new InverseLongIntegerModGadget(this.packedWire, 
/*  964 */           new PackedValue(Util.split(this.modulus, 
/*  965 */               UnsignedInteger.BITWIDTH_PER_CHUNK)), new String[0]))
/*  966 */         .getInverse();
/*      */ 
/*      */       
/*  969 */       GroupElement groupElement = new GroupElement(this.modulus, packedValue, 
/*  970 */           Util.computeMaxValue(this.modulus.bitLength()));
/*  971 */       return groupElement;
/*      */     } 
/*  973 */     BigInteger aux = Util.computeMaxValue(this.modulus.bitLength());
/*  974 */     BigInteger[] auxChunks = { aux };
/*  975 */     boolean overflowCheck = this.packedWire
/*  976 */       .mulOverflowCheck(new PackedValue(auxChunks));
/*  977 */     if (overflowCheck) {
/*  978 */       getBackInRange(false);
/*      */     }
/*  980 */     PackedValue v = (new ShortIntegerModGadget(this.packedWire, 
/*  981 */         new PackedValue(new BigInteger[] { this.modulus
/*  982 */           }, ), new String[0])).getInverse();
/*      */ 
/*      */     
/*  985 */     GroupElement result = new GroupElement(this.modulus, v, 
/*  986 */         Util.computeMaxValue(this.modulus.bitLength()));
/*  987 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public WireArray getBitWires() {
/*  993 */     if (this.generator.__getPhase() == 0) {
/*  994 */       this.variableState.setSplittedAhead(true);
/*  995 */       Wire[] result = new Wire[this.modulus.bitLength()];
/*  996 */       Arrays.fill((Object[])result, new BitWire(-1));
/*  997 */       return new WireArray(result);
/*      */     } 
/*  999 */     return this.bitWires.adjustLength(this.modulus.bitLength());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Bit[] getBitElements() {
/* 1007 */     if (this.generator.__getPhase() == 0) {
/* 1008 */       this.variableState.setSplittedAhead(true);
/* 1009 */       Bit[] arrayOfBit = new Bit[this.modulus.bitLength()];
/* 1010 */       if (this.constant == null) {
/* 1011 */         Arrays.fill((Object[])arrayOfBit, new Bit(new Wire(-1)));
/*      */       } else {
/* 1013 */         for (int j = 0; j < this.modulus.bitLength(); j++) {
/* 1014 */           boolean b = this.constant.testBit(j);
/* 1015 */           Wire w = b ? this.generator.__getOneWire() : this.generator
/* 1016 */             .__getZeroWire();
/* 1017 */           arrayOfBit[j] = new Bit(w);
/*      */         } 
/*      */       } 
/* 1020 */       return arrayOfBit;
/*      */     } 
/* 1022 */     Bit[] result = new Bit[this.modulus.bitLength()];
/* 1023 */     WireArray array = this.bitWires.adjustLength(this.modulus.bitLength());
/* 1024 */     for (int i = 0; i < this.modulus.bitLength(); i++) {
/* 1025 */       result[i] = new Bit(array.get(i));
/*      */     }
/*      */ 
/*      */     
/* 1029 */     return result;
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
/* 1042 */     return (this.maxValue.compareTo(this.modulus) >= 0);
/*      */   }
/*      */ 
/*      */   
/*      */   private void getBackInRange(boolean strict) {
/* 1047 */     if (this.modulus != null && !this.nativeSnarkField) {
/*      */       
/* 1049 */       if (this.maxValue.compareTo(this.modulus) >= 0 && this.maxValue.compareTo(Util.computeMaxValue(this.modulus.bitLength())) <= 0 && this.packedWire.isWitness()) {
/* 1050 */         PackedValue modValue = new PackedValue(Util.split(this.modulus, 
/* 1051 */               UnsignedInteger.BITWIDTH_PER_CHUNK));
/* 1052 */         this.generator.__addOneAssertion(this.packedWire.isLessThan(modValue, this.maxValue.bitLength()).getWire(), new String[0]);
/*      */       }
/* 1054 */       else if (this.maxValue.compareTo(this.modulus) >= 0 && 
/* 1055 */         this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/* 1056 */         ModConstantGadget modConstantGadget = new ModConstantGadget(this.packedWire.array[0], 
/* 1057 */             this.maxValue.bitLength(), this.modulus, strict, new String[0]);
/*      */         
/* 1059 */         this.packedWire = new PackedValue(modConstantGadget.getOutputWires()[0], 
/* 1060 */             this.maxValue);
/*      */       
/*      */       }
/* 1063 */       else if (this.maxValue.compareTo(this.modulus) >= 0 && 
/* 1064 */         this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*      */         
/* 1066 */         PackedValue modValue = new PackedValue(Util.split(this.modulus, 
/* 1067 */               UnsignedInteger.BITWIDTH_PER_CHUNK));
/* 1068 */         LongIntegerModConstantGadget g = new LongIntegerModConstantGadget(
/* 1069 */             this.packedWire, modValue, strict, new String[0]);
/* 1070 */         this.packedWire = g.getRemainder();
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1077 */       if (strict) {
/* 1078 */         this.maxValue = this.modulus.subtract(BigInteger.ONE);
/* 1079 */         this.currentBitwidth = this.maxValue.bitLength();
/*      */       } else {
/* 1081 */         this.maxValue = Util.computeMaxValue(this.modulus.bitLength());
/* 1082 */         this.currentBitwidth = this.maxValue.bitLength();
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
/* 1104 */     return (this.constant != null);
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
/* 1116 */     if (this.packedWire == null && this.generator.__getPhase() == 0) {
/* 1117 */       this.variableState.setPackedAhead(true);
/* 1118 */       return new PackedValue(new Wire(-1), this.modulus.bitLength());
/*      */     } 
/* 1120 */     return this.packedWire;
/*      */   }
/*      */   
/*      */   public BigInteger getMaxValue() {
/* 1124 */     return this.maxValue;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getRequiredBitWidth() {
/* 1129 */     return this.modulus.bitLength();
/*      */   }
/*      */   
/*      */   public BigInteger getModulus() {
/* 1133 */     return this.modulus;
/*      */   }
/*      */ 
/*      */   
/*      */   public int getCurrentBitWidth() {
/* 1138 */     return this.currentBitwidth;
/*      */   }
/*      */ 
/*      */   
/*      */   public Wire[] toWires() {
/* 1143 */     if (this.packedWire != null) {
/* 1144 */       return this.packedWire.getArray();
/*      */     }
/* 1146 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void mapValue(BigInteger value, CircuitEvaluator evaluator) {
/* 1152 */     if (this.packedWire != null) {
/*      */       
/* 1154 */       if (this.nativeSnarkField) {
/* 1155 */         evaluator.setWireValue(this.packedWire.array[0], value);
/*      */       } else {
/* 1157 */         evaluator.setWireValue(this.packedWire, value, this.modulus.bitLength(), 
/* 1158 */             UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */       } 
/*      */     } else {
/* 1161 */       int length = this.bitWires.size();
/* 1162 */       for (int i = 0; i < length; i++) {
/* 1163 */         evaluator.setWireValue(this.bitWires.get(i), 
/* 1164 */             value.testBit(i) ? BigInteger.ONE : BigInteger.ZERO);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public BigInteger getValueFromEvaluator(CircuitEvaluator evaluator) {
/* 1171 */     if (this.packedWire != null) {
/* 1172 */       return evaluator.getWireValue(this.packedWire, 
/* 1173 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */     }
/* 1175 */     BigInteger s = BigInteger.ZERO;
/* 1176 */     BigInteger powerTwo = BigInteger.ONE;
/* 1177 */     int length = this.bitWires.size();
/* 1178 */     for (int i = 0; i < length; i++) {
/* 1179 */       s = s.add(powerTwo.multiply(evaluator.getWireValue(this.bitWires
/* 1180 */               .get(i))));
/* 1181 */       powerTwo = powerTwo.add(powerTwo);
/*      */     } 
/* 1183 */     return s;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static GroupElement createInput(CircuitGenerator generator, BigInteger modulus, String... desc) {
/*      */     PackedValue v;
/* 1190 */     if (modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/* 1191 */       modulus.equals(Config.getFiniteFieldModulus())) {
/* 1192 */       Wire w = generator.__createInputWire(desc);
/* 1193 */       v = new PackedValue(w, modulus.bitLength());
/*      */     } else {
/* 1195 */       int numChunks = (int)Math.ceil(modulus.bitLength() * 1.0D / 
/* 1196 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1197 */       Wire[] w = generator.__createInputWireArray(numChunks, new String[0]);
/*      */       
/* 1199 */       int[] bitwidths = new int[numChunks];
/* 1200 */       Arrays.fill(bitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1201 */       if (numChunks * UnsignedInteger.BITWIDTH_PER_CHUNK != modulus
/* 1202 */         .bitLength()) {
/* 1203 */         bitwidths[numChunks - 1] = modulus.bitLength() % 
/* 1204 */           UnsignedInteger.BITWIDTH_PER_CHUNK;
/*      */       }
/* 1206 */       v = new PackedValue(w, bitwidths);
/*      */     } 
/*      */ 
/*      */     
/* 1210 */     GroupElement o = new GroupElement(modulus, v, 
/* 1211 */         modulus.subtract(BigInteger.ONE));
/* 1212 */     generator.__getInputAux().add(o.copy());
/* 1213 */     return o;
/*      */   }
/*      */ 
/*      */   
/*      */   public static GroupElement createWitness(CircuitGenerator generator, BigInteger modulus, String... desc) {
/*      */     PackedValue v;
/* 1219 */     if (modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/* 1220 */       modulus.equals(Config.getFiniteFieldModulus())) {
/* 1221 */       Wire w = generator.__createProverWitnessWire(desc);
/* 1222 */       v = new PackedValue(w, modulus.bitLength());
/*      */     } else {
/* 1224 */       int numChunks = (int)Math.ceil(modulus.bitLength() * 1.0D / 
/* 1225 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1226 */       Wire[] w = generator.__createProverWitnessWireArray(numChunks, new String[0]);
/* 1227 */       int[] bitwidths = new int[numChunks];
/* 1228 */       Arrays.fill(bitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1229 */       if (numChunks * UnsignedInteger.BITWIDTH_PER_CHUNK != modulus
/* 1230 */         .bitLength()) {
/* 1231 */         bitwidths[numChunks - 1] = modulus.bitLength() % 
/* 1232 */           UnsignedInteger.BITWIDTH_PER_CHUNK;
/*      */       }
/* 1234 */       v = new PackedValue(w, bitwidths);
/*      */     } 
/* 1236 */     GroupElement o = new GroupElement(modulus, v, 
/* 1237 */         modulus.subtract(BigInteger.ONE));
/* 1238 */     generator.__getProverAux().add(o.copy());
/* 1239 */     return o;
/*      */   }
/*      */ 
/*      */   
/*      */   public static GroupElement createVerifiedWitness(CircuitGenerator generator, BigInteger modulus, String... desc) {
/*      */     PackedValue v;
/* 1245 */     if (modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT || 
/* 1246 */       modulus.equals(Config.getFiniteFieldModulus())) {
/* 1247 */       Wire w = generator.__createProverWitnessWire(desc);
/* 1248 */       v = new PackedValue(w, modulus.bitLength());
/*      */     } else {
/* 1250 */       int numChunks = (int)Math.ceil(modulus.bitLength() * 1.0D / 
/* 1251 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1252 */       Wire[] w = generator.__createProverWitnessWireArray(numChunks, new String[0]);
/* 1253 */       int[] bitwidths = new int[numChunks];
/* 1254 */       Arrays.fill(bitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1255 */       if (numChunks * UnsignedInteger.BITWIDTH_PER_CHUNK != modulus
/* 1256 */         .bitLength()) {
/* 1257 */         bitwidths[numChunks - 1] = modulus.bitLength() % 
/* 1258 */           UnsignedInteger.BITWIDTH_PER_CHUNK;
/*      */       }
/* 1260 */       v = new PackedValue(w, bitwidths);
/*      */     } 
/* 1262 */     GroupElement o = new GroupElement(modulus, v, 
/* 1263 */         modulus.subtract(BigInteger.ONE));
/* 1264 */     generator.__getVerifiedProverAux().add(o.copy());
/* 1265 */     return o;
/*      */   }
/*      */   
/*      */   public void verifyRange() {
/* 1269 */     if (this.modulus.equals(Config.getFiniteFieldModulus())) {
/*      */       return;
/*      */     }
/*      */     
/* 1273 */     if (this.modulus.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/* 1274 */       this.packedWire.array[0].restrictBitLength(this.modulus.bitLength(), new String[0]);
/* 1275 */       this.generator.__addOneAssertion(this.packedWire.array[0].isLessThan(
/* 1276 */             this.modulus, this.modulus.bitLength() + 1, new String[0]), new String[0]);
/*      */     } else {
/*      */       
/* 1279 */       int numChunks = (int)Math.ceil(this.modulus.bitLength() * 1.0D / 
/* 1280 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1281 */       int[] bitwidths = new int[numChunks];
/* 1282 */       Arrays.fill(bitwidths, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1283 */       if (numChunks * UnsignedInteger.BITWIDTH_PER_CHUNK != this.modulus
/* 1284 */         .bitLength()) {
/* 1285 */         bitwidths[numChunks - 1] = this.modulus.bitLength() % 
/* 1286 */           UnsignedInteger.BITWIDTH_PER_CHUNK;
/*      */       }
/* 1288 */       for (int i = 0; i < numChunks; i++) {
/* 1289 */         this.packedWire.array[i]
/* 1290 */           .restrictBitLength(bitwidths[numChunks - 1], new String[0]);
/*      */       }
/* 1292 */       this.generator.__addOneAssertion((this.packedWire.isLessThan(
/* 1293 */             new PackedValue(Util.split(this.modulus, 
/* 1294 */                 UnsignedInteger.BITWIDTH_PER_CHUNK)), this.modulus
/* 1295 */             .bitLength() + 1)).wire, new String[0]);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static GroupElement[] createZeroArray(CircuitGenerator generator, int size, BigInteger modulus, String... desc) {
/* 1302 */     GroupElement[] out = new GroupElement[size];
/* 1303 */     for (int i = 0; i < size; i++) {
/* 1304 */       out[i] = new GroupElement(modulus, BigInteger.ZERO);
/*      */     }
/* 1306 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createZeroArray(CircuitGenerator generator, int[] dims, BigInteger modulus, String... desc) {
/* 1313 */     if (dims.length == 1)
/* 1314 */       return createZeroArray(generator, dims[0], modulus, desc); 
/* 1315 */     if (dims.length == 2) {
/* 1316 */       GroupElement[][] out = new GroupElement[dims[0]][];
/* 1317 */       for (int i = 0; i < dims[0]; i++) {
/* 1318 */         out[i] = createZeroArray(generator, dims[1], modulus, desc);
/*      */       }
/* 1320 */       return out;
/* 1321 */     }  if (dims.length == 3) {
/* 1322 */       GroupElement[][][] out = new GroupElement[dims[0]][dims[1]][];
/* 1323 */       for (int i = 0; i < dims[0]; i++) {
/* 1324 */         for (int j = 0; j < dims[1]; j++) {
/* 1325 */           out[i][j] = createZeroArray(generator, dims[2], modulus, 
/* 1326 */               desc);
/*      */         }
/*      */       } 
/* 1329 */       return out;
/*      */     } 
/* 1331 */     throw new IllegalArgumentException(
/* 1332 */         "Initialization of higher dim arrays not supported at this point");
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
/*      */   public static GroupElement[] createInputArray(CircuitGenerator generator, int size, BigInteger modulus, String... desc) {
/* 1347 */     GroupElement[] out = new GroupElement[size];
/* 1348 */     for (int i = 0; i < size; i++) {
/* 1349 */       out[i] = createInput(generator, modulus, desc);
/*      */     }
/* 1351 */     return out;
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
/*      */   public static GroupElement[] createWitnessArray(CircuitGenerator generator, int size, BigInteger modulus, String... desc) {
/* 1366 */     GroupElement[] out = new GroupElement[size];
/* 1367 */     for (int i = 0; i < size; i++) {
/* 1368 */       out[i] = createWitness(generator, modulus, desc);
/*      */     }
/* 1370 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static GroupElement[] createVerifiedWitnessArray(CircuitGenerator generator, int size, BigInteger modulus, String... desc) {
/* 1376 */     GroupElement[] out = new GroupElement[size];
/* 1377 */     for (int i = 0; i < size; i++) {
/* 1378 */       out[i] = createVerifiedWitness(generator, modulus, desc);
/*      */     }
/* 1380 */     return out;
/*      */   }
/*      */ 
/*      */   
/*      */   public void makeOutput(String... desc) {
/* 1385 */     if (this.generator.__getPhase() == 0) {
/* 1386 */       this.variableState.setPackedAhead(true);
/* 1387 */       this.variableState.setConditionallySplittedAhead(true);
/* 1388 */       this.variableState.setMustBeWithinRange(true);
/* 1389 */       this.variableState.setConditionallySplittedAndAlignedAhead(true);
/*      */     
/*      */     }
/*      */     else {
/*      */       
/* 1394 */       this.generator.__makeOutputArray(this.packedWire.array, desc);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static void makeOutput(CircuitGenerator generator, GroupElement x, String... desc) {
/* 1401 */     x.makeOutput(new String[0]);
/*      */   } public static void makeOutput(CircuitGenerator generator, GroupElement[] a, String... desc) {
/*      */     byte b;
/*      */     int i;
/*      */     GroupElement[] arrayOfGroupElement;
/* 1406 */     for (i = (arrayOfGroupElement = a).length, b = 0; b < i; ) { GroupElement x = arrayOfGroupElement[b];
/* 1407 */       x.makeOutput(new String[0]);
/*      */       b++; }
/*      */   
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createInputArray(CircuitGenerator generator, int[] dims, BigInteger modulus, String... desc) {
/* 1415 */     if (dims.length == 1)
/* 1416 */       return createInputArray(generator, dims[0], modulus, desc); 
/* 1417 */     if (dims.length == 2) {
/* 1418 */       GroupElement[][] out = new GroupElement[dims[0]][];
/* 1419 */       for (int i = 0; i < dims[0]; i++) {
/* 1420 */         out[i] = createInputArray(generator, dims[1], modulus, desc);
/*      */       }
/* 1422 */       return out;
/* 1423 */     }  if (dims.length == 3) {
/* 1424 */       GroupElement[][][] out = new GroupElement[dims[0]][dims[1]][];
/* 1425 */       for (int i = 0; i < dims[0]; i++) {
/* 1426 */         for (int j = 0; j < dims[1]; j++) {
/* 1427 */           out[i][j] = createInputArray(generator, dims[2], modulus, 
/* 1428 */               desc);
/*      */         }
/*      */       } 
/* 1431 */       return out;
/*      */     } 
/* 1433 */     throw new IllegalArgumentException(
/* 1434 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createWitnessArray(CircuitGenerator generator, int[] dims, BigInteger modulus, String... desc) {
/* 1441 */     if (dims.length == 1)
/* 1442 */       return createWitnessArray(generator, dims[0], modulus, desc); 
/* 1443 */     if (dims.length == 2) {
/* 1444 */       GroupElement[][] out = new GroupElement[dims[0]][];
/* 1445 */       for (int i = 0; i < dims[0]; i++) {
/* 1446 */         out[i] = createWitnessArray(generator, dims[1], modulus, desc);
/*      */       }
/* 1448 */       return out;
/* 1449 */     }  if (dims.length == 3) {
/* 1450 */       GroupElement[][][] out = new GroupElement[dims[0]][dims[1]][];
/* 1451 */       for (int i = 0; i < dims[0]; i++) {
/* 1452 */         for (int j = 0; j < dims[1]; j++) {
/* 1453 */           out[i][j] = createWitnessArray(generator, dims[2], modulus, 
/* 1454 */               desc);
/*      */         }
/*      */       } 
/* 1457 */       return out;
/*      */     } 
/* 1459 */     throw new IllegalArgumentException(
/* 1460 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object createVerifiedWitnessArray(CircuitGenerator generator, int[] dims, BigInteger modulus, String... desc) {
/* 1468 */     if (dims.length == 1)
/* 1469 */       return createVerifiedWitnessArray(generator, dims[0], modulus, desc); 
/* 1470 */     if (dims.length == 2) {
/* 1471 */       GroupElement[][] out = new GroupElement[dims[0]][];
/* 1472 */       for (int i = 0; i < dims[0]; i++) {
/* 1473 */         out[i] = createVerifiedWitnessArray(generator, dims[1], modulus, desc);
/*      */       }
/* 1475 */       return out;
/* 1476 */     }  if (dims.length == 3) {
/* 1477 */       GroupElement[][][] out = new GroupElement[dims[0]][dims[1]][];
/* 1478 */       for (int i = 0; i < dims[0]; i++) {
/* 1479 */         for (int j = 0; j < dims[1]; j++) {
/* 1480 */           out[i][j] = createVerifiedWitnessArray(generator, dims[2], modulus, 
/* 1481 */               desc);
/*      */         }
/*      */       } 
/* 1484 */       return out;
/*      */     } 
/* 1486 */     throw new IllegalArgumentException(
/* 1487 */         "Initialization of higher dimensional arrays as inputs not supported at this point. Only 3 dimensions are supported");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void makeOutput(CircuitGenerator generator, Object a, String... desc) {
/* 1495 */     if (a instanceof GroupElement[]) {
/* 1496 */       GroupElement[] array = (GroupElement[])a;
/* 1497 */       for (int i = 0; i < array.length; i++) {
/* 1498 */         makeOutput(generator, array[i], desc);
/*      */       }
/* 1500 */     } else if (a instanceof GroupElement[][]) {
/* 1501 */       GroupElement[][] array = (GroupElement[][])a;
/* 1502 */       for (int i = 0; i < array.length; i++) {
/* 1503 */         makeOutput(generator, array[i], desc);
/*      */       }
/* 1505 */     } else if (a instanceof GroupElement[][][]) {
/* 1506 */       GroupElement[][][] array = (GroupElement[][][])a;
/* 1507 */       for (int i = 0; i < array.length; i++) {
/* 1508 */         makeOutput(generator, array[i], desc);
/*      */       }
/*      */     } else {
/* 1511 */       throw new IllegalArgumentException("Declaring higher dimensional arrays as outputs not supported at this point. Only 3 dimensions are supported");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void mapRandomValue(CircuitEvaluator evaluator) {
/* 1521 */     BigInteger rnd = Util.nextRandomBigInteger(this.modulus);
/* 1522 */     if (this.packedWire != null) {
/*      */       
/* 1524 */       evaluator.setWireValue(this.packedWire, rnd, this.modulus.bitLength(), 
/* 1525 */           UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */     } else {
/*      */       
/* 1528 */       int length = this.bitWires.size();
/* 1529 */       for (int i = 0; i < length; i++) {
/* 1530 */         evaluator.setWireValue(this.bitWires.get(i), 
/* 1531 */             rnd.testBit(i) ? BigInteger.ONE : BigInteger.ZERO);
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
/*      */   public GroupElement mul(Bit bit) {
/* 1578 */     return mul(new GroupElement(this.modulus, new PackedValue(bit.wire, 
/* 1579 */             BigInteger.ONE), BigInteger.ONE));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void forceEqual(IAuxType o) {
/* 1586 */     if (!(o instanceof GroupElement)) {
/* 1587 */       throw new IllegalArgumentException("FieldType expected");
/*      */     }
/*      */     
/* 1590 */     GroupElement other = (GroupElement)o;
/* 1591 */     if (getConstant() != null && other.getConstant() != null && 
/* 1592 */       !getConstant().equals(other.getConstant())) {
/* 1593 */       throw new RuntimeException("Constraint fails on constant uints");
/*      */     }
/*      */ 
/*      */     
/* 1597 */     if (this.generator.__getPhase() == 0) {
/* 1598 */       this.variableState.setPackedAhead(true);
/* 1599 */       other.variableState.setPackedAhead(true);
/* 1600 */       this.variableState.setConditionallySplittedAhead(true);
/* 1601 */       other.variableState.setConditionallySplittedAhead(true);
/* 1602 */       this.variableState.setMustBeWithinRange(true);
/* 1603 */       other.variableState.setMustBeWithinRange(true);
/*      */     
/*      */     }
/* 1606 */     else if (!this.nativeSnarkField && (
/* 1607 */       this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT || other.modulus
/* 1608 */       .bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT)) {
/* 1609 */       this.packedWire.forceEquality2(other.packedWire);
/*      */     } else {
/* 1611 */       this.generator.__addEqualityAssertion(this.packedWire.array[0], 
/* 1612 */           other.packedWire.array[0], new String[0]);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static Class<?> __getClassRef() {
/* 1618 */     return GroupElement.class;
/*      */   }
/*      */   
/*      */   public VariableState getState() {
/* 1622 */     return this.variableState;
/*      */   }
/*      */ 
/*      */   
/*      */   public PackedValue getPackedValue() {
/* 1627 */     return this.packedWire;
/*      */   }
/*      */ 
/*      */   
/*      */   public Bit isEqualTo(IAuxType o) {
/* 1632 */     if (!(o instanceof GroupElement)) {
/* 1633 */       throw new IllegalArgumentException("UnsignedINT expected");
/*      */     }
/* 1635 */     if (this == o) {
/* 1636 */       return new Bit(true);
/*      */     }
/* 1638 */     GroupElement other = (GroupElement)o;
/* 1639 */     if (getConstant() != null && other.getConstant() != null) {
/* 1640 */       return new Bit(getConstant().equals(other.getConstant()));
/*      */     }
/* 1642 */     if (this.generator.__getPhase() == 0) {
/* 1643 */       this.variableState.setPackedAhead(true);
/* 1644 */       other.variableState.setPackedAhead(true);
/* 1645 */       this.variableState.setConditionallySplittedAndAlignedAhead(true);
/* 1646 */       other.variableState.setConditionallySplittedAndAlignedAhead(true);
/*      */ 
/*      */       
/* 1649 */       return new Bit(new Wire(-1));
/*      */     } 
/* 1651 */     if (!this.nativeSnarkField && (
/* 1652 */       this.modulus.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT || other.modulus
/* 1653 */       .bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT)) {
/* 1654 */       return this.packedWire.isEqualTo(other.packedWire);
/*      */     }
/* 1656 */     return new Bit(
/* 1657 */         this.packedWire.array[0]
/* 1658 */         .isEqualTo(other.packedWire.array[0], new String[0]));
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public Bit isNotEqualTo(IAuxType o) {
/* 1664 */     return isEqualTo(o).inv();
/*      */   }
/*      */   
/*      */   public static GroupElement instantiateFrom(BigInteger modulus, int v) {
/* 1668 */     return new GroupElement(modulus, BigInteger.valueOf(v));
/*      */   }
/*      */   
/*      */   public static GroupElement instantiateFrom(BigInteger modulus, long v) {
/* 1672 */     return new GroupElement(modulus, BigInteger.valueOf(v));
/*      */   }
/*      */   
/*      */   public static GroupElement instantiateFrom(BigInteger modulus, BigInteger v) {
/* 1676 */     return new GroupElement(modulus, v);
/*      */   }
/*      */ 
/*      */   
/*      */   public static GroupElement instantiateFrom(BigInteger modulus, GroupElement e) {
/* 1681 */     if (modulus.equals(e.getModulus())) {
/* 1682 */       return e.copy();
/*      */     }
/* 1684 */     return instantiateFrom(modulus, UnsignedInteger.instantiateFrom(e.getModulus().bitLength(), e));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static GroupElement instantiateFrom(BigInteger modulus, UnsignedInteger e) {
/* 1691 */     if (e.isConstant()) {
/* 1692 */       return new GroupElement(modulus, e.getConstant());
/*      */     }
/* 1694 */     CircuitGenerator generator = 
/* 1695 */       CircuitGenerator.__getActiveCircuitGenerator();
/* 1696 */     if (generator.__getPhase() == 0) {
/* 1697 */       e.getState().setConditionallySplittedAhead(true);
/* 1698 */       e.getState().setMustBeWithinRange(true);
/* 1699 */       e.getState().setPackedAhead(true);
/* 1700 */       GroupElement res = new GroupElement(generator, modulus);
/*      */ 
/*      */       
/* 1703 */       return res;
/*      */     } 
/* 1705 */     if (!modulus.equals(Config.getFiniteFieldModulus())) {
/* 1706 */       if (e.maxValue.compareTo(modulus) >= 0 && 
/* 1707 */         e.maxValue.bitLength() <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/* 1708 */         ModConstantGadget modConstantGadget = new ModConstantGadget(
/* 1709 */             e.packedWire.array[0], e.maxValue.bitLength(), 
/* 1710 */             modulus, true, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1715 */         GroupElement groupElement1 = new GroupElement(modulus, new PackedValue(
/* 1716 */               modConstantGadget.getOutputWires()[0], 
/* 1717 */               modulus.subtract(BigInteger.ONE)));
/*      */ 
/*      */ 
/*      */         
/* 1721 */         return groupElement1;
/*      */       } 
/*      */       
/* 1724 */       if (e.maxValue.compareTo(modulus) >= 0 && 
/* 1725 */         e.maxValue.bitLength() > UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*      */         
/* 1727 */         PackedValue modValue = new PackedValue(Util.split(
/* 1728 */               modulus, UnsignedInteger.BITWIDTH_PER_CHUNK));
/* 1729 */         LongIntegerModConstantGadget g = new LongIntegerModConstantGadget(
/* 1730 */             e.packedWire, modValue, true, new String[0]);
/*      */         
/* 1732 */         PackedValue remainder = g.getRemainder();
/* 1733 */         BigInteger[] maxVals = new BigInteger[remainder.array.length];
/* 1734 */         Arrays.fill((Object[])maxVals, Util.computeMaxValue(UnsignedInteger.BITWIDTH_PER_CHUNK));
/* 1735 */         if (remainder.array.length * UnsignedInteger.BITWIDTH_PER_CHUNK != modulus
/* 1736 */           .bitLength()) {
/* 1737 */           maxVals[remainder.array.length - 1] = Util.computeMaxValue(modulus.bitLength() % 
/* 1738 */               UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */         }
/* 1740 */         if (maxVals.length == 1) {
/* 1741 */           maxVals[0] = modulus.subtract(BigInteger.ONE);
/*      */         }
/*      */         
/* 1744 */         GroupElement groupElement1 = new GroupElement(modulus, new PackedValue(
/* 1745 */               (g.getRemainder()).array, 
/* 1746 */               maxVals), modulus.subtract(BigInteger.ONE));
/*      */ 
/*      */ 
/*      */         
/* 1750 */         return groupElement1;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1757 */       GroupElement groupElement = new GroupElement(modulus, e.packedWire, e.maxValue);
/*      */ 
/*      */ 
/*      */       
/* 1761 */       return groupElement;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1767 */     Wire w = e.packedWire.array[0];
/* 1768 */     for (int i = 1; i < e.packedWire.array.length; i++) {
/* 1769 */       w = w.add(e.packedWire.array[i].mul(2L, i * UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]), new String[0]);
/*      */     }
/* 1771 */     BigInteger maxVal = modulus.subtract(BigInteger.ONE);
/* 1772 */     if (e.maxValue.compareTo(maxVal) < 0) {
/* 1773 */       maxVal = e.maxValue;
/*      */     }
/*      */     
/* 1776 */     GroupElement result = new GroupElement(modulus, new PackedValue(w, maxVal), maxVal);
/*      */ 
/*      */ 
/*      */     
/* 1780 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static GroupElement instantiateFrom(BigInteger modulus, String v) {
/* 1787 */     return new GroupElement(modulus, new BigInteger(v));
/*      */   }
/*      */   
/*      */   public static GroupElement[] instantiateFrom(BigInteger modulus, int[] v) {
/* 1791 */     GroupElement[] a = new GroupElement[v.length];
/* 1792 */     for (int i = 0; i < a.length; i++)
/* 1793 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1794 */     return a;
/*      */   }
/*      */   
/*      */   public static GroupElement[] instantiateFrom(BigInteger modulus, byte[] v) {
/* 1798 */     GroupElement[] a = new GroupElement[v.length];
/* 1799 */     for (int i = 0; i < a.length; i++)
/* 1800 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1801 */     return a;
/*      */   }
/*      */   
/*      */   public static GroupElement[] instantiateFrom(BigInteger modulus, long[] v) {
/* 1805 */     GroupElement[] a = new GroupElement[v.length];
/* 1806 */     for (int i = 0; i < a.length; i++)
/* 1807 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1808 */     return a;
/*      */   }
/*      */ 
/*      */   
/*      */   public static GroupElement[] instantiateFrom(BigInteger modulus, BigInteger[] v) {
/* 1813 */     GroupElement[] a = new GroupElement[v.length];
/* 1814 */     for (int i = 0; i < a.length; i++)
/* 1815 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1816 */     return a;
/*      */   }
/*      */   
/*      */   public static GroupElement[] instantiateFrom(BigInteger modulus, String[] v) {
/* 1820 */     GroupElement[] a = new GroupElement[v.length];
/* 1821 */     for (int i = 0; i < a.length; i++)
/* 1822 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1823 */     return a;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static GroupElement[] instantiateFrom(BigInteger modulus, GroupElement[] v) {
/* 1829 */     GroupElement[] a = new GroupElement[v.length];
/* 1830 */     for (int i = 0; i < a.length; i++)
/* 1831 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1832 */     return a;
/*      */   }
/*      */   
/*      */   public static GroupElement[] instantiateFrom(BigInteger modulus, UnsignedInteger[] v) {
/* 1836 */     GroupElement[] a = new GroupElement[v.length];
/* 1837 */     for (int i = 0; i < a.length; i++)
/* 1838 */       a[i] = instantiateFrom(modulus, v[i]); 
/* 1839 */     return a;
/*      */   }
/*      */   
/*      */   public boolean isNativeSnarkField() {
/* 1843 */     return this.nativeSnarkField;
/*      */   }
/*      */   
/*      */   public GroupElement() {}
/*      */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\GroupElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */