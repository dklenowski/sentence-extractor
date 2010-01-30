package com.orbious.extractor.evaluator;

// $Id$

import java.io.FileNotFoundException;
import java.util.HashSet;
import com.orbious.extractor.Config;
import com.orbious.extractor.Word;
import com.orbious.extractor.Word.WordOp;
import com.orbious.extractor.util.Helper;

/**
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
  public Suspension(EvaluatorType type) {
    super("Suspension", type);
  }
  
  public boolean recordAsUnlikely() {
    return(true);
  }
  
  public boolean recordAsPause() {
    return(false);
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
  public boolean evaluate(final char[] buf, int idx) throws FileNotFoundException {
    WordOp op;
    
    if ( type == EvaluatorType.START ) {
      if ( !Character.isUpperCase(buf[idx]) ) {
        return(false);
      } 
      if ( idx != 0 ) {
        idx--;
      }
      op = Word.getNextWord(buf, idx, true);
    } else {
      if ( buf[idx] != '.' ) {
        return(false);
      }
      
      if ( idx+1 < buf.length ) {
        idx++;
      }
      op = Word.getPreviousWord(buf, idx, true);
    }

    if ( op == null ) {
      return(false);
    }

    if ( suspensions == null ) {
      suspensions = Helper.cvtFileToHashSet(Config.SUSPENSION_FILENAME.asStr(), true);
    }

    return( suspensions.contains(op.word().toLowerCase()) );
  }
}
