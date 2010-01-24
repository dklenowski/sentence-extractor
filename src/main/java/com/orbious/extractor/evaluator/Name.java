package com.orbious.extractor.evaluator;

// $Id$

import com.orbious.extractor.Config;
import com.orbious.extractor.Word;
import com.orbious.extractor.Word.WordOp;
import com.orbious.extractor.util.Helper;

import java.io.FileNotFoundException;
import java.util.HashSet;

/**
 * Determines whether a position/word in a text buffer is considered
 * a Name and therefore not a likely sentence start. This class should only 
 * be used for evaluating the start of a sentence.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Name extends Evaluator {

  /**
   * In memory list of names.
   */
  private static HashSet<String> names;
  
  /**
   * Constructor, set's the <code>name</code> of this <code>Evaluator</code>.
   */
  public Name() {
    super("Name");
  }
  
  public boolean authoritative() {
    return(false);
  }
  
  /**
   * Determines if the previous word from <code>idx</code>
   * in the buffer <code>buf</code> is a common name and therefore not a
   * likely sentence start.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in <code>buf</code> where evaluation begins.
   * 
   * @return    <code>true</code> if the word is a common name and not
   *            a likely sentence start, <code>false</code> otherwise.
   */
  public boolean evaluate(final char[] buf, int idx) throws FileNotFoundException {
    WordOp op = Word.getNextWord(buf, idx, false);
    String wd = op.word();
    
    if ( op == null ) {
      return(false);
    }

    if ( names == null ) {
      names = Helper.cvtFileToHashSet(Config.NAMES_FILENAME.asStr(), false);
    }
    
    if ( wd == "" ) {
      return(false);
    }
    
    char[] wdbuf = wd.toCharArray();
    StringBuilder str = new StringBuilder();
    
    str.append(Character.toUpperCase(wdbuf[0]));
    for ( int i = 1 ; i < wdbuf.length; i++ ) {
      str.append(Character.toLowerCase(wdbuf[i]));
    }

    return( names.contains(str.toString()) );
  } 
}
