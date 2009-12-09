package com.orbious.extractor;


/**
 * $Id: Config.java 14 2009-12-06 10:03:53Z app $
 * <p>
 * Provides constants that are shared across <code>DocumentSeparator</code>.
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
   * All characters that are considered punctuation.
   */
  PUNCTUATION("!\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~"),
  
  /**
   * All characters that are considered sentence ends.
   */
  SENTENCE_ENDS(".!?"),
  
  /**
   * Characters that should be preserved during the cleansing process.
   * This does not include punctuation that is part of a word (e.g. '.', '-' etc)
   * which is preserved by default.
   */
  PRESERVED_PUNCTUATION("\"!?'?()$&,."),
  
  /**
   * A regular expression to match URL's.
   */
  URL_REGEX("[a-zA-Z0-9\\-]+\\.(com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk)"),
  
  /**
   * A text file containing a list of suspensions (each suspension on a newline).
   */
  SUSPENSION_FILENAME("resources/suspensions.txt"),
  
  /**
   * A text file containing a list of common names, each on a newline.
   * Ignores lines that begin with "#" (i.e. comments).
   */
  NAMES_FILENAME("resources/names.txt");
  
  /**
   * The value for the enum. 
   */
  private String value;
  
  
  /**
   * Constructor, set's the enum constant to <code>value</code>.
   * 
   * @param value  The value to set the enum constant to.
   */
  private Config(String value) {
	this.value = value;  
  }
  
  /**
   * Returns the value of the enum constant.
   * 
   * @return  Retuns the value of the enum constant.
   */
  public String get() {
    return(value);
  }
  
  /**
   * Sets the enum constant to <code>value</code>.
   * 
   * @param value  The value of the enum constant.
   */
  public void put(String value) {
    this.value = value;
  }
}


