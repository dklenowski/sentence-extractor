package com.orbious.extractor.evaluator;

// $Id$

import com.orbious.AllTests;
import junit.framework.TestCase;

/**
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
    char[] buf;
    
    url = new UrlText();
    
    str = "www.gutenberg.org..";
    buf = str.toCharArray();
    
    for ( int i = 0; i < buf.length; i++ ) {
    	System.out.println(i + "=" + buf[i]);
    }
    result = url.evaluate(buf, 3);
    assertEquals(true, result);
    result = url.evaluate(buf, 13);  
    assertEquals(true, result);
    result = url.evaluate(buf, 17);
    assertEquals(true, result);   
    result = url.evaluate(buf, 18);
    assertEquals(false, result);  
    
    str = "google.com";
    buf = str.toCharArray();
    result = url.evaluate(buf, 6);
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
