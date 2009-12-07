package com.orbious.extractor;

import java.util.Arrays;
import java.util.Vector;

import com.orbious.AllTests;

import junit.framework.TestCase;

/**
 * $Id: CleanserTest.java 14 2009-12-06 10:03:53Z app $
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class CleanserTest extends TestCase {
	
	public CleanserTest(String name) {
		super(name);
		AllTests.initLogger();
	}

	public void test_CleanWordsBasic() {
		Vector<String> words = new Vector<String>(
				Arrays.asList("Schrodinger's ",
						"  cat *",
						"sat",
						"on",
						"the",
						"time-line",
						",",
						"who",
						"knew",
						"the",
						"cat",
						"wouldn't",
						"survive",
						"."));
	
		String sentence = Cleanser.cleanWordsAsStr(words);
		assertEquals("Schrodinger's cat sat on the time-line , who knew the cat wouldn't survive",
				sentence);
	}
	
	public void test_CleanWordsComplex() {
		Vector<String> words = new Vector<String>( 
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
		String sentence = Cleanser.cleanWordsAsStr(words);	
		assertEquals("HAL \" , \" noted Frank , said \" that everything was going extremely well \"",
				sentence);
	}
	
	public void test_CleanWordsWithNumbers() {
		Vector<String> words = new Vector<String>(
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
		String sentence = Cleanser.cleanWordsAsStr(words);
		assertEquals("The sun sets at 6:00pm , irrespective of the mood", sentence);
	}
	
	public void test_RemoveWhitespaceWithException() {
		Vector<String> text = new Vector<String>(
				Arrays.asList(
						" The  style and  use varies in the english language.  \n"
				));

		try {
			Cleanser.removeWhitespace(text, 500);
			fail("No ArrayIndexOutOfBoundsException for pos=" + 500);
		} catch ( ArrayIndexOutOfBoundsException aioobe ) { }			
	}

	public void test_RemoveWhitespaceBasic() {
		Vector<String> text = new Vector<String>(
				Arrays.asList(
						" The  style and  use varies in the english language.  \n"
				));
		String sentence = Cleanser.removeWhitespace(text, 0);
		
		assertEquals("The style and use varies in the english language. ", 
				sentence);
	}
		
	public void test_RemoveWhitespaceMultipleLines() { 
		Vector<String> text = new Vector<String>(
				Arrays.asList(
						" The  style and  use varies in the english language.  \n", 
						"\n", 
						" \n",
						"Towards the end of his life Turing became interested in chemistry.",
						"He wrote a paper on the  chemical basis of  morphogenesis."
				));

		Vector<String> expected = new Vector<String>(
				Arrays.asList(
						"The style and use varies in the english language. ", 
						null, 
						null,
						"Towards the end of his life Turing became interested in chemistry. ",
						"He wrote a paper on the chemical basis of morphogenesis. "
				));

		String sentence;
		for ( int i = 0; i < text.size(); i++ ) {
			sentence = Cleanser.removeWhitespace(text, i);
			assertEquals(expected.get(i), sentence);
		}	
	}
	
	public void test_RemoveWhitespaceWithHyphen() {
		Vector<String> text = new Vector<String>(
				Arrays.asList(
						"\n", 
						" \n",
						"In December  1940 , Turing solved the naval Enigma indicator system",
						"Turing was ranked twenty-  ",
						"first on the BBC nationwide poll of the 100 Greatest Britons."
				));		

		Vector<String> expected = new Vector<String>(
				Arrays.asList(
						null,
						null,
						"In December 1940 , Turing solved the naval Enigma indicator system ",
						"Turing was ranked twenty-",
						"first on the BBC nationwide poll of the 100 Greatest Britons. "
				));

		String sentence;
		for ( int i = 0; i < text.size(); i++ ) {
			sentence = Cleanser.removeWhitespace(text, i);
			assertEquals(expected.get(i), sentence);
		}
	}
}
