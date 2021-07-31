package org.tonylin.test.concurrent;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.ForkJoinWorkerThread;

public class CustomForkJoinWorkerThreadFactory implements ForkJoinWorkerThreadFactory {

	private String threadNamePrefix;

	private CustomForkJoinWorkerThreadFactory() {

	}

	
	@Override
	public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
		final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
		worker.setName(threadNamePrefix + "_FJ_" + worker.getPoolIndex());
		worker.setUncaughtExceptionHandler(pool.getUncaughtExceptionHandler());
		return worker;
	}

	public static ForkJoinWorkerThreadFactory create(String threadNamePrefix) {
		CustomForkJoinWorkerThreadFactory factory = new CustomForkJoinWorkerThreadFactory();
		factory.threadNamePrefix = threadNamePrefix;
		return factory;
	}
}
