/*    */ package backend.config;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Config
/*    */ {
/* 29 */   private static BigInteger finiteFieldModulus = new BigInteger(
/* 30 */       "21888242871839275222246405745257275088548364400416034343698204186575808495617");
/* 31 */   private static int numBitsFiniteFieldModulus = finiteFieldModulus.toString(2).length();
/*    */ 
/*    */   
/*    */   public static final boolean runningMultiGenerators = false;
/*    */ 
/*    */   
/*    */   public static boolean hexOutputEnabled = true;
/*    */ 
/*    */   
/*    */   public static boolean inputVerbose = true;
/*    */   
/*    */   public static boolean outputVerbose = true;
/*    */   
/*    */   public static boolean debugVerbose = false;
/*    */   
/*    */   public static boolean writeCircuits = false;
/*    */   
/* 48 */   public static String outputFilesPath = "";
/*    */   public static boolean multivariateExpressionMinimization = false;
/* 50 */   public static int arithOptimizerNumThreads = 4;
/* 51 */   public static int arithOptimizerTimeoutPerProblemMilliSec = 15000;
/*    */   public static boolean arithOptimizerIncrementalMode = false;
/*    */   public static boolean enforceInternalDivisionNonZeroChecks = true;
/*    */   public static boolean arithOptimizerDisableProgress = true;
/*    */   private static boolean gateSharingOptimization = false;
/*    */   
/*    */   public static void setFiniteFieldModulus(BigInteger p) {
/* 58 */     finiteFieldModulus = p;
/* 59 */     numBitsFiniteFieldModulus = finiteFieldModulus.toString(2).length();
/*    */   }
/*    */   
/*    */   public static BigInteger getFiniteFieldModulus() {
/* 63 */     return finiteFieldModulus;
/*    */   }
/*    */   
/*    */   public static int getNumBitsFiniteFieldModulus() {
/* 67 */     return numBitsFiniteFieldModulus;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\backend\config\Config.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */