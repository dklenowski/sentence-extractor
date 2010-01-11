package com.orbious.extractor;

// $Id: TextParser.java 14 2009-12-06 10:03:53Z app $

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.orbious.extractor.Sentence.EndOp;
import com.orbious.extractor.Sentence.StartOp;
import com.orbious.extractor.SentenceMapEntry.Likelihood;
import com.orbious.extractor.SentenceMapEntry.SentenceEntrySubType;
import com.orbious.extractor.SentenceMapEntry.SentenceEntryType;
import com.orbious.extractor.evaluator.UrlText;
import com.orbious.util.Helper;

/**
 * Parser a text document into sentences. This class is the central class
 * for the <code>sentence-extractor</code> package.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class TextParser {
  
  /**
   * A plain text file.
   */
  private String filename;

  /**
   * In memory representation of the text file with whitespace removed.
   */
  private char[] buffer;
  
  /**
   * List of allowable sentence ends (see {@link Config#SENTENCE_ENDS}).
   */
  private HashSet<Character> sentence_ends;
  
  /**
   * List of inner punctuation. (see {@link Config#INNER_PUNCTUATION}).
   */
  private HashSet<Character> inner_punctuation;
  
  /**
   * List of preserved punctuation (see {@link Config#PRESERVED_PUNCTUATION}).
   */
  private HashSet<Character> preserved_punctuation;
  
  /**
   * List of left punctuation marks (see {@link Config#LEFT_PUNCTUATION_MARKS}).
   */
  private HashSet<Character> left_punctuation_marks;
  
  /**
   * List of right punctuation marks (see {@link Config#RIGHT_PUNCTUATION_MARKS}).
   */
  private HashSet<Character> right_punctuation_marks;  
  
  /**
   * A buffer that contains entries for likely/unlikely sentence start's/end's.
   */
  private SentenceMapEntry[] sentence_map;
  
  /**
   * A buffer that contains a record of all the characters that have
   * been extracted and is used for index adjustments of the start/end indexes
   * (i.e. {@link TextParser#adjustIndexes(int, int)}).
   */
  private boolean[] extraction_map;
  
  /**
   * A buffer that contains where the line starts, which is used by some
   * by the {@link com.orbious.extractor.evaluator.NumberedHeading} <code>Evaluator</code>.
   */
  private static HashSet< Integer > line_starts;
  
  /**
   * A <code>Vector</code> of <code>TextParserOp</code> containing sentence
   * start/ends.
   */
  private Vector< TextParserOp > parser_map;
  
  /**
   * Contains a list of sentences extracted from <code>filename</code>.
   */
  private Vector< Vector<String> > sentences;
  
  /**
   * The most recent sentence start index found in <code>buffer</code>.
   */
  private int sent_start_idx;
  
  /**
   * The most recent unlikely sentence start index found in <code>buffer</code>.
   */
  private int sent_unlikely_start_idx;
  
  /**
   * The most recent sentence end index found in <code>buffer</code>.
   */
  private int sent_end_idx;
  
  /**
   * The most recent unlikely sentence end index found in <code>buffer</code>.
   */
  private int sent_unlikely_end_idx;
  
  /**
   * Logger object.
   */
  private Logger logger;
  
  /**
   * Intialize the <code>TextParser</code>.
   * 
   * @param filename    The absolute path to a plain-text document.
   */
  public TextParser(String filename) {
    this.filename = filename;
    logger = Logger.getLogger(Config.LOGGER_REALM.asStr());
    
    sentence_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());
    
    inner_punctuation = Helper.cvtStringToHashSet(Config.INNER_PUNCTUATION.asStr());
    preserved_punctuation = Helper.cvtStringToHashSet(Config.PRESERVED_PUNCTUATION.asStr());
    
    left_punctuation_marks = Helper.cvtStringToHashSet(Config.LEFT_PUNCTUATION_MARKS.asStr());
    right_punctuation_marks = Helper.cvtStringToHashSet(Config.RIGHT_PUNCTUATION_MARKS.asStr());
  }

  /**
   * Returns the in-memory representation of {@link TextParser#filename} as a 
   * <code>char</code> array with whitespace removed.
   * <p>
   * @return  The parsed <code>char</code> buffer.
   */
  protected char[] buffer() {
    return(buffer);
  }

  /**
   * Returns a <code>Vector</code> of sentences extracted from 
   * {@link TextParser#filename}. Each sentence put into a <code>Vector</code>, 
   * where each entry contains a word.
   * 
   * @return    A list of sentences extracted from <code>filename</code>
   *            with each sentence returned as a <code>Vector</code> of words.
   */
  public Vector< Vector<String> > sentences() {
    return(sentences);
  }
  
  /**
   * Returns the same as @{link TextParser#sentences} with the <code>Vector</code>
   * list's of words converted to <code>String</code>'s. 
   * 
   * Accessor for {@link TextParser#sentences} with the words in the sentences
   * converted to <code>String</code>'s.
   * 
   * @return    A list of sentences extracted from <code>filename</code>
   *            with each sentence returned as a <code>String</code>.
   */
  public Vector<String> sentencesAsStr() {
    Vector<String> sent = new Vector<String>();
    Vector<String> words;
    String str;
    
    for ( int i = 0; i < sentences.size(); i++ ) {
      words = sentences.get(i);
      str = "";
      for ( int j = 0; j < words.size(); j++ ) {
        if ( j+1 < words.size() ) {
          str += words.get(j) + " ";
        } else {
          str += words.get(j);
        }
      }

      sent.add(str);
    }
    
    return(sent);
  }
  
  /**
   * Parses {@link TextParser#filename} into memory. This method also calls 
   * {@link com.orbious.extractor.WhitespaceRemover#remove(Vector, int)} on each line
   * before adding to memory and updates {@link TextParser#line_starts}
   * with the start of each line (minus whitespace).
   * 
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void parse() throws FileNotFoundException, IOException { 
    BufferedReader br;
    Vector<String> raw;
    Vector<String> clean;
    String str;
    char[] buf;
    int len;
    int pos;

    line_starts = TextParserData.line_starts;
    line_starts.clear();
    
    br = new BufferedReader(new FileReader(filename));
    raw = new Vector<String>();
    while ( (str = br.readLine()) != null ) {
      raw.add(str);
    }
    br.close();
    
    logger.info("Found " + raw.size() + " lines in " + filename);
  
    clean = new Vector<String>();
    len = 0;
  
    for ( int i = 0; i < raw.size(); i++ ) {
      str = WhitespaceRemover.remove(raw, i);
      if ( str != null ) {
        clean.add(str);
        line_starts.add(len);
        len += str.length();
      }
    }
    
    pos = 0;
    buffer = new char[len];
    for ( int i = 0; i < clean.size(); i++ ) {
      buf = clean.get(i).toCharArray();
      System.arraycopy(buf, 0, buffer, pos, buf.length);
      pos += buf.length;
    }


    
    if ( logger.isInfoEnabled() ) {
      logger.info("Statistics for " + filename +
          " Raw LineCt=" + raw.size() + " LineStarts=" + line_starts.size() +
          " Cleansed CharCt=" + buffer.length);
    }
  }
 
  /**
   * Runs the sentence extraction algorithm to generate the sentences
   * for {@link TextParser#sentences}.
   */
  public void genSentences() {
    SentenceMapEntry entry;
    SentenceEntryType type;
    Likelihood likelihood;
    String debugStr;
    Vector<String> sentence;
    
    genSentenceMap();    

    if ( sentences != null ) {
      sentences.clear();
      parser_map.clear();
    } else {
      sentences = new Vector< Vector<String> >();
      parser_map = new Vector<TextParserOp>();
    }
    extraction_map = new boolean[buffer.length];
   
    sent_start_idx = -1;
    sent_unlikely_start_idx = -1;
    sent_end_idx = -1;
    sent_unlikely_end_idx = -1;
    debugStr = "";

    for ( int i = 0; i < sentence_map.length; i++ ) {      
      entry = sentence_map[i];
      if ( entry == null ) {
        continue;
      }
      
      type = entry.type();
      likelihood = entry.likelihood();

      if ( type == SentenceEntryType.START ) {
        if ( likelihood == Likelihood.UNLIKELY ) {
          if ( sent_unlikely_start_idx == -1 ) {
            sent_unlikely_start_idx = i;
          }
        } else {
          if ( sent_start_idx == -1 ) {
            sent_start_idx = i;
          }
        }
      } else if ( type == SentenceEntryType.END ) {
        if ( likelihood == Likelihood.UNLIKELY ) {
          sent_unlikely_end_idx = i;
        } else {
          sent_end_idx = i;
          checkIndexes();
        }
      }

      if ( (debugStr.length() != 0) && logger.isDebugEnabled() ) {
        logger.debug(debugStr + "\n" + 
            Helper.getDebugStringFromSentenceMap(buffer, sentence_map, i, 200, -1) + "\n" +
            Helper.getDebugStringFromCharBuf(buffer, i, 200, -1) + "\n");
        debugStr = "";
      }
    }

    checkIndexes();
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("Buffer:\n" + 
          Helper.getDebugStringFromCharBuf(buffer, 0, buffer.length, 100) +
          "SentenceMap:\n" + 
          Helper.getDebugStringFromSentenceMap(buffer, sentence_map, 0, 
              sentence_map.length, 100) +
          "ExtractionMap:\n" +
          Helper.getDebugStringFromBoolBuf(buffer, extraction_map, 0, 
              extraction_map.length, 100));
    }

    for ( int i = 0; i < parser_map.size(); i++ ) {
      sentence = extract(parser_map.get(i));
      if ( sentence.size() > Config.MIN_SENTENCE_LENGTH.asInt() ) {
        sentences.add(sentence);
      }
    }
  }
  
  /**
   * Runs a check of the current sentence start/end indexes we have to see
   * if a sentence can be generated.
   */
  private void checkIndexes() {
    int tStartIdx;
    int tEndIdx;

    if ( (sent_end_idx != -1 || sent_unlikely_end_idx != -1) &&
         (sent_start_idx != -1 || sent_unlikely_start_idx != -1) ) {
      
      if ( logger.isDebugEnabled() ) {
        logger.debug(" sent_start_idx=" + sent_start_idx + 
            " sent_unlikely_start_idx=" + sent_unlikely_start_idx + 
            " sent_end_idx=" + sent_end_idx + " sent_unlikely_end_idx=" + 
            sent_unlikely_end_idx);
      }
      
      tStartIdx = -1;
      if ( sent_start_idx != -1 ) {
        tStartIdx = sent_start_idx;
        sent_start_idx = -1;
        sent_unlikely_start_idx = -1;
      } else if ( sent_unlikely_start_idx != -1 ) {
        tStartIdx = sent_unlikely_start_idx;
        sent_unlikely_start_idx = -1;
      }
      
      tEndIdx = -1;
      if ( tStartIdx != -1 ) {
        if ( sent_end_idx != -1 ) {
          tEndIdx = sent_end_idx;
          sent_end_idx = -1;
          sent_unlikely_end_idx = -1;
        } else if ( sent_unlikely_end_idx != -1 ) {
          tEndIdx = sent_unlikely_end_idx;
          sent_unlikely_end_idx = -1;
        }
        
        if ( tEndIdx != -1 ) {
          recordSentence(tStartIdx, tEndIdx);
        }
      }
    }
  }
  
  
  /**
   * Add's a new sentence begin/end to the <code>extraction_map</code>
   * and the <code>parser_map</code>.
   * 
   * @param start   The start of the sentence.
   * @param end   The end of the sentence.
   */
  private void recordSentence(int start, int end) {
    parser_map.add(new TextParserOp(start, end));
    for ( int i = start; i <= end; i++ ) {
      extraction_map[i] = true;
    }
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("Added sentence startIdx=" + start + " endIdx=" + end);
    }
  }

  /**
   * Internal method to generate a sentence map that is used by
   * {@link TextParser#genSentences()} to find the begin and start 
   * indexes for sentences.
   */
  protected void genSentenceMap() {
    char ch;
    EndOp endOp;
    StartOp startOp;
    
    sentence_map = new SentenceMapEntry[buffer.length];
    
    for ( int i = 0; i < buffer.length; i++ ) {
      ch = buffer[i];
      
      if ( sentence_ends.contains(ch) ) {
        // check for ends
        endOp = Sentence.isEnd(buffer, i);
        if ( endOp != null ) {
          if ( !endOp.isEnd() ) {
            addToMap(i, Likelihood.UNLIKELY, SentenceEntryType.END, null);
          } else {
            addToMap(i, Likelihood.LIKELY, SentenceEntryType.END, null);
            
            if ( endOp.startIdx() >= 0 ) {
              addToMap(endOp.startIdx(), Likelihood.LIKELY, 
                  SentenceEntryType.START, SentenceEntrySubType.START_FROM_END);
            }
          }
        }

      } else {
        // run a quick check to reduce the workload
        if ( !Character.isUpperCase(ch) ) {
          continue;
        } else if ( (i > 0) && (!Character.isWhitespace(buffer[i-1]) &&
            !sentence_ends.contains(buffer[i-1])) ) {
          // a sentence start must either have a whitespace character
          // or a sentence end for the previous character
          continue;
        }

        startOp = Sentence.isStart(buffer, i);
        if ( startOp != null ) {
          if ( !startOp.isStart() ) {
            addToMap(i, Likelihood.UNLIKELY, SentenceEntryType.START, null);
          } else {
            addToMap(i, Likelihood.LIKELY, SentenceEntryType.START, null);
          
            //if ( startOp.stopIdx() >= 0 ) {
              //addToMap(startOp.stopIdx(), Likelihood.LIKELY, 
               //   SentenceEntryType.END, SentenceEntrySubType.END_FROM_START);
            //}
          }
        }
      }
    }  
  }

  /**
   * Adds a likely/unlikely sentence start/end to the 
   * {@link TextParser#sentence_map}. If the entry specified at <code>idx</code>
   * is not null, will only add if the new entry replaces a 
   * {@link SentenceMapEntry.Likelihood} of <code>LIKELY</code>.
   * 
   * @param idx   The index in {@link TextParser#sentence_map}.
   * @param likelihood    The likelihood of this <code>SentenceMapEntry</code>. 
   * @param type    The type of this <code>SentenceMapEntry</code>.
   * @param subtype   Optional, The subtype of this <code>SentenceMapEntry</code>.
   */
  private void addToMap(int idx, Likelihood likelihood, SentenceEntryType type, 
      SentenceEntrySubType subtype) {
    SentenceMapEntry old;
    SentenceMapEntry entry;
    boolean replace;
    
    old = sentence_map[idx];
    replace = false;
    if ( old == null ) {
      replace = true;
    } else if ( old.likelihood() == Likelihood.UNLIKELY ) {
      replace = true;
    }

    if ( replace ) {
      entry = new SentenceMapEntry(likelihood, type, subtype);
      sentence_map[idx] = entry;
    }
  }
  
  /**
   * Extract a <code>Vector</code> of words from {@link TextParser#buffer}.
   * 
   * @param op  A <code>TextParserOp</code> contain the start/end indexes
   *            for extraction.
   *            
   * @return    A <code>Vector</code> of words.
   */
  protected Vector<String> extract(TextParserOp op) {
    Vector<String> words;
    int startIdx;
    int endIdx;
    String wd;
    char ch;
    IndexAdjustment indexAdjustment;
    boolean hasLetter;
    boolean doAsNewWord;

    startIdx = op.start;
    endIdx = op.end;
    indexAdjustment = adjustIndexes(startIdx, endIdx);
    words = new Vector<String>();
    wd = "";
    hasLetter = false;
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("Beginning extract startIdx=" + startIdx + 
          " adjustedStartIdx=" + indexAdjustment.adjustedStartIdx +
          " endIdx=" + endIdx +
          " adjustedEndIdx=" + indexAdjustment.adjustedEndIdx);
    }

    for ( int i = indexAdjustment.adjustedStartIdx; i <= indexAdjustment.adjustedEndIdx; i++ ) {
      ch = buffer[i];

      if ( Character.isLetterOrDigit(ch) ) {
        wd += ch;
        hasLetter = true;
      } else if ( Character.isWhitespace(ch) ) {
        if ( wd.length() == 0 ) {
          continue;
        }
        
        words.add(wd);
        wd = "";
        hasLetter = false;
        
      } else {
        // punctuation
        doAsNewWord = false;
        if ( hasLetter || Helper.isPreviousLetter(buffer, i) ) {
          if ( (i < startIdx) || (i >= endIdx) ) {
            doAsNewWord = true;
          } else {
            if ( inner_punctuation.contains(ch) ) {
              // punctuation attached to the word.
              wd += ch;
            } else if ( (ch == '.') && new UrlText().evaluate(buffer, i) ) {
              // web address 
              wd += ch;
            } else if ( (ch == ',') && Helper.isPreviousNumber(buffer, i) &&
                Helper.isNextNumber(buffer, i) ) {
              // thousands separator
              wd += ch;
            } else if ( preserved_punctuation.contains(ch) ) {
              // we preserve this punctuation
              doAsNewWord = true;
            }
          }
        } else if ( preserved_punctuation.contains(ch) ) {
          doAsNewWord = true;
        }
          
        if ( doAsNewWord ) {
          if ( wd.length() != 0 ) {
            words.add(wd);
          }
          
          words.add(Character.toString(ch));
          wd = "";
          hasLetter = false;
        }
      }
    }
    
    if ( hasLetter ) {
      words.add(wd);
    }
    
    // we need to run a final check and join punctuation
    Vector<String> cleanwords = new Vector<String>();
    int p = 0;
    boolean skipMunge;
    String tmpwd;
    
    while ( p < words.size() ) {
      wd = words.get(p);
      p++;
      skipMunge = false;
      
      if ( (wd.length() != 1) ||
          !preserved_punctuation.contains(wd.charAt(0)) ||
          (p+1 >= words.size()) ) {
        skipMunge = true;
      }
      
      if ( skipMunge ) {
        cleanwords.add(wd);
      } else {
        tmpwd = wd;
        while ( p < words.size() ) {
          wd = words.get(p);
          if ( (wd.length() != 1) || !preserved_punctuation.contains(wd.charAt(0)) ) {
            break;
          } else {
            tmpwd += wd;
            p++;
          }
        }
        cleanwords.add(tmpwd);
      }
    }
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("Extracted = " + Helper.cvtVectorToString(cleanwords));
    }
    
    return(cleanwords);
  }
  
  /**
   * Adjusts the <code>startIdx</code> and <code>endIdx</code> to capture
   * any punctuation that is part of the sentence.
   * 
   * @param startIdx    Position in <code>buffer</code> where the start of a
   *                    sentence begins.
   * @param endIdx      Position in <code>buffer</code> where the end of a 
   *                    sentence begins.
   *                    
   * @return    An <code>IndexAdjustment</code> containing adjusted start/end
   *            indexes if adjustment was required, otherwise returns
   *            the <code>startIdx</code>, <code>endIdx</code>.
   */
  protected IndexAdjustment adjustIndexes(int startIdx, int endIdx) {
    IndexAdjustment adjustment;
    int nxtIdx;
    int adjustedStartIdx;
    int adjustedEndIdx;
    boolean adjustedLeft;
    
    adjustment = new IndexAdjustment();
    
    // check the start
    //
    adjustedLeft = false;
    
    if ( startIdx == 0 ) {
      adjustment.adjustedStartIdx = 0;
    } else {
      nxtIdx = startIdx-1;
      adjustedStartIdx = nxtIdx;
  
       while ( (adjustedStartIdx > 0) &&
           !extraction_map[adjustedStartIdx] &&
           (Character.isWhitespace(buffer[adjustedStartIdx]) ||
               left_punctuation_marks.contains(buffer[adjustedStartIdx]) ||
               buffer[adjustedStartIdx] == '\"') ) {
          adjustedStartIdx--;
      }
        
      if ( adjustedStartIdx == nxtIdx ) {
        adjustment.adjustedStartIdx = startIdx;
      } else {
        adjustedLeft = true;
        adjustedStartIdx++;
        adjustment.adjustedStartIdx = adjustedStartIdx;
        for ( int i = adjustedStartIdx; i < startIdx; i++ ) {
          extraction_map[i] = true;
        }
      }
    }

    int ct = 0;
    if ( adjustedLeft ) {
      ct++;
    }
    
    for ( int i = startIdx; i < endIdx; i++ ) {
      if ( left_punctuation_marks.contains(buffer[i]) ) {
        ct++;
      } else if ( right_punctuation_marks.contains(buffer[i]) ) {
        ct--;
      }
    }

    if ( ct != 0 ) {
      adjustedLeft = true;
    }
    // now check the end
    //
    if ( endIdx == buffer.length-1 ) {
      adjustment.adjustedEndIdx = endIdx;
    } else {
      nxtIdx = endIdx+1;
      adjustedEndIdx = nxtIdx;
      
      while ( (adjustedEndIdx < buffer.length) &&
          !extraction_map[adjustedEndIdx] &&
          (   sentence_ends.contains(buffer[adjustedEndIdx]) ||
              (adjustedLeft &&
              (Character.isWhitespace(buffer[adjustedEndIdx]) ||
                  right_punctuation_marks.contains(buffer[adjustedEndIdx]) ||
                  buffer[adjustedEndIdx] == '\"'))   ) ) {
        adjustedEndIdx++;
      }
      
      if ( adjustedEndIdx == nxtIdx ) {
        adjustment.adjustedEndIdx = endIdx;
      } else {
        adjustedEndIdx--;
        adjustment.adjustedEndIdx = adjustedEndIdx;
        for ( int i = endIdx+1; i <= adjustedEndIdx; i++ ) {
          extraction_map[i] = true;
        }
      }
    }
    
    return(adjustment);
  }
  
  
  /**
   * An inner class to share <code>TextParser</code> data.
   * 
   * @author dave
   * @since 1.0
   * @version 1.0
   */
  public static class TextParserData {
    
    /**
     * Copy of {@link TextParser#line_starts}
     */
    private static HashSet<Integer> line_starts;
    
    /**
     * Private constructor.
     */
    private TextParserData() { }
    
    /**
     * Returns the <code>line_starts</code>.
     * 
     * @return  The <code>line_starts</code>.
     */
    public static HashSet<Integer> lineStarts() {
      if ( line_starts == null ) {
        line_starts = new HashSet< Integer >();
      }
      return(line_starts);
    }
  }
  
  /**
   * An inner class to represent adjusted start/end indexes for a sentence.
   * 
   * @author dave
   * @since 1.0
   * @version 1.0
   */
  static class IndexAdjustment {
    
    /**
     * The adjusted start index.
     */
    private int adjustedStartIdx;
    
    /**
     * The adjusted end index.
     */
    private int adjustedEndIdx;
    
    /**
     * Creates an empty <code>IndexAdjustment</code>.
     */
    public IndexAdjustment() { }

    /** 
     * Updates the <code>adjustedStartIdx</code> for this 
     * <code>IndexAdjustment</code>.
     * 
     * @param adjustedStartIdx    The new <code>adjustedStartIdx</code>.
     */
    public void adjustedStartIdx(int adjustedStartIdx) { 
      this.adjustedStartIdx = adjustedStartIdx;
    }
    
    /**
     * Accessor for <code>adjustedStartIdx</code>.
     * 
     * @return    The <code>adjustedStartIdx</code>.
     */
    public int adjustedStartIdx() {
      return(adjustedStartIdx);
    }
  
    /** 
     * Updates the <code>adjustedEndIdx</code> for this 
     * <code>IndexAdjustment</code>.
     * 
     * @param adjustedEndIdx    The new <code>adjustedEndIdx</code>.
     */
    public void adjustedEndIdx(int adjustedEndIdx) { 
      this.adjustedEndIdx = adjustedEndIdx;
    }
    
    /**
     * Accessor for <code>adjustedEndIdx</code>.
     * 
     * @return    The <code>adjustedEndIdx</code>.
     */
    public int adjustedEndIdx() {
      return(adjustedEndIdx);
    }
  }
  
  
  /**
   * An inner class to represent sentence start/end indexes.
   * 
   * @author dave
   * @since 1.0
   * @version 1.0
   */
  static class TextParserOp {
    
    /**
     * The start index for a sentence.
     */
    private int start;
    
    /**
     * The end index for a sentence.
     */
    private int end;
    
    /**
     * Creates a new <code>TextParserOp</code> and sets the initial parameters.
     * 
     * @param start   The start index for a sentence.
     * @param end   The end index for a sentence.
     */
    public TextParserOp(int start, int end) {
      this.start = start;
      this.end = end;
    }
    
    /**
     * Accessor for <code>start</code>.
     * 
     * @return    The <code>start</code>.
     */
    public int start() {
      return(start);
    }
    
    /**
     * Accessor for <code>end</code>.
     * 
     * @return    The <code>end</code>.
     */
    public int end() {
      return(end);
    }
  }
}
