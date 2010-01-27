package com.orbious.extractor.evaluator;

import com.orbious.AllExtractorTests;
import com.orbious.extractor.evaluator.Evaluator.EvaluatorType;

import junit.framework.TestCase;

public class AbbreviatedNameTest extends TestCase {
  
  public AbbreviatedNameTest(String name) {
    super(name);
    AllExtractorTests.initLogger();
  }
  
  public void test_EvaluateMatch() {
    AbbreviatedName name;
    String str;
    
    name = new AbbreviatedName(EvaluatorType.START);

    str = "W.H.D. Rouse";
    assertTrue(name.evaluate(str.toCharArray(), 0));
    assertTrue(name.evaluate(str.toCharArray(), 1));
    assertTrue(name.evaluate(str.toCharArray(), 2));
    assertTrue(name.evaluate(str.toCharArray(), 3));
    assertTrue(name.evaluate(str.toCharArray(), 4));
    assertTrue(name.evaluate(str.toCharArray(), 5));
    
    str = "M.A. Little";
    assertTrue(name.evaluate(str.toCharArray(), 0));
    assertTrue(name.evaluate(str.toCharArray(), 1));
    assertTrue(name.evaluate(str.toCharArray(), 2));
    assertTrue(name.evaluate(str.toCharArray(), 3));
    
    str = "B. Thomas";
    assertTrue(name.evaluate(str.toCharArray(), 0));
    assertTrue(name.evaluate(str.toCharArray(), 1)); 
    
    str = "C. H. Bompas.";
    assertTrue(name.evaluate(str.toCharArray(), 0));
    assertTrue(name.evaluate(str.toCharArray(), 1));
    assertTrue(name.evaluate(str.toCharArray(), 3));
    assertTrue(name.evaluate(str.toCharArray(), 4)); 
  }

  public void test_EvaluateNotMatch() {
    AbbreviatedName name;
    String str;
    
    name = new AbbreviatedName(EvaluatorType.START);

    str = "plenty. The";
    assertFalse(name.evaluate(str.toCharArray(), 6)); 
    assertFalse(name.evaluate(str.toCharArray(), 8));
    
    str = "riders. THE";
    assertFalse(name.evaluate(str.toCharArray(), 6));
    assertFalse(name.evaluate(str.toCharArray(), 8));

    str = "www.gutenberg.net .";
    assertFalse(name.evaluate(str.toCharArray(), 3));
    assertFalse(name.evaluate(str.toCharArray(), 13));
    assertFalse(name.evaluate(str.toCharArray(), 18));
    
    str = "www.gutenberg.net . Title";
    assertFalse(name.evaluate(str.toCharArray(), 18));
  }
  
  public void test_EvaluateReverseMatch() {
    AbbreviatedName name;
    String str;
    
    name = new AbbreviatedName(EvaluatorType.START);

    str = "Rouse W.H.D. wore pants";
    assertTrue(name.evaluate(str.toCharArray(), 6));
    assertTrue(name.evaluate(str.toCharArray(), 7));
    assertTrue(name.evaluate(str.toCharArray(), 8));
    assertTrue(name.evaluate(str.toCharArray(), 9));
    assertTrue(name.evaluate(str.toCharArray(), 10));
    assertTrue(name.evaluate(str.toCharArray(), 11));

    str = "Little M.A. was tenable";
    assertTrue(name.evaluate(str.toCharArray(), 7));
    assertTrue(name.evaluate(str.toCharArray(), 8));
    assertTrue(name.evaluate(str.toCharArray(), 9));
    assertTrue(name.evaluate(str.toCharArray(), 10));
    
    str = "Thomas B. said there were many";
    assertTrue(name.evaluate(str.toCharArray(), 7));
    assertTrue(name.evaluate(str.toCharArray(), 8));
  }
  
  public void test_EvaluateSentence() {
    AbbreviatedName name;
    String str;
    char[] buf;
    
    name = new AbbreviatedName(EvaluatorType.END);
    
    str = "At length out steps P. Petronius, an old chum of";
    buf = str.toCharArray();
    for ( int i = 0; i < buf.length; i++ ) {
      System.out.println(i + "=" + buf[i]);
    }
    
    //assertTrue(name.evaluate(str.toCharArray(), 21));
    
    name = new AbbreviatedName(EvaluatorType.START);
    assertTrue(name.evaluate(str.toCharArray(), 23));
  }
}
