package com.orbious.extractor;

// $Id$

/**
 * Shared Constants.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public enum Config {
  /**
   * The default log4j logging realm.
   */
  LOGGER_REALM("sentence-extractor"),
  
  /**
   * The Log4j configuration file.
   */
  LOGGER_CONF_FILENAME("com/orbious/extractor/log4j.xml"),
  
  /**
   * Left punctuation marks.
   */
  LEFT_PUNCTUATION_MARKS("[{("),
  
  /**
   * Right punctuation marks.
   */
  RIGHT_PUNCTUATION_MARKS("]})"),

  /**
   * All characters that are considered sentence ends.
   */
  SENTENCE_ENDS(".!?\":"),

  /**
   * All punctuation that is considered part of a sentence
   * e.g. '-', '.'
   */
  INNER_PUNCTUATION(".-'`"),

  /**
   * Characters that should be preserved during the cleansing process.
   * This includes {@link Config#LEFT_PUNCTUATION_MARKS}, 
   * {@link Config#RIGHT_PUNCTUATION_MARKS},
   * {@link Config#SENTENCE_ENDS} and {@link Config#INNER_PUNCTUATION} 
   * as well as some additional punctuation (e.g. @, $, &).
   */
  PRESERVED_PUNCTUATION(LEFT_PUNCTUATION_MARKS.asStr() +
      RIGHT_PUNCTUATION_MARKS.asStr() + SENTENCE_ENDS.asStr() + 
      INNER_PUNCTUATION.asStr() + ":;@$&,%"),
  
  /**
   * All characters that are considered punctuation. This includes
   * {@link Config#PRESERVED_PUNCTUATION} along with some additional
   * punctuation (e.g. "*").
   */
  PUNCTUATION(PRESERVED_PUNCTUATION.asStr() + "#*+,/<=>\\^_|~"),
  
  /**
   * All characters that are used in roman numerals.
   */
  ROMAN_NUMERALS("IVXLCDM"),

  /**
   * A regular expression to match URL's.
   */
  URL_REGEX("[a-zA-Z0-9\\-]+\\.(com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk)"),
  
  /**
   * The maximum sentence length (in words), used as a cutoff value in 
   * sentence extraction.
   */
  MAX_SENTENCE_LENGTH(100),
  
  /**
   * A text file containing a list of suspensions (each suspension on a newline).
   */
  SUSPENSION_FILENAME("com/orbious/extractor/suspensions.txt"),
  
  /**
   * A text file containing a list of common names, each on a newline.
   * Ignores lines that begin with "#" (i.e. comments).
   */
  NAMES_FILENAME("com/orbious/extractor/names.txt"),
  
  /**
   * Minimum sentence length (in alphanumeric words).
   */
  MIN_SENTENCE_LENGTH(4);
  
  /**
   * The value for the enum. 
   */
  private String value;
  
  /**
   * Constructor, set's the enum constant.
   * 
   * @param value    The <code>String</code> value to set the enum constant to.
   */
  private Config(String value) {
	this.value = value; 
  }

  /**
   * Constructor, set's the enum constant.
   * 
   * @param value    The <code>int</code> value to set the enum constant to.
   */
  private Config(int value) {
	  this.value = Integer.toString(value);
  }
  
  /**
   * Returns the value of the enum constant as a <code>String</code>.
   * 
   * @return    Retuns the value of the enum constant as a <code>String</code>.
   */
  public String asStr() {
    return(value);
  }

  /**
   * Returns the value of the enum constant as an <code>int</code>.
   * 
   * @return    Retuns the value of the enum constant as an <code>int</code>.
   */
  public int asInt() {
	  return(Integer.parseInt(value));
  }

  /**
   * Sets the enum constant to <code>value</code>.
   * 
   * @param value    The <code>int</code> value to set the enum constant to.
   */
  public void put(String value) {
    this.value = value;
  }
  
  /**
   * Sets the enum constant to <code>value</code>.
   * 
   * @param value    The <code>int</code> value to set the enum constant to.
   */
  public void put(int value) {
	  this.value = Integer.toString(value);
  }
}


