/*    */ package examples.generators;
/*    */ 
/*    */ import backend.eval.CircuitEvaluator;
/*    */ import backend.structure.CircuitGenerator;
/*    */ import backend.structure.Wire;
/*    */ import examples.gadgets.PinocchioGadget;
/*    */ import examples.gadgets.SHA256Gadget;
/*    */ import java.util.Arrays;
/*    */ import util.Util;
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
/*    */ public class AugmentedAuctionCircuitGenerator
/*    */   extends CircuitGenerator
/*    */ {
/*    */   private Wire[] secretInputValues;
/*    */   private Wire[] secretOutputValues;
/*    */   private Wire[][] secretInputRandomness;
/*    */   private Wire[][] secretOutputRandomness;
/*    */   private String pathToCompiledCircuit;
/*    */   private int numParties;
/*    */   
/*    */   public AugmentedAuctionCircuitGenerator(String circuitName, String pathToCompiledCircuit, int numParticipants) {
/* 35 */     super(circuitName);
/* 36 */     this.pathToCompiledCircuit = pathToCompiledCircuit;
/* 37 */     this.numParties = numParticipants + 1;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void outsource() {
/* 43 */     this.secretInputValues = __createProverWitnessWireArray(this.numParties - 1, new String[0]);
/* 44 */     this.secretInputRandomness = new Wire[this.numParties - 1][];
/* 45 */     this.secretOutputRandomness = new Wire[this.numParties][];
/* 46 */     for (int i = 0; i < this.numParties - 1; i++) {
/* 47 */       this.secretInputRandomness[i] = __createProverWitnessWireArray(7, new String[0]);
/* 48 */       this.secretOutputRandomness[i] = __createProverWitnessWireArray(7, new String[0]);
/*    */     } 
/* 50 */     this.secretOutputRandomness[this.numParties - 1] = __createProverWitnessWireArray(7, new String[0]);
/*    */ 
/*    */     
/* 53 */     PinocchioGadget auctionGagdet = new PinocchioGadget(Util.concat(this.__zeroWire, this.secretInputValues), this.pathToCompiledCircuit, new String[0]);
/* 54 */     Wire[] outputs = auctionGagdet.getOutputWires();
/*    */ 
/*    */     
/* 57 */     this.secretOutputValues = Arrays.<Wire>copyOfRange(outputs, 0, outputs.length - 1);
/*    */     
/*    */     int j;
/* 60 */     for (j = 0; j < this.numParties - 1; j++) {
/* 61 */       SHA256Gadget g = new SHA256Gadget(Util.concat(this.secretInputValues[j], this.secretInputRandomness[j]), 64, 64, false, false, new String[0]);
/* 62 */       __makeOutputArray(g.getOutputWires(), new String[] { "Commitment for party # " + j + "'s input balance." });
/*    */     } 
/*    */ 
/*    */     
/* 66 */     for (j = 0; j < this.numParties; j++) {
/*    */       
/* 68 */       this.secretOutputValues[j] = this.secretOutputValues[j].getBitWires(128, new String[0]).packAsBits(64, new String[0]);
/* 69 */       SHA256Gadget g = new SHA256Gadget(Util.concat(this.secretOutputValues[j], this.secretOutputRandomness[j]), 64, 64, false, false, new String[0]);
/* 70 */       __makeOutputArray(g.getOutputWires(), new String[] { "Commitment for party # " + j + "'s output balance." });
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void __generateSampleInput(CircuitEvaluator evaluator) {
/*    */     int i;
/* 77 */     for (i = 0; i < this.numParties - 1; i++) {
/* 78 */       evaluator.setWireValue(this.secretInputValues[i], Util.nextRandomBigInteger(63));
/*    */     }
/*    */     
/* 81 */     for (i = 0; i < this.numParties - 1; i++) {
/* 82 */       byte b; int j; Wire[] arrayOfWire; for (j = (arrayOfWire = this.secretInputRandomness[i]).length, b = 0; b < j; ) { Wire w = arrayOfWire[b];
/* 83 */         evaluator.setWireValue(w, Util.nextRandomBigInteger(64)); b++; }
/*    */     
/*    */     } 
/* 86 */     for (i = 0; i < this.numParties; i++) {
/* 87 */       byte b; int j; Wire[] arrayOfWire; for (j = (arrayOfWire = this.secretOutputRandomness[i]).length, b = 0; b < j; ) { Wire w = arrayOfWire[b];
/* 88 */         evaluator.setWireValue(w, Util.nextRandomBigInteger(64));
/*    */         b++; }
/*    */     
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void main(String[] args) throws Exception {
/* 95 */     AugmentedAuctionCircuitGenerator generator = new AugmentedAuctionCircuitGenerator("augmented_auction_10", "auction_10.arith", 10);
/* 96 */     generator.__generateCircuit();
/* 97 */     generator.__evalCircuit();
/* 98 */     generator.__prepFiles();
/*    */   }
/*    */ }


/* Location:              D:\xjsnark_backend.jar!\examples\generators\AugmentedAuctionCircuitGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */