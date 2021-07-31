package org.tonylin.test.concurrent.exception;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestParallelStream {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected=RuntimeException.class)
	public void test() {
		List<String> strs = Arrays.asList("1", "2", "3", "4", "5");
		
		strs.parallelStream().forEach(x->{
			throw new RuntimeException();
		});
	}

}
