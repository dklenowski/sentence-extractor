package com.orbious;

// $Id: AllTests.java 14 2009-12-06 10:03:53Z app $

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import com.orbious.extractor.util.Helper;
import com.orbious.extractor.util.HelperTest;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.evaluator.AbbreviatedNameTest;
import com.orbious.extractor.evaluator.HeadingTest;
import com.orbious.extractor.evaluator.InsideLeftRightMarksTest;
import com.orbious.extractor.evaluator.NameTest;
import com.orbious.extractor.evaluator.NumberedHeadingTest;
import com.orbious.extractor.evaluator.SuspensionTest;
import com.orbious.extractor.evaluator.AcronymTest;
import com.orbious.extractor.evaluator.UrlTextTest;
import com.orbious.extractor.Config;
import com.orbious.extractor.SentenceMapEntry;
import com.orbious.extractor.SentenceSplitterTest;
import com.orbious.extractor.TextParserOp;
import com.orbious.extractor.TextParserTest;
import com.orbious.extractor.WhitespaceRemoverTest;
import com.orbious.extractor.WordTest;
import com.orbious.extractor.SentenceTest;
import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class AllExtractorTests {
  
  public static Test suite() {
    initLogger();
    TestSuite ts = new TestSuite("All Tests");
    
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
    
    return(ts);
  }
  
  public static void initLogger() {
    Logger root = Logger.getRootLogger();
    if ( !root.getAllAppenders().hasMoreElements() ) {
      try {
        File f = Helper.getResourceFile(Config.LOGGER_CONF_FILENAME.asStr());
        DOMConfigurator.configure(f.toString());
      } catch ( IOException ioe ) {
        System.err.println("Failed to find log4j resource (" + 
            Config.LOGGER_CONF_FILENAME.asStr() + "), using BasicConfigurator.");
        BasicConfigurator.configure();
      }
    }   
  }
  
  public static String compare(String expected, String actual) { 
    diff_match_patch dmp = new diff_match_patch();
    LinkedList<Diff> d = dmp.diff_main(expected, actual);
    
    String str = "Expected=|" + expected + "|\n" +
                 "Actual  =|" + actual + "\n" +
                 "Diff    =" + d;
    return(str);
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
  
  public static HashSet<Integer> cvtToSet(List<Integer> list) {
    HashSet<Integer> hs = new HashSet<Integer>();
    for ( int i = 0; i < list.size(); i++ ) {
      hs.add(list.get(i));
    }
    
    return(hs);
  }

}