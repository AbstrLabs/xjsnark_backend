/*     */ package backend.optimizer.arithmetic;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ 
/*     */ public class Cube
/*     */ {
/*     */   private int[] varPowers;
/*     */   private int[] constPowers;
/*     */   private int serial;
/*     */   private int numOfVars;
/*     */   private boolean sign;
/*     */   private BigInteger constant;
/*     */   
/*     */   public static Cube getOneCube(int numVars, int numConstants) {
/*  19 */     return new Cube(new int[numVars], new int[numConstants]);
/*     */   }
/*     */   
/*     */   public int getSerial() {
/*  23 */     return this.serial;
/*     */   }
/*     */   
/*     */   public void setSerial(int serial) {
/*  27 */     this.serial = serial;
/*     */   }
/*     */   
/*     */   public int getNumOfLiterals() {
/*  31 */     return this.varPowers.length + this.constPowers.length;
/*     */   }
/*     */   
/*     */   public int getNumOfVars() {
/*  35 */     return this.varPowers.length;
/*     */   }
/*     */   
/*     */   public int getNumOfConstants() {
/*  39 */     return this.constPowers.length;
/*     */   }
/*     */   
/*     */   public Cube(int[] powers, BigInteger constant) {
/*  43 */     this.numOfVars = powers.length;
/*  44 */     this.varPowers = powers;
/*  45 */     this.constPowers = new int[0];
/*  46 */     this.constant = constant;
/*  47 */     this.sign = (constant.signum() == -1);
/*  48 */     if (this.sign)
/*  49 */       this.constant = this.constant.abs(); 
/*     */   }
/*     */   
/*     */   public Cube(int[] powers) {
/*  53 */     this(powers, BigInteger.ONE);
/*     */   }
/*     */   
/*     */   public Cube(int[] varPowers, int[] constPowers) {
/*  57 */     this.varPowers = varPowers;
/*  58 */     this.constPowers = constPowers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void extendWithConstantPowers(int numLiterals, HashMap<BigInteger, Integer> map) {
/*  68 */     int[] newPowers = new int[map.size()];
/*  69 */     if (map.get(this.constant) != null)
/*  70 */       newPowers[((Integer)map.get(this.constant)).intValue()] = 1; 
/*  71 */     this.constPowers = newPowers;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void extendWithIntermediateVars(int numIntermediate) {
/*  77 */     int[] newPowers = new int[this.varPowers.length + numIntermediate];
/*  78 */     System.arraycopy(this.varPowers, 0, newPowers, 0, this.varPowers.length);
/*  79 */     this.varPowers = newPowers;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void genSerial(ExpressionMinimizer minimizer) {
/*  86 */     if (this.serial == 0) {
/*  87 */       this.serial = minimizer.cubeGlobalCount++;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCost() {
/*  94 */     int s = 0;
/*  95 */     boolean init = false;
/*  96 */     for (int i = 0; i < this.varPowers.length; i++) {
/*  97 */       int v = this.varPowers[i];
/*  98 */       if (v != 0) {
/*     */         
/* 100 */         s += (int)Math.floor(Math.log10(v) / Math.log10(2.0D)) + Integer.bitCount(v) - 1;
/* 101 */         if (!init) {
/* 102 */           init = true;
/*     */         } else {
/*     */           
/* 105 */           s++;
/*     */         } 
/*     */       } 
/*     */     } 
/* 109 */     return s;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cube(Cube o) {
/* 114 */     this.varPowers = Arrays.copyOf(o.varPowers, o.varPowers.length);
/* 115 */     this.constPowers = Arrays.copyOf(o.constPowers, o.constPowers.length);
/* 116 */     this.sign = o.sign;
/* 117 */     this.numOfVars = o.numOfVars;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 122 */     if (o == this)
/* 123 */       return true; 
/* 124 */     if (!(o instanceof Cube)) {
/* 125 */       return false;
/*     */     }
/*     */     
/* 128 */     Cube other = (Cube)o;
/* 129 */     boolean case1 = (Arrays.equals(this.varPowers, other.varPowers) && Arrays.equals(this.constPowers, other.constPowers) && this.sign == other.sign);
/* 130 */     boolean case2 = false;
/* 131 */     return !(!case1 && !case2);
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean isOne() {
/* 136 */     if (!isConstant()) {
/* 137 */       return false;
/*     */     }
/* 139 */     int s = 0; byte b; int i, arrayOfInt[];
/* 140 */     for (i = (arrayOfInt = this.constPowers).length, b = 0; b < i; ) { int c = arrayOfInt[b];
/* 141 */       s += c; b++; }
/*     */     
/* 143 */     return (s == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public int[] getVarPowers() {
/* 148 */     return this.varPowers;
/*     */   }
/*     */   
/*     */   public int[] getConstPowers() {
/* 152 */     return this.constPowers;
/*     */   }
/*     */   
/*     */   public boolean isConstant() {
/* 156 */     int sum = 0;
/* 157 */     for (int i = 0; i < this.varPowers.length; i++) {
/* 158 */       sum += this.varPowers[i];
/*     */     }
/* 160 */     return (sum == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 165 */     String s = "";
/* 166 */     if (this.sign) {
/* 167 */       s = String.valueOf(s) + "-";
/*     */     } else {
/* 169 */       s = String.valueOf(s) + "+";
/* 170 */     }  byte b; int i, arrayOfInt[]; for (i = (arrayOfInt = this.varPowers).length, b = 0; b < i; ) { int a = arrayOfInt[b];
/* 171 */       s = String.valueOf(s) + a; b++; }
/*     */     
/* 173 */     s = String.valueOf(s) + "*";
/* 174 */     for (i = (arrayOfInt = this.constPowers).length, b = 0; b < i; ) { int a = arrayOfInt[b];
/* 175 */       s = String.valueOf(s) + a; b++; }
/*     */     
/* 177 */     return s;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 182 */     return Arrays.hashCode(this.varPowers);
/*     */   }
/*     */ 
/*     */   
/*     */   public int get(int i) {
/* 187 */     if (i < this.varPowers.length) {
/* 188 */       return this.varPowers[i];
/*     */     }
/*     */     
/* 191 */     return this.constPowers[i - this.varPowers.length];
/*     */   }
/*     */ 
/*     */   
/*     */   public Cube divide(int varIndex) {
/* 196 */     if (varIndex < this.varPowers.length && this.varPowers[varIndex] > 0) {
/* 197 */       int[] varPowers = Arrays.copyOf(this.varPowers, this.varPowers.length);
/* 198 */       int[] constPowers = Arrays.copyOf(this.constPowers, this.constPowers.length);
/* 199 */       varPowers[varIndex] = varPowers[varIndex] - 1;
/* 200 */       return new Cube(varPowers, constPowers);
/*     */     } 
/* 202 */     if (varIndex >= this.varPowers.length && this.constPowers[varIndex - this.varPowers.length] > 0) {
/* 203 */       int[] varPowers = Arrays.copyOf(this.varPowers, this.varPowers.length);
/* 204 */       int[] constPowers = Arrays.copyOf(this.constPowers, this.constPowers.length);
/* 205 */       constPowers[varIndex - varPowers.length] = constPowers[varIndex - varPowers.length] - 1;
/* 206 */       return new Cube(varPowers, constPowers);
/*     */     } 
/*     */     
/* 209 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Cube divide(Cube c) {
/*     */     int i;
/* 215 */     for (i = 0; i < this.varPowers.length; i++) {
/* 216 */       if (this.varPowers[i] < c.varPowers[i]) {
/* 217 */         return null;
/*     */       }
/*     */     } 
/* 220 */     for (i = 0; i < this.constPowers.length; i++) {
/* 221 */       if (this.constPowers[i] < c.constPowers[i]) {
/* 222 */         return null;
/*     */       }
/*     */     } 
/*     */     
/* 226 */     int[] newVarPowers = Arrays.copyOf(this.varPowers, this.varPowers.length);
/* 227 */     int[] newConstPowers = Arrays.copyOf(this.constPowers, this.constPowers.length);
/*     */     int j;
/* 229 */     for (j = 0; j < this.varPowers.length; j++) {
/* 230 */       newVarPowers[j] = newVarPowers[j] - c.varPowers[j];
/*     */     }
/* 232 */     for (j = 0; j < this.constPowers.length; j++) {
/* 233 */       newConstPowers[j] = newConstPowers[j] - c.constPowers[j];
/*     */     }
/* 235 */     return new Cube(newVarPowers, newConstPowers);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Cube add(Cube c1, Cube c2) {
/* 240 */     if (c1.isOne())
/* 241 */       return new Cube(c2); 
/* 242 */     if (c2.isOne())
/* 243 */       return new Cube(c1); 
/* 244 */     int[] varPowers = Arrays.copyOf(c1.varPowers, c1.varPowers.length);
/* 245 */     for (int i = 0; i < c1.varPowers.length; i++) {
/* 246 */       varPowers[i] = varPowers[i] + c2.varPowers[i];
/*     */     }
/* 248 */     int[] constPowers = Arrays.copyOf(c1.constPowers, c1.constPowers.length);
/* 249 */     for (int j = 0; j < c1.constPowers.length; j++) {
/* 250 */       constPowers[j] = constPowers[j] + c2.constPowers[j];
/*     */     }
/* 252 */     return new Cube(varPowers, constPowers);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Cube largestDividingCube(ArrayList<Cube> powers) {
/* 257 */     if (powers.size() == 0) {
/* 258 */       return null;
/*     */     }
/* 260 */     Cube c = powers.get(0);
/* 261 */     int[] varPowers = new int[c.varPowers.length];
/* 262 */     Arrays.fill(varPowers, 2147483647);
/* 263 */     int[] constPowers = new int[c.constPowers.length];
/* 264 */     Arrays.fill(constPowers, 2147483647);
/* 265 */     for (Cube tmp : powers) {
/*     */       int i;
/*     */ 
/*     */ 
/*     */       
/* 270 */       for (i = 0; i < varPowers.length; i++) {
/* 271 */         if (tmp.varPowers[i] < varPowers[i]) {
/* 272 */           varPowers[i] = tmp.varPowers[i];
/*     */         }
/*     */       } 
/* 275 */       for (i = 0; i < constPowers.length; i++) {
/* 276 */         if (tmp.constPowers[i] < constPowers[i]) {
/* 277 */           constPowers[i] = tmp.constPowers[i];
/*     */         }
/*     */       } 
/*     */     } 
/* 281 */     return new Cube(varPowers, constPowers);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getPower(int k) {
/* 286 */     if (k < this.varPowers.length) {
/* 287 */       return this.varPowers[k];
/*     */     }
/*     */     
/* 290 */     return this.constPowers[k - this.varPowers.length];
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\Cube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */