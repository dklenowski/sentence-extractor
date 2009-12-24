package com.orbious.extractor;

import java.util.Arrays;
import java.util.Vector;
import com.orbious.AllTests;
import junit.framework.TestCase;

/**
 * $Id$
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */
public class WhitespaceRemoverTest extends TestCase {
  
  public WhitespaceRemoverTest(String name) {
    super(name);
    AllTests.initLogger();
  }
  
  public void test_ExceptionThrow() {
    Vector<String> text = new Vector<String>( Arrays.asList("test") );
    
    try {
      WhitespaceRemover.remove(text, -1);
    fail("No ArrayIndexOutOfBoundsException thrown");
    } catch ( ArrayIndexOutOfBoundsException aioobe ) { }
    
    try {
      WhitespaceRemover.remove(text, 10);
    fail("No ArrayIndexOutOfBoundsException thrown");
    } catch ( ArrayIndexOutOfBoundsException aioobe ) { }
  }
  
  public void test_RemoveWhitespaceBasic() {
    Vector<String> text = new Vector<String>(
        Arrays.asList(
            " The  style and  use varies in the english language.  \n"
        ));
    
    String str = WhitespaceRemover.remove(text, 0);
    assertEquals("The style and use varies in the english language. ", str);
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

    String str;
    for ( int i = 0; i < text.size(); i++ ) {
      str = WhitespaceRemover.remove(text, i);
      if ( expected.get(i) != null ) {
        assertEquals(expected.get(i), str);
      } else {
        assertNull(str);
      }
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

    String str;
    for ( int i = 0; i < text.size(); i++ ) {
      str = WhitespaceRemover.remove(text, i);
      if ( expected.get(i) != null ) {
        assertEquals(expected.get(i), str);
      } else {
        assertNull(str);
      }
    } 
  }
    
}
