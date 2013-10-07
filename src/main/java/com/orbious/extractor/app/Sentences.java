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
import com.orbious.util.tokyo.HDBFile;
import com.orbious.util.tokyo.StorageException;
import com.orbious.util.Bytes;

public class Sentences {
  private static File txtpath = null;
  private static File sentencepath = null;
  private static Logger logger = null;
  private static int tokyoSize = 10000;

  private static void usage() {
    System.out.println(
        "Usage: Generator: [-h] [-pa] [-c|-k|-d <key>|-e <key>|-t <txtpath>]\n" +
        "                  -s <sentence.hdb>\n\n" +
        "    -h                Print this help message and exit.\n" +
        "    -p                Dont preserve punctuation.\n" +
        "    -a                Dont preserve case.\n" +
        "    -c                Dump configuration for sentence file.\n" +
        "    -k                Dump keys in <sentence.hdb>\n" +
        "    -d <key>          Dump sentences for key <key>.\n" +
        "    -e <key>          Extract sentences for key <key> (in tokyodb format).\n" +
        "    -t <txtpath>      Path (file/directory) contain txt files to process.\n" +
        "    -s <sentence.hdb> A tokyo cabinet file.\n");
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
    boolean ashdb = false;

    try {
      Config.setDefaults(AppConfig.class);
    } catch ( ConfigException ce ) {
      System.err.println("Error setting defaults using AppConfig.class?");
      ce.printStackTrace();
      return;
    }

    logger = Loggers.logger();
    opts = new Getopt("Sentences", args, "hpackd:e:t:s:");

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
        case 'e':
          key = opts.getOptarg();
          ashdb = true;
        case 't':
          txtpath = new File(opts.getOptarg());
          break;
        case 's':
          sentencepath = new File(opts.getOptarg());
          break;
      }
    }

    if ( cfg ) {
      dumpConfig();
    } else if ( keys ) {
      dumpKeys();
    } else if ( key != null ) {
      extract(key, ashdb);
    } else if ( txtpath != null ) {
      process(preserveCase, preservePunct);
    } else {
      usage();
    }
  }
  
  private static void dumpConfig() {
    SentenceFile sentencefile;

    sentencefile = new SentenceFile(sentencepath, tokyoSize, true);
    if ( !sentencefile.exists() ) {
      logger.fatal("Sentence file " + sentencepath + "  does not exist?");
      return;
    }

    try {
      sentencefile.open();
    } catch ( StorageException se ) {
      logger.fatal("Error opening sentence file " + sentencepath, se);
      return;
    }

    System.out.println("Configuration for " + sentencefile + ":\n\n" +
        sentencefile.cfgstr());

    try {
      sentencefile.close();
    } catch ( StorageException ignored ) { }
  }

  private static void extract(String key, boolean ashdb) {
    SentenceFile sentencefile;

    if ( (key == null) || (key == "") ) {
      logger.fatal("Invalid key (" + key + ") specified, cannot continue?");
      return;
    }

    sentencefile = new SentenceFile(sentencepath, tokyoSize, true);
    if ( !sentencefile.exists() ) {
      logger.fatal("Sentence file " + sentencepath + "  does not exist?");
      return;
    }

    try {
      sentencefile.open();
    } catch ( StorageException se ) {
      logger.fatal("Error opening sentence file " + sentencepath, se);
      return;
    }

    logger.info("Extracting key " + key + " from " + sentencefile.path());

    Vector<Vector<String>> sentences;
    sentences = sentencefile.get(key);

    if ( sentences == null ) {
      logger.fatal("Failed to extract key " + key + " from " + sentencefile.path());
    } else {
      if ( !ashdb ) {
        for ( int i = 0; i < sentences.size(); i++ ) {
          System.out.println(Strings.cvtVector(sentences.get(i)));
        }
      } else {
        File outfile;
        HDBFile outhdb;

        outfile = new File(sentencefile.path() + "_" + key + ".hdb");
        outhdb = new HDBFile(outfile, 1, false);

        logger.info("Writing key " + key + " to " + outfile.toString());

        try {
          outhdb.open();
        } catch ( StorageException se ) {
          logger.fatal("Failed to open " + outfile + " for writing?", se);
        }

        if ( outhdb.isopen() ) {
          try {
            outhdb.write(key, sentences);
            } catch ( StorageException se ) {
              logger.fatal("Failed to write key " + key + " to " + outfile.toString(), se);
            }

          try {
            outhdb.close();
          } catch ( StorageException se ) {
            logger.fatal("Failed to close hdb file " + outfile.toString() + " cleanly", se);
          }
        }
      }
    }

    try {
      sentencefile.close();
    } catch ( StorageException ignored ) { }
  }

  private static void dumpKeys() {
    SentenceFile sentencefile;

    sentencefile = new SentenceFile(sentencepath, tokyoSize, true);
    if ( !sentencefile.exists() ) {
      logger.fatal("Sentence file " + sentencepath + "  does not exist?");
      return;
    }

    try {
      sentencefile.open();
    } catch ( StorageException se ) {
      logger.fatal("Error opening sentence file " + sentencepath, se);
      return;
    }

    logger.info("Dumping keys for " + sentencefile.path());

    try {
      sentencefile.iterinit();
      byte[] bytes;
      while ( (bytes = sentencefile.iternext()) != null ) {
        System.out.println(Bytes.bytesToStr(bytes));
      }
    } catch ( StorageException se ) {
      logger.fatal("Failed to initialize iterator for " + sentencepath, se);
    }

    try {
      sentencefile.close();
    } catch ( StorageException ignored ) { }
  }
  
  private static void setuphook() {
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
    
    setuphook();

    cmd = Command.instance();
    cmd.canExit(false);

    sentencefile = new SentenceFile(sentencepath, tokyoSize, false);
    try {
      sentencefile.open();
    } catch ( StorageException se ) {
      logger.fatal("Error opening sentence file " + sentencepath, se);
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
    } catch ( StorageException se ) {
      logger.fatal("Failed to close sentence file " + sentencepath +
          " cleanly?", se);
    }

    end = System.currentTimeMillis();
    logger.info("Completed processing of " + filect + " in " + (end-start) +
        " ms.");
    cmd.canExit(true);
  }

  private static Vector<Vector<String>> parse(File f,
      boolean preserveCase, boolean preservePunct)  {
    TextParser parser = new TextParser(f.toString());   
    parser.invalidate();

    try {
      parser.parse();
    } catch ( ParserException pe ) { 
      logger.fatal("failed to load config for parser?", pe);
      return null;
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
