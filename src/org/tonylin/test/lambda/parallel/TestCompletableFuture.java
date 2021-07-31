package org.tonylin.test.lambda.parallel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCompletableFuture {

	private ThreadPoolExecutor pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
	
	@After
	public void teardown(){
		pool.shutdownNow();
	}
	
	public static class BlockingOperation {
		
		public Integer invoke(){
			try {
				Thread.sleep(10*1000);
				System.out.println("execute BlockingOperation");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return 100;
		}
		
	}
	
	private CompletableFuture<Integer> asyncOp;
	
	@Test
	public void testDelayConstructor() throws Exception {
		//Thread.sleep(10*1000);
		
		
		
		CompletableFuture<Void> finalCf =  CompletableFuture.supplyAsync(()->{
			System.out.println("Run: " + Thread.currentThread().getName());
			BlockingOperation op = new BlockingOperation();
			
			ExecutorService es = Executors.newSingleThreadExecutor();
			asyncOp = CompletableFuture.supplyAsync(()->op.invoke(), es)
					.whenComplete((x,t)->{
						System.out.println("op invoke: " + Thread.currentThread().getName());
					});
			es.shutdown();
			return null;
		}, pool)
		.thenCompose((x)->{
			System.out.println("thenCompose: " + Thread.currentThread().getName());
//			x.whenComplete((x2,t)->{
//				System.out.println("thenCompose whenComplete: " + Thread.currentThread().getName());
//			});
//			
			return asyncOp;
		})
//		.whenComplete((r,ex)->{
//			System.out.println("whenComplete: " + Thread.currentThread().getName());
//		});
		.thenAccept((x)->{
			System.out.println(x);
			System.out.println("thenAccept: " + Thread.currentThread().getName());
		});
				
		CompletableFuture.runAsync(()->{
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Run: " + Thread.currentThread().getName());
			
		}, pool).get();
		
		finalCf.get();
//		CompletableFuture.runAsync(()->{
//			throw new RuntimeException("fuck");
//		}).exceptionally((ex)->{
//			ex.printStackTrace();
//			return null;
//		}).whenComplete((a,c)->{
//			c.printStackTrace();
//			System.out.println("complete");
//		}).join();
	}
	
	@Test
	public void testPoolUsage2() throws Exception {
		ThreadPoolExecutor pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(2);
		
		CompletableFuture.runAsync(()->{
			System.out.println("run1: " + Thread.currentThread().getName());
			try {
				Thread.sleep(2*1000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}, pool);
		
		CompletableFuture.runAsync(()->{
			System.out.println("run2: " + Thread.currentThread().getName());
			try {
				Thread.sleep(5*1000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}, pool).whenComplete((action,t)->{
			System.out.println("run complete: " + Thread.currentThread().getName());
		});
		
		System.out.println("damn it");
		Thread.sleep(40*1000);
	}
	
	@Test
	public void testPoolUsage() throws Exception {
		ThreadPoolExecutor pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(2);
		
		CompletableFuture.runAsync(()->{
			System.out.println("run: " + Thread.currentThread().getName());
		}, pool).whenCompleteAsync((action,t)->{
			System.out.println("run complete: " + Thread.currentThread().getName());
		}, pool).get();
	}
	
}
