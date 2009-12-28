package com.orbious.extractor;

// $Id: SentenceTest.java 14 2009-12-06 10:03:53Z app $

import com.orbious.AllTests;
import com.orbious.extractor.Sentence.StartOp;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class SentenceTest extends TestCase {
  
  public SentenceTest(String name) {
    super(name);
    AllTests.initLogger();
  }

  //
  // isEnd
  //

  public void test_IsEndBasic() {
    String str;
    char[] buf;

    str = "fantastic. The";
    buf = str.toCharArray();
    
    assertEquals(true, Sentence.isEnd(buf, 9).isEnd());
  }

  public void test_isEndWithPunct() {
    String str;
    char[] buf;

    str = "hope\". Too";
    buf = str.toCharArray();
 
    assertEquals(false, Sentence.isEnd(buf, 4).isEnd());    
    assertEquals(true, Sentence.isEnd(buf, 5).isEnd()); 
  }

  public void test_isEndWithNoCapEnd() {
    String str;
    char[] buf;
   
    str = "Mr. wiggles";
    buf = str.toCharArray();
    
    assertEquals(false, Sentence.isEnd(buf, 2).isEnd());    
  }
  
  //
  // hasLaterPunctuation
  //

  public void test_hasLaterPunctuationBasic() {
    String str;
    char[] buf;
    
    str = "fantastic. The";
    buf = str.toCharArray();
    
    assertEquals(false, Sentence.hasLaterEnd(buf, 9));  
  }

  public void test_hasLaterPunctuationPunct() {
    String str;
    char[] buf;
    
    str = "google.com..";
    buf = str.toCharArray();

    assertEquals(true, Sentence.hasLaterEnd(buf, 10)); 
    assertEquals(false, Sentence.hasLaterEnd(buf, 11));      
  }
  
  public void test_hasLaterPunctuationPunct2() {
    String str;
    char[] buf;
    
    str = "google.com. .";
    buf = str.toCharArray();

    assertEquals(true, Sentence.hasLaterEnd(buf, 10));  
    assertEquals(false, Sentence.hasLaterEnd(buf, 12));  
  }

  public void test_hasPunctuationLaterPunctWithLetters() {
    String str;
    char[] buf;
    
    str = "google.com. and";
    buf = str.toCharArray();

    assertEquals(false, Sentence.hasLaterEnd(buf, 10));   
  }
  
  //
  // hasUpper
  //
  
  public void test_hasUpperBasic() {
  String str;
  char[] buf;

  str = "fantastic. The";
  buf = str.toCharArray();
  
  assertEquals(11, Sentence.hasUpper(buf, 9));
  }
  
  public void test_hasUpperWithPunct() {
    String str;
    char[] buf;

    str = "hope\". Too";
    buf = str.toCharArray();

    assertEquals(7, Sentence.hasUpper(buf, 5));
  }

  public void test_hasUpperWithNoCapEnd() {
    String str;
    char[] buf;

    str = "Mr. wiggles";
    buf = str.toCharArray();

    assertEquals(-2, Sentence.hasUpper(buf, 2));  
  }
  
  public void test_hasUpperWithWhitespace() {
    String str;
    char[] buf;

    str = "sample.    The";
    buf = str.toCharArray();

    assertEquals(11, Sentence.hasUpper(buf, 6));    
  }
  
  //
  // isStart
  //

  public void test_isStartBasic() { 
    String str;
    char[] buf;

    str = "sample. The";
    buf = str.toCharArray();

    assertEquals(true, Sentence.isStart(buf, 8).isStart());    
  }
  
  public void test_isStartWithPunct() { 
    String str;
    char[] buf;
    
    str = "Mr. McGoo";
    buf = str.toCharArray();

    StartOp op = Sentence.isStart(buf, 4);
    assertEquals(false, op.isStart());    
  }
}
