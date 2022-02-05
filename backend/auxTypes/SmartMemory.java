/*      */ package backend.auxTypes;
/*      */ 
/*      */ import backend.config.Config;
/*      */ import backend.eval.CircuitEvaluator;
/*      */ import backend.eval.Instruction;
/*      */ import backend.structure.CircuitGenerator;
/*      */ import backend.structure.Wire;
/*      */ import backend.structure.WireArray;
/*      */ import java.lang.reflect.Method;
/*      */ import java.math.BigInteger;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import util.Util;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class SmartMemory<T>
/*      */ {
/*   26 */   private long numOfVariableReads = 0L;
/*   27 */   private long numOfVariableWrites = 0L;
/*   28 */   private long numOfInitialConstReads = 0L;
/*   29 */   private long numOfConstReads = 0L;
/*   30 */   private long numOfInitialConstWrites = 0L;
/*   31 */   private long numOfConstWrites = 0L;
/*   32 */   private long numOfCoditionalWrites = 0L;
/*   33 */   private int opCounter = 0;
/*      */   
/*      */   private boolean randAccessOccured = false;
/*      */   
/*      */   private boolean readOnly = false;
/*      */   
/*      */   private T[] elements;
/*      */   
/*      */   private int numElements;
/*      */   
/*      */   private Class<?> typeClass;
/*      */   
/*      */   private Object[] typeArgs;
/*      */   
/*      */   private CircuitGenerator generator;
/*      */   private T previousReadElement;
/*      */   private T previousWrittenElement;
/*      */   private UnsignedInteger previousReadIdx;
/*      */   private UnsignedInteger previousWriteIdx;
/*      */   private boolean dirty = false;
/*      */   private int conditionalScopeId;
/*      */   private static final int NW_MODE = 1;
/*      */   private static final int LINEAR_MODE = 2;
/*      */   private static final int SQRT_MOsDE = 3;
/*   57 */   public static int globalMemoryCounter = 0;
/*      */   
/*   59 */   private int memoryIndex = 0;
/*      */   
/*      */   private MemoryState state;
/*      */   
/*      */   private BigInteger[] runtimeVals;
/*      */   
/*      */   private Wire opCounterWire;
/*      */   
/*      */   private ArrayList<Wire> operations;
/*      */   
/*      */   private ArrayList<Wire> opCounters;
/*      */   
/*      */   private ArrayList<Wire> indexesToOperate;
/*      */   private ArrayList<PackedValue> data;
/*      */   private ArrayList<PackedValue> conditionalWitnessVals;
/*      */   private ArrayList<UnsignedInteger> indexes;
/*      */   private boolean isEmpty;
/*      */   private T defaultElement;
/*      */   private HashMap<MemoryCacheRecord, T> cachedAccesses;
/*      */   private HashSet<MemoryCacheRecord> cachedWrites;
/*      */   private PackedValue defaultValue;
/*      */   
/*      */   public static class MemoryState
/*      */   {
/*      */     private int mode;
/*      */     private int packingOption;
/*      */     private int indexBitsSplitted;
/*      */     private HashSet<Integer> redundantWrites;
/*      */     private boolean readOnly = false;
/*   88 */     public int bitCount = 0;
/*      */     public ArrayList<BigInteger[]> allCoeffSet;
/*      */     public int maxBitwidth;
/*      */     
/*      */     public int getMode() {
/*   93 */       return this.mode;
/*      */     }
/*      */     
/*      */     public int getPackingOption() {
/*   97 */       return this.packingOption;
/*      */     }
/*      */     
/*      */     public int getIndexBitsSplitted() {
/*  101 */       return this.indexBitsSplitted;
/*      */     }
/*      */     
/*      */     public void recordRedundantWire(int op) {
/*  105 */       if (this.redundantWrites == null) {
/*  106 */         this.redundantWrites = new HashSet<>();
/*      */       }
/*  108 */       this.redundantWrites.add(Integer.valueOf(op));
/*      */     }
/*      */   }
/*      */   
/*      */   private static class MemoryConsistencyRecord implements Comparable<MemoryConsistencyRecord> {
/*      */     private BigInteger op;
/*      */     private BigInteger index;
/*      */     private BigInteger counter;
/*      */     private BigInteger[] data;
/*      */     
/*      */     private MemoryConsistencyRecord() {}
/*      */     
/*      */     public int compareTo(MemoryConsistencyRecord o) {
/*  121 */       MemoryConsistencyRecord record = o;
/*  122 */       if (this.index.compareTo(record.index) < 0)
/*  123 */         return -1; 
/*  124 */       if (this.index.compareTo(record.index) > 0) {
/*  125 */         return 1;
/*      */       }
/*  127 */       return this.counter.compareTo(record.counter);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class ArrayIndexComparator
/*      */     implements Comparator<Integer> {
/*      */     private final SmartMemory.MemoryConsistencyRecord[] array;
/*      */     
/*      */     public ArrayIndexComparator(SmartMemory.MemoryConsistencyRecord[] array) {
/*  136 */       this.array = array;
/*      */     }
/*      */     
/*      */     public Integer[] createIndexArray() {
/*  140 */       Integer[] indexes = new Integer[this.array.length];
/*  141 */       for (int i = 0; i < this.array.length; i++) {
/*  142 */         indexes[i] = Integer.valueOf(i);
/*      */       }
/*  144 */       return indexes;
/*      */     }
/*      */ 
/*      */     
/*      */     public int compare(Integer index1, Integer index2) {
/*  149 */       return this.array[index1.intValue()].compareTo(this.array[index2.intValue()]);
/*      */     }
/*      */   }
/*      */   
/*      */   private static class MemoryCacheRecord {
/*      */     private UnsignedInteger idx;
/*      */     private int serialCounter;
/*      */     
/*      */     public MemoryCacheRecord(UnsignedInteger idx, int internalStateSerial) {
/*  158 */       this.idx = idx;
/*  159 */       this.serialCounter = internalStateSerial;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     public int hashCode() {
/*  165 */       return this.idx.hashCode() + this.serialCounter;
/*      */     }
/*      */     
/*      */     public boolean equals(Object o) {
/*  169 */       if (this == o)
/*  170 */         return true; 
/*  171 */       if (o instanceof MemoryCacheRecord) {
/*  172 */         MemoryCacheRecord r = (MemoryCacheRecord)o;
/*  173 */         return (r.idx == this.idx && r.serialCounter == this.serialCounter);
/*      */       } 
/*  175 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public SmartMemory(int n, T defaultElement, Class<?> typeClass, Object[] typeArgs) {
/*  182 */     if (n < 0)
/*  183 */       throw new IllegalArgumentException(
/*  184 */           "Array length must be a positive number!"); 
/*  185 */     this.numElements = n;
/*  186 */     this.readOnly = false;
/*  187 */     this.defaultElement = defaultElement;
/*      */     
/*  189 */     this.typeClass = typeClass;
/*  190 */     this.typeArgs = typeArgs;
/*  191 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*      */     
/*  193 */     this.conditionalScopeId = 0;
/*      */ 
/*      */     
/*  196 */     this.memoryIndex = globalMemoryCounter++;
/*  197 */     this.cachedAccesses = new HashMap<>();
/*  198 */     this.cachedWrites = new HashSet<>();
/*      */     
/*  200 */     if (this.generator.__getPhase() == 0) {
/*  201 */       this.state = new MemoryState();
/*  202 */       this.generator.__getMemoryList().add(this);
/*  203 */       this.generator.__getMemoryStateTable().put(Integer.valueOf(this.memoryIndex), this.state);
/*  204 */       if (defaultElement != null && 
/*  205 */         IAuxType.class.isAssignableFrom(typeClass)) {
/*  206 */         IAuxType t = (IAuxType)defaultElement;
/*  207 */         t.getState().setMustBeWithinRange(true);
/*  208 */         t.getState().setConditionallySplittedAndAlignedAhead(true);
/*  209 */         t.getState().setPackedAhead(true);
/*      */       
/*      */       }
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */       
/*  217 */       this.generator.__getMemoryList().add(this);
/*  218 */       this.state = (MemoryState)this.generator.__getMemoryStateTable().get(Integer.valueOf(this.memoryIndex));
/*  219 */       Object[] copiedArray = new Object[this.numElements];
/*      */       
/*  221 */       this.generator.__setUntrackedStateObjects(true);
/*  222 */       if (defaultElement == null) {
/*  223 */         defaultElement = constructDefaultElement();
/*      */       }
/*  225 */       for (int i = 0; i < this.numElements; i++) {
/*  226 */         if (typeClass == UnsignedInteger.class || 
/*  227 */           typeClass == Bit.class || 
/*  228 */           typeClass == FieldElement.class || typeClass == GroupElement.class) {
/*  229 */           copiedArray[i] = ((IAuxType)defaultElement).copy();
/*      */         } else {
/*  231 */           copiedArray[i] = ((RuntimeStruct)defaultElement)
/*  232 */             .____copy();
/*      */         } 
/*      */       } 
/*      */       
/*  236 */       this.generator.__setUntrackedStateObjects(false);
/*  237 */       this.elements = (T[])copiedArray;
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
/*      */   public SmartMemory(Object[] initialElements, char readOnly, Class<?> typeClass, Object[] typeArgs) {
/*  279 */     this.isEmpty = false;
/*  280 */     this.numElements = initialElements.length;
/*  281 */     this.elements = (T[])initialElements;
/*  282 */     this.readOnly = (readOnly == 's');
/*  283 */     this.typeClass = typeClass;
/*      */     
/*  285 */     this.typeArgs = typeArgs;
/*  286 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  287 */     this.conditionalScopeId = ConditionalScopeTracker.getCurrentScopeId();
/*  288 */     this.cachedAccesses = new HashMap<>();
/*  289 */     this.cachedWrites = new HashSet<>();
/*      */     
/*  291 */     Object[] copiedArray = new Object[this.numElements]; int i;
/*  292 */     for (i = 0; i < this.numElements; i++) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  297 */       if (IAuxType.class.isAssignableFrom(typeClass)) {
/*  298 */         copiedArray[i] = ((IAuxType)this.elements[i]).copy();
/*      */ 
/*      */       
/*      */       }
/*  302 */       else if (RuntimeStruct.class.isAssignableFrom(typeClass)) {
/*  303 */         copiedArray[i] = ((RuntimeStruct)this.elements[i]).____copy();
/*      */       } else {
/*      */         
/*  306 */         throw new UnsupportedOperationException(
/*  307 */             "Class type not supported ");
/*      */       } 
/*      */     } 
/*  310 */     this.elements = (T[])copiedArray;
/*      */     
/*  312 */     if (!this.readOnly) {
/*  313 */       if (this.generator.__getPhase() == 0) {
/*  314 */         if (IAuxType.class.isAssignableFrom(typeClass)) {
/*  315 */           for (i = 0; i < this.numElements; i++) {
/*  316 */             ((IAuxType)this.elements[i]).getState()
/*  317 */               .setMustBeWithinRange(true);
/*  318 */             ((IAuxType)this.elements[i]).getState()
/*  319 */               .setConditionallySplittedAndAlignedAhead(true);
/*  320 */             ((IAuxType)this.elements[i]).getState()
/*  321 */               .setPackedAhead(true);
/*      */           } 
/*  323 */         } else if (!RuntimeStruct.class.isAssignableFrom(typeClass)) {
/*      */ 
/*      */           
/*  326 */           throw new UnsupportedOperationException(
/*  327 */               "Class type not supportedds");
/*      */         }
/*      */       
/*      */       }
/*      */     }
/*  332 */     else if (this.generator.__getPhase() == 1) {
/*      */       
/*  334 */       if (IAuxType.class.isAssignableFrom(typeClass))
/*      */       {
/*      */         
/*  337 */         for (i = 0; i < this.numElements; i++) {
/*  338 */           if (!((IAuxType)this.elements[i]).isConstant()) {
/*  339 */             throw new IllegalArgumentException(
/*  340 */                 "In read only memory, all the contents must be constants");
/*      */           }
/*      */         } 
/*      */       }
/*      */     } 
/*      */ 
/*      */     
/*  347 */     this.memoryIndex = globalMemoryCounter++;
/*  348 */     if (this.generator.__getPhase() == 0) {
/*  349 */       this.state = new MemoryState();
/*  350 */       this.generator.__getMemoryList().add(this);
/*  351 */       this.generator.__getMemoryStateTable().put(Integer.valueOf(this.memoryIndex), this.state);
/*      */     } else {
/*  353 */       this.generator.__getMemoryList().add(this);
/*  354 */       this.state = (MemoryState)this.generator.__getMemoryStateTable().get(Integer.valueOf(this.memoryIndex));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public SmartMemory(Object[] initialElements, Class<?> typeClass, Object[] typeArgs) {
/*  364 */     this.isEmpty = false;
/*  365 */     this.numElements = initialElements.length;
/*  366 */     this.elements = (T[])initialElements;
/*  367 */     this.typeClass = typeClass;
/*      */     
/*  369 */     this.typeArgs = typeArgs;
/*  370 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  371 */     this.conditionalScopeId = ConditionalScopeTracker.getCurrentScopeId();
/*  372 */     this.cachedAccesses = new HashMap<>();
/*  373 */     this.cachedWrites = new HashSet<>();
/*      */     
/*  375 */     Object[] copiedArray = new Object[this.numElements]; int i;
/*  376 */     for (i = 0; i < this.numElements; i++) {
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  381 */       if (IAuxType.class.isAssignableFrom(typeClass)) {
/*  382 */         copiedArray[i] = ((IAuxType)this.elements[i]).copy();
/*      */ 
/*      */       
/*      */       }
/*  386 */       else if (RuntimeStruct.class.isAssignableFrom(typeClass)) {
/*  387 */         copiedArray[i] = ((RuntimeStruct)this.elements[i]).____copy();
/*      */       } else {
/*      */         
/*  390 */         throw new UnsupportedOperationException(
/*  391 */             "Class type not supported ");
/*      */       } 
/*      */     } 
/*  394 */     this.elements = (T[])copiedArray;
/*      */ 
/*      */     
/*  397 */     if (this.generator.__getPhase() == 0) {
/*  398 */       if (IAuxType.class.isAssignableFrom(typeClass)) {
/*  399 */         for (i = 0; i < this.numElements; i++) {
/*  400 */           ((IAuxType)this.elements[i]).getState()
/*  401 */             .setMustBeWithinRange(true);
/*  402 */           ((IAuxType)this.elements[i]).getState()
/*  403 */             .setConditionallySplittedAndAlignedAhead(true);
/*  404 */           ((IAuxType)this.elements[i]).getState()
/*  405 */             .setPackedAhead(true);
/*      */         } 
/*  407 */       } else if (!RuntimeStruct.class.isAssignableFrom(typeClass)) {
/*      */ 
/*      */         
/*  410 */         throw new UnsupportedOperationException(
/*  411 */             "Class type not supportedds");
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  433 */     this.memoryIndex = globalMemoryCounter++;
/*  434 */     if (this.generator.__getPhase() == 0) {
/*  435 */       this.state = new MemoryState();
/*  436 */       this.generator.__getMemoryList().add(this);
/*  437 */       this.generator.__getMemoryStateTable().put(Integer.valueOf(this.memoryIndex), this.state);
/*      */     } else {
/*  439 */       this.generator.__getMemoryList().add(this);
/*  440 */       this.state = (MemoryState)this.generator.__getMemoryStateTable().get(Integer.valueOf(this.memoryIndex));
/*  441 */       this.readOnly = this.state.readOnly;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void contructNetwork() {
/*  449 */     if (this.generator.__getPhase() == 0) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  455 */     this.runtimeVals = new BigInteger[this.numElements];
/*  456 */     if (IAuxType.class.isAssignableFrom(this.typeClass)) {
/*  457 */       PackedValue tmp = ((IAuxType)this.elements[0]).getPackedValue();
/*  458 */       for (int i = 1; i < this.numElements; i++) {
/*  459 */         PackedValue tmp2 = ((IAuxType)this.elements[i]).getPackedValue();
/*  460 */         if (!tmp2.equals(tmp)) {
/*  461 */           tmp = null;
/*      */           break;
/*      */         } 
/*      */       } 
/*  465 */       this.defaultValue = tmp;
/*  466 */     } else if (RuntimeStruct.class.isAssignableFrom(this.typeClass)) {
/*  467 */       PackedValue tmp = ((RuntimeStruct)this.elements[0]).ptrReference.packedWire;
/*  468 */       for (int i = 1; i < this.numElements; i++) {
/*  469 */         PackedValue tmp2 = ((RuntimeStruct)this.elements[i]).ptrReference.packedWire;
/*  470 */         if (!tmp2.equals(tmp)) {
/*  471 */           tmp = null;
/*      */           break;
/*      */         } 
/*      */       } 
/*  475 */       this.defaultValue = tmp;
/*      */     } 
/*      */     
/*  478 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*      */         {
/*      */           
/*      */           public void evaluate(CircuitEvaluator evaluator)
/*      */           {
/*  483 */             if (IAuxType.class.isAssignableFrom(SmartMemory.this.typeClass)) {
/*  484 */               for (int i = 0; i < SmartMemory.this.numElements; i++) {
/*  485 */                 SmartMemory.this.runtimeVals[i] = evaluator.getWireValue((
/*  486 */                     (IAuxType)SmartMemory.this.elements[i]).getPackedValue(), 
/*  487 */                     UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */               }
/*  489 */             } else if (RuntimeStruct.class.isAssignableFrom(SmartMemory.this.typeClass)) {
/*  490 */               for (int i = 0; i < SmartMemory.this.numElements; i++) {
/*  491 */                 SmartMemory.this.runtimeVals[i] = evaluator
/*  492 */                   .getWireValue(
/*  493 */                     ((RuntimeStruct)SmartMemory.this.elements[i]).ptrReference.packedWire, 
/*  494 */                     UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */               }
/*      */             } 
/*      */           }
/*      */         });
/*  499 */     this.conditionalWitnessVals = new ArrayList<>();
/*  500 */     this.opCounterWire = this.generator.__getZeroWire();
/*      */     
/*  502 */     this.opCounters = new ArrayList<>();
/*  503 */     this.operations = new ArrayList<>();
/*  504 */     this.data = new ArrayList<>();
/*  505 */     this.indexesToOperate = new ArrayList<>();
/*  506 */     if (this.defaultValue == null)
/*      */     {
/*  508 */       for (int i = 0; i < this.numElements; i++) {
/*  509 */         this.opCounters.add(this.opCounterWire);
/*  510 */         this.operations.add(this.generator.__getOneWire());
/*  511 */         this.indexesToOperate.add(this.generator.__createConstantWire(i, new String[0]));
/*  512 */         this.data.add(getPackedValue(this.elements[i]));
/*  513 */         this.opCounterWire = this.opCounterWire.add(1L, new String[0]);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   public T read(long idx) {
/*  520 */     return read(new UnsignedInteger(64, idx));
/*      */   }
/*      */   
/*      */   public void write(long idx, T element) {
/*  524 */     write(new UnsignedInteger(64, idx), element);
/*      */   }
/*      */ 
/*      */   
/*      */   public T read(UnsignedInteger idx) {
/*  529 */     this.opCounter++;
/*  530 */     this.cachedWrites.clear();
/*  531 */     MemoryCacheRecord cacheRecord = new MemoryCacheRecord(idx, 
/*  532 */         idx.internalStateSerial);
/*  533 */     if (this.cachedAccesses.containsKey(cacheRecord))
/*      */     {
/*  535 */       return this.cachedAccesses.get(cacheRecord);
/*      */     }
/*      */     
/*  538 */     if (idx.isConstant())
/*  539 */     { if (!this.randAccessOccured) {
/*  540 */         this.numOfInitialConstReads++;
/*      */       } else {
/*  542 */         this.numOfConstReads++;
/*      */       }  }
/*  544 */     else { this.numOfVariableReads++;
/*  545 */       this.randAccessOccured = true; }
/*      */ 
/*      */     
/*  548 */     if (this.indexes == null) {
/*  549 */       this.indexes = new ArrayList<>();
/*      */     }
/*  551 */     this.indexes.add(idx.copy());
/*      */     
/*  553 */     if (this.generator.__getPhase() == 0) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  561 */       idx.getState().setPackedAhead(true);
/*  562 */       idx.getState().setConditionallySplittedAhead(true);
/*      */       
/*  564 */       T t = null;
/*  565 */       t = constructDummyElement();
/*  566 */       this.cachedAccesses.put(cacheRecord, t);
/*  567 */       return t;
/*      */     } 
/*      */     
/*  570 */     T accessedElement = null;
/*  571 */     if (!this.state.readOnly) {
/*  572 */       if (idx.isConstant() && !this.randAccessOccured) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  579 */         accessedElement = constructFromExistingElement(this.elements[idx.constant
/*  580 */               .intValue()]);
/*      */       }
/*  582 */       else if (this.state.mode == 2) {
/*  583 */         accessedElement = readLinearMode(idx);
/*  584 */       } else if (this.state.mode == 1) {
/*  585 */         accessedElement = readNetworkMode(idx);
/*      */       
/*      */       }
/*      */     
/*      */     }
/*  590 */     else if (idx.isConstant()) {
/*  591 */       accessedElement = constructFromExistingElement(this.elements[idx.constant
/*  592 */             .intValue()]);
/*      */     } else {
/*  594 */       accessedElement = readReadOnlyMode(idx);
/*      */     } 
/*      */     
/*  597 */     this.cachedAccesses.put(cacheRecord, accessedElement);
/*  598 */     return accessedElement;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private T readReadOnlyMode(UnsignedInteger idx) {
/*  604 */     if (this.runtimeVals == null) {
/*  605 */       readFromInputArray();
/*      */     }
/*  607 */     final T output = constructWitnessElement();
/*  608 */     final UnsignedInteger idx2 = idx.copy();
/*  609 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*      */         {
/*      */ 
/*      */           
/*      */           public void evaluate(CircuitEvaluator evaluator)
/*      */           {
/*  615 */             BigInteger value = evaluator.getWireValue(idx2.packedWire.array[0]);
/*  616 */             int bitwidth = 1;
/*  617 */             if (SmartMemory.this.typeClass == UnsignedInteger.class) {
/*  618 */               bitwidth = Integer.parseInt((String)SmartMemory.this.typeArgs[0]);
/*  619 */             } else if (SmartMemory.this.typeClass == FieldElement.class || SmartMemory.this.typeClass == GroupElement.class) {
/*  620 */               bitwidth = (new BigInteger((String)SmartMemory.this.typeArgs[0])).bitLength();
/*      */             } 
/*      */             
/*  623 */             PackedValue p = SmartMemory.this.getPackedValue((T)output);
/*  624 */             if (value.intValue() >= SmartMemory.this.numElements) {
/*  625 */               evaluator.setWireValue(p, BigInteger.ZERO, bitwidth, 
/*  626 */                   UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */             } else {
/*  628 */               evaluator.setWireValue(p, SmartMemory.this.runtimeVals[value.intValue()], 
/*  629 */                   bitwidth, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */             } 
/*      */           }
/*      */         });
/*      */     
/*  634 */     int bitCount = this.state.bitCount;
/*      */     
/*  636 */     int indexBitwidth = (int)Math.ceil(Math.log(this.numElements) / 
/*  637 */         Math.log(2.0D));
/*  638 */     int outputBitwidth = 0; byte b; int j, arrayOfInt[];
/*  639 */     for (j = (arrayOfInt = getElementSize()).length, b = 0; b < j; ) { int x = arrayOfInt[b];
/*  640 */       outputBitwidth += x; b++; }
/*      */     
/*  642 */     int sqrtN = (int)Math.ceil(Math.sqrt(this.numElements));
/*  643 */     getPackedValue(output).forceBitwidth();
/*      */     
/*  645 */     Wire[] bitsIn = null, bitsOut = null;
/*      */     
/*  647 */     if (bitCount > 0) {
/*  648 */       bitsIn = idx.packedWire.getArray()[0].getBitWires(indexBitwidth, new String[0]).asArray();
/*  649 */       bitsOut = getPackedValue(output).getBits(outputBitwidth, UnsignedInteger.BITWIDTH_PER_CHUNK, new String[0]).asArray();
/*      */     } 
/*      */     
/*  652 */     Wire inputWire = idx.packedWire.getArray()[0];
/*  653 */     PackedValue outputPackedChunks = getPackedValue(output);
/*  654 */     Wire outputWire = (new WireArray(outputPackedChunks.array)).packWordsIntoLargerWords(UnsignedInteger.BITWIDTH_PER_CHUNK, outputPackedChunks.array.length, new String[0])[0];
/*      */     
/*  656 */     Wire[] vars = new Wire[sqrtN];
/*      */     
/*  658 */     Wire p = inputWire.mul(Util.computeBound(this.state.maxBitwidth), new String[0]).add(outputWire, new String[0]).add(1L, new String[0]);
/*  659 */     Wire currentProduct = p;
/*  660 */     if (bitCount != 0) {
/*  661 */       currentProduct = currentProduct.mul(currentProduct, new String[0]);
/*      */     }
/*  663 */     for (int i = 0; i < sqrtN; i++) {
/*  664 */       if (i < bitCount)
/*  665 */       { if (i < outputBitwidth) {
/*  666 */           vars[i] = bitsOut[i];
/*      */         } else {
/*  668 */           vars[i] = bitsIn[i - outputBitwidth];
/*      */         }  }
/*  670 */       else { vars[i] = currentProduct;
/*  671 */         if (i != sqrtN - 1) {
/*  672 */           currentProduct = currentProduct.mul(p, new String[0]);
/*      */         } }
/*      */     
/*      */     } 
/*  676 */     ArrayList<BigInteger[]> allCoeffSet = this.state.allCoeffSet;
/*  677 */     Wire product = this.generator.__getOneWire();
/*  678 */     for (BigInteger[] coeffs : allCoeffSet) {
/*  679 */       Wire accum = this.generator.__getZeroWire();
/*  680 */       for (int k = 0; k < vars.length; k++) {
/*  681 */         accum = accum.add(vars[k].mul(coeffs[k], new String[0]), new String[0]);
/*      */       }
/*  683 */       accum = accum.sub(1L, new String[0]);
/*  684 */       product = product.mul(accum, new String[0]);
/*      */     } 
/*  686 */     this.generator.__addZeroAssertion(product, new String[0]);
/*  687 */     return output;
/*      */   }
/*      */ 
/*      */   
/*      */   private void readFromInputArray() {
/*  692 */     BigInteger[] vals = new BigInteger[this.numElements];
/*  693 */     int maxBitwidth = 0;
/*  694 */     for (int i = 0; i < this.numElements; i++) {
/*  695 */       BigInteger c = null;
/*  696 */       if (IAuxType.class.isAssignableFrom(this.typeClass)) {
/*  697 */         c = ((IAuxType)this.elements[i]).getConstant();
/*  698 */       } else if (RuntimeStruct.class.isAssignableFrom(this.typeClass)) {
/*  699 */         c = ((RuntimeStruct)this.elements[i]).ptrReference.getConstant();
/*      */       } 
/*      */       
/*  702 */       vals[i] = c;
/*  703 */       int bitwidth = c.bitLength();
/*  704 */       if (bitwidth > maxBitwidth) {
/*  705 */         maxBitwidth = bitwidth;
/*      */       }
/*      */     } 
/*  708 */     this.runtimeVals = vals;
/*      */   }
/*      */ 
/*      */   
/*      */   private T readLinearMode(UnsignedInteger idx) {
/*  713 */     if (idx.isConstant()) {
/*  714 */       int v = idx.constant.intValue();
/*  715 */       if (v < 0 || v >= this.elements.length) {
/*  716 */         v = 0;
/*      */       }
/*  718 */       return constructFromExistingElement(this.elements[v]);
/*      */     } 
/*      */     
/*  721 */     Wire idxWire = idx.packedWire.array[0];
/*  722 */     int expectedElementArrayLength = (getElementSize()).length;
/*  723 */     PackedValue result = new PackedValue(Util.split(BigInteger.ZERO, 
/*  724 */           expectedElementArrayLength, UnsignedInteger.BITWIDTH_PER_CHUNK));
/*  725 */     if (IAuxType.class.isAssignableFrom(this.typeClass)) {
/*      */       
/*  727 */       for (int i = 0; i < this.numElements; i++) {
/*  728 */         Wire bitWire = idxWire.isEqualTo(i, new String[0]);
/*  729 */         result = result.muxBit((
/*  730 */             (IAuxType)this.elements[i]).getPackedValue(), bitWire);
/*      */       }
/*      */     
/*  733 */     } else if (RuntimeStruct.class.isAssignableFrom(this.typeClass)) {
/*      */ 
/*      */       
/*  736 */       for (int i = 0; i < this.numElements; i++) {
/*  737 */         this.generator.__addDebugInstruction(
/*  738 */             ((RuntimeStruct)this.elements[i]).ptrReference.copy(), new String[] {
/*  739 */               "Wire dduring linear" });
/*  740 */         Wire bitWire = idxWire.isEqualTo(i, new String[0]);
/*  741 */         result = result.muxBit(
/*  742 */             ((RuntimeStruct)this.elements[i]).ptrReference.packedWire, 
/*  743 */             bitWire);
/*      */       } 
/*      */     } 
/*      */     
/*  747 */     return constructFromPackedValue(result);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private T readNetworkMode(UnsignedInteger idx) {
/*      */     final UnsignedInteger idx2;
/*  754 */     if (idx.maxValue.compareTo(BigInteger.valueOf(this.numElements)) >= 0) {
/*  755 */       this.generator.__setUntrackedStateObjects(true);
/*  756 */       int idxBitlength = BigInteger.valueOf((this.numElements - 1)).bitLength();
/*  757 */       Wire w = null;
/*      */       
/*  759 */       if (idx.isConstant()) {
/*      */ 
/*      */         
/*  762 */         int v = idx.constant.intValue();
/*  763 */         if (v < 0 || v >= this.elements.length) {
/*  764 */           w = this.generator.__getZeroWire();
/*      */         } else {
/*  766 */           w = this.generator.__createConstantWire(idx.constant, new String[0]);
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */ 
/*      */         
/*  775 */         Wire isInRange = idx.packedWire.array[0].isLessThan(this.numElements, idx.maxValue.bitLength(), new String[0]);
/*  776 */         w = idx.packedWire.array[0].mul(isInRange, new String[0]);
/*      */       } 
/*  778 */       idx2 = new UnsignedInteger(idxBitlength, new PackedValue(w, 
/*  779 */             BigInteger.valueOf(this.numElements)));
/*      */       
/*  781 */       this.generator.__setUntrackedStateObjects(false);
/*      */     } else {
/*  783 */       idx2 = idx.copy();
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  788 */     if (this.runtimeVals == null) {
/*  789 */       contructNetwork();
/*      */     }
/*      */     
/*  792 */     final T readWitness = constructWitnessElement();
/*      */ 
/*      */     
/*  795 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*      */           public void evaluate(CircuitEvaluator evaluator)
/*      */           {
/*  804 */             BigInteger idxValue = evaluator.getWireValue(idx2.packedWire, 
/*  805 */                 UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/*  812 */             int bitwidth = 1;
/*  813 */             if (SmartMemory.this.typeClass == UnsignedInteger.class) {
/*  814 */               bitwidth = Integer.parseInt((String)SmartMemory.this.typeArgs[0]);
/*  815 */             } else if (SmartMemory.this.typeClass == FieldElement.class || SmartMemory.this.typeClass == GroupElement.class) {
/*  816 */               bitwidth = (new BigInteger((String)SmartMemory.this.typeArgs[0])).bitLength();
/*      */             } 
/*      */             
/*  819 */             PackedValue p = SmartMemory.this.getPackedValue((T)readWitness);
/*  820 */             if (idxValue.intValue() >= SmartMemory.this.numElements) {
/*  821 */               evaluator.setWireValue(p, SmartMemory.this.runtimeVals[0], bitwidth, 
/*  822 */                   UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */             } else {
/*      */               
/*  825 */               evaluator.setWireValue(p, SmartMemory.this.runtimeVals[idxValue.intValue()], 
/*  826 */                   bitwidth, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */             } 
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  836 */     this.opCounters.add(this.opCounterWire);
/*  837 */     this.operations.add(this.generator.__getZeroWire());
/*  838 */     this.indexesToOperate.add(idx2.packedWire.array[0]);
/*  839 */     this.data.add(getPackedValue(readWitness));
/*  840 */     this.opCounterWire = this.opCounterWire.add(1L, new String[0]);
/*  841 */     this.generator.__setUntrackedStateObjects(false);
/*      */     
/*  843 */     return readWitness;
/*      */   }
/*      */   
/*      */   private T constructFromExistingElement(T element) {
/*  847 */     if (IAuxType.class.isAssignableFrom(this.typeClass))
/*  848 */       return constructFromPackedValue(((IAuxType)element)
/*  849 */           .getPackedValue()); 
/*  850 */     if (RuntimeStruct.class.isAssignableFrom(this.typeClass)) {
/*  851 */       return constructFromPackedValue(((RuntimeStruct)element).ptrReference
/*  852 */           .getPackedValue());
/*      */     }
/*  854 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private T constructFromPackedValue(PackedValue p) {
/*  859 */     if (this.typeClass == UnsignedInteger.class)
/*  860 */       return (T)new UnsignedInteger(
/*  861 */           Integer.parseInt((String)this.typeArgs[0]), p); 
/*  862 */     if (this.typeClass == FieldElement.class)
/*  863 */       return (T)new FieldElement(new BigInteger((String)this.typeArgs[0]), p); 
/*  864 */     if (this.typeClass == GroupElement.class)
/*  865 */       return (T)new GroupElement(new BigInteger((String)this.typeArgs[0]), p); 
/*  866 */     if (this.typeClass == Bit.class)
/*  867 */       return (T)new Bit(p.array[0]); 
/*  868 */     if (RuntimeStruct.class.isAssignableFrom(this.typeClass)) {
/*      */       try {
/*  870 */         Method m = this.typeClass.getMethod("____createObjectWithReference", new Class[] {
/*  871 */               UnsignedInteger.class
/*      */             });
/*      */         
/*  874 */         Object[] params = { new UnsignedInteger(
/*  875 */               p.currentBitwidth[0], p) };
/*  876 */         return (T)m.invoke(null, new Object[] { params[0] });
/*  877 */       } catch (Exception e) {
/*  878 */         e.printStackTrace();
/*  879 */         return null;
/*      */       } 
/*      */     }
/*  882 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private T constructDummyElement() {
/*  888 */     if (this.typeClass == UnsignedInteger.class)
/*  889 */       return (T)new UnsignedInteger(
/*  890 */           Integer.parseInt((String)this.typeArgs[0])); 
/*  891 */     if (this.typeClass == FieldElement.class)
/*  892 */       return (T)new FieldElement(new BigInteger((String)this.typeArgs[0])); 
/*  893 */     if (this.typeClass == GroupElement.class)
/*  894 */       return (T)new GroupElement(new BigInteger((String)this.typeArgs[0])); 
/*  895 */     if (this.typeClass == Bit.class)
/*  896 */       return (T)new Bit(); 
/*  897 */     if (RuntimeStruct.class.isAssignableFrom(this.typeClass)) {
/*      */       try {
/*  899 */         Method m = this.typeClass.getMethod("____createDummyObject", new Class[0]);
/*  900 */         return (T)m.invoke(null, (Object[])new String[0]);
/*  901 */       } catch (Exception e) {
/*  902 */         e.printStackTrace();
/*  903 */         return null;
/*      */       } 
/*      */     }
/*  906 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private T constructWitnessElement() {
/*  912 */     if (this.typeClass == UnsignedInteger.class) {
/*  913 */       int bitwidth = Integer.parseInt((String)this.typeArgs[0]);
/*  914 */       UnsignedInteger e2 = 
/*  915 */         UnsignedInteger.createWitness(this.generator, bitwidth, new String[] { "read at " + 
/*  916 */             this.numOfVariableReads });
/*  917 */       return (T)e2;
/*  918 */     }  if (this.typeClass == FieldElement.class) {
/*  919 */       BigInteger m = new BigInteger((String)this.typeArgs[0]);
/*  920 */       FieldElement e2 = FieldElement.createWitness(this.generator, m, new String[0]);
/*  921 */       return (T)e2;
/*  922 */     }  if (this.typeClass == GroupElement.class) {
/*  923 */       BigInteger m = new BigInteger((String)this.typeArgs[0]);
/*  924 */       GroupElement e2 = GroupElement.createWitness(this.generator, m, new String[0]);
/*  925 */       return (T)e2;
/*  926 */     }  if (this.typeClass == Bit.class) {
/*  927 */       Bit e2 = Bit.createWitness(this.generator, new String[0]);
/*  928 */       return (T)e2;
/*  929 */     }  if (RuntimeStruct.class.isAssignableFrom(this.typeClass)) {
/*      */       try {
/*  931 */         Method m = this.typeClass.getMethod("____createObjectWithReference", new Class[] {
/*  932 */               UnsignedInteger.class });
/*  933 */         Object[] params = {
/*  934 */             UnsignedInteger.createWitness(this.generator, getElementSize()[0], new String[0]) };
/*  935 */         return (T)m.invoke(null, new Object[] { params[0] });
/*  936 */       } catch (Exception e) {
/*  937 */         e.printStackTrace();
/*  938 */         return null;
/*      */       } 
/*      */     }
/*  941 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private T constructDefaultElement() {
/*  947 */     if (this.typeClass == UnsignedInteger.class)
/*  948 */       return (T)new UnsignedInteger(
/*  949 */           Integer.parseInt((String)this.typeArgs[0]), BigInteger.ZERO); 
/*  950 */     if (this.typeClass == FieldElement.class)
/*  951 */       return (T)new FieldElement(new BigInteger((String)this.typeArgs[0]), 
/*  952 */           BigInteger.ZERO); 
/*  953 */     if (this.typeClass == GroupElement.class)
/*  954 */       return (T)new GroupElement(new BigInteger((String)this.typeArgs[0]), 
/*  955 */           BigInteger.ZERO); 
/*  956 */     if (this.typeClass == Bit.class)
/*  957 */       return (T)new Bit(false); 
/*  958 */     if (RuntimeStruct.class.isAssignableFrom(this.typeClass)) {
/*      */       try {
/*  960 */         Method m = this.typeClass.getMethod("____createNullObject", new Class[0]);
/*      */         
/*  962 */         return (T)m.invoke(null, (Object[])new String[0]);
/*  963 */       } catch (Exception e) {
/*  964 */         e.printStackTrace();
/*  965 */         return null;
/*      */       } 
/*      */     }
/*  968 */     return null;
/*      */   }
/*      */ 
/*      */   
/*      */   private PackedValue getPackedValue(T e) {
/*  973 */     if (IAuxType.class.isAssignableFrom(this.typeClass))
/*  974 */       return ((IAuxType)e).getPackedValue(); 
/*  975 */     if (RuntimeStruct.class.isAssignableFrom(this.typeClass)) {
/*  976 */       return ((RuntimeStruct)e).ptrReference.packedWire;
/*      */     }
/*  978 */     return null;
/*      */   }
/*      */   
/*      */   private int[] getElementSize() {
/*  982 */     if (this.typeClass == UnsignedInteger.class) {
/*  983 */       int bitwidth = Integer.parseInt((String)this.typeArgs[0]);
/*  984 */       if (bitwidth <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/*  985 */         return new int[] { bitwidth };
/*      */       }
/*  987 */       int[] sizes = new int[(bitwidth / 
/*  988 */           UnsignedInteger.BITWIDTH_PER_CHUNK + bitwidth % 
/*  989 */           UnsignedInteger.BITWIDTH_PER_CHUNK == 0) ? 0 : 1];
/*  990 */       Arrays.fill(sizes, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*  991 */       if (bitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/*  992 */         sizes[sizes.length - 1] = bitwidth % 
/*  993 */           UnsignedInteger.BITWIDTH_PER_CHUNK;
/*      */       }
/*  995 */       return sizes;
/*      */     } 
/*  997 */     if (this.typeClass == FieldElement.class || this.typeClass == GroupElement.class) {
/*  998 */       int bitwidth = (new BigInteger((String)this.typeArgs[0])).bitLength();
/*  999 */       if (bitwidth <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/* 1000 */         return new int[] { bitwidth };
/*      */       }
/* 1002 */       int[] sizes = new int[(bitwidth / 
/* 1003 */           UnsignedInteger.BITWIDTH_PER_CHUNK + bitwidth % 
/* 1004 */           UnsignedInteger.BITWIDTH_PER_CHUNK == 0) ? 0 : 1];
/* 1005 */       Arrays.fill(sizes, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1006 */       if (bitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/* 1007 */         sizes[sizes.length - 1] = bitwidth % 
/* 1008 */           UnsignedInteger.BITWIDTH_PER_CHUNK;
/*      */       }
/* 1010 */       return sizes;
/*      */     } 
/* 1012 */     if (this.typeClass == Bit.class)
/* 1013 */       return new int[] { 1 }; 
/* 1014 */     if (RuntimeStruct.class.isAssignableFrom(this.typeClass)) {
/*      */       try {
/* 1016 */         Method m = this.typeClass.getMethod("____getIndexBitwidth", new Class[0]);
/*      */         
/* 1018 */         return new int[] { ((Integer)m.invoke(null, 
/* 1019 */               (Object[])new String[0])).intValue() };
/* 1020 */       } catch (Exception e) {
/* 1021 */         e.printStackTrace();
/*      */       } 
/*      */     }
/* 1024 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void write(UnsignedInteger idx, T element) {
/* 1030 */     this.opCounter++;
/* 1031 */     this.cachedAccesses.clear();
/*      */     
/* 1033 */     if (this.state.redundantWrites != null && 
/* 1034 */       this.state.redundantWrites.contains(Integer.valueOf(this.opCounter))) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1039 */     MemoryCacheRecord cacheRecord = new MemoryCacheRecord(idx, 
/* 1040 */         idx.internalStateSerial);
/* 1041 */     if (this.cachedWrites.contains(cacheRecord) && 
/* 1042 */       ConditionalScopeTracker.getCurrentScopeId() == 0) {
/*      */       
/* 1044 */       System.out.println("Removing prev Writes");
/* 1045 */       this.state.recordRedundantWire(this.opCounter - 1);
/*      */     } 
/*      */     
/* 1048 */     if (ConditionalScopeTracker.getCurrentScopeId() == 0)
/*      */     {
/*      */ 
/*      */       
/* 1052 */       this.cachedAccesses.put(cacheRecord, element);
/*      */     }
/* 1054 */     this.cachedWrites.add(cacheRecord);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1068 */     if (idx.isConstant())
/* 1069 */     { if (!this.randAccessOccured) {
/* 1070 */         this.numOfInitialConstWrites++;
/*      */       } else {
/* 1072 */         this.numOfConstWrites++;
/*      */       }  }
/* 1074 */     else { this.numOfVariableWrites++;
/* 1075 */       this.randAccessOccured = true; }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1083 */     boolean conditionalWrite = false;
/* 1084 */     Bit activeBit = new Bit(true);
/* 1085 */     int conditionalScopeId = ConditionalScopeTracker.getCurrentScopeId();
/* 1086 */     if (conditionalScopeId != this.conditionalScopeId) {
/* 1087 */       conditionalWrite = true;
/* 1088 */       this.numOfCoditionalWrites++;
/* 1089 */       activeBit = ConditionalScopeTracker.getAccumActiveBit();
/*      */     } 
/*      */     
/* 1092 */     if (this.generator.__getPhase() == 0) {
/*      */       
/* 1094 */       idx.getState().setPackedAhead(true);
/* 1095 */       idx.getState().setConditionallySplittedAhead(true);
/* 1096 */       if (IAuxType.class.isAssignableFrom(this.typeClass)) {
/* 1097 */         IAuxType t = (IAuxType)element;
/* 1098 */         t.getState().setMustBeWithinRange(true);
/* 1099 */         t.getState().setConditionallySplittedAndAlignedAhead(true);
/* 1100 */         t.getState().setPackedAhead(true);
/*      */       
/*      */       }
/*      */     
/*      */     }
/*      */     else {
/*      */       
/* 1107 */       if (idx.isConstant() && !this.randAccessOccured) {
/*      */         
/* 1109 */         int v = idx.constant.intValue();
/*      */ 
/*      */         
/* 1112 */         if (v < 0 || v >= this.elements.length) {
/* 1113 */           System.out.println(">> elements.length:" + this.elements.length);
/*      */ 
/*      */           
/*      */           return;
/*      */         } 
/*      */         
/* 1119 */         this.generator.__setUntrackedStateObjects(true);
/*      */ 
/*      */         
/* 1122 */         PackedValue x1 = getPackedValue(this.elements[v]);
/* 1123 */         PackedValue x2 = getPackedValue(element);
/*      */         
/* 1125 */         this.elements[v] = constructFromPackedValue(x1.muxBit(x2, 
/* 1126 */               activeBit.wire));
/* 1127 */         this.generator.__setUntrackedStateObjects(false);
/*      */ 
/*      */         
/*      */         return;
/*      */       } 
/*      */       
/* 1133 */       if (this.state.mode == 2) {
/* 1134 */         writeLinearMode(idx, element, conditionalWrite, activeBit, 
/* 1135 */             cacheRecord);
/* 1136 */       } else if (this.state.mode == 1) {
/* 1137 */         writeNetworkMode(idx, element, conditionalWrite, activeBit, 
/* 1138 */             cacheRecord);
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
/*      */   private void writeNetworkMode(UnsignedInteger idx, final T element, boolean conditionalWrite, Bit activeBit, MemoryCacheRecord cacheRecord) {
/*      */     final UnsignedInteger idx2;
/* 1151 */     Wire overflowDetected = this.generator.__getZeroWire();
/* 1152 */     if (idx.maxValue.compareTo(BigInteger.valueOf(this.numElements)) >= 0) {
/*      */       
/* 1154 */       this.generator.__setUntrackedStateObjects(true);
/* 1155 */       int idxBitlength = BigInteger.valueOf((this.numElements - 1)).bitLength();
/* 1156 */       Wire w = null;
/* 1157 */       if (idx.isConstant()) {
/* 1158 */         int v = idx.constant.intValue();
/* 1159 */         if (v < 0 || v >= this.elements.length) {
/* 1160 */           overflowDetected = this.generator.__getOneWire();
/* 1161 */           w = this.generator.__getZeroWire();
/*      */         } else {
/* 1163 */           w = this.generator.__createConstantWire(idx.constant, new String[0]);
/*      */         
/*      */         }
/*      */ 
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1171 */         Wire isInRange = idx.packedWire.array[0].isLessThan(this.numElements, idx.maxValue.bitLength(), new String[0]);
/* 1172 */         w = idx.packedWire.array[0].mul(isInRange, new String[0]);
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1177 */       idx2 = new UnsignedInteger(idxBitlength, new PackedValue(w, 
/* 1178 */             BigInteger.valueOf(this.numElements)));
/*      */       
/* 1180 */       this.generator.__setUntrackedStateObjects(false);
/*      */     } else {
/* 1182 */       idx2 = idx.copy();
/*      */     } 
/*      */     
/* 1185 */     if (this.runtimeVals == null) {
/* 1186 */       contructNetwork();
/*      */     }
/*      */ 
/*      */     
/* 1190 */     final Wire activeBitAfterOverflowCheck = activeBit.wire.mul(overflowDetected.invAsBit(new String[0]), new String[0]);
/*      */     
/* 1192 */     this.generator.__setUntrackedStateObjects(true);
/*      */     
/* 1194 */     T conditionalWitness = constructWitnessElement();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1210 */     if (conditionalWrite) {
/* 1211 */       this.conditionalWitnessVals.add(getPackedValue(conditionalWitness));
/*      */     }
/*      */     
/* 1214 */     final T conditionalWitnessElement = conditionalWitness;
/* 1215 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*      */         {
/*      */           
/*      */           public void evaluate(CircuitEvaluator evaluator)
/*      */           {
/* 1220 */             BigInteger idxValue = evaluator.getWireValue(idx2.packedWire, 
/* 1221 */                 UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 1222 */             BigInteger activeBitValue = BigInteger.ONE;
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1227 */             activeBitValue = evaluator.getWireValue(activeBitAfterOverflowCheck);
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1232 */             if (activeBitValue.equals(BigInteger.ONE)) {
/*      */ 
/*      */ 
/*      */               
/* 1236 */               SmartMemory.this.runtimeVals[idxValue.intValue()] = evaluator.getWireValue(
/* 1237 */                   SmartMemory.this.getPackedValue((T)element), 
/* 1238 */                   UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */               
/* 1240 */               int bitwidth = 1;
/* 1241 */               if (SmartMemory.this.typeClass == UnsignedInteger.class) {
/* 1242 */                 bitwidth = Integer.parseInt((String)SmartMemory.this.typeArgs[0]);
/* 1243 */               } else if (SmartMemory.this.typeClass == FieldElement.class || SmartMemory.this.typeClass == GroupElement.class) {
/* 1244 */                 bitwidth = (new BigInteger((String)SmartMemory.this.typeArgs[0]))
/* 1245 */                   .bitLength();
/*      */               } 
/* 1247 */               evaluator.setWireValue(
/* 1248 */                   SmartMemory.this.getPackedValue((T)conditionalWitnessElement), BigInteger.ZERO, 
/* 1249 */                   bitwidth, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */             } else {
/* 1251 */               int bitwidth = 1;
/* 1252 */               if (SmartMemory.this.typeClass == UnsignedInteger.class) {
/* 1253 */                 bitwidth = Integer.parseInt((String)SmartMemory.this.typeArgs[0]);
/* 1254 */               } else if (SmartMemory.this.typeClass == FieldElement.class || SmartMemory.this.typeClass == GroupElement.class) {
/* 1255 */                 bitwidth = (new BigInteger((String)SmartMemory.this.typeArgs[0]))
/* 1256 */                   .bitLength();
/*      */               } 
/* 1258 */               if (idxValue.intValue() >= SmartMemory.this.numElements) {
/* 1259 */                 evaluator.setWireValue(
/* 1260 */                     SmartMemory.this.getPackedValue((T)conditionalWitnessElement), SmartMemory.this.runtimeVals[0], 
/* 1261 */                     bitwidth, UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */               } else {
/* 1263 */                 evaluator.setWireValue(
/* 1264 */                     SmartMemory.this.getPackedValue((T)conditionalWitnessElement), SmartMemory.this.runtimeVals[idxValue
/* 1265 */                       .intValue()], bitwidth, 
/* 1266 */                     UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */               } 
/*      */             } 
/*      */           }
/*      */         });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1276 */     PackedValue v1 = getPackedValue(element);
/* 1277 */     PackedValue v2 = getPackedValue(conditionalWitness);
/* 1278 */     PackedValue v3 = v2.muxBit(v1, activeBit.wire);
/* 1279 */     T elementToNw = constructFromPackedValue(v3);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1301 */     this.opCounters.add(this.opCounterWire);
/*      */     
/* 1303 */     this.operations.add(activeBitAfterOverflowCheck);
/* 1304 */     this.indexesToOperate.add(idx2.packedWire.array[0]);
/* 1305 */     this.data.add(getPackedValue(elementToNw));
/* 1306 */     this.opCounterWire = this.opCounterWire.add(1L, new String[0]);
/* 1307 */     this.generator.__setUntrackedStateObjects(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeLinearMode(UnsignedInteger idx, T element, boolean conditionalWrite, Bit activeBit, MemoryCacheRecord cacheRecord) {
/* 1315 */     if (idx.isConstant()) {
/*      */       
/* 1317 */       int v = idx.constant.intValue();
/* 1318 */       if (v < 0 || v >= this.elements.length) {
/*      */         return;
/*      */       }
/*      */       
/* 1322 */       this.generator.__setUntrackedStateObjects(true);
/* 1323 */       PackedValue x1 = getPackedValue(this.elements[idx.constant.intValue()]);
/* 1324 */       PackedValue x2 = getPackedValue(element);
/* 1325 */       this.elements[idx.constant.intValue()] = constructFromPackedValue(x1
/* 1326 */           .muxBit(x2, activeBit.wire));
/* 1327 */       this.generator.__setUntrackedStateObjects(false);
/*      */ 
/*      */       
/*      */       return;
/*      */     } 
/*      */     
/* 1333 */     this.generator.__setUntrackedStateObjects(true);
/* 1334 */     for (int i = 0; i < this.numElements; i++) {
/* 1335 */       Bit bit = idx.isEqualTo(new UnsignedInteger(BigInteger.valueOf(i)));
/* 1336 */       if (conditionalWrite) {
/* 1337 */         bit = bit.mul(activeBit);
/*      */       }
/* 1339 */       PackedValue w3 = getPackedValue(this.elements[i]).muxBit(
/* 1340 */           getPackedValue(element), bit.wire);
/*      */ 
/*      */       
/* 1343 */       this.elements[i] = constructFromPackedValue(w3);
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1356 */     this.generator.__setUntrackedStateObjects(false);
/*      */   }
/*      */ 
/*      */   
/*      */   public void finalize() {
/* 1361 */     if (this.generator.__getPhase() == 0) {
/* 1362 */       throw new RuntimeException("Should be called in the second phase");
/*      */     }
/*      */     
/* 1365 */     if (this.state.mode == 1 && this.opCounters != null) {
/* 1366 */       final int k = this.opCounters.size();
/* 1367 */       int[] elementSizes = getElementSize();
/* 1368 */       final int numWiresPerElement = elementSizes.length;
/* 1369 */       final ArrayList<Wire> operations2 = new ArrayList<>();
/* 1370 */       final ArrayList<Wire> indexesToOperate2 = new ArrayList<>();
/* 1371 */       final ArrayList<Wire> opCounters2 = new ArrayList<>();
/* 1372 */       final ArrayList<PackedValue> data2 = new ArrayList<>();
/*      */       
/* 1374 */       int indexBitwidth = (int)Math.ceil(Math.log(this.numElements) / 
/* 1375 */           Math.log(2.0D));
/* 1376 */       int opCounterBitwidth = (int)Math.ceil(Math.log(k) / Math.log(2.0D));
/*      */       
/* 1378 */       for (int i = 0; i < k; i++) {
/* 1379 */         operations2.add(this.generator.__createProverWitnessWire(new String[0]));
/* 1380 */         indexesToOperate2.add(this.generator.__createProverWitnessWire(new String[0]));
/* 1381 */         opCounters2.add(this.generator.__createProverWitnessWire(new String[0]));
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1386 */         data2.add(new PackedValue(this.generator
/* 1387 */               .__createProverWitnessWireArray(numWiresPerElement, new String[0]), 
/* 1388 */               elementSizes));
/*      */       } 
/*      */       
/* 1391 */       this.generator.__specifyProverWitnessComputation(new Instruction()
/*      */           {
/*      */             public void evaluate(CircuitEvaluator evaluator)
/*      */             {
/* 1395 */               SmartMemory.MemoryConsistencyRecord[] array = new SmartMemory.MemoryConsistencyRecord[k];
/*      */               
/* 1397 */               for (int i = 0; i < k; i++) {
/* 1398 */                 array[i] = new SmartMemory.MemoryConsistencyRecord(null);
/* 1399 */                 (array[i]).op = evaluator.getWireValue(SmartMemory.this.operations.get(i));
/* 1400 */                 (array[i]).counter = evaluator.getWireValue(SmartMemory.this.opCounters
/* 1401 */                     .get(i));
/* 1402 */                 (array[i]).index = evaluator
/* 1403 */                   .getWireValue(SmartMemory.this.indexesToOperate.get(i));
/* 1404 */                 (array[i]).data = new BigInteger[numWiresPerElement];
/* 1405 */                 for (int k = 0; k < numWiresPerElement; k++) {
/* 1406 */                   (array[i]).data[k] = evaluator.getWireValue(
/* 1407 */                       (SmartMemory.this.data.get(i)).array[k]);
/*      */                 }
/*      */               } 
/*      */               
/* 1411 */               SmartMemory.ArrayIndexComparator comparator = new SmartMemory.ArrayIndexComparator(
/* 1412 */                   array);
/* 1413 */               Integer[] indexes = comparator.createIndexArray();
/* 1414 */               Arrays.sort(indexes, comparator);
/*      */               
/* 1416 */               int[] permutation = new int[k]; int j;
/* 1417 */               for (j = 0; j < k; j++) {
/* 1418 */                 permutation[indexes[j].intValue()] = j;
/*      */               }
/* 1420 */               Arrays.sort((Object[])array);
/*      */               
/* 1422 */               for (j = 0; j < k; j++) {
/* 1423 */                 evaluator.setWireValue(operations2.get(j), (array[j]).op);
/* 1424 */                 evaluator.setWireValue(opCounters2.get(j), 
/* 1425 */                     (array[j]).counter);
/* 1426 */                 evaluator.setWireValue(indexesToOperate2.get(j), 
/* 1427 */                     (array[j]).index);
/* 1428 */                 for (int k = 0; k < numWiresPerElement; k++) {
/* 1429 */                   evaluator.setWireValue(((PackedValue)data2.get(j)).array[k], 
/* 1430 */                       (array[j]).data[k]);
/*      */                 }
/*      */               } 
/* 1433 */               evaluator.getPermutations().put("INT_" + SmartMemory.this.memoryIndex, 
/* 1434 */                   permutation);
/*      */             }
/*      */           });
/*      */       
/* 1438 */       Wire[][] inputs = new Wire[k][];
/* 1439 */       Wire[][] outputs = new Wire[k][];
/*      */       
/* 1441 */       if (this.state.packingOption == 1) {
/*      */ 
/*      */         
/* 1444 */         for (int m = 0; m < k; m++) {
/*      */           
/* 1446 */           BigInteger factor = BigInteger.valueOf(2L);
/* 1447 */           Wire packedWire1 = this.operations.get(m);
/* 1448 */           packedWire1 = packedWire1.add(((Wire)this.indexesToOperate.get(m)).mul(
/* 1449 */                 factor, new String[0]), new String[0]);
/* 1450 */           factor = factor.shiftLeft(indexBitwidth);
/* 1451 */           packedWire1 = packedWire1
/* 1452 */             .add(((Wire)this.opCounters.get(m)).mul(factor, new String[0]), new String[0]);
/* 1453 */           factor = factor.shiftLeft(opCounterBitwidth);
/* 1454 */           for (int n = 0; n < numWiresPerElement; n++) {
/*      */             
/* 1456 */             packedWire1 = packedWire1.add(((PackedValue)this.data.get(m)).array[n]
/* 1457 */                 .mul(factor, new String[0]), new String[0]);
/* 1458 */             factor = factor
/* 1459 */               .shiftLeft(UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */           } 
/*      */           
/* 1462 */           PackedValue element = this.data.get(m);
/* 1463 */           if (numWiresPerElement == 1) {
/* 1464 */             element.array[0].restrictBitLength(elementSizes[0], new String[0]);
/*      */           } else {
/* 1466 */             for (int i2 = 0; i2 < numWiresPerElement; i2++) {
/* 1467 */               element.array[i2].restrictBitLength(elementSizes[i2], new String[0]);
/*      */             }
/*      */           } 
/*      */ 
/*      */ 
/*      */           
/* 1473 */           (new Wire[1])[0] = packedWire1; inputs[m] = new Wire[1];
/*      */           
/* 1475 */           factor = BigInteger.valueOf(2L);
/* 1476 */           Wire packedWire2 = operations2.get(m);
/* 1477 */           packedWire2 = packedWire2.add(((Wire)indexesToOperate2.get(m)).mul(
/* 1478 */                 factor, new String[0]), new String[0]);
/* 1479 */           factor = factor.shiftLeft(indexBitwidth);
/* 1480 */           packedWire2 = packedWire2.add(((Wire)opCounters2.get(m))
/* 1481 */               .mul(factor, new String[0]), new String[0]);
/* 1482 */           factor = factor.shiftLeft(opCounterBitwidth);
/* 1483 */           for (int i1 = 0; i1 < numWiresPerElement; i1++) {
/* 1484 */             packedWire2 = packedWire2.add(((PackedValue)data2.get(m)).array[i1]
/* 1485 */                 .mul(factor, new String[0]), new String[0]);
/* 1486 */             factor = factor
/* 1487 */               .shiftLeft(UnsignedInteger.BITWIDTH_PER_CHUNK);
/*      */           } 
/* 1489 */           (new Wire[1])[0] = packedWire2; outputs[m] = new Wire[1];
/*      */ 
/*      */ 
/*      */           
/* 1493 */           this.generator.__addBinaryAssertion(operations2.get(m), new String[] {
/* 1494 */                 "assert on OP" });
/* 1495 */           ((Wire)opCounters2.get(m)).restrictBitLength(opCounterBitwidth, new String[0]);
/* 1496 */           ((Wire)indexesToOperate2.get(m)).restrictBitLength(indexBitwidth, new String[0]);
/* 1497 */           PackedValue element2 = data2.get(m);
/* 1498 */           if (numWiresPerElement == 1) {
/* 1499 */             element2.array[0].restrictBitLength(elementSizes[0], new String[0]);
/*      */           } else {
/* 1501 */             for (int i2 = 0; i2 < numWiresPerElement; i2++) {
/* 1502 */               element2.array[i2]
/* 1503 */                 .restrictBitLength(elementSizes[i2], new String[0]);
/*      */             }
/*      */           }
/*      */         
/*      */         } 
/*      */       } else {
/*      */         
/* 1510 */         for (int m = 0; m < k; m++) {
/* 1511 */           inputs[m] = new Wire[3 + numWiresPerElement];
/* 1512 */           outputs[m] = new Wire[3 + numWiresPerElement];
/* 1513 */           inputs[m][0] = this.operations.get(m);
/* 1514 */           inputs[m][1] = this.indexesToOperate.get(m);
/* 1515 */           inputs[m][2] = this.opCounters.get(m);
/*      */           
/* 1517 */           outputs[m][0] = operations2.get(m);
/* 1518 */           outputs[m][1] = indexesToOperate2.get(m);
/* 1519 */           outputs[m][2] = opCounters2.get(m);
/* 1520 */           for (int n = 0; n < numWiresPerElement; n++) {
/* 1521 */             inputs[m][3 + n] = ((PackedValue)this.data.get(m)).array[n];
/* 1522 */             outputs[m][3 + n] = ((PackedValue)data2.get(m)).array[n];
/*      */           } 
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1533 */       PackedValue defaultPackedValue = null;
/* 1534 */       if (this.defaultElement != null) {
/* 1535 */         defaultPackedValue = getPackedValue(this.defaultElement);
/* 1536 */         Wire isRead = ((Wire)operations2.get(0)).invAsBit(new String[0]);
/* 1537 */         for (int m = 0; m < numWiresPerElement; m++) {
/*      */           
/* 1539 */           if (m < defaultPackedValue.array.length) {
/* 1540 */             this.generator
/* 1541 */               .__addZeroAssertion(
/* 1542 */                 isRead.mul(((PackedValue)data2.get(0)).array[m]
/* 1543 */                   .sub(defaultPackedValue.array[m], new String[0]), new String[0]), new String[] {
/* 1544 */                   "Checking equality with default value if first operation is read" });
/*      */           } else {
/* 1546 */             this.generator
/* 1547 */               .__addZeroAssertion(
/* 1548 */                 isRead.mul(((PackedValue)data2.get(0)).array[m]
/* 1549 */                   .sub(this.generator.__getZeroWire(), new String[0]), new String[0]), new String[] {
/* 1550 */                   "Checking equality with default value if first operation is read" });
/*      */           } 
/*      */         } 
/*      */       } else {
/* 1554 */         Wire isRead = ((Wire)operations2.get(0)).invAsBit(new String[0]);
/* 1555 */         for (int m = 0; m < numWiresPerElement; m++) {
/*      */           
/* 1557 */           this.generator
/* 1558 */             .__addZeroAssertion(
/* 1559 */               isRead.mul(((PackedValue)data2.get(0)).array[m]
/* 1560 */                 .sub(this.generator.__getZeroWire(), new String[0]), new String[0]), new String[] {
/* 1561 */                 "Checking equality with default value if first operation is read"
/*      */               });
/*      */         } 
/*      */       } 
/*      */       
/* 1566 */       for (int j = 1; j < k; j++) {
/* 1567 */         Wire greaterIndex = ((Wire)indexesToOperate2.get(j)).isGreaterThan(
/* 1568 */             indexesToOperate2.get(j - 1), indexBitwidth, new String[0]);
/*      */         
/* 1570 */         Wire equalIndex = ((Wire)indexesToOperate2.get(j)).isEqualTo(
/* 1571 */             indexesToOperate2.get(j - 1), new String[0]);
/* 1572 */         Wire greaterOpCounter = ((Wire)opCounters2.get(j)).isGreaterThan(
/* 1573 */             opCounters2.get(j - 1), opCounterBitwidth, new String[0]);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1582 */         this.generator.__addOneAssertion(greaterIndex.add(equalIndex
/* 1583 */               .mul(greaterOpCounter, new String[0]), new String[0]), new String[] { "Either a greater index or equal index and higher op counter" });
/*      */         
/* 1585 */         Wire isRead = ((Wire)operations2.get(j)).invAsBit(new String[0]);
/*      */         
/* 1587 */         if (defaultPackedValue != null) {
/* 1588 */           for (int m = 0; m < numWiresPerElement; m++)
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1594 */             if (m < defaultPackedValue.array.length) {
/* 1595 */               tmp = defaultPackedValue.array[m];
/*      */             } else {
/* 1597 */               tmp = this.generator.__getZeroWire();
/*      */             } 
/*      */             
/* 1600 */             Wire tmp = tmp.add(equalIndex.mul(((PackedValue)data2.get(j - 1)).array[m]
/* 1601 */                   .sub(tmp, new String[0]), new String[0]), new String[0]);
/* 1602 */             this.generator
/* 1603 */               .__addZeroAssertion(isRead.mul(((PackedValue)data2.get(j)).array[m]
/* 1604 */                   .sub(tmp, new String[0]), new String[0]), new String[0]);
/*      */           
/*      */           }
/*      */         
/*      */         }
/*      */         else {
/*      */           
/* 1611 */           this.generator.__addZeroAssertion(equalIndex.invAsBit(new String[0]).mul(isRead.invAsBit(new String[0]).sub(1L, new String[0]), new String[0]), new String[] { "if index not equal, verify it's a write" });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/* 1625 */           for (int m = 0; m < numWiresPerElement; m++) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1635 */             this.generator
/* 1636 */               .__addZeroAssertion(isRead.mul(((PackedValue)data2.get(j)).array[m]
/* 1637 */                   .sub(((PackedValue)data2.get(j - 1)).array[m], new String[0]), new String[0]), new String[] { "consistent data items" });
/*      */           } 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public void incrementSize() {
/* 1646 */     if (this.generator.__getPhase() == 0) {
/* 1647 */       this.numElements++;
/*      */     }
/*      */   }
/*      */   
/*      */   public void analyzeWorkload() {
/*      */     long cost1;
/* 1653 */     System.out.println("Analyzing memory workload (more updates optimizing memory access costs are expected soon) .. ");
/*      */     
/* 1655 */     if (this.generator.__getPhase() == 1) {
/* 1656 */       throw new RuntimeException("Should be called in the first phase");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1662 */     int[] elementSizes = getElementSize();
/*      */     
/* 1664 */     int numOfWordsPerElement = elementSizes.length;
/* 1665 */     int elementSize = 0; byte b; int i, arrayOfInt1[];
/* 1666 */     for (i = (arrayOfInt1 = elementSizes).length, b = 0; b < i; ) { int x = arrayOfInt1[b];
/* 1667 */       elementSize += x;
/*      */       b++; }
/*      */     
/* 1670 */     long readOnlyMethodCost = 0L;
/*      */     
/* 1672 */     boolean isConstant = false;
/* 1673 */     boolean inSupportedRange = false;
/* 1674 */     int readOnlyMaxBitwidth = 0;
/* 1675 */     BigInteger[] vals = null;
/*      */     
/* 1677 */     if (this.numOfVariableWrites == 0L && this.numOfConstWrites == 0L && this.numOfInitialConstWrites == 0L) {
/*      */       
/* 1679 */       isConstant = true;
/* 1680 */       vals = new BigInteger[this.numElements];
/* 1681 */       inSupportedRange = true;
/* 1682 */       for (int j = 0; j < this.numElements; j++) {
/* 1683 */         BigInteger c = null;
/* 1684 */         if (IAuxType.class.isAssignableFrom(this.typeClass)) {
/* 1685 */           c = ((IAuxType)this.elements[j]).getConstant();
/* 1686 */         } else if (RuntimeStruct.class.isAssignableFrom(this.typeClass)) {
/* 1687 */           c = ((RuntimeStruct)this.elements[j]).ptrReference.getConstant();
/*      */         } 
/* 1689 */         if (c == null) {
/* 1690 */           isConstant = false; break;
/*      */         } 
/* 1692 */         if (c.bitLength() > 128) {
/* 1693 */           inSupportedRange = false;
/*      */           break;
/*      */         } 
/* 1696 */         vals[j] = c;
/* 1697 */         int bitwidth = c.bitLength();
/* 1698 */         if (bitwidth > readOnlyMaxBitwidth) {
/* 1699 */           readOnlyMaxBitwidth = bitwidth;
/*      */         }
/*      */       } 
/* 1702 */       if (isConstant && inSupportedRange) {
/*      */         
/* 1704 */         readOnlyMethodCost = (long)Math.ceil(Math.sqrt(this.numElements)) * this.numOfVariableReads * 2L;
/* 1705 */         this.readOnly = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1722 */     System.out.println("Num of variable reads = " + this.numOfVariableReads);
/* 1723 */     System.out.println("Num of conditional writes = " + 
/* 1724 */         this.numOfCoditionalWrites);
/* 1725 */     System.out
/* 1726 */       .println("Num of words per element = " + numOfWordsPerElement);
/* 1727 */     System.out.println("Num of variable writes = " + this.numOfVariableWrites);
/*      */     
/* 1729 */     long linearCost = this.numOfVariableReads * this.numElements * 
/* 1730 */       numOfWordsPerElement + (
/* 1731 */       this.numOfVariableWrites - this.numOfCoditionalWrites) * this.numElements * 
/* 1732 */       numOfWordsPerElement + this.numOfCoditionalWrites * 2L * 
/* 1733 */       this.numElements * numOfWordsPerElement + (
/* 1734 */       this.numOfVariableReads + this.numOfVariableWrites) * 2L * this.numElements;
/*      */ 
/*      */ 
/*      */     
/* 1738 */     long accesses = this.numOfVariableReads + this.numOfVariableWrites + 
/* 1739 */       this.numOfConstReads + this.numOfConstWrites;
/*      */     
/* 1741 */     System.out.println("num of accesses = " + accesses);
/*      */     
/* 1743 */     if (!this.isEmpty) {
/* 1744 */       accesses += this.numElements;
/*      */     }
/*      */     
/* 1747 */     int indexBitwidth = 
/* 1748 */       (int)Math.ceil(Math.log(this.numElements) / Math.log(2.0D));
/* 1749 */     int opCounterBitwidth = (int)Math.ceil(Math.log(accesses) / 
/* 1750 */         Math.log(2.0D));
/*      */ 
/*      */ 
/*      */     
/* 1754 */     int b1 = 1 + elementSize + indexBitwidth + opCounterBitwidth;
/*      */     
/* 1756 */     if (b1 > Config.getNumBitsFiniteFieldModulus() - 1) {
/* 1757 */       cost1 = -1L;
/*      */     } else {
/* 1759 */       cost1 = (1 + elementSize + indexBitwidth + opCounterBitwidth) * 
/* 1760 */         accesses + accesses * opCounterBitwidth + accesses * (
/* 1761 */         7 + indexBitwidth + opCounterBitwidth) + accesses * 
/* 1762 */         elementSize;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1767 */     int numOfWires = elementSizes.length + 3;
/* 1768 */     long cost2 = accesses * opCounterBitwidth * (numOfWires + 1) + accesses * (
/* 1769 */       7 + indexBitwidth + opCounterBitwidth);
/*      */ 
/*      */ 
/*      */     
/* 1773 */     long nwCost = cost2;
/* 1774 */     this.state.packingOption = 0;
/* 1775 */     if (cost1 < cost2 && cost1 > 0L) {
/* 1776 */       nwCost = cost1;
/* 1777 */       this.state.packingOption = 1;
/*      */     } 
/* 1779 */     System.out.println("Packing Option = " + this.state.packingOption);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1786 */     if (this.readOnly && readOnlyMethodCost < linearCost && readOnlyMethodCost < nwCost) {
/* 1787 */       System.out.println("Decision: Using Read-only O(sqrt n) mode for this memory");
/* 1788 */       this.state.readOnly = true;
/* 1789 */       this.state.maxBitwidth = readOnlyMaxBitwidth;
/* 1790 */       StaticMemorySolver.preprocess(vals, readOnlyMaxBitwidth, this.indexes, this.state);
/*      */       
/*      */       return;
/*      */     } 
/* 1794 */     if (nwCost > linearCost) {
/* 1795 */       this.state.mode = 2;
/* 1796 */       System.out.println("Decision: Using Linear mode for this memory");
/*      */     } else {
/* 1798 */       this.state.mode = 1;
/* 1799 */       System.out.println("Decision: Using Network mode for this memory");
/*      */     } 
/*      */   }
/*      */   
/*      */   public MemoryState getState() {
/* 1804 */     return this.state;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void verifyMembership(T element) {}
/*      */ 
/*      */   
/*      */   public Bit checkMembership(T element) {
/* 1813 */     return null;
/*      */   }
/*      */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\SmartMemory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */