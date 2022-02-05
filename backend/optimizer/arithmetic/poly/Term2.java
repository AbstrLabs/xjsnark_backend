/*     */ package backend.optimizer.arithmetic.poly;
/*     */ 
/*     */ import backend.config.Config;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Term2
/*     */ {
/*     */   ArrayList<OptVariable> optVariables;
/*     */   ArrayList<Short> vals;
/*     */   BigInteger coeff;
/*     */   
/*     */   public Term2() {
/*  22 */     this.optVariables = new ArrayList<>();
/*  23 */     this.vals = new ArrayList<>();
/*  24 */     this.coeff = BigInteger.ONE;
/*     */   }
/*     */   
/*     */   public Term2(OptVariable optVariable) {
/*  28 */     this();
/*  29 */     this.optVariables.add(optVariable);
/*  30 */     this.vals.add(Short.valueOf((short)1));
/*     */     
/*  32 */     this.coeff = BigInteger.ONE;
/*     */   }
/*     */ 
/*     */   
/*     */   public Term2(ArrayList<OptVariable> optVariables, ArrayList<Short> vals, BigInteger coeff) {
/*  37 */     this.optVariables = optVariables;
/*  38 */     this.vals = vals;
/*  39 */     this.coeff = coeff;
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
/*     */   public Term2(BigInteger coeff) {
/*  51 */     this();
/*  52 */     this.coeff = coeff;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Term2 multiply(Term2 t) {
/*  59 */     ArrayList<OptVariable> newOptVariables = new ArrayList<>(this.optVariables);
/*  60 */     ArrayList<Short> newVals = new ArrayList<>(this.vals);
/*     */     
/*  62 */     int idx = -1;
/*  63 */     for (OptVariable v : t.optVariables) {
/*  64 */       idx++;
/*  65 */       if (this.optVariables.contains(v)) {
/*  66 */         if (!v.isBit) {
/*  67 */           int idx1 = this.optVariables.indexOf(v);
/*     */           
/*  69 */           newVals.set(idx1, Short.valueOf((short)(((Short)this.vals.get(idx1)).shortValue() + ((Short)t.vals.get(idx)).shortValue())));
/*     */         } 
/*     */         continue;
/*     */       } 
/*  73 */       newOptVariables.add(v);
/*  74 */       newVals.add(Short.valueOf((short)((Short)t.vals.get(idx)).shortValue()));
/*     */     } 
/*     */ 
/*     */     
/*  78 */     BigInteger newCoeff = this.coeff.multiply(t.coeff).mod(Config.getFiniteFieldModulus());
/*  79 */     return new Term2(newOptVariables, newVals, newCoeff);
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
/*     */   public Term2 multiplyConstant(BigInteger constant) {
/* 106 */     BigInteger newCoeff = this.coeff.multiply(constant).mod(Config.getFiniteFieldModulus());
/*     */     
/* 108 */     return new Term2(this.optVariables, this.vals, newCoeff);
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
/* 120 */     return this.optVariables.hashCode() + this.vals.hashCode();
/*     */   }
/*     */   
/*     */   public boolean equals(Object o) {
/* 124 */     if (o == this)
/* 125 */       return true; 
/* 126 */     if (o instanceof Term2) {
/* 127 */       Term2 t2 = (Term2)o;
/* 128 */       if (t2.vals.size() != this.vals.size())
/* 129 */         return false; 
/* 130 */       int idx = -1;
/* 131 */       for (OptVariable v : this.optVariables) {
/* 132 */         idx++;
/* 133 */         if (!((Short)this.vals.get(idx)).equals(t2.getVal(v))) {
/* 134 */           return false;
/*     */         }
/*     */       } 
/* 137 */       return true;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 142 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     String result;
/* 149 */     if (this.coeff.equals(BigInteger.ONE)) {
/* 150 */       result = "";
/* 151 */       if (this.optVariables.size() == 0) {
/* 152 */         result = "1";
/*     */       }
/*     */     } else {
/*     */       
/* 156 */       result = this.coeff.toString();
/* 157 */       if (this.optVariables.size() > 0) {
/* 158 */         result = String.valueOf(result) + "*";
/*     */       }
/*     */     } 
/* 161 */     int size = this.optVariables.size();
/* 162 */     int idx = -1;
/* 163 */     for (OptVariable v : this.optVariables) {
/* 164 */       idx++;
/* 165 */       Short p = this.vals.get(idx);
/* 166 */       if (p.shortValue() == 1) {
/* 167 */         result = String.valueOf(result) + v;
/*     */       } else {
/*     */         
/* 170 */         result = String.valueOf(result) + v + "^" + p;
/*     */       } 
/* 172 */       size--;
/* 173 */       if (size > 0) {
/* 174 */         result = String.valueOf(result) + "*";
/*     */       }
/*     */     } 
/*     */     
/* 178 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigInteger getCoeff() {
/* 187 */     return this.coeff;
/*     */   }
/*     */ 
/*     */   
/*     */   public Short getVal(OptVariable v) {
/* 192 */     int idx = this.optVariables.indexOf(v);
/* 193 */     if (idx != -1) {
/* 194 */       return this.vals.get(idx);
/*     */     }
/* 196 */     return null;
/*     */   }
/*     */   
/*     */   public int getCost() {
/* 200 */     int cost = 0;
/* 201 */     boolean init = false;
/* 202 */     for (OptVariable v : this.optVariables) {
/* 203 */       Short p = getVal(v);
/* 204 */       cost += (int)Math.floor(Math.log10(p.shortValue()) / Math.log10(2.0D)) + Integer.bitCount(p.shortValue()) - 1;
/* 205 */       if (init) {
/* 206 */         cost++;
/*     */       }
/* 208 */       init = true;
/*     */     } 
/* 210 */     return cost;
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<? extends OptVariable> getOptVars() {
/* 215 */     return this.optVariables;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\poly\Term2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */