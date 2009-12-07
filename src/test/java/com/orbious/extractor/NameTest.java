package com.orbious.extractor;

import com.orbious.AllTests;
import junit.framework.TestCase;

/**
 * $Id$
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class NameTest extends TestCase {
	
	public NameTest(String name) {
		super(name);
		AllTests.initLogger();
	}
	
	public void test_isName() {
		boolean ret;
		
		ret = Name.isName("Abril");
		assertEquals(true, ret);
		
		ret = Name.isName("Taylor");
		assertEquals(true, ret);
	}

	public void test_isNotName() {
		boolean ret;
		
		ret = Name.isName("Tomato");
		assertEquals(false, ret);
		
		ret = Name.isName("mr.");
		assertEquals(false, ret);		
	}
}
