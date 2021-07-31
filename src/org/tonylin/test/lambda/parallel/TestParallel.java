package org.tonylin.test.lambda.parallel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestParallel {

	private static ForkJoinPool pool = new ForkJoinPool(50);
	
	private static List<String> createTokens(){
		int testLen = 50;
		List<String> tokens = new ArrayList<>();
		for( int i = 0 ; i < testLen ; i++ ) {
			tokens.add(""+i);
		}
		return tokens;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(Runtime.getRuntime().availableProcessors());
		System.out.println(ForkJoinPool.getCommonPoolParallelism());
		List<String> tokens1 = createTokens();
		List<String> tokens2 = createTokens();
		
		ForkJoinTask<?> tasks1 =  pool.submit(()->{
			return tokens1.parallelStream().map(str->{
				System.out.println("map:" + str);
				try {
					Thread.sleep(10*1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}).collect(Collectors.toList());
		});
		
		Field f = Class.forName("java.util.stream.AbstractTask").getDeclaredField("LEAF_TARGET");
		f.setAccessible(true);
		System.out.println(f.getInt(null));
		
//		ForkJoinTask<?> tasks2 =  pool.submit(()->{
//			return tokens2.parallelStream().map(str->{
//				System.out.println("map:" + str);
//				try {
//					Thread.sleep(10*1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				return Integer.valueOf(str);
//			}).collect(Collectors.toList());
//		});
		
		tasks1.join();
	//	tasks2.join();
	}

}
