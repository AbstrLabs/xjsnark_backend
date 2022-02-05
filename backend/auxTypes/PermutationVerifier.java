/*     */ package backend.auxTypes;
/*     */ 
/*     */ import backend.structure.CircuitGenerator;
/*     */ import backend.structure.Wire;
/*     */ import examples.gadgets.PermutationNetworkGadget;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PermutationVerifier<T>
/*     */ {
/*     */   private T[] a1;
/*     */   private T[] a2;
/*     */   private String id;
/*     */   private Class<?> typeClass;
/*     */   private Object[] typeArgs;
/*     */   private CircuitGenerator generator;
/*     */   
/*     */   public PermutationVerifier(Object[] a1, Object[] a2, String id, Class<?> typeClass, Object[] typeArgs) {
/*  22 */     this.a1 = (T[])a1;
/*  23 */     this.a2 = (T[])a2;
/*  24 */     if (a1.length != a2.length) {
/*  25 */       throw new IllegalArgumentException();
/*     */     }
/*  27 */     this.id = id;
/*  28 */     this.typeArgs = typeArgs;
/*  29 */     this.typeClass = typeClass;
/*  30 */     this.generator = CircuitGenerator.__getActiveCircuitGenerator();
/*  31 */     if (this.generator.__getPhase() == 0) {
/*  32 */       prepare();
/*     */     } else {
/*  34 */       constructCircuit();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void prepare() {
/*  39 */     int n = this.a1.length;
/*  40 */     if (IAuxType.class.isAssignableFrom(this.typeClass)) {
/*  41 */       for (int i = 0; i < n; i++) {
/*     */         
/*  43 */         VariableState st1 = ((IAuxType)this.a1[i]).getState();
/*  44 */         st1.setPackedAhead(true);
/*  45 */         st1.setConditionallySplittedAndAlignedAhead(true);
/*  46 */         st1.setMustBeWithinRange(true);
/*  47 */         VariableState st2 = ((IAuxType)this.a2[i]).getState();
/*  48 */         st2.setPackedAhead(true);
/*  49 */         st2.setConditionallySplittedAndAlignedAhead(true);
/*  50 */         st2.setMustBeWithinRange(true);
/*     */       } 
/*  52 */     } else if (StructDefinition.class.isAssignableFrom(this.typeClass)) {
/*  53 */       for (int i = 0; i < n; i++) {
/*  54 */         ((StructDefinition)this.a1[i]).__alignAndPackAll();
/*  55 */         ((StructDefinition)this.a2[i]).__alignAndPackAll();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void constructCircuit() {
/*  61 */     int n = this.a1.length;
/*  62 */     Wire[][] input = new Wire[n][];
/*  63 */     Wire[][] output = new Wire[n][];
/*  64 */     if (IAuxType.class.isAssignableFrom(this.typeClass)) {
/*  65 */       int[] elementSizes = getElementSize();
/*  66 */       int numWires = elementSizes.length;
/*  67 */       for (int i = 0; i < n; i++) {
/*  68 */         input[i] = (((IAuxType)this.a1[i]).getPackedValue()).array;
/*  69 */         output[i] = (((IAuxType)this.a2[i]).getPackedValue()).array;
/*     */       } 
/*  71 */     } else if (StructDefinition.class.isAssignableFrom(this.typeClass)) {
/*  72 */       for (int i = 0; i < n; i++) {
/*  73 */         ArrayList<IAuxType> list1 = ((StructDefinition)this.a1[i]).__getBasicElements();
/*  74 */         ArrayList<Wire> wireList1 = new ArrayList<>();
/*  75 */         for (IAuxType t : list1) {
/*  76 */           PackedValue v = t.getPackedValue(); byte b; int m; Wire[] arrayOfWire;
/*  77 */           for (m = (arrayOfWire = v.array).length, b = 0; b < m; ) { Wire w = arrayOfWire[b];
/*  78 */             wireList1.add(w); b++; }
/*     */         
/*     */         } 
/*  81 */         input[i] = new Wire[wireList1.size()];
/*  82 */         for (int j = 0; j < (input[i]).length; j++) {
/*  83 */           input[i][j] = wireList1.get(i);
/*     */         }
/*  85 */         ArrayList<IAuxType> list2 = ((StructDefinition)this.a2[i]).__getBasicElements();
/*  86 */         ArrayList<Wire> wireList2 = new ArrayList<>();
/*  87 */         for (IAuxType t : list2) {
/*  88 */           PackedValue v = t.getPackedValue(); byte b; int m; Wire[] arrayOfWire;
/*  89 */           for (m = (arrayOfWire = v.array).length, b = 0; b < m; ) { Wire w = arrayOfWire[b];
/*  90 */             wireList2.add(w); b++; }
/*     */         
/*     */         } 
/*  93 */         output[i] = new Wire[wireList2.size()];
/*  94 */         for (int k = 0; k < (output[i]).length; k++)
/*  95 */           output[i][k] = wireList2.get(i); 
/*     */       } 
/*     */     } 
/*  98 */     PermutationNetworkGadget p = new PermutationNetworkGadget(input, output, new int[0], "ext_" + this.id);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int[] getElementSize() {
/* 104 */     if (this.typeClass == UnsignedInteger.class) {
/* 105 */       int bitwidth = Integer.parseInt((String)this.typeArgs[0]);
/* 106 */       if (bitwidth <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/* 107 */         return new int[] { bitwidth };
/*     */       }
/* 109 */       int[] sizes = new int[(bitwidth / UnsignedInteger.BITWIDTH_PER_CHUNK + bitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK == 0) ? 0 : 1];
/* 110 */       Arrays.fill(sizes, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 111 */       if (bitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/* 112 */         sizes[sizes.length - 1] = bitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK;
/*     */       }
/* 114 */       return sizes;
/*     */     } 
/* 116 */     if (this.typeClass == FieldElement.class) {
/* 117 */       int bitwidth = (new BigInteger((String)this.typeArgs[0])).bitLength();
/* 118 */       if (bitwidth <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/* 119 */         return new int[] { bitwidth };
/*     */       }
/* 121 */       int[] sizes = new int[(bitwidth / UnsignedInteger.BITWIDTH_PER_CHUNK + bitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK == 0) ? 0 : 1];
/* 122 */       Arrays.fill(sizes, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 123 */       if (bitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/* 124 */         sizes[sizes.length - 1] = bitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK;
/*     */       }
/* 126 */       return sizes;
/*     */     } 
/* 128 */     if (this.typeClass == GroupElement.class) {
/* 129 */       int bitwidth = (new BigInteger((String)this.typeArgs[0])).bitLength();
/* 130 */       if (bitwidth <= UnsignedInteger.BITWIDTH_LIMIT_SHORT) {
/* 131 */         return new int[] { bitwidth };
/*     */       }
/* 133 */       int[] sizes = new int[(bitwidth / UnsignedInteger.BITWIDTH_PER_CHUNK + bitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK == 0) ? 0 : 1];
/* 134 */       Arrays.fill(sizes, UnsignedInteger.BITWIDTH_PER_CHUNK);
/* 135 */       if (bitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK != 0) {
/* 136 */         sizes[sizes.length - 1] = bitwidth % UnsignedInteger.BITWIDTH_PER_CHUNK;
/*     */       }
/* 138 */       return sizes;
/*     */     } 
/* 140 */     if (this.typeClass == Bit.class) {
/* 141 */       return new int[] { 1 };
/*     */     }
/* 143 */     return null;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\PermutationVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */