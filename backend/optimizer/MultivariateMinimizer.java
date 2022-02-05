/*     */ package backend.optimizer;
/*     */ 
/*     */ import backend.config.Config;
/*     */ import backend.optimizer.arithmetic.ExpressionMinimizer;
/*     */ import backend.optimizer.arithmetic.poly.MultivariatePolynomial;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultivariateMinimizer
/*     */ {
/*     */   private ArrayList<CircuitOptimizer.Problem> combinedProblems;
/*     */   private Queue<Future<?>> tasks;
/*     */   private Queue<CircuitOptimizer.Problem> problems;
/*     */   private Queue<CircuitOptimizer.Problem> problemsToSolve;
/*     */   private LinkedList<Long> startTimes;
/*  28 */   private int numThreads = Config.arithOptimizerNumThreads;
/*  29 */   private int timeout = Config.arithOptimizerTimeoutPerProblemMilliSec;
/*     */   
/*     */   private ExecutorService executor;
/*  32 */   private int numPolls = 0;
/*  33 */   private int numPuts = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int total;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CircuitOptimizer.Problem getNext() {
/*  44 */     return this.problemsToSolve.poll();
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*  49 */     for (CircuitOptimizer.Problem p : this.combinedProblems) {
/*  50 */       if (!p.isEmpty() && !p.isDontSolve() && 
/*  51 */         p.getMvpList().size() < 100 && p.getVariables().size() > 1 && 
/*  52 */         p.getVariables().size() < 100) {
/*  53 */         this.problemsToSolve.add(p);
/*     */       }
/*     */     } 
/*     */     
/*  57 */     this.executor = Executors.newFixedThreadPool(this.numThreads);
/*     */ 
/*     */     
/*  60 */     synchronized (this) {
/*  61 */       for (int i = 0; i < this.combinedProblems.size(); i++) {
/*  62 */         CircuitOptimizer.Problem p = getNext();
/*  63 */         if (p == null)
/*     */           break; 
/*  65 */         this.startTimes.add(Long.valueOf(System.currentTimeMillis()));
/*  66 */         Future<?> f = this.executor.submit(() -> {
/*     */               Object solution;
/*     */               
/*     */               try {
/*     */                 ExpressionMinimizer minimizer = paramProblem.prep();
/*     */                 solution = minimizer.run(1);
/*     */                 taskCompleted();
/*  73 */               } catch (Exception e) {
/*     */                 e.printStackTrace();
/*     */                 solution = null;
/*     */               } 
/*     */               return solution;
/*     */             });
/*  79 */         this.tasks.add(f);
/*  80 */         this.problems.add(p);
/*     */         
/*  82 */         this.numPuts++;
/*  83 */         if (this.tasks.size() == this.numThreads) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  90 */     while (!this.tasks.isEmpty()) {
/*     */ 
/*     */ 
/*     */       
/*  94 */       this.numPolls++;
/*  95 */       Future<?> f = null;
/*  96 */       CircuitOptimizer.Problem p = null;
/*  97 */       Long t1 = Long.valueOf(0L);
/*  98 */       synchronized (this) {
/*     */         
/* 100 */         f = this.tasks.poll();
/* 101 */         p = this.problems.poll();
/* 102 */         t1 = this.startTimes.poll();
/*     */       } 
/*     */       
/* 105 */       Long t2 = Long.valueOf(System.currentTimeMillis());
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/* 111 */         Object s = f.get((this.timeout - t2.longValue() - t1.longValue() > 0L) ? (this.timeout - t2.longValue() - t1.longValue()) : 
/* 112 */             1L, TimeUnit.MILLISECONDS);
/* 113 */         if (s != null && f.isDone()) {
/* 114 */           p.setSolutions((HashMap<String, MultivariatePolynomial>)s);
/*     */         }
/* 116 */         if (this.tasks.size() == 0) {
/* 117 */           taskCompleted();
/*     */         }
/* 119 */       } catch (InterruptedException|java.util.concurrent.ExecutionException|java.util.concurrent.TimeoutException e) {
/* 120 */         f.cancel(true);
/* 121 */         if (this.tasks.size() == 0)
/* 122 */           taskCompleted(); 
/*     */       } 
/*     */     } 
/* 125 */     this.executor.shutdown();
/*     */   }
/*     */   
/* 128 */   public MultivariateMinimizer(ArrayList<CircuitOptimizer.Problem> combinedProblems) { this.total = 0; this.combinedProblems = combinedProblems;
/*     */     this.tasks = new LinkedList<>();
/*     */     this.problems = new LinkedList<>();
/*     */     this.startTimes = new LinkedList<>();
/* 132 */     this.problemsToSolve = new LinkedList<>(); } public void taskCompleted() { synchronized (this) {
/* 133 */       CircuitOptimizer.Problem p = getNext();
/* 134 */       if (p == null)
/*     */         return; 
/* 136 */       this.startTimes.add(Long.valueOf(System.currentTimeMillis()));
/*     */       
/* 138 */       Future<?> f = this.executor.submit(() -> {
/*     */             ExpressionMinimizer minimizer = paramProblem.prep();
/*     */             
/*     */             Object solution = minimizer.run(1);
/*     */             
/*     */             taskCompleted();
/*     */             
/*     */             return solution;
/*     */           });
/* 147 */       this.tasks.add(f);
/* 148 */       this.problems.add(p);
/*     */     }  }
/*     */ 
/*     */ }


/* Location:              D:\xjsnark_backend.jar!\backend\optimizer\MultivariateMinimizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */