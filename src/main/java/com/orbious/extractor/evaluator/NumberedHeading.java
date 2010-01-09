package com.orbious.extractor.evaluator;

import java.util.HashSet;

import com.orbious.extractor.TextParser;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.util.Helper;

/**
* The <code>NumberedHeading</code> <code>Evaluator</code> determines if a full stop
* is a part of a numbered heading and cannot be considered a sentence end.
*
* @author dave
* @version 1.0
* @since 1.0
*/

public class NumberedHeading extends Evaluator {

  /**
   * <code>HashSet</code> of roman numerals.
   */
  private static HashSet< Character > roman_numerals;
  
  /**
   * Local copy of lines ends from {@link TextParser#line_starts}.
   */
  private static HashSet< Integer > line_starts;
  
  /**
   * Constructor, sets the name of this <code>Evaluator</code>
   * and initializes {@link NumberedHeading#roman_numerals_set} 
   */
  public NumberedHeading() {
    super("NumberedHeading");
    roman_numerals = Helper.cvtStringToHashSet("IVXLCDM");
    line_starts = TextParserData.lineStarts();
  }
  
  /**
   * Determines if the previous word from <code>idx</code>
   * in the buffer <code>buf</code> is a numbered heading and therefore 
   * not a likely sentence end.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in buffer where evaluation begins.
   * 
   * @return    <code>true</code> if the previous word is a suspension and 
   *            therefore not a likely sentence end, 
   *            <code>false</code> otherwise.
   */
  public boolean evaluate(final char[] buf, int idx) {
    boolean b;
    
    b = evaluateNumbered(buf, idx, true);
    if ( b ) {
      return(b);
    }
    
    b = evaluateRoman(buf, idx, true);
    return(b);
  }
  
    /**
     * Determines if a 'word' is part of a numbered heading and therefore not a
     * likely sentence end.
     * 
     * @param wd  A word.
     * 
     * @return    <code>true</code> if the <code>wd</code> is a numbered heading and not
     *            a likely sentence end, <code>false</code> otherwise.
     */
  public boolean evaluate(String wd) {
    char[] buf;
    int idx;
    boolean b;
    
    // for a word, we need to move to the fullstop, if none return false
    // otherwise run the evaluations
    buf = wd.toCharArray();
    idx = -1;
    for ( int i = 0; i < buf.length; i++ ) {
     if ( buf[i] == '.' ) {
       idx = i;
       break;
     }
    }
    
    if ( idx == -1 ) {
      return(false);
    }
    
    b = evaluateNumbered(wd.toCharArray(), idx, false);
    if ( b ) {
      return(b);
    }
    
    b = evaluateRoman(wd.toCharArray(), idx, false);
    return(b);
  }
  
  /**
   * 
   * @param wd
   * @return
   */
  protected boolean evaluateNumbered(final char[] buf, int idx, boolean useLineStarts) {
    boolean fnd;
    boolean hasNonNumber;
    char ch;
    int firstIdx;

    fnd = false;
    hasNonNumber = false;
    firstIdx = -1;

    if ( buf[idx] != '.' ) {
      return(false);
    } else if ( idx == 0 ) {
      return(false);
    }
    
    for ( int i = idx-1; i >= 0; i-- ) {
      ch = buf[i];
      if ( Character.isWhitespace(ch) ) {
        firstIdx = i+1;
        break;
      }
      
      if ( !Character.isDigit(ch) ) {
        hasNonNumber = true;
        break;
      }
    }
    
    if ( useLineStarts ) {
      if ( firstIdx == -1 ) {
        firstIdx = 0;
      }
      if ( line_starts == null ) {
        line_starts = TextParserData.lineStarts();
      }
      
      if ( !hasNonNumber && line_starts.contains(firstIdx) ) {
        fnd = true;
      }
    } else {
      if ( !hasNonNumber ) {
        fnd = true;
      }
    }
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("Numbered: buf[" + idx + "]=" + buf[idx] +
          " firstIdx=" + firstIdx + 
          " hasNonNumber=" + String.valueOf(hasNonNumber).toUpperCase() + 
          " Match=" + String.valueOf(fnd).toUpperCase());
    }

    return(fnd);
  }
  
  /**
   * 
   * @param wd
   * @return
   */
  protected boolean evaluateRoman(final char[] buf, int idx, boolean useLineStarts) {
    boolean fnd;
    boolean hasNonRoman;
    char ch;
    int firstIdx;

    fnd = false;
    hasNonRoman = false;
    firstIdx = -1;
    
    if ( buf[idx] != '.' ) {
      return(false);
    } else if ( idx == 0 ) {
      return(false);
    }
    
    for ( int i = idx-1; i >= 0; i-- ) {
      ch = buf[i];
      if ( Character.isWhitespace(ch) ) {
        firstIdx = i+1;
        break;
      }
      
      if ( !roman_numerals.contains(ch) ) {
        hasNonRoman = true;
        break;
      }
    }

    if ( useLineStarts ) {
      if ( line_starts == null ) {
        line_starts = TextParserData.lineStarts();
      }
      
      if ( !hasNonRoman && line_starts.contains(firstIdx) ) {
        fnd = true;
      }
    } else {
      if ( !hasNonRoman ) {
        fnd = true;
      }
    }
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("Roman: buf[" + idx + "]=" + buf[idx] +
          " firstIdx=" + firstIdx + 
          " hasNonRoman=" + String.valueOf(hasNonRoman).toUpperCase() +          
          " Match=" + String.valueOf(fnd).toUpperCase());
    }

    return(fnd);
  }
}

