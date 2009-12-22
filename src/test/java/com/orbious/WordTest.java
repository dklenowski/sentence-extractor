package com.orbious.extractor;

import com.orbious.AllTests;
import junit.framework.TestCase;

/**
 * $Id: WordTest.java 12 2009-12-05 11:40:44Z app $
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class WordTest extends TestCase {

    public WordTest(String name) {
      super(name);
      AllTests.initLogger();
    }
    
    public void test_ExceptionThrown() {
      String str = "test";
      char[] buf = str.toCharArray();

      try {
        Word.getPreviousWord(buf, -1);
        fail("No ArrayIndexOutOfBoundsException for pos=" + 4);
      } catch ( ArrayIndexOutOfBoundsException aioobe ) { } 
    }
    
    public void test_GetWord() {
      String str = "the cat sat on the mat";
      char[] buf = str.toCharArray();
      
      assertEquals("t", Word.getPreviousWord(buf, 0));
      assertEquals("th", Word.getPreviousWord(buf, 1));
      assertEquals("the", Word.getPreviousWord(buf, 3));
      assertEquals("cat", Word.getPreviousWord(buf, 6));
      assertEquals("cat", Word.getPreviousWord(buf, 7));
      assertEquals("mat", Word.getPreviousWord(buf, 21));
    }
    
    public void test_GetWords() {
      String[] wds;
      String str = "the cat sat on the mat";
      char[] buf = str.toCharArray();
      
      wds = Word.getPreviousWords(buf, 3, 1);
      assertEquals(1, wds.length);
      assertEquals("the", wds[0]);
      
      wds = Word.getPreviousWords(buf, 10, 4);
      assertEquals(3, wds.length);
      assertEquals("the", wds[0]);
      assertEquals("cat", wds[1]);
      assertEquals("sat", wds[2]);

      wds = Word.getPreviousWords(buf, 13, 4);
      assertEquals(4, wds.length);
      assertEquals("the", wds[0]);
      assertEquals("cat", wds[1]);
      assertEquals("sat", wds[2]);
      assertEquals("on", wds[3]); 

      wds = Word.getPreviousWords(buf, 13, 3);
      assertEquals(3, wds.length);
      assertEquals("cat", wds[0]);
      assertEquals("sat", wds[1]);
      assertEquals("on", wds[2]); 
      
      
    }
}
