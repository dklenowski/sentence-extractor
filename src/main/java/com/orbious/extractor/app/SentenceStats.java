package com.orbious.extractor.app;

import gnu.getopt.Getopt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.orbious.extractor.AppConfig;
import com.orbious.extractor.util.SentenceFile;
import com.orbious.util.Bytes;
import com.orbious.util.Command;
import com.orbious.util.Loggers;
import com.orbious.util.config.Config;
import com.orbious.util.config.ConfigException;
import com.orbious.util.tokyo.StorageException;

public class SentenceStats {

  private static String header = "Name, Number of Sentences, Min Sentence size, Max Sentence Size, Avg Sentence Size\n";

  private static Logger logger = null;
  private static int tokyoSize = 10000;


  private static void usage() {
    System.out.println(
        "Usage: Generator: [-h] [-d <key>] -s <sentence.hdb> [-o <outfile>]\n\n" +
        "    -h                Print this help message and exit.\n" +
        "    -d <key>          Dump sentences stats for key <key>, otherwise statistics for all texts are generated.\n" +
        "    -s <sentence.hdb> A tokyo cabinet file.\n" +
        "    -o <outfile>      Write results to <outfile> in csv format (only valid when -d is not specified)\n");
    System.exit(1);
  }

  public static void main(String[] args) {
    Getopt opts;
    int c;
    String key;
    String sentencename;
    String outname;

    try {
      Config.setDefaults(AppConfig.class);
    } catch ( ConfigException ce ) {
      System.err.println("Error setting defaults using AppConfig.class?");
      ce.printStackTrace();
      return;
    }

    logger = Loggers.logger();

    key = null;
    sentencename = null;
    outname = null;

    opts = new Getopt("SentenceStats", args, "hd:s:o:");
    while ( (c = opts.getopt()) != -1 ) {
      switch ( c ) {
        case 'h':
          usage();
        case 'd':
          key = opts.getOptarg();
          break;
        case 's':
          sentencename = opts.getOptarg();
          break;
        case 'o':
          outname = opts.getOptarg();
          break;
      }
    }

    if ( sentencename == null ) {
      System.err.println("You must specify a sentence path?\n");
      usage();
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

    String result;
    result = stats(new File(sentencename), key);

    if ( result == null ) {
      logger.warn("Failed to extract any results?");
      return;
    }

    if ( outname == null ) {
      System.out.println(header + result);
    } else {
      write(new File(outname), result);
    }
  }

  protected static void write(File file, String str) {
    BufferedWriter bw;

    bw = null;
    try {
      bw = new BufferedWriter(new FileWriter(file));
    } catch ( IOException ioe ) {
      logger.fatal("Failed to open " + file + " for writing?", ioe);
      return;
    }

    try {
      bw.write(header + str);
    } catch ( IOException ioe ) {
      logger.fatal("IOError writing to " + file, ioe);
    }

    try {
      bw.close();
    } catch ( IOException ioe ) {
      logger.fatal("Failed to close outfile " + file, ioe);
    }
  }


  protected static String filestats(Vector<Vector<String>> sentences) {
    int min;
    int max;
    double avg;
    int sz;
    int ct;

    ct = 0;
    avg = 0;
    min = Integer.MAX_VALUE;
    max = Integer.MIN_VALUE;

    for ( int i = 0; i < sentences.size(); i++ ) {
      sz = sentences.get(i).size();
      if ( sz < min ) {
        min = sz;
      }

      if ( sz > max ) {
        max = sz;
      }

      ct++;
      avg += (double)sz;
    }

    avg = avg/ct;

    return ct + ", " + min + ", " + max + ", " + avg;
  }

  private static String stats(File file, String name) {
    String key;
    byte[] bytes;
    SentenceFile sentencefile;
    Vector<Vector<String>> sentences;
    Command cmd;
    StringBuilder results;
    String result;
    String str;

    cmd = Command.instance();
    cmd.canExit(false);

    results = new StringBuilder();

    sentencefile = new SentenceFile(file, tokyoSize, true);
    if ( !sentencefile.exists() ) {
      logger.fatal("Sentence file " + file + "  does not exist?");
      return null;
    }

    try {
      sentencefile.open();
    } catch ( StorageException se ) {
      logger.fatal("Error opening sentence file " + file, se);
      return null;
    }

    if ( name != null ) {
      sentences = sentencefile.get(name);
      if ( sentences == null ) {
        logger.warn("Failed to find sentences for name " + name);
      } else {
        results.append(name + ", " + filestats(sentences) + "\n");
      }
    } else {
      try {
        sentencefile.iterinit();
        while ( (bytes = sentencefile.iternext()) != null ) {
          key = Bytes.bytesToStr(bytes);
          sentences = sentencefile.get(key);

          result = filestats(sentences);
          str = key + ", " + result;

          logger.info(str);
          results.append(str + "\n");

          if ( cmd.shutdown() ) {
            break;
          }
        }
      } catch ( StorageException se ) {
        logger.fatal("Failed to initialize iterator for " + file, se);
      }
    }

    try {
      sentencefile.close();
    } catch ( StorageException ignored ) { }

    cmd.canExit(true);
    return results.toString();
  }

}
