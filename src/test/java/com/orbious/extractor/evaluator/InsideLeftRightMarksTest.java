package com.orbious.extractor.evaluator;

import java.util.Arrays;
import java.util.List;
import com.orbious.AllExtractorTests;
import com.orbious.extractor.SentenceMapEntry;
import com.orbious.extractor.SentenceMapEntry.Likelihood;
import com.orbious.extractor.SentenceMapEntry.SentenceEntryType;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.evaluator.Evaluator.EvaluatorType;

import junit.framework.TestCase;

public class InsideLeftRightMarksTest extends TestCase {

  public InsideLeftRightMarksTest(String name) {
    super(name);
    AllExtractorTests.initLogger();
  }
  
  public void test_IsNotInsideBracketStart() {
    String str;
    InsideLeftRightMarks inside;
    char[] buf;
    TextParserData parserData;
    
    try {
      str = "I have had to omit a certain number of stories as unsuited for publication.";
      buf = str.toCharArray();
      parserData = AllExtractorTests.initEmptyTextParserData();
      
      inside = new InsideLeftRightMarks(parserData, EvaluatorType.START);
      assertFalse(inside.evaluate(buf, 0));
      
      str = "Australia has the worst public broadcast network in the world. There is absolutely nothing of value. Albeit the news.";
      buf = str.toCharArray();
      parserData = initParserData(str.toCharArray(), Arrays.asList(0, 61, 99, 116));
      
      inside = new InsideLeftRightMarks(parserData, EvaluatorType.START);
      assertFalse(inside.evaluate(buf, 0));
      assertFalse(inside.evaluate(buf, 63));   
      assertFalse(inside.evaluate(buf, 101));

    } catch ( Exception e ) {
      fail(e.getMessage());
    }
  }
  
  public void test_IsInsideBracketStart() {
    String str;
    InsideLeftRightMarks inside;
    char[] buf;
    TextParserData parserData;
    
    try {
      str = "The matter will probably continue to be decided by every one according to his" +
      "view of Seneca's character and abilities: in the matters of style and of" +
      "sentiment much may be said on both sides. Dion Cassius (lx, 35) says that" +
      "Seneca composed an [Greek: apokolokuntosis] or Pumpkinification of";
      buf = str.toCharArray();
      parserData = initParserData(str.toCharArray(), Arrays.asList(189));
      
      inside = new InsideLeftRightMarks(parserData, EvaluatorType.START);
      assertTrue(inside.evaluate(buf, 242));
      
    } catch ( Exception e ) {
      fail(e.getMessage());
    }
  }
  
  public void test_IsInsideBracketStart2() {
    String str;
    InsideLeftRightMarks inside;
    char[] buf;
    TextParserData parserData;
    
    try {      
      str = "or multitude. [Sidenote: Il ix, 385] Claudius find";
      buf = str.toCharArray();
      parserData = initParserData(str.toCharArray(), Arrays.asList(12));
      
      inside = new InsideLeftRightMarks(parserData, EvaluatorType.START);
      assertTrue(inside.evaluate(buf, 25));
      
    } catch ( Exception e ) {
      fail(e.getMessage());
    }
  }
  
  public void test_IsInsideBracketEnd() {
    String str;
    InsideLeftRightMarks inside;
    char[] buf;
    TextParserData parserData;
    
    try {
      str = "The matter will probably continue to be decided by every one according to his" +
      "view of Seneca's character and abilities: in the matters of style and of" +
      "sentiment much may be said on both sides. Dion Cassius (lx, 35) says that" +
      "Seneca composed an [greek: apokolokuntosis.] or Pumpkinification of";
      buf = str.toCharArray();
      parserData = initParserData(str.toCharArray(), Arrays.asList(189));

      inside = new InsideLeftRightMarks(parserData, EvaluatorType.START);
      assertTrue(inside.evaluate(buf, 264));
      
    } catch ( Exception e ) {
      fail(e.getMessage());
    }
  }
  
  public TextParserData initParserData(final char[] buf, List<Integer> likelyEnds) {
    SentenceMapEntry[] map;
    
    map = new SentenceMapEntry[buf.length];
    for ( int i = 0; i < likelyEnds.size(); i++ ) {
      map[likelyEnds.get(i)] = 
        new SentenceMapEntry(Likelihood.LIKELY, SentenceEntryType.END);
    }
    
    TextParserData parserData = AllExtractorTests.initTextParserData(map);
    return(parserData);
  }
}
