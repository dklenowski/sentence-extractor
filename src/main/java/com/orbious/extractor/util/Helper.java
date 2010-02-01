package com.orbious.extractor.util;

// $Id: Helper.java 11 2009-12-04 14:07:11Z app $

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.orbious.extractor.Config;
import com.orbious.extractor.ParseDirn;
import com.orbious.extractor.SentenceMapEntry;
import com.orbious.extractor.SentenceMapEntry.Likelihood;
import com.orbious.extractor.SentenceMapEntry.SentenceEntrySubType;
import com.orbious.extractor.SentenceMapEntry.SentenceEntryType;

/**
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
   * <code>pos</code>. Also includes a string specifying the location
   * of <code>idx</code>.
   * <code>start</code> and <code>end</code> are 
   * automatically update to the <code>buf</code> 
   * extremium's if required.
   * 
   * @param buf   Text buffer.
   * @param idx   The <code>buf</code> array index specifying the 
   *              middle point for extraction.
   * @param size    The length of the <code>String</code> to return.
   * 
   * @return    A <code>String</code> containing text extracted from 
   *            <code>buf</code>.
   */
  public static String getDebugStringFromCharBuf(char[] buf, int idx, int size) {
    StringBuilder str;
    StringBuilder id;
    int start;
    int end;

    str = new StringBuilder();
    id = new StringBuilder();

    start = idx-(size/2);
    if ( start < 0 ) {
      size += Math.abs(start*2);
      start = 0;
    }
    
    end = idx+(size/2)+1;
    if ( end >= buf.length ) {
      end = buf.length;
    }
    
    for ( int i = start; i < end; i++ ) {
      str.append(buf[i]);
      if ( i == idx ) {
        id.append("|");
      } else {
        id.append("-");
      }
    }

    return( str + "\n" + id );
  }
  
  /**
   * Returns a <code>String</code> containing text extracted from 
   * <code>buf</code> of size <code>size</code> relative to the position 
   * <code>pos</code>. <code>start</code> and <code>end</code> are 
   * automatically update to the <code>buf</code> 
   * extremium's if required.
   * 
   * @param buf   Text buffer.
   * @param idx   The <code>buf</code> array index specifying the 
   *              middle point for extraction.
   * @param size    The length of the <code>String</code> to return.
   * @param width   The width of the debug string where a newline is inserted
   *                at each length <code>width</code> in the debug string.
   *                If <code>-1</code>, no newline is inserted.
   * @return    A <code>String</code> containing text extracted from 
   *            <code>buf</code>.
   */
  public static String getDebugStringFromCharBuf(final char[] buf,
      int idx, int size, int width) { 
    StringBuilder sb;
    int modct;
    int start;
    int end;
    int lineCt;
    
    sb = new StringBuilder();
    lineCt = 0;
    if ( width != -1 ) {
      sb.append(String.format("%5s :", lineCt));
      lineCt++;
    } 
    modct = 1;
    
    start = idx-(size/2);
    if ( start < 0 ) {
      size += Math.abs(start*2);
      start = 0;
    }
    
    end = idx+(size/2)+1;
    if ( end > buf.length ) {
      end = buf.length;
    }
 
    for ( int i = start; i < end; i++ ) {
      sb.append(buf[i]);
      
      if ( (width != -1) && (modct % width == 0) ) {
        sb.append("\n"  + String.format("%5s :", lineCt));
        lineCt++;
      }
      modct++;
    }
    
    sb.append("\n");
    return(sb.toString());        
  }
  
  /**
   * Returns a debugging string for an array of <code>boolean</code>'s 
   * (which is used in the sentence extraction algorithm).
   * 
   * @param template    A text buffer of the same size as <code>buf</code>
   *                    used to insert whitespace characters.    
   * @param buf   An array of <code>boolean</code>'s.
   * @param idx   The position in <code>buf</code> to begin writing
   *              the debug string.
   * @param size    The number of entries to examine in <code>buf</code>.     
   * @param width   The width of the debug string where a newline
   *                is inserted at each length <code>width</code> in the debug
   *                string. If <code>-1</code>, no newline is inserted.
   * 
   * @return    A debug string for <code>buf</code>.
   */
  public static String getDebugStringFromBoolBuf(final char[] template, 
      final boolean[] buf, int idx, int size, int width) { 
    StringBuilder sb;
    int modct;
    int start;
    int end;
    int lineCt;
    
    sb = new StringBuilder();
    lineCt = 0;
    if ( width != -1 ) {
      sb.append(String.format("%5s :", lineCt));
      lineCt++;
    } 
    modct = 1;
    
    start = idx-(size/2);
    if ( start < 0 ) {
      size += Math.abs(start*2);
      start = 0;
    }
    
    end = idx+(size/2)+1;
    if ( end > buf.length ) {
      end = buf.length;
    }
 
    for ( int i = start; i < end; i++ ) {
      if ( Character.isWhitespace(template[i]) ) {
        sb.append(" ");
      } else {
        if ( buf[i] ) {
          sb.append(".");
        } else {
          sb.append("+");
        }
      }
      
      if ( (width != -1) && (modct % width == 0) ) {
        sb.append("\n" + String.format("%5s :", lineCt));
        lineCt++;
      }
      modct++;
    }
    
    sb.append("\n");
    return(sb.toString());    
  }
  
  /**
   * Returns a debugging string for an array of <code>SentenceMapEntry</code>'s.
   * 
   * @param template    A text buffer of the same size as <code>buf</code>
   *                    used to insert whitespace characters.
   * @param buf   An array of <code>SentenceMapEntry</code>'s.
   * @param idx   The position in <code>buf</code> to begin writing
   *              the debug string.
   * @param size    The number of entries to examine in <code>buf</code>.     
   * @param width   The width of the debug string where a newline
   *                is inserted at each length <code>width</code> in the debug
   *                string. If <code>-1</code>, no newline is inserted.
   * 
   * @return    A debug string for <code>buf</code>.
   */
  public static String getDebugStringFromSentenceMap(final char[] template,
      final SentenceMapEntry[] buf, int idx, int size, int width) { 
    StringBuilder sb;
    int modct;
    SentenceMapEntry entry;
    SentenceEntryType type;
    SentenceEntrySubType subtype;
    int start;
    int end;
    int lineCt;
    
    sb = new StringBuilder();
    lineCt = 0;
    if ( width != -1 ) {
      sb.append(String.format("%5s :", lineCt));
      lineCt++;
    } 
    modct = 1;
    
    start = idx-(size/2);
    if ( start < 0 ) {
      size += Math.abs(start*2);
      start = 0;
    }
    
    end = idx+(size/2)+1;
    if ( end >= buf.length ) {
      end = buf.length;
    }
    
    // e - Likely end from start
    // E - likely end
    // U - unlikely end
    // s - Likely start from end
    // S - likely start
    // u - unlikely start
    // P - pause
    // H - Heading
    for ( int i = start; i < end; i++ ) {
      if ( Character.isWhitespace(template[i]) ) {
        sb.append(" ");
      } else {
        entry = buf[i];
        if ( entry == null ) {
          sb.append(".");
        } else {
          type = entry.type();
          subtype = entry.subtype();
          
          if ( type == SentenceEntryType.PAUSE ) {
            sb.append("P");
          } else if ( type == SentenceEntryType.HEADING ) {
            sb.append("H");
          } else if ( type == SentenceEntryType.END ) {
            if ( subtype == SentenceEntrySubType.END_FROM_START ) {
              // this is always likely
              sb.append("e");
            } else {
              // 
              if ( entry.likelihood() == Likelihood.LIKELY ) {
                sb.append("E");
              } else {
                sb.append("U");
              }
            }
          } else if ( type == SentenceEntryType.START ) {
            if ( subtype == SentenceEntrySubType.START_FROM_END ) {
              // always likely 
              sb.append("s");
            } else {
              if ( entry.likelihood() == Likelihood.LIKELY ) {
                sb.append("S");
              } else {
                sb.append("n");
              }
            }
          } 
        }
      }
      
      if ( (width != -1) && (modct % width == 0) ) {
        sb.append("\n" + String.format("%5s :", lineCt));
        lineCt++;
      }
      modct++;
    }
    
    sb.append("\n");
    return(sb.toString());
  }
  
  /**
   * Convert the contents of a <code>String</code> to a 
   * <code>HashSet</code> separated on <code>Character</code> boundaries.
   * 
   * @param str   The <code>String</code> to interrogate.
   * 
   * @return    A <code>HashSet</code> containing the contents of the 
   *            <code>String</code> <code>str</code>.
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
   * @param lowercase   Convert text to lowercase before adding to <code>HashSet</code>.
   * @return    A <code>HashSet</code> containing the contents
   *            of <code>filename</code>.
   */
  public static HashSet<String> cvtFileToHashSet(String filename, boolean lowercase)
    throws FileNotFoundException {
    Logger logger;
    HashSet<String> hs;
    BufferedReader br;
    InputStream in;
    
    br = null;

    in = getResourceStream(filename);
    logger = Logger.getLogger(Config.LOGGER_REALM.asStr());
    if ( in == null ) {
      throw new FileNotFoundException("Failed to open file " + filename);
    }
    
    br = new BufferedReader(new InputStreamReader(in));
    hs = new HashSet<String>();

    try {
      String wd;
      while ( (wd = br.readLine()) != null ) {
        if ( !wd.matches("#.*") ) {
          // ignore comments.
          if ( lowercase ) {
            hs.add(wd.toLowerCase());
          } else {
            hs.add(wd);
          }
        } 
      }
    } catch ( IOException ioe ) {
      logger.fatal("Failed to read names file " + 
          Config.NAMES_FILENAME, ioe);
    }
    
    try {
      br.close();
    } catch ( IOException ioe ) { } 
    
    logger.info("Extracted " + hs.size() + " entries from " + filename);
    return(hs);
  }
  
  /**
   * Convert a <code>Vector</code> of <code>String</code>'s to a single 
   * <code>String</code> separated by whitespace.
   * 
   * @param words    A list of <code>String</code>'s to convert.
   * 
   * @return    The <code>Vector</code> with its <code>String</code>
   *            contents appended to a <code>String</code>.
   */
  
  public static String cvtVectorToString(Vector<String> words) {
    StringBuilder sb;

    sb = new StringBuilder();
    for ( int i = 0; i < words.size(); i++ ) {
      sb.append(words.get(i));
      if ( i+1 < words.size() ) {
        sb.append(" ");
      } 
    }

    return(sb.toString());
  }
  
  /**
   * Determines if the previous non-whitespace character in <code>buf</code> 
   * from <code>idx</code> is a letter.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in <code>buf</code>.
   * 
   * @return    <code>true</code> if the first previous non-whitespace
   *            character is a letter, <code>false</code> otherwise.
   */
  public static boolean isPreviousLetter(final char[] buf, int idx) { 
    int i;

    i = moveToNonWhitespace(ParseDirn.LEFT, buf, idx);
    if ( i == idx ) {
      return(false);
    }
    
    return( Character.isLetter(buf[i]) );
  }
  
  /**
   * Determines if the next non-whitespace character in <code>buf</code> 
   * from <code>idx</code> is a letter.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in <code>buf</code>.
   * 
   * @return    <code>true</code> if the first next non-whitespace
   *            character is a letter, <code>false</code> otherwise.
   */
  public static boolean isNextLetter(final char[] buf, int idx) {
    int i;
    
    i = moveToNonWhitespace(ParseDirn.RIGHT, buf, idx);
    if ( i == idx ) {
      return(false);
    }
    
    return( Character.isLetter(buf[i]) );
  }
  
  /**
   * Determines if the previous non-whitespace character in <code>buf</code> 
   * from <code>idx</code> is a number.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in <code>buf</code>.
   * 
   * @return    <code>true</code> if the first previous non-whitespace
   *            character is a number, <code>false</code> otherwise.
   */
  public static boolean isPreviousNumber(final char[] buf, int idx) {
    int i;
    
    i = moveToNonWhitespace(ParseDirn.LEFT, buf, idx);
    if ( i == idx ) {
      return(false);
    }
    
    return( Character.isDigit(buf[i]) );
  }
  
  /**
   * Determines if the next non-whitespace character in <code>buf</code> 
   * from <code>idx</code> is a number.
   * 
   * @param buf   Text buffer.
   * @param idx   Position in <code>buf</code>.
   * 
   * @return    <code>true</code> if the first next non-whitespace
   *            character is a number, <code>false</code> otherwise.
   */
  public static boolean isNextNumber(final char[] buf, int idx) {
    int i;
    
    i = moveToNonWhitespace(ParseDirn.RIGHT, buf, idx);
    if ( i == idx ) {
      return(false);
    }
    
    return( Character.isDigit(buf[i]) );
  }
  
  /**
   * Returns an index that points to the first non-whitespace character
   * in <code>buf</code>.
   * 
   * @param dirn  Either <code>LEFT</code> or <code>RIGHT</code>.
   * @param buf   Text buffer.
   * @param idx   Position in <code>buf</code>.
   * 
   * @return    The first non-whitespace index in <code>buf</code> either
   *            in the <code>LEFT</code> or <code>RIGHT</code> direction.
   */
  public static int moveToNonWhitespace(ParseDirn dirn, final char[] buf, int idx) {
    int i;
    
    if ( dirn == ParseDirn.LEFT ) {
      i = idx-1;
      while ( (i > 0) && Character.isWhitespace(buf[i]) ) {
        i--;
      }
    } else {
      i = idx+1;
      while ( (i < buf.length) && Character.isWhitespace(buf[i]) ) {
        i++;
      }
    }
    
    if ( (i < 0) || (i >= buf.length) ) {
      return(-1);
    }
    
    return(i);
  }
  
  /**
   * Retreives a resource from the classpath and returns the results
   * as an <code>InputStream</code>.
   * 
   * @param filename    The filename to search for on the classpath.
   * @return    An <code>InputSteam</code> for the resource.
   */
  public static InputStream getResourceStream(String filename) {
    ClassLoader classLoader = null;
    InputStream in = null;

    classLoader = Thread.currentThread().getContextClassLoader();
    if ( classLoader != null ) {
      in = classLoader.getResourceAsStream(filename);
      if ( in != null ) {
        return(in);
      }
    }

    classLoader = Helper.class.getClassLoader();
    if ( classLoader != null ) {
      in = classLoader.getResourceAsStream(filename);
      if ( in != null ) {
        return(in);
      }
    }

    return( ClassLoader.getSystemResourceAsStream(filename) );
  }
  
  /**
   * Retrieves a resource from the classpath and returns the results
   * a <code>File<code> result. Because not many applications can understand
   * jar files, the process is to read in the file using 
   * <code>getResourceAsStream</code>, write the file to a temporary location
   * and parse the location back to the application.
   * 
   * @param filename    The filename to search for on the classpath.
   * @return    A file containing the fully qualified name of the resource.
   */
  public static File getResourceFile(String filename)
    throws IOException {
    InputStream in;
    File f;
    BufferedWriter bw;
    BufferedReader br;
    String line;
    
    in = getResourceStream(filename);
    if ( in == null ) {
      throw new FileNotFoundException("Failed to find file " + filename);
    }

    f = File.createTempFile("SentenceExtractorLog4j", ".xml");
    
    br = new BufferedReader(new InputStreamReader(in));
    bw = new BufferedWriter(new FileWriter(f));
    
    while ( (line = br.readLine()) != null ) {
      bw.write(line + "\n");
    }
    
    br.close();
    bw.close();
    
    return(f);
  }
}
