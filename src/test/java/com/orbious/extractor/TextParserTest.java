package com.orbious.extractor;

// $Id: TextParserTest.java 12 2009-12-05 11:40:44Z app $

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;
import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import com.orbious.AllExtractorTests;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class TextParserTest extends TestCase {

  public TextParserTest(String name) {
    super(name);
    AllExtractorTests.initLogger();
  }

  public void test_readWithException() {
    String fname = "asdfasdf";
    TextParser parser = new TextParser(fname);
    
    try {
      parser.parse();
      fail("No FileNotFoundException thrown"); 
    } catch ( FileNotFoundException fnfe ) {   
    } catch (IOException ioe ) {
      fail("Wrong Exception (IOException) thrown.");
    }
  }

  public void test_Read() {
    String fname = "src/test/resources/17216_short.txt";
    TextParser parser = new TextParser(fname);
    
    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }
    
    char[] buffer = parser.buffer();
    String str = String.copyValueOf(buffer);
    
    String expected = "The Project Gutenberg EBook of Punch, or the London Charivari, Volume 1, " +
    "Complete, by Various. This eBook is for the use of anyone anywhere at no " +
    "cost and with almost no restrictions whatsoever. You may copy it, give " +
    "it away or re-use it under the terms of the Project Gutenberg License " +
    "included with this eBook or online at www.gutenberg.net . " +
    "Title: Punch, or the London Charivari, Volume 1, " +
    "Complete Author: Various Release Date: December 4, " +
    "2005 [EBook #17216] Language: English Character set encoding: " +
    "ASCII *** START OF THIS PROJECT GUTENBERG EBOOK PUNCH, VOLUME 1 *** ";

    if ( !expected.equals(str) ) {
      diff_match_patch dmp = new diff_match_patch();
      LinkedList<Diff> d = dmp.diff_main(expected, str);

      System.out.println("Expected=|" + expected + "|\n" +
                         "Actual  =|" + str + "|\n" + 
                         "Diff    =" + d);
      fail();
    }
  }

  public void test_GenSentences17216() {
    String fname = "src/test/resources/17216_short.txt";
    TextParser parser = new TextParser(fname);

    Vector<String> expected = new Vector<String>( 
        Arrays.asList(
            "The Project Gutenberg EBook of Punch , or the London Charivari , Volume 1 , Complete , by Various ." , 
            "This eBook is for the use of anyone anywhere at no cost and with almost no restrictions whatsoever .", 
            "You may copy it , give it away or re-use it under the terms of the Project Gutenberg License included with this eBook or online at www.gutenberg.net ."
        ));
            
    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }

    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr();
    
    assertEquals(expected.size(), sentences.size());   
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        diff_match_patch dmp = new diff_match_patch();
        LinkedList<Diff> d = dmp.diff_main(expected.get(i), sentences.get(i));

        System.out.println("Expected=|" + expected.get(i) + "|\n" +
                           "Actual  =|" + sentences.get(i) + "|\n" + 
                           "Diff    =" + d);
        fail();
      }
    }
  }

  public void test_GenSentences17216_2() {
    String fname = "src/test/resources/17216_short2.txt";
    TextParser parser = new TextParser(fname);

    Vector<String> expected = new Vector<String>( 
        Arrays.asList(
            "MUSIC AND THE DRAMA .",
            "These are amongst the most prominent features of the work ." , 
            "The Musical Notices are written by the gentleman who plays the mouth-organ , assisted by the professors of the drum and cymbals .", 
            "\" Punch \" himself does the Drama .",
            "A Prophet is engaged !",
            "He foretells not only the winners of each race , but also the \" VATES \" and colours of the riders .",
            "THE FACETIAE Are contributed by the members of the following learned bodies :-- THE COURT OF COMMON COUNCIL AND THE ZOOLOGICAL SOCIETY :-- THE TEMPERANCE ASSOCIATION AND THE WATERPROOFING COMPANY :-- THE COLLEGE OF PHYSICIANS AND THE HIGHGATE CEMETERY :-- THE DRAMATIC AUTHORS' AND THE MENDICITY SOCIETIES :-- THE BEEFSTEAK CLUB AND THE ANTI-DRY-ROT COMPANY .",
            "Together with original , humorous , and satirical articles in verse and prose , from all the [ Illustration : FUNNY DOGS WITH COMIC TALES . ]"
        ));
            
    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }

    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr();
  
    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
     if ( !expected.get(i).equals(sentences.get(i)) ) {
        diff_match_patch dmp = new diff_match_patch();
        LinkedList<Diff> d = dmp.diff_main(expected.get(i), sentences.get(i));

        System.out.println("Expected=|" + expected.get(i) + "|\n" +
                           "Actual  =|" + sentences.get(i) + "|\n" + 
                           "Diff    =" + d);
        fail();
      }
    }    
  }

  public void test_GenSentences17216_3() {
    String fname = "src/test/resources/17216_short3.txt";
    TextParser parser = new TextParser(fname);

    Vector<String> expected = new Vector<String>( 
        Arrays.asList(
            "Early in the month of July , 1841 , a small handbill was freely distributed by the newsmen of London , and created considerable amusement and inquiry .",
            "That handbill now stands as the INTRODUCTION to this , the first Volume of Punch , and was employed to announce the advent of a publication which has sustained for nearly twenty years a popularity unsurpassed in the history of periodical literature .",
            "Punch and the Elections were the only matters which occupied the public mind on July 17, 1842 .",
            "The Whigs had been defeated in many places where hitherto they had been the popular party , and it was quite evident that the Meeting of Parliament would terminate their lease of Office .",
            //"[ STREET POLITICS . ]",
            "The House met on the 19th of August , and unanimously elected MR. SHAW LEFEVRE to be Speaker .",
            "The address on the QUEEN'S Speech was moved by MR. MARK PHILLIPS , and seconded by MR. DUNDAS .",
            "MR. J.S. WORTLEY moved an amendment , negativing the confidence of the House in the Ministry , and the debate continued to occupy Parliament for four nights , when the Opposition obtained a majority of 91 against the Ministers .",
            "Amongst those who spoke against the Government , and directly in favour of SIR ROBERT PEEL , was MR. DISRAELI .",
            "In his speech he accused the Whigs of seeking to retain power in opposition to the wishes of the country , and of profaning the name of the QUEEN at their elections , as if she had been a second candidate at some petty poll , and considered that they should blush for the position in which they had placed their Sovereign .",
            "MR. BERNAL , Jun. , retorted upon MR. DISRAELI for inveighing against the Whigs , with whom he had formerly been associated .",
            "SIR ROBERT PEEL , in a speech of great eloquence , condemned the inactivity and feebleness of the existing Government , and promised that , should he displace it , and take office , it should be by walking in the open light , and in the direct paths of the constitution .",
            "He would only accept power upon his conception of public duty , and would resign the moment he was satisfied he was unsupported by the confidence of the people , and not continue to hold place when the voice of the country was against him .",
            "[ HERCULES TEARING THESEUS FROM THE ROCK TO WHICH HE HAD GROWN . ]",
            "LORD JOHN defended the acts of the Ministry , and denied that they had been guilty of harshness to the poor by the New Poor Law , or enemies of the Church by reducing \" the ARCHBISHOP OF CANTERBURY to the miserable pittance of L15,000 a year , cutting down the BISHOP OF LONDON to no more than L10,000 a year , and the BISHOP OF DURHAM to the wretched stipend of L8,000 a year ! \""        
            ));
    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }

    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr();
    
    assertEquals(expected.size(), sentences.size());    
    for ( int i = 0; i < sentences.size(); i++ ) {
     if ( !expected.get(i).equals(sentences.get(i)) ) {
        diff_match_patch dmp = new diff_match_patch();
        LinkedList<Diff> d = dmp.diff_main(expected.get(i), sentences.get(i));

        System.out.println("Expected=|" + expected.get(i) + "|\n" +
                           "Actual  =|" + sentences.get(i) + "|\n" + 
                           "Diff    =" + d);
        fail();
      }
    }    
  }

  public void test_GenSentences10001() {
    String fname = "src/test/resources/10001_short.txt";
    TextParser parser = new TextParser(fname);

    Vector<String> expected = new Vector<String>( 
        Arrays.asList(
            "This piece is ascribed to Seneca by ancient tradition ; it is impossible to prove that it is his , and impossible to prove that it is not .",
            "The matter will probably continue to be decided by every one according to his view of Seneca's character and abilities : in the matters of style and of sentiment much may be said on both sides ."
        ));
    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }

    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr();
   
    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        diff_match_patch dmp = new diff_match_patch();
        LinkedList<Diff> d = dmp.diff_main(expected.get(i), sentences.get(i));

        System.out.println("Expected=|" + expected.get(i) + "|\n" +
                           "Actual  =|" + sentences.get(i) + "|\n" + 
                           "Diff    =" + d);
        fail();
      }
    }    
  }
  
  public void test_GenSentences100012() {
    String fname = "src/test/resources/10001_short2.txt";
    TextParser parser = new TextParser(fname);

    Vector<String> expected = new Vector<String>( 
        Arrays.asList(
            "Pedo brings him before the judgement seat of 14 Aeacus , who was holding court under the Lex Cornelia to try cases of murder and assassination .",
            "Pedo requests the judge to take the prisoner's name , and produces a summons with this charge : Senators killed , 35 ; Roman Knights , 221 ; others as the sands of the sea-shore for multitude .",
            //////////////////////////////////////////////////
            // THIS IS NOT THE BEST, BUT if we add "]" as a sentence end
            // it generates more false positives.
            "[ Sidenote : Il. ix , 385 ] Claudius finds no counsel .",
            "At length out steps P. Petronius , an old chum of his , a finished scholar in the Claudian tongue and claims a remand .",
            "Pedo Pompeius prosecutes with loud outcry .",
            "The counsel for the defence tries to reply ; but Aeacus , who is the soul of justice , will not have it .",
            "Aeacus hears the case against Claudius , refuses to hear the other side and passes sentence against him , quoting the line : \" As he did , so be he done by , this is justice undefiled. \"",
            "[ Footnote : A proverbial line . ]"
        ));
    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }

    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr();
    
    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        diff_match_patch dmp = new diff_match_patch();
        LinkedList<Diff> d = dmp.diff_main(expected.get(i), sentences.get(i));

        System.out.println("Expected=|" + expected.get(i) + "|\n" +
                           "Actual  =|" + sentences.get(i) + "|\n" + 
                           "Diff    =" + d);
        fail();
      }
    }    
  }
  
  public void test_GenSentences11938() {
    String fname = "src/test/resources/11938_short.txt";
    TextParser parser = new TextParser(fname);

    Vector<String> expected = new Vector<String>( 
        Arrays.asList(
            "I have roughly classified the stories : in part 1 are stories of a general character ; part 2 , stories relating to animals ; in part 3 , stories which are scarcely folklore but are anecdotes relating to Santal life ; in Part 4 , stories relating to the dealings of bongas and men .",
            "In part 5 , are some legends and traditions , and a few notes relating to tribal customs .",
            "Part 6 contains illustrations of the belief in witchcraft .",
            "I have had to omit a certain number of stories as unsuited for publication ."
            ));
    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }

    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr();

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        diff_match_patch dmp = new diff_match_patch();
        LinkedList<Diff> d = dmp.diff_main(expected.get(i), sentences.get(i));

        System.out.println("Expected=|" + expected.get(i) + "|\n" +
                           "Actual  =|" + sentences.get(i) + "|\n" + 
                           "Diff    =" + d);
        fail();
      }
    }    
  }
  
  public void test_GenSentencesWiki() {
    String fname = "src/test/resources/wiki.txt";
    TextParser parser = new TextParser(fname);

    Vector<String> expected = new Vector<String>( 
        Arrays.asList(
            "The Project Gutenberg EBook of Punch , or the London Charivari , Volume 1 , Complete , by Various ." , 
            "This eBook is for the use of anyone anywhere at no cost and with almost no restrictions whatsoever .", 
            "You may copy it , give it away or re-use it under the terms of the Project Gutenberg License included with this eBook or online at www.gutenberg.net ."
        ));
            
    try {
      parser.parse();
    } catch ( FileNotFoundException fnfe ) {
      fnfe.printStackTrace();
      fail("FileNotFoundException thrown");
    } catch ( IOException ioe ) {
      ioe.printStackTrace();
      fail("IOException thrown");
    }

    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr();
  
    for ( int i = 0; i < sentences.size(); i++ ) {
      System.out.println(sentences.get(i));
    }
  }
}
