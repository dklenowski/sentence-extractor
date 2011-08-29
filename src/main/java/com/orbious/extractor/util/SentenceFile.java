package com.orbious.extractor.util;

import java.io.File;
import java.util.Vector;

import com.orbious.util.config.Config;
import com.orbious.util.config.ConfigException;
import com.orbious.util.tokyo.HDBWrapper;
import com.orbious.util.tokyo.WrapperException;

public class SentenceFile {

  private final File sentencefile;
  private HDBWrapper hdbw;

  public SentenceFile(File sentencefile) {
    this.sentencefile = sentencefile;
  }

  public SentenceFile(String name) {
    this.sentencefile = new File(name);
  }

  public boolean exists() {
    if ( sentencefile == null ) {
      return false;
    }
    return sentencefile.exists();
  }

  public void init(boolean readOnly) throws SentenceFileException {
    if ( hdbw != null ) {
      return;
    }

    hdbw = new HDBWrapper();

    try {
      if ( readOnly ) {
        hdbw.initReader(sentencefile, 10000);
      } else {
        hdbw.initWriter(sentencefile, 1);
      }
    } catch ( WrapperException we ) {
      throw new SentenceFileException("Failed to open sentence file" +
          sentencefile, we);
    }
  }

  public void close() throws SentenceFileException {
    Exception e = null;

    if ( !hdbw.readOnly() ) {
      try {
        hdbw.write(Config.stored_key, Config.xmlstr());
      } catch ( ConfigException ce ) {
        e = ce;
      } catch ( WrapperException we ) {
        e = we;
      }
    }

    try {
      hdbw.close();
    } catch ( WrapperException we ) {
      throw new SentenceFileException("Error closing sentence file " +
          sentencefile, we);
    }

    if ( e != null ) {
      throw new SentenceFileException("Failed to write config to " +
          sentencefile.toString(), e);
    }
  }

  public String cfgstr() {
    return (String)hdbw.readObject(Config.stored_key);
  }

  public void write(String name, Vector<Vector<String>> sentences)
      throws SentenceFileException {
    try {
      hdbw.write(name, sentences);
    } catch ( WrapperException we ) {
      throw new SentenceFileException("Error writing value with key " +
          name + " to " + sentencefile, we);
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
          " to " + sentencefile, we);
    }
  }

  public void iterinit() {
    hdbw.iterinit();
  }

  public byte[] iternext() {
    return hdbw.iternext();
  }

  // do not convert to strings, can use a bit of memory ..
//  public Vector<byte[]> names() throws SentenceFileException {
//    if ( hdbw == null ) {
//      return null;
//    }
//
//    try {
//      return hdbw.keys();
//    } catch ( WrapperException we ) {
//      throw new SentenceFileException("Error retreiving keys from sentence file " +
//          sentencefile, we);
//    }
//  }


}
