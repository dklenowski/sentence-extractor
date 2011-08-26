package com.orbious.extractor.evaluator;

import com.orbious.AllExtractorTests;
import com.orbious.extractor.evaluator.Evaluator.EvaluatorType;
import junit.framework.TestCase;

public class AbbreviatedNameTest extends TestCase {

  public AbbreviatedNameTest(String name) {
    super(name);
    AllExtractorTests.init();
  }

  public void test_EvaluateLeftToRightMatch() {
    AbbreviatedName name;
    String str;
    char[] buf;
    boolean result;

    name = new AbbreviatedName(null, EvaluatorType.START);
    str = "P. Petro";
    buf = str.toCharArray();

    result = name.evaluateLeftToRight(buf, 0);
    if ( !result ) {
      fail(name.debugStr());
    }

    result = name.evaluateLeftToRight(buf, 1);
    if ( !result ) {
      fail(name.debugStr());
    }

    result = name.evaluateLeftToRight(buf, 3);
    if ( !result ) {
      fail(name.debugStr());
    }
  }

  public void test_EvaluateLeftToRightNotMatch() {
    AbbreviatedName name;
    String str;
    char[] buf;

    name = new AbbreviatedName(null, EvaluatorType.START);
    str = "A Potato";
    buf = str.toCharArray();

    assertFalse(name.evaluateLeftToRight(buf, 0));
    assertFalse(name.evaluateLeftToRight(buf, 3));
  }

  public void test_EvaluateRightToLeftMatch() {
    AbbreviatedName name;
    String str;
    char[] buf;
    boolean result;

    name = new AbbreviatedName(null, EvaluatorType.START);
    str = "Petro P.";
    buf = str.toCharArray();

    result = name.evaluateRightToLeft(buf, 0);
    if ( !result ) {
      fail(name.debugStr());
    }

    result = name.evaluateRightToLeft(buf, 6);
    if ( !result ) {
      fail(name.debugStr());
    }

    result = name.evaluateRightToLeft(buf, 7);
    if ( !result ) {
      fail(name.debugStr());
    }
  }

  public void test_EvaluateLToRMatch() {
    AbbreviatedName name;
    String str;
    char[] buf;

    str = "W.H.D. Rouse";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.START);
    assertTrue(name.evaluate(buf, 0));
    assertTrue(name.evaluate(buf, 2));
    assertTrue(name.evaluate(buf, 4));
    assertTrue(name.evaluate(buf, 7));

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertTrue(name.evaluate(buf, 1));
    assertTrue(name.evaluate(buf, 3));
    assertTrue(name.evaluate(buf, 5));

    str = "M.A. Little";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.START);
    assertTrue(name.evaluate(buf, 0));
    assertTrue(name.evaluate(buf, 2));
    assertTrue(name.evaluate(buf, 5));

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertTrue(name.evaluate(buf, 1));
    assertTrue(name.evaluate(buf, 3));

    str = "B. Thomas";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.START);
    assertTrue(name.evaluate(buf, 0));
    assertTrue(name.evaluate(buf, 3));

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertTrue(name.evaluate(buf, 1));

    str = "C. H. Bompas.";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.START);
    assertTrue(name.evaluate(buf, 0));
    assertTrue(name.evaluate(buf, 3));
    assertTrue(name.evaluate(buf, 6));

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertTrue(name.evaluate(buf, 1));
    assertTrue(name.evaluate(buf, 4));
  }

  public void test_EvaluateLToRNotMatch() {
    AbbreviatedName name;
    String str;
    char[] buf;

    str = "plenty. The";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertFalse(name.evaluate(buf, 6));

    name = new AbbreviatedName(null, EvaluatorType.START);
    assertFalse(name.evaluate(buf, 8));

    str = "riders. THE";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertFalse(name.evaluate(buf, 6));

    name = new AbbreviatedName(null, EvaluatorType.START);
    assertFalse(name.evaluate(buf, 8));

    str = "www.gutenberg.net .";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertFalse(name.evaluate(buf, 3));
    assertFalse(name.evaluate(buf, 13));
    assertFalse(name.evaluate(buf, 18));

    str = "www.gutenberg.net . Title";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertFalse(name.evaluate(buf, 18));
  }

  public void test_EvaluateRToLMatch() {
    AbbreviatedName name;
    String str;
    char[] buf;

    str = "Rouse W.H.D. wore pants";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.START);
    assertTrue(name.evaluate(buf, 0));
    assertTrue(name.evaluate(buf, 6));
    assertTrue(name.evaluate(buf, 8));
    assertTrue(name.evaluate(buf, 10));

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertTrue(name.evaluate(buf, 7));
    assertTrue(name.evaluate(buf, 9));
    assertTrue(name.evaluate(buf, 11));

    str = "Little M.A. was tenable";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.START);
    assertTrue(name.evaluate(buf, 0));
    assertTrue(name.evaluate(buf, 7));
    assertTrue(name.evaluate(buf, 9));

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertTrue(name.evaluate(buf, 8));
    assertTrue(name.evaluate(buf, 10));

    str = "Thomas B. said there were many";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.START);
    assertTrue(name.evaluate(buf, 0));
    assertTrue(name.evaluate(buf, 7));

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertTrue(name.evaluate(buf, 8));
  }


  public void test_EvaluateSentence() {
    AbbreviatedName name;
    String str;
    char[] buf;

    str = "At length out steps P. Petronius, an old chum of";
    buf = str.toCharArray();

    name = new AbbreviatedName(null, EvaluatorType.START);
    assertTrue(name.evaluate(buf, 20));
    assertTrue(name.evaluate(buf, 23));

    name = new AbbreviatedName(null, EvaluatorType.END);
    assertTrue(name.evaluate(buf, 21));
  }
}
