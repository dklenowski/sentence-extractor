package com.orbious.extractor;

/**
 * $Id$
 * <p>
 * A <code>SentenceException</code> exception occurs when:
 * <ul>
 * <li>The <code>index</code> where the sentence extraction begins is not 
 * punctuation.
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class SentenceException extends Exception {
  
  /**
   * Version Identifier
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   * 
   * @param msg   A message that identifies the <code>AcronymException</code>.
   */
  public SentenceException(String msg) {
    super(msg);
  }
}