package com.orbious.extractor.evaluator;

import com.orbious.extractor.Config;
import com.orbious.extractor.Word;
import com.orbious.util.Helper;
import java.util.HashSet;

/**
 * $Id$
 * <p>
 * Determines whether a word/position in a text buffer is considered
 * a Name and therefore not a valid sentence start/end.
 * 
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
   * Determines if the word is a common name and therefore not a sentence start.
   * 
   * @param buf    Text buffer.
   * @param idx    Position in <code>buf</code> where a to begin investigation.
   * 
   * @return    <code>true</code> if the word is a common name and not
   *            a sentence start, <code>false</code> otherwise.
   */
  public boolean evaluate(final char[] buf, int idx) {
    String wd = Word.getPreviousWord(buf, idx);
    if ( wd == null ) {
      return(false);
    }
    
    return( evaluate(wd) );
  } 
  
  
  /**
   * Determines if the word is a common name and therefore not a sentence start.
   * 
   * @param wd    A word.
   * 
   * @return  <code>true</code> if the word is an common name and not
   *          a sentence start, <code>false</code> otherwise.
   */ 
  public boolean evaluate(String wd) {
    if ( names == null ) {
      names = Helper.cvtFileToHashSet(Config.NAMES_FILENAME.asStr());
    }
    
    return( names.contains(wd) );
  }
}
