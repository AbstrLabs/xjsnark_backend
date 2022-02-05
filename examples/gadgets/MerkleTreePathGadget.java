/*    */ package examples.gadgets;
/*    */ 
/*    */ import backend.config.Config;
/*    */ import backend.operations.Gadget;
/*    */ import backend.structure.Wire;
/*    */ import backend.structure.WireArray;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MerkleTreePathGadget
/*    */   extends Gadget
/*    */ {
/* 13 */   private static int digestWidth = 3;
/*    */   
/*    */   private int treeHeight;
/*    */   
/*    */   private Wire directionSelectorWire;
/*    */   
/*    */   private Wire[] directionSelectorBits;
/*    */   
/*    */   private Wire[] leafWires;
/*    */   private Wire[] intermediateHashWires;
/*    */   private Wire[] outRoot;
/*    */   private int leafWordBitWidth;
/*    */   
/*    */   public MerkleTreePathGadget(Wire directionSelectorWire, Wire[] leafWires, Wire[] intermediateHasheWires, int leafWordBitWidth, int treeHeight, String... desc) {
/* 27 */     super(desc);
/* 28 */     this.directionSelectorWire = directionSelectorWire;
/* 29 */     this.treeHeight = treeHeight;
/* 30 */     this.leafWires = leafWires;
/* 31 */     this.intermediateHashWires = intermediateHasheWires;
/* 32 */     this.leafWordBitWidth = leafWordBitWidth;
/*    */     
/* 34 */     buildCircuit();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private void buildCircuit() {
/* 40 */     this.directionSelectorBits = this.directionSelectorWire.getBitWires(this.treeHeight, new String[0]).asArray();
/*    */ 
/*    */     
/* 43 */     Wire[] leafBits = (new WireArray(this.leafWires)).getBits(this.leafWordBitWidth, new String[0]).asArray();
/* 44 */     SubsetSumHashGadget subsetSumGadget = new SubsetSumHashGadget(leafBits, false, new String[0]);
/* 45 */     Wire[] currentHash = subsetSumGadget.getOutputWires();
/*    */ 
/*    */     
/* 48 */     for (int i = 0; i < this.treeHeight; i++) {
/* 49 */       Wire[] inHash = new Wire[2 * digestWidth]; int j;
/* 50 */       for (j = 0; j < digestWidth; j++) {
/* 51 */         Wire temp = currentHash[j].sub(this.intermediateHashWires[i * digestWidth + j], new String[0]);
/* 52 */         Wire temp2 = this.directionSelectorBits[i].mul(temp, new String[0]);
/* 53 */         inHash[j] = this.intermediateHashWires[i * digestWidth + j].add(temp2, new String[0]);
/*    */       } 
/* 55 */       for (j = digestWidth; j < 2 * digestWidth; j++) {
/* 56 */         Wire temp = currentHash[j - digestWidth].add(this.intermediateHashWires[i * digestWidth + j - digestWidth], new String[0]);
/* 57 */         inHash[j] = temp.sub(inHash[j - digestWidth], new String[0]);
/*    */       } 
/*    */       
/* 60 */       Wire[] nextInputBits = (new WireArray(inHash)).getBits(Config.getNumBitsFiniteFieldModulus(), new String[0]).asArray();
/* 61 */       subsetSumGadget = new SubsetSumHashGadget(nextInputBits, false, new String[0]);
/* 62 */       currentHash = subsetSumGadget.getOutputWires();
/*    */     } 
/* 64 */     this.outRoot = currentHash;
/*    */   }
/*    */ 
/*    */   
/*    */   public Wire[] getOutputWires() {
/* 69 */     return this.outRoot;
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\MerkleTreePathGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */