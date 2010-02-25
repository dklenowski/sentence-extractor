package com.orbious.extractor;

import java.util.HashSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import com.orbious.extractor.evaluator.UrlText;
import com.orbious.extractor.evaluator.Evaluator.EvaluatorType;
import com.orbious.extractor.util.Helper;

public class SentenceSplitter {

  /**
   * The {@link TextParser} that is using this <code>SentenceSplitter</code>.
   */
  private TextParser parser;
  
  /**
   * List of inner punctuation. (see {@link Config#INNER_PUNCTUATION}).
   */
  private HashSet<Character> inner_punctuation;
  
  /**
   * List of preserved punctuation (see {@link Config#PRESERVED_PUNCTUATION}).
   */
  private HashSet<Character> preserved_punctuation;
  
  /**
   * List of left punctuation marks (see {@link Config#LEFT_PUNCTUATION_MARKS}).
   */
  private HashSet<Character> left_marks;
  
  /**
   * List of right punctuation marks (see {@link Config#RIGHT_PUNCTUATION_MARKS}).
   */
  private HashSet<Character> right_marks;

  /**
   * List of allowable sentence ends (see {@link Config#SENTENCE_ENDS}).
   */
  private HashSet<Character> sentence_ends;
  
  /**
   * Logger object.
   */
  private Logger logger;
  
  public SentenceSplitter(TextParser parser) {
    this.parser = parser;
    
    inner_punctuation = Helper.cvtStringToHashSet(Config.INNER_PUNCTUATION.asStr());
    preserved_punctuation = Helper.cvtStringToHashSet(Config.PRESERVED_PUNCTUATION.asStr());
    
    left_marks = Helper.cvtStringToHashSet(Config.LEFT_PUNCTUATION_MARKS.asStr());
    right_marks = Helper.cvtStringToHashSet(Config.RIGHT_PUNCTUATION_MARKS.asStr());
    
    sentence_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());

