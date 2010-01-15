package com.orbious.extractor.evaluator;

// $Id$

import com.orbious.AllExtractorTests;
import com.orbious.extractor.evaluator.Name;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class NameTest extends TestCase {
  
  public NameTest(String name) {
    super(name);
    AllExtractorTests.initLogger();
  }
  
  public void test_WordIsName() {
    boolean ret;
    Name name;
    
    name = new Name();
    ret = name.evaluate("Abril".toCharArray(), 0);
    assertEquals(true, ret);

    ret = name.evaluate("Taylor".toCharArray(), 0);
    assertEquals(true, ret);
  }

  public void test_WordIsNotName() {
    boolean ret;
    Name name;
    
    name = new Name();
    ret = name.evaluate("Tomato".toCharArray(), 0);    
    assertEquals(false, ret);   

    ret = name.evaluate("mr.".toCharArray(), 0);   
    assertEquals(false, ret); 
  }
  
  public void test_WordIsNameUppercase() {
    boolean ret;
    Name name;
    
    name = new Name();
    ret = name.evaluate("WESH".toCharArray(), 0);    
    assertEquals(true, ret); 
  }
  
  public void test_BufIsName() {
    boolean ret;
    Name name;
    String str;
    
    name = new Name();
    
    str = "Good day Mr WESH.";
    ret = name.evaluate(str.toCharArray(), 12);
    assertEquals(true, ret);
  }
}
