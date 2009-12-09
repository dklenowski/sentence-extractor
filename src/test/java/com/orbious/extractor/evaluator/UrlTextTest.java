package com.orbious.extractor.evaluator;

import com.orbious.AllTests;
import junit.framework.TestCase;

/**
 * $Id$
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class UrlTextTest extends TestCase {

  public UrlTextTest(String name) {
    super(name);
    AllTests.initLogger();    
  }
  
  public void test_isUrlWord() {
    boolean result;
    UrlText url;
    
    url = new UrlText();
    
    result = url.evaluate("www.gutenberg.org");
    assertEquals(true, result);
    
    result = url.evaluate("google.com");
    assertEquals(true, result);
  }
  
  public void test_isUrlBuffer() {
    boolean result;
    UrlText url;
    String str;
    
    url = new UrlText();
    
    str = "www.gutenberg.org";
    result = url.evaluate(str.toCharArray(), 3);
    result = url.evaluate(str.toCharArray(), 13);   
    assertEquals(true, result);
    
    str = "google.com";
    result = url.evaluate(str.toCharArray(), 6);
    assertEquals(true, result);   
  }
  
  public void test_notUrlWord() {
    boolean result;
    UrlText url;
    
    url = new UrlText();
    
    result = url.evaluate("gutenberg.");
    assertEquals(false, result);    
    
    result = url.evaluate("google.");
    assertEquals(false, result);    
  }

  public void test_notUrlBuffer() {
    boolean result;
    UrlText url;
    String str;
    
    url = new UrlText();
    str = "gutenberg.";
    result = url.evaluate(str.toCharArray(), 9);
    assertEquals(false, result);    
    
    str = "google.";
    result = url.evaluate(str.toCharArray(), 6);
    assertEquals(false, result);    
  }
}
