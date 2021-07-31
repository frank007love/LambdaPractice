package org.tonylin.test.concurrent.exception;

import static org.junit.Assert.*;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tonylin.test.concurrent.CustomForkJoinWorkerThreadFactory;
import org.tonylin.test.concurrent.DefaultUncaughtExceptionHandler;
import org.tonylin.test.concurrent.ThreadUtility;

public class TestExecutorService {
	private ExecutorService testExecutorService;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		if( testExecutorService != null )
			testExecutorService.shutdownNow();
	}

	/**
	 * Console Exception in thread "ForkJoinPool-1-worker-1" java.lang.RuntimeException: fuck
	 */
	@Test
	public void testForkJoinPoolWithExecute() {
		testExecutorService = new ForkJoinPool();
		testExecutorService.execute(()->{
			throw new RuntimeException("fuck");
		});
		
		ThreadUtility.sleep(3*100);
	}

	@Test
	public void testCustomForkJoinPoolWithExecute() {
		testExecutorService = new ForkJoinPool(2, 
				CustomForkJoinWorkerThreadFactory.create("test"), 
				DefaultUncaughtExceptionHandler.get(), false);
		
		
		testExecutorService.execute(()->{
			throw new RuntimeException("fuck");
		});
		
		ThreadUtility.sleep(3*100);
	}
	
	/**
	 * Depend on get method:
	 * java.util.concurrent.ExecutionException: java.lang.RuntimeException: java.lang.RuntimeException: fuck
	 * at java.util.concurrent.ForkJoinTask.get(ForkJoinTask.java:1006)
	 */
	@Test
	public void testForkJoinPoolWithSubmitAndGet(){
		testExecutorService = new ForkJoinPool();
		Future<Void> task = testExecutorService.submit(()->{
			throw new RuntimeException("fuck");
		});
		
		try {
			task.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Depend on join method:
	 * java.lang.RuntimeException: java.lang.RuntimeException: fuck
	 * at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	 * at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	 * at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	 * at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
	 * aat java.util.concurrent.ForkJoinTask.getThrowableException(ForkJoinTask.java:593)
	 * at java.util.concurrent.ForkJoinTask.reportException(ForkJoinTask.java:677)
	 * at java.util.concurrent.ForkJoinTask.join(ForkJoinTask.java:720)
	 */
	@Test
	public void testForkJoinPoolWithSubmitAndJoin(){
		List<String> list = Arrays.asList("a", "b", "c", "d", "e");
		testExecutorService = new ForkJoinPool();
		ForkJoinTask<Void> task = ((ForkJoinPool)testExecutorService).submit(()->{
			list.parallelStream().forEach(x->{
				throw new RuntimeException("fuck");	
			});
			return null;
		});
		
		try {
			task.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
