package com.orbious.extractor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.orbious.extractor.evaluator.Acronym;
import com.orbious.extractor.evaluator.Evaluator;
import com.orbious.extractor.evaluator.Name;
import com.orbious.extractor.evaluator.Suspension;
import com.orbious.extractor.evaluator.UrlText;
import com.orbious.util.Helper;

/**
 * $Id$
 * <p>
 * Provides static methods for Sentence operations.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Sentence {
  
  /**
   * A local copy of sentence ends from 
   * {@link com.orbious.separator.Config#SENTENCE_ENDS}.
   */
  private static HashSet<Character> allowable_ends;
  
  /**
   * Logger object.
   */
  private static final Logger logger;

  /**
   * A list of <code>Evaluator</code>'s that are used to determine
   * whether a sentence end is valid.
   */
  private static Vector<Evaluator> end_evaluators;
  
  /**
   * 
   */
  private static Vector<Evaluator> start_evaluators;
  
  /**
   * Static Initializer block.
   */
  static {
    allowable_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());
    logger = Logger.getLogger(Config.LOGGER_REALM.asStr());
  }
  
  /**
   * Private constructor.
   */
  private Sentence() { }
  
  /**
   * Reloads the local copy of sentence ends from 
   * {@link com.orbious.separator.Config#SENTENCE_ENDS}
   * and the default <code>Evaluator</code>'s.
   */
  public static void reload() {
    allowable_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());
  }
  
  public static void initDefaultStartEvaluators() {
    start_evaluators = new Vector<Evaluator>(
        Arrays.asList(  new Suspension(),
                new Acronym(),
                new Name() )); 
  }
  
  public static void addStartEvaluator(Evaluator evaluator) {
    if ( start_evaluators == null ) {
      start_evaluators = new Vector<Evaluator>();
    }
    
    start_evaluators.add(evaluator);
  }
  
  /**
   * Initializes the <code>Evaluator</code>'s that are
   * used to determine whether a sentence end is valid.
   */
  public static void initDefaultEndEvaluators() {
    end_evaluators = new Vector<Evaluator>(
        Arrays.asList(  new Suspension(), 
                new Acronym(),
                new UrlText() ));   
  }

  /**
   * Adds a non-default <code>Evaluator</code> to the list
   * of evaluators that are used to determine whether a sentence
   * end is valid.
   * 
   * @param evaluator  The <code>Evaluator</code> to add
   *                   for determining whether a sentence end 
   *                   is valid.
   */
  public static void addEndEvaluator(Evaluator evaluator) {
    if ( end_evaluators == null ) {
      end_evaluators = new Vector<Evaluator>();
    }
    
    end_evaluators.add(evaluator);
  }
  
  /**
   * Determines if the punctuation specified at <code>idx</code> in the 
   * text buffer <code>buf</code> is a valid sentence end.
   * 
   * The end algorithm:
   * - If the next character is capitalized and not a name/acronym/suspension
   *   consider a sentence end.
   * - If we have reached the end of the buffer and the current punctuation mark
   *   is not part of a name/acronym/suspension consider a sentence end.
   *
   * @param buf   Text buffer.
   * @param idx   Position in the buffer where a sentence end exists.
   * 
   * @return      <code>true</code> if the position in the <code>buf</code>
   *              is a valid sentence end, <code>false</code> otherwise.
   */
  public static EndOp isEnd(final char[] buf, int idx) { 
    Evaluator evaluator;
    String debugStr;
    EndOp op;
    int startIdx;

    debugStr = "End Evaluation idx=" + idx + "\n" + 
        Helper.getDebugStringFromCharBuf(buf, idx, 50) + "\n";
    
    op = new EndOp(false, -1);
    
    if ( hasLaterPunctuation(buf, idx) ) {
      if ( logger.isDebugEnabled() ) {
        debugStr += "\thasLaterPunctuation=TRUE\n";
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
    
    for ( int i = 0; i < end_evaluators.size(); i++ ) {
      evaluator = end_evaluators.get(i);

      if ( evaluator.evaluate(buf, idx) ) {
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
  protected static boolean hasLaterPunctuation(final char[] buf, int idx) {
    boolean fndLater;
    boolean inWhitespace;
    int i;
    char ch;
    
    i = idx+1;
    fndLater = false;
    inWhitespace = false;
    
    while ( i < buf.length ) {
      ch = buf[i];

      if ( allowable_ends.contains(ch) ) {
        fndLater = true;
        break;
      
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
   * Determines if the letter is capitalized (i.e. a potential start) 
   * after a potential end.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in the buffer where a sentence end exists.
   * 
   * @return      <code>-1</code> if the extremium's were reached.
   *              <code>-2</code> if no uppercase was found
   *              <code>idx</code> position in the buffer where the potential
   *              start was found.
   */
  protected static int hasUpper(final char[] buf, int idx) {
    int i;
    char ch;

    if ( idx+1 >= buf.length ) {
      return(-1);
    }
    
    i = idx+1;
    ch = buf[i];
    while ( Character.isWhitespace(ch) ) {
      i++;
      if ( i >= buf.length ) {
        return(-1);
      }
      ch = buf[i];
    }
    
    if ( Character.isUpperCase(ch) ) {
      return(i);
    }
    
    return(-2);
  }  
    
  /**
   * The start algorithm:
   * <ul>
   * <li>If the word is capitalized and the previous character is a sentence end 
   *   consider a sentence start.
   * <li>If MAX_SENTENCE_LENGTH is exceeded during this process return false
   *   (this check is performed in {@link Sentence#previous(char[], int)}).
   *
   * @param buf   Text buffer.
   * @param idx   Position in the buffer for a potential sentence start.
   * 
   * @return    <code>true</code> if the sentence start is valid,
   *            <code>false</code> otherwise.
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
    
    for ( int i = 0; i < start_evaluators.size(); i++ ) {
      evaluator = start_evaluators.get(i);
      if ( evaluator.evaluate(buf, idx-1) ) {
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
   * 
   * @param buf
   * @param idx
   * @return
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
  
  
  static class StartOp {
    private boolean isStart;
    private int stopIdx;

    public StartOp() { }
    
    public StartOp(boolean isStart, int stopIdx) {
      this.isStart = isStart;
      this.stopIdx = stopIdx;
    }
    
    public boolean isStart() {
      return(isStart);
    }
    
    public void isStart(boolean isStart) {
      this.isStart = isStart;
    }
    
    public int stopIdx() {
      return(stopIdx);
    }
    
    public void stopIdx(int stopIdx) {
      this.stopIdx = stopIdx;
    }
  }
  
  static class EndOp {
    private boolean isEnd;
    private int startIdx;
    
    public EndOp() { }
    
    public EndOp(boolean isEnd, int startIdx) { 
      this.isEnd = isEnd;
      this.startIdx = startIdx;
    }
    
    public boolean isEnd() {
      return(isEnd);
    }
    
    public void isEnd(boolean isEnd) {
      this.isEnd = isEnd;
    }
    
    public int startIdx() {
      return(startIdx);
    }
    
    public void startIdx(int startIdx) {
      this.startIdx = startIdx;
    }
  }
}
