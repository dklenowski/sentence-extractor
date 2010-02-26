package com.orbious.extractor.evaluator;

//$Id$

import java.util.Arrays;
import com.orbious.AllExtractorTests;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.evaluator.Evaluator.EvaluatorType;

import junit.framework.TestCase;


/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class NumberedHeadingTest extends TestCase {
  
  public NumberedHeadingTest(String name) {
    super(name);
    AllExtractorTests.initLogger();
  }
  
  public void test_IsRoman() {
    NumberedHeading head;
    TextParserData parserData;
    
    parserData = AllExtractorTests.initTextParserData(
        AllExtractorTests.cvtToSet(Arrays.asList(0)));

    head = new NumberedHeading(parserData, EvaluatorType.START);
    assertEquals(true, head.evaluate("I.".toCharArray(), 1));
    assertEquals(true, head.evaluate("IMD.".toCharArray(), 3));
  }
  
  public void test_IsNotRoman() {
    NumberedHeading head;
    TextParserData parserData;
    
    parserData = AllExtractorTests.initTextParserData(
        AllExtractorTests.cvtToSet(Arrays.asList(0)));

    head = new NumberedHeading(parserData, EvaluatorType.START);
    assertEquals(false, head.evaluate("typhoonIIM".toCharArray(), 7));
    assertEquals(false, head.evaluate("IPD.".toCharArray(), 3));
  }
  
  public void test_IsNumbered() {
    NumberedHeading head;
    String str;
    TextParserData parserData;
    
    parserData = AllExtractorTests.initTextParserData(
        AllExtractorTests.cvtToSet(Arrays.asList(0, 2)));

    head = new NumberedHeading(parserData, EvaluatorType.START);

    str = "15.";
    assertEquals(true, head.evaluate(str.toCharArray(), 2));
    
    str = "  1."; 
    assertEquals(true, head.evaluate(str.toCharArray(), 3));
  }
  
  public void test_IsNotNumbered() {
    NumberedHeading head;
    String str;
    TextParserData parserData;
    
    parserData = AllExtractorTests.initTextParserData(
        AllExtractorTests.cvtToSet(Arrays.asList(0)));

    head = new NumberedHeading(parserData, EvaluatorType.START);

    str = "sad day in 1984.";
    assertEquals(false, head.evaluate(str.toCharArray(), 15));
  }
}
