package com.orbious.extractor.evaluator;

import com.orbious.extractor.Word;
import com.orbious.extractor.Word.WordOp;

// $Id$


/**
 * AbbreviatedName is used an an sentence end <code>Evaluator</code> to identify
 * non sentence ends like:
 * <p>
 * W.H.D. Rouse<br>
 * M.A. Little<br>
 * B. Thomas<br>
 * <p>
 * or the reverse 
 * <p>
 * Rouse W.H.D<br>
 * Little M.A.<br>
 * Thomas B.<br>
 * <p>
 * Note that some of these names may also be picked up by 
 * {@link Acronym}.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class AbbreviatedName extends Evaluator {

  /**
   * Direction of traversal, used in 
   * {@link AbbreviatedName#checkCase(char[], int, DIRN, boolean)}.
   */
  private enum DIRN { LEFT, RIGHT };
  
  /**
   * Debugging string.
   */
  private String debugStr;
  
  /**
   * Constructor, set's the <code>name</code> of this <code>Evaluator</code>.
   */
  public AbbreviatedName() {
    super("AbbreviatedName");
  }
  
  /**
   * Determines if the current characters are part of an Abbreviated Name
   * and therefore not a likely sentence start/end.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in <code>buf</code> where evaluation begins.
   * 
   * @return    <code>true</code> if the text is part of an abbreviated name,
   *            and therefore not a likely sentence start/end, 
   *            <code>false</code> otherwise.
   */
  public boolean evaluate(final char[] buf, int idx) {
    boolean b;
    
    if ( buf[idx] != '.' && !Character.isUpperCase(buf[idx]) ) {
      return(false);
    }
    
    debugStr = "";
    b = evaluateLeftToRight(buf, idx);
    if ( b ) {
      if ( logger.isDebugEnabled() && (debugStr.length() != 0) ) {
        logger.debug("AbbreviatedName:" + debugStr + 
            " RESULT=" + String.valueOf(b).toUpperCase());
      }
      return(b);
    }
    
    b = evaluateRightToLeft(buf, idx);
    
    if ( logger.isDebugEnabled() && (debugStr.length() != 0) ) {
      logger.debug("AbbreviatedName:" + debugStr + 
          " RESULT=" + String.valueOf(b).toUpperCase());
    }
    
    return(b);
  }
  
  /**
   * Tries to find abbreviated names of the format.
   * <p>
   * [INITIALS] [SURNAME] 
   * <p>
   * @param buf   Text buffer.
   * @param idx   Position in the <code>buf</code> to begin examination.
   * 
   * @return    <code>true</code> if an abbreviated name was found,
   *            <code>false</code> otherwise.
   */
  protected boolean evaluateLeftToRight(final char[] buf, int idx) {
    if ( (idx-1 < 0) && (idx+1 >= buf.length) ) {
      // there is not enough data in the buf to perform an evaluation
      return(false);
    } else if ( (buf[idx] != '.') && !Character.isUpperCase(buf[idx]) ) {
      return(false);
    }

    int i;
    
    // check the previous characters are part of the abbreviated name
    if ( idx-1 > 0 ) {
      i = checkCase(buf, idx, DIRN.LEFT, false);
      if ( i == -1 ) {
        if ( logger.isDebugEnabled() ) {
          debugStr += " failed left (evaluateLeftToRight).";
        }
        return(false);
      }
    }
    
    // now check the next characters
    i = checkCase(buf, idx, DIRN.RIGHT, true);
    if ( i == -1 ) {
      if ( logger.isDebugEnabled() ) {
        debugStr += " failed right (evaluateLeftToRight)";
      }
      return(false);
    }
    
    // we now need to check the idx return from checkCase is an uppercase
    i++;
    while ( (i < buf.length) && Character.isWhitespace(buf[i]) ) {
      i++;
    }
    
    if ( (i >= buf.length) || !Character.isUpperCase(buf[i]) ) {
      if ( logger.isDebugEnabled() ) {
        debugStr += " failed uppercase (evaluateLeftToRight)";
      }
      return(false);
    }
    
    if ( logger.isDebugEnabled() ) {
      debugStr += " passed evaluateLeftToRight";
    }
    return(true);
  }
  
  /**
   * Tries to find abbreviated names of the format.
   * <p>
   * [SURNAME] [INITIALS] 
   * <p>
   * @param buf   Text buffer.
   * @param idx   Position in the <code>buf</code> to begin examination.
   * 
   * @return    <code>true</code> if an abbreviated name was found,
   *            <code>false</code> otherwise.
   */
  protected boolean evaluateRightToLeft(final char[] buf, int idx) {
    if ( (idx-1 < 0) && (idx+1 >= buf.length) ) {
      // there is not enough data in the buf to perform an evaluation
      return(false);
    } else if ( (buf[idx] != '.') && !Character.isUpperCase(buf[idx]) ) {
      return(false);
    }

    int i;
    
    if ( idx+1 < buf.length ) {
      i = checkCase(buf, idx, DIRN.RIGHT, false);
      if ( i == -1 ) {
        if ( logger.isDebugEnabled() ) {
          debugStr += " failed right (evaluateRightToLeft).";
        }
        return(false);
      }  

      if ( logger.isDebugEnabled() ) {
        debugStr += " RIGHT idx=" + i;
      }
    }
    
    i = checkCase(buf, idx, DIRN.LEFT, true);
    if ( i == -1 ) {
      if ( logger.isDebugEnabled() ) {
        debugStr += " failed left (evaluateRightToLeft).";
      }
      return(false);
    }
    
    if ( logger.isDebugEnabled() ) {
      debugStr += " LEFT idx=" + i;
    }
    
    // we now need to check the idx return from checkCase is an uppercase
    // at the start of the previous word
    i--;
    
    WordOp op = Word.getPreviousWord(buf, i, true);
    if ( (op == null) || !Character.isUpperCase(op.word().charAt(0)) ) {
      if ( logger.isDebugEnabled() ) {
        debugStr += " failed uppercase (evaluateRightToLeft)";
      }
      return(false);
    }
    
    if ( logger.isDebugEnabled() ) {
      debugStr += " passed evaluateRightToLeft";
    }
    return(true);
  }
  
  /**
   * Checks the case for an abbreviated name. 
   * 
   * @param buf   Text buffer.
   * @param idx   Position in the buffer to begin examination.
   * @param dirn  Direction of traversal.
   * 
   * @return    <code>true</code> if the case is correct for an abbreviated name,
   *             <code>false</code> otherwise.
   */
  protected int checkCase(final char[] buf, int idx, DIRN dirn, boolean ignoreMatch) {
    char ch;
    int i;
    int inc;
    boolean nxtUpperCase;
    boolean match;

    if ( dirn == DIRN.LEFT ) {
      inc = -1;
    } else {
      inc = 1;
    }
    
    i = idx+inc;
    if ( buf[idx] == '.' ) {
      nxtUpperCase = true;
    } else {
      nxtUpperCase = false;
    }
    
    match = false;
    while ( (i >= 0) && (i < buf.length) && !Character.isWhitespace(buf[i]) ) {
      ch = buf[i];
      if ( nxtUpperCase ) {
        // we are looking for an uppercase character
        if ( !Character.isUpperCase(ch) ) {
          return(-1);
        }
        nxtUpperCase = false;
      } else {
        // we are looking for a fullstop
        if ( ch != '.' ) {
          return(-1);
        }
        nxtUpperCase = true;
      }
      
      // if we get to here we have at least 1 match
      match = true;
      i += inc;
    }
    
    if ( !ignoreMatch && !match ) {
      return(-1);
    }
    
    if ( dirn == DIRN.LEFT ) {
      i++;
    } else {
      i--;
    }
    return(i);
  }
}
