package com.orbious.extractor;

import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * $Id$
 * <p>
 * Consistently removes whitespace characters.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class WhitespaceRemover {

  private static Logger logger;
  
  static {
    logger = Logger.getLogger(Config.LOGGER_REALM.asStr());
  }
  
  private WhitespaceRemover() { }
  
  
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
