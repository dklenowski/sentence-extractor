package com.orbious.extractor.evaluator;

//$Id$

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import com.orbious.AllTests;
import com.orbious.extractor.TextParser.TextParserData;

import junit.framework.TestCase;


/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class NumberedHeadingTest extends TestCase {
  
  public NumberedHeadingTest(String name) {
    super(name);
    AllTests.initLogger();
  }
  
  public void test_WordIsNumbered() {
    NumberedHeading head;
    
    head = new NumberedHeading();
    assertEquals(true, head.evaluate("1."));
    assertEquals(true, head.evaluate("16534."));
    
  }
  
  public void test_WordIsNotNumbered() {
    NumberedHeading head;
    
    head = new NumberedHeading();
    assertEquals(false, head.evaluate("typhoon1."));    
    assertEquals(false, head.evaluate("1984"));
  }
  
  public void test_WordIsRoman() {
    NumberedHeading head;
    
    head = new NumberedHeading();
    assertEquals(true, head.evaluate("I."));
    assertEquals(true, head.evaluate("IMD."));
  }
  
  public void test_WordIsNotRomain() {
    NumberedHeading head;
    
    head = new NumberedHeading();
    assertEquals(false, head.evaluate("typhoonIIM"));
    assertEquals(false, head.evaluate("IPD."));
  }
  
  public void test_BufIsNumbered() {
    NumberedHeading head;
    String str;

    initLineStarts(Arrays.asList(0, 2));
    head = new NumberedHeading();

    str = "15.";
    assertEquals(true, head.evaluate(str.toCharArray(), 2));
    
    str = "  1."; 
    assertEquals(true, head.evaluate(str.toCharArray(), 3));
  }
  
  public void test_BufIsNotNumbered() {
    NumberedHeading head;
    String str;

    initLineStarts(Arrays.asList(0));
    head = new NumberedHeading();

    str = "sad day in 1984.";
    assertEquals(false, head.evaluate(str.toCharArray(), 15));
  }

  private void initLineStarts(List< Integer > list) {
    HashSet<Integer> lineStarts;

    lineStarts = new HashSet<Integer>();
    for ( int i = 0; i < list.size(); i++ ) {
      lineStarts.add(list.get(i));
    }
    
    TextParserData.lineStarts(lineStarts);
  }
}
