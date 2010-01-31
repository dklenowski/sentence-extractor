package com.orbious.extractor.evaluator;

// $Id$

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.orbious.extractor.Config;
import com.orbious.extractor.TextParser.TextParserData;

/**
 * Determines whether a position/word in a text buffer is considered
 * a url and therefore "full stops" are not considered a likely 
 * sentence end.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class UrlText extends Evaluator {

  /**
   * The regular expression pattern that is used to match a URL.
   */
  private static Pattern url_pattern;
  
  static {
    url_pattern = Pattern.compile(Config.URL_REGEX.asStr());    
  }
  
  /**
   * Constructor, set's the <code>name</code> of this <code>Evaluator</code>.
   */
  public UrlText(TextParserData parserData, EvaluatorType type) {
    super("UrlText", type);
  }
  
  public boolean recordAsUnlikely() {
    return(false);
  }
  
  public boolean recordAsPause() {
    return(false);
  }
  
  /**
   * Determines if the previous word from <code>idx</code>
   * in the buffer <code>buf</code> is a url and therefore not a
   * likely sentence end.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in <code>buf</code> where evaluation begins.
   * 
   * @return    <code>true</code> if the word is a url and not
   *            a likely sentence end, <code>false</code> otherwise.
   */  
  public boolean evaluate(final char[] buf, int idx) {
    char ch;
    int i;
    StringBuilder sb;
 
    sb = new StringBuilder();
    
    // if the next character is punctuation, then return false
    // this handles the special case of www.google.com.'.' (i.e. the second 
    // stop)
    if ( (idx-1 >= 0) && !Character.isWhitespace(buf[idx-1]) &&
    		!Character.isLetterOrDigit(buf[idx-1]) ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug("Extracted (ch)=" + buf[idx-1] + " idx=" + idx + " Result=FALSE");
      }
    	return(false);
    }
    
    // add from idx-1 to a whitespace
    if ( (idx-1) >= 0 ) {
      i = idx-1;
      while ( i >= 0 ) {
        ch = buf[i];
        if ( Character.isWhitespace(ch) ) {
          break;
        }
        
        sb.append(ch);
        i--;
      }
    }
    
    sb = sb.reverse();

    // add from idx to a whitespace
    i = idx;
    while ( i < buf.length ) {
      ch = buf[i];
      if ( Character.isWhitespace(ch) ) {
        break;
      }
 
      sb.append(ch);
      i++;
    }

    Matcher matcher = url_pattern.matcher(sb.toString());
    boolean result = matcher.find();
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("String=" + sb.toString() + " Match=" + 
          String.valueOf(result).toUpperCase());
    }

    return(result);
  }
}
