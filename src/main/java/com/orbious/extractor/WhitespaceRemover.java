package com.orbious.extractor;

// $Id$

import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * Provides static methods to remove whitespace.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class WhitespaceRemover {

  /**
   * The logger object.
   */
  private static Logger logger;
  
  static {
    logger = Logger.getLogger(Config.LOGGER_REALM.asStr());
  }
  
  /**
   * Private constructor.
   */
  private WhitespaceRemover() { }
  
  
  /**
   * Removes excessive whitespace characters from position <code>idx</code>
   * in the <code>Vector</code> <code>text</code>. If the <code>String</code>
   * in <code>text</code> has a hyphen at the end with not append
   * a whitespace to the end of the returned <code>String</code>.
   * 
   * @param text  A <code>Vector</code> containing text.
   * @param idx   The position <code>idx</code> in <code>text</code>.
   *    
   * @return    Return's <code>null</code> if no text was found, otherwise 
   *            returns a <code>String</code> with excessive whitespace removed.
   */
  public static String remove(final Vector<String> text, int idx) {
    boolean inWhitespace;
    boolean hasText;
    char[] buf;
    char ch;
    String str;
    
    if ( (idx < 0) || (idx >= text.size()) ) {
      throw new ArrayIndexOutOfBoundsException("Invalid index=" + idx);
    }
    
    buf = text.get(idx).toCharArray();
    
    if ( buf.length == 0 ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug("No text converted to char array, idx=" + idx);
      }
      return(null);
    }
    
    inWhitespace = false;
    hasText = false;
    str = "";

    for ( int i = 0; i < buf.length; i++ ) {
      ch = buf[i];
      if ( !Character.isWhitespace(ch) ) {  
        hasText = true;
        inWhitespace = false;        
        str += ch;
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
        logger.debug("No text found, idx=" + idx);
      }
      return(null);
    }
    
    // we need to determine whether or not to add a whitespace at the
    // end of the text (because we have removed the newline)
    // we dont add a whitespace if the last character is a '-' 
    // otherwise we add a space
    int ct = hasHyphenAtEnd(str);
    if ( ct != -1 ) {
      // hyphenated at end, strip any whitespace 
      str = String.copyValueOf(str.toCharArray(), 0, ct);
    } else if ( !Character.isWhitespace(str.charAt(str.length()-1)) ) {
      str += " ";
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug("Idx=" + idx + 
          "\n\tCleansed=|" + str + 
          "|\n\tOriginal=|" + text.get(idx) + "|");
    }

    return(str);
  }
  
  /**
   * Checks for hyphenation at the end of a string.
   * 
   * @param str   A text <code>String</code>.
   * @return      <code>true</code> if the <code>String</code> has a hyphen
   *              at the last non whitespace character, <code>false</code>
   *              otherwise.
   */
  private static int hasHyphenAtEnd(final String str) {
    char[] buf;
    int ct;
    char ch;

    buf = str.toCharArray();
    ct = buf.length;
    
    for ( int i = ct-1; i >= 0; i-- ) {
      ch = buf[i];
      if ( Character.isWhitespace(ch) ) {
        continue;
      } else {
        // we have text
        if ( ch == '-' ) {
          ct = i+1;
          return(ct);
        } else {
          return(-1);
        }
      }
    }    
    
    return(-1);
  }  
}
