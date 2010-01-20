package com.orbious.extractor.evaluator;

// $Id$

import org.apache.log4j.Logger;
import com.orbious.extractor.Config;

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
  
  /**
   * The name of this <code>Evaluator</code>, used in debugging.
   */
  private String name;

  /**
   * Logger object.
   */
  protected static final Logger logger = Logger.getLogger(Config.LOGGER_REALM.asStr());
  
  /**
   * Initializes the <code>Evaluator</code>.
   * 
   * @param name    The name of this <code>Evaluator</code>, used in 
   *                debugging.
   */
  public Evaluator(String name) {
    this.name = name;
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
   * Runs an evaluation based on position <code>idx</code> in the 
   * <code>buffer</code>. If the evaluation returns <code>true</code>,
   * the position in the buffer is likely to be not part of a sentence start/end.
   *  
   * @param buf   Text buffer.
   * @param idx   The position in the buffer to obtain the previous word.
   * 
   * @return    <code>true</code> if the position in the buffer is
   *            not a sentence end, <code>false</code> otherwise.
   */
  public abstract boolean evaluate(final char[] buf, int idx) throws Exception;  
}
