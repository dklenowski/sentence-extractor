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
 * Parser a text document into sentences.
 * 
 * @author dave
 * @version 1.0
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
   * List of allowable sentence ends.
   */
  private HashSet<Character> allowable_ends;
  
  /**
   * A buffer that contains entries for likely/unlikely sentence start's/end's.
   */
  private SentenceMapEntry[] sentence_map;
  
  /**
   * Contains a list of sentences extracted from <code>filename</code>.
   */
  private Vector< Vector<String> > sentences;
  
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
    allowable_ends = Helper.cvtStringToHashSet(Config.SENTENCE_ENDS.asStr());
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
   * list's of words converted to <code>String</code>'s. These string's are
   * run through the @{link {@link Cleanser#cleanWordsAsStr(Vector)} to
   * generate the <code>String</code> values.
   * 
   * Accessor for {@link TextParser#sentences} with the words in the sentences
   * converted to <code>String</code>'s.
   * 
   * @return    A list of sentences extracted from <code>filename</code>
   *            with each sentence returned as a <code>String</code>.
   */
  public Vector<String> sentencesAsStr() {
    Vector<String> sent = new Vector<String>();
    
    for ( int i = 0; i < sentences.size(); i++ ) {
      sent.add(Cleanser.cleanWordsAsStr(sentences.get(i)));
    }

    return(sent);
  }
  
  /**
   * Parses {@link TextParser#filename} into memory. This method also calls 
   * {@link WhitespaceRemover#remove(Vector, int)} on each line
   * before adding to memory.
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
          " Raw LineCt=" + raw.size() + 
          " Cleansed CharCt=" + buffer.length);
    }
  }
 
  /**
   * Runs the sentence extraction algorithm to generate the sentences
   * for {@link TextParser#sentences}.
   */
  public void genSentences() {
    int startIdx;
    int unlikelyStartIdx;
    int unlikelyEndIdx;
    SentenceMapEntry entry;
    SentenceEntryType type;
    String debugStr;
    
    generateSentenceMap();    

    sentences = new Vector< Vector<String> >();
   
    startIdx = -1;
    unlikelyStartIdx = -1;
    unlikelyEndIdx = -1;
    debugStr = "";

    for ( int i = 0; i < sentence_map.length; i++ ) {
      entry = sentence_map[i];
      if ( entry == null ) {
        continue;
      }
      
      type = entry.type();
      
      if ( type == SentenceEntryType.START ) {
        if ( entry.likelihood() == Likelihood.UNLIKELY ) {
          unlikelyStartIdx = i;
        } else {
          if ( (startIdx != -1) && (unlikelyEndIdx != -1) ) {
            // special case, we have a start index and no end index
            // use the unlikely end idx
            if ( logger.isDebugEnabled() ) {
              logger.debug("Using unlikely: StartIdx=" + startIdx + 
                  " UnlikelyEndIdx=" + unlikelyEndIdx);
            }
            sentences.add(extract(unlikelyStartIdx, i));
            unlikelyEndIdx = -1;
          }
          startIdx = i;
          if ( logger.isDebugEnabled() ) {
            debugStr =  "StartIdx=" + startIdx;
          }
        }
      } else if ( type == SentenceEntryType.END ) {
        if ( entry.likelihood() == Likelihood.UNLIKELY ) {
          unlikelyEndIdx = i;
        } else if ( (startIdx == -1) && (unlikelyStartIdx == -1) ) {
          if ( logger.isDebugEnabled() ) {
            debugStr = "No StartIdx for endIdx=" + i;
          }
          // store for later
        } else {
          if ( startIdx == -1 ) {
            // no concrete startIdx use unlikelyStartIdx
            if ( logger.isDebugEnabled() ) {
              debugStr = "Using unlikely: UnlikelyStartIdx=" + unlikelyStartIdx + 
                  " EndIdx=" + i;
            }
            sentences.add(extract(unlikelyStartIdx, i));
            unlikelyStartIdx = -1;
          } else {
            if ( logger.isDebugEnabled() ) {
              debugStr = "Using likely: StartIdx=" + startIdx + 
                  " EndIdx=" + i;
            }
            sentences.add(extract(startIdx, i));
            startIdx = -1;          
          }
        }
      }
      
      if ( (debugStr.length() != 0) && logger.isDebugEnabled() ) {
        logger.debug(debugStr + "\n" + 
            Helper.getDebugStringFromSentenceMap(buffer, sentence_map, i, 200, -1) + "\n" +
            Helper.getDebugStringFromCharBuf(buffer, i, 200) + "\n");
        debugStr = "";
      }
    }
  }
  
  /**
   * 
   */
  protected void generateSentenceMap() {
    char ch;
    EndOp endOp;
    StartOp startOp;
    
    sentence_map = new SentenceMapEntry[buffer.length];
    
    for ( int i = 0; i < buffer.length; i++ ) {
      ch = buffer[i];
      
      if ( allowable_ends.contains(ch) ) {
        // check for ends
        endOp = Sentence.isEnd(buffer, i);
        if ( !endOp.isEnd() ) {
          addToMap(i, Likelihood.UNLIKELY, SentenceEntryType.END, null);
        } else {
          addToMap(i, Likelihood.LIKELY, SentenceEntryType.END, null);
          
          if ( endOp.startIdx() >= 0 ) {
            addToMap(endOp.startIdx(), Likelihood.LIKELY, 
                SentenceEntryType.START, SentenceEntrySubType.START_FROM_END);
          }
        }
      } else {
        // run a quick check to reduce the workload
        if ( !Character.isUpperCase(ch) ) {
          continue;
        }

        startOp = Sentence.isStart(buffer, i);
        if ( startOp != null ) {
          if ( !startOp.isStart() ) {
            addToMap(i, Likelihood.UNLIKELY, SentenceEntryType.START, null);
          } else {
            addToMap(i, Likelihood.LIKELY, SentenceEntryType.START, null);
          
            if ( startOp.stopIdx() >= 0 ) {
              addToMap(startOp.stopIdx(), Likelihood.LIKELY, 
                  SentenceEntryType.START, SentenceEntrySubType.END_FROM_START);
            }
          }
        }
      }
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug("SentenceMap\n" + 
          Helper.getDebugStringFromSentenceMap(buffer, sentence_map, 0, 
              buffer.length, 100));
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
    } else {
      if ( old.likelihood() == Likelihood.UNLIKELY ) {
        replace = true;
      }
    }

    if ( replace ) {
      entry = new SentenceMapEntry(likelihood, type, subtype);
      sentence_map[idx] = entry;
    }
    
  }
  
  /**
   * Extract a <code>Vector</code> of words from {@link TextParser#buffer}.
   * 
   * @param startIdx    The start index to begin extraction.
   * @param endIdx    The end index to stop extraction.
   * 
   * @return    A <code>Vector</code> of words.
   */
  protected Vector<String> extract(int startIdx, int endIdx) {
    Vector<String> words;
    String wd;
    char ch;
    boolean hasLetter;
    
    words = new Vector<String>();
    wd = "";
    hasLetter = false;
    
    if ( logger.isDebugEnabled() ) {
      logger.debug("Beginning extract startIdx=" + startIdx + " endIdx=" + endIdx);
    }
    
    for ( int i = startIdx; i <= endIdx; i++ ) {
      ch = buffer[i];
      if ( Character.isLetterOrDigit(ch) ) {
        wd += ch;
        hasLetter = true;
      } else if ( Character.isWhitespace(ch) ) {
        if ( wd.length() == 0 ) {
          continue;
        }
        
        words.add(wd);
        System.out.println("CASE 1 WORD="+ wd);
        wd = "";
        hasLetter = false;
        
      } else {
        // punctuation
        if ( hasLetter ||
            ((i-1 >= 0) && Character.isLetter(buffer[i-1])) ) {
          // the punctuation is attached to the word e.g. type's, time-line
          // hasLetter assumes there is text to the right,
          // if that fails we need to test there is text to the left
          if ( new UrlText().evaluate(buffer, i) ) {
            wd += ch;
          } else if ( ch != ',' && !allowable_ends.contains(ch) ) {
            wd += ch;
          } else {
            // the exceptions are sentence ends are ','
            if ( wd.length() != 0 ) {
              words.add(wd);
            }
            words.add(Character.toString(ch));
            wd = "";
            hasLetter = false;
          } 
        } else {
          // punctuation, add as a separate 'wd'
          if ( wd.length() != 0 ) {
            words.add(wd);
          }
          words.add(Character.toString(ch));
          wd = "";
          hasLetter = false;;
        }
      }
    }
    
    if ( hasLetter ) {
      words.add(wd);
    }
    
    if ( logger.isDebugEnabled() ) {
      logger.debug(Helper.cvtVectorToString(words));
    }
    
    return(words);
  }
}
