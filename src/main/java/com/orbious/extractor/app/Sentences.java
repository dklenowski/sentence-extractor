package com.orbious.extractor.app;

import gnu.getopt.Getopt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.orbious.extractor.AppConfig;
import com.orbious.extractor.ParserException;
import com.orbious.extractor.TextParser;
import com.orbious.extractor.util.SentenceFile;
import com.orbious.extractor.util.SentenceFileException;
import com.orbious.util.Command;
import com.orbious.util.Loggers;
import com.orbious.util.Strings;
import com.orbious.util.config.Config;
import com.orbious.util.config.ConfigException;
import com.orbious.util.tokyo.Bytes;

public class Sentences {
  private static File txtpath = null;
  private static File sentencepath = null;
  private static Logger logger = null;

  private static void usage() {
    System.out.println(
        "Usage: Generator: [-h] [-pa] [-c|-k|-d <key>|-t <txtpath>] -s <sentence.hdb>\n" +
        "    -h                Print this help message and exit.\n" +
        "    -p                Dont preserve punctuation.\n" +
        "    -a                Dont preserve case.\n" +
        "    -c                Dump configuration for sentence file.\n" +
        "    -k                Dump keys in <sentence.hdb>\n" +
        "    -d <key>          Dump sentences for key <key>.\n" +
        "    -h                Print this help message and exit.\n" +
        "    -t <txtpath>      Path (file/directory) contain txt files to process.\n" +
        "    -s <sentence.hdb> A tokyo cabinet file.\n");
    Command.instance().canExit(true);
    System.exit(1);
  }

  public static void main(String[] args) {
    Getopt opts;
    int c;
    boolean keys = false;
    boolean cfg = false;
    boolean preserveCase = true;
    boolean preservePunct = true;
    String key = null;

    try {
      Config.setDefaults(AppConfig.class);
    } catch ( ConfigException ce ) {
      System.err.println("Error setting defaults using AppConfig.class?");
      ce.printStackTrace();
      return;
    }

    logger = Loggers.logger();
    opts = new Getopt("Sentences", args, "hpackd:t:s:");

    while ( (c = opts.getopt()) != -1 ) {
      switch ( c ) {
        case 'h':
          usage();
        case 'p':
          preserveCase = false;
          break;
        case 'a':
          preservePunct = false;
          break;
        case 'c':
          cfg = true;
          break;
        case 'k':
          keys = true;
        case 'd':
          key = opts.getOptarg();
          break;
        case 't':
          txtpath = new File(opts.getOptarg());
          break;
        case 's':
          sentencepath = new File(opts.getOptarg());
          break;
      }
    }

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        logger.warn("Shutdown hook called");
        Command.instance().shutdown(true);
        while ( !Command.instance().canExit() ) {
          try {
            sleep(10000);
          } catch ( InterruptedException ignored ) { }
        }
      }
    });;

    if ( cfg ) {
      dumpConfig();
    } else if ( keys || (key != null) ) {
      dumpKeys(key);
    } else if ( txtpath != null ) {
      process(preserveCase, preservePunct);
    } else {
      usage();
    }

    Command.instance().canExit(true);
  }

  private static void dumpConfig() {
    SentenceFile sentencefile;

    sentencefile = new SentenceFile(sentencepath);
    if ( !sentencefile.exists() ) {
      logger.fatal("Sentence file " + sentencepath + "  does not exist?");
      return;
    }

    try {
      sentencefile.init(true);
    } catch ( SentenceFileException sfe ) {
      logger.fatal("Error opening sentence file " + sentencepath, sfe);
      return;
    }

    System.out.println("Configuration for " + sentencefile + ":\n\n" +
        sentencefile.cfgstr());

    try {
      sentencefile.close();
    } catch ( SentenceFileException ignored ) { }
  }

  private static void dumpKeys(String key) {
    SentenceFile sentencefile;

    sentencefile = new SentenceFile(sentencepath);
    if ( !sentencefile.exists() ) {
      logger.fatal("Sentence file " + sentencepath + "  does not exist?");
      return;
    }

    try {
      sentencefile.init(true);
    } catch ( SentenceFileException sfe ) {
      logger.fatal("Error opening sentence file " + sentencepath, sfe);
      return;
    }

    if ( key != null ) {
      Vector<Vector<String>> sentences = sentencefile.get(key);
      for ( int i = 0; i < sentences.size(); i++ ) {
        System.out.println(Strings.cvtVectorToString(sentences.get(i)));
      }
    } else {
      Vector<byte[]> keys = null;
      try {
        keys = sentencefile.names();
      } catch ( SentenceFileException sfe ) {
        logger.fatal("Error extracting keys from " + sentencepath, sfe);
      }
      if ( keys != null ) {
        for ( int i = 0; i < keys.size(); i++ ) {
          System.out.println(Bytes.bytesToStr(keys.get(i)));
        }
      }
    }

    try {
      sentencefile.close();
    } catch ( SentenceFileException ignore ) { }
  }

  private static void process(boolean preserveCase, boolean preservePunct) {
    SentenceFile sentencefile;
    Vector<Vector<String>> sentences;
    Command cmd;
    File f;
    File[] files;
    long start, end;
    int filect;

    if ( !txtpath.exists() ) {
      logger.fatal("Text path " + txtpath + " does not exist?");
      return;
    }

    cmd = Command.instance();
    cmd.canExit(false);

    sentencefile = new SentenceFile(sentencepath);
    try {
      sentencefile.init(false);
    } catch ( SentenceFileException sfe ) {
      logger.fatal("Error opening sentence file " + sentencepath, sfe);
      return;
    }

    if ( txtpath.isFile() ) {
      files = new File[1];
      files[0] = txtpath;
    } else {
        files = txtpath.listFiles();
    }

    start = System.currentTimeMillis();

    filect = 0;
    for ( int i = 0; i < files.length; i++ ) {
      f = files[i];
      if ( !f.getName().matches(".*\\.txt") ) {
        logger.info("Skipping non txt file " + f.getName());
        continue;
      }

      filect++;
      sentences = parse(f, preserveCase, preservePunct);
      if ( sentences != null ) {
        try {
          sentencefile.put(f.getName(), sentences);
        } catch ( SentenceFileException sfe ) {
          logger.fatal("Error writing sentences from " + f.getName() +
              " to " + sentencepath, sfe);
        }
      }

      if ( cmd.shutdown() ) {
        cmd.canExit(true);
        logger.warn("Shutdown hook called, cleaning up");
        break;
      }
    }

    try {
      sentencefile.close();
    } catch ( SentenceFileException sfe ) {
      logger.fatal("Failed to close sentence file " + sentencepath +
          " cleanly?", sfe);
    }

    end = System.currentTimeMillis();
    logger.info("Completed processing of " + filect + " in " + (end-start) +
        " ms.");
    cmd.canExit(true);
  }

  private static Vector<Vector<String>> parse(File f,
      boolean preserveCase, boolean preservePunct)  {
    TextParser parser;

    parser = new TextParser(f.toString());
    parser.invalidate();

    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      logger.fatal("Failed to open text file " + f, fnfe);
      return null;
    } catch ( IOException ioe ) {
      logger.fatal("IOError parsings text file " + f, ioe);
      return null;
    }

    try {
      parser.genSentences();
    } catch ( ParserException pe ) {
      logger.fatal("Failed to generate sentences for " + f, pe);
    }

    return parser.sentences(preserveCase, preservePunct);
  }
}
