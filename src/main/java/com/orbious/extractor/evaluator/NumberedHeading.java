package com.orbious.extractor.evaluator;

import java.util.HashSet;
import com.orbious.extractor.AppConfig;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.util.HashSets;
import com.orbious.util.config.Config;

/**
* The <code>NumberedHeading</code> <code>Evaluator</code> determines if a full stop
* is a part of a numbered heading and cannot be considered a sentence end.
*
* @author dave
* @version 1.0
* @since 1.0
*/

public class NumberedHeading extends Evaluator {

  /**
   * <code>HashSet</code> of roman numerals.
   */
  private static HashSet< Character > roman_numerals;

  /**
   * Constructor, initializes this <code>Evaluator</code>.
   *
   * @param parserData  Data generating during <code>TextParser</code> parsing.
   * @param type    The type of <code>Evaluator</code>.
   */
  public NumberedHeading(TextParserData parserData, EvaluatorType type) {
    super("NumberedHeading", parserData, type);
  }

  public void invalidate() {
    roman_numerals = HashSets.cvtStringToHashSet(
        Config.getString(AppConfig.roman_numerals));
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
    return(true);
  }

  /**
   * Determines if the previous word from <code>idx</code>
   * in the buffer <code>buf</code> is a numbered heading and therefore
   * not a likely sentence end.
   */
  public boolean evaluate(final char[] buf, int idx) {
    boolean b;

    b = evaluateNumbered(buf, idx);
    if ( b ) {
      return(b);
    }

    b = evaluateRoman(buf, idx);
    return(b);
  }

  /**
   * Evaluates the current position to see if it is part of a Numbered Heading.
   * Note, <code>buf[idx]</code> must contain a full stop for evaluation to proceed.
   *
   * @param buf    Text buffer.
   * @param idx    Position in the buffer <code>buf</code> to begin evaluation.
   *
   * @return    <code>true</code> if the current position is likely to be a
   *            Numbered Heading, <code>false</code> otherwise.
   */
  protected boolean evaluateNumbered(final char[] buf, int idx) {
    boolean fnd;
    boolean hasNonNumber;
    char ch;
    int firstIdx;

    fnd = false;
    hasNonNumber = false;
    firstIdx = -1;

    if ( buf[idx] != '.' ) {
      return(false);
    } else if ( idx == 0 ) {
      return(false);
    }

    for ( int i = idx-1; i >= 0; i-- ) {
      ch = buf[i];
      if ( Character.isWhitespace(ch) ) {
        firstIdx = i+1;
        break;
      }

      if ( !Character.isDigit(ch) ) {
        hasNonNumber = true;
        break;
      }
    }

    if ( firstIdx == -1 ) {
      firstIdx = 0;
    }

    if ( !hasNonNumber && parser_data.containsLineStart(firstIdx) ) {
      fnd = true;
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug("Numbered: buf[" + idx + "]=" + buf[idx] +
          " firstIdx=" + firstIdx + " fnd=" + fnd +
          " hasNonNumber=" + String.valueOf(hasNonNumber).toUpperCase() +
          " Match=" + String.valueOf(fnd).toUpperCase());
    }

    return(fnd);
  }

  /**
   * Evaluates the current position to see if it is part of a Roman Numeral Heading.
   * Note, <code>buf[idx]</code> must contain a full stop for evaluation to proceed.
   *
   * @param buf    Text buffer.
   * @param idx    Position in the buffer <code>buf</code> to begin evaluation.
   *
   * @return    <code>true</code> if the current position is likely to be a
   *            Roman Numeral Heading, <code>false</code> otherwise.
   */
  protected boolean evaluateRoman(final char[] buf, int idx) {
    boolean fnd;
    boolean hasNonRoman;
    char ch;
    int firstIdx;

    fnd = false;
    hasNonRoman = false;
    firstIdx = -1;

    if ( buf[idx] != '.' ) {
      return(false);
    } else if ( idx == 0 ) {
      return(false);
    }

    for ( int i = idx-1; i >= 0; i-- ) {
      ch = buf[i];
      if ( Character.isWhitespace(ch) ) {
        firstIdx = i+1;
        break;
      }

      if ( !roman_numerals.contains(ch) ) {
        hasNonRoman = true;
        break;
      }
    }

    if ( firstIdx == -1 ) {
      firstIdx = 0;
    }

    if ( !hasNonRoman && parser_data.containsLineStart(firstIdx) ) {
      fnd = true;
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug("Roman: buf[" + idx + "]=" + buf[idx] +
          " firstIdx=" + firstIdx +
          " hasNonRoman=" + String.valueOf(hasNonRoman).toUpperCase() +
          " Match=" + String.valueOf(fnd).toUpperCase());
    }

    return(fnd);
  }
}

