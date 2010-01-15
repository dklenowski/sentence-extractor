package com.orbious.extractor.evaluator;

import com.orbious.util.Helper;

public class InnerQuote extends Evaluator {
  
  public InnerQuote() {
    super("InnerQuote");
  }
  
  public boolean evaluate(final char[] buf, int idx) {
    if ( (idx < 0) || (idx >= buf.length) ) {
      throw new ArrayIndexOutOfBoundsException("Invalid index=" + idx);
    }
    
    if ( buf[idx] != '"' ) { 
      return(false);
    }
    
    int prevIdx =Helper.moveToNonWhitespace(Helper.DIRN.LEFT, buf, idx);
    if ( prevIdx < 0 ) {
      return(false);
    } else if ( buf[prevIdx] == ':' ) {
      return(true);
    }
    
    return(false);
  }

}
