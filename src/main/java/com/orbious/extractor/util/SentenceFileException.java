package com.orbious.extractor.util;

public class SentenceFileException extends Exception {

  private static final long serialVersionUID = 1L;

  public SentenceFileException(String msg) {
    super(msg);
  }

  public SentenceFileException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
