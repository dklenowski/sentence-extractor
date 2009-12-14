package com.orbious.extractor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;
import org.apache.log4j.Logger;

import com.orbious.extractor.evaluator.Acronym;
import com.orbious.extractor.evaluator.Evaluator;
import com.orbious.extractor.evaluator.Name;
//import com.orbious.extractor.evaluator.Name;
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
  private static HashSet<Character> sentence_ends;
  
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
	  sentence_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());
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
    sentence_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());
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
  protected static boolean isEnd(final char[] buf, int idx) { 
    Evaluator evaluator;
    String debugStr;

    debugStr = "End Evaluation idx=" + idx + "\n" + 
        Helper.getDebugStringFromCharBuf(buf, idx, 50) + "\n";
    
    if ( !hasUpper(buf, idx) ) {
      if ( logger.isDebugEnabled() ) {
        debugStr += "\thasUpper=FALSE\n";
        logger.debug(debugStr);  
      }

      return(false);
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
        return(false);
        
      } else {
        if ( logger.isDebugEnabled() ) {
          debugStr += "\t" + evaluator.name() + " Result=FALSE\n";
        }   	  
      }
    }
    
    if ( logger.isDebugEnabled() ) {
    	logger.debug(debugStr);
    }
    
    return(true);
  }
 
  /**
   * Determines if the letter is capitalized after a potential end. If so,
   * returns <code>true</code>, otherwise <code>false</code>.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in the buffer where a sentence end exists.
   * 
   * @return      The end of a capitalized word post the sentence end,
   *              otherwise <code>idx</code>.
   */
  protected static boolean hasUpper(final char[] buf, int idx) {
    int i;
    boolean inWhitespace;
    boolean fndUpper;
    char ch;
    
    if ( idx+1 >= buf.length ) {
      return(true);
    }
    
    i = idx+1;
    inWhitespace = false;
    fndUpper = false;
    
    while ( i < buf.length ) {
      ch = buf[i];
      if ( Character.isWhitespace(ch) ) {
        inWhitespace = true;
      } else if ( Character.isLetter(ch) && inWhitespace ) {
        if ( Character.isUpperCase(ch) ) {
          fndUpper = true;
        }
        break;
      }

      i++;
    }
	
    return(fndUpper);
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
  protected static boolean isStart(final char[] buf, int idx) {
	  boolean fndStart;
	  boolean inWhitespace;
	  int i;
	  char ch;
	  Evaluator evaluator;
	  String debugStr;
   
    debugStr = "Start Evaluation idx=" + idx + "\n" + 
      Helper.getDebugStringFromCharBuf(buf, idx, 50) + "\n";
	  
    if ( (idx-1) < 0 ) {
      logger.debug(debugStr + "\tResult=TRUE (index=0).\n");
    	return(true);
    }

    i = idx-1;
    fndStart = false;
    inWhitespace = false;
      
    while ( i >= 0 ) {
    	ch = buf[i];
    	if ( inWhitespace ) {
    		if ( sentence_ends.contains(ch) ) {
    			// a sentence start
    			fndStart = true;
    		}
    		break;
    	} else if ( Character.isWhitespace(ch) ) {
    		inWhitespace = true;
    	} 
    	  
    	i--;
    }

    debugStr += "\tStartResult=" + String.valueOf(fndStart).toUpperCase() + "\n";
    
    if ( !fndStart ) {
      logger.debug(debugStr);     
      return(fndStart);
    }
    
    // now run some evaluators
    if ( start_evaluators == null ) {
      initDefaultStartEvaluators();
    }
    
    for ( int j = 0; j < start_evaluators.size(); j++ ) {
      evaluator = start_evaluators.get(j);
      if ( evaluator.evaluate(buf, idx-1) ) {
        if ( logger.isDebugEnabled() ) {
          debugStr += "\t" + evaluator.name() + " Result=TRUE\n";
          logger.debug(debugStr);
        }
        return(false);
      } else {
        if ( logger.isDebugEnabled() ) {
          debugStr += "\t" + evaluator.name() + " Result=FALSE\n";
        }         
      }
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug(debugStr);
    }
    
    return(fndStart);
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
      
      if ( sentence_ends.contains(ch) ) {
        fndLater = true;
        break;
      
      } else if ( Character.isLetterOrDigit(ch) ) {
        break;
        
      } else if ( inWhitespace ) {
        if ( Character.isWhitespace(ch) ) { 
          continue;
        }
        
        if ( sentence_ends.contains(ch) ) {
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
   * 
   * @param buf
   * @param idx
   * @return
   */
  
  protected static Vector<String> previous(final char[] buf, int idx) {
    Vector<String> words;
    char ch;
    boolean hasLetter;
    boolean hasStart;
    String reverseWd;
    String wd;
    String debugStr;
    int i;  
    
    words = new Vector<String>();
    i = idx;
    reverseWd = "";
    hasLetter = false;
    hasStart = false;
    debugStr = "\n";
    
    while ( (i >= 0) && (i < buf.length) ) {
      ch = buf[i];

      if ( Character.isLetterOrDigit(ch) ) {
        // a letter, add it to the word
        reverseWd += ch;
        hasLetter = true;
        
      } else if ( Character.isWhitespace(ch) ) {
        wd = new StringBuffer(reverseWd).reverse().toString();
        if ( wd.length() == 0 ) {
          // there needs to be something in the buffer ..
            i--;
            continue;
        }
        words.add(wd);
        if ( words.size() >= Config.MAX_SENTENCE_LENGTH.asInt() ) {
          debugStr += "\tMAX_SENTENCE_LENGTH exceeded\n";
          break;
        }
          
        reverseWd = "";
        hasLetter = false;
        
        if ( Character.isUpperCase(wd.charAt(0)) ) {
          // capitalised, a potential start, see if the previous character 
          // is a sentence_end
          //
          hasStart = isStart(buf, i+1);
          if ( hasStart ) {
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
          if ( new UrlText().evaluate(buf, i) ) {
            reverseWd += ch;
          } else if ( ch != ',' && !sentence_ends.contains(ch) ) {
            reverseWd += ch;
          } else {
            // the exceptions are sentence ends are ','
            words.add(Character.toString(ch));
          } 
        } else {
          // punctuation, add as a separate 'wd'
          words.add(Character.toString(ch));
        }
      }
      
      i--;
    } 

    if ( hasLetter ) {
      wd = new StringBuffer(reverseWd).reverse().toString();
      words.add(wd); 
      
      if ( Character.isUpperCase(wd.charAt(0)) ) {
        hasStart = isStart(buf, i);
      }
      
      if ( logger.isDebugEnabled() ) {
        debugStr += "\tOverfill=" + wd + "\n";
      }
    }

    Collections.reverse(words);

    if ( logger.isDebugEnabled() ) {
      logger.debug(debugStr);
      logger.debug(Helper.cvtVectorToString(words));
    }
    
    if ( !hasStart ) {
      logger.warn("Failed to find sentence start for " + 
          Helper.cvtVectorToString(words));
    }
    
    return(words);
  }

  /**
   * Returns the previous sentence from a text buffer.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in the buffer where a sentence end exists.
   *             
   * @return    A <code>Vector</code> containing the words that constitute
   *            a sentence or <code>null</code> if a sentence could not be formed.
   */
  public static Vector<String> getPreviousSentence(final char[] buf, int idx) 
  	throws SentenceException {
	
    boolean endResult;
    boolean endLater;
	
    if ( logger.isDebugEnabled() ) {
      logger.debug("Beginning EXTRACTION for idx=" + idx + "\n" + 
          Helper.getDebugStringFromCharBuf(buf, idx, 100));
    }
    
    if ( (idx < 0) || (idx > buf.length) ) {
      throw new ArrayIndexOutOfBoundsException("Invalid index=" + idx);
    } else if ( !sentence_ends.contains(buf[idx]) ) {
      throw new SentenceException("Not sentence end at index=" + idx);
    }

    endResult = isEnd(buf, idx);
    if ( !endResult ) {
    	return(null);
    }    

    endLater = hasLaterPunctuation(buf, idx);
    if ( endLater ) {
      return(null);
    }
    
    return(previous(buf, idx));
  }
}
