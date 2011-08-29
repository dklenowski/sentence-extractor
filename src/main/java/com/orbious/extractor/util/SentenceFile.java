package com.orbious.extractor.util;

import java.io.File;
import java.util.Vector;
import com.orbious.util.tokyo.HDBFile;
import com.orbious.util.tokyo.WrapperException;

public class SentenceFile extends HDBFile {

  public SentenceFile(File sentencefile) {
    super(sentencefile);
  }

  public SentenceFile(String name) {
    super(name);
  }

  public void write(String name, Vector<Vector<String>> sentences)
      throws SentenceFileException {
    try {
      hdbw.write(name, sentences);
    } catch ( WrapperException we ) {
      throw new SentenceFileException("Error writing value with key " +
          name + " to " + file, we);
    }
  }

  @SuppressWarnings("unchecked")
  public Vector<Vector<String>> get(String name) {
    Object obj;

    obj = hdbw.readObject(name);
    if ( obj == null ) {
      return null;
    }

    Vector<Vector<String>> v = (Vector<Vector<String>>)obj;
    return v;
  }

  public void put(String name, Vector<Vector<String>> sentences)
      throws SentenceFileException {
    try {
      hdbw.write(name, sentences);
    } catch ( WrapperException we ) {
      throw new SentenceFileException("Error writing sentences for " + name +
          " to " + file, we);
    }
  }
}
