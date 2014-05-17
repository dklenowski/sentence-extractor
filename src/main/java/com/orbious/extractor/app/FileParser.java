package com.orbious.extractor.app;

import gnu.getopt.Getopt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import org.apache.log4j.Logger;
import com.orbious.extractor.AppConfig;
import com.orbious.extractor.ParserException;
import com.orbious.extractor.TextParser;
import com.orbious.util.Loggers;
import com.orbious.util.config.Config;
import com.orbious.util.config.ConfigException;

public class FileParser {
  private static Logger logger = null;

  private static void usage() {
    System.out.println("usage: FileParser [-h] [-p] [-a] -i <input-txt-file> -o <output-txt-file>\n" +
        "    -h                    Print this help message and exit.\n" + 
        "    -p                    Dont preserve punctuation.\n" +
        "    -a                    Dont preserve case.\n" + 
        "    -i <input-txt-file>   The input file (plain text).\n" +
        "    -o <output-txt-file>  The output text file containining one sentence per line.\n");
    System.exit(1);
  }

  public static void main(String[] args) {
    File inputfile = null;
    File outputfile = null;
    boolean preserveCase = true;
    boolean preservePunct = true;
    
    try {
      Config.setDefaults(AppConfig.class);
    } catch ( ConfigException ce ) {
      System.err.println("error setting defaults using AppConfig.class?");
      ce.printStackTrace();
      return;
    }

    logger = Loggers.logger();
    
    Getopt opts = new Getopt("Sentences", args, "hpai:o:");
    int c;
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
        case 'i':
          inputfile = new File(opts.getOptarg());
          break;
        case 'o':
          outputfile = new File(opts.getOptarg());
          break;
      }
    }

    if ( inputfile == null ) {
      System.err.println("you mst specify an input txt path?");
      usage();
    } else if ( outputfile == null ) {
      System.err.println("you must specify an output txt path?");
      usage();
    }
    
    if ( !inputfile.exists() ) {
      System.err.println("the input txt path (" + inputfile + ") does not exist?");
      usage();
    }
    
    Vector<String> sentences = parse(inputfile, preserveCase, preservePunct);
    if ( sentences == null ) System.exit(1);
    
    try {
      write(outputfile, sentences);
    } catch ( IOException ioe ) {
      logger.fatal("error writing setences to " +outputfile, ioe);
    }
  }
  
  private static Vector<String> parse(File file, boolean preserveCase, boolean preservePunct) {
    TextParser parser = new TextParser(file.toString());   
    
    try {
      parser.parse();
    } catch ( ParserException pe ) { 
      logger.fatal("failed to load config for parser?", pe);
      return null;
    } catch ( FileNotFoundException fnfe ) {
      logger.fatal("failed to parse " + file, fnfe);
      return null;
    } catch ( IOException ioe ) {
      logger.fatal("ioerror parsing " + file, ioe);
      return null;
    }
    
    try {
      parser.genSentences();
    } catch ( ParserException pe ) {
      logger.fatal("error parsing " + file, pe);
      return null;
    }
    
    return parser.sentencesAsStr(preserveCase, preservePunct);
  }

  private static void write(File file, Vector<String> sentences) throws IOException {
    BufferedWriter bw =  new BufferedWriter(new FileWriter(file));
    for ( int i = 0; i < sentences.size(); i++ ) 
      bw.write( sentences.get(i) + "\n" );
    
    bw.close();
  }
  

}
