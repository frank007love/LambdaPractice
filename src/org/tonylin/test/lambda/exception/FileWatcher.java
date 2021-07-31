package org.tonylin.test.lambda.exception;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.helpers.FileWatchdog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diffplug.common.base.Errors.Rethrowing;

public class FileWatcher extends FileWatchdog {

	private List<IFileChangedListener> listeners = new ArrayList<>();
	static private Logger logger = LoggerFactory.getLogger(FileWatcher.class); 
	
	public FileWatcher(String filename) {
		super(filename);
	}
	
	@Override
	protected void doOnChange() {
		try {
			if( listeners == null || listeners.isEmpty() )
				return;
			
			
			
			String newContent = new String(Files.readAllBytes(Paths.get(super.filename)));
			listeners.forEach(Errors.logException(i->i.update2(newContent)));
		} catch( IOException e ){
			throw new RuntimeException(e);
		}
	}
	
	public void addListener(IFileChangedListener listener){
		listeners.add(listener);
	}
}

