package org.tonylin.test.lambda.exception;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.diffplug.common.base.Errors;

import static org.mockito.Mockito.*;

public class FileWatcherTest {

	private String testFilePath = "./testdata/testfile.txt";
	
	@Before
	public void setup() throws Exception{
		Files.deleteIfExists(Paths.get(testFilePath));
		Files.createFile(Paths.get(testFilePath));
	}
	
	@After
	public void teardown() throws Exception {
		Files.delete(Paths.get(testFilePath));
	}
	
	@Test
	public void testNotificationWithException() throws Exception {
		String newContent = "test";
		int delay = 1000;
		
		FileWatcher watcher = new FileWatcher(testFilePath);
		watcher.setDelay(delay);
		
		IFileChangedListener listener1 = mock(IFileChangedListener.class);
		doThrow(new RuntimeException("test")).when(listener1).update(newContent);
		IFileChangedListener listener2 = mock(IFileChangedListener.class);
		doNothing().when(listener2).update(newContent);
		
		watcher.addListener(listener1);
		watcher.addListener(listener2);
		
		watcher.start();
		
		Files.write(Paths.get(testFilePath), newContent.getBytes());
		
		verify(listener1, timeout(delay*2).times(1)).update(newContent);
		verify(listener2, timeout(delay*2).times(1)).update(newContent);
	}
	
	@Test
	public void testNotification() throws Exception {
		String newContent = "test";
		int delay = 1000;
		
		FileWatcher watcher = new FileWatcher(testFilePath);
		watcher.setDelay(delay);

		IFileChangedListener listener1 = mock(IFileChangedListener.class);
		doNothing().when(listener1).update(newContent);
		IFileChangedListener listener2 = mock(IFileChangedListener.class);
		doNothing().when(listener2).update(newContent);
		
		watcher.addListener(listener1);
		watcher.addListener(listener2);
		
		watcher.start();
		
		Files.write(Paths.get(testFilePath), newContent.getBytes());
		
		verify(listener1, timeout(delay*2).times(1)).update(newContent);
		verify(listener2, timeout(delay*2).times(1)).update(newContent);
	}

}
