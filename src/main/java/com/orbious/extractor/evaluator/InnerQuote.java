package com.orbious.extractor.evaluator;

// $Id$

import com.orbious.extractor.ParseDirn;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.util.Helper;

/**
 * Used as an end <code>Evaluator</code> to determine if a sentence end
 * is inside an inner quote (and therefore not a likely sentence end).
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class InnerQuote extends Evaluator {
  
  /**
   * Constructor, initializes this <code>Evaluator</code>.
   * 
   * @param parserData  Data generating during <code>TextParser</code> parsing.
   * @param type    The type of <code>Evaluator</code>.
   */
  public InnerQuote(TextParserData parserData, EvaluatorType type) {
    super("InnerQuote", type);
  }
  
  /**
   * Return's <code>true</code> as it is possible that a sentence end
   * exists inside an inner quote.
   */
  public boolean recordAsUnlikely() {
    return(true);
  }
  
  /**
   * Return's <code>false</code>.
   */
  public boolean recordAsPause() {
    return(false);
  }
  
  /**
   * Determines if the current characters are part of an Inner Quote and
   * therefore not a likely sentence end.
   */
  public boolean evaluate(final char[] buf, int idx) {
    if ( (idx < 0) || (idx >= buf.length) ) {
      throw new ArrayIndexOutOfBoundsException("Invalid index=" + idx);
    }
    
    if ( buf[idx] != '"' ) { 
      return(false);
    }
    
    int prevIdx =Helper.moveToNonWhitespace(ParseDirn.LEFT, buf, idx);
    if ( prevIdx < 0 ) {
      return(false);
    } else if ( buf[prevIdx] == ':' ) {
      return(true);
    }
    
    return(false);
  }

}
