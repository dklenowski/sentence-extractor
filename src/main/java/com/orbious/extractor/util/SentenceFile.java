package com.orbious.extractor.util;

import java.io.File;
import java.util.Vector;
import com.orbious.util.tokyo.HDBFile;
import com.orbious.util.tokyo.StorageException;

public class SentenceFile extends HDBFile {

  public SentenceFile(File sentencefile, int tokyoSize, boolean readOnly) {
    super(sentencefile, tokyoSize, readOnly);
  }

  public SentenceFile(String name, int tokyoSize, boolean readOnly) {
    super(new File(name), tokyoSize, readOnly);
  }

  public void write(String name, Vector<Vector<String>> sentences)
      throws SentenceFileException {
    try {
      super.write(name, sentences);
    } catch ( StorageException se ) {
      throw new SentenceFileException("Error writing value with key " +
          name + " to " + filestore, se);
    }
  }

  @SuppressWarnings("unchecked")
  public Vector<Vector<String>> get(String name) {
    Object obj;

    obj = readObject(name);
    if ( obj == null ) {
      return null;
    }

    Vector<Vector<String>> v = (Vector<Vector<String>>)obj;
    return v;
  }

  public void put(String name, Vector<Vector<String>> sentences)
      throws SentenceFileException {
    try {
      super.write(name, sentences);
    } catch ( StorageException se ) {
      throw new SentenceFileException("Error writing sentences for " + name +
          " to " + filestore, se);
    }
  }
}
