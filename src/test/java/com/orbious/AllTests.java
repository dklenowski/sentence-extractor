package com.orbious;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import com.orbious.util.HelperTest;
import com.orbious.extractor.CleanserTest;
import com.orbious.extractor.evaluator.NameTest;
import com.orbious.extractor.evaluator.SuspensionTest;
import com.orbious.extractor.evaluator.AcronymTest;
import com.orbious.extractor.WordTest;
import com.orbious.extractor.SentenceTest;
import junit.framework.TestSuite;
import junit.framework.Test;

/**
 * $Id: AllTests.java 14 2009-12-06 10:03:53Z app $
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class AllTests {

	public static void initLogger() {
		Logger root = Logger.getRootLogger();
		if ( !root.getAllAppenders().hasMoreElements() ) {
			DOMConfigurator.configure("resources/log4j.xml");
		}		
	}
	
	public static Test suite() {
		initLogger();
		TestSuite ts = new TestSuite("All Tests");
		
		ts.addTestSuite(HelperTest.class);
		ts.addTestSuite(SuspensionTest.class);
		ts.addTestSuite(AcronymTest.class);
		ts.addTestSuite(NameTest.class);
		ts.addTestSuite(CleanserTest.class);
		ts.addTestSuite(WordTest.class);
		ts.addTestSuite(SentenceTest.class);
		
		return(ts);
	}
}