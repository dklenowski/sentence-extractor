package com.orbious.extractor;

import com.orbious.util.config.IConfig;

/**
 * Configuration information.
 *
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public enum AppConfig implements IConfig {
  /**
   * A version string (for tracking output).
   */
  app_version("5.0"),

  /**
   * The default log4j logging realm.
   */
  log_realm("sentence-extractor"),

  /**
   * The Log4j configuration file.
   */
  log_config("com/orbious/extractor/log4j.xml"),

  /**
   * Left punctuation marks.
   */
  left_punctuation_marks("[{("),

  /**
   * Right punctuation marks.
   */
  right_punctuation_marks("]})"),

  /**
   * All characters that are considered sentence ends.
   */
  sentence_ends(".!?\":"),

  /**
   * All punctuation that is considered part of a sentence
   * e.g. '-', '.'
   */
  inner_punctuation(".-'`:,"),

  /**
   * Characters that should be preserved during the cleansing process.
   * This includes {@link Config#left_punctuation_marks},
   * {@link Config#right_punctuation_marks},
   * {@link Config#sentence_ends} and {@link Config#inner_punctuation}
   * as well as some additional punctuation (e.g. @, $, &).
   */
  preserved_punctuation(left_punctuation_marks.asString() +
      right_punctuation_marks.asString() + sentence_ends.asString() +
      inner_punctuation.asString() + ":;@$&,%"),

  /**
   * All characters that are considered punctuation. This includes
   * {@link Config#PRESERVED_PUNCTUATION} along with some additional
   * punctuation (e.g. "*").
   */
  punctuation(preserved_punctuation.asString() + "#*+,/<=>\\^_|~"),

  /**
   * All characters that are used in roman numerals.
   */
  roman_numerals("IVXLCDM"),

  /**
   * A regular expression to match URL's.
   */
  url_regex("[a-zA-Z0-9\\-]+\\.(com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk)"),

  /**
   * The maximum sentence length (in words), used as a cutoff value in
   * sentence extraction.
   */
  max_sentence_length(100),

  /**
   * A text file containing a list of suspensions (each suspension on a newline).
   */
  suspension_filename("com/orbious/extractor/suspensions.txt"),

  /**
   * A text file containing a list of common names, each on a newline.
   * Ignores lines that begin with "#" (i.e. comments).
   */
  names_filename("com/orbious/extractor/names.txt"),

  /**
   * Minimum sentence length (in alphanumeric words).
   */
  min_sentence_length(4);

  // implementation

  /**
   * The <code>String</code> value for this enum.
   */
  private String svalue = null;

  /**
   * The <code>int</code> value for this enum.
   */
  private int ivalue = -1;

  /**
   * The <code>float</code> value for this enum.
   */
  private float fvalue = Float.NaN;

  /**
   * The <code>double</code> value for this enum.
   */
  private double dvalue = Double.NaN;

  /**
   * The <code>long</code> value for this enum.
   */
  private long lvalue = -1;

  /**
   * The <code>boolean</code> value for this enum.
   */
  private boolean bvalue = false;

  /**
   * Constructor, set's the enum constant.
   *
   * @param value    The <code>String</code> value to set the enum constant to.
   */
  private AppConfig(String value) {
	this.svalue = value;
  }

  /**
   * Constructor, set's the enum constant.
   *
   * @param value    The <code>int</code> value to set the enum constant to.
   */
  private AppConfig(int value) {
	  this.ivalue = value;
  }

  /**
   * Constructor, set's the enum constant.
   *
   * @param value    The <code>double</code> value to set the enum constant to.
   */
  private AppConfig(double value) {
    this.dvalue = value;
  }

  /**
   * Constructor, set's the enum constant.
   *
   * @param value    The <code>long</code> value to set the enum constant to.
   */
  private AppConfig(long value) {
    this.lvalue = value;
  }


  /**
   * Constructor, set's the enum constant.
   *
   * @param value    The <code>boolean</code> value to set the enum constant to.
   */
  private AppConfig(boolean value) {
    this.bvalue = value;
  }

  // implementation of the IConfig interface methods.

  public boolean isString() {
    return (svalue != null) ? true : false;
  }

  public String asString() {
    return svalue;
  }

  public boolean isInt() {
    return (ivalue != -1) ? true : false;
  }

  public int asInt() {
    return ivalue;
  }

  public boolean isFloat() {
    return (fvalue != Float.NaN) ? true : false;
  }

  public float asFloat() {
    return fvalue;
  }

  public boolean isDouble() {
    return (dvalue != Double.NaN) ? true : false;
  }

  public double asDouble() {
    return dvalue;
  }

  public boolean isLong() {
    return (lvalue != -1) ? true : false;
  }

  public long asLong() {
    return lvalue;
  }

  public boolean isBool() {
    // no really way to check a bool
    // therefore must be checked last ..
    return true;
  }

  public boolean asBool() {
    return bvalue;
  }

  public String getName() {
    return this.name();
  }
}


