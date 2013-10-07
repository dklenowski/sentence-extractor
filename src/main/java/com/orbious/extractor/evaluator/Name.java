package com.orbious.extractor.evaluator;

import com.orbious.extractor.AppConfig;
import com.orbious.extractor.Word;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.Word.WordOp;
import com.orbious.util.HashSets;
import com.orbious.util.Resources;
import com.orbious.util.config.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
   * Constructor, initializes this <code>Evaluator</code>.
   *
   * @param parserData  Data generating during <code>TextParser</code> parsing.
   * @param type    The type of <code>Evaluator</code>.
   */
  public Name(TextParserData parserData, EvaluatorType type) {
    super("Name", type);
  }

  public void invalidate() throws EvaluatorException {
    String filename = Config.getString(AppConfig.names_filename); 
    logger.info("using names filename " + filename);
    
    InputStream in = Resources.getResourceStream(new File(filename));
    if ( in == null ) 
      throw new EvaluatorException("failed to find names file " + filename );      

    try {
      names = HashSets.cvtStreamToHash(in, false);
    } catch ( IOException ioe ) {
      throw new EvaluatorException("Failed to load names file " +
         filename, ioe);
    }
    
    if ( names.size() == 0 ) 
      throw new EvaluatorException("failed to extract any names from " + filename);
  }

  /**
   * Return's <code>true</code> as this <code>Evaluator</code> is only
   * used a a start <code>Evaluator</code>.
   */
  public boolean recordAsUnlikely() {
    return(true);
  }

  /**
   * Return's <code>false</code>.
   */
  public boolean recordAsPause() {
    return(false);
  }

  /**
   * Determines if the previous word from <code>idx</code>
   * in the buffer <code>buf</code> is a common name and therefore not a
   * likely sentence start.
   */
  public boolean evaluate(final char[] buf, int idx) {
    if ( idx != 0 ) {
      idx--;
    }
    WordOp op = Word.getNextWord(buf, idx, false);
    if ( op == null ) {
      return(false);
    }

    String wd = op.word();

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
