package com.orbious.extractor.util;

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
   * Determines if the previous non-whitespace character in <code>buf</code>
   * from <code>idx</code> is a alpha numeric character.
   *
   * @param buf   Text buffer.
   * @param idx   Position in <code>buf</code>.
   *
   * @return    <code>true</code> if the first previous non-whitespace
   *            character is a alpha numeric character, <code>false</code> otherwise.
   */
  public static boolean isPreviousAlpha(final char[] buf, int idx) {
    int i;

    i = moveToNonWhitespace(ParseDirn.LEFT, buf, idx);
    if ( i == idx ) {
      return(false);
    }

    return( Character.isLetterOrDigit(buf[i]) );
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
  public static boolean isNextAlpha(final char[] buf, int idx) {
    int i;

    i = moveToNonWhitespace(ParseDirn.RIGHT, buf, idx);
    if ( i == idx ) {
      return(false);
    }

    return( Character.isLetterOrDigit(buf[i]) );
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
}
