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
/*     */ public class MultivariatePolynomial
/*     */ {
/*     */   ArrayList<Term> terms;
/*     */   
/*     */   public MultivariatePolynomial() {
/*  19 */     this.terms = new ArrayList<>(1);
/*     */   }
/*     */   
/*     */   public MultivariatePolynomial(Term t) {
/*  23 */     this();
/*  24 */     if (!t.coeff.equals(BigInteger.ZERO))
/*     */     {
/*  26 */       this.terms.add(t);
/*     */     }
/*     */   }
/*     */   
/*     */   public MultivariatePolynomial(OptVariable v) {
/*  31 */     this();
/*  32 */     Term t = new Term(v);
/*     */     
/*  34 */     this.terms.add(t);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultivariatePolynomial(ArrayList<Term> terms) {
/*  44 */     terms.trimToSize();
/*  45 */     this.terms = terms;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultivariatePolynomial multiply(MultivariatePolynomial p) {
/*  53 */     ArrayList<Term> newTermList = new ArrayList<>(this.terms.size() + p.terms.size());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  58 */     for (Term t : this.terms) {
/*  59 */       for (Term pt : p.terms) {
/*  60 */         Term newTerm = t.multiply(pt);
/*     */ 
/*     */         
/*  63 */         int idx = newTermList.indexOf(newTerm);
/*  64 */         if (idx == -1) {
/*  65 */           if (!newTerm.coeff.equals(BigInteger.ZERO))
/*  66 */             newTermList.add(newTerm);  continue;
/*     */         } 
/*  68 */         Term exisitingTerm = newTermList.get(idx);
/*  69 */         exisitingTerm.coeff = exisitingTerm.coeff
/*  70 */           .add(newTerm.coeff).mod(Config.getFiniteFieldModulus());
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  79 */     return new MultivariatePolynomial(newTermList);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultivariatePolynomial multiplyInPlace(MultivariatePolynomial p) {
/*  86 */     ArrayList<Term> newTermList = new ArrayList<>(this.terms.size() + p.terms.size());
/*     */     
/*  88 */     for (Term t : this.terms) {
/*  89 */       for (Term pt : p.terms) {
/*  90 */         Term newTerm = t.multiply(pt);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 100 */         int idx = newTermList.indexOf(newTerm);
/* 101 */         if (idx == -1) {
/* 102 */           if (!newTerm.coeff.equals(BigInteger.ZERO))
/* 103 */             newTermList.add(newTerm);  continue;
/*     */         } 
/* 105 */         Term exisitingTerm = newTermList.get(idx);
/* 106 */         exisitingTerm.coeff = exisitingTerm.coeff
/* 107 */           .add(newTerm.coeff).mod(Config.getFiniteFieldModulus());
/*     */       } 
/*     */     } 
/*     */     
/* 111 */     this.terms = newTermList;
/* 112 */     this.terms.trimToSize();
/*     */ 
/*     */ 
/*     */     
/* 116 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public MultivariatePolynomial multiplyConstant(BigInteger c) {
/* 123 */     if (c.equals(BigInteger.ZERO)) {
/* 124 */       return new MultivariatePolynomial();
/*     */     }
/*     */     
/* 127 */     ArrayList<Term> newTermList = new ArrayList<>(this.terms.size());
/*     */     
/* 129 */     for (Term t : this.terms) {
/* 130 */       Term newTerm = t.multiplyConstant(c);
/* 131 */       if (!newTerm.coeff.equals(BigInteger.ZERO))
/*     */       {
/* 133 */         newTermList.add(newTerm); } 
/*     */     } 
/* 135 */     return new MultivariatePolynomial(newTermList);
/*     */   }
/*     */ 
/*     */   
/*     */   public MultivariatePolynomial multiplyConstantInPlace(BigInteger c) {
/* 140 */     if (c.equals(BigInteger.ZERO)) {
/* 141 */       this.terms.clear();
/* 142 */       return this;
/*     */     } 
/*     */     
/* 145 */     ArrayList<Term> newTermList = new ArrayList<>(this.terms.size());
/*     */     
/* 147 */     for (Term t : this.terms) {
/* 148 */       Term newTerm = t.multiplyConstant(c);
/* 149 */       if (!newTerm.coeff.equals(BigInteger.ZERO))
/*     */       {
/* 151 */         newTermList.add(newTerm);
/*     */       }
/*     */     } 
/* 154 */     this.terms = newTermList;
/* 155 */     this.terms.trimToSize();
/* 156 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public MultivariatePolynomial add(MultivariatePolynomial p) {
/* 162 */     ArrayList<Term> newTermList = new ArrayList<>(this.terms.size());
/*     */     
/* 164 */     for (Term t : this.terms) {
/* 165 */       newTermList.add(t);
/*     */     }
/* 167 */     for (Term pt : p.terms) {
/* 168 */       int idx = newTermList.indexOf(pt);
/* 169 */       if (idx == -1) {
/* 170 */         newTermList.add(pt); continue;
/*     */       } 
/* 172 */       Term exisitingTerm = newTermList.get(idx);
/* 173 */       newTermList.remove(exisitingTerm);
/* 174 */       Term newTerm = exisitingTerm.addToConstant(pt.coeff);
/*     */ 
/*     */ 
/*     */       
/* 178 */       if (!newTerm.getCoeff().equals(BigInteger.ZERO)) {
/* 179 */         newTermList.add(newTerm);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 185 */     return new MultivariatePolynomial(newTermList);
/*     */   }
/*     */ 
/*     */   
/*     */   public MultivariatePolynomial addInPlace(MultivariatePolynomial p) {
/* 190 */     ArrayList<Term> newTermList = new ArrayList<>(this.terms.size());
/*     */     
/* 192 */     for (Term t : this.terms) {
/* 193 */       newTermList.add(t);
/*     */     }
/* 195 */     for (Term pt : p.terms) {
/* 196 */       int idx = newTermList.indexOf(pt);
/* 197 */       if (idx == -1) {
/* 198 */         newTermList.add(pt); continue;
/*     */       } 
/* 200 */       Term exisitingTerm = newTermList.get(idx);
/* 201 */       newTermList.remove(exisitingTerm);
/* 202 */       Term newTerm = exisitingTerm.addToConstant(pt.coeff);
/*     */ 
/*     */ 
/*     */       
/* 206 */       if (!newTerm.getCoeff().equals(BigInteger.ZERO)) {
/* 207 */         newTermList.add(newTerm);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 213 */     this.terms = newTermList;
/* 214 */     this.terms.trimToSize();
/* 215 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 248 */     String result = "";
/* 249 */     int size = this.terms.size();
/* 250 */     for (Term t : this.terms) {
/* 251 */       result = String.valueOf(result) + t;
/* 252 */       size--;
/* 253 */       if (size > 0) {
/* 254 */         result = String.valueOf(result) + "+";
/*     */       }
/*     */     } 
/* 257 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 262 */     MultivariatePolynomial p1 = new MultivariatePolynomial(new OptVariable(
/* 263 */           "v", 1));
/* 264 */     MultivariatePolynomial p2 = new MultivariatePolynomial(new OptVariable(
/* 265 */           "v", 2));
/* 266 */     MultivariatePolynomial p3 = new MultivariatePolynomial(new OptVariable(
/* 267 */           "v", 3));
/*     */     
/* 269 */     MultivariatePolynomial p4 = p1.multiply(p1).multiply(p2);
/* 270 */     MultivariatePolynomial p5 = p2.multiply(p1).multiply(p1);
/* 271 */     System.out.println(p4.add(p5));
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
/*     */   public Collection<Term> getTerms() {
/* 296 */     return this.terms;
/*     */   }
/*     */   
/*     */   public int getCost() {
/* 300 */     int cost = 0;
/* 301 */     for (Term t : this.terms) {
/* 302 */       cost += t.getCost();
/*     */     }
/* 304 */     return cost;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCostly() {
/* 310 */     if (this.terms.size() >= 5)
/* 311 */       return true; 
/* 312 */     for (Term t : this.terms) {
/* 313 */       if (t.getVarCount() >= 4 || t.getMaxExponent() > 16383) {
/* 314 */         return true;
/*     */       }
/*     */     } 
/* 317 */     return false;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\poly\MultivariatePolynomial.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */