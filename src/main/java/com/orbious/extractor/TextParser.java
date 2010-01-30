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
import com.orbious.extractor.evaluator.Evaluator;
import com.orbious.extractor.evaluator.HeadingEvaluator;
import com.orbious.extractor.evaluator.UrlText;
import com.orbious.extractor.evaluator.Evaluator.EvaluatorType;
import com.orbious.extractor.util.Helper;

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
  private HashSet< Integer > line_starts;
  
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
   * 
   */
  private static TextParserData parser_data;
  
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
  
  public static TextParserData parserData() {
    return(parser_data);
  }

  // SHOULD REALLY ONLY BE USED IN TESTING
  public char[] _buffer() {
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
    StringBuilder sb;
    
    sb = new StringBuilder();
    
    for ( int i = 0; i < sentences.size(); i++ ) {
      words = sentences.get(i);
      sb.setLength(0);
      for ( int j = 0; j < words.size(); j++ ) {
        sb.append(words.get(j));
        if ( j+1 < words.size() ) {
          sb.append(" ");
        }
      }

      sent.add(sb.toString());
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
    int lineCt;
    
    br = new BufferedReader(new FileReader(filename));
    raw = new Vector<String>();
    while ( (str = br.readLine()) != null ) {
      raw.add(str);
    }
    br.close();
    
    logger.info("Found " + raw.size() + " lines in " + filename);

    line_starts = new HashSet<Integer>();
    clean = new Vector<String>();
    len = 0;
    lineCt = 0;
  
    for ( int i = 0; i < raw.size(); i++ ) {
      str = WhitespaceRemover.remove(raw, i);
      if ( str != null ) {
        clean.add(str);
        line_starts.add(len);
        len += str.length();
        lineCt++;
      }
    }
    
    pos = 0;
    buffer = new char[len];
    for ( int i = 0; i < clean.size(); i++ ) {
      buf = clean.get(i).toCharArray();
      System.arraycopy(buf, 0, buffer, pos, buf.length);
      pos += buf.length;
    }
    
    sentence_map = new SentenceMapEntry[buffer.length];
    
    parser_data = new TextParserData();
    parser_data.setTextParserData(line_starts, sentence_map, (len/lineCt));
    
    if ( logger.isInfoEnabled() ) {
      logger.info("Statistics for " + filename +
          " Raw: LineCt=" + raw.size() + 
          " Cleansed: LineStarts=" + line_starts.size() +
          " CharCt=" + buffer.length +
          " AvgLineCharCt=" + parser_data.avg_line_char_ct);
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
    ExtractionOp op;
    
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
          if ( sent_start_idx != -1 ) {
            checkIndexes();
          }
          sent_start_idx = i;
        }
      } else if ( type == SentenceEntryType.END ) {
        if ( likelihood == Likelihood.UNLIKELY ) {
          if ( sent_unlikely_end_idx == -1 ) {
            sent_unlikely_end_idx = i;
          }
        } else {
          if ( sent_end_idx != -1 ) {
            checkIndexes();
          }
          sent_end_idx = i;
          checkIndexes();
        }
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
      op = extract(parser_map.get(i));
      if ( op.wordCt() >= Config.MIN_SENTENCE_LENGTH.asInt() ) {
        sentences.add(op.words());
      }
    }
    
    if ( logger.isInfoEnabled() ) {
      logger.info("Found " + sentences.size() + " sentences in " + filename);
    }
  }
  
  /**
   * Runs a check of the current sentence start/end indexes we have to see
   * if a sentence can be generated.
   */
  private void checkIndexes() {
    int tStartIdx;
    int tEndIdx;
    boolean fndStart;
    boolean fndEnd;
    
    if ( (sent_end_idx != -1 || sent_unlikely_end_idx != -1) &&
         (sent_start_idx != -1 || sent_unlikely_start_idx != -1) ) {
      
      if ( logger.isDebugEnabled() ) {
        logger.debug(" sent_start_idx=" + sent_start_idx + 
            " sent_unlikely_start_idx=" + sent_unlikely_start_idx + 
            " sent_end_idx=" + sent_end_idx + " sent_unlikely_end_idx=" + 
            sent_unlikely_end_idx);
      }
    
      fndStart = false;
      fndEnd = false;
      
      tStartIdx = -1;
      if ( sent_start_idx != -1 ) {
        tStartIdx = sent_start_idx;
        fndStart = true;
        sent_start_idx = -1;
      } else if ( sent_unlikely_start_idx != -1 ) {
        tStartIdx = sent_unlikely_start_idx;
      }
      
      tEndIdx = -1;
      if ( tStartIdx != -1 ) {
        if ( sent_end_idx != -1 ) {
          tEndIdx = sent_end_idx;
          fndEnd = true;
          sent_end_idx = -1;
        } else if ( sent_unlikely_end_idx != -1 ) {
          tEndIdx = sent_unlikely_end_idx;
        }
        
        if ( (tEndIdx != -1) && (tStartIdx < tEndIdx) && (fndStart || fndEnd) ) {
          recordSentence(tStartIdx, tEndIdx);
          sent_unlikely_start_idx = -1;
          sent_unlikely_end_idx = -1;
        } else {
          // restore
          if ( fndStart ) {
            sent_start_idx = tStartIdx;
          } else {
            sent_unlikely_start_idx = tStartIdx;
          }
          
          if ( fndEnd ) {
            sent_end_idx = tEndIdx;
          } else {
            sent_unlikely_end_idx = tEndIdx;
          }
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
    boolean inHeading;
    Evaluator evaluator;
    
    inHeading = false;
    
    for ( int i = 0; i < buffer.length; i++ ) {
      ch = buffer[i];
      
      if ( sentence_ends.contains(ch) ) {
        endOp = Sentence.isEnd(buffer, i);
        if ( endOp != null ) {
          if ( !endOp.isEnd() ) {
            evaluator = endOp.failedEvaluator();

            if ( evaluator != null ) {
              if ( evaluator.recordAsUnlikely() ) {
                addToMap(i, Likelihood.UNLIKELY, SentenceEntryType.END, null);
              } else if ( evaluator.recordAsPause() ) {
                addToMap(i, Likelihood.LIKELY, SentenceEntryType.PAUSE, null);
              }
            }
          } else {
            addToMap(i, Likelihood.LIKELY, SentenceEntryType.END, null);
            
            if ( endOp.startIdx() >= 0 ) {
              addToMap(endOp.startIdx(), Likelihood.LIKELY, 
                  SentenceEntryType.START, SentenceEntrySubType.START_FROM_END);
            }
          }
          
          if ( inHeading ) {
            inHeading = false;
          }
        }

      } else {
        // run a quick check to reduce the workload
        if ( !Character.isUpperCase(ch) ) {
          continue;
        } else if ( (i > 0) && !Character.isWhitespace(buffer[i-1]) &&
            !sentence_ends.contains(buffer[i-1]) ) {
          // the setence_ends conditional is included for the case
          // ...: "As ...
          continue;
        }

        startOp = Sentence.isStart(buffer, i, inHeading);
        if ( startOp != null ) {
          if ( !startOp.isStart() ) {
            evaluator = startOp.failedEvaluator();
            
            if ( evaluator != null ) { 
              if ( evaluator instanceof HeadingEvaluator ) {
                addToMap(i, Likelihood.LIKELY, SentenceEntryType.HEADING, null);
                inHeading = true;
              } else if ( evaluator.recordAsUnlikely() ) {
                addToMap(i, Likelihood.UNLIKELY, SentenceEntryType.START, null);
              }
            }
          } else {
            addToMap(i, Likelihood.LIKELY, SentenceEntryType.START, null);
          
            ////////////////////
            // THIS seems to generate allot of false positives? 
            //
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

    if ( replace || 
        (type == SentenceEntryType.PAUSE) || 
        (type == SentenceEntryType.HEADING) ) {
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
   * @return    A <code>ExtractionOp</code> containing the <code>Vector</code>
   *            of words and the <code>wordCt</code>.
   */
  protected ExtractionOp extract(TextParserOp op) {
    Vector<String> words;
    int startIdx;
    int endIdx;
    int wordCt;
    String wd;
    char ch;
    IndexAdjustment indexAdjustment;
    boolean hasAlpha;
    boolean hasLetter;
    boolean doAsNewWord;

    startIdx = op.start;
    endIdx = op.end;
    indexAdjustment = adjustIndexes(startIdx, endIdx);
    words = new Vector<String>();
    wd = "";
    hasAlpha = false;
    hasLetter = false;
    wordCt = 0;
    
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
        hasAlpha = true;
        if ( Character.isLetter(ch) ) {
          hasLetter = true;
        }
      } else if ( Character.isWhitespace(ch) ) {
        if ( wd.length() == 0 ) {
          continue;
        }
        
        words.add(wd);
        wd = "";
        if ( hasLetter ) {
          wordCt++;
        }
        hasAlpha = false;
        hasLetter = false;
        
      } else {
        // punctuation
        doAsNewWord = false;
        
        if ( hasAlpha || Helper.isPreviousLetter(buffer, i) ) {
          if ( (i < startIdx) || (i >= endIdx) ) {
            doAsNewWord = true;
          } else {
            if ( inner_punctuation.contains(ch) ) {
              // punctuation attached to the word.
              wd += ch;
            } else if ( (ch == '.') && new UrlText(EvaluatorType.END).evaluate(buffer, i) ) {
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
            wd = "";
            if ( hasLetter ) {
              wordCt++;
            }
          }
          
          words.add(Character.toString(ch));
          hasAlpha = false;
          hasLetter = false;
        }
      }
    }
    
    if ( hasAlpha ) {
      words.add(wd);
      if ( hasLetter ) {
        wordCt++;
      }
    }
    
    // we need to run a final check and join punctuation
    Vector<String> cleanwords = new Vector<String>();
    int p = 0;
    boolean skipMunge;
    StringBuilder tmpwd;
    
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
        tmpwd = new StringBuilder(wd);
        while ( p < words.size() ) {
          wd = words.get(p);
          if ( (wd.length() != 1) || sentence_ends.contains(wd.charAt(0)) || 
              !preserved_punctuation.contains(wd.charAt(0)) ) {
            break;
          } else {
            tmpwd.append(wd);
            p++;
          }
        }
        cleanwords.add(tmpwd.toString());
      }
    }
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("Extracted = " + Helper.cvtVectorToString(cleanwords));
    }
    
    return( new ExtractionOp(cleanwords, wordCt) );
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
  
  // USED IN TESTING
  public static void _setTextParserData(TextParserData data) {
    parser_data = data;
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
    protected HashSet<Integer> line_starts;

    protected SentenceMapEntry[] sentence_map;
    
    protected int avg_line_char_ct = -1;
    
    /**
     * Private constructor.
     */
    public TextParserData() { }

    public void setTextParserData(HashSet<Integer> lineStarts,
        SentenceMapEntry[] sentenceMap, int avgLineCharCt) {
      line_starts = lineStarts;
      sentence_map = sentenceMap;
      avg_line_char_ct = avgLineCharCt;
    }
    
    public boolean containsLineStart(int idx) {
      if ( line_starts == null ) {
        throw new NullPointerException("TextParserData (line_starts) not initialized correctly");
      }
      
      return( line_starts.contains(idx) );
    }
    
    public boolean containsHeading(int idx) {
      if ( sentence_map == null ) {
        throw new NullPointerException("TextParserData (sentence_map) not initialized correctly");
      } else if ( line_starts == null ) {
        throw new NullPointerException("TextParserData (line_starts) not initialized correctly");
      }
      
      int i = idx-1;
      if ( i < 0 ) {
        return(false);
      }
      
      SentenceMapEntry entry;
      SentenceEntryType type;

      while ( i > 0 ) {
        entry = sentence_map[i];

        if ( line_starts.contains(i) ) {
          if ( (entry != null) && (entry.type() == SentenceEntryType.HEADING) ) {
            return(true);
          }
          return(false);
        } 

        if ( entry == null ) {
          i--;
          continue;
        } 
        
        type = entry.type();
        if ( type == SentenceEntryType.HEADING ) {
          return(true);
        }
        
        return(false);
      }
      
      return(false);
    }
    
    public int findPreviousPause(int idx) {
      if ( sentence_map == null ) {
        throw new NullPointerException("TextParserData (sentence_map) not initialized correctly");
      }

      int i = idx-1;
      if ( i < 0 ) {
        return(-1);
      }
      
      SentenceMapEntry entry;
      int endIdx = -1;
      
      while ( i > 0 ) {
        entry = sentence_map[i];
        if ( entry == null ) {
          i--;
          continue;
        }
        
        if ( entry.likelihood() == Likelihood.LIKELY &&
            ((entry.type() == SentenceEntryType.END) ||
             (entry.type() == SentenceEntryType.PAUSE)) ) {
          endIdx = i;
          break;
        }
        i--;
      }
      
      return(endIdx);
    }
    
    public int findPreviousLikelyEnd(int idx) {
      if ( sentence_map == null ) {
        throw new NullPointerException("TextParserData (sentence_map) not initialized correctly");
      }

      int i = idx-1;
      if ( i < 0 ) {
        return(-1);
      }
      
      SentenceMapEntry entry;
      int endIdx = -1;
      
      while ( i > 0 ) {
        entry = sentence_map[i];
        if ( entry == null ) {
          i--;
          continue;
        }
        
        if ( (entry.likelihood() == Likelihood.LIKELY) &&
             (entry.type() == SentenceEntryType.END) ) {
          endIdx = i;
          break;
        }
        i--;
      }
      
      return(endIdx);
    }
    
    public int findPreviousUnlikelyEnd(int idx) {
      if ( sentence_map == null ) {
        throw new NullPointerException("TextParserData (sentence_map) not initialized correctly");
      }

      int i = idx-1;
      if ( i < 0 ) {
        return(-1);
      }

      SentenceMapEntry entry;
      int endIdx = -1;
      
      while ( i > 0 ) {
        entry = sentence_map[i];
        if ( entry == null ) {
          i--;
          continue;
        }

        if ( (entry.type() == SentenceEntryType.END) &&
             (entry.likelihood() == Likelihood.UNLIKELY) ) {
          endIdx = i;
          break;
        }
        i--;
      }

      return(endIdx);
    }

    public int avgLineCharCt() {
      return(avg_line_char_ct);
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
  
  /**
   * An inner class used to store data related to the extraction of words
   * for a sentence.
   * 
   * @author dave
   * @version 1.0
   * @since 1.0
   */
  static class ExtractionOp {
    
    /**
     * The words constituting a sentence.
     */
    private Vector<String> words;
    
    /** 
     * A count of alphanumeric words within <code>words</code>. This is required
     * because punctuation occupies its own position within the 
     * <code>words</code> vector.
     */
    private int wordCt;
    
    /**
     * Constructor, initialize the <code>ExtractionOp<code> object.
     * 
     * @param words   The words constituting a sentence.
     * @param wordCt  A count of alphanumeric words within <code>words</code>.
     */
    public ExtractionOp(Vector<String> words, int wordCt) {
      this.words = words;
      this.wordCt = wordCt;
    }
    
    /**
     * Accessor for <code>words</code>.
     * 
     * @return    The <code>words</code>.
     */
    public Vector<String> words() {
      return(words);
    }
    
    /**
     * Accessor for <code>wordCt</code>.
     * 
     * @return    The <code>wordCt</code>.
     */
    public int wordCt() {
      return(wordCt);
    }
  }
}
