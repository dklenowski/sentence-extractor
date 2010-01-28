package com.orbious.extractor.evaluator;

import java.util.HashSet;
import com.orbious.extractor.Config;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.util.Helper;

public class HeadingEvaluator extends Evaluator {
  
  private static HashSet< Character > sentence_ends;
  
  private static double HEADING_THRESHOLD = 0.75;
  
  public HeadingEvaluator(EvaluatorType type) {
    super("HeadingEvaluator", type);
    sentence_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());
  }
  
  public boolean recordAsUnlikely() {
    return(true);
  }
  
  public boolean evaluate(char[] buf, int idx) throws Exception {  
    if ( !Character.isUpperCase(buf[idx]) || !TextParserData.containsLineStart(idx) ) {
      // we are not at the start of of a line, so not a heading
      if ( logger.isDebugEnabled() ) {
        logger.debug(" failed initial linestarts test at idx=" + idx);
      }
      return(false);
    }
    
    // move to the next line start, checking that there is no sentence
    // ends
    char ch;
    boolean fndStart = false;
    int i = idx+1;
    int letterCt = 0;
    String debugStr;
    
    debugStr = "";
    while ( i < buf.length ) {
      ch = buf[i];

      if ( sentence_ends.contains(ch) ) {
        debugStr += " found end at " + i + ", ";
        break;
      }
    
      if ( TextParserData.containsLineStart(i) ) {
        debugStr += " found start at " + i + ", ";
        fndStart = true;
        break;
      }
      
      letterCt++;
      i++;
    }

    if ( !fndStart ) {
      if ( logger.isDebugEnabled() && debugStr.length() != 0 ) {
        logger.debug(debugStr);
      }
      return(false);
    }
  
    double thresh = TextParserData.avgLineCharCt()*HEADING_THRESHOLD;
    if ( letterCt >= thresh ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug(debugStr + " failed threshold, letterCt=" + letterCt +  
            " threshold=" + thresh);
      }
      return(false);
    }
    
    if ( logger.isDebugEnabled() ) {
      logger.debug(debugStr + " passed threshold, letterCt=" + letterCt +  
          " threshold=" + thresh);
    }
    return(true);
  }
}
