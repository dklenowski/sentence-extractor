package com.orbious.util;

import java.util.HashSet;
import java.util.Vector;

/**
 * $Id: Helper.java 11 2009-12-04 14:07:11Z app $
 * <p>
 * Provides static helper methods for <code>SentenceExtractor</code>.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Helper {

  /**
   * Private Constructor
   */
  private Helper() { }
  
  /**
   * Returns a <code>String</code> containing text extracted from 
   * <code>buf</code> of size <code>size</code> relative to the position 
   * <code>pos</code>. <code>start</code> and <code>end</code> are 
   * automatically update to the <code>buf</code> 
   * extremium's if required.
   * 
   * @param buf   The buffer to extract text from.
   * @param pos   The <code>buf</code> array index specifying the 
   *              middle point for extraction.
   * @param size  The length of the <code>String</code> to return.
   * 
   * @return  A <code>String</code> containing text extracted from 
   *          <code>buf</code>.         
   */
  public static String getDebugStringFromCharBuf(char[] buf, int pos, int size) {
    StringBuffer sb = new StringBuffer();
    StringBuffer id = new StringBuffer();
    int start;
    int end;

    start = pos-(size/2);
    if ( start < 0 ) {
      size += Math.abs(start);
      start = 0;
    }
    
    end = pos+(size/2)+1;
    if ( end >= buf.length ) {
      end = buf.length;
    }
    
    for ( int i = start; i < end; i++ ) {
      sb.append(buf[i]);
      if ( i == pos ) {
        id.append("|");
      } else {
        id.append("-");
      }
    }

    return( sb.toString() + "\n" + id.toString() );
  }
  
  /**
   * Convert the contents of a <code>String</code> to a 
   * <code>HashSet</code> separated on <code>Character</code> boundaries.
   * 
   * @param str  The <code>String</code> to interrogate.
   * @return     A <code>HashSet</code> containing the contents of the 
   *             <code>String</code> <code>str</code>.
   */
  public static HashSet<Character> cvtStringToHashSet(String str) {
	  char[] buf = str.toCharArray();
	  HashSet<Character> hs = new HashSet<Character>();
	  
	  for ( int i = 0; i < buf.length; i++ ) {
		hs.add(buf[i]);
	  }
	  
	  return(hs);
  }
  
  
  public static String cvtVectorToString(Vector<String> words) {
    String str = "|";

    for ( int i = 0; i < words.size(); i++ ) {
      if ( i+1 < words.size() ) {
        str += words.get(i) + " ";
      } else {
        str += words.get(i);
      }
    }

    str += "|";
    return(str);    
  }
}
