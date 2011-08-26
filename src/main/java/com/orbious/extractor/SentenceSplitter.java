package com.orbious.extractor;

import java.util.HashSet;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.util.HashSets;
import com.orbious.util.Loggers;
import com.orbious.util.Strings;
import com.orbious.util.config.Config;

/**
 *
 * Uses the following data from <code>TextParserData</code>.
 * <li>TextParserData - Used by URLText (only buffer used in evaluate()).
 * <li>buffer - Read only.
 * <li>extraction_map - Read/Write.
 *
 * @author dklenowski
 * @version 2.0
 * @since 2.0
 */
public class SentenceSplitter {


  /**
   * Text Parser data that is used during Sentence Splitting.
   */
  private TextParserData parser_data;

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
  private Logger logger = Loggers.logger();

  public SentenceSplitter(TextParserData parserData) {
    this.parser_data = parserData;
  }

  public void invalidate() {
    inner_punctuation = HashSets.cvtStringToHashSet(
        Config.getString(AppConfig.inner_punctuation));
    preserved_punctuation = HashSets.cvtStringToHashSet(
        Config.getString(AppConfig.preserved_punctuation));
    left_marks = HashSets.cvtStringToHashSet(
        Config.getString(AppConfig.left_punctuation_marks));
    right_marks = HashSets.cvtStringToHashSet(
        Config.getString(AppConfig.right_punctuation_marks));
    sentence_ends = HashSets.cvtStringToHashSet(
        Config.getString(AppConfig.sentence_ends));
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
    int adStartIdx, adEndIdx;
    int postEndIdx;
    int wordCt;
    String wd;
    char ch;
    //Suspension suspension;
    IndexAdjustment indexAdjustment;
    boolean hasAlpha, hasLetter;
    boolean doAsNewWord;

    //suspension = new Suspension(parser_data, EvaluatorType.END);
    indexAdjustment = adjustIndexes(op.start(), op.end());

    adStartIdx = indexAdjustment.adjustedStartIdx();
    adEndIdx = indexAdjustment.adjustedEndIdx();

    postEndIdx = 0;

    for ( int i = adStartIdx; i < adEndIdx; i++ ) {
      if ( Character.isLetterOrDigit(parser_data.buffer[i]) ) {
        postEndIdx = 0;
      } else if ( postEndIdx == 0 ) {
          postEndIdx = i;
      }
    }

    if ( postEndIdx == 0 ) {
      postEndIdx = adEndIdx;
    }

    words = new Vector<String>();
    wd = "";
    hasAlpha = false;
    hasLetter = false;
    wordCt = 0;

    if ( logger.isDebugEnabled() ) {
      logger.debug("Beginning extract start=" + op.start() +
          " adjustedStartIdx=" + adStartIdx +
          " end=" + op.end() +
          " adjustedEndIdx=" + adEndIdx +
          " postEndIdx=" + postEndIdx + "\n" +
          Strings.cvtCharArrayToString(parser_data.buffer, adStartIdx, adEndIdx) + "\n");
    }

    for ( int i = adStartIdx; i <= adEndIdx; i++ ) {
      ch = parser_data.buffer[i];

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
        //
        doAsNewWord = false;
        if ( (i < op.start()) || (i >= postEndIdx) ) {
          // we are at the ends, so consider the punctuation as a new word
          doAsNewWord = true;

        } else {
          if ( hasAlpha && inner_punctuation.contains(ch) ) {
            if ( (ch == '.') ||
                ((i+1 < parser_data.buffer.length) &&
                    Character.isLetterOrDigit(parser_data.buffer[i+1])) ) {
              // there are 2 cases, suspension which need to be combined
              // and text where the next letter is text
              wd += ch;
            } else {
              doAsNewWord = true;
            }
          } else if ( preserved_punctuation.contains(ch) ) {
            doAsNewWord = true;
          }
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

    // we need to run a final check and certain punctuation
    //
    Vector<String> clean = new Vector<String>();
    int p = 0;

    StringBuilder tmpwd = null;

    while ( p < words.size() ) {
      wd = words.get(p);
      p++;

      if ( wd.matches(".*[a-zA-Z0-9].*") ) {
        //
        // special case, for words with a fullstop at the end of the
        // of the word (which can occur when TextParser#hasLaterPunctuation
        // is true
        if ( tmpwd != null ) {
          clean.add(tmpwd.toString());
          tmpwd = null;
        }

        /*if ( wd.charAt(wd.length()-1) == '.' ) {
          // run a suspension evaluation and check not a suspension
          int pos = wd.length()-1;
          try {
            if ( suspension.evaluate(wd.toCharArray(), pos) ) {
              clean.add(wd);
            } else {
              clean.add(wd.substring(0, pos));
              clean.add(".");
            }
          } catch ( Exception e ) {
            logger.fatal("Error initializing suspensions?");
            return(null);
          }
        } else {*/
          clean.add(wd);
        //}
        continue;
      }

      if ( tmpwd == null ) {
        tmpwd = new StringBuilder(wd);
        continue;
      }

      // if we get to here we allready have a tmpwd
      if ( wd.length() == 1 ) {
        ch = wd.charAt(0);
        if ( sentence_ends.contains(ch) ||
            left_marks.contains(ch) || right_marks.contains(ch) ) {
          clean.add(tmpwd.toString());
          tmpwd = null;
          clean.add(wd);
        } else {
          tmpwd.append(wd);
        }
      }
    }

    if ( tmpwd != null ) {
      clean.add(tmpwd.toString());
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug("PreClean =" + Strings.cvtVectorToString(words));
      logger.debug("Clean    =" + Strings.cvtVectorToString(clean));
    }

    return( new SplitterOp(clean, wordCt) );
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
           !parser_data.extraction_map[adjustedStartIdx] &&
           (Character.isWhitespace(parser_data.buffer[adjustedStartIdx]) ||
               left_marks.contains(parser_data.buffer[adjustedStartIdx]) ||
               parser_data.buffer[adjustedStartIdx] == '\"') ) {
          adjustedStartIdx--;
      }

      if ( adjustedStartIdx == nxtIdx ) {
        adjustment.adjustedStartIdx(startIdx);
      } else {
        adjustedLeft = true;
        adjustedStartIdx++;
        adjustment.adjustedStartIdx(adjustedStartIdx);
        for ( int i = adjustedStartIdx; i < startIdx; i++ ) {
          parser_data.extraction_map[i] = true;
        }
      }
    }

    int ct = 0;
    if ( adjustedLeft ) {
      ct++;
    }

    for ( int i = startIdx; i < endIdx; i++ ) {
      if ( left_marks.contains(parser_data.buffer[i]) ) {
        ct++;
      } else if ( right_marks.contains(parser_data.buffer[i]) ) {
        ct--;
      }
    }

    if ( ct != 0 ) {
      adjustedLeft = true;
    }
    // now check the end
    //
    if ( endIdx == parser_data.buffer.length-1 ) {
      adjustment.adjustedEndIdx(endIdx);
    } else {
      nxtIdx = endIdx+1;
      adjustedEndIdx = nxtIdx;

      while ( (adjustedEndIdx < parser_data.buffer.length) &&
          !parser_data.extraction_map[adjustedEndIdx] &&
          (   sentence_ends.contains(parser_data.buffer[adjustedEndIdx]) ||
              (adjustedLeft &&
              (Character.isWhitespace(parser_data.buffer[adjustedEndIdx]) ||
                  right_marks.contains(parser_data.buffer[adjustedEndIdx]) ||
                  parser_data.buffer[adjustedEndIdx] == '\"'))   ) ) {
        adjustedEndIdx++;
      }

      if ( adjustedEndIdx == nxtIdx ) {
        adjustment.adjustedEndIdx(endIdx);
      } else {
        adjustedEndIdx--;
        adjustment.adjustedEndIdx(adjustedEndIdx);
        for ( int i = endIdx+1; i <= adjustedEndIdx; i++ ) {
          parser_data.extraction_map[i] = true;
        }
      }
    }

    return(adjustment);
  }
}
