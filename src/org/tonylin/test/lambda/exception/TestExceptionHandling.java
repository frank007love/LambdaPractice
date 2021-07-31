package org.tonylin.test.lambda.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.diffplug.common.base.Errors;

public class TestExceptionHandling {

	
	
	
	private static void handle(String str){
		System.out.println(str);
	}
	public interface ExceptionWrapper2<T> extends Consumer<T>{
		default Consumer<T> wrapper(Consumer<T> job){
			return null;
		}
	}
	
	@FunctionalInterface
	public interface ExceptionWrapper<T> extends Consumer<T> {

		 public static <T> Consumer<T> interruptableJob(Consumer<T> job){
			return input -> {
				try {
					job.accept(input);
				} catch (Exception e) {
					if( Thread.currentThread().isInterrupted() ) {
						throw e;
					}
					e.printStackTrace();
				}
			};
		}
		
//		 public static <T> InterruptedJob<T> accept(InterruptedJob<T> job){
//			return input -> {
//				try {
//					job.accept(input);
//				} catch (Exception e) {
//					if( Thread.currentThread().isInterrupted() ) {
//						throw e;
//					}
//					e.printStackTrace();
//				}
//			};
//		}
	}
	
	public static <T> Consumer<T> lambdaWrapper(Consumer<T> consumer) {
		return input -> {
			try {
				consumer.accept(input);
			} catch (Exception e) {
				if( Thread.currentThread().isInterrupted() ) {
					throw e;
				}
				e.printStackTrace();
			}
		};
	}
	
	
	
	public static void main(String[] args) {
		List<String> strList = Arrays.asList("1","2","3","4","5");
		strList.parallelStream().forEach(lambdaWrapper(TestExceptionHandling::handle));
		
		//ExceptionWrapper2 s = ExceptionWrapper.interruptableJob(TestExceptionHandling::handle);
		
		ForkJoinPool pool = new ForkJoinPool();
		pool.execute(()->strList.parallelStream().forEach(ExceptionWrapper.interruptableJob(TestExceptionHandling::handle)));

		
		
		Errors.createHandling(ex->{
			if( Thread.currentThread().isInterrupted() ) {
				throw Errors.asRuntime(ex);
			}
			Errors.log().accept(ex);
		});
	}

}
