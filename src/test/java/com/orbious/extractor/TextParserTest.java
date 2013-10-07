package com.orbious.extractor;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;
import com.orbious.AllExtractorTests;
import com.orbious.util.Strings;
import junit.framework.TestCase;

/**
 * @author dave
 * @version 1.0
 * @since 1.0
 */

public class TextParserTest extends TestCase {

  public TextParserTest(String name) {
    super(name);
    AllExtractorTests.init();
  }

  public void test_readWithException() {
    String fname = "asdfasdf";
    TextParser parser = new TextParser(fname);

    try {
      parser.invalidate();
      parser.parse();
      fail("No FileNotFoundException thrown");
    } catch ( FileNotFoundException fnfe ) {
      // we expect this to be thrown ..
    } catch ( ParserException pe ) {
      fail("Wrong Exception (ParseException) thrown.");
    } catch (IOException ioe ) {
      fail("Wrong Exception (IOException) thrown.");
    }
  }

  public void test_Read() throws Exception {
    String fname = "src/test/resources/17216_short.txt";
    TextParser parser = new TextParser(fname);

    parser.parse();
    char[] buffer = parser.parser_data.buffer;
    String actual = String.copyValueOf(buffer);

    String expected = "The Project Gutenberg EBook of Punch, or the London Charivari, Volume 1, " +
    "Complete, by Various. This eBook is for the use of anyone anywhere at no " +
    "cost and with almost no restrictions whatsoever. You may copy it, give " +
    "it away or re-use it under the terms of the Project Gutenberg License " +
    "included with this eBook or online at www.gutenberg.net . " +
    "Title: Punch, or the London Charivari, Volume 1, " +
    "Complete Author: Various Release Date: December 4, " +
    "2005 [EBook #17216] Language: English Character set encoding: " +
    "ASCII *** START OF THIS PROJECT GUTENBERG EBOOK PUNCH, VOLUME 1 *** ";

    if ( !expected.equals(actual) ) {
      System.out.println(Strings.diff(expected, actual));
      fail();
    }
  }

