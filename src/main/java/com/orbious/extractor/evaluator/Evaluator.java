package com.orbious.extractor.evaluator;

import org.apache.log4j.Logger;

import com.orbious.extractor.Config;

/**
 * $Id$
 * <p>
 * A class that subclasses <code>Evaluator</code> runs <code>evaluate</code>
 * to determine whether a word/position in buffer can be considered part 
 * of a sentence start/end.
 * <p>
 * When run as a end evaluator, the {@link Evaluator#evaluate(char[], int)}
 * is used.
 * <p>
 * When run as a start evaluator, the {@link Evaluator#evaluate(String)}
 * is used.
 * <p>
 * If <code>evaluate</code> returns <code>true</code>, the <code>Evaluator</code>
 * believes that the passed in parameters does not constitute a 
 * sentence start/end.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public abstract class Evaluator {
  
  /**
   * The name of this <code>Evaluator</code>, used in debugging.
   */
  private String name;

  /**
   * Logger object.
   */
  protected static final Logger logger = Logger.getLogger(Config.LOGGER_REALM.asStr());
  
  /**
   * Initializes a <code>Evaluator</code>.
   * 
   * @param name  The name of this <code>Evaluator</code>, used in 
   *              debugging.
   */
  public Evaluator(String name) {
    this.name = name;
  }
  
  /**
   * Returns the name of this <code>Evaluator</code>.
   * 
   * @return  The name of this <code>Evaluator</code>.
   */
  public String name() {
    return(this.name);
  }
  
  /**
   * Runs an evaluation based on position <code>idx</code> in the 
   * <code>buffer</code>. If the evaluation returns <code>true</code>,
   * the position in the buffer is not part of a sentence end.
   *  
   * @param buf   A text buffer.
   * @param idx   The position in the buffer to obtain the previous word.
   * 
   * @return      <code>true</code> if the position in the buffer is
   *          not a sentence end, <code>false</code> otherwise.
   */
  public abstract boolean evaluate(final char[] buf, int idx);  
  
  /**
   * Runs an evaluation on the word <code>wd</code>. If the 
   * evaluation returns <code>true</code>, the word is not part of a 
   * sentence start.
   *  
   * @param wd    A word.
   * 
   * @return      <code>true</code> if the word is not a sentence start,
   *          <code>false</code> otherwise.
   */
  public abstract boolean evaluate(String wd);
}
