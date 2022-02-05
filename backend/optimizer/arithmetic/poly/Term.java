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
/*     */ public class Term
/*     */   implements Iterable<OptVariable>
/*     */ {
/*     */   OptVariable[] vars;
/*     */   short[] powers;
/*     */   BigInteger coeff;
/*     */   short numVars;
/*  18 */   static int threshold = 20;
/*  19 */   static int step = 1;
/*     */   
/*     */   Term() {
/*  22 */     this.vars = new OptVariable[step];
/*  23 */     this.powers = new short[step];
/*  24 */     this.coeff = BigInteger.ONE;
/*  25 */     this.numVars = 0;
/*     */   }
/*     */   
/*     */   Term(Term t) {
/*  29 */     this.vars = Arrays.<OptVariable>copyOf(t.vars, t.vars.length);
/*  30 */     this.powers = Arrays.copyOf(t.powers, t.powers.length);
/*  31 */     this.coeff = t.coeff;
/*  32 */     this.numVars = t.numVars;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Term(OptVariable optVariable) {
/*  38 */     this();
/*  39 */     this.powers[0] = 1;
/*  40 */     this.vars[0] = optVariable;
/*     */     
/*  42 */     this.coeff = BigInteger.ONE;
/*  43 */     this.numVars = 1;
/*     */   }
/*     */   
/*     */   public Term(OptVariable optVariable, short p) {
/*  47 */     this();
/*  48 */     this.powers[0] = p;
/*  49 */     this.vars[0] = optVariable;
/*  50 */     this.coeff = BigInteger.ONE;
/*  51 */     this.numVars = 1;
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
/*     */   public Term(BigInteger coeff) {
/*  64 */     this();
/*  65 */     this.coeff = ResourceBundle.getInstance().getBigInteger(coeff);
/*     */   }
/*     */ 
/*     */   
/*     */   public Term(OptVariable[] vars, short[] powers, BigInteger coeff, short numVars) {
/*  70 */     this.vars = vars;
/*  71 */     this.powers = powers;
/*  72 */     this.coeff = coeff;
/*  73 */     this.numVars = numVars;
/*     */   }
/*     */ 
/*     */   
/*     */   private void reallocate() {
/*  78 */     OptVariable[] tmpOptVarArray = new OptVariable[this.numVars + step];
/*  79 */     short[] tmpExponents = new short[this.numVars + step];
/*  80 */     System.arraycopy(this.vars, 0, tmpOptVarArray, 0, this.numVars);
/*  81 */     System.arraycopy(this.powers, 0, tmpExponents, 0, this.numVars);
/*  82 */     this.vars = tmpOptVarArray;
/*  83 */     this.powers = tmpExponents;
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
/* 113 */     for (i = (arrayOfOptVariable = this.vars).length, b = 0; b < i; ) { OptVariable y = arrayOfOptVariable[b];
/* 114 */       if (y.equals(x))
/* 115 */         return true;  b++; }
/* 116 */      return false;
/*     */   }
/*     */   
/*     */   public int getIdx(OptVariable x) {
/* 120 */     for (int i = 0; i < this.numVars; i++) {
/* 121 */       if (this.vars[i].equals(x)) {
/* 122 */         return i;
/*     */       }
/*     */     } 
/* 125 */     return -1;
/*     */   }
/*     */   
/*     */   public short getExponent(OptVariable x) {
/* 129 */     for (int i = 0; i < this.numVars; i++) {
/* 130 */       if (this.vars[i].equals(x)) {
/* 131 */         return this.powers[i];
/*     */       }
/*     */     } 
/* 134 */     return 0;
/*     */   }
/*     */   
/*     */   public void setExponent(OptVariable v, short exp) {
/* 138 */     if (exp == 0) {
/*     */       return;
/*     */     }
/* 141 */     int idx = getIdx(v);
/* 142 */     if (idx == -1) {
/* 143 */       if (this.numVars == this.vars.length) {
/* 144 */         reallocate();
/*     */       }
/* 146 */       this.vars[this.numVars] = v;
/* 147 */       if (!v.isBit) {
/* 148 */         this.powers[this.numVars] = exp;
/*     */       } else {
/* 150 */         this.powers[this.numVars] = 1;
/*     */       } 
/* 152 */       this.numVars = (short)(this.numVars + 1);
/*     */     }
/* 154 */     else if (!v.isBit) {
/* 155 */       this.powers[idx] = (short)(this.powers[idx] + exp);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Term multiply(Term t) {
/* 164 */     Term result = new Term(this); byte b;
/*     */     int i;
/*     */     OptVariable[] arrayOfOptVariable;
/* 167 */     for (i = (arrayOfOptVariable = t.vars).length, b = 0; b < i; ) { OptVariable v = arrayOfOptVariable[b];
/* 168 */       result.setExponent(v, t.getExponent(v));
/*     */       
/*     */       b++; }
/*     */     
/* 172 */     BigInteger newCoeff = ResourceBundle.getInstance().getBigInteger(this.coeff.multiply(t.coeff).mod(Config.getFiniteFieldModulus()));
/* 173 */     result.coeff = newCoeff;
/*     */     
/* 175 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Term addToConstant(BigInteger constant) {
/* 181 */     BigInteger newCoeff = this.coeff.add(constant).mod(Config.getFiniteFieldModulus());
/* 182 */     newCoeff = ResourceBundle.getInstance().getBigInteger(newCoeff);
/* 183 */     return new Term(this.vars, this.powers, newCoeff, this.numVars);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Term multiplyConstant(BigInteger constant) {
/* 191 */     BigInteger newCoeff = this.coeff.multiply(constant).mod(Config.getFiniteFieldModulus());
/* 192 */     newCoeff = ResourceBundle.getInstance().getBigInteger(newCoeff);
/* 193 */     return new Term(this.vars, this.powers, newCoeff, this.numVars);
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
/* 205 */     int h = 0;
/* 206 */     for (int i = 0; i < this.numVars; i++) {
/* 207 */       h += this.vars[i].hashCode() + this.powers[i];
/*     */     }
/* 209 */     return h;
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/* 213 */     if (o == this)
/* 214 */       return true; 
/* 215 */     if (o instanceof Term) {
/*     */ 
/*     */       
/* 218 */       Term t1 = this;
/* 219 */       Term t2 = (Term)o;
/*     */ 
/*     */       
/* 222 */       if (t1.numVars != t2.numVars) {
/* 223 */         return false;
/*     */       }
/* 225 */       boolean check = true;
/*     */       
/* 227 */       for (int i = 0; i < t1.numVars; i++) {
/* 228 */         short p = this.powers[i];
/* 229 */         OptVariable v = this.vars[i];
/* 230 */         if (p != t2.getExponent(v)) {
/* 231 */           check = false;
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 240 */       return check;
/*     */     } 
/*     */     
/* 243 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     String result;
/* 250 */     if (this.coeff.equals(BigInteger.ONE)) {
/* 251 */       result = "";
/* 252 */       if (this.numVars == 0) {
/* 253 */         result = "1";
/*     */       }
/*     */     } else {
/*     */       
/* 257 */       result = this.coeff.toString();
/* 258 */       if (this.numVars > 0) {
/* 259 */         result = String.valueOf(result) + "*";
/*     */       }
/*     */     } 
/*     */     
/* 263 */     for (int i = 0; i < this.numVars; i++) {
/* 264 */       Short p = Short.valueOf(this.powers[i]);
/* 265 */       OptVariable v = this.vars[i];
/* 266 */       if (p.shortValue() == 1) {
/* 267 */         result = String.valueOf(result) + v;
/*     */       } else {
/*     */         
/* 270 */         result = String.valueOf(result) + v + "^" + p;
/*     */       } 
/* 272 */       if (i != this.numVars - 1) {
/* 273 */         result = String.valueOf(result) + "*";
/*     */       }
/*     */     } 
/* 276 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public BigInteger getCoeff() {
/* 281 */     return this.coeff;
/*     */   }
/*     */   
/*     */   public int getCost() {
/* 285 */     int cost = 0;
/* 286 */     boolean init = false;
/* 287 */     for (int i = 0; i < this.numVars; i++) {
/* 288 */       Short p = Short.valueOf(this.powers[i]);
/* 289 */       cost += (int)Math.floor(Math.log10(p.shortValue()) / Math.log10(2.0D)) + Integer.bitCount(p.shortValue()) - 1;
/* 290 */       if (init) {
/* 291 */         cost++;
/*     */       }
/* 293 */       init = true;
/*     */     } 
/* 295 */     return cost;
/*     */   }
/*     */   
/*     */   public class VarIterator
/*     */     implements Iterator<OptVariable>
/*     */   {
/* 301 */     int idx = 0;
/*     */     
/*     */     public boolean hasNext() {
/* 304 */       return (this.idx < Term.this.numVars);
/*     */     }
/*     */ 
/*     */     
/*     */     public OptVariable next() {
/* 309 */       return Term.this.vars[this.idx++];
/*     */     }
/*     */ 
/*     */     
/*     */     public short getExponent() {
/* 314 */       return Term.this.powers[this.idx - 1];
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<OptVariable> iterator() {
/* 321 */     return new VarIterator();
/*     */   }
/*     */   
/*     */   public VarIterator getVarIterator() {
/* 325 */     return new VarIterator();
/*     */   }
/*     */   
/*     */   public Collection<OptVariable> getVars() {
/* 329 */     return Arrays.<OptVariable>asList(this.vars).subList(0, this.numVars);
/*     */   }
/*     */   
/*     */   public int getVarCount() {
/* 333 */     return this.numVars;
/*     */   }
/*     */   
/*     */   public short getMaxExponent() {
/* 337 */     short max = 0;
/* 338 */     for (int i = 0; i < this.numVars; i++) {
/* 339 */       if (this.powers[i] > max) {
/* 340 */         max = this.powers[i];
/*     */       }
/*     */     } 
/* 343 */     return max;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\poly\Term.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */