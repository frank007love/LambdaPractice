package org.tonylin.test.lambda.parallel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.junit.Test;

public class TestCommonPool {
	
	private static void dump(String x){
		try {
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName()+"_"+x);
	}
	@Test
	public void testCommonPoolArgument(){
		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
		System.out.println(ForkJoinPool.getCommonPoolParallelism());
		
		List<String> yy = Arrays.asList("y1", "y2", "y3", "y4", "y5");
		yy.parallelStream().peek(TestCommonPool::dump).forEach(TestCommonPool::dump);
	}
}
