package com.orbious.extractor;

import java.util.Arrays;
import java.util.Vector;
import com.orbious.AllTests;
import junit.framework.TestCase;

/**
 * $Id: SentenceTest.java 14 2009-12-06 10:03:53Z app $
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class SentenceTest extends TestCase {
	
	public SentenceTest(String name) {
		super(name);
		AllTests.initLogger();
	}

	public void test_BasicSentence() {	
		String str = "sample. The cat sat.";
		char[] buf = str.toCharArray();
		Vector<String> sentence = null;
		
		try {
			 sentence = Sentence.getPreviousSentence(buf, 19);
		} catch ( SentenceException se ) {
			fail(se.getMessage());
		}
		assertEquals(4, sentence.size());
		assertEquals("The", sentence.get(0));
		assertEquals("cat", sentence.get(1));	
		assertEquals("sat", sentence.get(2));
		assertEquals(".", sentence.get(3));
	}

	public void test_IncompleteSentence() {
		String str = "the cat sat.";
		char[] buf = str.toCharArray();
		Vector<String> sentence = null;
		
		try {
			sentence = Sentence.getPreviousSentence(buf, 11);
		} catch ( SentenceException se ) {
			fail(se.getMessage());
		}
		
		assertEquals(4, sentence.size());
		assertEquals("the", sentence.get(0));
		assertEquals("cat", sentence.get(1));	
		assertEquals("sat", sentence.get(2));
		assertEquals(".", sentence.get(3));		
	}
	
	public void test_SentenceWithWordPunctuation() {
		String str = "sample. The cat sat on the time-line.";
		char[] buf = str.toCharArray();
		Vector<String> sentence = null;
		
		try {
			sentence = Sentence.getPreviousSentence(buf, 36);
		} catch ( SentenceException se ) {
			fail(se.getMessage());
		}
		
		assertEquals(7, sentence.size());
		assertEquals("The", sentence.get(0));
		assertEquals("cat", sentence.get(1));	
		assertEquals("sat", sentence.get(2));
		assertEquals("on", sentence.get(3));
		assertEquals("the", sentence.get(4));
		assertEquals("time-line", sentence.get(5));
		assertEquals(".", sentence.get(6));
	}
	
	public void test_SentenceWithPunctuation() {
		String str = "sample. The cat sat, on the time-line.";
		char[] buf = str.toCharArray();
		Vector<String> sentence = null;
		
		try {
			sentence = Sentence.getPreviousSentence(buf, 37);
		} catch ( SentenceException se ) {
			fail(se.getMessage());
		}
		
		assertEquals(8, sentence.size());
		assertEquals("The", sentence.get(0));
		assertEquals("cat", sentence.get(1));	
		assertEquals("sat", sentence.get(2));
		assertEquals(",", sentence.get(3));
		assertEquals("on", sentence.get(4));
		assertEquals("the", sentence.get(5));
		assertEquals("time-line", sentence.get(6));
		assertEquals(".", sentence.get(7));		
	}
	
	public void test_SentenceWithCustomPunctuation() {
		String str = "The're are no more _bananas_.";
		char[] buf = str.toCharArray();
		Vector<String> sentence = null;
		
		try {
			sentence = Sentence.getPreviousSentence(buf, 28);
		} catch ( SentenceException se ) {
			fail(se.getMessage());
		}
		
		assertEquals(6, sentence.size());
		assertEquals("The're", sentence.get(0));
		assertEquals("are", sentence.get(1));	
		assertEquals("no", sentence.get(2));
		assertEquals("more", sentence.get(3));
		assertEquals("_bananas_", sentence.get(4));
		assertEquals(".", sentence.get(5));
	}

	public void test_SentenceWithComplexPunctuation() {
		String str = "\"HAL,\" noted Frank, \"said that everything was going extremely well.\"";
		char[] buf = str.toCharArray();
		Vector<String> sentence = null;

		try { 
			sentence = Sentence.getPreviousSentence(buf, 67);
		} catch ( SentenceException se ) {
			fail(se.getMessage());
		}
		
		Vector<String> expected = new Vector<String>( 
				Arrays.asList("HAL" , 
						"\"", 
						",",
						"\"",
						"noted",
						"Frank",
						",",
						"said",
						"\"",
						"that",
						"everything",
						"was",
						"going",
						"extremely",
						"well",
						".",
						"\""));
		
		assertEquals(expected.size(), sentence.size());
		for ( int i = 0; i < sentence.size(); i++ ) {
			assertEquals(expected.get(i), sentence.get(i));
		}
	}

	public void test_SentenceWithNumbers() {
		String str;
		char[] buf;
		Vector<String> sentence = null;
		Vector<String> expected;
		
		str = "The test will occur at 9:00pm.";
		buf = str.toCharArray();
		try {
			sentence = Sentence.getPreviousSentence(buf, 29);
		} catch ( SentenceException se ) {
			fail(se.getMessage());
		}
		
		expected = new Vector<String>(
				Arrays.asList("The",
						"test",
						"will",
						"occur",
						"at",
						"9:00pm",
						"."));
		
		assertEquals(expected.size(), sentence.size());
		for ( int i = 0; i < sentence.size(); i++ ) {
			assertEquals(expected.get(i), sentence.get(i));
		}
	}
	
	public void test_SentenceWithNumbers2() {
		String str;
		char[] buf;
		Vector<String> sentence = null;
		Vector<String> expected;
		
		str = "The sun sets at 6:00pm, irrespective of the mood.";
		buf = str.toCharArray();
		
		try {
			sentence = Sentence.getPreviousSentence(buf, 48);
		} catch ( SentenceException se ) {
			fail(se.getMessage());
		}
		
		expected = new Vector<String>(
				Arrays.asList("The",
						"sun",
						"sets",
						"at",
						"6:00pm",
						",",
						"irrespective",
						"of",
						"the",
						"mood",
						"."));
		
		assertEquals(expected.size(), sentence.size());
		for ( int i = 0; i < sentence.size(); i++ ) {
			assertEquals(expected.get(i), sentence.get(i));
		}
	}
}
