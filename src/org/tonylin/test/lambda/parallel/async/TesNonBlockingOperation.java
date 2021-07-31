package org.tonylin.test.lambda.parallel.async;

import static org.junit.Assert.fail;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import static org.tonylin.test.util.ThreadUtil.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.tonylin.test.concurrent.CustomThreadFactory;

public class TesNonBlockingOperation {
	@AfterClass
	public static void classTeardown(){
		es.shutdownNow();
	}
	
	private static void launchTaskWithAuxThread(Runnable runner){
		ExecutorService auxEs = Executors.newSingleThreadExecutor(CustomThreadFactory.create("aux_blocking_thread_"));
		auxEs.execute(runner);
		auxEs.shutdown();
	}
	
	@Test
	public void testSendSync2() {
		CompletableFuture<Response> sendSync =  CompletableFuture.supplyAsync(()->{
			dumpCurrentThreadName("supplyAsync");
			
			CompletableFuture<Response> responseReceived = new CompletableFuture<>();
			launchTaskWithAuxThread(()->{
				new BlockingJob2(responseReceived).invoke();
			});
			return responseReceived;
		}, es)
		.thenCompose((CompletableFuture<Response> responseReceived)->{
			dumpCurrentThreadName("thenCompose");
			return responseReceived;
		})
		.thenApply((Response x)->{
			dumpCurrentThreadName("thenApply: " + x.getClass().getName());
			return x;
		});
		
		sendSync.join();
	}
	
	private static <T> CompletableFuture<T> launchTaskWithAuxThread(Supplier<T> supplier){
		ExecutorService auxEs = 
				Executors.newSingleThreadExecutor(CustomThreadFactory.create("aux_blocking_thread_"));
		CompletableFuture<T> task = CompletableFuture
				.supplyAsync(supplier, auxEs);
		auxEs.shutdown();
		return task;
	}
	
	private static int cpu_num = Runtime.getRuntime().availableProcessors();
	private static ExecutorService es = Executors.newFixedThreadPool(cpu_num, 
				CustomThreadFactory.create("cpu_bound_thread_"));
	@Test
	public void testSendSync1(){
		CompletableFuture<Response> sendSync = CompletableFuture.supplyAsync(()->{
			dumpCurrentThreadName("supplyAsync");
			return launchTaskWithAuxThread(()->new BlockingJob().invoke());
		}, es)
		.thenCompose((CompletableFuture<Response> responseReceived)->{
			dumpCurrentThreadName("thenCompose");
			return responseReceived;
		})
		.thenApplyAsync((Response x)->{
			dumpCurrentThreadName("thenApply");
			return x;
		}, es);
		
		sendSync.join();
	}

	public static class BlockingJob {
		public Response invoke() {
			try {
				dumpCurrentThreadName("before blocking job");
				//Thread.sleep(2*1000);
			} catch( Exception e ) {
				e.printStackTrace();
			} finally {
				dumpCurrentThreadName("after blocking job");
			}
			return new Response();
		}
	}
	
	public static class BlockingJob2 {
		private CompletableFuture<Response> responseReceived;
		public BlockingJob2(CompletableFuture<Response> responseReceived) {
			this.responseReceived = responseReceived;
		}
		public void invoke() {
			try {
				dumpCurrentThreadName("before blocking job");
				//Thread.sleep(2*1000);
				// io blocking job and get response
				responseReceived.complete(new Response());
			} catch( Exception e ) {
				// log
			} finally {
				dumpCurrentThreadName("after blocking job");
			}
		}
	}
}
