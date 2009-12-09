package com.orbious.extractor.evaluator;

import com.orbious.extractor.Config;
import org.apache.log4j.Logger;


public class Acronym extends Evaluator {

  /**
   * The minimum number of punctuation characters that constitute an acronym.
   */
  private static int OSCILLATION_MIN = 2;
  
  /**
   * The maximum number of consecutive characters that can exist as part of 
   * an acronym.
   */
  private static int OSCILLATION_CHARACTER_MAX = 1;
  
  /**
   * Evaluates whether text is an acronym by checking the left direction
   * of text.
   */
  private static int LEFT = 1;
  
  /**
   * Evaluates whether text is an acronym by checking the right direction
   * of text.
   */
  private static int RIGHT = 2;
  
  /**
   * Logger object.
   */
  private static Logger logger = Logger.getLogger(Config.LOGGER_REALM);

  
  /**
   * Constructor, initializes the <code>Acronym</code> class.
   */
  public Acronym() {
    super("Acronym");
  }
  
  /**
   * Determines if the full stop is part of of an acronym and therefore
   * not a sentence end.
   * 
   * @param buf The buffer to examine.
   * @param idx The position in the buffer where punctuation occurs.
   * 
   * @return    <code>true</code> if the full stop is part of an acronym,
   *        and not a sentence end, <code>false</code> otherwise.
   */
  public boolean evaluate(char[] buf, int idx) {

    if ( (idx < 0) || (idx >= buf.length) ) {
      throw new ArrayIndexOutOfBoundsException("Invalid index=" + idx);
    }
    
    // need to determine whether the full stop lies at the end of a acronym
    // if so we need to parse left otherwise we need to parse right
    AcronymOp op;

    if ( (idx+1 < buf.length) && (Character.isWhitespace(buf[idx+1]) ||
        !Character.isLetterOrDigit(buf[idx+1])) ) {
      op = isOscillating(buf, idx, LEFT);
    } else {
      op = isOscillating(buf, idx, RIGHT);
    }
    
    return(op.result);
  }
  
    /**
     * Determines if the word is an acronym and therefore not a sentence 
     * start.
     * 
     * @param wd    A string containing a word to check if an acronym.
     * @return      <code>true</code> if the word is an acronym and not
     *        a sentence end, <code>false</code> otherwise.
     */
    public boolean evaluate(String wd) {
      char[] buf = wd.toCharArray();
      boolean result;
            
      for ( int i = 0; i < buf.length; i++ ) {
        if ( !Character.isLetterOrDigit(buf[i]) && 
            !Character.isWhitespace(buf[i]) ) {
          result = evaluate(buf, i);
          if ( result ) {
            return(result);
          }
        }
      }
            
      return(false);
    }

  
  /**
   * Checks the <code>buf</code> for oscillations of punctuation/letters
   * to determine whether or not an acronym.
   * 
   * @param buf   Character buffer to examine for acronym.
   * @param idx   Index in the buffer to begin acronym examination.
   * @param dirn    The direction {@link Acronym#LEFT} or
   *          {@link Acronym#RIGHT} to traverse in <code>buf</code>
   *          for determine whether an acronym.
   * @return      An {@link AcronymOp} containing the results of the 
   *          oscillation check.
   */
  protected static AcronymOp isOscillating(char[] buf, int idx, int dirn) {
    boolean result = false;
    boolean hasChar = false;
    int j;
    int oscilCt = 1;
    int conschCt = 0;
    int maxConschCt = 0;
    int ctr;

    if ( dirn == LEFT ) {
      ctr = -1;
      j = idx-1;
    } else {
      ctr = 1;
      j = idx+1;
    }
  
    while ( (j < buf.length) && (j >= 0) ) {  
      if ( Character.isLetter(buf[j]) ) {
        conschCt++;
        hasChar = true;
      } else if ( !Character.isLetterOrDigit(buf[j]) &&
          !Character.isWhitespace(buf[j]) ) {
        oscilCt++;
        
        if ( conschCt > maxConschCt ) {
          maxConschCt = conschCt;
        }
        conschCt = 0;
      } else if ( Character.isWhitespace(buf[j]) ) {
        // for well formed acronyms e.g. |E.M.C*.* .|
        // we need to check the character after the space
        // NOTE we only increment if we are moving right, not left .
        if ( (dirn == RIGHT) &&
            ((j+1) < buf.length) &&
            !Character.isWhitespace(buf[j+1]) &&
            !Character.isLetterOrDigit(buf[j+1]) ) {
          oscilCt++;
        }
        break;
      }
      
      j += ctr;
    }
    
    if ( maxConschCt > conschCt ) {
      conschCt = maxConschCt;
    }
    
    if ( hasChar && 
        (oscilCt >= OSCILLATION_MIN) && 
        (conschCt <= OSCILLATION_CHARACTER_MAX) ) {
      result = true;
    }
    
    if ( logger.isDebugEnabled() ) {
      String dirnStr;
      String bufStr;
      if ( dirn == LEFT ) {
        dirnStr = "left";
        bufStr = String.copyValueOf(buf, (j+1), (idx-j));
      } else { 
        dirnStr = "right";
        bufStr = String.copyValueOf(buf, idx, (j-idx));
      }

      logger.debug("result=" + result + 
          " direction=" + dirnStr + " buf[" + idx + "]=" + buf[idx] +
          " oscilCt=" + oscilCt + " conschCt=" + conschCt +
          " res=" + result + " Buf=|" + bufStr + "|");
    }
    
    AcronymOp op = new Acronym.AcronymOp(result, oscilCt, conschCt);
    return(op);   
  }
  

  /**
   * <code>AcronymOp</code> is an inner class that is used by 
   * <code>Acronym</code> to pass the results from 
   * {@link Acronym#isOscillating(char[], int, int)} between methods.
   * 
   * @author dave
   * @version 1.0
   * @since 1.0
   */
  private static class AcronymOp {
  
    /**
     * Whether or not an acronym was found.
     */
    private boolean result;
    
    /**
     * The number of punctuation characters found.
     */
    private int oscillatingCt;
    
    /**
     * The maximum number of consecutive characters (letters or digits) 
     * found.
     */
    private int consecutiveCharCt;
    
    /**
     * Constructor, initializes the <code>AcronymOp</code> object.
     * 
     * @param result        Whether or not an acronym was found.
     * @param oscillatingCt     The number of punctuation characters.
     * @param consecutiveCharCt   Maximum number of consecutive characters
     *                found.
     */
    public AcronymOp(boolean result, int oscillatingCt, 
        int consecutiveCharCt) {
      this.result = result;
      this.oscillatingCt = oscillatingCt;
      this.consecutiveCharCt = consecutiveCharCt;
    }
    
    /**
     * @return  Returns whether or not an acronym was found.
     */
    @SuppressWarnings("unused")
    public boolean result() {
      return(result);
    }
    
    /**
     * @return  The number of punctuation characters found.
     */
    @SuppressWarnings("unused")
    public int oscillatingCt() {
      return(oscillatingCt);
    }

    /**
     * @return  The maximum number of consecutive characters 
     *      (letters or digits) found.
     */   
    @SuppressWarnings("unused")
    public int consecutiveCharCt() {
      return(consecutiveCharCt);
    }
  }
  

  
}
