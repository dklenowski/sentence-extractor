package com.orbious.extractor.evaluator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * $Id$
 * <p>
 * Implements the <code>SentenceStart</code> interface to determine
 * if a possible sentence start is a common first name/surname.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class UrlText extends Evaluator {

	
	private Pattern url_pattern;
	
	/**
	 * Initializes the <code>UrlText</code>.
	 */
	public UrlText() {
		super("UrlText");
		url_pattern = Pattern.compile("[a-zA-Z0-9\\-]+\\.(com|edu|gov|mil|net|org|biz|info|name|museum|us|ca|uk)");
	}
	
	public boolean evaluate(char[] buf, int idx) {
		String str;
		char ch;
		int i;
		
		str = "";
		
		// add from idx-1 to a whitespace
		if ( (idx-1) >= 0 ) {
			i = idx-1;
			while ( i >= 0 ) {
				ch = buf[i];
				if ( Character.isWhitespace(ch) ) {
					break;
				}
				
				str += ch;
				i--;
			}
		}
		
		str = new StringBuffer(str).reverse().toString();
		
		// add from idx to a whitespace
		i = idx;
		while ( i < buf.length ) {
			ch = buf[i];
			if ( Character.isWhitespace(ch) ) {
				break;
			}
			
			str += ch;
			i++;
		}

		Matcher matcher = url_pattern.matcher(str);
		boolean result = matcher.find();
		
		if ( logger.isDebugEnabled() ) {
			logger.debug("ExtractedString=" + str + " Match=" + result);
		}

		return(result);
	}
	
	public boolean evaluate(String wd) {
		Matcher matcher = url_pattern.matcher(wd);
		boolean result = matcher.find();
		
		if ( logger.isDebugEnabled() ) {
			logger.debug("Word=" + wd + " Match=" + result);
		}

		return(result);
	}
}
