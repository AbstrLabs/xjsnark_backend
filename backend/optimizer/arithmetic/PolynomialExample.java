/*     */ package backend.optimizer.arithmetic;
/*     */ 
/*     */ import backend.optimizer.arithmetic.poly.MultivariatePolynomial;
/*     */ import backend.optimizer.arithmetic.poly.OptVariable;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PolynomialExample
/*     */ {
/*     */   public static void main(String[] args) {}
/*     */   
/*     */   private static void testcase5() {
/*  18 */     int dim = 10;
/*  19 */     int numOfVars = dim * dim;
/*  20 */     String[] vars = new String[numOfVars];
/*  21 */     for (int i = 0; i < numOfVars; i++) {
/*  22 */       vars[i] = "x" + i;
/*     */     }
/*     */     
/*  25 */     ArrayList<MultivariatePolynomial> list = new ArrayList<>();
/*     */ 
/*     */     
/*  28 */     OptVariable[] varList = new OptVariable[numOfVars];
/*  29 */     for (int j = 0; j < numOfVars; j++) {
/*  30 */       varList[j] = new OptVariable("x", j);
/*     */     }
/*     */     
/*  33 */     MultivariatePolynomial[] mvp1 = new MultivariatePolynomial[numOfVars];
/*  34 */     MultivariatePolynomial[] mvp2 = new MultivariatePolynomial[numOfVars];
/*     */     int k;
/*  36 */     for (k = 0; k < numOfVars; k++) {
/*  37 */       mvp1[k] = new MultivariatePolynomial(varList[k]);
/*  38 */       mvp2[k] = new MultivariatePolynomial(varList[k]);
/*     */     } 
/*     */     
/*  41 */     for (k = 0; k < dim; k++) {
/*  42 */       for (int m = 0; m < dim; m++) {
/*  43 */         MultivariatePolynomial mvp = new MultivariatePolynomial();
/*  44 */         for (int n = 0; n < dim; n++) {
/*  45 */           mvp = mvp.add(mvp1[k * dim + n].multiply(mvp2[n * dim + m]));
/*     */         }
/*  47 */         list.add(mvp);
/*  48 */         System.out.println(mvp);
/*     */       } 
/*     */     } 
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
/*     */   private static void testcase4() {
/*  62 */     int dim = 5;
/*  63 */     int numOfVars = dim;
/*     */     
/*  65 */     String[] vars = new String[numOfVars];
/*  66 */     for (int i = 0; i < numOfVars; i++) {
/*  67 */       vars[i] = "x" + i;
/*     */     }
/*     */     
/*  70 */     ArrayList<MultivariatePolynomial> list = new ArrayList<>();
/*     */ 
/*     */     
/*  73 */     OptVariable[] varList = new OptVariable[numOfVars];
/*  74 */     for (int j = 0; j < numOfVars; j++) {
/*  75 */       varList[j] = new OptVariable("x", j);
/*     */     }
/*     */     
/*  78 */     MultivariatePolynomial[] mvp1 = new MultivariatePolynomial[numOfVars];
/*  79 */     MultivariatePolynomial[] mvp2 = new MultivariatePolynomial[numOfVars];
/*     */     
/*  81 */     for (int k = 0; k < numOfVars; k++) {
/*  82 */       mvp1[k] = new MultivariatePolynomial(varList[k]);
/*  83 */       mvp2[k] = new MultivariatePolynomial(varList[k]);
/*     */     } 
/*     */ 
/*     */     
/*  87 */     MultivariatePolynomial[] out = new MultivariatePolynomial[2 * (dim - 1) + 1];
/*     */     int m;
/*  89 */     for (m = 0; m < 2 * (dim - 1) + 1; m++) {
/*  90 */       out[m] = new MultivariatePolynomial();
/*     */     }
/*     */     
/*  93 */     for (m = 0; m < dim; m++) {
/*  94 */       for (int n = 0; n < dim; n++) {
/*  95 */         out[m + n] = out[m + n].addInPlace(mvp1[m].multiply(mvp2[n]));
/*     */       }
/*     */     } 
/*  98 */     for (m = 0; m < 2 * (dim - 1) + 1; m++) {
/*  99 */       list.add(out[m]);
/* 100 */       System.out.println(out[m]);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\arithmetic\PolynomialExample.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */