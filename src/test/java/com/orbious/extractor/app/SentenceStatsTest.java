package com.orbious.extractor.app;

import com.orbious.AllExtractorTests;
import com.orbious.extractor.app.SentenceStats;
import java.util.Vector;

import junit.framework.TestCase;

import com.orbious.util.Strings;

public class SentenceStatsTest extends TestCase {

  public SentenceStatsTest(String name) {
    super(name);
    AllExtractorTests.init();
  }

	public void test_Filestats() throws Exception {
	  Vector<Vector<String>> sentences;
	  String actual;
	  String expected;

	  sentences = new Vector<Vector<String>>();
	  sentences.add(Strings.cvtString("a short sentence"));
	  sentences.add(Strings.cvtString("A slightly longer sentence"));
	  sentences.add(Strings.cvtString("And finally a reasonably large sentence that contains a few different words"));

	  expected = "3, 3, 12, 6.333333333333333";
	  actual = SentenceStats.filestats(sentences);

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }
	}

}
