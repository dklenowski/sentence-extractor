package com.orbious.extractor;

// $Id: TextParserTest.java 12 2009-12-05 11:40:44Z app $

import java.io.FileNotFoundException;
import java.io.IOException;
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

public class TextParserTest extends TestCase {

  public TextParserTest(String name) {
    super(name);
    AllTests.initLogger();
  }

  public void test_readWithException() {
    String fname = "asdfasdf";
    TextParser parser = new TextParser(fname);
    
    try {
      parser.parse();
      fail("No FileNotFoundException thrown");
    } catch ( FileNotFoundException fnfe ) { 
      
    } catch (IOException ioe ) {
      fail("Wrong Exception (IOException) thrown.");
    }
  }

  public void test_Read() {
    System.out.println(System.getProperty("user.dir"));
    String fname = "src/test/testdata/17216_short.txt";
    TextParser parser = new TextParser(fname);
    
    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }
    
    char[] buffer = parser.buffer();
    String str = String.copyValueOf(buffer);
    
    String expected = "The Project Gutenberg EBook of Punch, or the London Charivari, Volume 1, " +
    "Complete, by Various. This eBook is for the use of anyone anywhere at no " +
    "cost and with almost no restrictions whatsoever. You may copy it, give " +
    "it away or re-use it under the terms of the Project Gutenberg License " +
    "included with this eBook or online at www.gutenberg.net . " +
    "Title: Punch, or the London Charivari, Volume 1, " +
    "Complete Author: Various Release Date: December 4, " +
    "2005 [EBook #17216] Language: English Character set encoding: " +
    "ASCII *** START OF THIS PROJECT GUTENBERG EBOOK PUNCH, VOLUME 1 *** ";

    if ( !expected.equals(str) ) {
      diff_match_patch dmp = new diff_match_patch();
      LinkedList<Diff> d = dmp.diff_main(expected, str);

      System.out.println("Expected=|" + expected + "|\n" +
                         "Actual  =|" + str + "|\n" + 
                         "Diff    =" + d);
      fail();
    }
  }

  public void test_GenSentences() {
    System.out.println(System.getProperty("user.dir"));
    String fname = "src/test/testdata/17216_short.txt";
    TextParser parser = new TextParser(fname);

    Vector<String> expected = new Vector<String>( 
        Arrays.asList(
            "The Project Gutenberg EBook of Punch , or the London Charivari , Volume 1 , Complete , by Various ." , 
            "This eBook is for the use of anyone anywhere at no cost and with almost no restrictions whatsoever .", 
            "You may copy it , give it away or re-use it under the terms of the Project Gutenberg License included with this eBook or online at www.gutenberg.net ."
        ));
            
    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }

    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr();
    assertEquals(expected.size(), sentences.size());
    
    for ( int i = 0; i < sentences.size(); i++ ) {
      assertEquals(expected.get(i), sentences.get(i));
    }
  }

  public void test_GenSentences2() {
    String fname = "src/test/testdata/17216_short2.txt";
    TextParser parser = new TextParser(fname);

    Vector<String> expected = new Vector<String>( 
        Arrays.asList(
            "MUSIC AND THE DRAMA .",
            "These are amongst the most prominent features of the work ." , 
            "The Musical Notices are written by the gentleman who plays the mouth-organ , assisted by the professors of the drum and cymbals .", 
            "\" Punch \" himself _ does _ the Drama .",
            "A Prophet is engaged !",
            "He foretells not only the winners of each race , but also the \" VATES \" and colours of the riders .",
            "THE FACETIAE Are contributed by the members of the following learned bodies:--THE COURT OF COMMON COUNCIL AND THE ZOOLOGICAL SOCIETY:--THE TEMPERANCE ASSOCIATION AND THE WATERPROOFING COMPANY:--THE COLLEGE OF PHYSICIANS AND THE HIGHGATE CEMETERY:--THE DRAMATIC AUTHORS' AND THE MENDICITY SOCIETIES:--THE BEEFSTEAK CLUB AND THE ANTI-DRY-ROT COMPANY .",
            "Together with original , humorous , and satirical articles in verse and prose , from all the [ Illustration: FUNNY DOGS WITH COMIC TALES . ]"
        ));
            
    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }

    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr();
    
    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
     if ( !expected.get(i).equals(sentences.get(i)) ) {
        diff_match_patch dmp = new diff_match_patch();
        LinkedList<Diff> d = dmp.diff_main(expected.get(i), sentences.get(i));

        System.out.println("Expected=|" + expected.get(i) + "|\n" +
                           "Actual  =|" + sentences.get(i) + "|\n" + 
                           "Diff    =" + d);
        fail();
      }
    }    
  }
}
