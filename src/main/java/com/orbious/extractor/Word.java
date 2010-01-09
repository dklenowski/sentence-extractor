package com.orbious.extractor;

// $Id: Word.java 12 2009-12-05 11:40:44Z app $

import java.util.HashSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.orbious.util.Helper;

/**
 * Provides various static word operation methods.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Word {

  /**
   * An enum representing the direction to extract words from i.e.
   * <code>LEFT</code> for previous, <code>RIGHT</code> for next.
   */
  private enum DIRN { LEFT, RIGHT };
  
  /**
   * In memory representation of inner punctuation.
   */
  private static HashSet<Character> inner_punctuation =
    Helper.cvtStringToHashSet(Config.INNER_PUNCTUATION.asStr());
  
  /**
   * Logger object.
   */
  private static final Logger logger = 
    Logger.getLogger(Config.LOGGER_REALM.asStr());
  
  /**
   * Private constructor.
   */
  private Word() { }
  
  /**
   * Returns the previous word in <code>buf</code>.
   * 
   * @param buf   Character buffer to extract words from.
   * @param idx   Index in the buffer to begin word extraction.
   * @param includeStop   Include any fullstops that are encountered.
   * 
   * @return    <code>null</code> if no word was extracted, 
   *            otherwise the word extracted.
   */
  public static WordOp getPreviousWord(final char[] buf, int idx,
      boolean includeStop) {
    MultipleWordOp mop = getWords(buf, idx, 1, DIRN.LEFT, includeStop);
    if ( mop.length() == 0 ) {
      return(null);
    }
    
    WordOp op = new WordOp(mop.word(0), mop.idx(0));
    return(op);
  }
  
  /**
   * Returns the next word in <code>buf</code>.
   * 
   * @param buf   Character buffer to extract words from.
   * @param idx   Index in the buffer to begin word extraction.
   * @param includeStop   Include any fullstops that are encountered.
   * 
   * @return    <code>null</code> if no word was extracted, 
   *            otherwise the word extracted.
   */
  public static WordOp getNextWord(final char[] buf, int idx, boolean includeStop) {
    MultipleWordOp mop = getWords(buf, idx, 1, DIRN.RIGHT, includeStop);
    if ( mop.length() == 0 ) {
      return(null);
    }
    
    WordOp op = new WordOp(mop.word(0), mop.idx(0));
    return(op);
  }
  
  /**
   * Returns the <code>dirn</code> <code>num</code> number of words from the 
   * <code>char</code> buffer <code>buf</code>. Text with the buffer 
   * is considered a word if it is separated by whitespace. If either end 
   * of the <code>buf</code> is reached and no whitespace is encountered, 
   * the text is still treated as a word.
   * 
   * @param buf   Character buffer to extract words from.
   * @param idx    Index in the buffer to begin word extraction.
   * @param num   The number of words to extract.
   * @param dirn   The direction to parse, either <code>LEFT</code> or 
   *                <code>RIGHT</code>.
   * @param includeStop   Include any fullstops that are encountered.
   * 
   * @return    <code>null</code> if no word was extracted,  
   *            otherwise up to <code>num</code> number of words extracted.
   */
  public static MultipleWordOp getWords(final char[] buf, int idx, int num, DIRN dirn,
      boolean includeStop) {
    MultipleWordOp op;
    char ch;
    boolean hasLetter;
    String wd;
    int i, j;
    int lastIdx;
   
    if ( (idx < 0) || (idx > buf.length) ) {
      throw new ArrayIndexOutOfBoundsException("Invalid index=" + idx);
    }

    op = new MultipleWordOp();
    hasLetter = false;
    wd = "";
    lastIdx = -1;
    
    i = idx;
    if ( dirn == DIRN.LEFT ) {
      j = num-1;
    } else {
      j = 0;
    }

    while ( (i >= 0) && (i < buf.length) ) {
      ch = buf[i];
      
      if ( Character.isLetterOrDigit(ch) ) {
        wd += ch;
        lastIdx = i;
        hasLetter = true;
      } else if ( Character.isWhitespace(ch) ) {
        if ( hasLetter ) {
          if ( dirn == DIRN.LEFT ) {
            op.add(new StringBuffer(wd).reverse().toString(), lastIdx);
            j--;
          } else {
            op.add(new StringBuffer(wd).toString(), lastIdx);
            j++;
          }
          wd = "";
          lastIdx = -1;
          hasLetter = false;
          
          if ( (j >= num) || (j < 0) || (j >= buf.length) ) {
            break;
          }
        }
      } else {
        // punctuation, check if the punctuation is part of the word
        // and not a sentence end
        if ((inner_punctuation.contains(ch) &&
            (i-1 >= 0) && Character.isLetterOrDigit(buf[i-1])) ) {
          if ( ch != '.' ) {
            wd += ch;
          } else if ( includeStop ) {
            wd += ch;
          }
        }
      }

      if ( dirn == DIRN.LEFT ) {
        i--;
      } else {
        i++;
      }
    }
    
    if ( hasLetter ) {
      if ( dirn == DIRN.LEFT ) {
        op.add(new StringBuffer(wd).reverse().toString(), lastIdx);
        j--;
      } else {
        op.add(new StringBuffer(wd).toString(), lastIdx);
        j++;
      }
    }
    
    if ( logger.isDebugEnabled() ) {
      StringBuffer sb = new StringBuffer();
      for ( i = 0; i < op.length(); i++ ) {
        sb.append("\t" + i + "=|" + op.word(i) + "| (" + op.idx(i) + ")\n");
      }
      
      logger.debug("Words extraction:\n" + sb.toString());
    }
    
    return(op);
  }
  
  
  public static class WordOp {
    private String word;
    private int idx;
    
    public WordOp(String word, int idx) {
      this.word = word;
      this.idx = idx;
    }
    
    public String word() {
      return(word);
    }
    
    public int idx() {
      return(idx);
    }   
  }
  
  static class MultipleWordOp {
    private Vector< String > words;
    private Vector< Integer > idxs;

    public MultipleWordOp() {
      words = new Vector< String >();
      idxs = new Vector< Integer >();
    }
    
    public void add(String word, int idx) {
      words.add(word);
      idxs.add(idx);
    }
    
    public String word(int idx) {
      return(words.get(idx));
    }
    
    public int idx(int idx) {
      return(idxs.get(idx));
    }
    
    public int length() {
      return(words.size());
    }
  }
}
