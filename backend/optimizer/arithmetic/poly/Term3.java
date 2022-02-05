/*     */ package backend.optimizer.arithmetic.poly;
/*     */ 
/*     */ import backend.config.Config;
/*     */ import backend.resource.ResourceBundle;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Term3
/*     */   implements Iterable<OptVariable>
/*     */ {
/*     */   OptVariable[] vars;
/*     */   short[] powers;
/*     */   BigInteger coeff;
/*     */   short numVars;
/*  20 */   static int threshold = 20;
/*  21 */   static int step = 2;
/*     */   
/*     */   Term3() {
/*  24 */     this.vars = new OptVariable[step];
/*  25 */     this.powers = new short[step];
/*  26 */     this.coeff = BigInteger.ONE;
/*  27 */     this.numVars = 0;
/*     */   }
/*     */   
/*     */   Term3(Term3 t) {
/*  31 */     this.vars = Arrays.<OptVariable>copyOf(t.vars, t.vars.length);
/*  32 */     this.powers = Arrays.copyOf(t.powers, t.powers.length);
/*  33 */     this.coeff = t.coeff;
/*  34 */     this.numVars = t.numVars;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Term3(OptVariable optVariable) {
/*  40 */     this();
/*  41 */     this.powers[0] = 1;
/*  42 */     this.vars[0] = optVariable;
/*     */     
/*  44 */     this.coeff = BigInteger.ONE;
/*  45 */     this.numVars = 1;
/*     */   }
/*     */   
/*     */   public Term3(OptVariable optVariable, short p) {
/*  49 */     this();
/*  50 */     this.powers[0] = p;
/*  51 */     this.vars[0] = optVariable;
/*  52 */     this.coeff = BigInteger.ONE;
/*  53 */     this.numVars = 1;
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
/*     */   public Term3(BigInteger coeff) {
/*  67 */     this();
/*  68 */     this.coeff = ResourceBundle.getInstance().getBigInteger(coeff);
/*     */   }
/*     */ 
/*     */   
/*     */   public Term3(OptVariable[] vars, short[] powers, BigInteger coeff, short numVars) {
/*  73 */     this.vars = vars;
/*  74 */     this.powers = powers;
/*  75 */     this.coeff = coeff;
/*  76 */     this.numVars = numVars;
/*     */   }
/*     */ 
/*     */   
/*     */   private void reallocate() {
/*  81 */     OptVariable[] tmpOptVarArray = new OptVariable[this.numVars + step];
/*  82 */     short[] tmpExponents = new short[this.numVars + step];
/*  83 */     System.arraycopy(this.vars, 0, tmpOptVarArray, 0, this.numVars);
/*  84 */     System.arraycopy(this.powers, 0, tmpExponents, 0, this.numVars);
/*  85 */     this.vars = tmpOptVarArray;
/*  86 */     this.powers = tmpExponents;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasOptvar(OptVariable x) {
/*     */     byte b;
/*     */     int i;
/*     */     OptVariable[] arrayOfOptVariable;
/* 116 */     for (i = (arrayOfOptVariable = this.vars).length, b = 0; b < i; ) { OptVariable y = arrayOfOptVariable[b];
/* 117 */       if (y.equals(x))
/* 118 */         return true;  b++; }
/* 119 */      return false;
/*     */   }
/*     */   
/*     */   public int getIdx(OptVariable x) {
/* 123 */     for (int i = 0; i < this.numVars; i++) {
/* 124 */       if (this.vars[i].equals(x)) {
/* 125 */         return i;
/*     */       }
/*     */     } 
/* 128 */     return -1;
/*     */   }
/*     */   
/*     */   public short getExponent(OptVariable x) {
/* 132 */     for (int i = 0; i < this.numVars; i++) {
/* 133 */       if (this.vars[i].equals(x)) {
/* 134 */         return this.powers[i];
/*     */       }
/*     */     } 
/* 137 */     return 0;
/*     */   }
/*     */   
/*     */   public void setExponent(OptVariable v, short exp) {
/* 141 */     if (exp == 0) {
/*     */       return;
/*     */     }
/* 144 */     int idx = getIdx(v);
/* 145 */     if (idx == -1) {
/* 146 */       if (this.numVars == this.vars.length) {
/* 147 */         reallocate();
/*     */       }
/* 149 */       this.vars[this.numVars] = v;
/* 150 */       if (!v.isBit) {
/* 151 */         this.powers[this.numVars] = exp;
/*     */       } else {
/* 153 */         this.powers[this.numVars] = 1;
/*     */       } 
/* 155 */       this.numVars = (short)(this.numVars + 1);
/*     */     }
/* 157 */     else if (!v.isBit) {
/* 158 */       this.powers[idx] = (short)(this.powers[idx] + exp);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Term3 multiply(Term3 t) {
/* 167 */     Term3 result = new Term3(this); byte b; int i; OptVariable[] arrayOfOptVariable;
/* 168 */     for (i = (arrayOfOptVariable = t.vars).length, b = 0; b < i; ) { OptVariable v = arrayOfOptVariable[b];
/* 169 */       result.setExponent(v, t.getExponent(v));
/*     */       
/*     */       b++; }
/*     */     
/* 173 */     BigInteger newCoeff = ResourceBundle.getInstance().getBigInteger(this.coeff.multiply(t.coeff).mod(Config.getFiniteFieldModulus()));
/* 174 */     result.coeff = newCoeff;
/*     */     
/* 176 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Term3 addToConstant(BigInteger constant) {
/* 182 */     BigInteger newCoeff = this.coeff.add(constant).mod(Config.getFiniteFieldModulus());
/* 183 */     newCoeff = ResourceBundle.getInstance().getBigInteger(newCoeff);
/* 184 */     return new Term3(this.vars, this.powers, newCoeff, this.numVars);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Term3 multiplyConstant(BigInteger constant) {
/* 192 */     BigInteger newCoeff = this.coeff.multiply(constant).mod(Config.getFiniteFieldModulus());
/* 193 */     newCoeff = ResourceBundle.getInstance().getBigInteger(newCoeff);
/* 194 */     return new Term3(this.vars, this.powers, newCoeff, this.numVars);
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
/*     */   public int hashCode() {
/* 206 */     int h = 0;
/* 207 */     for (int i = 0; i < this.numVars; i++) {
/* 208 */       h += this.vars[i].hashCode() + this.powers[i];
/*     */     }
/* 210 */     return h;
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/* 214 */     if (o == this)
/* 215 */       return true; 
/* 216 */     if (o instanceof Term3) {
/*     */ 
/*     */       
/* 219 */       Term3 t1 = this;
/* 220 */       Term3 t2 = (Term3)o;
/*     */ 
/*     */       
/* 223 */       if (t1.numVars != t2.numVars) {
/* 224 */         return false;
/*     */       }
/* 226 */       boolean check = true;
/*     */       
/* 228 */       for (int i = 0; i < t1.numVars; i++) {
/* 229 */         short p = this.powers[i];
/* 230 */         OptVariable v = this.vars[i];
/* 231 */         if (p != t2.getExponent(v)) {
/* 232 */           check = false;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 241 */       return check;
/*     */     } 
/*     */     
/* 244 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     String result;
/* 251 */     if (this.coeff.equals(BigInteger.ONE)) {
/* 252 */       result = "";
/* 253 */       if (this.numVars == 0) {
/* 254 */         result = "1";
/*     */       }
/*     */     } else {
/*     */       
/* 258 */       result = this.coeff.toString();
/* 259 */       if (this.numVars > 0) {
/* 260 */         result = String.valueOf(result) + "*";
/*     */       }
/*     */     } 
/*     */     
/* 264 */     for (int i = 0; i < this.numVars; i++) {
/* 265 */       Short p = Short.valueOf(this.powers[i]);
/* 266 */       OptVariable v = this.vars[i];
/* 267 */       if (p.shortValue() == 1) {
/* 268 */         result = String.valueOf(result) + v;
/*     */       } else {
/*     */         
/* 271 */         result = String.valueOf(result) + v + "^" + p;
/*     */       } 
/* 273 */       if (i != this.numVars - 1) {
/* 274 */         result = String.valueOf(result) + "*";
/*     */       }
/*     */     } 
/* 277 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public BigInteger getCoeff() {
/* 282 */     return this.coeff;
/*     */   }
/*     */   
/*     */   public int getCost() {
/* 286 */     int cost = 0;
/* 287 */     boolean init = false;
/* 288 */     for (int i = 0; i < this.numVars; i++) {
/* 289 */       Short p = Short.valueOf(this.powers[i]);
/* 290 */       cost += (int)Math.floor(Math.log10(p.shortValue()) / Math.log10(2.0D)) + Integer.bitCount(p.shortValue()) - 1;
/* 291 */       if (init) {
/* 292 */         cost++;
/*     */       }
/* 294 */       init = true;
/*     */     } 
/* 296 */     return cost;
/*     */   }
/*     */   
/*     */   public class VarIterator
/*     */     implements Iterator<OptVariable>
/*     */   {
/* 302 */     int idx = 0;
/*     */     
/*     */     public boolean hasNext() {
/* 305 */       return (this.idx < Term3.this.numVars);
/*     */     }
/*     */ 
/*     */     
/*     */     public OptVariable next() {
/* 310 */       return Term3.this.vars[this.idx++];
/*     */     }
/*     */ 
/*     */     
/*     */     public short getExponent() {
/* 315 */       return Term3.this.powers[this.idx - 1];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<OptVariable> iterator() {
/* 322 */     return new VarIterator();
/*     */   }
/*     */   
/*     */   public VarIterator getVarIterator() {
/* 326 */     return new VarIterator();
/*     */   }
/*     */   
/*     */   public Collection<OptVariable> getVars() {
/* 330 */     return Arrays.<OptVariable>asList(this.vars).subList(0, this.numVars);
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\poly\Term3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */