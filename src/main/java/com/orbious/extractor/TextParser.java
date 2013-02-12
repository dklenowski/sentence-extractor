package com.orbious.extractor;

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
import com.orbious.extractor.evaluator.EvaluatorException;
import com.orbious.extractor.evaluator.Heading;
import com.orbious.extractor.util.Helper;
import com.orbious.util.HashSets;
import com.orbious.util.Loggers;
import com.orbious.util.config.Config;

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
  private final String filename;

  /**
   * List of allowable sentence ends (see {@link Config#SENTENCE_ENDS}).
   */
  private HashSet<Character> sentence_ends;

  /**
   * Minimum sentence length (see {@link AppConfig#min_sentence_length}).
   */
  private int min_sentence_len;

  /**
   * Contains a list of sentences extracted from <code>filename</code>.
   */
  private Vector<SplitterOp> sentences;

  /**
   * The data extracted during {@link TextParser#parse()} and
   * {@link TextParser#genSentenceMap()}. Protected because it is used by
   * {@link SentenceSplitter}.
   */
  protected TextParserData parser_data;

  /**
   * Used to separate sentences.
   */
  private SentenceSplitter splitter;

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
  private Logger logger = Loggers.logger();

  /**
   * Intialize the <code>TextParser</code>.
   *
   * @param filename    The absolute path to a plain-text document.
   */
  public TextParser(String filename) {
    this.filename = filename;

    parser_data = new TextParserData();
    splitter = new SentenceSplitter(parser_data);
  }

  public void invalidate() {
    sentence_ends = HashSets.cvtStringToCharHashSet(
        Config.getString(AppConfig.sentence_ends));

    min_sentence_len = Config.getInt(AppConfig.min_sentence_length);
    splitter.invalidate();
  }

  /**
   * Returns a <code>Vector</code> of sentences extracted from
   * {@link TextParser#filename}. Each sentence put into a <code>Vector</code>,
   * where each entry contains a word.
   *
   * @param ignorePunct   Ignore words that contain only punctuation.
   *
   * @return    A list of sentences extracted from <code>filename</code>
   *            with each sentence returned as a <code>Vector</code> of words.
   */
  public Vector< Vector<String> > sentences(boolean preserveCase, boolean preservePunct) {
    Vector< Vector<String> > s;

    s = new Vector<Vector<String>>(sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      s.add(wordsFromOp(sentences.get(i), preserveCase, preservePunct));
    }

    return s;
  }

  /**
   * Returns the same as @{link TextParser#sentences} with the <code>Vector</code>
   * list's of words converted to <code>String</code>'s.
   *
   * Accessor for {@link TextParser#sentences} with the words in the sentences
   * converted to <code>String</code>'s.
   *
   *
   * @return    A list of sentences extracted from <code>filename</code>
   *            with each sentence returned as a <code>String</code>.
   */
  public Vector<String> sentencesAsStr(boolean preserveCase, boolean preservePunct) {
    Vector<String> sent = new Vector<String>();
    Vector<String> words;
    StringBuilder sb;

    sb = new StringBuilder();

    for ( int i = 0; i < sentences.size(); i++ ) {
      words = wordsFromOp(sentences.get(i), preserveCase, preservePunct);
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
   *
   */
  private Vector<String> wordsFromOp(SplitterOp op, boolean preserveCase, boolean preservePunct) {
    if ( preservePunct ) {
      if ( preserveCase ) {
        return op.words();
      } else {
        return op.wordsWithoutCase();
      }
    } else {
      // !preserve_punctuation
      if ( preserveCase ) {
        return op.wordsWithoutPunct();
      } else {
        // !preserve_punctuation && !preserve_case
        return op.wordsWithoutPunctAndCase();
      }
    }
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

    parser_data.line_starts = new HashSet<Integer>();
    clean = new Vector<String>();
    len = 0;
    lineCt = 0;

    for ( int i = 0; i < raw.size(); i++ ) {
      str = WhitespaceRemover.remove(raw, i);
      if ( str != null ) {
        clean.add(str);
        parser_data.line_starts.add(len);
        len += str.length();
        lineCt++;
      }
    }

    pos = 0;
    parser_data.buffer = new char[len];
    for ( int i = 0; i < clean.size(); i++ ) {
      buf = clean.get(i).toCharArray();
      System.arraycopy(buf, 0, parser_data.buffer, pos, buf.length);
      pos += buf.length;
    }

    parser_data.sentence_map = new SentenceMapEntry[parser_data.buffer.length];
    parser_data.avg_line_char_ct = (len/lineCt);

    if ( logger.isInfoEnabled() ) {
      logger.info("Statistics for " + filename +
          " Raw: LineCt=" + raw.size() +
          " Cleansed: LineStarts=" + parser_data.line_starts.size() +
          " CharCt=" + parser_data.buffer.length +
          " AvgLineCharCt=" + parser_data.avg_line_char_ct);
    }
  }

  /**
   * Runs the sentence extraction algorithm to generate the sentences
   * for {@link TextParser#sentences}.
   */
  public void genSentences() throws ParserException {
    SentenceMapEntry entry;
    SentenceEntryType type;
    Likelihood likelihood;
    SplitterOp op;

    genSentenceMap();

    if ( sentences != null ) {
      sentences.clear();
      parser_data.parser_map.clear();
    } else {
      sentences = new Vector<SplitterOp>();
      parser_data.parser_map = new Vector<TextParserOp>();
    }
    parser_data.extraction_map = new boolean[parser_data.buffer.length];

    sent_start_idx = -1;
    sent_unlikely_start_idx = -1;
    sent_end_idx = -1;
    sent_unlikely_end_idx = -1;

    for ( int i = 0; i < parser_data.sentence_map.length; i++ ) {
      entry = parser_data.sentence_map[i];
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
          Helper.getDebugStringFromCharBuf(parser_data.buffer, 0,
              parser_data.buffer.length, 100) +
          "SentenceMap:\n" +
          Helper.getDebugStringFromSentenceMap(parser_data.buffer,
              parser_data.sentence_map, 0,
              parser_data.sentence_map.length, 100) +
          "ExtractionMap:\n" +
          Helper.getDebugStringFromBoolBuf(parser_data.buffer,
              parser_data.extraction_map, 0,
              parser_data.extraction_map.length, 100));
    }

    for ( int i = 0; i < parser_data.parser_map.size(); i++ ) {
      op = splitter.split(parser_data.parser_map.get(i));
      if ( op.wordCt() >= min_sentence_len ) {
        sentences.add(op);
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
    parser_data.parser_map.add(new TextParserOp(start, end));
    for ( int i = start; i <= end; i++ ) {
      parser_data.extraction_map[i] = true;
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
  protected void genSentenceMap() throws ParserException {
    char ch;
    EndOp endOp;
    StartOp startOp;
    boolean inHeading;
    Evaluator evaluator;
    Sentence sentence;

    inHeading = false;
    sentence = new Sentence(parser_data);

    try {
      sentence.invalidate();
    } catch ( EvaluatorException ee ) {
      throw new ParserException("Failed to validate sentence", ee);
    }

    for ( int i = 0; i < parser_data.buffer.length; i++ ) {
      ch = parser_data.buffer[i];

      if ( sentence_ends.contains(ch) ) {
        try {
          endOp = sentence.isEnd(parser_data.buffer, i);
        } catch ( SentenceException se ) {
          throw new ParserException("Error during evaluation of end", se);
        }

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
        } else if ( (i > 0) && !Character.isWhitespace(parser_data.buffer[i-1]) &&
            !sentence_ends.contains(parser_data.buffer[i-1]) ) {
          // the setence_ends conditional is included for the case
          // ...: "As ...
          continue;
        }

        try {
          startOp = sentence.isStart(parser_data.buffer, i, inHeading);
        } catch ( SentenceException se ) {
          throw new ParserException("Error during evaluation of start", se);
        }
        if ( startOp != null ) {
          if ( !startOp.isStart() ) {
            evaluator = startOp.failedEvaluator();

            if ( evaluator != null ) {
              if ( evaluator instanceof Heading ) {
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

    old = parser_data.sentence_map[idx];
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
      parser_data.sentence_map[idx] = entry;
    }
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
     * In memory representation of the text file with whitespace removed.
     * Protected because it is used by {@link SentenceSplitter}.
     */
    protected char[] buffer;

    /**
     * A buffer that contains where the line starts, which is used by some
     * by the {@link com.orbious.extractor.evaluator.NumberedHeading} <code>Evaluator</code>.
     */
    protected HashSet<Integer> line_starts;

    /**
     * A buffer that contains entries for likely/unlikely sentence start's/end's.
     */
    protected SentenceMapEntry[] sentence_map;

    /**
     * A <code>Vector</code> of <code>TextParserOp</code> containing sentence
     * start/ends. Protected because it is used by {@link SentenceSplitter}.
     */
    protected Vector< TextParserOp > parser_map;

    /**
     * A buffer that contains a record of all the characters that have
     * been extracted and is used for index adjustments of the start/end indexes
     * (i.e. {@link TextParser#adjustIndexes(int, int)}).
     */
    protected boolean[] extraction_map;

    /**
     * The average number of characters on each line.
     */
    protected int avg_line_char_ct = -1;

    /**
     * Constructor, initializes an empty <code>TextParserData</code>.
     */
    public TextParserData() { }

    /**
     * Should only be used for testing.
     */
    public void _setTextParserData(char[] buffer,
        HashSet<Integer> lineStarts,
        SentenceMapEntry[] sentenceMap,
        Vector<TextParserOp> parserMap,
        boolean[] extractionMap,
        int avgLineCharCt) {

      this.buffer = buffer;
      line_starts = lineStarts;
      sentence_map = sentenceMap;
      parser_map = parserMap;
      extraction_map = extractionMap;
      avg_line_char_ct = avgLineCharCt;
    }

    /**
     * Determines if the index <code>idx</code> in the <code>TextParser</code>
     * character buffer is a line start.
     *
     * @param idx   The index in the {@link TextParser#buffer}.
     *
     * @return    <code>true</code> if <code>idx</code> is a line start,
     *            <code>false</code> otherwise.
     */
    public boolean containsLineStart(int idx) {
      if ( line_starts == null ) {
        throw new NullPointerException(
            "TextParserData (line_starts) not initialized correctly");
      }

      return( line_starts.contains(idx) );
    }

    /**
     * Determines if the index <code>idx</code> in the <code>TextParser</code>
     * character buffer lines on a line containing a heading.
     *
     * @param idx   The index in the {@link TextParser#buffer}.
     *
     * @return    <code>true</code> if <code>idx</code> is on the same line
     *            as a heading, <code>false</code> otherwise.
     */
    public boolean containsHeading(int idx) {
      if ( sentence_map == null ) {
        throw new NullPointerException(
            "TextParserData (sentence_map) not initialized correctly");
      } else if ( line_starts == null ) {
        throw new NullPointerException(
            "TextParserData (line_starts) not initialized correctly");
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

    /**
     * Returns the previous <code>PAUSE</code> ({@link SentenceEntryType})
     * from <code>idx</code> in the <code>TextParser</code> character buffer.
     *
     * @param idx   The index in {@link TextParser#buffer}.
     *
     * @return  The index of the previous <code>PAUSE</code> or <code>-1</code>
     *          if no <code>PAUSE</code> was found.
     */
    public int findPreviousPause(int idx) {
      if ( sentence_map == null ) {
        throw new NullPointerException(
            "TextParserData (sentence_map) not initialized correctly");
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

    /**
     * Returns the previous <code>LIKELY</code> ({@link Likelihood})
     * <code>END</code> ({@link SentenceEntryType}) from <code>idx</code>
     * in the <code>TextParser</code>
     * character buffer.
     *
     * @param idx   The index in {@link TextParser#buffer}.
     *
     * @return  The index of the previous <code>LIKELY</code> <code>END</code>
     *          or <code>-1</code> if no <code>LIKELY</code> <code>END</code>
     *          was found.
     */
    public int findPreviousLikelyEnd(int idx) {
      if ( sentence_map == null ) {
        throw new NullPointerException(
            "TextParserData (sentence_map) not initialized correctly");
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

    /**
     * Returns the previous <code>UNLIKELY</code> ({@link Likelihood})
     * <code>END</code> ({@link SentenceEntryType}) from <code>idx</code>
     * in the <code>TextParser</code>
     * character buffer.
     *
     * @param idx   The index in {@link TextParser#buffer}.
     *
     * @return  The index of the previous <code>UNLIKELY</code> <code>END</code>
     *          or <code>-1</code> if no <code>UNLIKELY</code> <code>END</code>
     *          was found.
     */
    public int findPreviousUnlikelyEnd(int idx) {
      if ( sentence_map == null ) {
        throw new NullPointerException(
            "TextParserData (sentence_map) not initialized correctly");
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
}
