package com.orbious.extractor;

import java.util.Vector;

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
}