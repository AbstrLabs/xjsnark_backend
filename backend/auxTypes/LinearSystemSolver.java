/*    */ package backend.auxTypes;
/*    */ 
/*    */ import backend.config.Config;
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LinearSystemSolver
/*    */ {
/* 15 */   public static BigInteger prime = Config.getFiniteFieldModulus();
/*    */   private BigInteger[][] mat;
/*    */   private int numRows;
/*    */   private int numCols;
/*    */   
/*    */   public LinearSystemSolver(BigInteger[][] mat) {
/* 21 */     this.mat = mat;
/* 22 */     this.numRows = mat.length;
/* 23 */     this.numCols = (mat[0]).length;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void solveInPlace() {
/* 30 */     guassJordan();
/* 31 */     rref();
/*    */   }
/*    */   
/*    */   private void guassJordan() {
/* 35 */     for (int colIdx = 0, rowIdx = 0; colIdx < this.numCols; colIdx++, rowIdx++) {
/* 36 */       int pivotRowIdx = rowIdx;
/* 37 */       while (pivotRowIdx < this.numRows && 
/* 38 */         this.mat[pivotRowIdx][colIdx].equals(BigInteger.ZERO)) {
/* 39 */         pivotRowIdx++;
/*    */       }
/* 41 */       if (pivotRowIdx != this.numRows) {
/*    */ 
/*    */ 
/*    */         
/* 45 */         BigInteger[] tmp = this.mat[pivotRowIdx];
/* 46 */         this.mat[pivotRowIdx] = this.mat[rowIdx];
/* 47 */         this.mat[rowIdx] = tmp;
/*    */         
/* 49 */         pivotRowIdx = rowIdx;
/*    */ 
/*    */         
/* 52 */         BigInteger invF = inverse(this.mat[pivotRowIdx][colIdx]);
/* 53 */         for (int j = 0; j < this.numCols; j++) {
/* 54 */           this.mat[pivotRowIdx][j] = this.mat[pivotRowIdx][j].multiply(invF).mod(
/* 55 */               prime);
/*    */         }
/*    */         
/* 58 */         for (int k = pivotRowIdx + 1; k < this.numRows; k++) {
/* 59 */           BigInteger f = negate(this.mat[k][colIdx]);
/* 60 */           for (int i = 0; i < this.numCols; i++) {
/* 61 */             this.mat[k][i] = this.mat[k][i].add(this.mat[pivotRowIdx][i].multiply(f));
/* 62 */             this.mat[k][i] = this.mat[k][i].mod(prime);
/*    */           } 
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   private void rref() {
/* 70 */     for (int rowIdx = this.numRows - 1; rowIdx >= 0; rowIdx--) {
/* 71 */       int pivotColIdx = 0;
/* 72 */       while (pivotColIdx < this.numCols && 
/* 73 */         this.mat[rowIdx][pivotColIdx].equals(BigInteger.ZERO)) {
/* 74 */         pivotColIdx++;
/*    */       }
/* 76 */       if (pivotColIdx != this.numCols)
/*    */       {
/*    */         
/* 79 */         for (int k = rowIdx - 1; k >= 0; k--) {
/* 80 */           BigInteger f = this.mat[k][pivotColIdx];
/* 81 */           for (int j = 0; j < this.numCols; j++) {
/* 82 */             this.mat[k][j] = this.mat[k][j]
/* 83 */               .add(negate(this.mat[rowIdx][j].multiply(f)));
/* 84 */             this.mat[k][j] = this.mat[k][j].mod(prime);
/*    */           } 
/*    */         }  } 
/*    */     } 
/*    */   }
/*    */   
/*    */   private static BigInteger negate(BigInteger x) {
/* 91 */     return prime.subtract(x.mod(prime)).mod(prime);
/*    */   }
/*    */   
/*    */   private static BigInteger inverse(BigInteger x) {
/* 95 */     return x.mod(prime).modInverse(prime);
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\auxTypes\LinearSystemSolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */