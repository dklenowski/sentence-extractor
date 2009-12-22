package com.orbious.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.orbious.extractor.Config;
import com.orbious.extractor.SentenceMapEntry;
import com.orbious.extractor.SentenceMapEntry.Likelyhood;
import com.orbious.extractor.SentenceMapEntry.SentenceEntrySubType;
import com.orbious.extractor.SentenceMapEntry.SentenceEntryType;


/**
 * $Id: Helper.java 11 2009-12-04 14:07:11Z app $
 * <p>
 * Static helper methods.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class Helper {

  /**
   * Private Constructor
   */
  private Helper() { }
  
  /**
   * Returns a <code>String</code> containing text extracted from 
   * <code>buf</code> of size <code>size</code> relative to the position 
   * <code>pos</code>. <code>start</code> and <code>end</code> are 
   * automatically update to the <code>buf</code> 
   * extremium's if required.
   * 
   * @param buf    Text buffer.
   * @param idx    The <code>buf</code> array index specifying the 
   *               middle point for extraction.
   * @param size   The length of the <code>String</code> to return.
   * 
   * @return    A <code>String</code> containing text extracted from 
   *           <code>buf</code>.
   */
  public static String getDebugStringFromCharBuf(char[] buf, int idx, int size) {
    StringBuffer sb;
    StringBuffer id;
    int start;
    int end;

    sb = new StringBuffer();
    id = new StringBuffer();
    
    start = idx-(size/2);
    if ( start < 0 ) {
      size += Math.abs(start);
      start = 0;
    }
    
    end = idx+(size/2)+1;
    if ( end >= buf.length ) {
      end = buf.length;
    }
    
    for ( int i = start; i < end; i++ ) {
      sb.append(buf[i]);
      if ( i == idx ) {
        id.append("|");
      } else {
        id.append("-");
      }
    }

    return( sb.toString() + "\n" + id.toString() );
  }
  
  /**
   * 
   * @param template
   * @param buf
   * @param idx
   * @param size
   * @param width
   * @return
   */
  public static String getDebugStringFromSentenceMap(final char[] template,
      final SentenceMapEntry[] buf, int idx, int size, int width) { 
    String str;
    int modct;
    SentenceMapEntry entry;
    str = "";
    modct = 1;
    int start;
    int end;
    
    start = idx-(size/2);
    if ( start < 0 ) {
      size += Math.abs(start);
      start = 0;
    }
    
    end = idx+(size/2)+1;
    if ( end >= buf.length ) {
      end = buf.length;
    }
    
    for ( int i = start; i < end; i++ ) {
      if ( Character.isWhitespace(template[i]) ) {
        str += " ";
      } else {
        entry = buf[i];
        if ( entry == null ) {
          str += ".";
        } else if ( entry.type() == SentenceEntryType.END ) {
          if ( entry.subtype() == SentenceEntrySubType.END_FROM_START ) {
            // this is always likely
            str += "e";
          } else {
            // 
            if ( entry.likelyhood() == Likelyhood.LIKELY ) {
              str += "E";
            } else {
              str += "U";
            }
          }
        } else if ( entry.type() == SentenceEntryType.START ) {
          if ( entry.subtype() == SentenceEntrySubType.START_FROM_END ) {
            // always likely 
            str += "s";
          } else {
            if ( entry.likelyhood() == Likelyhood.LIKELY ) {
              str += "S";
            } else {
              str += "n";
            }
          }
        }
      }
      
      if ( (width != -1) && (modct % width == 0) ) {
        str += "\n";
      }
      modct++;
    }
    
    str += "\n";
    return(str);
  }
  
  /**
   * Convert the contents of a <code>String</code> to a 
   * <code>HashSet</code> separated on <code>Character</code> boundaries.
   * 
   * @param str    The <code>String</code> to interrogate.
   * @return       A <code>HashSet</code> containing the contents of the 
   *               <code>String</code> <code>str</code>.
   */
  public static HashSet<Character> cvtStringToHashSet(String str) {
    char[] buf = str.toCharArray();
    HashSet<Character> hs = new HashSet<Character>();
    
    for ( int i = 0; i < buf.length; i++ ) {
    hs.add(buf[i]);
    }
    
    return(hs);
  }
  
  /**
   * Converts the contents of the file <code>filename</code> to 
   * a <code>HashSet</code> of <code>String</code>' which each line in the 
   * file occupying an entry in the <code>HashSet</code>.
   * 
   * @param filename    The absolute filename to parse.
   * @return            A <code>HashSet</code> containing the contents
   *                    of <code>filename</code>.
   */
  public static HashSet<String> cvtFileToHashSet(String filename) {
    Logger logger;
    HashSet<String> hs;
    BufferedReader br;
    
    br = null;
    
    
    logger = Logger.getLogger(Config.LOGGER_REALM.asStr());
    try {
      br = new BufferedReader(
          new FileReader(filename));
    } catch ( FileNotFoundException fnfe ) {
      logger.fatal("Failed to open file " + filename, fnfe);
    }
    
    hs = new HashSet<String>();

    try {
      String wd;
      while ( (wd = br.readLine()) != null ) {
        if ( !wd.matches("#.*") ) {
          // ignore comments.
          hs.add(wd);
        } 
      }
    } catch ( IOException ioe ) {
      logger.fatal("Failed to read names file " + 
          Config.NAMES_FILENAME, ioe);
    }
    
    logger.info("Extracted " + hs.size() + " entries from " + filename);
    return(hs);
  }
  
  /**
   * Convert a <code>Vector</code> of <code>String</code>'s to a single 
   * <code>String</code> separated by whitespace.
   * 
   * @param words    A list of <code>String</code>'s to convert.
   * @return        The <code>Vector</code> with its <code>String</code>
   *                contents appended to a <code>String</code>.
   */
  
  public static String cvtVectorToString(Vector<String> words) {
    String str = "|";

    for ( int i = 0; i < words.size(); i++ ) {
      if ( i+1 < words.size() ) {
        str += words.get(i) + " ";
      } else {
        str += words.get(i);
      }
    }

    str += "|";
    return(str);
  }
}
