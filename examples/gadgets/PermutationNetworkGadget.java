/*     */ package examples.gadgets;
/*     */ 
/*     */ import backend.eval.CircuitEvaluator;
/*     */ import backend.eval.Instruction;
/*     */ import backend.operations.Gadget;
/*     */ import backend.structure.CircuitGenerator;
/*     */ import backend.structure.Wire;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Stack;
/*     */ 
/*     */ public class PermutationNetworkGadget
/*     */   extends Gadget
/*     */ {
/*     */   private String permutationNetworkId;
/*  17 */   private static int switchIndex = 0;
/*     */   
/*     */   private int dim;
/*     */   
/*     */   private Wire[][] inputs;
/*     */   
/*     */   private Wire[][] outputs;
/*     */   private HashMap<Integer, PermutationNetworkAux.DummySwitch> switchMap;
/*     */   private SwitchingNetwork network;
/*     */   
/*     */   public class SwitchingNetwork
/*     */   {
/*     */     int n;
/*     */     int dim;
/*     */     PermutationNetworkGadget.Switch[] inSwitches;
/*     */     PermutationNetworkGadget.Switch[] outSwitches;
/*     */     SwitchingNetwork top;
/*     */     SwitchingNetwork bottom;
/*     */     Wire[][] ins;
/*     */     Wire[][] outs;
/*  37 */     int numSwitches = 0;
/*     */     
/*     */     public SwitchingNetwork(Wire[][] ins, Wire[][] outs) {
/*  40 */       this.ins = ins;
/*  41 */       this.outs = outs;
/*  42 */       this.dim = (ins[0]).length;
/*  43 */       this.n = ins.length;
/*  44 */       build();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void build() {
/*  51 */       if (this.n == 2) {
/*     */ 
/*     */         
/*  54 */         this.inSwitches = this.outSwitches = new PermutationNetworkGadget.Switch[1];
/*  55 */         this.inSwitches[0] = new PermutationNetworkGadget.Switch(this.ins[0], this.ins[1], this.outs[0], this.outs[1]);
/*  56 */         this.numSwitches = 1;
/*  57 */       } else if (this.n == 3) {
/*     */ 
/*     */         
/*  60 */         this.inSwitches = new PermutationNetworkGadget.Switch[1];
/*  61 */         this.outSwitches = new PermutationNetworkGadget.Switch[1];
/*  62 */         this.inSwitches[0] = new PermutationNetworkGadget.Switch(this.ins[0], this.ins[1], true);
/*  63 */         this.outSwitches[0] = new PermutationNetworkGadget.Switch(this.outs[0], this.outs[1], (this.inSwitches[0]).out1);
/*     */ 
/*     */         
/*  66 */         this.top = null;
/*  67 */         this.bottom = new SwitchingNetwork(new Wire[][] {
/*  68 */               (this.inSwitches[0]).out2, this.ins[2] }, new Wire[][] {
/*  69 */               (this.outSwitches[0]).in2, this.outs[2]
/*     */             });
/*  71 */         this.numSwitches += 2 + this.bottom.numSwitches;
/*     */       }
/*  73 */       else if (this.n % 2 == 1) {
/*     */ 
/*     */ 
/*     */         
/*  77 */         this.inSwitches = new PermutationNetworkGadget.Switch[this.n / 2];
/*  78 */         this.outSwitches = new PermutationNetworkGadget.Switch[this.n / 2];
/*     */         
/*  80 */         Wire[][] topInput = new Wire[this.n / 2][];
/*  81 */         Wire[][] bottomInput = new Wire[this.n / 2 + this.n % 2][];
/*  82 */         Wire[][] topOutput = new Wire[this.n / 2][];
/*  83 */         Wire[][] bottomOutput = new Wire[this.n / 2 + this.n % 2][];
/*     */         int i;
/*  85 */         for (i = 0; i < this.inSwitches.length; i++) {
/*  86 */           this.inSwitches[i] = new PermutationNetworkGadget.Switch(this.ins[2 * i], this.ins[2 * i + 1], true);
/*  87 */           topInput[i] = (this.inSwitches[i]).out1;
/*  88 */           bottomInput[i] = (this.inSwitches[i]).out2;
/*     */         } 
/*     */         
/*  91 */         for (i = 0; i < this.outSwitches.length; i++) {
/*  92 */           this.outSwitches[i] = new PermutationNetworkGadget.Switch(this.outs[2 * i], this.outs[2 * i + 1], 
/*  93 */               false);
/*  94 */           topOutput[i] = (this.outSwitches[i]).in1;
/*  95 */           bottomOutput[i] = (this.outSwitches[i]).in2;
/*     */         } 
/*     */         
/*  98 */         bottomInput[bottomInput.length - 1] = this.ins[this.ins.length - 1];
/*  99 */         bottomOutput[bottomOutput.length - 1] = this.outs[this.outs.length - 1];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 105 */         this.top = new SwitchingNetwork(topInput, topOutput);
/* 106 */         this.bottom = new SwitchingNetwork(bottomInput, bottomOutput);
/* 107 */         this.numSwitches = this.top.numSwitches + this.bottom.numSwitches + 
/* 108 */           this.outSwitches.length + this.inSwitches.length;
/* 109 */       } else if (this.n % 2 == 0) {
/* 110 */         this.inSwitches = new PermutationNetworkGadget.Switch[this.n / 2];
/* 111 */         this.outSwitches = new PermutationNetworkGadget.Switch[this.n / 2 - 1];
/*     */         
/* 113 */         Wire[][] topInput = new Wire[this.n / 2][];
/* 114 */         Wire[][] bottomInput = new Wire[this.n / 2 + this.n % 2][];
/* 115 */         Wire[][] topOutput = new Wire[this.n / 2][];
/* 116 */         Wire[][] bottomOutput = new Wire[this.n / 2 + this.n % 2][];
/*     */         int i;
/* 118 */         for (i = 0; i < this.inSwitches.length; i++) {
/* 119 */           this.inSwitches[i] = new PermutationNetworkGadget.Switch(this.ins[2 * i], this.ins[2 * i + 1], true);
/* 120 */           topInput[i] = (this.inSwitches[i]).out1;
/* 121 */           bottomInput[i] = (this.inSwitches[i]).out2;
/*     */         } 
/*     */         
/* 124 */         for (i = 0; i < this.outSwitches.length; i++) {
/* 125 */           this.outSwitches[i] = new PermutationNetworkGadget.Switch(this.outs[2 * i], this.outs[2 * i + 1], 
/* 126 */               false);
/* 127 */           topOutput[i] = (this.outSwitches[i]).in1;
/* 128 */           bottomOutput[i] = (this.outSwitches[i]).in2;
/*     */         } 
/*     */         
/* 131 */         topOutput[topOutput.length - 1] = this.outs[this.outs.length - 2];
/* 132 */         bottomOutput[bottomOutput.length - 1] = this.outs[this.outs.length - 1];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 140 */         this.top = new SwitchingNetwork(topInput, topOutput);
/* 141 */         this.bottom = new SwitchingNetwork(bottomInput, bottomOutput);
/*     */ 
/*     */         
/* 144 */         this.numSwitches = this.top.numSwitches + this.bottom.numSwitches + 
/* 145 */           this.outSwitches.length + this.inSwitches.length;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     class Edge
/*     */     {
/*     */       boolean visited;
/*     */       
/*     */       boolean color;
/*     */       PermutationNetworkGadget.SwitchingNetwork.Node src;
/*     */       PermutationNetworkGadget.SwitchingNetwork.Node dst;
/*     */       int srcIdx;
/*     */       int dstIdx;
/*     */       
/*     */       public Edge(PermutationNetworkGadget.SwitchingNetwork.Node src, PermutationNetworkGadget.SwitchingNetwork.Node dst, int srcIdx, int dstIdx) {
/* 161 */         this.src = src;
/* 162 */         this.dst = dst;
/* 163 */         this.srcIdx = srcIdx;
/* 164 */         this.dstIdx = dstIdx;
/* 165 */         src.edges.add(this);
/* 166 */         dst.edges.add(this);
/*     */       }
/*     */     }
/*     */     
/*     */     class Node
/*     */     {
/* 172 */       ArrayList<PermutationNetworkGadget.SwitchingNetwork.Edge> edges = new ArrayList<>();
/*     */       boolean isSourceNode;
/*     */       boolean visited = false;
/*     */       boolean switchSetting = false;
/*     */       
/*     */       public Node(boolean isSourceNode) {
/* 178 */         this.isSourceNode = isSourceNode;
/*     */       }
/*     */       
/*     */       public PermutationNetworkGadget.SwitchingNetwork.Edge getFirstEdge() {
/* 182 */         if (this.edges.size() == 1) {
/* 183 */           return this.edges.get(0);
/*     */         }
/* 185 */         if (this.isSourceNode) {
/* 186 */           return (((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).srcIdx < ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).srcIdx) ? this.edges
/* 187 */             .get(0) : this.edges.get(1);
/*     */         }
/* 189 */         return (((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).dstIdx < ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).dstIdx) ? this.edges
/* 190 */           .get(0) : this.edges.get(1);
/*     */       }
/*     */ 
/*     */       
/*     */       public PermutationNetworkGadget.SwitchingNetwork.Edge getSecondEdge() {
/* 195 */         if (this.edges.size() == 1) {
/* 196 */           return this.edges.get(0);
/*     */         }
/* 198 */         if (this.isSourceNode) {
/* 199 */           return (((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).srcIdx > ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).srcIdx) ? this.edges
/* 200 */             .get(0) : this.edges.get(1);
/*     */         }
/* 202 */         return (((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).dstIdx > ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).dstIdx) ? this.edges
/* 203 */           .get(0) : this.edges.get(1);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public boolean isVisited() {
/* 212 */         return this.visited;
/*     */       }
/*     */ 
/*     */       
/*     */       public PermutationNetworkGadget.SwitchingNetwork.Edge getAnyUnvisitedEdge() {
/* 217 */         if (this.edges.size() == 1) {
/* 218 */           if (((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).visited) {
/* 219 */             return null;
/*     */           }
/* 221 */           return this.edges.get(0);
/*     */         } 
/*     */         
/* 224 */         if (((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).visited) {
/* 225 */           if (((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).visited) {
/* 226 */             return null;
/*     */           }
/* 228 */           return this.edges.get(1);
/*     */         } 
/*     */         
/* 231 */         return this.edges.get(0);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public void color() {
/* 237 */         if (this.edges.size() == 1) {
/* 238 */           if (!((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).visited)
/*     */           {
/*     */             
/* 241 */             throw new RuntimeException("You should not be here!");
/*     */           }
/*     */         } else {
/* 244 */           if (((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).visited) {
/* 245 */             if (!((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).visited)
/*     */             {
/*     */ 
/*     */               
/* 249 */               ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).color = !((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).color;
/*     */             }
/*     */           }
/* 252 */           else if (((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).visited) {
/* 253 */             ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).color = !((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).color;
/*     */           } else {
/* 255 */             ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).color = true;
/* 256 */             ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).color = false;
/*     */           } 
/*     */           
/* 259 */           ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).visited = true;
/* 260 */           ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).visited = true;
/*     */         } 
/*     */       }
/*     */       
/*     */       public void setSwitch() {
/* 265 */         if (this.edges.size() == 1) {
/* 266 */           this.switchSetting = false;
/*     */         }
/* 268 */         else if (this.isSourceNode) {
/* 269 */           this.switchSetting = !((((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).srcIdx <= ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).srcIdx || 
/* 270 */             ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).color) && (
/* 271 */             ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).srcIdx <= ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).srcIdx || 
/* 272 */             ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).color));
/* 273 */           (PermutationNetworkGadget.SwitchingNetwork.this.inSwitches[((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).srcIdx / 2]).direction = this.switchSetting;
/*     */         } else {
/* 275 */           this.switchSetting = !((((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).dstIdx <= ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).dstIdx || 
/* 276 */             ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).color) && (
/* 277 */             ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).dstIdx <= ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).dstIdx || 
/* 278 */             ((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(1)).color));
/*     */           
/* 280 */           if (((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).dstIdx / 2 < PermutationNetworkGadget.SwitchingNetwork.this.outSwitches.length) {
/* 281 */             (PermutationNetworkGadget.SwitchingNetwork.this.outSwitches[((PermutationNetworkGadget.SwitchingNetwork.Edge)this.edges.get(0)).dstIdx / 2]).direction = this.switchSetting;
/*     */           }
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void route(int[] permutation) {
/* 292 */       if (permutation.length == 2) {
/* 293 */         if (permutation[0] != 0) {
/* 294 */           (this.inSwitches[0]).direction = true;
/*     */         } else {
/* 296 */           (this.inSwitches[0]).direction = false;
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/* 301 */       int numberOfNodes = (int)Math.ceil(permutation.length / 2.0D);
/* 302 */       Node[] srcNodes = new Node[numberOfNodes];
/* 303 */       Node[] dstNodes = new Node[numberOfNodes];
/* 304 */       ArrayList<Edge> allEdges = new ArrayList<>();
/*     */       int i;
/* 306 */       for (i = 0; i < numberOfNodes; i++) {
/* 307 */         srcNodes[i] = new Node(true);
/* 308 */         dstNodes[i] = new Node(false);
/*     */       } 
/*     */       
/* 311 */       for (i = 0; i < permutation.length; i++) {
/* 312 */         int srcIndex = i;
/* 313 */         int dstIndex = permutation[i];
/*     */         
/* 315 */         Edge e = new Edge(srcNodes[srcIndex / 2], 
/* 316 */             dstNodes[dstIndex / 2], srcIndex, dstIndex);
/* 317 */         allEdges.add(e);
/*     */       } 
/*     */ 
/*     */       
/* 321 */       (dstNodes[dstNodes.length - 1].getSecondEdge()).color = true;
/* 322 */       (dstNodes[dstNodes.length - 1].getSecondEdge()).visited = true;
/* 323 */       Stack<Node> allNodes = new Stack<>();
/*     */       
/* 325 */       for (int j = 0; j < numberOfNodes; j++) {
/* 326 */         allNodes.push(srcNodes[j]);
/* 327 */         allNodes.push(dstNodes[j]);
/*     */       } 
/* 329 */       allNodes.push(dstNodes[dstNodes.length - 1]);
/*     */       
/* 331 */       while (!allNodes.isEmpty()) {
/* 332 */         Node n = allNodes.pop();
/* 333 */         if (!n.isVisited()) {
/* 334 */           n.visited = true;
/* 335 */           n.color();
/* 336 */           n.setSwitch();
/*     */           
/* 338 */           for (Edge e : n.edges) {
/* 339 */             if (!e.src.visited) {
/* 340 */               allNodes.push(e.src);
/*     */             }
/* 342 */             if (!e.dst.visited) {
/* 343 */               allNodes.push(e.dst);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 350 */       if (permutation.length >= 3) {
/* 351 */         int[] upperPermutation = new int[permutation.length / 2];
/* 352 */         for (Edge e : allEdges) {
/* 353 */           if (!e.color)
/*     */           {
/* 355 */             upperPermutation[e.srcIdx / 2] = e.dstIdx / 2;
/*     */           }
/*     */         } 
/* 358 */         if (this.top != null) {
/* 359 */           this.top.route(upperPermutation);
/*     */         }
/*     */         
/* 362 */         int[] lowerPermutation = new int[permutation.length / 2 + 
/* 363 */             permutation.length % 2];
/* 364 */         for (Edge e : allEdges) {
/* 365 */           if (e.color) {
/* 366 */             lowerPermutation[e.srcIdx / 2] = e.dstIdx / 2;
/*     */           }
/*     */         } 
/* 369 */         if (this.bottom != null) {
/* 370 */           this.bottom.route(lowerPermutation);
/*     */         }
/*     */       } 
/*     */     }
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
/*     */     
/*     */     public int getNumSwitches() {
/* 406 */       return this.numSwitches;
/*     */     } }
/*     */   public class Switch { boolean direction;
/*     */     int id;
/*     */     Wire[] in1;
/*     */     
/*     */     public Switch(Wire[] w1, Wire[] w2, Wire[] w3) {
/* 413 */       PermutationNetworkGadget.switchIndex = PermutationNetworkGadget.switchIndex + 1; this.id = PermutationNetworkGadget.switchIndex;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 422 */       this.out1 = w1;
/* 423 */       this.out2 = w2;
/* 424 */       this.in1 = w3;
/* 425 */       int dim = w1.length;
/*     */       
/* 427 */       this.in2 = new Wire[dim];
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
/* 442 */       if (dim == 1) {
/*     */         
/* 444 */         PermutationNetworkGadget.this.generator.__addAssertion(this.in1[0].sub(this.out1[0], new String[0]), this.in1[0].sub(this.out2[0], new String[0]), PermutationNetworkGadget.this.generator.__getZeroWire(), new String[0]);
/*     */       } else {
/*     */         
/* 447 */         this.selector = PermutationNetworkGadget.this.generator.__createProverWitnessWire(new String[0]);
/* 448 */         PermutationNetworkGadget.this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */             {
/*     */               public void evaluate(CircuitEvaluator evaluator) {
/* 451 */                 if (((PermutationNetworkGadget.Switch.access$0(PermutationNetworkGadget.Switch.this)).switchMap.get(Integer.valueOf(PermutationNetworkGadget.Switch.this.id))).direction) {
/* 452 */                   evaluator.setWireValue(PermutationNetworkGadget.Switch.this.selector, BigInteger.ONE);
/*     */                 } else {
/*     */                   
/* 455 */                   evaluator.setWireValue(PermutationNetworkGadget.Switch.this.selector, BigInteger.ZERO);
/*     */                 } 
/*     */               }
/*     */             });
/* 459 */         PermutationNetworkGadget.this.generator.__addBinaryAssertion(this.selector, new String[0]);
/* 460 */         for (int j = 0; j < dim; j++) {
/* 461 */           PermutationNetworkGadget.this.generator.__addAssertion(this.selector, this.out2[j].sub(this.out1[j], new String[0]), this.in1[j].sub(this.out1[j], new String[0]), new String[0]);
/*     */         }
/*     */       } 
/*     */       
/* 465 */       for (int i = 0; i < dim; i++)
/* 466 */         this.in2[i] = w1[i].add(w2[i], new String[0]).sub(this.in1[i], new String[0]); 
/*     */     }
/*     */     Wire[] in2; Wire[] out1; Wire[] out2; Wire selector;
/*     */     public Switch(Wire[] w1, Wire[] w2, boolean isInput) {
/*     */       PermutationNetworkGadget.switchIndex = PermutationNetworkGadget.switchIndex + 1;
/*     */       this.id = PermutationNetworkGadget.switchIndex;
/* 472 */       if (isInput) {
/* 473 */         this.in1 = w1;
/* 474 */         this.in2 = w2;
/* 475 */         int dim = w1.length;
/* 476 */         this.out1 = new Wire[dim];
/* 477 */         this.out2 = new Wire[dim];
/*     */         
/* 479 */         if (dim == 1) {
/* 480 */           this.out1 = PermutationNetworkGadget.this.generator.__createProverWitnessWireArray(1, new String[0]);
/* 481 */           PermutationNetworkGadget.this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */               {
/*     */                 public void evaluate(CircuitEvaluator evaluator) {
/* 484 */                   if (((PermutationNetworkGadget.Switch.access$0(PermutationNetworkGadget.Switch.this)).switchMap.get(Integer.valueOf(PermutationNetworkGadget.Switch.this.id))).direction) {
/* 485 */                     evaluator.setWireValue(PermutationNetworkGadget.Switch.this.out1[0], evaluator.getWireValue(PermutationNetworkGadget.Switch.this.in2[0]));
/*     */                   } else {
/*     */                     
/* 488 */                     evaluator.setWireValue(PermutationNetworkGadget.Switch.this.out1[0], evaluator.getWireValue(PermutationNetworkGadget.Switch.this.in1[0]));
/*     */                   } 
/*     */                 }
/*     */               });
/* 492 */           this.out2 = new Wire[] { w1[0].add(w2[0], new String[0]).sub(this.out1[0], new String[0]) };
/*     */ 
/*     */           
/* 495 */           PermutationNetworkGadget.this.generator.__addAssertion(this.in1[0].sub(this.out1[0], new String[0]), this.in2[0].sub(this.out1[0], new String[0]), PermutationNetworkGadget.this.generator.__getZeroWire(), new String[0]);
/*     */         }
/*     */         else {
/*     */           
/* 499 */           this.selector = PermutationNetworkGadget.this.generator.__createProverWitnessWire(new String[0]);
/* 500 */           PermutationNetworkGadget.this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */               {
/*     */                 public void evaluate(CircuitEvaluator evaluator) {
/* 503 */                   if (((PermutationNetworkGadget.Switch.access$0(PermutationNetworkGadget.Switch.this)).switchMap.get(Integer.valueOf(PermutationNetworkGadget.Switch.this.id))).direction) {
/* 504 */                     evaluator.setWireValue(PermutationNetworkGadget.Switch.this.selector, BigInteger.ONE);
/*     */                   } else {
/*     */                     
/* 507 */                     evaluator.setWireValue(PermutationNetworkGadget.Switch.this.selector, BigInteger.ZERO);
/*     */                   } 
/*     */                 }
/*     */               });
/* 511 */           PermutationNetworkGadget.this.generator.__addBinaryAssertion(this.selector, new String[0]);
/* 512 */           for (int i = 0; i < dim; i++) {
/* 513 */             this.out1[i] = this.in1[i].add(this.selector.mul(this.in2[i].sub(this.in1[i], new String[0]), new String[0]), new String[0]);
/* 514 */             this.out2[i] = this.in1[i].add(this.in2[i], new String[0]).sub(this.out1[i], new String[0]);
/*     */           }
/*     */         
/*     */         } 
/*     */       } else {
/*     */         
/* 520 */         this.out1 = w1;
/* 521 */         this.out2 = w2;
/* 522 */         int dim = w1.length;
/* 523 */         this.in1 = new Wire[dim];
/* 524 */         this.in2 = new Wire[dim];
/*     */         
/* 526 */         if (dim == 1) {
/* 527 */           this.in1 = PermutationNetworkGadget.this.generator.__createProverWitnessWireArray(1, new String[0]);
/* 528 */           PermutationNetworkGadget.this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */               {
/*     */                 public void evaluate(CircuitEvaluator evaluator) {
/* 531 */                   if (((PermutationNetworkGadget.Switch.access$0(PermutationNetworkGadget.Switch.this)).switchMap.get(Integer.valueOf(PermutationNetworkGadget.Switch.this.id))).direction) {
/* 532 */                     evaluator.setWireValue(PermutationNetworkGadget.Switch.this.in1[0], evaluator.getWireValue(PermutationNetworkGadget.Switch.this.out2[0]));
/*     */                   } else {
/*     */                     
/* 535 */                     evaluator.setWireValue(PermutationNetworkGadget.Switch.this.in1[0], evaluator.getWireValue(PermutationNetworkGadget.Switch.this.out1[0]));
/*     */                   } 
/*     */                 }
/*     */               });
/*     */           
/* 540 */           this.in2 = new Wire[] { w1[0].add(w2[0], new String[0]).sub(this.in1[0], new String[0]) };
/*     */           
/* 542 */           PermutationNetworkGadget.this.generator.__addAssertion(this.in1[0].sub(this.out1[0], new String[0]), this.in1[0].sub(this.out2[0], new String[0]), PermutationNetworkGadget.this.generator.__getZeroWire(), new String[0]);
/*     */         } else {
/*     */           
/* 545 */           this.selector = PermutationNetworkGadget.this.generator.__createProverWitnessWire(new String[0]);
/* 546 */           PermutationNetworkGadget.this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */               {
/*     */                 public void evaluate(CircuitEvaluator evaluator) {
/* 549 */                   if (((PermutationNetworkGadget.Switch.access$0(PermutationNetworkGadget.Switch.this)).switchMap.get(Integer.valueOf(PermutationNetworkGadget.Switch.this.id))).direction) {
/* 550 */                     evaluator.setWireValue(PermutationNetworkGadget.Switch.this.selector, BigInteger.ONE);
/*     */                   } else {
/*     */                     
/* 553 */                     evaluator.setWireValue(PermutationNetworkGadget.Switch.this.selector, BigInteger.ZERO);
/*     */                   } 
/*     */                 }
/*     */               });
/* 557 */           PermutationNetworkGadget.this.generator.__addBinaryAssertion(this.selector, new String[0]);
/* 558 */           for (int i = 0; i < dim; i++) {
/* 559 */             this.in1[i] = this.out1[i].add(this.selector.mul(this.out2[i].sub(this.out1[i], new String[0]), new String[0]), new String[0]);
/* 560 */             this.in2[i] = this.out1[i].add(this.out2[i], new String[0]).sub(this.in1[i], new String[0]);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Switch(Wire[] in1, Wire[] in2, Wire[] out1, Wire[] out2) {
/*     */       PermutationNetworkGadget.switchIndex = PermutationNetworkGadget.switchIndex + 1;
/*     */       this.id = PermutationNetworkGadget.switchIndex;
/* 572 */       this.in1 = in1;
/* 573 */       this.in2 = in2;
/* 574 */       this.out1 = out1;
/* 575 */       this.out2 = out2;
/*     */ 
/*     */ 
/*     */       
/* 579 */       int dim = in1.length;
/* 580 */       if (dim == 1) {
/*     */         
/* 582 */         PermutationNetworkGadget.this.generator.__addAssertion(in1[0].sub(out1[0], new String[0]), in2[0].sub(out1[0], new String[0]), PermutationNetworkGadget.this.generator.__getZeroWire(), new String[] { "CASE I" });
/* 583 */         PermutationNetworkGadget.this.generator.__addZeroAssertion(in1[0].add(in2[0], new String[0]).sub(out1[0].add(out2[0], new String[0]), new String[0]), new String[] { "CASE II" });
/*     */       }
/*     */       else {
/*     */         
/* 587 */         this.selector = PermutationNetworkGadget.this.generator.__createProverWitnessWire(new String[0]);
/* 588 */         PermutationNetworkGadget.this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */             {
/*     */               public void evaluate(CircuitEvaluator evaluator) {
/* 591 */                 if (((PermutationNetworkGadget.Switch.access$0(PermutationNetworkGadget.Switch.this)).switchMap.get(Integer.valueOf(PermutationNetworkGadget.Switch.this.id))).direction) {
/* 592 */                   evaluator.setWireValue(PermutationNetworkGadget.Switch.this.selector, BigInteger.ONE);
/*     */                 } else {
/*     */                   
/* 595 */                   evaluator.setWireValue(PermutationNetworkGadget.Switch.this.selector, BigInteger.ZERO);
/*     */                 } 
/*     */               }
/*     */             });
/* 599 */         PermutationNetworkGadget.this.generator.__addBinaryAssertion(this.selector, new String[0]);
/* 600 */         for (int i = 0; i < dim; i++) {
/* 601 */           PermutationNetworkGadget.this.generator.__addAssertion(this.selector, out2[i].sub(out1[i], new String[0]), in1[i].sub(out1[i], new String[0]), new String[0]);
/* 602 */           PermutationNetworkGadget.this.generator.__addEqualityAssertion(out1[i].add(out2[i], new String[0]).sub(in1[i], new String[0]), in2[i], new String[0]);
/*     */         } 
/*     */       } 
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PermutationNetworkGadget(final Wire[][] inputs, final Wire[][] outputs, int[] bitwidth, final String permutationNetworkId) {
/* 612 */     super(new String[0]);
/*     */     
/* 614 */     this.inputs = inputs;
/* 615 */     this.outputs = outputs;
/* 616 */     this.dim = (inputs[0]).length;
/* 617 */     this.permutationNetworkId = permutationNetworkId;
/* 618 */     this.generator.__specifyProverWitnessComputation(new Instruction()
/*     */         {
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
/*     */           public void evaluate(CircuitEvaluator evaluator)
/*     */           {
/* 639 */             int[] permutation = (int[])evaluator.getPermutations().get(permutationNetworkId);
/* 640 */             if (permutation == null) {
/*     */ 
/*     */ 
/*     */               
/* 644 */               permutation = new int[inputs.length];
/* 645 */               boolean[] reserved = new boolean[outputs.length];
/* 646 */               for (int i = 0; i < inputs.length; i++) {
/* 647 */                 boolean found = false;
/* 648 */                 for (int j = 0; j < outputs.length; j++) {
/* 649 */                   if (!reserved[j]) {
/*     */ 
/*     */                     
/* 652 */                     boolean equal = true;
/* 653 */                     for (int k = 0; k < PermutationNetworkGadget.this.dim; k++) {
/* 654 */                       BigInteger v1 = evaluator.getWireValue(inputs[i][k]);
/* 655 */                       BigInteger v2 = evaluator.getWireValue(outputs[j][k]);
/* 656 */                       if (!v1.equals(v2)) {
/* 657 */                         equal = false;
/*     */                         break;
/*     */                       } 
/*     */                     } 
/* 661 */                     if (equal) {
/* 662 */                       permutation[i] = j;
/* 663 */                       found = true;
/* 664 */                       reserved[j] = true;
/*     */                       break;
/*     */                     } 
/*     */                   } 
/*     */                 } 
/* 669 */                 if (!found) {
/* 670 */                   throw new RuntimeException("No Valid permutation found during evaluation.");
/*     */                 }
/*     */               } 
/*     */             } 
/*     */ 
/*     */ 
/*     */             
/* 677 */             PermutationNetworkGadget.this.switchMap = (new PermutationNetworkAux(permutation.length, permutation)).getSetting();
/*     */           }
/*     */         });
/*     */     
/* 681 */     buildCircuit();
/*     */   }
/*     */ 
/*     */   
/*     */   private void buildCircuit() {
/* 686 */     this.network = new SwitchingNetwork(this.inputs, this.outputs);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Wire[] getOutputWires() {
/* 692 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public SwitchingNetwork getNetwork() {
/* 697 */     return this.network;
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\PermutationNetworkGadget.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */