package com.orbious.extractor;

/**
 * $Id$
 * <p>
 * Used by TextParser.
 * 
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class SentenceMapEntry {    
  /**
   * 
   */
  public enum Likelyhood { LIKELY, UNLIKELY }
  
  /**
   * 
   */
  public enum SentenceEntryType { START, END }
  
  /**
   * 
   */
  public enum SentenceEntrySubType { START_FROM_END, END_FROM_START  }
  
  private Likelyhood likelyhood;
  private SentenceEntryType type;
  private SentenceEntrySubType subtype;
  
  public SentenceMapEntry() { }

  public SentenceMapEntry(Likelyhood likelyhood, SentenceEntryType type) {
    this.likelyhood = likelyhood;
    this.type = type;
  }
  
  public SentenceMapEntry(Likelyhood likelyhood, SentenceEntryType type,
      SentenceEntrySubType subtype) {
    this.likelyhood = likelyhood;
    this.type = type;
    this.subtype = subtype;
  }
  
  public Likelyhood likelyhood() {
    return(likelyhood);
  }
  
  public void likelyhood(Likelyhood likelyhood) {
    this.likelyhood = likelyhood;
  }
  
  public SentenceEntryType type() {
    return(type);
  }
  
  public void type(SentenceEntryType type) {
    this.type = type;
  }
 
  public SentenceEntrySubType subtype() {
    return(subtype);
  }
  
  public void subtype(SentenceEntrySubType subtype) {
    this.subtype = subtype;
  }
  
  public String debugStr() {
    return("Type=" + type + " Likelyhood=" + likelyhood + " Subtype=" + subtype);
  }
  
}
