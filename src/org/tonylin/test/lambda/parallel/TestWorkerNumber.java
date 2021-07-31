package org.tonylin.test.lambda.parallel;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Supplier;

import org.junit.Test;

public class TestWorkerNumber {
	
	public static void sleep(int s){
		try {
			Thread.sleep(s);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static ForkJoinPool pool = new ForkJoinPool(2);
	
//	public static void main(String[] args) {
//		
//		List<String> yy = Arrays.asList("y1", "y2", "y3", "y4", "y5");
//		ForkJoinTask<?> task = pool.submit(()->
//		yy.parallelStream().forEach(x->{
//			
//			sleep(100*1000);
//		}));
//		 
//		System.out.println(task.getClass().getName()+", " + task.getQueuedTaskCount());
//				task.join();
//	}
	

	
	@Test
	public void test() {
		
		List<String> yy = Arrays.asList("y1", "y2", "y3", "y4", "y5");
		List<String> gg = Arrays.asList("g1", "g2", "g3", "g4", "g5");
		
		CompletableFuture.runAsync(()->BlockingTasks.callInManagedBlock(()->{
			System.out.println(Thread.currentThread().getName()+", blocking: 1");
			sleep(25000);
			System.out.println(Thread.currentThread().getName()+", blocking end:1" );
			return null;
		}), pool);
		sleep(1500);
		CompletableFuture.runAsync(()->BlockingTasks.callInManagedBlock(()->{
			System.out.println(Thread.currentThread().getName()+", blocking: 2");
			sleep(25000);
			System.out.println(Thread.currentThread().getName()+", blocking end: 2" );
			return null;
		}), pool);
		sleep(1500);
		CompletableFuture.runAsync(()->BlockingTasks.callInManagedBlock(()->{
			System.out.println(Thread.currentThread().getName()+", blocking: 3");
			sleep(25000);
			System.out.println(Thread.currentThread().getName()+", blocking end: 3" );
			return null;
		}), pool);
		
		sleep(1500);
		CompletableFuture.runAsync(()->BlockingTasks.callInManagedBlock(()->{
			System.out.println(Thread.currentThread().getName()+", blocking: 4");
			sleep(25000);
			System.out.println(Thread.currentThread().getName()+", blocking end: 4" );
			return null;
		}), pool);
		sleep(1500);
		CompletableFuture.runAsync(()->BlockingTasks.callInManagedBlock(()->{
			System.out.println(Thread.currentThread().getName()+", blocking: 5");
			sleep(25000);
			System.out.println(Thread.currentThread().getName()+", blocking end: 5" );
			return null;
		}), pool);
		
		sleep(1500);
		CompletableFuture.runAsync(()->BlockingTasks.callInManagedBlock(()->{
			System.out.println(Thread.currentThread().getName()+", blocking: 6");
			sleep(25000);
			System.out.println(Thread.currentThread().getName()+", blocking end: 6" );
			return null;
		}), pool);
		
//		pool.submit(()->{
//			System.out.println("start: " + Thread.currentThread().getName());
//			gg.stream().parallel().peek(x->System.out.println(Thread.currentThread().getName()+", " + x)).map(TestWorkerNumber::map).forEach(x->{
//				System.out.println(Thread.currentThread().getName()+", " + x);
//			});
//		}).join();
		
//		ForkJoinTask<?> task =	pool.submit(()->{
//			System.out.println("start1: " + Thread.currentThread().getName());
//			yy.stream().parallel().peek(x->System.out.println(Thread.currentThread().getName()+", " + x)).map(x->BlockingTasks.callInManagedBlock(()->{
//				System.out.println(Thread.currentThread().getName()+", blocking: " + x);
//				sleep(15000);
//				System.out.println(Thread.currentThread().getName()+", blocking end: " + x);
//				return x+"-";
//			}))
//			.peek(x->System.out.println(Thread.currentThread().getName()+", " + x))
//			.forEach(x->{
//				System.out.println(Thread.currentThread().getName()+", " + x);
//			});
//		});
		
		sleep(1000);
		
		pool.submit(()->{
			System.out.println("start: " + Thread.currentThread().getName());
			gg.stream().parallel().peek(x->System.out.println(Thread.currentThread().getName()+", " + x)).map(x->x+"-")
			.peek(x->System.out.println(Thread.currentThread().getName()+", " + x))
			.forEach(x->{
				sleep(1000);
				System.out.println(Thread.currentThread().getName()+", " + x);
		});
	});
	sleep(300*1000);
	}

	private static int map(String s){
		byte[] data = s.getBytes();
		
		return Arrays.asList(data[0], data[1]).stream().parallel().map(x->(int)x).filter(x->{
			System.out.println(Thread.currentThread().getName()+", check > 20, " + s);
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				return x>20;
			}).findFirst().get();
	}
}
