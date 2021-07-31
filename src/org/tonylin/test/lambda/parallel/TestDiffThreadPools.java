package org.tonylin.test.lambda.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public class TestDiffThreadPools {
/*
 4 core, 4 thread, 100 rounds
testCustomThreadPool: 6504
testCachedThreadPool: 8375
testNewFixedThreadPool: 6571
testNewScheduledThreadPool: 6559
testForkJoinPool: 6573
testLinkedTransferQueue: 12401
testExecutorScalingQueue: 6607

4 core, 2 thread, 10 rounds
testCustomThreadPool: 7871
testCachedThreadPool: 9336
testNewFixedThreadPool: 7766
testNewScheduledThreadPool: 7748
testForkJoinPool: 7872
testLinkedTransferQueue: 12248
testExecutorScalingQueue: 7764

4 core, 8 thread, 1 rounds
testCustomThreadPool: 8469
testCachedThreadPool: 11113
testNewFixedThreadPool: 7701
testNewScheduledThreadPool: 7343
testForkJoinPool: 7586
testLinkedTransferQueue: 12666
testExecutorScalingQueue: 7490

*/

	
	@Test
	public void testCachedThreadPool() throws Exception{
		long sum=0;
		for( int i = 0 ; i < round ; i++ ){
	
		ThreadPoolExecutor es = (ThreadPoolExecutor)Executors.newCachedThreadPool();
		
		sum+=measure(es);
		
		es.shutdownNow();
		}
		System.out.println("testCachedThreadPool: " + (sum/round));
	}
	
	@Test
	public void testNewScheduledThreadPool() throws Exception{
		long sum=0;
		for( int i = 0 ; i < round ; i++ ){
		
		ThreadPoolExecutor es = (ThreadPoolExecutor)Executors.newScheduledThreadPool(cpu);
		
		sum+=measure(es);
		
		es.shutdownNow();
		}
		System.out.println("testNewScheduledThreadPool: " + (sum/round));
	}
	
	@Test
	public void testNewFixedThreadPool() throws Exception{
		long sum=0;
		for( int i = 0 ; i < round ; i++ ){
		ThreadPoolExecutor es = (ThreadPoolExecutor)Executors.newFixedThreadPool(cpu);
		
		sum+=measure(es);
		
		es.shutdownNow();
		}
		System.out.println("testNewFixedThreadPool: " + (sum/round));
	}
	
	@Test
	public void testForkJoinPool() throws Exception{
		long sum=0;
		for( int i = 0 ; i < round ; i++ ){
		ForkJoinPool es = new ForkJoinPool(cpu);
		
		sum+=measure(es);
		
		es.shutdownNow();
		}
		System.out.println("testForkJoinPool: " + (sum/round));
	}
	

    static class ExecutorScalingQueue<E> extends LinkedTransferQueue<E> {

        ThreadPoolExecutor executor;

        ExecutorScalingQueue() {
        }

        @Override
        public boolean offer(E e) {
            // first try to transfer to a waiting worker thread
            if (!tryTransfer(e)) {
                // check if there might be spare capacity in the thread
                // pool executor
                int left = executor.getMaximumPoolSize() - executor.getCorePoolSize();
                if (left > 0) {
                    // reject queuing the task to force the thread pool
                    // executor to add a worker if it can; combined
                    // with ForceQueuePolicy, this causes the thread
                    // pool to always scale up to max pool size and we
                    // only queue when there is no spare capacity
                    return false;
                } else {
                    return super.offer(e);
                }
            } else {
                return true;
            }
        }

    }
    //private int cpu = Runtime.getRuntime().availableProcessors();
    private int cpu = 8;
    private int round = 1;
    
	@Test
	public void testExecutorScalingQueue() throws Exception {

		long sum = 0;
		for (int i = 0; i < round; i++) {
			ExecutorScalingQueue<Runnable> workQueue = new ExecutorScalingQueue<>();
			
			ThreadPoolExecutor es = new ThreadPoolExecutor(0, cpu, 60, TimeUnit.SECONDS, workQueue);
			workQueue.executor = es;
			es.setRejectedExecutionHandler((r, e) -> {
				try {
					//System.out.println("gy");
					e.getQueue().put(r);
				} catch (InterruptedException e1) {
					throw new IllegalStateException(e1);
				}
			});
			sum += measure(es);
			// System.out.println("testLinkedTransferQueue: " + measure(es));

			es.shutdownNow();
		}
		System.out.println("testExecutorScalingQueue: " + (sum / round));
	}
    
	@Test
	public void testLinkedTransferQueue() throws Exception {

		long sum = 0;
		for (int i = 0; i < round; i++) {
			BlockingQueue<Runnable> workQueue = new LinkedTransferQueue<>();
			
			ThreadPoolExecutor es = new ThreadPoolExecutor(0, cpu, 60, TimeUnit.SECONDS, workQueue);
			es.setRejectedExecutionHandler((r, e) -> {
				try {
					//System.out.println("gy");
					e.getQueue().put(r);
				} catch (InterruptedException e1) {
					throw new IllegalStateException(e1);
				}
			});
			sum += measure(es);
			// System.out.println("testLinkedTransferQueue: " + measure(es));

			es.shutdownNow();
		}
		System.out.println("testLinkedTransferQueue: " + (sum / round));
	}


	@Test
	public void testCustomThreadPool() throws Exception {
		long sum=0;
		for( int i = 0 ; i < round ; i++ ){
		
		BlockingQueue<Runnable> workQueue = new SynchronousQueue<>();
		ThreadPoolExecutor es = new ThreadPoolExecutor(0, cpu, 60, TimeUnit.SECONDS, workQueue);
		
		es.setRejectedExecutionHandler((r, e)->{
			try {
				e.getQueue().put(r);
			} catch (InterruptedException e1) {
				throw new IllegalStateException(e1);
			}
		});
		sum+=measure(es);
		//System.out.println("testCustomThreadPool: " + measure(es));
		
		es.shutdownNow();
		}
		System.out.println("testCustomThreadPool: " + (sum/round));
	}
	
	private long  measure(ExecutorService es ) throws Exception{
		int times = 1000;
		CountDownLatch latch = new CountDownLatch(times);
	
	long before = System.currentTimeMillis();
	//ForkJoinPool es = new ForkJoinPool(10);
	for( int j = 0 ; j < times ; j++ ) {
		es.submit(()->{
			
			List<String> data = new ArrayList<>();
			for( int i = 0 ; i < 100000; i++ ){
				data.add(i+"");
			}
			data.stream().map(a->Integer.valueOf(a)).collect(Collectors.toList());
			latch.countDown();
		});
		//Thread.sleep(1);
//		System.out.println("Run " + j);
//		CompletableFuture<?> c=  CompletableFuture.runAsync(()->{
//			System.out.println("Execute " + Thread.currentThread().getName());
//			int sum = 0;
//			for( int i = 0 ; i < 1000; i++ ){
//				sum = sum + i;
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}, es);
//	
//		c.get();
	}
	latch.await();
	return System.currentTimeMillis()-before;
	}
	
}
