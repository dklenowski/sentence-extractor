package com.orbious.extractor;

import java.util.HashSet;
import java.util.Vector;
import com.orbious.AllExtractorTests;
import com.orbious.extractor.TextParser.TextParserData;
import com.orbious.util.Strings;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class SentenceSplitterTest extends TestCase {

  public SentenceSplitterTest(String name) {
    super(name);
    AllExtractorTests.init();
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
    String expected, actual;

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
    splitter.invalidate();

    SplitterOp op = splitter.split(new TextParserOp(75, 108));

    actual = Strings.cvtVector(op.words());
    expected = "I live to-day by you , a stranger . \"";

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
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

  public void test_Jimmy() {
    SentenceSplitter splitter;
    TextParserData parserData;
    String str;
    String expected, actual;

    str =
      "So that is why he told Peter that he was coming back at\n" +
      "dark. He felt that if Peter was kept a prisoner in there for a while,\n" +
      "all the time worrying about how he was to get out, he would be very slow\n" +
      "to try such a trick again.\n" +
      "\n" +
      "As Jimmy ambled away to look for some beetles, he chuckled and chuckled\n" +
      "and chuckled. \"I guess that by this time Peter wishes he hadn't thought\n" +
      "of that joke on Reddy Fox and myself,\" said he.\n";

    parserData = genParserData(str);

    splitter = new SentenceSplitter(parserData);
    splitter.invalidate();

    SplitterOp op = splitter.split(new TextParserOp(226, 310));

    actual = Strings.cvtVector(op.words());
    expected = "As Jimmy ambled away to look for some beetles , " +
               "he chuckled and chuckled and chuckled . \"";
    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }
  }

  /*
  "After seven generations," was his cryptic remark, "you simply can't keep
  them away. It's bred in the bone...."

  He drove Martha down to the works himself, and took her through the
  various shops, some of which were of such a length that when you stood at
  one end, the other seemed to vanish into distance.
  */

  public void test_Bone() {
    SentenceSplitter splitter;
    TextParserData parserData;
    String str;
    String expected, actual;

    str =
      "\"After seven generations,\" was his cryptic remark, \"you simply can't keep\n" +
      "them away. It's bred in the bone....\n" +
      "\n" +
      "He drove Martha down to the works himself, and took her through the\n" +
      "various shops, some of which were of such a length that when you stood at\n" +
      "one end, the other seemed to vanish into distance.\n";

    parserData = genParserData(str);

    splitter = new SentenceSplitter(parserData);
    splitter.invalidate();

    SplitterOp op;

    op = splitter.split(new TextParserOp(0, 83));
    actual = Strings.cvtVector(op.words());
    expected = "\" After seven generations , \" was his cryptic remark , \" " +
               "you simply can't keep them away .";

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }

    op = splitter.split(new TextParserOp(85, 109));
    actual = Strings.cvtVector(op.words());
    expected = "It's bred in the bone . . . .";

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }
  }

  /*
  Here an interesting circumstance in the history of the preparation of
  these materials has been seized and beautifully appropriated by our
  symbolic science. We learn from the account of the temple, contained in
  the First Book of Kings, that "The house, when it was in building, was
  built of stone, made ready before it was brought thither, so that there
  was neither hammer nor axe, nor any tool of iron, heard in the house while
  it was in building." [57]
  */

  public void test_House() {
    SentenceSplitter splitter;
    TextParserData parserData;
    String str;
    String expected, actual;

    str =
      "We learn from the account of the temple, contained in\n" +
      "the First Book of Kings, that \"The house, when it was in building, was\n" +
      "built of stone, made ready before it was brought thither, so that there\n" +
      "was neither hammer nor axe, nor any tool of iron, heard in the house while\n" +
      "it was in building.\" [57]";

    parserData = genParserData(str);

    splitter = new SentenceSplitter(parserData);
    splitter.invalidate();

    SplitterOp op;

    op = splitter.split(new TextParserOp(0, 291));
    actual = Strings.cvtVector(op.words());
    expected = "We learn from the account of the temple , contained " +
    "in the First Book of Kings , that \" The house , when it was in " +
    "building , was built of stone , made ready before it was brought " +
    "thither , so that there was neither hammer nor axe , nor any tool of " +
    "iron , heard in the house while it was in building . \"";

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }
  }

  /*
  Now, this mode of construction, undoubtedly adopted to avoid confusion and
  discord among so many thousand workmen,[58] has been selected as an
  elementary symbol of concord and harmony -- virtues which are not more
  essential to the preservation and perpetuity of our own society than they
  are to that of every human association.
  */

  public void test_Construction() {
    SentenceSplitter splitter;
    TextParserData parserData;
    String str;
    String expected, actual;

    str =
      "Now, this mode of construction, undoubtedly adopted to avoid confusion and\n" +
      "discord among so many thousand workmen,[58] has been selected as an\n" +
      "elementary symbol of concord and harmony -- virtues which are not more\n" +
      "essential to the preservation and perpetuity of our own society than they\n" +
      "are to that of every human association.";

    parserData = genParserData(str);

    splitter = new SentenceSplitter(parserData);
    splitter.invalidate();

    SplitterOp op;

    op = splitter.split(new TextParserOp(0, 326));
    actual = Strings.cvtVector(op.words());
    expected = "Now , this mode of construction , undoubtedly adopted to avoid " +
    "confusion and discord among so many thousand workmen , [ 58 ] has been " +
    "selected as an elementary symbol of concord and harmony -- virtues " +
    "which are not more essential to the preservation and perpetuity of our " +
    "own society than they are to that of every human association .";

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }
  }

  /*
  "He calls her 'child'..." thought Mary.

  That night Wally was a visitor at the house on the hill :- and when Mary
  saw how subdued he was :-- how chastened he looked--her heart went out to
  him.
  */

  public void test_Child() {
    SentenceSplitter splitter;
    TextParserData parserData;
    String str;
    String expected, actual;

    str =
      "\"He calls her 'child'...\" thought Mary.\n" +
      "\n" +
      "That night Wally was a visitor at the house on the hill :- and when Mary\n" +
      "saw how subdued he was :-- how chastened he looked--her heart went out to\n" +
      "him.\n";

    parserData = genParserData(str);

    splitter = new SentenceSplitter(parserData);
    splitter.invalidate();

    SplitterOp op;

    op = splitter.split(new TextParserOp(0, 38));
    actual = Strings.cvtVector(op.words());
    expected = "\" He calls her ' child ' . . . \" thought Mary .";

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }

    op = splitter.split(new TextParserOp(40, 190));
    actual = Strings.cvtVector(op.words());
    expected = "That night Wally was a visitor at the house on the hill :- " +
    "and when Mary saw how subdued he was :-- how chastened he looked -- " +
    "her heart went out to him .";

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }
  }

  //Illustration: FUNNY DOGS WITH COMIC TALES .

  public void test_Funny() {
    SentenceSplitter splitter;
    TextParserData parserData;
    String str;
    String expected, actual;

    str =
      "Illustration: FUNNY DOGS WITH COMIC TALES .";

    parserData = genParserData(str);

    splitter = new SentenceSplitter(parserData);
    splitter.invalidate();

    SplitterOp op;

    op = splitter.split(new TextParserOp(0, 42));
    actual = Strings.cvtVector(op.words());
    expected = "Illustration : FUNNY DOGS WITH COMIC TALES .";

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }
  }

  // The House met on the 19th of August, and unanimously elected
  // MR. SHAW LEFEVRE to be Speaker.

  public void test_Shaw() {
    SentenceSplitter splitter;
    TextParserData parserData;
    String str;
    String expected, actual;

    str =
      "The House met on the 19th of August, and unanimously elected MR. " +
      "SHAW LEFEVRE to be Speaker.";

    parserData = genParserData(str);

    splitter = new SentenceSplitter(parserData);
    splitter.invalidate();

    SplitterOp op;

    op = splitter.split(new TextParserOp(0, str.length()-1));
    actual = Strings.cvtVector(op.words());
    expected = "The House met on the 19th of August , and unanimously elected MR. " +
    "SHAW LEFEVRE to be Speaker .";

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }
  }

  /*
   LORD JOHN defended the acts of the Ministry, and denied that they had been
   guilty of harshness to the poor by the New Poor Law, or enemies of the
   Church by reducing the ARCHBISHOP OF CANTERBURY to the miserable
   pittance of L15,000 a year, cutting down the BISHOP OF LONDON to no
   more than L10,000 a year, and the BISHOP OF DURHAM to the wretched
   stipend of L8,000 a year!
   */

  public void test_John() {
    SentenceSplitter splitter;
    TextParserData parserData;
    String str;
    String expected, actual;

    str =
      "LORD JOHN defended the acts of the Ministry, and denied that they had been " +
      "guilty of harshness to the poor by the New Poor Law, or enemies of the " +
      "Church by reducing the ARCHBISHOP OF CANTERBURY to the miserable " +
      "pittance of L15,000 a year, cutting down the BISHOP OF LONDON to no " +
      "more than L10,000 a year, and the BISHOP OF DURHAM to the wretched " +
      "stipend of L8,000 a year!";

    parserData = genParserData(str);

    splitter = new SentenceSplitter(parserData);
    splitter.invalidate();

    SplitterOp op;

    op = splitter.split(new TextParserOp(0, str.length()-1));
    actual = Strings.cvtVector(op.words());
    expected = "LORD JOHN defended the acts of the Ministry , and denied that " +
    "they had been guilty of harshness to the poor by the New Poor Law , or " +
    "enemies of the Church by reducing the ARCHBISHOP OF CANTERBURY to the " +
    "miserable pittance of L15,000 a year , cutting down the BISHOP OF LONDON " +
    "to no more than L10,000 a year , and the BISHOP OF DURHAM to the " +
    "wretched stipend of L8,000 a year !";

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }
  }

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
