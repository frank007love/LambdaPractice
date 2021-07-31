package org.tonylin.test.lambda.exception;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Errors {
	static private Logger logger = LoggerFactory.getLogger(Errors.class); 
	
	public static <T>  Consumer<T> logException(ThrowingConsumer<T, Exception> operation){
		return i -> {
			try {
				operation.accept(i);
			} catch (Exception e) {
				logger.warn("", e);
			}
		};
	}
	
	@FunctionalInterface
	public interface ThrowingConsumer<T, E extends Exception> {
	    void accept(T t) throws E;
	}
}
