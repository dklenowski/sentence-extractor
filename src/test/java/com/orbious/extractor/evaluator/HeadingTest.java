package com.orbious.extractor.evaluator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import com.orbious.AllExtractorTests;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.evaluator.Evaluator.EvaluatorType;

import junit.framework.TestCase;

public class HeadingTest extends TestCase {
  
  public HeadingTest(String name) { 
    super(name);
    AllExtractorTests.initLogger();
  }
  
  public void test_BufWithHeading() {
    Vector<String> data = new Vector<String>(
        Arrays.asList(
            "* Uses heuristic algorithms. ", 
            "The Sentence Extraction Algorithm ",
            "Basically consists of the following steps: ",
            "1. Extract all text from document, removing excessive whitespace (e.g. multiple spaces between words) into a character buffer. ",
            "2. For each word in the character buffer:",
            "1. If the first character of a word (has a whitespace previously) is uppercase begin the Start Evaluation Process. ",
            "2. If the character is a sentence end (e.g. '.', '!' etc) run the End Evaluation Process." ));
 
    char[] buf = initTextParserData(data);
    HeadingEvaluator evaluator = new HeadingEvaluator(EvaluatorType.START);
    boolean ret;
    try {
      ret = evaluator.evaluate(buf, 1);
      assertEquals(false, ret);
      ret = evaluator.evaluate(buf, 28);
      assertEquals(true, ret);
      ret = evaluator.evaluate(buf, 62);
      assertEquals(false, ret);
    } catch ( Exception e ) { 
      fail("Exception thrown: \n" + e.getMessage());
    }
    
  }
  
  
  private char[] initTextParserData(Vector<String> data) {
    HashSet<Integer> lineStarts;
    int charCt;
    
    lineStarts = new HashSet<Integer>();
    charCt = 0;
    for ( int i = 0; i < data.size(); i++ ) {
      lineStarts.add(charCt);
      charCt += data.get(i).length();
    }
    
    TextParserData.setTextParserData( lineStarts, null, (charCt/data.size()) );
    
    char[] buffer;
    char[] buf;
    int pos;
    
    buffer = new char[charCt];
    pos = 0;
    for ( int i = 0; i < data.size(); i++ ) {
      buf = data.get(i).toCharArray();
      System.arraycopy(buf, 0, buffer, pos, buf.length);
      pos += buf.length;
    }
    
    return(buffer);
  }

}
