package com.orbious;

// $Id: AllTests.java 14 2009-12-06 10:03:53Z app $

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import com.orbious.extractor.util.Helper;
import com.orbious.extractor.util.HelperTest;
import com.orbious.extractor.evaluator.AbbreviatedNameTest;
import com.orbious.extractor.evaluator.NameTest;
import com.orbious.extractor.evaluator.NumberedHeadingTest;
import com.orbious.extractor.evaluator.SuspensionTest;
import com.orbious.extractor.evaluator.AcronymTest;
import com.orbious.extractor.evaluator.UrlTextTest;
import com.orbious.extractor.Config;
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
    
    ts.addTestSuite(WhitespaceRemoverTest.class);
    ts.addTestSuite(WordTest.class);
    ts.addTestSuite(SentenceTest.class);
    ts.addTestSuite(TextParserTest.class);
    
    return(ts);
  }
  
  public static void initLogger() {
    Logger root = Logger.getRootLogger();
    if ( !root.getAllAppenders().hasMoreElements() ) {
      File f = Helper.getResourceFile(Config.LOGGER_CONF_FILENAME.asStr());
      DOMConfigurator.configure(f.toString());
    }   
  }
}