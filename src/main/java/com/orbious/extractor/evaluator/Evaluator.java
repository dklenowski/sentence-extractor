package com.orbious.extractor.evaluator;

// $Id$

import org.apache.log4j.Logger;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.util.Loggers;

/**
 * A class that is sub-class for running evaluations on sentence start/ends.
 * <p>
 * If the overridden <code>evaluate</code> method returns <code>true</code>,
 * the implementing <code>Evaluator</code> believes the passed in parameters
 * are unlikely to be a sentence start/end.
 *
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public abstract class Evaluator {

  public enum EvaluatorType { START, END };

  /**
   * The name of this <code>Evaluator</code>, used in debugging.
   */
  protected String name;

  /**
   * The type of this evaluator
   */
  protected EvaluatorType type;

  /**
   * Data generated via <code>TextParser</code> that may be required
   * during evaluator.
   */
  protected TextParserData parser_data;

  /**
   * A debug string for the current <code>evaluate</code>.
   */
  protected StringBuilder debug_str;

  /**
   * Logger object.
   */
  protected static final Logger logger = Loggers.logger();

  /**
   * Initializes the <code>Evaluator</code>.
   *
   * @param name    The name of this <code>Evaluator</code>, used in
   *                debugging.
   * @param type    The type of this <code>Evaluator</code>,
   *                either <code>START</code> or <code>END</code>.
   */
  public Evaluator(String name, EvaluatorType type) {
    this.name = name;
    this.type = type;
    debug_str = new StringBuilder();
  }

  /**
   * Initializes the <code>Evaluator</code>.
   *
   * @param name    The name of this <code>Evaluator</code>, used in
   *                debugging.
   * @param parserData      Data generated via <code>TextParser</code>.
   * @param type    The type of this <code>Evaluator</code>,
   *                either <code>START</code> or <code>END</code>.
   */
  public Evaluator(String name, TextParserData parserData, EvaluatorType type) {
    this.name = name;
    this.type = type;
    this.parser_data = parserData;
    debug_str = new StringBuilder();
  }

  /**
   * Returns the name of this <code>Evaluator</code>.
   *
   * @return    The name of this <code>Evaluator</code>.
   */
  public String name() {
    return(name);
  }

  /**
   * Returns the type of this <code>Evaluator</code>.
   *
   * @return    The type of this <code>Evaluator</code>.
   */
  public EvaluatorType type() {
    return(type);
  }

  /**
   * Returns the debug string for the current evaluation.
   *
   * @return    The debug string for the current evaluation.
   */
  public String debugStr() {
    return(debug_str.toString());
  }

  /**
   * Reload any fixed configuration.
   */
  public abstract void invalidate() throws EvaluatorException;

  /**
   * Determines whether or not to record a failed evaluation as unlikely.
   *
   * @return  Whether or not to record the failed evaluation as unlikely.
   */
  public abstract boolean recordAsUnlikely();

  /**
   * Determines whether or not to record a failed evaluation as a pause.
   * e.g. a Numbered Heading is recorded as a pause for an <code>END</code>.
   *
   * @return  Whether or not to record the failed evaluation as a pause.
   */
  public abstract boolean recordAsPause();

  /**
   * Runs an evaluation based on position <code>idx</code> in the
   * <code>buffer</code>. If the evaluation returns <code>true</code>,
   * the position in the buffer is not likely to be part of a sentence start/end.
   *
   * @param buf   Text buffer.
   * @param idx   The position in the buffer to obtain the previous word.
   *
   * @return    <code>true</code> if the position in the buffer is
   *            not a sentence start/end, <code>false</code> otherwise.
   */
  public abstract boolean evaluate(final char[] buf, int idx) throws EvaluatorException;
}
