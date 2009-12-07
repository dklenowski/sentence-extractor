package com.orbious.extractor;

import com.orbious.AllTests;

import junit.framework.TestCase;

/**
 * $Id: SuspensionTest.java 12 2009-12-05 11:40:44Z app $
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class SuspensionTest extends TestCase {
	
	public SuspensionTest(String name) {
		super(name);
		AllTests.initLogger();
	}
	
	public void test_isSuspension() {
		boolean ret;
			
		ret = Suspension.isSuspension("Mr.");
		assertEquals(true, ret);
	}

	public void test_notSuspension() {
		boolean ret;

		ret = Suspension.isSuspension("Tomato");
		assertEquals(false, ret);
		
		ret = Suspension.isSuspension("mr.");
		assertEquals(false, ret);		
	}
}
