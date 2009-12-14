package com.orbious.extractor.evaluator;

import com.orbious.extractor.Config;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import org.apache.log4j.Logger;

/**
 * $Id$
 * <p>
 * Implements the <code>SentenceStart</code> interface to determine
 * if a possible sentence start is a common first name/surname.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Name extends Evaluator {

  /**
   * In memory list of names.
   */
  private static HashSet<String> names;
  
  public Name() {
    super("Name");
  }
  
  /**
   * Note used, returns <code>false</code> because a common name
   * does not need to be evaluated as a sentence end.
   * 
   * @param buf
   * @param idx
   *  
   * @return    <code>false</code>.
   */
  public boolean evaluate(final char[] buf, int idx) {
    return(false);
  } 
  
  
  /**
   * Determines if the word is a common name and therefore 
   * cannot be considered a sentence start.
   * 
   * @param wd  A string containing a word to check if an common name.
   * 
   * @return  <code>true</code> if the word is an common name and not
   *          a sentence end, <code>false</code> otherwise.
   */ 
  public boolean evaluate(String wd) {
    if ( names == null ) {
      init();
    }
    
    return( names.contains(wd) );
  }

  /**
   * Parses the {@link Config#NAMES_FILENAME} into memory.
   */
  private static void init() {
    Logger logger;
    BufferedReader br = null;
    
    logger = Logger.getLogger(Config.LOGGER_REALM.asStr());
    try {
      br = new BufferedReader(
          new FileReader(Config.NAMES_FILENAME.asStr()));
    } catch ( FileNotFoundException fnfe ) {
      logger.fatal("Failed to open names file " + 
          Config.NAMES_FILENAME, fnfe);
    }
    
    names = new HashSet<String>();

    try {
      String wd;
      while ( (wd = br.readLine()) != null ) {
        if ( !wd.matches("#.*") ) {
          names.add(wd);
        } 
      }
    } catch ( IOException ioe ) {
      logger.fatal("Failed to read names file " + 
          Config.NAMES_FILENAME, ioe);
    }
    
    logger.info("Initialized " + names.size() + " common names.");    
  }
}
