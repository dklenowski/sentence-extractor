package com.orbious.extractor.evaluator;

import com.orbious.extractor.ParseDirn;
import com.orbious.extractor.Word;
import com.orbious.extractor.Word.WordOp;
import com.orbious.extractor.util.Helper;

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
   * Constructor, set's the <code>name</code> of this <code>Evaluator</code>.
   */
  public AbbreviatedName(EvaluatorType type) {
    super("AbbreviatedName", type);
  }  
  
  public boolean recordAsUnlikely() {
    return(true);
  }
  
  public boolean recordAsPause() {
    return(false);
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
    
    if ( (type == EvaluatorType.START) && !Character.isUpperCase(buf[idx]) ) {
      return(false);
    } else if ( (type == EvaluatorType.END) && buf[idx] != '.' ) {
      return(false);
    }

    debug_str.setLength(0);
    b = evaluateLeftToRight(buf, idx);
    if ( b ) {
      if ( logger.isDebugEnabled() && (debug_str.length() != 0) ) {
        logger.debug(debug_str + 
            " RESULT=" + String.valueOf(b).toUpperCase());
      }
      return(b);
    }
    
    b = evaluateRightToLeft(buf, idx);
    
    if ( logger.isDebugEnabled() && (debug_str.length() != 0) ) {
      logger.debug(debug_str + 
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

    //
    // We have to handle 3 cases:
    // P. Petro
    // 12 3
    //
    int i;
    
    if ( logger.isDebugEnabled() ) {
      debug_str.append("LtoR: ");
    }
    
    if ( Character.isUpperCase(buf[idx]) ) {
      if ( idx+1 >= buf.length ) {
        return(false);
      }
      
      if ( buf[idx+1] == '.' ) {
        //
        // case 1
        //
        i = checkCase(buf, idx, ParseDirn.RIGHT);
        if ( i == -1 ) {
          if ( logger.isDebugEnabled() ) {
            debug_str.append("Case 1-Failed Checkcase, ");
          }
          return(false);
        } 
 
        i = Helper.moveToNonWhitespace(ParseDirn.RIGHT, buf, i+1);
        if ( (i != -1) && Character.isUpperCase(buf[i]) ) {
          if ( logger.isDebugEnabled() ) {
            debug_str.append("Case 1-TRUE, ");
          }
          return(true);
        }

        if ( logger.isDebugEnabled() ) {
          debug_str.append("Case 1-Default FALSE, ");
        }
        return(false);
        
      } else if ( Character.isLowerCase(buf[idx+1]) ) {
        //
        // case 3
        // 
        i = Helper.moveToNonWhitespace(ParseDirn.LEFT, buf, idx-1);
        if ( i == -1 ) {
          if ( logger.isDebugEnabled() ) {
            debug_str.append("Case 3-Failed move left, ");
          }
          return(false);
        } 
        
        i = checkCase(buf, idx, ParseDirn.LEFT);
        if ( i == -1 ) {
          if ( logger.isDebugEnabled() ) {
            debug_str.append("Case 1-Failed Checkcase, ");
          }
          return(false);
        } 
        
        if ( logger.isDebugEnabled() ) {
          debug_str.append("Case 3-TRUE, ");
        }
        return(true);
        
        
      } else {
        if ( logger.isDebugEnabled() ) {
          debug_str.append("Case 1,3-Default FALSE, ");
        }
        return(false);
      }
      
    } else if ( buf[idx] == '.' ) {
      //
      // case 2
      //
      i = checkCase(buf, idx, ParseDirn.LEFT);
      if ( i == -1 ) {
        if ( logger.isDebugEnabled() ) {
          debug_str.append("Case 2-Failed Checkcase, ");
        }
        return(false);
      }  
      
      i = Helper.moveToNonWhitespace(ParseDirn.RIGHT, buf, idx+1);
      if ( (i != -1) && Character.isUpperCase(buf[i]) ) {
        if ( logger.isDebugEnabled() ) {
          debug_str.append("Case 2-TRUE, ");
        }
        return(true);
      }

      if ( logger.isDebugEnabled() ) {
        debug_str.append("Case 2-Default FALSE, ");
      }
      return(false);
    }
    
    if ( logger.isDebugEnabled() ) {
      debug_str.append("Default FALSE, ");
    }
    return(false);
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
    
    //
    // 3 cases:
    // Petro P.
    // 1     23
    //
    int i;
    WordOp op;
    
    if ( logger.isDebugEnabled() ) {
      debug_str.append("RtoL: ");
    }
    
    if ( Character.isUpperCase(buf[idx]) ) {
      if ( idx+1 >= buf.length ) {
        return(false);
      }
      
      if ( buf[idx+1] == '.' ) {
        //
        // case 2
        //
        i = checkCase(buf, idx, ParseDirn.RIGHT);
        if ( i == -1 ) {
          if ( logger.isDebugEnabled() ) {
            debug_str.append("Case 2-Failed Checkcase, ");
          }
          return(false);
        }
    
        op = Word.getPreviousWord(buf, i-1, true);
        
        if ( (op == null) || (op.word().length() == 0) ) {
          if ( logger.isDebugEnabled() ) {
            debug_str.append("Case 2-Failed Word, ");
          }
          return(true);
        }

        if ( Character.isUpperCase(op.word().charAt(0)) ) {
          if ( logger.isDebugEnabled() ) {
            debug_str.append("Case 2-TRUE, ");
          }
          return(true);
        }
        
      } else if ( Character.isLowerCase(buf[idx+1] ) ) {
        //
        // case 1
        //
        i = idx+1;
        while ( (i < buf.length) && !Character.isWhitespace(buf[i]) ) {
          i++;
        }
        
        i = Helper.moveToNonWhitespace(ParseDirn.RIGHT, buf, i);
        if ( i == -1 ) {
          if ( logger.isDebugEnabled() ) {
            debug_str.append("Case 1-Failed Move, ");
          }
          return(false);
        }
        
        i = checkCase(buf, i, ParseDirn.RIGHT);
        if ( i == -1 ) {
          if ( logger.isDebugEnabled() ) {
            debug_str.append("Case 1-Failed Checkcase, ");
          }
          return(false);
        }
        
        if ( logger.isDebugEnabled() ) {
          debug_str.append("Case 1-TRUE, ");
        }
        return(true);
      }
  
    } else if ( buf[idx] == '.' ) {
      // 
      // case 3
      // 
      i = checkCase(buf, idx, ParseDirn.LEFT);
      if ( i == -1 ) {
        if ( logger.isDebugEnabled() ) {
          debug_str.append("Case 3-Failed Checkcase, ");
        }
        return(false);
      }
      
      op = Word.getPreviousWord(buf, idx-1, true);

      if ( (op == null) || (op.word().length() == 0) ) {
        if ( logger.isDebugEnabled() ) {
          debug_str.append("Case 3-Failed Word, ");
        }
        return(true);
      }

      if ( Character.isUpperCase(op.word().charAt(0)) ) {
        if ( logger.isDebugEnabled() ) {
          debug_str.append("Case 3-TRUE, ");
        }
        return(true);
      }
    }
    
    if ( logger.isDebugEnabled() ) {
      debug_str.append("Default FALSE, ");
    }
    return(false);
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
  protected int checkCase(final char[] buf, int idx, ParseDirn dirn) {
    char ch;
    int i;
    int inc;
    boolean nxtUpperCase;
    boolean match;

    if ( dirn == ParseDirn.LEFT ) {
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
    
    if ( (i < 0) || (i >= buf.length) ) {
      return(-1);
    }
 
    while ( Character.isWhitespace(buf[i]) ) {
      i += inc;
      if ( (i < 0) || (i >= buf.length) ) {
        return(-1);
      }
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
    
    if ( !match ) {
      return(-1);
    }
    
    if ( dirn == ParseDirn.LEFT ) {
      i++;
    } else {
      i--;
    }
    return(i);
  }
}
