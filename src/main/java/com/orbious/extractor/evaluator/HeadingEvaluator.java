package com.orbious.extractor.evaluator;

import java.util.HashSet;
import com.orbious.extractor.Config;
import com.orbious.extractor.TextParser;
import com.orbious.extractor.util.Helper;

public class HeadingEvaluator extends Evaluator {
  
  private static HashSet< Character > sentence_ends;
  
  private static double HEADING_THRESHOLD = 0.75;
  
  static {
    sentence_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());    
  }
  
  public HeadingEvaluator(EvaluatorType type) {
    super("HeadingEvaluator", type);
  }
  
  public boolean recordAsUnlikely() {
    return(true);
  }
  
  public boolean recordAsPause() {
    return(true);
  }
  
  public boolean evaluate(char[] buf, int idx) throws Exception {  
    if ( !Character.isUpperCase(buf[idx]) ) {
      return(false);
    } else if ( !TextParser.parserData().containsLineStart(idx) ) {
      // check if we are part of a previous heading
      if ( TextParser.parserData().containsHeading(idx) ) {
        return(true);
      }
      
      return(false);
    }
    
    // move to the next line start, checking that there is no sentence
    // ends
    char ch;
    boolean fndStart = false;
    int i = idx+1;
    int letterCt = 0;
    
    debug_str.setLength(0);
    while ( i < buf.length ) {
      ch = buf[i];

      if ( sentence_ends.contains(ch) ) {
        debug_str.append(" found end at " + i + ", ");
        break;
      }
    
      if ( TextParser.parserData().containsLineStart(i) ) {
        debug_str.append(" found start at " + i + ", ");
        fndStart = true;
        break;
      }
      
      letterCt++;
      i++;
    }

    if ( !fndStart ) {
      if ( logger.isDebugEnabled() && debug_str.length() != 0 ) {
        logger.debug(debug_str);
      }
      return(false);
    }
  
    double thresh = TextParser.parserData().avgLineCharCt()*HEADING_THRESHOLD;
    if ( letterCt >= thresh ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug(debug_str + " failed threshold, letterCt=" + letterCt +  
            " threshold=" + thresh);
      }
      return(false);
    }
    
    if ( logger.isDebugEnabled() ) {
      logger.debug(debug_str + " passed threshold, letterCt=" + letterCt +  
          " threshold=" + thresh);
    }
    return(true);
  }
}
