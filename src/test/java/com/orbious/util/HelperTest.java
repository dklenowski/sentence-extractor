package com.orbious.util;

// $Id: HelperTest.java 12 2009-12-05 11:40:44Z app $

import java.util.LinkedList;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

import com.orbious.AllExtractorTests;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class HelperTest extends TestCase {

  public HelperTest(String name) {
    super(name);
    AllExtractorTests.initLogger();
  }
  
  public void test_getDebugStringFromCharBuf() {
    String result;
    
    String str = "Too many people watch too much T.V.";
    char[] buf = str.toCharArray();
   
    result = Helper.getDebugStringFromCharBuf(buf, 0, 10);
    checkDiff("Too many pe\n|----------", result);

    result = Helper.getDebugStringFromCharBuf(buf, 20, 10);
    checkDiff(" watch too \n-----|-----", result);

    result = Helper.getDebugStringFromCharBuf(buf, 26, 30);
    checkDiff("ople watch too much T.V.\n---------------|--------", result);

    result = Helper.getDebugStringFromCharBuf(buf, 0, 60);
    checkDiff("Too many people watch too much T.V.\n|----------------------------------", result);
  }
  
  private void checkDiff(String expected, String actual) {
    if (!expected.equals(actual) ) {
      diff_match_patch dmp = new diff_match_patch();
      LinkedList<Diff> d = dmp.diff_main(expected, actual);

      System.out.println("Expected=" + expected + "\n" +
                         "Actual  =" + actual + "\n" + 
                         "Diff    =" + d);
      fail();
    }
  }
}
