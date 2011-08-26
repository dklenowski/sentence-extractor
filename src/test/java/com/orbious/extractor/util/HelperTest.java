package com.orbious.extractor.util;

import com.orbious.AllExtractorTests;
import com.orbious.util.Strings;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class HelperTest extends TestCase {

  public HelperTest(String name) {
    super(name);
    AllExtractorTests.init();
  }

  public void test_getDebugStringFromCharBuf() {
    String result;

    String str = "Too many people watch too much T.V.";
    char[] buf = str.toCharArray();

    result = Helper.getDebugStringFromCharBuf(buf, 0, 10);
    checkDiff("Too many pe\n|----------", result);

    result = Helper.getDebugStringFromCharBuf(buf, 20, 10);
    checkDiff(" watch too \n-----|-----", result);

    result = Helper.getDebugStringFromCharBuf(buf, 26, 30);
    checkDiff("ople watch too much T.V.\n---------------|--------", result);

    result = Helper.getDebugStringFromCharBuf(buf, 0, 60);
    checkDiff("Too many people watch too much T.V.\n|----------------------------------", result);
  }

  private void checkDiff(String expected, String actual) {
    if (!expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }
  }
}
