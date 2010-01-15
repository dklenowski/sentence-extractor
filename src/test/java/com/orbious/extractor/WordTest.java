package com.orbious.extractor;

// $Id: WordTest.java 12 2009-12-05 11:40:44Z app $

import com.orbious.AllExtractorTests;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class WordTest extends TestCase {

    public WordTest(String name) {
      super(name);
      AllExtractorTests.initLogger();
    }
    
    public void test_ExceptionThrown() {
      String str = "test";
      char[] buf = str.toCharArray();

      try {
        Word.getPreviousWord(buf, -1, true);
        fail("No ArrayIndexOutOfBoundsException for pos=" + 4);
      } catch ( ArrayIndexOutOfBoundsException aioobe ) { } 
    }
    
    public void test_GetPreviousWord() {
      String str = "the cat sat on the mat";
      char[] buf = str.toCharArray();
      
      assertEquals("t", Word.getPreviousWord(buf, 0, true).word());
      assertEquals("th", Word.getPreviousWord(buf, 1, true).word());
      assertEquals("the", Word.getPreviousWord(buf, 3, true).word());
      assertEquals("cat", Word.getPreviousWord(buf, 6, true).word());
      assertEquals("cat", Word.getPreviousWord(buf, 7, true).word());
      assertEquals("mat", Word.getPreviousWord(buf, 21, true).word());
    }
    
    public void test_GetNextWord() {
      String str = "the cat sat on the mat";
      char[] buf = str.toCharArray();

      assertEquals("the", Word.getNextWord(buf, 0, true).word());
      assertEquals("cat", Word.getNextWord(buf, 4, true).word());
      assertEquals("mat", Word.getNextWord(buf, 18, true).word());
    }   
}
