package com.orbious.extractor.util;

import java.util.LinkedList;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;

public class TextDiff {
  public static String compare(String expected, String actual) { 
    diff_match_patch dmp = new diff_match_patch();
    LinkedList<Diff> d = dmp.diff_main(expected, actual);
    
    String str = "Expected=|" + expected + "|\n" +
                 "Actual  =|" + actual + "\n" +
                 "Diff    =" + d;
    return(str);
  }
}
