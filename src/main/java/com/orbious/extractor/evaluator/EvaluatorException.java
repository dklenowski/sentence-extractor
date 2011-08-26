package com.orbious.extractor.evaluator;

public class EvaluatorException extends Exception {
  private static final long serialVersionUID = 1L;

  public EvaluatorException(String msg) {
    super(msg);
  }

  public EvaluatorException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
