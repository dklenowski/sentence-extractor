package com.orbious.extractor;

import java.util.Arrays;
import java.util.Collections;
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
  private static HashSet<Character> sentence_ends = Config.getSentenceEnds();
  
  /**
   * Logger object.
   */
  private static final Logger logger = Logger.getLogger(Config.LOGGER_REALM);
  
  private static Vector<Evaluator> start_evaluators;
  
  private static Vector<Evaluator> end_evaluators;
  
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
    sentence_ends = Config.getSentenceEnds();
    initDefaultStartEvaluators();
    initDefaultEndEvaluators();
  }
  
  /**
   * Initializes the <code>Evaluator</code>'s that are
   * used to determine whether a sentence start is valid.
   */
  public static void initDefaultStartEvaluators() {
    start_evaluators = new Vector<Evaluator>(
        Arrays.asList(  new Name(), 
                new Suspension(), 
                new Acronym() ));
  }

  /**
   * Adds a non-default <code>Evaluator</code> to the list
   * of evaluators that are used to determine whether a sentence
   * start is valid.
   * 
   * @param evaluator   The <code>Evaluator</code> to add
   *            for determining whether a sentence start 
   *            is valid.
   */
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
   * @param evaluator   The <code>Evaluator</code> to add
   *            for determining whether a sentence end 
   *            is valid.
   */
  public static void addEndEvaluator(Evaluator evaluator) {
    if ( end_evaluators == null ) {
      end_evaluators = new Vector<Evaluator>();
    }
    
    end_evaluators.add(evaluator);
  }
  
  /**
   * Returns the previous sentence from a text buffer.
   * 
   * @param buf The <code>Character</code> buffer to extract the previous 
   *        sentence from.
   * @param idx The index in the buffer <code>buf</code> to begin sentence 
   *        extraction.
   * @return    A <code>Vector</code> containing the words that constitute
   *        a sentence.
   */
  public static Vector<String> getPreviousSentence(char[] buf, int idx)
    throws SentenceException {
    Evaluator evaluator;
    Vector<String> words;
    char ch;
    boolean hasLetter;
    boolean fndStart;
    String reverseWd;
    String wd;
    String debugStr;
    int i;
    
    if ( (idx < 0) || (idx > buf.length) ) {
      throw new ArrayIndexOutOfBoundsException("Invalid index=" + idx);
    }

    if ( !sentence_ends.contains(buf[idx]) ) {
      throw new SentenceException("Not sentence end at index=" + idx);
    }
    
    if ( start_evaluators == null ) {
      initDefaultStartEvaluators();
    }
    
    if ( end_evaluators == null ) {
      initDefaultEndEvaluators();
    }
    
    //
    // check the end first
    //
    for ( i = 0; i < end_evaluators.size(); i++ ) {
      evaluator = end_evaluators.get(i);

      if ( evaluator.evaluate(buf, idx) ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug("End Evaluator '" + evaluator.name() + 
              "' returned true for idx=" + idx + " buf=" +
              Helper.getStringFromCharBuf(buf, idx, 20));
        }
        return(null);
      }
    }
    
    //
    // now we need to find the start
    //
    words = new Vector<String>();
    hasLetter = false;
    fndStart = false;
    reverseWd = "";
    debugStr = "";
    i = idx;
    
    while ( (i >= 0) && (i < buf.length) ) {
      ch = buf[i];
      
      if ( Character.isLetterOrDigit(ch) ) {
        // a letter, add it to the word
        reverseWd += ch;
        hasLetter = true;
        
      } else if ( Character.isWhitespace(ch) ) {
        wd = new StringBuffer(reverseWd).reverse().toString();
        words.add(wd);
          
        reverseWd = "";
        hasLetter = false;
          
        if ( logger.isDebugEnabled() ) {
          debugStr += " idx=" + i + " wd=" + wd;
        }
          
        if ( Character.isUpperCase(wd.charAt(0)) ) {
          // capitalised, a potential start
          //
          fndStart = true;
          for ( int j = 0; j < start_evaluators.size(); j++ ) {
            evaluator = start_evaluators.get(j);
            
            if ( evaluator.evaluate(wd) ) {
              if ( logger.isDebugEnabled() ) {
                logger.debug("Start Evaluator '" + evaluator.name() + 
                    "' returned true for idx=" + idx + " wd=" + wd + 
                    " buf=" +
                    Helper.getStringFromCharBuf(buf, idx, 40));
              }
              fndStart = false;
              break;  
            }
          }
          
          if ( fndStart ) {
            debugStr += " sentStart=" + wd;
            break;
          }
        }
      } else {
        // if we get to here we have punctuation
        if ( hasLetter ||
            ((i-1 >= 0) && Character.isLetter(buf[i-1])) ) {
          // the punctuation is attached to the word e.g. type's, time-line
          // hasLetter assumes there is text to the right,
          // if that fails we need to test there is text to the left
          if ( ch != ',' && !sentence_ends.contains(ch) ) {
            reverseWd += ch;
          } else {
            // the exceptions are sentence ends are ','
            words.add(Character.toString(ch));
            if ( logger.isDebugEnabled() ) {
              debugStr += " punct [" + i + "]=" + ch; 
            }
          } 
        } else {
          // punctuation, add as a separate 'wd'
          words.add(Character.toString(ch));
          if ( logger.isDebugEnabled() ) {
            debugStr += " punct [" + i + "]=" + ch; 
          }
        }
      }
      
      i--;
    } 

    if ( hasLetter ) {
      wd = new StringBuffer(reverseWd).reverse().toString();
      words.add(wd); 
      
      if ( Character.isUpperCase(wd.charAt(0)) ) {
        fndStart = true;
      }
      
      if ( logger.isDebugEnabled() ) {
        debugStr += " overfill=" + wd;
      }
      
      
    }

    Collections.reverse(words);

    if ( logger.isDebugEnabled() ) {
      logger.debug(debugStr);
      logger.debug(getSentenceAsDebugStr(words));
    }
    
    if ( !fndStart ) {
      logger.warn("Failed to find sentence start for " + 
          getSentenceAsDebugStr(words));
    }
    
    return(words);  
  }
  
  /**
   * Utility method, converts a <code>Vector</code> of words in a sentence
   * to a debugging string.
   * 
   * @param words   A <code>Vector</code> containing words in a sentence.
   * @return      The sentence converted to a debugging string.
   */
  public static String getSentenceAsDebugStr(Vector<String> words) {
    String str = "";
    for ( int i = 0; i < words.size(); i++ ) {
      str += "[" + i + "]=" + words.get(i) + " ";
    }
    return(str);
  }
}
