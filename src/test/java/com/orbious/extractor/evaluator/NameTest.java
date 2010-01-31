package com.orbious.extractor.evaluator;

// $Id$

import java.io.FileNotFoundException;

import com.orbious.AllExtractorTests;
import com.orbious.extractor.evaluator.Name;
import com.orbious.extractor.evaluator.Evaluator.EvaluatorType;

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
    
    try {
      name = new Name(null, EvaluatorType.START);
      ret = name.evaluate("Abril".toCharArray(), 0);
      assertEquals(true, ret);
  
      ret = name.evaluate("Taylor".toCharArray(), 0);
      assertEquals(true, ret);
    } catch ( FileNotFoundException fnfe ) {
      fail("Failed to open names txt file");
    }
  }

  public void test_WordIsNotName() {
    boolean ret;
    Name name;
    
    try {
      name = new Name(null, EvaluatorType.START);
      ret = name.evaluate("Tomato".toCharArray(), 0);    
      assertEquals(false, ret);   
  
      ret = name.evaluate("mr.".toCharArray(), 0);   
      assertEquals(false, ret); 
    } catch ( FileNotFoundException fnfe ) {
      fail("Failed to open names txt file");
    }
  }
  
  public void test_WordIsNameUppercase() {
    boolean ret;
    Name name;
    
    try {
      name = new Name(null, EvaluatorType.START);
      ret = name.evaluate("WESH".toCharArray(), 0);    
      assertEquals(true, ret); 
    } catch ( FileNotFoundException fnfe ) {
      fail("Failed to open names txt file");;
    }
  }
  
  public void test_BufIsName() {
    boolean ret;
    Name name;
    String str;
    
    try {
      name = new Name(null, EvaluatorType.START);
      str = "Good day Mr WESH.";
      ret = name.evaluate(str.toCharArray(), 12);
      assertEquals(true, ret);
    } catch ( FileNotFoundException fnfe ) {
      fail("Failed to open names txt file");
    }
  }
  
  public void test_BufIsNotName() {
    boolean ret;
    Name name;
    String str;

    try {
      name = new Name(null, EvaluatorType.START);
      str = "Evaluation Process 1. Run through the start eval";
      ret = name.evaluate(str.toCharArray(), 22);
      assertEquals(false, ret);
    } catch ( FileNotFoundException fnfe ) {
      fail("Failed to open names txt file");
    }    
  }
}
