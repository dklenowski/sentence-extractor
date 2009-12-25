package com.orbious.extractor.evaluator;

// $Id: SuspensionTest.java 12 2009-12-05 11:40:44Z app $

import com.orbious.AllTests;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class SuspensionTest extends TestCase {
  
  public SuspensionTest(String name) {
    super(name);
    AllTests.initLogger();
  }
  
  public void test_isSuspension() {
    Suspension suspension = new Suspension();
    boolean ret = suspension.evaluate("Mr.");
    assertEquals(true, ret);
  }

  public void test_notSuspension() {
    boolean ret;

    Suspension suspension = new Suspension();
    
    ret = suspension.evaluate("Tomato");
    assertEquals(false, ret);

    ret = suspension.evaluate("mr.");
    assertEquals(false, ret); 
  }
}
