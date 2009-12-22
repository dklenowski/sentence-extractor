package com.orbious.util;

import com.orbious.AllTests;
import junit.framework.TestCase;

/**
 * $Id: HelperTest.java 12 2009-12-05 11:40:44Z app $
 * <p>
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class HelperTest extends TestCase {

  public HelperTest(String name) {
    super(name);
    AllTests.initLogger();
  }
  
  public void test_getDebugStringFromCharBuf() {
    String str = "Too many people watch too much T.V.";
    char[] buf = str.toCharArray();
    String result;

    result = Helper.getDebugStringFromCharBuf(buf, 0, 10);
    assertEquals("Too many\n|-------", result);

    result = Helper.getDebugStringFromCharBuf(buf, 20, 10);
    assertEquals(" watch too \n-----|-----", result);

    result = Helper.getDebugStringFromCharBuf(buf, 26, 30);
    assertEquals("ople watch too much T.V.\n---------------|--------", result);

    result = Helper.getDebugStringFromCharBuf(buf, 0, 60);
    assertEquals("Too many people watch too much T.V.\n|----------------------------------", result);
  }
}