    logger = Logger.getLogger(Config.LOGGER_REALM.asStr());
  }

  /**
   * Extract a <code>Vector</code> of words from {@link TextParser#buffer}.
   * 
   * @param op  A <code>TextParserOp</code> contain the start/end indexes
   *            for extraction.
   *            
   * @return    A <code>ExtractionOp</code> containing the <code>Vector</code>
   *            of words and the <code>wordCt</code>.
   */
  protected SplitterOp split(TextParserOp op) {
    Vector<String> words;
    int startIdx;
    int endIdx;
    int wordCt;
    String wd;
    char ch;
    IndexAdjustment indexAdjustment;
    boolean hasAlpha;
    boolean hasLetter;
    boolean doAsNewWord;

    startIdx = op.start();
    endIdx = op.end();
    indexAdjustment = adjustIndexes(startIdx, endIdx);
    words = new Vector<String>();
    wd = "";
    hasAlpha = false;
    hasLetter = false;
    wordCt = 0;
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("Beginning extract startIdx=" + startIdx + 
          " adjustedStartIdx=" + indexAdjustment.adjustedStartIdx() +
          " endIdx=" + endIdx +
          " adjustedEndIdx=" + indexAdjustment.adjustedEndIdx());
    }

    for ( int i = indexAdjustment.adjustedStartIdx(); i <= indexAdjustment.adjustedEndIdx(); i++ ) {
      ch = parser.buffer[i];

      if ( Character.isLetterOrDigit(ch) ) {
        wd += ch;
        hasAlpha = true;
        if ( Character.isLetter(ch) ) {
          hasLetter = true;
        }
      } else if ( Character.isWhitespace(ch) ) {
        if ( wd.length() == 0 ) {
          continue;
        }
        
        words.add(wd);
        wd = "";
        if ( hasLetter ) {
          wordCt++;
        }
        hasAlpha = false;
        hasLetter = false;
        
      } else {
        // punctuation
        doAsNewWord = false;
        
        if ( hasAlpha || Helper.isPreviousLetter(parser.buffer, i) ) {
          if ( (i < startIdx) || (i >= endIdx) ) {
            doAsNewWord = true;
          } else {
            if ( inner_punctuation.contains(ch) ) {
              // punctuation attached to the word.
              wd += ch;
            } else if ( (ch == '.') && 
                new UrlText(parser.parser_data, EvaluatorType.END).evaluate(parser.buffer, i) ) {
              
              // web address 
              wd += ch;
            } else if ( (ch == ',') && Helper.isPreviousNumber(parser.buffer, i) &&
                Helper.isNextNumber(parser.buffer, i) ) {
              // thousands separator
              wd += ch;
            } else if ( preserved_punctuation.contains(ch) ) {
              // we preserve this punctuation
              doAsNewWord = true;
            }
          }
        } else if ( preserved_punctuation.contains(ch) ) {
          doAsNewWord = true;
        }
          
        if ( doAsNewWord ) {
          if ( wd.length() != 0 ) {
            words.add(wd);
            wd = "";
            if ( hasLetter ) {
              wordCt++;
            }
          }
          
          words.add(Character.toString(ch));
          hasAlpha = false;
          hasLetter = false;
        }
      }
    }
    
    if ( hasAlpha ) {
      words.add(wd);
      if ( hasLetter ) {
        wordCt++;
      }
    }
    
    // we need to run a final check and join punctuation
    Vector<String> cleanwords = new Vector<String>();
    int p = 0;
    boolean skipMunge;
    StringBuilder tmpwd;
    
    while ( p < words.size() ) {
      wd = words.get(p);
      p++;
      skipMunge = false;
      
      if ( (wd.length() != 1) ||
          !preserved_punctuation.contains(wd.charAt(0)) ||
          (p+1 >= words.size()) ) {
        skipMunge = true;
      }
      
      if ( skipMunge ) {
        cleanwords.add(wd);
      } else {
        tmpwd = new StringBuilder(wd);
        while ( p < words.size() ) {
          wd = words.get(p);
          if ( (wd.length() != 1) || sentence_ends.contains(wd.charAt(0)) || 
              !preserved_punctuation.contains(wd.charAt(0)) ) {
            break;
          } else {
            tmpwd.append(wd);
            p++;
          }
        }
        cleanwords.add(tmpwd.toString());
      }
    }
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("Extracted = " + Helper.cvtVectorToString(cleanwords));
    }
    
    return( new SplitterOp(cleanwords, wordCt) );
  }
  
  /**
   * Adjusts the <code>startIdx</code> and <code>endIdx</code> to capture
   * any punctuation that is part of the sentence.
   * 
   * @param startIdx    Position in <code>buffer</code> where the start of a
   *                    sentence begins.
   * @param endIdx      Position in <code>buffer</code> where the end of a 
   *                    sentence begins.
   *                    
   * @return    An <code>IndexAdjustment</code> containing adjusted start/end
   *            indexes if adjustment was required, otherwise returns
   *            the <code>startIdx</code>, <code>endIdx</code>.
   */
  protected IndexAdjustment adjustIndexes(int startIdx, int endIdx) {
    IndexAdjustment adjustment;
    int nxtIdx;
    int adjustedStartIdx;
    int adjustedEndIdx;
    boolean adjustedLeft;
    
    adjustment = new IndexAdjustment();
    
    // check the start
    //
    adjustedLeft = false;
    
    if ( startIdx == 0 ) {
      adjustment.adjustedStartIdx(0);
    } else {
      nxtIdx = startIdx-1;
      adjustedStartIdx = nxtIdx;
  
       while ( (adjustedStartIdx > 0) &&
           !parser.extraction_map[adjustedStartIdx] &&
           (Character.isWhitespace(parser.buffer[adjustedStartIdx]) ||
               left_marks.contains(parser.buffer[adjustedStartIdx]) ||
               parser.buffer[adjustedStartIdx] == '\"') ) {
          adjustedStartIdx--;
      }
        
      if ( adjustedStartIdx == nxtIdx ) {
        adjustment.adjustedStartIdx(startIdx);
      } else {
        adjustedLeft = true;
        adjustedStartIdx++;
        adjustment.adjustedStartIdx(adjustedStartIdx);
        for ( int i = adjustedStartIdx; i < startIdx; i++ ) {
          parser.extraction_map[i] = true;
        }
      }
    }

    int ct = 0;
    if ( adjustedLeft ) {
      ct++;
    }
    
    for ( int i = startIdx; i < endIdx; i++ ) {
      if ( left_marks.contains(parser.buffer[i]) ) {
        ct++;
      } else if ( right_marks.contains(parser.buffer[i]) ) {
        ct--;
      }
    }

    if ( ct != 0 ) {
      adjustedLeft = true;
    }
    // now check the end
    //
    if ( endIdx == parser.buffer.length-1 ) {
      adjustment.adjustedEndIdx(endIdx);
    } else {
      nxtIdx = endIdx+1;
      adjustedEndIdx = nxtIdx;

      while ( (adjustedEndIdx < parser.buffer.length) &&
          !parser.extraction_map[adjustedEndIdx] &&
          (   sentence_ends.contains(parser.buffer[adjustedEndIdx]) ||
              (adjustedLeft &&
              (Character.isWhitespace(parser.buffer[adjustedEndIdx]) ||
                  right_marks.contains(parser.buffer[adjustedEndIdx]) ||
                  parser.buffer[adjustedEndIdx] == '\"'))   ) ) {
        adjustedEndIdx++;
      }
      
      if ( adjustedEndIdx == nxtIdx ) {
        adjustment.adjustedEndIdx(endIdx);
      } else {
        adjustedEndIdx--;
        adjustment.adjustedEndIdx(adjustedEndIdx);
        for ( int i = endIdx+1; i <= adjustedEndIdx; i++ ) {
          parser.extraction_map[i] = true;
        }
      }
    }
    
    return(adjustment);
  }
}
