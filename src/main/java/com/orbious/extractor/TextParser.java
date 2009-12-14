package com.orbious.extractor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;
import org.apache.log4j.Logger;

import com.orbious.util.Helper;

/**
 * $Id: TextParser.java 14 2009-12-06 10:03:53Z app $
 * <p>
 * Parser a text document into sentences.
 * 
 * @author dave
 * @version 1.0
 */
public class TextParser {
  
  /**
   * The plain-text file we wish to extract sentences from.
   */
  private String filename;

  /**
   * A <code>HashSet</code> containing valid sentence ends.
   * @see {@link Config#SENTENCE_ENDS}
   */
  private HashSet<Character> sentence_ends;
  
  /**
   * The plain-text document {@link TextParser#filename}
   * parsed and cleaned into an in memory <code>char</code> buffer.
   * @see {@link Cleanser}
   */
  private char[] buffer;
  
  /**
   * A <code>Vector</code> of <code>Vector</code> words for 
   * sentences that were found in {@link TextParser#filename}.
   */
  private Vector< Vector<String> > sentences;
  
  /**
   * A <code>Logger</code> object.
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
  }
  
  /**
   * Accessor for {@link TextParser#buffer}.
   * 
   * @return  {@link TextParser#buffer}.
   */
  public char[] getBuffer() {
    return(buffer);
  }
  
  /**
   * Accessor for {@link TextParser#sentences}.
   * 
   * @return  {@link TextParser#sentences}
   */
  public Vector< Vector<String> > getSentences() {
    return(sentences);
  }
  
  /**
   * Accessor for {@link TextParser#sentences} with the words in the sentences
   * converted to <code>String</code>'s.
   * 
   * @return  {@link TextParser#sentences} with <code>String</code> sentences.
   */
  public Vector<String> getSentencesAsStr() {
    Vector<String> sent = new Vector<String>();
    
    for ( int i = 0; i < sentences.size(); i++ ) {
      sent.add(Cleanser.cleanWordsAsStr(sentences.get(i)));
    }

    return(sent);
  }
  
  /**
   * Parses {@link TextParser#filename} and removes all the whitespace
   * (using {@link Cleanser#removeWhitespace(Vector, int)}) 
   * populating {@link TextParser#buffer}. 
   * 
   * @throws FileNotFoundException
   * @throws IOException
   */
  public void read() throws FileNotFoundException, IOException {
    BufferedReader br;
    Vector<String> raw;
    Vector<String> cleaned;
    String line;
    char[] buf;
    int len;
    int pos;
    
    
    br = new BufferedReader(new FileReader(filename));
    raw = new Vector<String>();

    while ( (line = br.readLine()) != null ) {
      raw.add(line);
    }
    br.close();
    
    logger.info("Found " + raw.size() + " lines in " + filename);
    
    cleaned = new Vector<String>();
    len = 0;
    
    for ( int i = 0; i < raw.size(); i++ ) {
      line = Cleanser.removeWhitespace(raw, i);
      if ( line != null ) {
        cleaned.add(line);
        len += line.length();
      }
    }

    pos = 0;
    buffer = new char[len];
    for ( int i = 0; i < cleaned.size(); i++ ) {
      buf = cleaned.get(i).toCharArray();
      System.arraycopy(buf, 0, buffer, pos, buf.length);
      pos += buf.length;
    }
    
    logger.info("Found " + len + " cleansed characters in " + filename);
  }
  
  
  public void parse() {
    Vector<String> raw;
    Vector<String> cleansed;
    int i;
    
    sentences = new Vector< Vector<String> >();
    i = 0;
    
    while ( i < buffer.length ) {
      if ( sentence_ends.contains(buffer[i]) ) {
        raw = null;
        try {
          raw = Sentence.getPreviousSentence(buffer, i);
        } catch ( SentenceException se ) {
          logger.fatal("Logic Error, idx=" + i + 
              "\nRaw=" + Helper.getDebugStringFromCharBuf(buffer, i, 50),
              se);
        }
    
        if ( raw != null ) {
          cleansed = Cleanser.cleanWords(raw);
          
          if ( logger.isDebugEnabled() ) {
            logger.debug("SetenceEnd idx=" + i + 
                "\nBuffer:\n" +
                Helper.getDebugStringFromCharBuf(buffer, i, 50) + 
                "\t\nRaw     =" + raw + 
                "\t\nCleansed=" + cleansed + "\n\n");
            }

            sentences.add(cleansed);
          }
        }
      
      i++;
    }
  }
}
