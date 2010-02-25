package com.orbious.extractor;

/**
 * Class to represent sentence start/end indexes.
 * 
 * @author dave
 * @since 1.0
 * @version 1.0
 */
public class TextParserOp {
  
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
