package com.orbious.extractor.evaluator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import com.orbious.AllExtractorTests;
import com.orbious.extractor.SentenceMapEntry;
import com.orbious.extractor.TextParserOp;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.evaluator.Evaluator.EvaluatorType;

import junit.framework.TestCase;

public class HeadingTest extends TestCase {

  public HeadingTest(String name) {
    super(name);
    AllExtractorTests.init();
  }

  public void test_BufWithHeading() {
    TextParserData parserData;
    Vector<String> data = new Vector<String>(
        Arrays.asList(
            "* Uses heuristic algorithms. ",
            "The Sentence Extraction Algorithm ",
            "Basically consists of the following steps: ",
            "1. Extract all text from document, removing excessive whitespace (e.g. multiple spaces between words) into a character buffer. ",
            "2. For each word in the character buffer:",
            "1. If the first character of a word (has a whitespace previously) is uppercase begin the Start Evaluation Process. ",
            "2. If the character is a sentence end (e.g. '.', '!' etc) run the End Evaluation Process." ));

    parserData = new TextParserData();
    char[] buf = initTextParserData(data, parserData);

    Heading heading = new Heading(parserData, EvaluatorType.START);
    heading.invalidate();

    boolean ret;
    try {
      ret = heading.evaluate(buf, 2);
      assertEquals(false, ret);
      ret = heading.evaluate(buf, 29);
      assertEquals(true, ret);
      ret = heading.evaluate(buf, 63);
      assertEquals(false, ret);
    } catch ( Exception e ) {
      fail("Exception thrown: \n" + e.getMessage());
    }

  }


  private char[] initTextParserData(Vector<String> data, TextParserData parserData) {
    HashSet<Integer> lineStarts;
    int charCt;

    lineStarts = new HashSet<Integer>();
    charCt = 0;
    for ( int i = 0; i < data.size(); i++ ) {
      lineStarts.add(charCt);
      charCt += data.get(i).length();
    }

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

    parserData._setTextParserData(buffer, lineStarts,
        new SentenceMapEntry[buffer.length],
        new Vector<TextParserOp>(),
        new boolean[buffer.length], (charCt/data.size()) );

    return(buffer);
  }

}
