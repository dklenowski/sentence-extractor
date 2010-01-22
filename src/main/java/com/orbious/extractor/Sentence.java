package com.orbious.extractor;

// $Id$

import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.orbious.extractor.evaluator.AbbreviatedName;
import com.orbious.extractor.evaluator.Acronym;
import com.orbious.extractor.evaluator.Evaluator;
import com.orbious.extractor.evaluator.InnerQuote;
import com.orbious.extractor.evaluator.Name;
import com.orbious.extractor.evaluator.NumberedHeading;
import com.orbious.extractor.evaluator.Suspension;
import com.orbious.extractor.evaluator.UrlText;
import com.orbious.extractor.util.Helper;

/**
 * Provides static methods for Sentence operations.
 * <p>
 * To use your own evaluators, write a class that extends 
 * {@link com.orbious.extractor.evaluator.Evaluator} and then add the evaluator
 * using (for start evaluators):
 * <p>
 * <code>
 * Sentence.addStartEvaluator( new MyStartEvaluator() );
 * </code>
 * <p>
 * or for end evaluators:
 * <p>
 * <code>
 * Sentence.addEndEvaluator( new MyEndEvaluator() );
 * </code> 
 * <p>
 * If you would like to use your evaluators in addition to the default evaluators,
 * you will need to call (for end evaluators):
 * <p>
 * <code>
 * Sentence.initDefaultEndEvaluators();
 * </code>
 * <p>
 * or for start evaluators:
 * <p>
 * <code>
 * Sentence.initDefaultStartEvaluators();
 * </code>
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Sentence {
  
  /**
   * A local copy of sentence ends from 
   * {@link com.orbious.extractor.Config#SENTENCE_ENDS}.
   */
  private static HashSet<Character> allowable_ends;
  
  /**
   * A local copy of punctuation from
   * {@link com.orbious.extractor.Config#PUNCTUATION}
   */
  private static HashSet<Character> punctuation;
  
  /**
   * Logger object.
   */
  private static final Logger logger;

  /**
   * A list of <code>Evaluator</code>'s that are used to determine
   * whether a sentence end is likely.
   */
  private static Vector<Evaluator> end_evaluators;
  
  /**
   * A list of <code>Evaluator</code>'s that are used to determine
   * whether a sentence start is likely.
   */
  private static Vector<Evaluator> start_evaluators;
  
  static {
    allowable_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());
    punctuation = Helper.cvtStringToHashSet(Config.PUNCTUATION.asStr());
    logger = Logger.getLogger(Config.LOGGER_REALM.asStr());
  }
  
  /**
   * Private constructor.
   */
  private Sentence() { }
  
  /**
   * Reloads the local copy of allowable ends.
   */
  public static void reload() {
    allowable_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());
  }
  
  /**
   * Initializes the <code>Evaluator</code>'s that are used for determining
   * the likelihood of sentence starts.
   */
  public static void initDefaultStartEvaluators() {
    start_evaluators = new Vector<Evaluator>(
        Arrays.asList(  new Suspension(),
                new Acronym(),
                new Name(),
                new AbbreviatedName() )); 
  }
  
  /**
   * Adds an evaluate to the <code>Vector</code> of <code>Evaluator</code>'s
   * that are used for determining the likelihood of sentence starts.
   * 
   * @param evaluator   The <code>Evaluator</code> to add.
   */
  public static void addStartEvaluator(Evaluator evaluator) {
    if ( start_evaluators == null ) {
      start_evaluators = new Vector<Evaluator>();
    }
    
    start_evaluators.add(evaluator);
  }
  
  /**
   * Initializes the <code>Evaluator</code>'s that are used for determining
   * the likelihood of sentence ends.
   */
  public static void initDefaultEndEvaluators() {
    end_evaluators = new Vector<Evaluator>(
        Arrays.asList(  new Suspension(), 
                new Acronym(),
                new UrlText(),
                new NumberedHeading(),
                new AbbreviatedName(),
                new InnerQuote() ));   
  }

  /**
   * Adds an evaluate to the <code>Vector</code> of <code>Evaluator</code>'s
   * that are used for determining the likelihood of sentence ends.
   * 
   * @param evaluator   The <code>Evaluator</code> to add.
   */
  public static void addEndEvaluator(Evaluator evaluator) {
    if ( end_evaluators == null ) {
      end_evaluators = new Vector<Evaluator>();
    }
    
    end_evaluators.add(evaluator);
  }
  
  /**
   * Determines if the punctuation specified at <code>idx</code> in the 
   * text buffer <code>buf</code> is a likely sentence end.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in the buffer where a sentence end exists.
   * 
   * @return      <code>true</code> if the position specified by <code>idx</code>
   *              in the <code>buf</code> is a likely sentence end, 
   *              <code>false</code> otherwise.
   */
  public static EndOp isEnd(final char[] buf, int idx) { 
    Evaluator evaluator;
    String debugStr;
    EndOp op;
    int startIdx;

    debugStr = "End Evaluation idx=" + idx + "\n" + 
        Helper.getDebugStringFromCharBuf(buf, idx, 50) + "\n";
    
    op = new EndOp(false, -1);
    
    if ( hasLaterEnd(buf, idx) ) {
      if ( logger.isDebugEnabled() ) {
        debugStr += "\thasLaterEnd=TRUE\n";
        logger.debug(debugStr);
      }
      
      return(op);
    }
    
    if ( (buf[idx] == '"') && hasLaterQuotation(buf, idx) ) {
      if ( logger.isDebugEnabled() ) {
        debugStr += "\thasLaterQuotation=TRUE\n";
        logger.debug(debugStr);
      }
      
      return(op);
    }
    
    startIdx = hasUpper(buf, idx);
    op.startIdx = startIdx;
    
    if ( startIdx < -1 ) {
      if ( logger.isDebugEnabled() ) {
        debugStr += "\thasUpper=FALSE\n";
        logger.debug(debugStr);  
      }

      return(op);
    }
       
    if ( end_evaluators == null ) {
      initDefaultEndEvaluators();
    }
    
    boolean result;
    for ( int i = 0; i < end_evaluators.size(); i++ ) {
      evaluator = end_evaluators.get(i);

      result = false;
      try {
        result = evaluator.evaluate(buf, idx);
      } catch ( Exception e ) {
        logger.fatal("Exception thrown running end evaluator " + evaluator.name() +
            ", most likely the results will be corrupt?", e);
      }

      if ( result ) {
        if ( logger.isDebugEnabled() ) {
          debugStr += "\t" + evaluator.name() + " Result=TRUE\n";
          logger.debug(debugStr);
        }
        return(null);
        
      } else {
        if ( logger.isDebugEnabled() ) {
          debugStr += "\t" + evaluator.name() + " Result=FALSE\n";
        }       
      }
    }
    
    if ( logger.isDebugEnabled() ) {
      logger.debug(debugStr);
    }
    
    op.isEnd = true;
    return(op);
  }

  /**
   * Determines if we have encountered a premature sentence end. For example, 
   * where 2 sentence end's exist sequentially, optionally separated by a space.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in the buffer where a potential sentence end exists.
   * 
   * @return    <code>true</code> if a later potential sentence end was found,
   *            <code>false</code> otherwise.
   */
  protected static boolean hasLaterEnd(final char[] buf, int idx) {
    boolean fndLater;
    boolean inWhitespace;
    int i;
    int j;
    char ch;
    
    i = idx+1;
    fndLater = false;
    inWhitespace = false;
    
    while ( i < buf.length ) {
      ch = buf[i];

      if ( allowable_ends.contains(ch) ) {
        // the special case is " 
        // e.g.
        // drum and cymbals. "Punch" himself
        // we need to check which sentence the " belongs to
        if ( ch != '"' ) {
          fndLater = true;
          break;
        } else {
          j = i+1;
          while ( j < buf.length ) {
            ch = buf[j];
            if ( ch == '"' ) {
              // we have found another " before a sentence end
              // therefore we use the existing punctuation
              break;
            } else if ( allowable_ends.contains(ch) ) {
              // we have not found another ", therefore the "
              // must be tied to the existing sentence
              fndLater = true;
              break;
            }
            j++;
          }
          if ( j == (i+1) ) {
            // we are near the end of the buffer,
            // and could not tie " to a sentence
            // therefore consider later punctuation
            fndLater = true;
          }
          break;
        }
      
      } else if ( Character.isLetterOrDigit(ch) ) {
        break;
        
      } else if ( inWhitespace ) {
        if ( Character.isWhitespace(ch) ) { 
          continue;
        }
        
        if ( allowable_ends.contains(ch) ) {
          fndLater = true;
        }
        break;

      } else if ( Character.isWhitespace(ch) ) {
        if ( inWhitespace ) {
          break;
        }
        inWhitespace = true;
      } 

      i++;
    }
    
    return(fndLater);
  }
  
  /**
   * Determines if a punctuation mark is defined before a sentence boundary.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in the buffer where a quotation mark.
   * 
   * @return     <code>true</code> if a quotation mark appears before a sentence 
   *              end, <code>false</code> otherwise.
   */
  protected static boolean hasLaterQuotation(final char[] buf, int idx) {
    int i;
    boolean fndLater;
    char ch;
    
    i = idx+1;
    fndLater = false;
    
    while ( i < buf.length ) {
      ch = buf[i];
      if ( ch == '"' ) {
        fndLater = true;
        break;
      } else if ( allowable_ends.contains(ch) ) {
        break;
      }
      i++;
    }
    
    return(fndLater);
  }
  
  /**
   * Determines if a letter is capitalized (i.e. a potential start) 
   * after a potential end.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in the buffer where a sentence end exists.
   * 
   * @return    
   * <ul>
   * <li><code>-1</code> if the extremium's were reached.
   * <li><code>-2</code> if no uppercase character was found.
   * <li>Otherwise returns the position in the buffer where the potential
   * sentence start was found.
   */
  protected static int hasUpper(final char[] buf, int idx) {
    int i;
    char ch;

    if ( idx+1 >= buf.length ) {
      return(-1);
    }
    
    i = idx+1;
    ch = buf[i];
    while ( Character.isWhitespace(ch) || 
          punctuation.contains(ch) ) {
      i++;
      if ( i >= buf.length ) {
        return(-1);
      }
      ch = buf[i];
    }
    
    if ( Character.isUpperCase(ch) || Character.isDigit(ch) ) {
      return(i);
    }
    
    return(-2);
  }  

  /**
   * Determines if the punctuation specified at <code>idx</code> in the 
   * text buffer <code>buf</code> is a likely sentence start.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in the buffer where a potential sentence start exists.
   * 
   * @return      <code>true</code> if the position specified by <code>idx</code>
   *              in the <code>buf</code> is a likely sentence start, 
   *              <code>false</code> otherwise.
   */
  public static StartOp isStart(final char[] buf, int idx) {
    int stopIdx;
    Evaluator evaluator;
    String debugStr;
    StartOp op;
   
    debugStr = "Start Evaluation idx=" + idx + "\n" + 
      Helper.getDebugStringFromCharBuf(buf, idx, 50) + "\n";

    op = new StartOp(false, -1);
    
    if ( !Character.isUpperCase(buf[idx]) ) {
      logger.debug(debugStr + "\tResult=FALSE (no uppercase).\n");
      return(null);
    }
    
    if ( (idx-1) < 0 ) {
      logger.debug(debugStr + "\tResult=TRUE (index=0).\n");
      op.isStart = true;
      return(op);
    }
    
    stopIdx = hasStop(buf, idx);
    op.stopIdx = stopIdx;
    
    if ( stopIdx < -1 ) {
      if ( logger.isDebugEnabled() ) {
        debugStr += "\thasStop=FALSE\n";
        logger.debug(debugStr);  
      }

      return(null);
    }

    
    // now run some evaluators
    if ( start_evaluators == null ) {
      initDefaultStartEvaluators();
    }
    
    boolean result;
    for ( int i = 0; i < start_evaluators.size(); i++ ) {
      evaluator = start_evaluators.get(i);
      result = false;
      
      try {
        result = evaluator.evaluate(buf, idx-1);
      } catch ( Exception e ) {
        logger.fatal("Exception thrown running end evaluator " + evaluator.name() +
            ", most likely the results will be corrupt?", e);
      }
      
      if ( result ) {
        if ( logger.isDebugEnabled() ) {
          debugStr += "\t" + evaluator.name() + " Result=TRUE\n";
          logger.debug(debugStr);
        }
        return(op);
      } else {
        if ( logger.isDebugEnabled() ) {
          debugStr += "\t" + evaluator.name() + " Result=FALSE\n";
        }         
      }
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug(debugStr);
    }
    
    op.isStart = true;
    return(op);
  }

  /**
   * Finds the previous sentence end for a likely sentence start.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in the buffer where a likely sentence start exists
   * 
   * @return
   * <ul>
   * <li><code>-1<code> if the extremium's were reached.
   * <li><code>-2</code> if no sentence end was found.
   * <li>Otherwise returns the position in teh buffer where the potential
   * sentence end was found.
   */
  protected static int hasStop(final char[] buf, int idx) {
    int i;
    char ch;

    if ( idx-1 < 0 ) {
      return(-1);
    }
    
    i = idx-1;
    ch = buf[i];
    
    while ( Character.isWhitespace(ch) ) {
      i--;
      if ( i < 0 ) {
        return(-1);
      }
      
      ch = buf[i];
    }
    
    if ( allowable_ends.contains(ch) ) {
      return(i);
    }
    
    return(-2);
  }
  
  
  /**
   * An inner class used to return the results of 
   * {@link Sentence#isStart(char[], int)}.
   */
  static class StartOp {
    /**
     * Whether or not a likely sentence start was found.
     */
    private boolean isStart;
    
    /**
     * The previous sentence end for a likely sentence start.
     */
    private int stopIdx;

    /**
     * Initializes an empty <code>StartOp</code>.
     */
    public StartOp() { }

    /**
     * Initializes the <code>StartOp</code>.
     * 
     * @param isStart   Whether or not a likely sentence start was found.
     * @param stopIdx   The previous end for a likely sentence start.
     */
    public StartOp(boolean isStart, int stopIdx) {
      this.isStart = isStart;
      this.stopIdx = stopIdx;
    }
    
    /**
     * Accessor for <code>isStart</code>.
     * 
     * @return    <code>isStart</code>  
     */
    public boolean isStart() {
      return(isStart);
    }
    
    /**
     * Setter for <code>isStart</code>.
     * 
     * @param isStart   Sets <code>isStart</code>.
     */
    public void isStart(boolean isStart) {
      this.isStart = isStart;
    }
    
    /**
     * Accessor for <code>stopIdx</code>.
     * 
     * @return    <code>stopIdx</code>
     */
    public int stopIdx() {
      return(stopIdx);
    }
    
    /**
     * Setter for <code>stopIdx</code>.
     * 
     * @param stopIdx   Sets <code>stopIdx</code>.
     */
    public void stopIdx(int stopIdx) {
      this.stopIdx = stopIdx;
    }
  }
  
  
  /**
   * An inner class used to return the results of 
   * {@link Sentence#isEnd(char[], int)}.
   * 
   * @author dave
   * @version 1.0
   * @since 1.0
   */
  static class EndOp {
    /**
     * Whether or not a likely sentence end was found.
     */
    private boolean isEnd;
    
    /**
     * The next sentence start for a likely sentence end.
     */
    private int startIdx;
    
    /**
     * Initializes an empty <code>EndOp</code>.
     */
    public EndOp() { }
    
    /**
     * Initializes the <code>StartOp</code>.
     * 
     * @param isEnd   Whether or not a likely sentence end was found.
     * @param startIdx   The next sentence start for a likely sentence end.
     */
    public EndOp(boolean isEnd, int startIdx) { 
      this.isEnd = isEnd;
      this.startIdx = startIdx;
    }
    
    /**
     * Accessor for <code>isEnd</code>.
     * 
     * @return    <code>isEnd</code>  
     */
    public boolean isEnd() {
      return(isEnd);
    }
    
    /**
     * Setter for <code>isEnd</code>.
     * 
     * @param isEnd   Sets <code>isEnd</code>.
     */
    public void isEnd(boolean isEnd) {
      this.isEnd = isEnd;
    }
    
    /**
     * Accessor for <code>startIdx</code>.
     * 
     * @return    <code>startIdx</code>
     */
    public int startIdx() {
      return(startIdx);
    }
    
    /**
     * Setter for <code>startIdx</code>.
     * 
     * @param startIdx   Sets <code>startIdx</code>.
     */
    public void startIdx(int startIdx) {
      this.startIdx = startIdx;
    }
  }
}
