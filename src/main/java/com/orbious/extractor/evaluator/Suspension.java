package com.orbious.extractor.evaluator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import com.orbious.extractor.AppConfig;
import com.orbious.extractor.Word;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.extractor.Word.WordOp;
import com.orbious.util.HashSets;
import com.orbious.util.Resources;
import com.orbious.util.config.Config;

/**
* The <code>Suspension</code> <code>Evaluator</code> determines if a word
* is a suspension and cannot be considered a sentence start/end. i.e.
* <ul>
* <li>A suspension can be capitalized and It can be capitalized and not be a sentence start.
* <li>It can be terminated with a fullstop and not be a sentence end.
* </ul>
*
* @author dave
* @version 1.0
* @since 1.0
*/


public class Suspension extends Evaluator {

  /**
   * In memory list of suspensions.
   */
  private static HashSet<String> suspensions;

  /**
   * Constructor, initializes this <code>Evaluator</code>.
   *
   * @param parserData  Data generating during <code>TextParser</code> parsing.
   * @param type    The type of <code>Evaluator</code>.
   */
  public Suspension(TextParserData parserData, EvaluatorType type) {
    super("Suspension", type);
  }

  public void invalidate() throws EvaluatorException {
    String filename = Config.getString(AppConfig.suspension_filename); 
    logger.info("using suspension filename " + filename);
    
    InputStream in = Resources.getResourceStream(new File(filename));
    
    if ( in == null ) 
      throw new EvaluatorException("failed to find suspension file " + filename );      
    
    try {
      suspensions = HashSets.cvtStreamToHash(in, true);
    } catch ( IOException ioe ) {
      throw new EvaluatorException("Failed to suspensions file " +filename, ioe);
    }
    
    if ( suspensions.size() == 0 ) 
      throw new EvaluatorException("failed to extract any suspensions from " + filename);
  }

  /**
   * Returns <code>true</code> if run as a start <code>Evaluator</code>,
   * <code>false</code> otherwise.
   */
  public boolean recordAsUnlikely() {
    if ( type == EvaluatorType.START ) {
      return(true);
    }
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
   * in the buffer <code>buf</code> is a suspension and therefore
   * not a likely sentence start/end.
   */
  public boolean evaluate(final char[] buf, int idx)  {
    WordOp op;

    if ( type == EvaluatorType.START ) {
      if ( !Character.isUpperCase(buf[idx]) ) {
        return(false);
      }
      if ( idx != 0 ) {
        idx--;
      }
      op = Word.getNextWord(buf, idx, true);
    } else {
      if ( buf[idx] != '.' ) {
        return(false);
      }

      if ( idx+1 < buf.length ) {
        idx++;
      }
      op = Word.getPreviousWord(buf, idx, true);
    }

    if ( op == null ) {
      return(false);
    }

    return( suspensions.contains(op.word().toLowerCase()) );
  }
}
