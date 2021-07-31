package org.tonylin.test.lambda.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IFileChangedListener {
		static Logger logger = LoggerFactory.getLogger(IFileChangedListener.class);
		public void update(String newContent);
		public void update2(String newContent) throws Exception;
		
		default public void updateEx(String newContent){
			try {
				update(newContent);
			} catch( Exception e ) {
				logger.warn("", e);
			}
		}
}
