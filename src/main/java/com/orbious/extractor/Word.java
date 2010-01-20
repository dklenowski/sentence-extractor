package com.orbious.extractor;

// $Id: Word.java 12 2009-12-05 11:40:44Z app $

import java.util.HashSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.orbious.extractor.util.Helper;

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
  
  /**
   * An inner class to store data for word operations.
   * 
   * @author dave
   * @version 1.0
   * @since 1.0
   */
  public static class WordOp {
    
    /**
     * The word that was found during the word operation.
     */
    private String word;
    
    /**
     * The index of the first letter in the word extracted relative to the buffer it 
     * was extracted from.
     */
    private int idx;
    
    /**
     * Constructor, initializes the <code>WordOp</code>.
     * 
     * @param word    The word that was found.
     * @param idx     The index of the first letter in the word relative 
     *                to the buffer it was extracted from.
     */
    public WordOp(String word, int idx) {
      this.word = word;
      this.idx = idx;
    }
    
    /**
     * Access for <code>word</code>.
     * 
     * @return    The word.
     */
    public String word() {
      return(word);
    }
    
    /**
     * Accessor for <code>idx</code>.
     * 
     * @return    The index.
     */
    public int idx() {
      return(idx);
    }   
  }
  
  /**
   * An inner class to store data for word operations that return 
   * multiple words/indexes.
   * 
   * @author dave
   * @version 1.0
   * @since 1.0
   *
   */
  static class MultipleWordOp {
    
    /**
     * The words that were found during the word operation.
     */
    private Vector< String > words;
    
    /**
     * The indexes of the first letter in the words extracted relative to the
     * buffer it was extracted from.
     */
    private Vector< Integer > buf_idxs;

    /**
     * Constructor, initializes an empty <code>MultipleWordOp</code>.
     */
    public MultipleWordOp() {
      words = new Vector< String >();
      buf_idxs = new Vector< Integer >();
    }
    
    /**
     * Adds a found word to the internal list.
     * 
     * @param word    The word to add.
     * @param idx     The index of the word relative to the buffer it was extracted
     *                from.
     */
    public void add(String word, int idx) {
      words.add(word);
      buf_idxs.add(idx);
    }
 
    /**
     * Returns the number of words that were extracted.
     * 
     * @return    The number of words extracted.
     */
    public int length() {
      return(words.size());
    }
    
    /**
     * Returns the <code>n</code>'th word extracted.
     * 
     * @param n   The <code>n</code>'th index. 
     * 
     * @return    The <code>n</code>'th word extracted.
     */
    public String word(int n) {
      return(words.get(n));
    }
    
    /**
     * Returns the index of the <code>n</code>'th extracted word relative to the
     * buffer it was extracted from.
     * 
     * @param n   The <code>n</code>'th index.
     * @return    The <code>n</code>'th index of the extracted word relative
     *            to the buffer it was extractted from.
     */
    public int idx(int n) {
      return(buf_idxs.get(n));
    }
  }
}
