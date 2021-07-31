package org.tonylin.test.lambda.parallel.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.tonylin.test.util.ThreadUtil.*;
import org.junit.Test;

public class TestCompletableFuture {

	public Response doSmth(){
		return doSmthAsync().join();
	}
	
	public CompletableFuture<Response> doSmthAsync(){
		return CompletableFuture.supplyAsync(()->{
			return new Response();
		});
	}
	
	@Test
	public void fixTransitionTask(){
		doSmthAsync()
		.thenAccept(response->{
			System.out.println(response);
		});
	}
	
	@Test
	public void transitionTask(){
		Response reponse = doSmth();
		System.out.println(reponse);
	}
	
	private ExecutorService es = Executors.newFixedThreadPool(2);
	
	@Test
	public void transitionTaskOfCallback(){
		CompletableFuture.supplyAsync(()->{
			dumpCurrentThreadName("supplyAsync");
			return new Response();
		}, es)
		.thenAcceptAsync(response->{
			dumpCurrentThreadName("thenAcceptAsync");
			System.out.println(response);
		}, es);
	}
	
	@Test
	public void fixTransitionTaskOfCallback(){
		CompletableFuture.supplyAsync(()->{
			dumpCurrentThreadName("supplyAsync");
			return new Response();
		}, es)
		.thenAccept(response->{
			dumpCurrentThreadName("thenAccept");
			System.out.println(response);
		});
	}
}
