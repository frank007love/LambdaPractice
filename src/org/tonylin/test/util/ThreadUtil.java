package org.tonylin.test.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

public class ThreadUtil {
	private static boolean logginEnabled = true;
	
	public static void dumpCurrentThreadName(String suffix) {
		if( logginEnabled )
			System.out.println(Thread.currentThread().getName() + ": " + suffix);
	}
	
	public static void launchThreadsOfCommonPool(){
		
		List<ForkJoinTask<?>> tasks = new ArrayList<>();
		for( int i = 0 ; i < ForkJoinPool.getCommonPoolParallelism(); i++){
			ForkJoinTask<?> task = ForkJoinPool.commonPool().submit(()->{
				sleep(100);
			});	
			tasks.add(task);
		}
		tasks.forEach(task->task.join());
	}
	
	public static void sleep(long duration){
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
	}
	
	public static void enableLoggin(){
		logginEnabled = true;
	}
	
	public static void disableLoggin(){
		logginEnabled = false;
	}
}
