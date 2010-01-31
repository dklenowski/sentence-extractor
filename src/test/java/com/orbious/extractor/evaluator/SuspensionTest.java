package com.orbious.extractor.evaluator;

// $Id: SuspensionTest.java 12 2009-12-05 11:40:44Z app $

import java.io.FileNotFoundException;

import com.orbious.AllExtractorTests;
import com.orbious.extractor.evaluator.Evaluator.EvaluatorType;

import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class SuspensionTest extends TestCase {
  
  public SuspensionTest(String name) {
    super(name);
    AllExtractorTests.initLogger();
  }
  
  public void test_isSuspension() {
    try {
      Suspension suspension = new Suspension(null, EvaluatorType.END);
      
      assertTrue(suspension.evaluate("Mr.".toCharArray(), 2));
      assertTrue(suspension.evaluate("Il.".toCharArray(), 2));
    } catch ( FileNotFoundException fnfe ) {
      fail("Failed to open suspensions txt file");
    }
  }

  public void test_notSuspension() {
    Suspension suspension;
    
    try {
      suspension = new Suspension(null, EvaluatorType.START);
      assertFalse(suspension.evaluate("Tomato".toCharArray(), 0));
      
      suspension = new Suspension(null, EvaluatorType.END);      
      assertFalse(suspension.evaluate("empty.".toCharArray(), 5));
    } catch ( FileNotFoundException fnfe ) {
      fail("Failed to open suspensions txt file");
    }
  }
}
