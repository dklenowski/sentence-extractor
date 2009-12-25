package com.orbious.extractor.evaluator;

// $Id$

import com.orbious.AllTests;
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
    AllTests.initLogger();
  }
  
  public void test_isName() {
    boolean ret;
    Name name;
    
    name = new Name();
    ret = name.evaluate("Abril");
    assertEquals(true, ret);

    ret = name.evaluate("Taylor");
    assertEquals(true, ret);
  }

  public void test_isNotName() {
    boolean ret;
    Name name;
    
    name = new Name();
    ret = name.evaluate("Tomato");    
    assertEquals(false, ret);   

    ret = name.evaluate("mr.");   
    assertEquals(false, ret); 
  }
}
