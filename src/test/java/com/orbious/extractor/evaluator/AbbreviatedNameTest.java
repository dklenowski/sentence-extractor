package com.orbious.extractor.evaluator;

import com.orbious.AllExtractorTests;

import junit.framework.TestCase;

public class AbbreviatedNameTest extends TestCase {
  
  public AbbreviatedNameTest(String name) {
    super(name);
    AllExtractorTests.initLogger();
  }
  
  public void test_EvaluateMatch() {
    AbbreviatedName name;
    String str;
    
    name = new AbbreviatedName();

    str = "W.H.D. Rouse";
    assertEquals(true, name.evaluate(str.toCharArray(), 0));
    assertEquals(true, name.evaluate(str.toCharArray(), 1));
    assertEquals(true, name.evaluate(str.toCharArray(), 2));
    assertEquals(true, name.evaluate(str.toCharArray(), 3));
    assertEquals(true, name.evaluate(str.toCharArray(), 4));
    assertEquals(true, name.evaluate(str.toCharArray(), 5));
    
    str = "M.A. Little";
    assertEquals(true, name.evaluate(str.toCharArray(), 0));
    assertEquals(true, name.evaluate(str.toCharArray(), 1));
    assertEquals(true, name.evaluate(str.toCharArray(), 2));
    assertEquals(true, name.evaluate(str.toCharArray(), 3));
    
    str = "B. Thomas";
    assertEquals(true, name.evaluate(str.toCharArray(), 0));
    assertEquals(true, name.evaluate(str.toCharArray(), 1)); 
    
    str = "C. H. Bompas.";
    assertEquals(true, name.evaluate(str.toCharArray(), 0));
    assertEquals(true, name.evaluate(str.toCharArray(), 1));
    assertEquals(true, name.evaluate(str.toCharArray(), 3));
    assertEquals(true, name.evaluate(str.toCharArray(), 4)); 
  }

  public void test_EvaluateNotMatch() {
    AbbreviatedName name;
    String str;
    
    name = new AbbreviatedName();

    str = "plenty. The";
    assertEquals(false, name.evaluate(str.toCharArray(), 6)); 
    assertEquals(false, name.evaluate(str.toCharArray(), 8));
    
    str = "riders. THE";
    assertEquals(false, name.evaluate(str.toCharArray(), 6));
    assertEquals(false, name.evaluate(str.toCharArray(), 8));

    str = "www.gutenberg.net .";
    assertEquals(false, name.evaluate(str.toCharArray(), 3));
    assertEquals(false, name.evaluate(str.toCharArray(), 13));
    assertEquals(false, name.evaluate(str.toCharArray(), 18));
    
    str = "www.gutenberg.net . Title";
    assertEquals(false, name.evaluate(str.toCharArray(), 18));
  }
  
  public void test_EvaluateReverseMatch() {
    AbbreviatedName name;
    String str;
    
    name = new AbbreviatedName();

    str = "Rouse W.H.D.";
    assertEquals(true, name.evaluate(str.toCharArray(), 6));
    assertEquals(true, name.evaluate(str.toCharArray(), 7));
    assertEquals(true, name.evaluate(str.toCharArray(), 8));
    assertEquals(true, name.evaluate(str.toCharArray(), 9));
    assertEquals(true, name.evaluate(str.toCharArray(), 10));
    assertEquals(true, name.evaluate(str.toCharArray(), 11));

    str = "Little M.A.";
    assertEquals(true, name.evaluate(str.toCharArray(), 7));
    assertEquals(true, name.evaluate(str.toCharArray(), 8));
    assertEquals(true, name.evaluate(str.toCharArray(), 9));
    assertEquals(true, name.evaluate(str.toCharArray(), 10));
    
    str = "Thomas B.";
    assertEquals(true, name.evaluate(str.toCharArray(), 7));
    assertEquals(true, name.evaluate(str.toCharArray(), 8));
  }
}
