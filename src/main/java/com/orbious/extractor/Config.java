package com.orbious.extractor;

import java.util.HashSet;

/**
 * $Id: Config.java 14 2009-12-06 10:03:53Z app $
 * <p>
 * Provides constants that are shared across <code>DocumentSeparator</code>.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Config {

  /**
   * The default log4j Logging Realm.
   */
  public static String LOGGER_REALM = "SentenceExtractor";
  
  /**
   * All characters that are considered punctuation.
   */
  public static String PUNCTUATION = "!\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~";
  
  /**
   * All characters that are considered sentence ends.
   */
  public static char[] SENTENCE_ENDS = new char[] { '.', '"', '!', '?' };
  
  /**
   * Characters that should be preserved when cleansing.
   * This does not include punctuation that is part of a word (e.g. '.', '-')
   * which is preserved by default.
   */
  public static char[] PRESERVED_PUNCTUATION = 
    new char[] { '"', '!', '?',  '\'', '?', '(', ')', '$', '&', ',' };
  
  /**
   * A text file containing a list of suspensions, each on a newline.
   */
  public static String SUSPENSION_FILENAME = "resources/suspensions.txt";
  
  /**
   * A text file containing a list of common names, each on a newline.
   * (Respects the use of comments which begin a line with a "#").
   */
  public static String NAMES_FILENAME = "resources/names.txt";
  
  /**
   * Constructor, does nothing.
   */
  private Config() { }
  
  /**
   * Returns a list of valid ends (from {@link Config#SENTENCE_ENDS})
   * as a <code>HashSet</code>.
   * 
   * @return    A list of valid sentence ends as a <code>HashSet</code>.
   */ 
  public static HashSet<Character> getSentenceEnds() {
    HashSet<Character> ends = new HashSet<Character>();
    for ( int i = 0; i < SENTENCE_ENDS.length; i++ ) {
      ends.add(SENTENCE_ENDS[i]);
    }
    return(ends);
  }
  
  /**
   * Returns a list of punctuation that should be preserved in sentence
   * parsing (from {@link Config#PRESERVED_PUNCTUATION} as a <code>HashSet</code>.
   * 
   * @return    A list of punctuation that should be preserved as a 
   *        <code>HashSet</code>.
   */
  public static HashSet<Character> getPreservedPunctuation() {
    HashSet<Character> punct = new HashSet<Character>();
    for ( int i = 0; i < PRESERVED_PUNCTUATION.length; i++ ) {
      punct.add(PRESERVED_PUNCTUATION[i]);
    }
    return(punct);
  }
}
