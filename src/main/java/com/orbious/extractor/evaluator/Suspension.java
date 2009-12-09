package com.orbious.extractor.evaluator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import org.apache.log4j.Logger;
import com.orbious.extractor.Config;
import com.orbious.extractor.Word;

/**
* $Id$
* <p>
* The <code>Suspension</code> <code>Evaluator</code> determines if a word 
* is a suspension and cannot be considered a sentence start/end.
* <ul>
* <li>It can be capitalized and not be a sentence start.
* <li>It can be terminated with a fullstop and not be a sentence end.
* </ul>
* 
* @author dave
* @version 1.0
* @since 1.0
*/


public class Suspension extends Evaluator {

  /**
   * In memory list of suspension extracted from 
   * {@link Config#SUSPENSION_FILENAME}.
   */
  private static HashSet<String> suspensions;
  
  /**
   * Constructor, initializes the <code>Suspension</code> class.
   */
  public Suspension() {
    super("Suspension");
  }
  
  /**
   * Determines if the full stop is part of of an suspension and therefore
   * not a sentence end.
   * 
   * @param buf The buffer to examine.
   * @param idx The position in the buffer where punctuation occurs.
   * 
   * @return  <code>true</code> if the full stop is part of an suspension,
   *          and not a sentence end, <code>false</code> otherwise.
   */
  public boolean evaluate(char[] buf, int idx) {
    String wd = Word.getPreviousWord(buf, idx);

    if ( suspensions == null ) {
      init();
    }
    
    return( suspensions.contains(wd) );
  }
  
    /**
     * Determines if the word is an suspension and therefore not a sentence 
     * start.
     * 
     * @param wd  A string containing a word to check if an suspension.
     * 
     * @return  <code>true</code> if the word is an suspension and not
     *          a sentence end, <code>false</code> otherwise.
     */
  public boolean evaluate(String wd) {
    if ( suspensions == null ) {
      init();
    }
    
    return( suspensions.contains(wd) );
  }
  
  /**
   * Parses the {@link Config#SUSPENSION_FILENAME} into memory.
   */
  private static void init() {
    Logger logger;
    BufferedReader br = null;
    
    logger = Logger.getLogger(Config.LOGGER_REALM.get());
    try {
      br = new BufferedReader(
          new FileReader(Config.SUSPENSION_FILENAME.get()));
    } catch ( FileNotFoundException fnfe ) {
      logger.fatal("Failed to open suspension file " + 
          Config.SUSPENSION_FILENAME, fnfe);
    }
    
    suspensions = new HashSet<String>();

    try {
      String wd;
      while ( (wd = br.readLine()) != null ) {
        suspensions.add(wd);
      }
    } catch ( IOException ioe ) {
      logger.fatal("Failed to read suspension file " + 
          Config.SUSPENSION_FILENAME, ioe);
    }
    
    logger.info("Initialized " + suspensions.size() + " suspensions.");
  }
}
