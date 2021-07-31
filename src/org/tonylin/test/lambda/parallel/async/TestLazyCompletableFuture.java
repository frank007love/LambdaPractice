package org.tonylin.test.lambda.parallel.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tonylin.test.util.ThreadUtil;

public class TestLazyCompletableFuture {

	private int jobNum = 100;
	private long delayTime = 50;
	private List<CompletableFuture<Void>> jobs = new ArrayList<>();
	private AtomicInteger count = new AtomicInteger(0);
	
	@BeforeClass
	public static void classSetup(){
		ThreadUtil.disableLoggin();
		ThreadUtil.launchThreadsOfCommonPool();
	}
	
	@AfterClass
	public static void classTeardown(){
		ThreadUtil.enableLoggin();
	}
	
	private void countIfMainThread(){
		if(Thread.currentThread().getName().equals("main")){
			count.incrementAndGet();
		}
	}
	
	@Test
	public void launchImmediately(){
		long before = System.currentTimeMillis();
		for( int i = 0 ; i < jobNum ; i++ ) {
			CompletableFuture<Void> job = CompletableFuture.runAsync(()->{
				ThreadUtil.dumpCurrentThreadName("runAsync");
			}).whenComplete((ret,ex)->{
				countIfMainThread();
				ThreadUtil.dumpCurrentThreadName("whenComplete");
				ThreadUtil.sleep(delayTime);
			});		
			jobs.add(job);
		}
		dumpDuration("launchImmediately", before);
	}
	
	@Test
	public void launchLazily(){
		long before = System.currentTimeMillis();
		for( int i = 0 ; i < jobNum ; i++ ) {
			CompletableFuture<Void> job = new CompletableFuture<>(); 
			job.whenComplete((ret,ex)->{
				countIfMainThread();
				ThreadUtil.dumpCurrentThreadName("whenComplete");
				ThreadUtil.sleep(delayTime);
			});		
			
			jobs.add(CompletableFuture.runAsync(()->{
				ThreadUtil.dumpCurrentThreadName("runAsync");
				job.complete(null);
			}));
		}
		dumpDuration("launchLazily", before);
	}
	
//	@Test
//	public void launchLazilyJDK9() {
//		long before = System.currentTimeMillis();
//		for( int i = 0 ; i < jobNum ; i++ ) {
//			CompletableFuture<Void> job = new CompletableFuture<>(); 
//			job.whenComplete((ret,ex)->{
//				countIfMainThread();
//				ThreadUtil.dumpCurrentThreadName("whenComplete");
//				ThreadUtil.sleep(delayTime);
//			});		
//			
//			job.completeAsync(()->{
//				ThreadUtil.dumpCurrentThreadName("runAsync");
//				return null;
//			});
//			
//			jobs.add(job);
//		}
//		dumpDuration("launchLazilyJDK9", before);
//	}
	
	private void dumpDuration(String testCase, long before){
		long duration_submit = System.currentTimeMillis() - before;
		System.out.println(testCase+"-submit: " + duration_submit);
		
		CompletableFuture.allOf(jobs.toArray(new CompletableFuture[0])).join();
		long duration = System.currentTimeMillis() - before;
		System.out.println(testCase+"-done: " + duration);
		System.out.println("count for context swtching of submit operation: " + count);
	}
	
}
