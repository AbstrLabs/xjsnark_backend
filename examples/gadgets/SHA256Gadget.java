/*     */ package examples.gadgets;
/*     */ 
/*     */ import backend.operations.Gadget;
/*     */ import backend.structure.Wire;
/*     */ import backend.structure.WireArray;
/*     */ import java.util.Arrays;
/*     */ import util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SHA256Gadget
/*     */   extends Gadget
/*     */ {
/*  15 */   private static final long[] H = new long[] { 1779033703L, 3144134277L, 1013904242L, 2773480762L, 1359893119L, 2600822924L, 
/*  16 */       528734635L, 1541459225L };
/*     */   
/*  18 */   private static final long[] K = new long[] { 1116352408L, 1899447441L, 3049323471L, 3921009573L, 961987163L, 1508970993L, 
/*  19 */       2453635748L, 2870763221L, 3624381080L, 310598401L, 607225278L, 1426881987L, 1925078388L, 2162078206L, 
/*  20 */       2614888103L, 3248222580L, 3835390401L, 4022224774L, 264347078L, 604807628L, 770255983L, 1249150122L, 
/*  21 */       1555081692L, 1996064986L, 2554220882L, 2821834349L, 2952996808L, 3210313671L, 3336571891L, 3584528711L, 
/*  22 */       113926993L, 338241895L, 666307205L, 773529912L, 1294757372L, 1396182291L, 1695183700L, 1986661051L, 
/*  23 */       2177026350L, 2456956037L, 2730485921L, 2820302411L, 3259730800L, 3345764771L, 3516065817L, 3600352804L, 
/*  24 */       4094571909L, 275423344L, 430227734L, 506948616L, 659060556L, 883997877L, 958139571L, 1322822218L, 
/*  25 */       1537002063L, 1747873779L, 1955562222L, 2024104815L, 2227730452L, 2361852424L, 2428436474L, 2756734187L, 
/*  26 */       3204031479L, 3329325298L };
/*     */   
/*     */   private Wire[] unpaddedInputs;
/*     */   
/*     */   private int bitwidthPerInputElement;
/*     */   
/*     */   private int totalLengthInBytes;
/*     */   
/*     */   private int numBlocks;
/*     */   
/*     */   private boolean binaryOutput;
/*     */   
/*     */   private boolean paddingRequired;
/*     */   private Wire[] preparedInputBits;
/*     */   private Wire[] output;
/*     */   
/*     */   public SHA256Gadget(Wire[] ins, int bitWidthPerInputElement, int totalLengthInBytes, boolean binaryOutput, boolean paddingRequired, String... desc) {
/*  43 */     super(desc);
/*  44 */     if (totalLengthInBytes * 8 > ins.length * bitWidthPerInputElement || 
/*  45 */       totalLengthInBytes * 8 < (ins.length - 1) * bitWidthPerInputElement) {
/*  46 */       throw new IllegalArgumentException("Inconsistent Length Information");
/*     */     }
/*     */     
/*  49 */     if (!paddingRequired && totalLengthInBytes % 64 != 0 && 
/*  50 */       ins.length * bitWidthPerInputElement != totalLengthInBytes) {
/*  51 */       throw new IllegalArgumentException("When padding is not forced, totalLengthInBytes % 64 must be zero.");
/*     */     }
/*     */     
/*  54 */     this.unpaddedInputs = ins;
/*  55 */     this.bitwidthPerInputElement = bitWidthPerInputElement;
/*  56 */     this.totalLengthInBytes = totalLengthInBytes;
/*  57 */     this.binaryOutput = binaryOutput;
/*  58 */     this.paddingRequired = paddingRequired;
/*     */     
/*  60 */     buildCircuit();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void buildCircuit() {
/*  69 */     prepare();
/*     */ 
/*     */     
/*  72 */     Wire[] outDigest = new Wire[8];
/*  73 */     Wire[] hWires = new Wire[H.length];
/*  74 */     for (int i = 0; i < H.length; i++) {
/*  75 */       hWires[i] = this.generator.__createConstantWire(H[i], new String[0]);
/*     */     }
/*     */     
/*  78 */     for (int blockNum = 0; blockNum < this.numBlocks; blockNum++) {
/*     */       
/*  80 */       Wire[][] wsSplitted = new Wire[64][];
/*  81 */       Wire[] w = new Wire[64];
/*     */       
/*  83 */       for (int j = 0; j < 64; j++) {
/*  84 */         if (j < 16) {
/*  85 */           wsSplitted[j] = Util.reverseBytes(Arrays.<Wire>copyOfRange(this.preparedInputBits, blockNum * 512 + j * 32, 
/*  86 */                 blockNum * 512 + (j + 1) * 32));
/*     */           
/*  88 */           w[j] = (new WireArray(wsSplitted[j])).packAsBits(32, new String[0]);
/*     */         } else {
/*  90 */           Wire t1 = w[j - 15].rotateRight(32, 7, new String[0]);
/*  91 */           Wire t2 = w[j - 15].rotateRight(32, 18, new String[0]);
/*  92 */           Wire t3 = w[j - 15].shiftRight(32, 3, new String[0]);
/*  93 */           Wire s0 = t1.xorBitwise(t2, 32, new String[0]);
/*  94 */           s0 = s0.xorBitwise(t3, 32, new String[0]);
/*     */           
/*  96 */           Wire t4 = w[j - 2].rotateRight(32, 17, new String[0]);
/*  97 */           Wire t5 = w[j - 2].rotateRight(32, 19, new String[0]);
/*  98 */           Wire t6 = w[j - 2].shiftRight(32, 10, new String[0]);
/*  99 */           Wire s1 = t4.xorBitwise(t5, 32, new String[0]);
/* 100 */           s1 = s1.xorBitwise(t6, 32, new String[0]);
/*     */           
/* 102 */           w[j] = w[j - 16].add(w[j - 7], new String[0]);
/* 103 */           w[j] = w[j].add(s0, new String[0]).add(s1, new String[0]);
/* 104 */           w[j] = w[j].trimBits(34, 32, new String[0]);
/*     */         } 
/*     */       } 
/*     */       
/* 108 */       Wire a = hWires[0];
/* 109 */       Wire b = hWires[1];
/* 110 */       Wire c = hWires[2];
/* 111 */       Wire d = hWires[3];
/* 112 */       Wire e = hWires[4];
/* 113 */       Wire f = hWires[5];
/* 114 */       Wire g = hWires[6];
/* 115 */       Wire h = hWires[7];
/*     */       
/* 117 */       for (int k = 0; k < 64; k++) {
/*     */         
/* 119 */         Wire maj, t1 = e.rotateRight(32, 6, new String[0]);
/* 120 */         Wire t2 = e.rotateRight(32, 11, new String[0]);
/* 121 */         Wire t3 = e.rotateRight(32, 25, new String[0]);
/* 122 */         Wire s1 = t1.xorBitwise(t2, 32, new String[0]);
/* 123 */         s1 = s1.xorBitwise(t3, 32, new String[0]);
/*     */         
/* 125 */         Wire ch = computeCh(e, f, g, 32);
/*     */         
/* 127 */         Wire t4 = a.rotateRight(32, 2, new String[0]);
/* 128 */         Wire t5 = a.rotateRight(32, 13, new String[0]);
/* 129 */         Wire t6 = a.rotateRight(32, 22, new String[0]);
/* 130 */         Wire s0 = t4.xorBitwise(t5, 32, new String[0]);
/* 131 */         s0 = s0.xorBitwise(t6, 32, new String[0]);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 136 */         if (k % 2 == 1) {
/* 137 */           maj = computeMaj(c, b, a, 32);
/*     */         } else {
/*     */           
/* 140 */           maj = computeMaj(a, b, c, 32);
/*     */         } 
/*     */         
/* 143 */         Wire temp1 = w[k].add(K[k], new String[0]).add(s1, new String[0]).add(h, new String[0]).add(ch, new String[0]);
/*     */         
/* 145 */         Wire temp2 = maj.add(s0, new String[0]);
/*     */         
/* 147 */         h = g;
/* 148 */         g = f;
/* 149 */         f = e;
/* 150 */         e = temp1.add(d, new String[0]);
/* 151 */         e = e.trimBits(35, 32, new String[0]);
/*     */         
/* 153 */         d = c;
/* 154 */         c = b;
/* 155 */         b = a;
/* 156 */         a = temp2.add(temp1, new String[0]);
/* 157 */         a = a.trimBits(35, 32, new String[0]);
/*     */       } 
/*     */ 
/*     */       
/* 161 */       hWires[0] = hWires[0].add(a, new String[0]).trimBits(33, 32, new String[0]);
/* 162 */       hWires[1] = hWires[1].add(b, new String[0]).trimBits(33, 32, new String[0]);
/* 163 */       hWires[2] = hWires[2].add(c, new String[0]).trimBits(33, 32, new String[0]);
/* 164 */       hWires[3] = hWires[3].add(d, new String[0]).trimBits(33, 32, new String[0]);
/* 165 */       hWires[4] = hWires[4].add(e, new String[0]).trimBits(33, 32, new String[0]);
/* 166 */       hWires[5] = hWires[5].add(f, new String[0]).trimBits(33, 32, new String[0]);
/* 167 */       hWires[6] = hWires[6].add(g, new String[0]).trimBits(33, 32, new String[0]);
/* 168 */       hWires[7] = hWires[7].add(h, new String[0]).trimBits(33, 32, new String[0]);
/*     */     } 
/*     */     
/* 171 */     outDigest[0] = hWires[0];
/* 172 */     outDigest[1] = hWires[1];
/* 173 */     outDigest[2] = hWires[2];
/* 174 */     outDigest[3] = hWires[3];
/* 175 */     outDigest[4] = hWires[4];
/* 176 */     outDigest[5] = hWires[5];
/* 177 */     outDigest[6] = hWires[6];
/* 178 */     outDigest[7] = hWires[7];
/*     */     
/* 180 */     if (!this.binaryOutput) {
/* 181 */       this.output = outDigest;
/*     */     } else {
/* 183 */       this.output = new Wire[256];
/* 184 */       for (int j = 0; j < 8; j++) {
/* 185 */         Wire[] bits = outDigest[j].getBitWires(32, new String[0]).asArray();
/* 186 */         for (int k = 0; k < 32; k++) {
/* 187 */           this.output[k + j * 32] = bits[k];
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Wire computeMaj(Wire a, Wire b, Wire c, int numBits) {
/* 195 */     Wire[] result = new Wire[numBits];
/* 196 */     Wire[] aBits = a.getBitWires(numBits, new String[0]).asArray();
/* 197 */     Wire[] bBits = b.getBitWires(numBits, new String[0]).asArray();
/* 198 */     Wire[] cBits = c.getBitWires(numBits, new String[0]).asArray();
/*     */     
/* 200 */     for (int i = 0; i < numBits; i++) {
/* 201 */       Wire t1 = aBits[i].mul(bBits[i], new String[0]);
/* 202 */       Wire t2 = aBits[i].add(bBits[i], new String[0]).add(t1.mul(-2L, new String[0]), new String[0]);
/* 203 */       result[i] = t1.add(cBits[i].mul(t2, new String[0]), new String[0]);
/*     */     } 
/* 205 */     return (new WireArray(result)).packAsBits(new String[0]);
/*     */   }
/*     */   
/*     */   private Wire computeCh(Wire a, Wire b, Wire c, int numBits) {
/* 209 */     Wire[] result = new Wire[numBits];
/*     */     
/* 211 */     Wire[] aBits = a.getBitWires(numBits, new String[0]).asArray();
/* 212 */     Wire[] bBits = b.getBitWires(numBits, new String[0]).asArray();
/* 213 */     Wire[] cBits = c.getBitWires(numBits, new String[0]).asArray();
/*     */     
/* 215 */     for (int i = 0; i < numBits; i++) {
/* 216 */       Wire t1 = bBits[i].sub(cBits[i], new String[0]);
/* 217 */       Wire t2 = t1.mul(aBits[i], new String[0]);
/* 218 */       result[i] = t2.add(cBits[i], new String[0]);
/*     */     } 
/* 220 */     return (new WireArray(result)).packAsBits(new String[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   private void prepare() {
/* 225 */     this.numBlocks = (int)Math.ceil(this.totalLengthInBytes * 1.0D / 64.0D);
/* 226 */     if (this.numBlocks == 0) {
/* 227 */       this.numBlocks++;
/*     */     }
/* 229 */     Wire[] bits = (new WireArray(this.unpaddedInputs)).getBits(this.bitwidthPerInputElement, new String[0]).asArray();
/* 230 */     int tailLength = this.totalLengthInBytes % 64;
/* 231 */     if (this.paddingRequired) {
/*     */       Wire[] pad;
/* 233 */       if (64 - tailLength >= 9) {
/* 234 */         pad = new Wire[64 - tailLength];
/*     */       } else {
/* 236 */         pad = new Wire[128 - tailLength];
/* 237 */         this.numBlocks++;
/*     */       } 
/* 239 */       pad[0] = this.generator.__createConstantWire(128L, new String[0]);
/* 240 */       for (int i = 1; i < pad.length - 8; i++) {
/* 241 */         pad[i] = this.generator.__getZeroWire();
/*     */       }
/* 243 */       long lengthInBits = (this.totalLengthInBytes * 8);
/* 244 */       Wire[] lengthBits = new Wire[64];
/* 245 */       for (int j = 0; j < 8; j++) {
/* 246 */         pad[pad.length - 1 - j] = this.generator.__createConstantWire(lengthInBits >>> 8 * j & 0xFFL, new String[0]);
/* 247 */         Wire[] tmp = pad[pad.length - 1 - j].getBitWires(8, new String[0]).asArray();
/* 248 */         System.arraycopy(tmp, 0, lengthBits, (7 - j) * 8, 8);
/*     */       } 
/* 250 */       int totalNumberOfBits = this.numBlocks * 512;
/* 251 */       this.preparedInputBits = new Wire[totalNumberOfBits];
/* 252 */       Arrays.fill((Object[])this.preparedInputBits, this.generator.__getZeroWire());
/* 253 */       System.arraycopy(bits, 0, this.preparedInputBits, 0, bits.length);
/* 254 */       this.preparedInputBits[bits.length + 7] = this.generator.__getOneWire();
/* 255 */       System.arraycopy(lengthBits, 0, this.preparedInputBits, this.preparedInputBits.length - 64, 64);
/*     */     } else {
/* 257 */       this.preparedInputBits = bits;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire[] getOutputWires() {
/* 266 */     return this.output;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\SHA256Gadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */