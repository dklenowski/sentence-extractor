package com.orbious.extractor.evaluator;

import com.orbious.AllTests;
import com.orbious.extractor.evaluator.Acronym;

import junit.framework.TestCase;

/**
 * $Id: AcronymTest.java 12 2009-12-05 11:40:44Z app $
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class AcronymTest extends TestCase {

  public AcronymTest(String name) { 
    super(name);
    AllTests.initLogger();
  }
  
  public void test_ExceptionThrown() {
    String str = "test";
    char[] buf = str.toCharArray();
    Acronym acronym = new Acronym();

    try {
      acronym.evaluate(buf, 4);
      fail("No ArrayIndexOutOfBoundsException for pos=" + 4);
    } catch ( ArrayIndexOutOfBoundsException ioobe ) { }

    try {
      acronym.evaluate(buf, -1);
      fail("No ArrayIndexOutOfBoundsException for pos=" + 4);
    } catch ( ArrayIndexOutOfBoundsException ioobe ) { }
      
  }

  public void test_AcronymWithSeparatedStop() {
    String str = "E.M.C. .";
    char[] buf = str.toCharArray();
    Acronym acronym = new Acronym();
    
    assertTrue(acronym.evaluate(buf, 1));
    assertTrue(acronym.evaluate(buf, 3));
    assertTrue(acronym.evaluate(buf, 5));
    assertFalse(acronym.evaluate(buf, 7));
  }
  
  public void test_AcronymWithSeparatedStopAsWord() {
    String str = "E.M.C. .";
    Acronym acronym = new Acronym();    

    assertTrue(acronym.evaluate(str));
  }
  
  public void test_AcronymWithStop() {
    String str = "I.B.M..";
    char[] buf = str.toCharArray();
    Acronym acronym = new Acronym();

    assertTrue(acronym.evaluate(buf, 1));
    assertTrue(acronym.evaluate(buf, 3));
    assertTrue(acronym.evaluate(buf, 5));
    assertFalse(acronym.evaluate(buf, 6));
  }
  
  public void test_AcronymWithStopAsWord() {
    String str = "I.B.M.."; 
    Acronym acronym = new Acronym();

    assertTrue(acronym.evaluate(str));
  }
  
  public void test_AcronymAsLowercase() {
    String str = "e.g. ";
    char[] buf = str.toCharArray();
    Acronym acronym = new Acronym();

    assertTrue(acronym.evaluate(buf, 1));
    assertTrue(acronym.evaluate(buf, 3)); 
  }
  
  public void test_AcronymAsLowercaseAsWord() {
    String str = "e.g. ";
    Acronym acronym = new Acronym();

    assertTrue(acronym.evaluate(str));  
  }

  public void test_NotAcronym() {
    String str;
    char[] buf;

    Acronym acronym = new Acronym();
    
    str = "\",As";
    buf = str.toCharArray();

    assertFalse(acronym.evaluate(buf, 0));
    assertFalse(acronym.evaluate(buf, 1));

    str = "they?\"";
    buf = str.toCharArray();

    assertFalse(acronym.evaluate(buf, 4));
    assertFalse(acronym.evaluate(buf, 5));
  
    str = "lecturin\',\"";
    buf = str.toCharArray();

    assertFalse(acronym.evaluate(buf, 8));
    assertFalse(acronym.evaluate(buf, 9));    
    assertFalse(acronym.evaluate(buf, 10));
      
    str = "Titan...";
    buf = str.toCharArray();

    assertFalse(acronym.evaluate(buf, 5));
    assertFalse(acronym.evaluate(buf, 6));    
    assertFalse(acronym.evaluate(buf, 7));
    
    str = "\"Oh,";
    buf = str.toCharArray();

    assertFalse(acronym.evaluate(buf, 0));
    assertFalse(acronym.evaluate(buf, 3));
  }
  
  public void test_NotAcronymAsWord() {
    String str;
    Acronym acronym = new Acronym();
    
    str = "\",As";
    assertFalse(acronym.evaluate(str));
    
    str = "they?\"";
    assertFalse(acronym.evaluate(str));
    
    str = "lecturin\',\"";
    assertFalse(acronym.evaluate(str));
    
    str = "Titan...";
    assertFalse(acronym.evaluate(str));

    str = "\"Oh,";
    assertFalse(acronym.evaluate(str)); 
  }
}
