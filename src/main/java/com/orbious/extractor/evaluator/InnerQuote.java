package com.orbious.extractor.evaluator;

import com.orbious.extractor.ParseDirn;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.util.Helper;

public class InnerQuote extends Evaluator {
  
  public InnerQuote(TextParserData parserData, EvaluatorType type) {
    super("InnerQuote", type);
  }
  
  public boolean recordAsUnlikely() {
    return(true);
  }
  
  public boolean recordAsPause() {
    return(false);
  }
  
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
