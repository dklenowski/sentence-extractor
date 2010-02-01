package com.orbious.extractor.evaluator;

// $Id$

import java.util.HashSet;
import com.orbious.extractor.Config;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.util.Helper;

/**
 * Used as both a sentence start and end <code>Evaluator</code> to determine
 * whether the start/end exists inside left/right punctuation marks.
 * If so, the text is not considered a sentence start/end.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class InsideLeftRightMarks extends Evaluator {

  /**
   * List of allowable sentence ends (see {@link Config#SENTENCE_ENDS}).
   */
  private static HashSet<Character> sentence_ends;
  
  /**
   * List of left punctuation marks (see {@link Config#LEFT_PUNCTUATION_MARKS}).
   */
  private static HashSet<Character> leftMarks;
  
  /**
   * List of right punctuation marks (see {@link Config#RIGHT_PUNCTUATION_MARKS}).
   */
  private static HashSet<Character> rightMarks;

  
  static {
    leftMarks = Helper.cvtStringToHashSet(Config.LEFT_PUNCTUATION_MARKS.asStr());
    rightMarks = Helper.cvtStringToHashSet(Config.RIGHT_PUNCTUATION_MARKS.asStr());
    sentence_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());    
  }
  
  /**
   * Constructor, initializes this <code>Evaluator</code>.
   * 
   * @param parserData  Data generating during <code>TextParser</code> parsing.
   * @param type    The type of <code>Evaluator</code>.
   */
  public InsideLeftRightMarks(TextParserData parserData, EvaluatorType type) {
    super("InsideLeftRightMarks", parserData, type);
  }

  /**
   * Return's <code>false</code>.
   */
  public boolean recordAsUnlikely() {
    return(false);
  }
  
  /**
   * Return's <code>false</code>.
   */
  public boolean recordAsPause() {
    return(false);
  }
  
  /**
   * Determines if the current characters are between left and right punctuation marks
   * and therefore not a likely sentence start/end.
   */
  public boolean evaluate(char[] buf, int idx) throws Exception {
    int startIdx;
    
    if ( (idx-1) < 0 ) {
      // not enough data
      return(false);
    }
    
    startIdx = parser_data.findPreviousLikelyEnd(idx);
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
  
  /**
   * Checks if a right punctuation mark exists between
   * position <code>idx</code> in the character buffer <code>buf</code>
   * and a sentence end.
   * 
   * @param buf   Text buffer.
   * @param idx   Position <code>idx</code> in <code>buf</code>.
   * 
   * @return    <code>true</code> if a right punctuation mark is encountered 
   *            before a sentence end, <code>false</code> otherwise.
   */
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
  
  /**
   * Checks if a left punctuation mark exists between
   * position <code>idx</code> in the character buffer <code>buf</code>
   * and a sentence start.
   * 
   * @param buf   Text buffer.
   * @param idx   Position <code>idx</code> in <code>buf</code>.
   * 
   * @return    <code>true</code> if a left punctuation mark is encountered 
   *            before a sentence start, <code>false</code> otherwise.
   */
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

