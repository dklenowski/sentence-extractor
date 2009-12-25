package com.orbious.extractor;

// $Id$

/**
 * Used by TextParser to store information for potential sentence start/ends.
 *
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class SentenceMapEntry {    
  /**
   * An enum representing the likelihood's
   */
  public enum Likelihood { LIKELY, UNLIKELY }
  
  /**
   * an enum representing the entry types.
   */
  public enum SentenceEntryType { START, END }
  
  /**
   * An enum representing the 
   */
  public enum SentenceEntrySubType { START_FROM_END, END_FROM_START  }
  
  /**
   * The likelihood of this entry.
   */
  private Likelihood likelihood;
  
  /**
   * The type of this entry.
   */
  private SentenceEntryType type;
  
  /**
   * The subtype of this entry.
   */
  private SentenceEntrySubType subtype;
  
  /**
   * Initializes an empty <code>SentenceMapEntry</code>.
   */
  public SentenceMapEntry() { }

  /**
   * Initializes the <code>SentenceMapEntry</code>.
   * 
   * @param likelihood    The likelihood of this entry.
   * @param type    The type of entry.
   */
  public SentenceMapEntry(Likelihood likelihood, SentenceEntryType type) {
    this.likelihood = likelihood;
    this.type = type;
  }
  
  /**
   * Initializes the <code>SentenceMapEntry</code>.
   * 
   * @param likelihood    The likelihood of this entry.
   * @param type    The type of entry.
   * @param subtype   The subtype of the entry.
   */
  public SentenceMapEntry(Likelihood likelihood, SentenceEntryType type,
      SentenceEntrySubType subtype) {
    this.likelihood = likelihood;
    this.type = type;
    this.subtype = subtype;
  }
  
  /**
   * Accessor for likelihood.
   * 
   * @return    The likelihood.
   */
  public Likelihood likelihood() {
    return(likelihood);
  }
  
  /**
   * Setter for likelihood.
   * 
   * @param likelihood    The likelihood.
   */
  public void likelihood(Likelihood likelihood) {
    this.likelihood = likelihood;
  }
  
  /**
   * Accessor for type.
   * 
   * @return    The type.
   */
  public SentenceEntryType type() {
    return(type);
  }
  
  /**
   * Setter for type.
   * 
   * @param type    The type.
   */
  public void type(SentenceEntryType type) {
    this.type = type;
  }
 
  /**
   * Accessor for subtype.
   * 
   * @return    The subtype.
   */
  public SentenceEntrySubType subtype() {
    return(subtype);
  }
  
  /**
   * Setter for subtype.
   * 
   * @param subtype   The subtype.
   */
  public void subtype(SentenceEntrySubType subtype) {
    this.subtype = subtype;
  }
  
  /**
   * Returns a debug string for this <code>SentenceMapEntry</code>.
   * 
   * @return    A debug string.
   */
  public String debugStr() {
    return("Type=" + type + " Likelihood=" + likelihood + " Subtype=" + subtype);
  }
  
}
