package com.orbious.extractor;

import org.apache.log4j.Logger;

/**
 * $Id: Word.java 12 2009-12-05 11:40:44Z app $
 * <p>
 * Provides various static word operation methods.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Word {

  /**
   * Logger object.
   */
  private static final Logger logger = Logger.getLogger(Config.LOGGER_REALM.asStr());

  /**
   * Private constructor.
   */
  private Word() { }
  
  /**
   * Returns the previous word in <code>buf</code>.
   * 
   * @param buf  Character buffer to extract words from.
   * @param idx  Index in the buffer to begin word extraction.
   * 
   * @return  <code>null</code> if no word was extracted, 
   *          otherwise the word extracted.
   */
  public static String getPreviousWord(final char[] buf, int idx) {
    String[] words = getPreviousWords(buf, idx, 1);
    if ( words.length == 0 ) {
      return(null);
    }
    return(words[0]);
  }
  
  /**
   * Returns the previous <code>num</code> number of words from the 
   * <code>char</code> buffer <code>buf</code>. Text with the buffer 
   * is considered a word if it is separated by whitespace. If either end 
   * of the <code>buf</code> is reached and no whitespace is encountered, 
   * the text is still treated as a word.
   * 
   * @param buf  Character buffer to extract words from.
   * @param idx  Index in the buffer to begin word extraction.
   * @param num  The number of words to extract.
   * 
   * @return  <code>null</code> if no word was extracted,  
   *          otherwise up to <code>num</code> number of words extracted.
   */
  public static String[] getPreviousWords(final char[] buf, int idx, int num) {
    String[] tmpwords;
    char ch;
    boolean hasLetter;
    String reverseWd;
    int i, j;
    
    if ( (idx < 0) || (idx > buf.length) ) {
      throw new ArrayIndexOutOfBoundsException("Invalid index=" + idx);
    }

    tmpwords = new String[num];
    hasLetter = false;
    reverseWd = "";
    
    i = idx;
    j = num-1;

    while ( (i >= 0) && (i < buf.length) ) {
      ch = buf[i];
      
      if ( Character.isLetterOrDigit(ch) ) {
        reverseWd += ch;
        hasLetter = true;
      } else if ( Character.isWhitespace(ch) ) {
        if ( hasLetter ) {
          tmpwords[j] = new StringBuffer(reverseWd).reverse().toString();
          reverseWd = "";
          hasLetter = false;
            
          j--;
          if ( j < 0 ) { 
            break;
          }
        }
      } else {
        // punctuation, check if the punctuation is part of the word
        if ( (i-1 >= 0) && Character.isLetterOrDigit(buf[i-1]) ) {
          reverseWd += ch;
        }
      }

      i--;
    }
    
    if ( hasLetter ) {
      tmpwords[j] = new StringBuffer(reverseWd).reverse().toString();
      j--;
    }
    
    if ( j < num ) {
      int wdCt = num-j-1;
      String[] tmpwords2 = new String[wdCt];
      System.arraycopy(tmpwords, j+1, tmpwords2, 0, wdCt);
      tmpwords = tmpwords2;
    }
    
    
    if ( logger.isDebugEnabled() ) {
      StringBuffer sb = new StringBuffer();
      for ( i = 0; i < tmpwords.length; i++ ) {
        sb.append("\t" + i + "=|" + tmpwords[i] + "|\n");
      }
      
      logger.debug("Words extraction:\n" + sb.toString());
    }
    
    return(tmpwords);
  }
  

}
