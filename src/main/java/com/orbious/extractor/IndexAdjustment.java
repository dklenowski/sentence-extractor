package com.orbious.extractor;

/**
 * A class to represent adjusted start/end indexes for a sentence.
 * 
 * @author dave
 * @since 1.0
 * @version 1.0
 */
public class IndexAdjustment {
  
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
