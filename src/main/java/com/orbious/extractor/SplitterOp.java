package com.orbious.extractor;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An class used to store data related to the extraction of words
 * for a sentence.
 *
 * @author dave
 * @version 1.0
 * @since 1.0
 */
public class SplitterOp {

  /**
   * The words constituting a sentence.
   */
  private Vector<String> words;

  /**
   * A count of alphanumeric words within <code>words</code>. This is required
   * because punctuation occupies its own position within the
   * <code>words</code> vector.
   */
  private int wordCt;

  /**
   * A pattern for returning words without punctuation.
   */
  private static final Pattern alpha = Pattern.compile("[0-9a-zA-Z]");

  /**
   * Constructor, initialize the <code>ExtractionOp<code> object.
   *
   * @param words   The words constituting a sentence.
   * @param wordCt  A count of alphanumeric words within <code>words</code>.
   */
  public SplitterOp(Vector<String> words, int wordCt) {
    this.words = words;
    this.wordCt = wordCt;
  }

  /**
   * Accessor for <code>words</code>.
   *
   * @return    The <code>words</code>.
   */
  public Vector<String> words() {
    return(words);
  }

  /**
   * Accessor for <code>wordCt</code>.
   *
   * @return    The <code>wordCt</code>.
   */
  public int wordCt() {
    return(wordCt);
  }

  public Vector<String> wordsWithoutPunct() {
    return Utils.wordsWithoutPunct(words);
  }

  public Vector<String> wordsWithoutCase() {
    return Utils.wordsWithoutCase(words);
  }

  public Vector<String> wordsWithoutPunctAndCase() {
    return Utils.wordsWithoutPunctAndCase(words);
  }

  /**
   * Helper methods.
   *
   * @author dave
   */
  public static class Utils {

    /**
     *
     * @param wds
     * @return
     */
    public static Vector<String> wordsWithoutPunct(Vector<String> wds) {
      Matcher m;
      String str;
      Vector<String> w;

      w = new Vector<String>(wds.size());
      for ( int i = 0; i < wds.size(); i++ ) {
        str = wds.get(i);
        m = alpha.matcher(str);
        if ( m.find() ) {
          w.add(str);
        }
      }

      return w;
    }

    /**
     *
     * @return
     */
    public static Vector<String> wordsWithoutCase(Vector<String> wds) {
      Vector<String> w;

      w = new Vector<String>(wds.size());
      for ( int i = 0; i < wds.size(); i++ ) {
        w.add(wds.get(i).toLowerCase());
      }

      return w;
    }

    /**
     *
     * @return
     */
    public static Vector<String> wordsWithoutPunctAndCase(Vector<String> wds) {
      Matcher m;
      String str;
      Vector<String> w;

      w = new Vector<String>(wds.size());
      for ( int i = 0; i < wds.size(); i++ ) {
        str = wds.get(i);
        m = alpha.matcher(str);
        if ( m.find() ) {
          w.add(str.toLowerCase());
        }
      }

      return w;
    }
  }
}