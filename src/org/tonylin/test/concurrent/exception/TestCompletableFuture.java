package org.tonylin.test.concurrent.exception;

import static org.junit.Assert.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tonylin.test.concurrent.ThreadUtility;

public class TestCompletableFuture {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private void invokeWithException(){
		throw new RuntimeException("test exception");
	}
	
	@Test
	public void runWithJoin() {
		try {
			CompletableFuture
			.runAsync(this::invokeWithException).join();
			fail();
		} catch( CompletionException e ) {
			assertNotNull(e.getMessage());
		}
	}

	@Test
	public void runWithoutJoin() {
		CompletableFuture
		.runAsync(this::invokeWithException)
		.exceptionally((t)->{
			return null;
		})
		.whenComplete((r,ex)->{
			System.out.println(r);
			System.out.println(ex);
		});

		ThreadUtility.sleep(2*1000);
	}
	
	private String supplyWithException(){
		throw new RuntimeException("test exception");
	}
	
	@Test
	public void runWithoutJoin2() {
		CompletableFuture
		.supplyAsync(this::supplyWithException)
		.exceptionally((t)->{
			System.out.println("exceptionally: " + t);
			return null;
		})
		.thenAccept((x)->{
			System.out.println("thenAccept: " + x);
		});
		
		
//		.whenComplete((r,ex)->{
//			System.out.println("whenComplete: " + r);
//			System.out.println("whenComplete " + ex);
//		});

		ThreadUtility.sleep(2*1000);
	}
}
