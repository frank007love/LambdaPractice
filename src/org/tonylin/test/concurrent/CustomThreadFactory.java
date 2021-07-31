package org.tonylin.test.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private String threadNamePrefix;

	private CustomThreadFactory() {

	}

	@Override
	public Thread newThread(Runnable runnable) {
		Thread t = new Thread(runnable, threadNamePrefix + threadNumber.getAndIncrement());
		t.setUncaughtExceptionHandler(DefaultUncaughtExceptionHandler.get());
		return t;
	}

	public static CustomThreadFactory create(String threadNamePrefix) {
		CustomThreadFactory factory = new CustomThreadFactory();
		factory.threadNamePrefix = threadNamePrefix;
		return factory;
	}
}
