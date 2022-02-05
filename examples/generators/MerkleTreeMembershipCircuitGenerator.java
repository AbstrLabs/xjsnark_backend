/*    */ package examples.generators;
/*    */ 
/*    */ import backend.config.Config;
/*    */ import backend.eval.CircuitEvaluator;
/*    */ import backend.structure.CircuitGenerator;
/*    */ import backend.structure.Wire;
/*    */ import examples.gadgets.MerkleTreePathGadget;
/*    */ import util.Util;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MerkleTreeMembershipCircuitGenerator
/*    */   extends CircuitGenerator
/*    */ {
/*    */   private Wire[] publicRootWires;
/*    */   private Wire[] intermediateHasheWires;
/*    */   private Wire directionSelector;
/*    */   private Wire[] leafWires;
/* 20 */   private int leafNumOfWords = 10;
/* 21 */   private int leafWordBitWidth = 32;
/*    */   private int treeHeight;
/* 23 */   private int hashDigestDimension = 3;
/*    */   
/*    */   private MerkleTreePathGadget merkleTreeGadget;
/*    */   
/*    */   public MerkleTreeMembershipCircuitGenerator(String circuitName, int treeHeight) {
/* 28 */     super(circuitName);
/* 29 */     this.treeHeight = treeHeight;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void outsource() {
/* 37 */     this.publicRootWires = __createInputWireArray(this.hashDigestDimension, new String[] { "Input Merkle Tree Root" });
/* 38 */     this.intermediateHasheWires = __createProverWitnessWireArray(this.hashDigestDimension * this.treeHeight, new String[] { "Intermediate Hashes" });
/* 39 */     this.directionSelector = __createProverWitnessWire(new String[] { "Direction selector" });
/* 40 */     this.leafWires = __createProverWitnessWireArray(this.leafNumOfWords, new String[] { "Secret Leaf" });
/*    */ 
/*    */ 
/*    */     
/* 44 */     this.merkleTreeGadget = new MerkleTreePathGadget(
/* 45 */         this.directionSelector, this.leafWires, this.intermediateHasheWires, this.leafWordBitWidth, this.treeHeight, new String[0]);
/* 46 */     Wire[] actualRoot = this.merkleTreeGadget.getOutputWires();
/*    */ 
/*    */     
/* 49 */     Wire errorAccumulator = __getZeroWire();
/* 50 */     for (int i = 0; i < this.hashDigestDimension; i++) {
/* 51 */       Wire diff = actualRoot[i].sub(this.publicRootWires[i], new String[0]);
/* 52 */       Wire check = diff.checkNonZero(new String[0]);
/* 53 */       errorAccumulator = errorAccumulator.add(check, new String[0]);
/*    */     } 
/*    */     
/* 56 */     __makeOutputArray(actualRoot, new String[] { "Computed Root" });
/*    */ 
/*    */     
/* 59 */     __makeOutput(errorAccumulator.checkNonZero(new String[0]), new String[] { "Error if NON-zero" });
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void __generateSampleInput(CircuitEvaluator circuitEvaluator) {
/*    */     int i;
/* 66 */     for (i = 0; i < this.hashDigestDimension; i++) {
/* 67 */       circuitEvaluator.setWireValue(this.publicRootWires[i], Util.nextRandomBigInteger(Config.getFiniteFieldModulus()));
/*    */     }
/*    */     
/* 70 */     circuitEvaluator.setWireValue(this.directionSelector, Util.nextRandomBigInteger(this.treeHeight));
/* 71 */     for (i = 0; i < this.hashDigestDimension * this.treeHeight; i++) {
/* 72 */       circuitEvaluator.setWireValue(this.intermediateHasheWires[i], Util.nextRandomBigInteger(Config.getFiniteFieldModulus()));
/*    */     }
/*    */     
/* 75 */     for (i = 0; i < this.leafNumOfWords; i++) {
/* 76 */       circuitEvaluator.setWireValue(this.leafWires[i], 2147483647L);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 84 */     MerkleTreeMembershipCircuitGenerator generator = new MerkleTreeMembershipCircuitGenerator("tree_64", 64);
/* 85 */     generator.__generateCircuit();
/* 86 */     generator.__evalCircuit();
/* 87 */     generator.__prepFiles();
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\generators\MerkleTreeMembershipCircuitGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */