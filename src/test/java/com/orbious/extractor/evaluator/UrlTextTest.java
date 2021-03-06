package com.orbious.extractor.evaluator;

import com.orbious.AllExtractorTests;
import com.orbious.extractor.evaluator.Evaluator.EvaluatorType;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class UrlTextTest extends TestCase {

  public UrlTextTest(String name) {
    super(name);
    AllExtractorTests.init();
  }

  public void test_isUrlWord() {
    boolean result;
    UrlText url;

    url = new UrlText(null, EvaluatorType.END);
    url.invalidate();

    result = url.evaluate("www.gutenberg.org".toCharArray(), 0);
    assertEquals(true, result);

    result = url.evaluate("google.com".toCharArray(), 0);
    assertEquals(true, result);
  }

  public void test_isUrlBuffer() {
    boolean result;
    UrlText url;
    String str;
    char[] buf;

    url = new UrlText(null, EvaluatorType.END);
    url.invalidate();

    str = "www.gutenberg.org..";
    buf = str.toCharArray();

    result = url.evaluate(buf, 3);
    assertEquals(true, result);
    result = url.evaluate(buf, 13);
    assertEquals(true, result);
    result = url.evaluate(buf, 17);
    assertEquals(true, result);
    result = url.evaluate(buf, 18);
    assertEquals(false, result);

    str = "google.com";
    buf = str.toCharArray();
    result = url.evaluate(buf, 6);
    assertEquals(true, result);
  }

  public void test_notUrlWord() {
    boolean result;
    UrlText url;

    url = new UrlText(null, EvaluatorType.END);
    url.invalidate();

    result = url.evaluate("gutenberg.".toCharArray(), 0);
    assertEquals(false, result);

    result = url.evaluate("google.".toCharArray(), 0);
    assertEquals(false, result);
  }

  public void test_notUrlBuffer() {
    boolean result;
    UrlText url;
    String str;

    url = new UrlText(null, EvaluatorType.END);
    url.invalidate();

    str = "gutenberg.";
    result = url.evaluate(str.toCharArray(), 9);
    assertEquals(false, result);

    str = "google.";
    result = url.evaluate(str.toCharArray(), 6);
    assertEquals(false, result);
  }
}
