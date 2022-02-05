/*     */ package examples.gadgets;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Stack;
/*     */ import sandbox.ArrayIndexComparator;
/*     */ import util.Util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PermutationNetworkAux
/*     */ {
/*  19 */   private HashMap<Integer, DummySwitch> switchMap = new HashMap<>();
/*  20 */   private static int switchIndex = 0;
/*     */ 
/*     */   
/*     */   public static class DummyWire
/*     */   {
/*     */     BigInteger value;
/*     */   }
/*     */ 
/*     */   
/*     */   public class PermutationNetwork
/*     */   {
/*     */     int n;
/*     */     PermutationNetworkAux.DummySwitch[] inSwitches;
/*     */     PermutationNetworkAux.DummySwitch[] outSwitches;
/*     */     PermutationNetwork top;
/*     */     PermutationNetwork bottom;
/*     */     PermutationNetworkAux.DummyWire[] ins;
/*     */     PermutationNetworkAux.DummyWire[] outs;
/*  38 */     int numSwitches = 0;
/*     */     
/*     */     public PermutationNetwork(PermutationNetworkAux.DummyWire[] ins, PermutationNetworkAux.DummyWire[] outs) {
/*  41 */       this.ins = ins;
/*     */       
/*  43 */       this.outs = outs;
/*  44 */       this.n = ins.length;
/*  45 */       build();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void build() {
/*  52 */       if (this.n == 2) {
/*  53 */         this.inSwitches = this.outSwitches = new PermutationNetworkAux.DummySwitch[1];
/*  54 */         this.inSwitches[0] = new PermutationNetworkAux.DummySwitch(this.ins[0], this.ins[1], this.outs[0], this.outs[1]);
/*  55 */         this.numSwitches = 1;
/*  56 */       } else if (this.n == 3) {
/*  57 */         this.inSwitches = new PermutationNetworkAux.DummySwitch[1];
/*  58 */         this.outSwitches = new PermutationNetworkAux.DummySwitch[1];
/*  59 */         this.inSwitches[0] = new PermutationNetworkAux.DummySwitch(this.ins[0], this.ins[1], true);
/*  60 */         this.outSwitches[0] = new PermutationNetworkAux.DummySwitch(this.outs[0], this.outs[1], false);
/*  61 */         (this.outSwitches[0]).in1 = (this.inSwitches[0]).out1;
/*     */         
/*  63 */         this.top = null;
/*  64 */         this.bottom = new PermutationNetwork(new PermutationNetworkAux.DummyWire[] {
/*  65 */               (this.inSwitches[0]).out2, this.ins[2] }, new PermutationNetworkAux.DummyWire[] {
/*  66 */               (this.outSwitches[0]).in2, this.outs[2]
/*     */             });
/*  68 */         this.numSwitches += 2 + this.bottom.numSwitches;
/*     */       }
/*  70 */       else if (this.n % 2 == 1) {
/*  71 */         this.inSwitches = new PermutationNetworkAux.DummySwitch[this.n / 2];
/*  72 */         this.outSwitches = new PermutationNetworkAux.DummySwitch[this.n / 2];
/*     */         
/*  74 */         PermutationNetworkAux.DummyWire[] topInput = new PermutationNetworkAux.DummyWire[this.n / 2];
/*  75 */         PermutationNetworkAux.DummyWire[] bottomInput = new PermutationNetworkAux.DummyWire[this.n / 2 + this.n % 2];
/*  76 */         PermutationNetworkAux.DummyWire[] topOutput = new PermutationNetworkAux.DummyWire[this.n / 2];
/*  77 */         PermutationNetworkAux.DummyWire[] bottomOutput = new PermutationNetworkAux.DummyWire[this.n / 2 + this.n % 2];
/*     */         int i;
/*  79 */         for (i = 0; i < this.inSwitches.length; i++) {
/*  80 */           this.inSwitches[i] = new PermutationNetworkAux.DummySwitch(this.ins[2 * i], this.ins[2 * i + 1], true);
/*  81 */           topInput[i] = (this.inSwitches[i]).out1;
/*  82 */           bottomInput[i] = (this.inSwitches[i]).out2;
/*     */         } 
/*     */         
/*  85 */         for (i = 0; i < this.outSwitches.length; i++) {
/*  86 */           this.outSwitches[i] = new PermutationNetworkAux.DummySwitch(this.outs[2 * i], this.outs[2 * i + 1], 
/*  87 */               false);
/*  88 */           topOutput[i] = (this.outSwitches[i]).in1;
/*  89 */           bottomOutput[i] = (this.outSwitches[i]).in2;
/*     */         } 
/*     */         
/*  92 */         bottomInput[bottomInput.length - 1] = this.ins[this.ins.length - 1];
/*  93 */         bottomOutput[bottomOutput.length - 1] = this.outs[this.outs.length - 1];
/*  94 */         this.top = new PermutationNetwork(topInput, topOutput);
/*  95 */         this.bottom = new PermutationNetwork(bottomInput, bottomOutput);
/*  96 */         this.numSwitches = this.top.numSwitches + this.bottom.numSwitches + 
/*  97 */           this.outSwitches.length + this.inSwitches.length;
/*  98 */       } else if (this.n % 2 == 0) {
/*  99 */         this.inSwitches = new PermutationNetworkAux.DummySwitch[this.n / 2];
/* 100 */         this.outSwitches = new PermutationNetworkAux.DummySwitch[this.n / 2 - 1];
/*     */         
/* 102 */         PermutationNetworkAux.DummyWire[] topInput = new PermutationNetworkAux.DummyWire[this.n / 2];
/* 103 */         PermutationNetworkAux.DummyWire[] bottomInput = new PermutationNetworkAux.DummyWire[this.n / 2 + this.n % 2];
/* 104 */         PermutationNetworkAux.DummyWire[] topOutput = new PermutationNetworkAux.DummyWire[this.n / 2];
/* 105 */         PermutationNetworkAux.DummyWire[] bottomOutput = new PermutationNetworkAux.DummyWire[this.n / 2 + this.n % 2];
/*     */         int i;
/* 107 */         for (i = 0; i < this.inSwitches.length; i++) {
/* 108 */           this.inSwitches[i] = new PermutationNetworkAux.DummySwitch(this.ins[2 * i], this.ins[2 * i + 1], true);
/* 109 */           topInput[i] = (this.inSwitches[i]).out1;
/* 110 */           bottomInput[i] = (this.inSwitches[i]).out2;
/*     */         } 
/*     */         
/* 113 */         for (i = 0; i < this.outSwitches.length; i++) {
/* 114 */           this.outSwitches[i] = new PermutationNetworkAux.DummySwitch(this.outs[2 * i], this.outs[2 * i + 1], 
/* 115 */               false);
/* 116 */           topOutput[i] = (this.outSwitches[i]).in1;
/* 117 */           bottomOutput[i] = (this.outSwitches[i]).in2;
/*     */         } 
/*     */         
/* 120 */         topOutput[topOutput.length - 1] = this.outs[this.outs.length - 2];
/* 121 */         bottomOutput[bottomOutput.length - 1] = this.outs[this.outs.length - 1];
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 126 */         this.top = new PermutationNetwork(topInput, topOutput);
/* 127 */         this.bottom = new PermutationNetwork(bottomInput, bottomOutput);
/* 128 */         this.numSwitches = this.top.numSwitches + this.bottom.numSwitches + 
/* 129 */           this.outSwitches.length + this.inSwitches.length;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     class Edge
/*     */     {
/*     */       boolean visited;
/*     */       
/*     */       boolean color;
/*     */       PermutationNetworkAux.PermutationNetwork.Node src;
/*     */       PermutationNetworkAux.PermutationNetwork.Node dst;
/*     */       int srcIdx;
/*     */       int dstIdx;
/*     */       
/*     */       public Edge(PermutationNetworkAux.PermutationNetwork.Node src, PermutationNetworkAux.PermutationNetwork.Node dst, int srcIdx, int dstIdx) {
/* 145 */         this.src = src;
/* 146 */         this.dst = dst;
/* 147 */         this.srcIdx = srcIdx;
/* 148 */         this.dstIdx = dstIdx;
/* 149 */         src.edges.add(this);
/* 150 */         dst.edges.add(this);
/*     */       }
/*     */     }
/*     */     
/*     */     class Node
/*     */     {
/* 156 */       ArrayList<PermutationNetworkAux.PermutationNetwork.Edge> edges = new ArrayList<>();
/*     */       boolean isSourceNode;
/*     */       boolean visited = false;
/*     */       boolean switchSetting = false;
/*     */       
/*     */       public Node(boolean isSourceNode) {
/* 162 */         this.isSourceNode = isSourceNode;
/*     */       }
/*     */       
/*     */       public PermutationNetworkAux.PermutationNetwork.Edge getFirstEdge() {
/* 166 */         if (this.edges.size() == 1) {
/* 167 */           return this.edges.get(0);
/*     */         }
/* 169 */         if (this.isSourceNode) {
/* 170 */           return (((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).srcIdx < ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).srcIdx) ? this.edges
/* 171 */             .get(0) : this.edges.get(1);
/*     */         }
/* 173 */         return (((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).dstIdx < ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).dstIdx) ? this.edges
/* 174 */           .get(0) : this.edges.get(1);
/*     */       }
/*     */ 
/*     */       
/*     */       public PermutationNetworkAux.PermutationNetwork.Edge getSecondEdge() {
/* 179 */         if (this.edges.size() == 1) {
/* 180 */           return this.edges.get(0);
/*     */         }
/* 182 */         if (this.isSourceNode) {
/* 183 */           return (((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).srcIdx > ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).srcIdx) ? this.edges
/* 184 */             .get(0) : this.edges.get(1);
/*     */         }
/* 186 */         return (((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).dstIdx > ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).dstIdx) ? this.edges
/* 187 */           .get(0) : this.edges.get(1);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       public boolean isVisited() {
/* 196 */         return this.visited;
/*     */       }
/*     */ 
/*     */       
/*     */       public PermutationNetworkAux.PermutationNetwork.Edge getAnyUnvisitedEdge() {
/* 201 */         if (this.edges.size() == 1) {
/* 202 */           if (((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).visited) {
/* 203 */             return null;
/*     */           }
/* 205 */           return this.edges.get(0);
/*     */         } 
/*     */         
/* 208 */         if (((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).visited) {
/* 209 */           if (((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).visited) {
/* 210 */             return null;
/*     */           }
/* 212 */           return this.edges.get(1);
/*     */         } 
/*     */         
/* 215 */         return this.edges.get(0);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public void color() {
/* 221 */         if (this.edges.size() == 1) {
/* 222 */           if (!((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).visited)
/*     */           {
/*     */             
/* 225 */             throw new RuntimeException("Unexpected case! Please report.");
/*     */           }
/*     */         } else {
/* 228 */           if (((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).visited) {
/* 229 */             if (!((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).visited)
/*     */             {
/*     */               
/* 232 */               ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).color = !((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).color;
/*     */             }
/*     */           }
/* 235 */           else if (((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).visited) {
/* 236 */             ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).color = !((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).color;
/*     */           } else {
/* 238 */             ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).color = true;
/* 239 */             ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).color = false;
/*     */           } 
/*     */           
/* 242 */           ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).visited = true;
/* 243 */           ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).visited = true;
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/*     */       public void setSwitch() {
/* 249 */         if (this.edges.size() == 1) {
/* 250 */           this.switchSetting = false;
/*     */         
/*     */         }
/* 253 */         else if (this.isSourceNode) {
/*     */ 
/*     */           
/* 256 */           this.switchSetting = !((((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).srcIdx <= ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).srcIdx || 
/* 257 */             ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).color) && (
/* 258 */             ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).srcIdx <= ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).srcIdx || 
/* 259 */             ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).color));
/* 260 */           (PermutationNetworkAux.PermutationNetwork.this.inSwitches[((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).srcIdx / 2]).direction = this.switchSetting;
/*     */         } else {
/* 262 */           this.switchSetting = !((((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).dstIdx <= ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).dstIdx || 
/* 263 */             ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).color) && (
/* 264 */             ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).dstIdx <= ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).dstIdx || 
/* 265 */             ((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(1)).color));
/*     */           
/* 267 */           if (((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).dstIdx / 2 < PermutationNetworkAux.PermutationNetwork.this.outSwitches.length)
/*     */           {
/* 269 */             (PermutationNetworkAux.PermutationNetwork.this.outSwitches[((PermutationNetworkAux.PermutationNetwork.Edge)this.edges.get(0)).dstIdx / 2]).direction = this.switchSetting;
/*     */           }
/*     */         } 
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void route(int[] permutation) {
/* 278 */       if (permutation.length == 2) {
/* 279 */         if (permutation[0] != 0) {
/* 280 */           (this.inSwitches[0]).direction = true;
/*     */         } else {
/* 282 */           (this.inSwitches[0]).direction = false;
/*     */         } 
/*     */         
/*     */         return;
/*     */       } 
/* 287 */       int numberOfNodes = (int)Math.ceil(permutation.length / 2.0D);
/* 288 */       Node[] srcNodes = new Node[numberOfNodes];
/* 289 */       Node[] dstNodes = new Node[numberOfNodes];
/* 290 */       ArrayList<Edge> allEdges = new ArrayList<>();
/*     */       int i;
/* 292 */       for (i = 0; i < numberOfNodes; i++) {
/* 293 */         srcNodes[i] = new Node(true);
/* 294 */         dstNodes[i] = new Node(false);
/*     */       } 
/*     */       
/* 297 */       for (i = 0; i < permutation.length; i++) {
/* 298 */         int srcIndex = i;
/* 299 */         int dstIndex = permutation[i];
/*     */         
/* 301 */         Edge e = new Edge(srcNodes[srcIndex / 2], 
/* 302 */             dstNodes[dstIndex / 2], srcIndex, dstIndex);
/* 303 */         allEdges.add(e);
/*     */       } 
/*     */ 
/*     */       
/* 307 */       (dstNodes[dstNodes.length - 1].getSecondEdge()).color = true;
/* 308 */       (dstNodes[dstNodes.length - 1].getSecondEdge()).visited = true;
/* 309 */       Stack<Node> allNodes = new Stack<>();
/*     */       
/* 311 */       for (int j = 0; j < numberOfNodes; j++) {
/* 312 */         allNodes.push(srcNodes[j]);
/* 313 */         allNodes.push(dstNodes[j]);
/*     */       } 
/* 315 */       allNodes.push(dstNodes[dstNodes.length - 1]);
/*     */       
/* 317 */       while (!allNodes.isEmpty()) {
/* 318 */         Node n = allNodes.pop();
/* 319 */         if (!n.isVisited()) {
/* 320 */           n.visited = true;
/* 321 */           n.color();
/* 322 */           n.setSwitch();
/*     */           
/* 324 */           for (Edge e : n.edges) {
/* 325 */             if (!e.src.visited) {
/* 326 */               allNodes.push(e.src);
/*     */             }
/* 328 */             if (!e.dst.visited) {
/* 329 */               allNodes.push(e.dst);
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 336 */       if (permutation.length >= 3) {
/* 337 */         int[] upperPermutation = new int[permutation.length / 2];
/* 338 */         for (Edge e : allEdges) {
/* 339 */           if (!e.color)
/*     */           {
/* 341 */             upperPermutation[e.srcIdx / 2] = e.dstIdx / 2;
/*     */           }
/*     */         } 
/* 344 */         if (this.top != null) {
/* 345 */           this.top.route(upperPermutation);
/*     */         }
/*     */         
/* 348 */         int[] lowerPermutation = new int[permutation.length / 2 + 
/* 349 */             permutation.length % 2];
/* 350 */         for (Edge e : allEdges) {
/* 351 */           if (e.color) {
/* 352 */             lowerPermutation[e.srcIdx / 2] = e.dstIdx / 2;
/*     */           }
/*     */         } 
/* 355 */         if (this.bottom != null) {
/* 356 */           this.bottom.route(lowerPermutation);
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
/*     */     public void eval() {
/*     */       byte b;
/*     */       int i;
/*     */       PermutationNetworkAux.DummySwitch[] arrayOfDummySwitch;
/* 392 */       for (i = (arrayOfDummySwitch = this.inSwitches).length, b = 0; b < i; ) { PermutationNetworkAux.DummySwitch s = arrayOfDummySwitch[b];
/* 393 */         s.map(); b++; }
/*     */       
/* 395 */       if (this.top != null) {
/* 396 */         this.top.eval();
/*     */       }
/* 398 */       if (this.bottom != null) {
/* 399 */         this.bottom.eval();
/*     */       }
/*     */       
/* 402 */       for (i = (arrayOfDummySwitch = this.outSwitches).length, b = 0; b < i; ) { PermutationNetworkAux.DummySwitch s = arrayOfDummySwitch[b];
/* 403 */         s.map();
/*     */         b++; }
/*     */        } public void printOutputs() { byte b;
/*     */       int i;
/*     */       PermutationNetworkAux.DummyWire[] arrayOfDummyWire;
/* 408 */       for (i = (arrayOfDummyWire = this.outs).length, b = 0; b < i; ) { PermutationNetworkAux.DummyWire w = arrayOfDummyWire[b];
/* 409 */         System.out.println("Value = " + w.value);
/*     */         b++; }
/*     */        }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public class DummySwitch
/*     */   {
/*     */     boolean direction;
/*     */     PermutationNetworkAux.DummyWire in1;
/*     */     PermutationNetworkAux.DummyWire in2;
/*     */     PermutationNetworkAux.DummyWire out1;
/*     */     PermutationNetworkAux.DummyWire out2;
/*     */     
/*     */     public DummySwitch(PermutationNetworkAux.DummyWire w1, PermutationNetworkAux.DummyWire w2, boolean isInput) {
/* 425 */       PermutationNetworkAux.switchIndex = PermutationNetworkAux.switchIndex + 1; PermutationNetworkAux.this.switchMap.put(Integer.valueOf(PermutationNetworkAux.switchIndex), this);
/* 426 */       if (isInput) {
/* 427 */         this.in1 = w1;
/* 428 */         this.in2 = w2;
/* 429 */         this.out1 = new PermutationNetworkAux.DummyWire();
/* 430 */         this.out2 = new PermutationNetworkAux.DummyWire();
/*     */       } else {
/* 432 */         this.out1 = w1;
/* 433 */         this.out2 = w2;
/* 434 */         this.in1 = new PermutationNetworkAux.DummyWire();
/* 435 */         this.in2 = new PermutationNetworkAux.DummyWire();
/*     */       } 
/* 437 */       if (this.in1 == null || this.in2 == null || this.out1 == null || this.out2 == null) {
/* 438 */         throw new NullPointerException();
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public DummySwitch(PermutationNetworkAux.DummyWire in1, PermutationNetworkAux.DummyWire in2, PermutationNetworkAux.DummyWire out1, PermutationNetworkAux.DummyWire out2) {
/* 445 */       PermutationNetworkAux.switchIndex = PermutationNetworkAux.switchIndex + 1; PermutationNetworkAux.this.switchMap.put(Integer.valueOf(PermutationNetworkAux.switchIndex), this);
/* 446 */       if (in1 == null || in2 == null || out1 == null || out2 == null) {
/* 447 */         throw new NullPointerException();
/*     */       }
/*     */       
/* 450 */       this.in1 = in1;
/* 451 */       this.in2 = in2;
/* 452 */       this.out1 = out1;
/* 453 */       this.out2 = out2;
/*     */     }
/*     */     
/*     */     void map() {
/* 457 */       if (this.in1.value == null || this.in2.value == null) {
/* 458 */         throw new NullPointerException();
/*     */       }
/* 460 */       if (!this.direction) {
/* 461 */         this.out1.value = this.in1.value;
/* 462 */         this.out2.value = this.in2.value;
/*     */       } else {
/* 464 */         this.out1.value = this.in2.value;
/* 465 */         this.out2.value = this.in1.value;
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public HashMap<Integer, DummySwitch> getSetting() {
/* 471 */     return this.switchMap;
/*     */   }
/*     */ 
/*     */   
/*     */   public PermutationNetworkAux(int n, int[] permutation) {
/* 476 */     DummyWire[] ins = new DummyWire[n];
/* 477 */     DummyWire[] outs = new DummyWire[n];
/* 478 */     for (int i = 0; i < n; i++) {
/* 479 */       ins[i] = new DummyWire();
/* 480 */       outs[i] = new DummyWire();
/*     */     } 
/* 482 */     PermutationNetwork p = new PermutationNetwork(ins, outs);
/* 483 */     p.route(permutation);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 490 */     int n = 1023;
/* 491 */     BigInteger[] a = new BigInteger[n];
/* 492 */     for (int i = 0; i < n; i++) {
/* 493 */       a[i] = Util.nextRandomBigInteger(200);
/*     */     }
/*     */     
/* 496 */     ArrayIndexComparator comparator = new ArrayIndexComparator(a);
/* 497 */     Integer[] indexes = new Integer[n];
/* 498 */     for (int j = 0; j < n; j++) {
/* 499 */       indexes[j] = Integer.valueOf(j);
/*     */     }
/* 501 */     Arrays.sort(indexes, (Comparator<? super Integer>)comparator);
/* 502 */     int[] permutation = new int[n];
/* 503 */     for (int k = 0; k < n; k++) {
/* 504 */       permutation[indexes[k].intValue()] = k;
/*     */     }
/*     */     
/* 507 */     DummyWire[] ins = new DummyWire[n];
/* 508 */     DummyWire[] outs = new DummyWire[n];
/*     */     
/* 510 */     for (int m = 0; m < n; m++) {
/* 511 */       ins[m] = new DummyWire();
/* 512 */       (ins[m]).value = a[m];
/* 513 */       outs[m] = new DummyWire();
/*     */     } 
/*     */     
/* 516 */     PermutationNetworkAux aux = new PermutationNetworkAux(n, permutation);
/* 517 */     aux.getClass(); PermutationNetwork p = new PermutationNetwork(ins, outs);
/* 518 */     System.out.println("Number of switches in constructed permutation network = " + p.numSwitches);
/*     */     
/* 520 */     int numSwitches = 0; int i1;
/* 521 */     for (i1 = 1; i1 <= n; i1++) {
/* 522 */       numSwitches += (int)Math.ceil(Math.log(i1) / Math.log(2.0D));
/*     */     }
/* 524 */     if (numSwitches == p.numSwitches) {
/* 525 */       System.out.println("The number of switches matches the equation.");
/*     */     } else {
/*     */       
/* 528 */       System.err.println("There is a mismatch -- double check.");
/*     */     } 
/* 530 */     p.route(permutation);
/* 531 */     p.eval();
/* 532 */     for (i1 = 1; i1 < p.outs.length; i1++) {
/* 533 */       if ((p.outs[i1 - 1]).value.compareTo((p.outs[i1]).value) > 0) {
/* 534 */         System.err.println("The outputs are not sorted as expected. There is likely a problem in routing.");
/* 535 */         throw new RuntimeException();
/*     */       } 
/*     */     } 
/* 538 */     System.out.println("The output is sorted");
/*     */   }
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\examples\gadgets\PermutationNetworkAux.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */