package com.orbious.extractor;

import java.util.Vector;
import org.apache.log4j.Logger;
import com.orbious.util.Loggers;

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
  private static Logger logger = Loggers.logger();

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
    StringBuilder sb;
    int letterStartPos;

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
    sb = new StringBuilder();

    for ( int i = 0; i < buf.length; i++ ) {
      ch = buf[i];
      if ( !Character.isWhitespace(ch) ) {
        hasText = true;
        inWhitespace = false;
        sb.append(ch);
      } else {
        if ( !inWhitespace ) {
          if ( (i != 0) && (ch != '\n') ) {
            // we dont add whitespace to the start
            sb.append(ch);
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
    int ct = hasHyphenAtEnd(sb.toString());
    if ( ct != -1 ) {
      // hyphenated at end, strip any whitespace
      sb = new StringBuilder(sb.substring(0, ct));
    } else if ( !Character.isWhitespace(sb.charAt(sb.length()-1)) ) {
      sb.append(" ");
    }

    // once we have cleaned the string we need to find position of the first letter
    //
    letterStartPos = -1;
    buf = sb.toString().toCharArray();
    for ( int i = 0; i < buf.length; i++ ) {
      if ( Character.isLetter(buf[i]) ) {
        letterStartPos = i;
        break;
      }
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug("Idx=" + idx + " letterStartPos=" + letterStartPos +
          "\n\tCleansed=|" + sb.toString() +
          "|\n\tOriginal=|" + text.get(idx) + "|");
    }

    return(sb.toString());
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
