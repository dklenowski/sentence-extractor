package com.orbious.extractor.evaluator;

import com.orbious.extractor.Config;
import com.orbious.extractor.Word;
import com.orbious.util.Helper;
import java.util.HashSet;

/**
 * $Id$
 * <p>
 * Determines whether a position/word in a text buffer is considered
 * a Name and therefore not a likely sentence start. This class should only 
 * be used for evaluating the start of a sentence.
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Name extends Evaluator {

  /**
   * In memory list of names.
   */
  private static HashSet<String> names;
  
  /**
   * Constructor, set's the <code>name</code> of this <code>Evaluator</code>.
   */
  public Name() {
    super("Name");
  }
  
  /**
   * Determines if the previous word from <code>idx</code>
   * in the buffer <code>buf</code> is a common name and therefore not a
   * likely sentence start.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in <code>buf</code> where evaluation begins.
   * 
   * @return    <code>true</code> if the word is a common name and not
   *            a likely sentence start, <code>false</code> otherwise.
   */
  public boolean evaluate(final char[] buf, int idx) {
    String wd = Word.getPreviousWord(buf, idx);
    if ( wd == null ) {
      return(false);
    }
    
    return( evaluate(wd) );
  } 
  
  
  /**
   * Determines if the word is a common name and therefore not a
   * likely sentence start.
   * 
   * @param wd    A word.
   * 
   * @return    <code>true</code> if the word is an common name and not
   *            a likely sentence start, <code>false</code> otherwise.
   */ 
  public boolean evaluate(String wd) {
    if ( names == null ) {
      names = Helper.cvtFileToHashSet(Config.NAMES_FILENAME.asStr());
    }
    
    return( names.contains(wd) );
  }
}
