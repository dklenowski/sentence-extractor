package com.orbious.extractor;

// $Id: CleanserTest.java 14 2009-12-06 10:03:53Z app $

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import com.orbious.AllTests;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class CleanserTest extends TestCase {
  
  public CleanserTest(String name) {
    super(name);
    AllTests.initLogger();
  }

  public void test_CleanWordsBasic() {
    Vector<String> words = new Vector<String>(
        Arrays.asList("Schrodinger's ",
            "  cat *",
            "sat",
            "on",
            "the",
            "time-line",
            ",",
            "who",
            "knew",
            "the",
            "cat",
            "wouldn't",
            "survive",
            "."));

    String sentence = Cleanser.cleanWordsAsStr(words);
    String expected = "Schrodinger's cat sat on the time-line , who knew the cat wouldn't survive .";

    if (!expected.equals(sentence) ) {
      diff_match_patch dmp = new diff_match_patch();
      LinkedList<Diff> d = dmp.diff_main(expected, sentence);

      System.out.println("Expected=|" + expected + "|\n" +
                         "Actual  =|" + sentence + "|\n" + 
                         "Diff    =" + d);
      fail();
    }
  }
  
  public void test_CleanWordsComplex() {
    Vector<String> words = new Vector<String>( 
        Arrays.asList("HAL" , 
            "\"", 
            ",",
            "\"",
            "noted",
            "Frank",
            ",",
            "said",
            "\"",
            "that",
            "everything",
            "was",
            "going",
            "extremely",
            "well",
            ".",
            "\""));
    
    String sentence = Cleanser.cleanWordsAsStr(words); 
    String expected = "HAL \" , \" noted Frank , said \" that everything was going extremely well . \"";

    if (!expected.equals(sentence) ) {
      diff_match_patch dmp = new diff_match_patch();
      LinkedList<Diff> d = dmp.diff_main(expected, sentence);

      System.out.println("Expected=|" + expected + "|\n" +
                         "Actual  =|" + sentence + "|\n" + 
                         "Diff    =" + d);
      fail();
    }
  }
  
  public void test_CleanWordsWithNumbers() {
    Vector<String> words = new Vector<String>(
        Arrays.asList("The",
            "sun",
            "sets",
            "at",
            "6:00pm",
            ",",
            "irrespective",
            "of",
            "the",
            "mood",
            "."));    
    String sentence = Cleanser.cleanWordsAsStr(words);
    String expected = "The sun sets at 6:00pm , irrespective of the mood .";
    
    if (!expected.equals(sentence) ) {
      diff_match_patch dmp = new diff_match_patch();
      LinkedList<Diff> d = dmp.diff_main(expected, sentence);

      System.out.println("Expected=|" + expected + "|\n" +
                         "Actual  =|" + sentence + "|\n" + 
                         "Diff    =" + d);
      fail();
    }
  }
}
