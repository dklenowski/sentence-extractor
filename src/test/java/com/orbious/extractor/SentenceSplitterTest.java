package com.orbious.extractor;

// $Id$

import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

import com.orbious.AllExtractorTests;
import com.orbious.extractor.TextParser.TextParserData;

import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class SentenceSplitterTest extends TestCase {

  public SentenceSplitterTest(String name) {
    super(name);
    AllExtractorTests.initLogger();
  }
 
  /*
  "You have fed me,
  you have protected me, you have carried me in your arms. I live to-day
  by you, a stranger."

  Upon Moffat replying that he was unaware of having rendered him any such
  service, he said, pointing to his two ambassadors: "These are great men;
  Umbate is my right hand.
  */
  public void test_Stranger() {
    SentenceSplitter splitter;
    TextParserData parserData;
    String str;
    Vector<String> wds;
    
    str = 
      "\"You have fed me,\n" +
      "you have protected me, you have carried me in your arms. I live to-day\n" +
      "by you, a stranger.\"\n" + 
      "\n" +
      "Upon Moffat replying that he was unaware of having rendered him any such\n" +
      "service, he said, pointing to his two ambassadors: \"These are great men;\n" +
      "Umbate is my right hand.\n";

    parserData = genParserData(str);
    
    splitter = new SentenceSplitter(parserData);
    SplitterOp op = splitter.split(new TextParserOp(75, 108));
    
    wds = op.words();
    for ( int i = 0; i < wds.size(); i++ ) {
      System.out.println(i + "=" + wds.get(i));
    }
    
  }
  
  
  
  /*
  So that is why he told Peter that he was coming back at
  dark. He felt that if Peter was kept a prisoner in there for a while,
  all the time worrying about how he was to get out, he would be very slow
  to try such a trick again.

  As Jimmy ambled away to look for some beetles, he chuckled and chuckled
  and chuckled. "I guess that by this time Peter wishes he hadn't thought
  of that joke on Reddy Fox and myself," said he.
  */

  /*
  "After seven generations," was his cryptic remark, "you simply can't keep
  them away. It's bred in the bone...."

  He drove Martha down to the works himself, and took her through the
  various shops, some of which were of such a length that when you stood at
  one end, the other seemed to vanish into distance.
  */
  
  /*
  In many parts of the country their sermons are purely
  political, and the altars in the several chapels are the rostra from
  which they declaim on the subject of Roman Catholic grievances, exhort
  to the collection of rent, or denounce their Protestant neighbours in
  a mode perfectly intelligible and effective, but not within the grasp
  of the law. In several towns no Roman Catholic will now deal with a
  Protestant shop-keeper, in consequence of the priest's interdiction,
  and this species of interference, stirring up enmity on one hand and
  feelings of resentment on the other, is mainly conducive to outrage
  and disorder.... The first vacancy on the Roman Catholic bench is to
  be supplied by Dr. England from America, a man of all others most
  decidedly hostile to British interests and the most active in
  fomenting the discord of this country....
  */

  /*
  Here an interesting circumstance in the history of the preparation of
  these materials has been seized and beautifully appropriated by our
  symbolic science. We learn from the account of the temple, contained in
  the First Book of Kings, that "The house, when it was in building, was
  built of stone, made ready before it was brought thither, so that there
  was neither hammer nor axe, nor any tool of iron, heard in the house while
  it was in building." [57]

  Now, this mode of construction, undoubtedly adopted to avoid confusion and
  discord among so many thousand workmen,[58] has been selected as an
  elementary symbol of concord and harmony--virtues which are not more
  essential to the preservation and perpetuity of our own society than they
  are to that of every human association.
  */

  /*
  "I need somebody to mind me," said Burdon, flashing Mary one of his
  violent smiles; and turning to go he said to Helen over his shoulder,
  "Come, child. We're late."

  "He calls her 'child'..." thought Mary.

  That night Wally was a visitor at the house on the hill--and when Mary
  saw how subdued he was--how chastened he looked--her heart went out to
  him.
  */
  
  private TextParserData genParserData(String str) { 
    TextParserData parserData;
    Vector<String> dirty;
    Vector<String> clean;
    HashSet<Integer> lineStarts;
    char[] buffer;
    char[] buf;
    String tmpstr;
    int len;
    int pos;
    
    dirty = cvtTextToVector(str);
    clean = new Vector<String>();
    
    lineStarts = new HashSet<Integer>();
    len = 0;
    for ( int i = 0; i < dirty.size(); i++ ) {
      tmpstr = WhitespaceRemover.remove(dirty, i);
      if ( tmpstr != null ) {
        clean.add(tmpstr);
        lineStarts.add(len);
        len += tmpstr.length();
      }
    }
    
    buffer = new char[len];
    pos = 0;
    for ( int i = 0; i < clean.size(); i++ ) {
      buf = clean.get(i).toCharArray();
      System.arraycopy(buf, 0, buffer, pos, buf.length);
      pos += buf.length;
    }
    
    for ( int i = 0; i < buffer.length; i++ ) {
      System.out.println(i + "=" + buffer[i]);
    }
    
    parserData = AllExtractorTests.initTextParserData(buffer, lineStarts, 
        new SentenceMapEntry[buffer.length],
        new Vector<TextParserOp>(),
        new boolean[buffer.length],
        80);
    return(parserData);
  }
  
  private Vector<String> cvtTextToVector(String str) {
    Vector<String> v;
    String a[];
    
    v = new Vector<String>();
    a = str.split("\n");
    
    for ( int i = 0; i < a.length; i++ ) {
      v.add(a[i]);
    }
    
    return(v);
  }

}
