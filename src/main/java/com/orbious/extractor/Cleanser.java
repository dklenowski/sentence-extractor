package com.orbious.extractor;

import java.util.HashSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.orbious.util.Helper;

/**
 * $Id: Cleanser.java 14 2009-12-06 10:03:53Z app $
 * <p>
 * Provides static methods for cleaning up text within a document
 * ready for preparation for sentence separation.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Cleanser {

  /**
   * A local copy of preserved punctuation from 
   * {@link com.orbious.separator.Config#PRESERVED_PUNCTUATION}.
   */
  private static HashSet<Character> preserved_punctuation;
  
  /**
   * Logger object.
   */
  private static final Logger logger;
  
  /**
   * Private Constructor.
   */
  private Cleanser() { }
  
  /**
   * Static initializer block.
   */
  static {
	  preserved_punctuation = Helper.cvtStringToHashSet(
			  Config.PRESERVED_PUNCTUATION.get());
	  logger =  Logger.getLogger(Config.LOGGER_REALM.get());
  }
  
  /**
   * Reloads the local copy of preserved punctuation from 
   * {@link com.orbious.separator.Config#PRESERVED_PUNCTUATION}.
   */
  public static void reload() {
	  preserved_punctuation = Helper.cvtStringToHashSet(
			  Config.PRESERVED_PUNCTUATION.get());
  }
  
  /**
   * Removes whitespace from the line at index <code>idx</code>
   * in <code>lines</code>. All multiple occurrences of whitespace are 
   * replaced with a single whitespace and newlines are removed completely.
   * 
   * @param text  A <code>Vector</code> of raw text.
   * @param idx The index in <code>Vector</code> to remove unnecessary 
   *        whitespace.
   * 
   * @return  <code>null</code> if the line contains no letters, digits,
   *          punctuation, otherwise the line with multiple whitespace
   *          and newlines removed.
   */
  public static String removeWhitespace(Vector<String> text, int idx) { 
    boolean inWhitespace;
    boolean hasText;
    boolean fndHyphen;
    char[] buf;
    char ch;
    int ct;
    String str;
    
    if ( (idx < 0) || (idx >= text.size()) ) {
      throw new ArrayIndexOutOfBoundsException("Invalid index=" + idx);
    }
    
    buf = text.get(idx).toCharArray();
    
    if ( buf.length == 0 ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug("No text converted to char array, idx=" + idx + 
            " Text=|" + text.get(idx) + "|");
      }
      return(null);
    }
    
    inWhitespace = false;
    hasText = false;
    str = "";
    for ( int i = 0; i < buf.length; i++ ) {
      ch = buf[i];
      if ( !Character.isWhitespace(ch) ) {
        str += ch;
        hasText = true;
        inWhitespace = false;
      } else {
        if ( !inWhitespace ) {
          if ( (i != 0) && (ch != '\n') ) {
            // we dont add whitespace to the start
            str += ch;
          }
          inWhitespace = true;
        }
      }
    }
    
    if ( !hasText ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug("No text found, idx=" + idx + " Text=|" + 
            text.get(idx) + "|");
      }
      return(null);
    }
    
    // we need to determine whether or not to add a whitespace at the
    // end of the text (because we have removed the newline)
    // we dont add a whitespace if the last character is a '-' 
    // otherwise we add a space
    buf = str.toCharArray();
    ct = buf.length;
    fndHyphen = false;
    for ( int i = ct-1; i >= 0; i-- ) {
      ch = buf[i];
      if ( Character.isWhitespace(ch) ) {
        continue;
      } else {
        // we have text
        if ( ch == '-' ) {
          ct = i+1;
          fndHyphen = true;
        }
        break;
      }
    }

    str = String.copyValueOf(buf, 0, ct);
    if ( !fndHyphen && !Character.isWhitespace(str.charAt(str.length()-1)) ) {
      str += " ";
    }
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("Idx=" + idx + " Cleansed=|" + str + 
          "|\nOriginal=" + text.get(idx) + "|");
    }
    return(str);
  }

  /**
   * Clean's a <code>Vector</code> of <code>String</code> words
   * in preparation for programmatic analysis. 
   * <ul>
   * <li>All punctuation is separated by spaces.
   * <li>Punctuation at either ends (e.g. '_' is removed from '_space_')
   *     is removed.
   * <li>Punctuation within a word or an apostrophe at the 
   *     end of a word (e.g. 'Fred's, boys', time-line) is preserved.
   *     
   * @param words   A list of words to process.
   * @return      A <code>Vector</code> of <code>String</code>'s 
   *          representing a cleansed version
   *          of the <code>words</code>.
   */
  
  public static Vector<String> cleanWords(Vector<String> words) {
    Vector<String> cleansed;
    String wd;
    char[] buf;
    char ch;
    boolean hasData;
    
    cleansed = new Vector<String>();
    
    for ( int i = 0; i < words.size(); i++ ) {
      buf = words.get(i).toCharArray();
      wd = "";
      hasData = false;
      for ( int j = 0; j < buf.length; j++ ) {
        ch = buf[j];
        if ( Character.isLetterOrDigit(ch) ) {
          wd += ch;
          hasData = true;
        } else if ( !Character.isWhitespace(ch) ) {
          // we have punctuation
          // we want to remove any punctuation from either end
          // 
          if ( preserved_punctuation.contains(ch) ) {
            wd += ch;
            hasData = true;
          } else if ( ch == '\'' ) {
            // we keep this punctuation
            // irrespective of the there position
            // e.g. all the boys'
            wd += ch;
            hasData = true;
          } else if ( (j != 0) && (j != buf.length-1) ) {
            // we keep this punctuation
            // e.g. flock of sheep's, time-line
            wd += ch;
            hasData = true;
          }
        }
      }
      
      if ( !hasData ) {
        continue;
      }
      
      // we need to use a vector, because the last character might
      // be garbage and we would mean there is an additional 
      // space at the end.
      cleansed.add(wd);
    }
    
    if ( logger.isDebugEnabled() ) {
      String debugStr = "Original=";
      for ( int i = 0; i < words.size(); i++ ) {
        debugStr += "[" + i + "]=|" + words.get(i);
        if ( i+1 >= words.size() ) {
          debugStr += "|\n";
        } else {
          debugStr += "| ";
        }
      }
      
      debugStr += "Cleansed=|";
      for ( int i = 0; i < cleansed.size(); i++ ) {
        debugStr += "[" + i + "]=|" + cleansed.get(i);
        if ( i+1 >= cleansed.size() ) {
          debugStr += "|\n";
        } else {
          debugStr += "| ";
        }
      }
      
      logger.debug(debugStr);
    }
  
    return(cleansed);   
  }
  
  /**
   * Clean's a <code>Vector</code> of <code>String</code> words
   * in preparation for programmatic analysis. 
   * <ul>
   * <li>All punctuation is separated by spaces.
   * <li>Punctuation at either ends (e.g. '_' is removed from '_space_')
   *     is removed.
   * <li>Punctuation within a word or an apostrophe at the 
   *     end of a word (e.g. 'Fred's, boys', time-line) is preserved.
   *     
   * @param words   A list of words to process.
   * @return      A <code>String</code> representing a cleansed version
   *          of the <code>words</code>.
   */
  public static String cleanWordsAsStr(Vector<String> words) {
    Vector<String> cleansed = cleanWords(words);
    String str = "";

    for ( int i = 0; i < cleansed.size(); i++ ) {
      if ( i+1 < cleansed.size() ) {
        str += cleansed.get(i) + " ";
      } else {
        str += cleansed.get(i);
      }
    }
    
    return(str);
  }
}
