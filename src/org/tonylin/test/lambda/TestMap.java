package org.tonylin.test.lambda;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Test;

public class TestMap {

	
	
	@Test
	public void test(){
		Map<Long, Date> map = new HashMap<>();
		
		Map<Long, String> t_map = new HashMap<>();
		for( Entry<Long, Date> entry : map.entrySet() ) {
			t_map.put(entry.getKey(), entry.getValue().toString());
		}
		
		map.entrySet().stream().map(e->new AbstractMap.SimpleImmutableEntry<>(e.getKey(), e.getValue()))
		.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
}

