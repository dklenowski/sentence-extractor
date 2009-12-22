package com.orbious.extractor.evaluator;

import java.util.HashSet;
import com.orbious.extractor.Config;
import com.orbious.extractor.Word;
import com.orbious.util.Helper;

/**
* $Id$
* <p>
* The <code>Suspension</code> <code>Evaluator</code> determines if a word 
* is a suspension and cannot be considered a sentence start/end. i.e. 
* <ul>
* <li>A suspension can be capitalized and It can be capitalized and not be a sentence start.
* <li>It can be terminated with a fullstop and not be a sentence end.
* </ul>
* 
* @author dave
* @version 1.0
* @since 1.0
*/


public class Suspension extends Evaluator {

  /**
   * In memory list of suspension extracted from 
   * {@link Config#SUSPENSION_FILENAME}.
   */
  private static HashSet<String> suspensions;
  
  /**
   * Constructor, initializes the <code>Suspension</code> class.
   */
  public Suspension() {
    super("Suspension");
  }
  
  /**
   * Determines if the full stop is part of of an suspension and therefore
   * not a sentence end.
   * 
   * @param buf The buffer to examine.
   * @param idx The position in the buffer where punctuation occurs.
   * 
   * @return  <code>true</code> if the full stop is part of an suspension,
   *          and not a sentence end, <code>false</code> otherwise.
   */
  public boolean evaluate(final char[] buf, int idx) {
    String wd = Word.getPreviousWord(buf, idx);
    if ( wd == null ) {
      return(false);
    }
    
    return( evaluate(wd) );
  }
  
    /**
     * Determines if the word is an suspension and therefore not a sentence 
     * start.
     * 
     * @param wd  A string containing a word to check if an suspension.
     * 
     * @return  <code>true</code> if the word is an suspension and not
     *          a sentence end, <code>false</code> otherwise.
     */
  public boolean evaluate(String wd) {
    if ( suspensions == null ) {
      suspensions = Helper.cvtFileToHashSet(Config.SUSPENSION_FILENAME.asStr());
    }

    return( suspensions.contains(wd) );
  }
}
