package com.orbious.extractor.evaluator;

import java.util.HashSet;
import com.orbious.extractor.Config;
import com.orbious.extractor.TextParser;
import com.orbious.extractor.util.Helper;

public class InsideLeftRightMarks extends Evaluator {

  private static HashSet<Character> leftMarks;
  private static HashSet<Character> rightMarks;
  private static HashSet<Character> sentence_ends;
  
  static {
    leftMarks = Helper.cvtStringToHashSet(Config.LEFT_PUNCTUATION_MARKS.asStr());
    rightMarks = Helper.cvtStringToHashSet(Config.RIGHT_PUNCTUATION_MARKS.asStr());
    sentence_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());    
  }
  
  public InsideLeftRightMarks(EvaluatorType type) {
    super("InsideLeftRightMarks", type);
  }

  public boolean recordAsUnlikely() {
    return(false);
  }
  
  public boolean recordAsPause() {
    return(false);
  }
  
  public boolean evaluate(char[] buf, int idx) throws Exception {
    int startIdx;
    
    if ( (idx-1) < 0 ) {
      // not enough data
      return(false);
    }
    
    startIdx = TextParser.parserData().findPreviousLikelyEnd(idx);
    if ( startIdx == -1 ) {
      return(false);
    }
    startIdx++;
    
    // if there is a left mark and no right mark, assume that the 
    // punctuation is inside the marks
    char ch;
    int markCt = 0;

    for ( int j = startIdx; j < idx; j++ ) {
      ch = buf[j];
      if ( leftMarks.contains(ch) ) {
        markCt++;
      } else if ( rightMarks.contains(ch) ) {
        markCt--;
      }
    }

    if ( markCt == 0 ) {
      return(false);
    }
    
    if ( type == EvaluatorType.START ) {
      // find an end before a right mark
      return( processStart(buf, idx) );
    } else {
      return( processEnd(buf, idx) );
    }
  }
  
  private boolean processStart(final char[] buf, int idx) { 
    char ch;
    
    for ( int j = idx+1; j < buf.length; j++ ) {
      ch = buf[j];
      if ( rightMarks.contains(ch) ) {
        return(true);
      } else if ( (ch != ':') && sentence_ends.contains(ch) ) {
        return(false);
      }
    }
    
    return(false);
  }
  
  private boolean processEnd(final char[] buf, int idx) { 
    char ch;
    int j;
    
    j = idx-1;
    while ( j > 0 ) {
      ch = buf[j];
      if ( leftMarks.contains(ch) ) {
        return(true);
      } else if ( Character.isUpperCase(ch) ) {
        return(false);
      }
      j--;
    }
    
    return(false);
  }
}