  public void test_GenSentences17216() throws Exception {
    String fname = "src/test/resources/17216_short.txt";
    TextParser parser = new TextParser(fname);
    parser.invalidate();

    Vector<String> expected = new Vector<String>(
        Arrays.asList(
            "The Project Gutenberg EBook of Punch , or the London Charivari , Volume 1 , Complete , by Various ." ,
            "This eBook is for the use of anyone anywhere at no cost and with almost no restrictions whatsoever .",
            "You may copy it , give it away or re-use it under the terms of the Project Gutenberg License included with this eBook or online at www.gutenberg.net .",
            "Punch , or the London Charivari , Volume 1 , Complete Author :",
            "English Character set encoding :"
        ));

    parser.parse();
    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr(true, true);

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        System.out.println(Strings.diff(expected.get(i), sentences.get(i)));
        fail();
      }
      //System.out.println(sentences.get(i));
    }
  }

  public void test_GenSentences17216_noPunct() throws Exception {
    String fname = "src/test/resources/17216_short.txt";
    TextParser parser = new TextParser(fname);
    parser.invalidate();

    Vector<String> expected = new Vector<String>(
        Arrays.asList(
            "The Project Gutenberg EBook of Punch or the London Charivari Volume 1 Complete by Various" ,
            "This eBook is for the use of anyone anywhere at no cost and with almost no restrictions whatsoever",
            "You may copy it give it away or re-use it under the terms of the Project Gutenberg License included with this eBook or online at www.gutenberg.net",
            "Punch or the London Charivari Volume 1 Complete Author",
            "English Character set encoding"
        ));

    parser.parse();
    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr(true, false);

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        System.out.println(Strings.diff(expected.get(i), sentences.get(i)));
        fail();
      }
      //System.out.println(sentences.get(i));
    }
  }

  public void test_GenSentences17216_noPunctNoCase() throws Exception {
    String fname = "src/test/resources/17216_short.txt";
    TextParser parser = new TextParser(fname);
    parser.invalidate();

    Vector<String> expected = new Vector<String>(
        Arrays.asList(
            "the project gutenberg ebook of punch or the london charivari volume 1 complete by various" ,
            "this ebook is for the use of anyone anywhere at no cost and with almost no restrictions whatsoever",
            "you may copy it give it away or re-use it under the terms of the project gutenberg license included with this ebook or online at www.gutenberg.net",
            "punch or the london charivari volume 1 complete author",
            "english character set encoding"
        ));

    parser.parse();
    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr(false, false);

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        System.out.println(Strings.diff(expected.get(i), sentences.get(i)));
        fail();
      }
      //System.out.println(sentences.get(i));
    }
  }

  public void test_GenSentences17216_2() throws Exception {
    String fname = "src/test/resources/17216_short2.txt";
    TextParser parser = new TextParser(fname);
    parser.invalidate();

    Vector<String> expected = new Vector<String>(
        Arrays.asList(
            "MUSIC AND THE DRAMA .",
            "These are amongst the most prominent features of the work ." ,
            "The Musical Notices are written by the gentleman who plays the mouth-organ , assisted by the professors of the drum and cymbals .",
            "\" Punch \" himself does the Drama .",
            "A Prophet is engaged !",
            "He foretells not only the winners of each race , but also the \" VATES \" and colours of the riders .",
            "Are contributed by the members of the following learned bodies :",
            "Together with original , humorous , and satirical articles in verse and prose , from all the [ Illustration : FUNNY DOGS WITH COMIC TALES . ]"
        ));

    parser.parse();
    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr(true, true);

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        System.out.println(Strings.diff(expected.get(i), sentences.get(i)));
        fail();
      }
      //System.out.println(sentences.get(i));
    }
  }

  public void test_GenSentences17216_2_noPunct() throws Exception {
    String fname = "src/test/resources/17216_short2.txt";
    TextParser parser = new TextParser(fname);
    parser.invalidate();

    Vector<String> expected = new Vector<String>(
        Arrays.asList(
            "MUSIC AND THE DRAMA",
            "These are amongst the most prominent features of the work" ,
            "The Musical Notices are written by the gentleman who plays the mouth-organ assisted by the professors of the drum and cymbals",
            "Punch himself does the Drama",
            "A Prophet is engaged",
            "He foretells not only the winners of each race but also the VATES and colours of the riders",
            "Are contributed by the members of the following learned bodies",
            "Together with original humorous and satirical articles in verse and prose from all the Illustration FUNNY DOGS WITH COMIC TALES"
        ));

    parser.parse();
    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr(true, false);

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        System.out.println(Strings.diff(expected.get(i), sentences.get(i)));
        fail();
      }
      //System.out.println(sentences.get(i));
    }
  }

  public void test_GenSentences17216_3()  throws Exception {
    String fname = "src/test/resources/17216_short3.txt";
    TextParser parser = new TextParser(fname);
    parser.invalidate();

    Vector<String> expected = new Vector<String>(
        Arrays.asList(
            "Early in the month of July , 1841 , a small handbill was freely distributed by the newsmen of London , and created considerable amusement and inquiry .",
            "That handbill now stands as the INTRODUCTION to this , the first Volume of Punch , and was employed to announce the advent of a publication which has sustained for nearly twenty years a popularity unsurpassed in the history of periodical literature .",
            "Punch and the Elections were the only matters which occupied the public mind on July 17 , 1842 .",
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

    parser.parse();
    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr(true, true);

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        System.out.println(Strings.diff(expected.get(i), sentences.get(i)));
        fail();
      }
      //System.out.println(sentences.get(i));
    }
  }

  public void test_GenSentences10001() throws Exception {
    String fname = "src/test/resources/10001_short.txt";
    TextParser parser = new TextParser(fname);
    parser.invalidate();

    Vector<String> expected = new Vector<String>(
        Arrays.asList(
            "This piece is ascribed to Seneca by ancient tradition ; it is impossible to prove that it is his , and impossible to prove that it is not .",
            "The matter will probably continue to be decided by every one according to his view of Seneca's character and abilities : in the matters of style and of sentiment much may be said on both sides ."
        ));

    parser.parse();
    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr(true, true);

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        System.out.println(Strings.diff(expected.get(i), sentences.get(i)));
        fail();
      }
      //System.out.println(sentences.get(i));
    }
  }

  public void test_GenSentences100012() throws Exception {
    String fname = "src/test/resources/10001_short2.txt";
    TextParser parser = new TextParser(fname);
    parser.invalidate();

    Vector<String> expected = new Vector<String>(
        Arrays.asList(
            "Pedo brings him before the judgement seat of 14 Aeacus , who was holding court under the Lex Cornelia to try cases of murder and assassination .",
            "Pedo requests the judge to take the prisoner's name , and produces a summons with this charge :",
            // NOT IDEAL
            "Senators killed , 35 ; Roman Knights , 221 ; others as the sands of the sea-shore for multitude .",
            //////////////////////////////////////////////////
            // THIS IS NOT THE BEST, BUT if we add "]" as a sentence end
            // it generates more false positives.
            "[ Sidenote : Il. ix , 385 ] Claudius finds no counsel .",
            "At length out steps P. Petronius , an old chum of his , a finished scholar in the Claudian tongue and claims a remand .",
            "Pedo Pompeius prosecutes with loud outcry .",
            "The counsel for the defence tries to reply ; but Aeacus , who is the soul of justice , will not have it .",
            "Aeacus hears the case against Claudius , refuses to hear the other side and passes sentence against him , quoting the line : \" As he did , so be he done by , this is justice undefiled . \"",
            "[ Footnote : A proverbial line . ]"));

    parser.parse();
    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr(true, true);

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        System.out.println(Strings.diff(expected.get(i), sentences.get(i)));
        fail();
      }
      //System.out.println(i + "=" + sentences.get(i));
    }
  }

  public void test_GenSentences11938() throws Exception {
    String fname = "src/test/resources/11938_short.txt";
    TextParser parser = new TextParser(fname);
    parser.invalidate();

    Vector<String> expected = new Vector<String>(
        Arrays.asList(
            "I have roughly classified the stories : in part 1 are stories of a general character ; part 2 , stories relating to animals ; in part 3 , stories which are scarcely folklore but are anecdotes relating to Santal life ; in Part 4 , stories relating to the dealings of bongas and men .",
            "In part 5 , are some legends and traditions , and a few notes relating to tribal customs .",
            "Part 6 contains illustrations of the belief in witchcraft .",
            "I have had to omit a certain number of stories as unsuited for publication ."
            ));
    
    parser.parse();
    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr(true, true);

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        System.out.println(Strings.diff(expected.get(i), sentences.get(i)));
        fail();
      }
      //System.out.println(sentences.get(i));
    }
  }

  public void test_GenSentences11938_noPunct() throws Exception {
    String fname = "src/test/resources/11938_short.txt";
    TextParser parser = new TextParser(fname);
    parser.invalidate();

    Vector<String> expected = new Vector<String>(
        Arrays.asList(
            "I have roughly classified the stories in part 1 are stories of a general character part 2 stories relating to animals in part 3 stories which are scarcely folklore but are anecdotes relating to Santal life in Part 4 stories relating to the dealings of bongas and men",
            "In part 5 are some legends and traditions and a few notes relating to tribal customs",
            "Part 6 contains illustrations of the belief in witchcraft",
            "I have had to omit a certain number of stories as unsuited for publication"
            ));
    
    parser.parse();
    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr(true, false);

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        System.out.println(Strings.diff(expected.get(i), sentences.get(i)));
        fail();
      }
      //System.out.println(sentences.get(i));
    }
  }

  public void test_GenSentencesWiki() throws Exception {
    String fname = "src/test/resources/wiki.txt";
    TextParser parser = new TextParser(fname);
    parser.invalidate();

    Vector<String> expected = new Vector<String>(
        Arrays.asList(
            "Used to extract sentences from text documents .",
            "Has been used successfully on Project Gutenberg texts .",
            "Basically consists of the following steps :",
            "Extract all text from document , removing excessive whitespace ( e.g. multiple spaces between words ) into a character buffer .",
            "For each word in the character buffer :",
            "If the first character of a word ( has a whitespace previously ) is uppercase begin the Start Evaluation Process .",
            "If the character is a sentence end ( e.g. ' . ',' ! ' etc ) run the End Evaluation Process .",
            "Once the evaluation process is complete and a map of startsends is generated , begin sentence extraction .",
            "Record \" likely \" starts and \" unlikely \" starts until a \" likely \" end or \" unlikely \" end is reached .",
            "When the end is reached use either the \" likely \" start or \" unlikely \" start detected earlier as the sentence start .",
            "Run through the start evaluators ( e.g. Name , Suspension etc ) and if an evaluator returns true , conclude the sentence start is \" unlikely \", otherwise conclude the sentence start is \" likely \" .",
            "Add the start to a map .",
            "If the start is likely , also record the previous position as a likely sentence end .",
            "Run through the end evaluators ( e.g. Acroynm , URL etc ) and if an evaluator returns true , conclude the sentence end is \" unlikely \", otherwise conclude the sentence end is \" likely \" .",
            "Add the end to a map .",
            "If the end is likely , also record the next position as a likely sentence start .",
            "Evaluators are classes that take a character buffer and a position in the buffer and check whether the position constitutes a sentence startend ( depending on whether the evaluator is evaluating a sentence start or end ) .",
            "For example , there is a default evaluator called \" Name \" that checks the word extracted from the index against a table of known names .",
            "If it finds the name , it returns true and the algorithm records the position as an \" unlikely \" sentence start .",
            "The texts available on the internet are difficult to process programmatically because of inconsistency in punctuation ( e.g. Project Gutenberg eTexts ) .",
            "It is advisable to examine texts before extraction to see if any pre processing can be done .",
            "This can increase accuracy significantly ."
        ));

    parser.parse();
    parser.genSentences();
    Vector<String> sentences = parser.sentencesAsStr(true, true);

    assertEquals(expected.size(), sentences.size());
    for ( int i = 0; i < sentences.size(); i++ ) {
      if ( !expected.get(i).equals(sentences.get(i)) ) {
        System.out.println(Strings.diff(expected.get(i), sentences.get(i)));
        fail();
      }
      //System.out.println(sentences.get(i));
    }
  }
}
