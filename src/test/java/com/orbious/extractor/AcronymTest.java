package com.orbious.extractor;

import com.orbious.AllTests;
import junit.framework.TestCase;

/**
 * $Id: AcronymTest.java 12 2009-12-05 11:40:44Z app $
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class AcronymTest extends TestCase {

	public AcronymTest(String name) { 
		super(name);
		AllTests.initLogger();
	}
	
	public void test_ExceptionThrown() {
		String str = "test";
		char[] buf = str.toCharArray();

		try {
			Acronym.isAcronym(buf, 4);
			fail("No ArrayIndexOutOfBoundsException for pos=" + 4);
		} catch ( ArrayIndexOutOfBoundsException ioobe ) { 
			
		} catch ( AcronymException ae ) { }
		
		try {
			Acronym.isAcronym(buf, -1);
			fail("No ArrayIndexOutOfBoundsException for pos=" + 4);
		} catch ( ArrayIndexOutOfBoundsException ioobe ) { 
			
		} catch ( AcronymException ae ) { }
			
	}
	
	public void test_WrongPos() {
		String str = "test";
		char[] buf = str.toCharArray();
		
		try {
			assertFalse(Acronym.isAcronym(buf, 1));
			fail("No AcronymException for pos=" + 1);
		} catch ( AcronymException ae ) { }
	}
	
	public void test_AcronymWithSeparatedStop() {
		String str = "E.M.C. .";
		char[] buf = str.toCharArray();

		try {
			assertTrue(Acronym.isAcronym(buf, 1));
			assertTrue(Acronym.isAcronym(buf, 3));
			assertTrue(Acronym.isAcronym(buf, 5));
			assertFalse(Acronym.isAcronym(buf, 7));	
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
	}
	
	public void test_AcronymWithSeparatedStopAsWord() {
		String str = "E.M.C. .";
		try {
			assertTrue(Acronym.isAcronym(str));
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
	}
	
	public void test_AcronymWithStop() {
		String str = "I.B.M..";
		char[] buf = str.toCharArray();

		try {
			assertTrue(Acronym.isAcronym(buf, 1));
			assertTrue(Acronym.isAcronym(buf, 3));
			assertTrue(Acronym.isAcronym(buf, 5));
			assertFalse(Acronym.isAcronym(buf, 6));	
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
	}
	
	public void test_AcronymWithStopAsWord() {
		String str = "I.B.M..";		
		try {
			assertTrue(Acronym.isAcronym(str));
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
	}
	
	public void test_AcronymAsLowercase() {
		String str = "e.g. ";
		char[] buf = str.toCharArray();

		try {
			assertTrue(Acronym.isAcronym(buf, 1));
			assertTrue(Acronym.isAcronym(buf, 3));	
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
	}
	
	public void test_AcronymAsLowercaseAsWord() {
		String str = "e.g. ";
		try {
			assertTrue(Acronym.isAcronym(str));		
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
	}

	public void test_NotAcronym() {
		String str;
		char[] buf;

		str = "\",As";
		buf = str.toCharArray();
		
		try {
			assertFalse(Acronym.isAcronym(buf, 0));
			assertFalse(Acronym.isAcronym(buf, 1));
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
	
		str = "they?\"";
		buf = str.toCharArray();

		try {
			assertFalse(Acronym.isAcronym(buf, 4));
			assertFalse(Acronym.isAcronym(buf, 5));
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
	
		str = "lecturin\',\"";
		buf = str.toCharArray();

		try {
			assertFalse(Acronym.isAcronym(buf, 8));
			assertFalse(Acronym.isAcronym(buf, 9));		
			assertFalse(Acronym.isAcronym(buf, 10));
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
			
		str = "Titan...";
		buf = str.toCharArray();

		try {
			assertFalse(Acronym.isAcronym(buf, 5));
			assertFalse(Acronym.isAcronym(buf, 6));		
			assertFalse(Acronym.isAcronym(buf, 7));	
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
		
		str = "\"Oh,";
		buf = str.toCharArray();

		try {
			assertFalse(Acronym.isAcronym(buf, 0));
			assertFalse(Acronym.isAcronym(buf, 3));
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
	}
	
	public void test_NotAcronymAsWord() {
		String str;

		str = "\",As";
		try {
			assertFalse(Acronym.isAcronym(str));
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
		
		str = "they?\"";
		try {
			assertFalse(Acronym.isAcronym(str));
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
		
		str = "lecturin\',\"";
		try {
			assertFalse(Acronym.isAcronym(str));
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
		
		str = "Titan...";
		try {
			assertFalse(Acronym.isAcronym(str));
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
		
		str = "\"Oh,";
		try {
			assertFalse(Acronym.isAcronym(str));		
		} catch ( AcronymException ae ) {
			fail(ae.getMessage());
		}
	}
}
