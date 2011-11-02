package com.orbious;

import java.util.Vector;
import java.util.HashSet;
import com.orbious.extractor.util.HelperTest;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.app.SentenceStatsTest;
import com.orbious.extractor.evaluator.AbbreviatedNameTest;
import com.orbious.extractor.evaluator.HeadingTest;
import com.orbious.extractor.evaluator.InsideLeftRightMarksTest;
import com.orbious.extractor.evaluator.NameTest;
import com.orbious.extractor.evaluator.NumberedHeadingTest;
import com.orbious.extractor.evaluator.SuspensionTest;
import com.orbious.extractor.evaluator.AcronymTest;
import com.orbious.extractor.evaluator.UrlTextTest;
import com.orbious.extractor.AppConfig;
import com.orbious.extractor.SentenceMapEntry;
import com.orbious.extractor.SentenceSplitterTest;
import com.orbious.extractor.TextParserOp;
import com.orbious.extractor.TextParserTest;
import com.orbious.extractor.WhitespaceRemoverTest;
import com.orbious.extractor.WordTest;
import com.orbious.extractor.SentenceTest;
import com.orbious.util.Loggers;
import com.orbious.util.config.Config;
import com.orbious.util.config.ConfigException;

import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class AllExtractorTests {

  public static Test suite() {
    TestSuite ts;

    AllExtractorTests.init();
    ts = new TestSuite("All Tests");

    ts.addTestSuite(HelperTest.class);

    ts.addTestSuite(NameTest.class);
    ts.addTestSuite(SuspensionTest.class);
    ts.addTestSuite(AcronymTest.class);
    ts.addTestSuite(UrlTextTest.class);
    ts.addTestSuite(NumberedHeadingTest.class);
    ts.addTestSuite(AbbreviatedNameTest.class);
    ts.addTestSuite(HeadingTest.class);
    ts.addTestSuite(InsideLeftRightMarksTest.class);

    ts.addTestSuite(WhitespaceRemoverTest.class);
    ts.addTestSuite(SentenceSplitterTest.class);
    ts.addTestSuite(WordTest.class);
    ts.addTestSuite(SentenceTest.class);
    ts.addTestSuite(TextParserTest.class);

    ts.addTestSuite(SentenceStatsTest.class);

    return(ts);
  }

  public static void init() {
    try {
      Config.setDefaults(AppConfig.class);
    } catch ( ConfigException ce ) {
      System.err.println("Error during Config initialisation");
      ce.printStackTrace();
    }

    Loggers.init();
  }

  public static TextParserData initTextParserData(char[] buffer,
      HashSet<Integer> lineStarts,
      SentenceMapEntry[] sentenceMap,
      Vector<TextParserOp> parserMap,
      boolean[] extractionMap,
      int avgLineCharCt) {

    TextParserData parserData = new TextParserData();
    parserData._setTextParserData(buffer, lineStarts, sentenceMap,
        parserMap, extractionMap, avgLineCharCt);

    return(parserData);
  }

  public static TextParserData initEmptyTextParserData() {
    return( initTextParserData(
        new char[5000],
        new HashSet<Integer>(),
        new SentenceMapEntry[5000],
        new Vector<TextParserOp>(),
        new boolean[5000],
        80) );
  }

  public static TextParserData initTextParserData(SentenceMapEntry[] sentenceMap) {
    return( initTextParserData(
        new char[sentenceMap.length],
        new HashSet<Integer>(),
        sentenceMap,
        new Vector<TextParserOp>(),
        new boolean[sentenceMap.length],
        80) );
  }

  public static TextParserData initTextParserData(HashSet<Integer> lineStarts) {
    return( initTextParserData(
        new char[5000],
        lineStarts,
        new SentenceMapEntry[5000],
        new Vector<TextParserOp>(),
        new boolean[5000],
        80) );
  }
}