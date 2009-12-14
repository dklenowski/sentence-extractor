package com.orbious.extractor;

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
 * $Id: TextParserTest.java 12 2009-12-05 11:40:44Z app $
 * <p>
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
      parser.read();
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
      parser.read();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }
    
    char[] buffer = parser.getBuffer();
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

  public void test_Parse() {
    System.out.println(System.getProperty("user.dir"));
    String fname = "src/test/testdata/17216_short.txt";
    TextParser parser = new TextParser(fname);

    Vector<String> expected = new Vector<String>( 
        Arrays.asList(
            "The Project Gutenberg EBook of Punch , or the London Charivari , Volume 1 , Complete , by Various" , 
            "This eBook is for the use of anyone anywhere at no cost and with almost no restrictions whatsoever", 
            "You may copy it , give it away or re-use it under the terms of the Project Gutenberg License included with this eBook or online at www.gutenberg.net"
        ));
            
    try {
      parser.read();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }

    parser.parse();
    Vector<String> sentences = parser.getSentencesAsStr();
    assertEquals(expected.size(), sentences.size());
    
    for ( int i = 0; i < sentences.size(); i++ ) {
      assertEquals(expected.get(i), sentences.get(i));
    }
    
  }
}
