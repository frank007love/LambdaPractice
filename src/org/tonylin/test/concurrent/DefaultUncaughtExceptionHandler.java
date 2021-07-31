package org.tonylin.test.concurrent;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {
	final private static Logger logger = LoggerFactory.getLogger(DefaultUncaughtExceptionHandler.class); 
	final private static UncaughtExceptionHandler instance = new DefaultUncaughtExceptionHandler();
	private DefaultUncaughtExceptionHandler(){
		
	}
	
	public static UncaughtExceptionHandler get(){
		return instance;
	}
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		logger.warn("Thread {} occur exception", t.getName(), e);
	}

}
