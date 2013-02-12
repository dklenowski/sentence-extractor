package com.orbious.extractor.evaluator;

import java.util.HashSet;

import com.orbious.extractor.AppConfig;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.util.HashSets;
import com.orbious.util.config.Config;

/**
 * Determines whether a word/position in a text buffer is considered
 * an Heading and therefore not a valid sentence start.
 *
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Heading extends Evaluator {

  /**
   * In memory list of sentence ends.
   */
  private static HashSet< Character > sentence_ends;

  /**
   * The minimum threshold (a percentage of characters) to consider the
   * line as a <code>Heading</code>.
   */
  private static double HEADING_THRESHOLD = 0.75;


  /**
   * Constructor, initializes this <code>Evaluator</code>.
   *
   * @param parserData  Data generating during <code>TextParser</code> parsing.
   * @param type    The type of <code>Evaluator</code>.
   */
  public Heading(TextParserData parserData, EvaluatorType type) {
    super("Heading", parserData, type);
  }

  public void invalidate() {
    sentence_ends = HashSets.cvtStringToCharHashSet(
        Config.getString(AppConfig.sentence_ends));
  }

  /**
   * Returns <code>true</code> as the position could be an unlikely start.
   */
  public boolean recordAsUnlikely() {
    return(true);
  }

  /**
   * Returns <code>true</code> as the position could be a pause between sentences.
   */
  public boolean recordAsPause() {
    return(true);
  }

  /**
   * Determines if the current characters are part of an Heading
   * and therefore not a likely sentence start/end.
   */
  public boolean evaluate(char[] buf, int idx) {
    if ( !Character.isUpperCase(buf[idx]) ) {
      return(false);
    } else if ( !parser_data.containsLineStart(idx) ) {
      // check if we are part of a previous heading
      if ( parser_data.containsHeading(idx) ) {
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

      if ( parser_data.containsLineStart(i) ) {
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

    double thresh = parser_data.avgLineCharCt()*HEADING_THRESHOLD;
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
