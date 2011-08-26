package com.orbious.extractor;

import com.orbious.AllExtractorTests;
import com.orbious.extractor.Sentence.StartOp;
import com.orbious.extractor.SentenceMapEntry.Likelihood;
import com.orbious.extractor.SentenceMapEntry.SentenceEntryType;
import com.orbious.extractor.TextParser.TextParserData;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class SentenceTest extends TestCase {

  private TextParserData parser_data;

  public SentenceTest(String name) {
    super(name);
    AllExtractorTests.init();
  }

  public void setUp() {
    parser_data = AllExtractorTests.initEmptyTextParserData();
  }

  //
  // isEnd
  //

  public void test_IsEndBasic() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "fantastic. The";
    buf = str.toCharArray();
    sentence = new Sentence(parser_data);
    sentence.invalidate();

    assertTrue(sentence.isEnd(buf, 9).isEnd());
  }

  public void test_isEndWithPunct() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "hope\". Too";
    buf = str.toCharArray();
    sentence = new Sentence(parser_data);
    sentence.invalidate();

    assertNull(sentence.isEnd(buf, 4));
    assertTrue(sentence.isEnd(buf, 5).isEnd());
  }

  public void test_isEndWithNoCapEnd() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "Mr. wiggles";
    buf = str.toCharArray();
    sentence = new Sentence(parser_data);
    sentence.invalidate();

    assertFalse(sentence.isEnd(buf, 2).isEnd());
  }

  //
  // hasLaterPunctuation
  //

  public void test_hasLaterPunctuationBasic() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "fantastic. The";
    buf = str.toCharArray();
    sentence = new Sentence(parser_data);
    sentence.invalidate();

    assertFalse(sentence.hasLaterEnd(buf, 9));
  }

  public void test_hasLaterPunctuationPunct() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "google.com..";
    buf = str.toCharArray();

    sentence = new Sentence(parser_data);
    sentence.invalidate();

    assertTrue(sentence.hasLaterEnd(buf, 10));
    assertFalse(sentence.hasLaterEnd(buf, 11));
  }

  public void test_hasLaterPunctuationPunct2() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "google.com. .";
    buf = str.toCharArray();

    sentence = new Sentence(parser_data);
    sentence.invalidate();

    assertTrue(sentence.hasLaterEnd(buf, 10));
    assertFalse(sentence.hasLaterEnd(buf, 12));
  }

  public void test_hasPunctuationLaterPunctWithLetters() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "google.com. and";
    buf = str.toCharArray();

    sentence = new Sentence(parser_data);
    sentence.invalidate();

    assertFalse(sentence.hasLaterEnd(buf, 10));
  }

  //
  // hasUpper
  //

  public void test_hasUpperBasic() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "fantastic. The";
    buf = str.toCharArray();

    sentence = new Sentence(parser_data);
    sentence.initDefaultEndEvaluators();

    assertEquals(11, sentence.hasUpper(buf, 9));
  }

  public void test_hasUpperWithPunct() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "hope\". Too";
    buf = str.toCharArray();

    sentence = new Sentence(parser_data);
    sentence.invalidate();

    assertEquals(7, sentence.hasUpper(buf, 5));
  }

  public void test_hasUpperWithNoCapEnd() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "Mr. wiggles";
    buf = str.toCharArray();

    sentence = new Sentence(parser_data);
    sentence.invalidate();

    assertEquals(-2, sentence.hasUpper(buf, 2));
  }

  public void test_hasUpperWithWhitespace() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "sample.    The";
    buf = str.toCharArray();

    sentence = new Sentence(parser_data);
    sentence.invalidate();

    assertEquals(11, sentence.hasUpper(buf, 6));
  }

  public void test_hasUpperList() throws Exception {
    String str;
    char[] buf;
    Sentence sentence;

    str = "Process. 2. The";
    buf = str.toCharArray();

    sentence = new Sentence(parser_data);
    sentence.invalidate();

    assertEquals(12, sentence.hasUpper(buf, 7));
  }

  //
  // isStart
  //

  public void test_isStartBasic() throws Exception {
    String str;
    char[] buf;
    SentenceMapEntry[] map;
    Sentence sentence;

    str = "sample. The";
    buf = str.toCharArray();

    map = new SentenceMapEntry[buf.length];
    map[6] = new SentenceMapEntry(Likelihood.LIKELY, SentenceEntryType.END);

    TextParserData parserData = AllExtractorTests.initTextParserData(map);

    sentence = new Sentence(parserData);
    sentence.invalidate();

    assertTrue(sentence.isStart(buf, 8, false).isStart());
  }

  public void test_isStartWithPunct() throws Exception {
    String str;
    char[] buf;
    SentenceMapEntry[] map;
    Sentence sentence;

    str = "Mr. McGoo";
    buf = str.toCharArray();

    map = new SentenceMapEntry[buf.length];
    map[2] = new SentenceMapEntry(Likelihood.UNLIKELY, SentenceEntryType.END);

    TextParserData parserData = AllExtractorTests.initTextParserData(map);

    sentence = new Sentence(parserData);
    sentence.invalidate();

    StartOp op = sentence.isStart(buf, 4, false);
    assertEquals(false, op.isStart());
  }
}
