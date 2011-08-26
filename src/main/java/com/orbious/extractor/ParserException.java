package com.orbious.extractor;

public class ParserException extends Exception {

  private static final long serialVersionUID = 1L;

  public ParserException(String msg) {
    super(msg);
  }

  public ParserException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
