package com.orbious.extractor.evaluator;

import java.util.HashSet;
import com.orbious.extractor.Config;
import com.orbious.extractor.Word;
import com.orbious.util.Helper;

/**
* $Id$
* <p>
* Determines if a position/word in a text buffer is considered a Suspension
* and therefore not a 
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
   * In memory list of suspensions.
   */
  private static HashSet<String> suspensions;
  
  /**
   * Constructor, initializes the <code>Suspension</code> class.
   */
  public Suspension() {
    super("Suspension");
  }
  
  /**
   * Determines if the previous word from <code>idx</code>
   * in the buffer <code>buf</code> is a suspension and therefore 
   * not a likely sentence start/end.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in buffer where evaluation begins.
   * 
   * @return    <code>true</code> if the previous word is a suspension and 
   *            therefore not a likely sentence start/end, 
   *            <code>false</code> otherwise.
   */
  public boolean evaluate(final char[] buf, int idx) {
    String wd = Word.getPreviousWord(buf, idx);
    if ( wd == null ) {
      return(false);
    }
    
    return( evaluate(wd) );
  }
  
    /**
     * Determines if the word is an suspension and therefore not a
     * likely sentence start/end.
     * 
     * @param wd  A word.
     * 
     * @return    <code>true</code> if the word is an suspension and not
     *            a likely sentence end, <code>false</code> otherwise.
     */
  public boolean evaluate(String wd) {
    if ( suspensions == null ) {
      suspensions = Helper.cvtFileToHashSet(Config.SUSPENSION_FILENAME.asStr());
    }

    return( suspensions.contains(wd) );
  }
}
