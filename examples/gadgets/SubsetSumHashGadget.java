/*    */ package examples.gadgets;
/*    */ 
/*    */ import backend.config.Config;
/*    */ import backend.operations.Gadget;
/*    */ import backend.structure.Wire;
/*    */ import java.math.BigInteger;
/*    */ import java.util.Arrays;
/*    */ import util.Util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SubsetSumHashGadget
/*    */   extends Gadget
/*    */ {
/*    */   public static final int DIMENSION = 3;
/* 17 */   public static final int INPUT_LENGTH = 6 * Config.getNumBitsFiniteFieldModulus();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 25 */   private static final BigInteger[][] COEFFS = new BigInteger[3][INPUT_LENGTH]; static {
/* 26 */     for (int i = 0; i < 3; i++) {
/* 27 */       for (int k = 0; k < INPUT_LENGTH; k++) {
/* 28 */         COEFFS[i][k] = Util.nextRandomBigInteger(Config.getFiniteFieldModulus());
/*    */       }
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private Wire[] inputWires;
/*    */   
/*    */   private Wire[] outWires;
/*    */   
/*    */   private boolean binaryOutput;
/*    */ 
/*    */   
/*    */   public SubsetSumHashGadget(Wire[] ins, boolean binaryOutput, String... desc) {
/* 43 */     super(desc);
/* 44 */     int numBlocks = (int)Math.ceil(ins.length * 1.0D / INPUT_LENGTH);
/*    */     
/* 46 */     if (numBlocks > 1) {
/* 47 */       throw new IllegalArgumentException("Only one block is supported at this point");
/*    */     }
/*    */     
/* 50 */     int rem = numBlocks * INPUT_LENGTH - ins.length;
/*    */     
/* 52 */     Wire[] pad = new Wire[rem];
/* 53 */     for (int i = 0; i < pad.length; i++) {
/* 54 */       pad[i] = this.generator.__getZeroWire();
/*    */     }
/* 56 */     this.inputWires = Util.concat(ins, pad);
/* 57 */     this.binaryOutput = binaryOutput;
/* 58 */     buildCircuit();
/*    */   }
/*    */ 
/*    */   
/*    */   private void buildCircuit() {
/* 63 */     Wire[] outDigest = new Wire[3];
/* 64 */     Arrays.fill((Object[])outDigest, this.generator.__getZeroWire());
/*    */     int i;
/* 66 */     for (i = 0; i < 3; i++) {
/* 67 */       for (int j = 0; j < INPUT_LENGTH; j++) {
/* 68 */         Wire t = this.inputWires[j].mul(COEFFS[i][j], new String[0]);
/* 69 */         System.out.println(t);
/* 70 */         outDigest[i] = outDigest[i].add(t, new String[0]);
/*    */       } 
/*    */     } 
/* 73 */     if (!this.binaryOutput) {
/* 74 */       this.outWires = outDigest;
/*    */     } else {
/* 76 */       this.outWires = new Wire[3 * Config.getNumBitsFiniteFieldModulus()];
/* 77 */       for (i = 0; i < 3; i++) {
/* 78 */         Wire[] bits = outDigest[i].getBitWires(Config.getNumBitsFiniteFieldModulus(), new String[0]).asArray();
/* 79 */         for (int j = 0; j < bits.length; j++) {
/* 80 */           this.outWires[j + i * Config.getNumBitsFiniteFieldModulus()] = bits[j];
/*    */         }
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Wire[] getOutputWires() {
/* 88 */     return this.outWires;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\SubsetSumHashGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */