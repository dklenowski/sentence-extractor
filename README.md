### To build the project run

    mvn clean install

### To kill the apps send a SIGTERM to the process

Note, debugging is turned on, to turn off edit `main/resources/com/orbious/extractor/log4j.xml`

### To build the classpath

    mvn dependency:build-classpath

### To run the FileParser application

    export CLASSPATH=`cat .maven_classpath`:target/sentence-extractor-<version>.jar
    java -server com.orbious.extractor.app.FileParser -h

## Notes

- Imported from svn (without datesort).
- Used to extract sentences from text documents.
- Has been used successfully on Project Gutenberg texts.
- Uses heuristic algorithms.

## Introduction

Uses heuristic algorithm to extract sentences from text documents.

## Building

There is a an eclipse project file along with a maven pom.xml file (for those not using eclipse):

Building with maven:

        mvn install

Generating javadoc with maven:

        mvn javadoc:javadoc


## Usage

        import com.orbious.extractor.TextParser;

        ...

        String fname;
        TextParser parser;

        fname = "src/test/testdata/17216_short2.txt";
        parser = new TextParser(fname);

        try {
          parser.parse();
        } catch ( FileNotFoundException fnfe ) {
          fnfe.printStackTrace();
        } catch ( IOException ioe ) {
          ioe.printStackTrace();
        }

        parser.genSentences();

        Vector<String> sentences;
        Vector< Vector<String> > sentencesAsVectors;

        sentences = parser.sentencesAsStr();
        sentencesAsVectors = parser.sentences();


## Sentence Extraction Algorithm

Basically consists of the following steps:

- Extract all text from document, removing excessive whitespace (e.g. multiple spaces between words) into a character buffer.
- For each word in the character buffer:
  - If the first character of a word (has a whitespace previously) is uppercase begin the **Start Evaluation Process**.
  - If the character is a sentence end (e.g. '.', '!' etc) run the **End Evaluation Process**.
  - Once the evaluation process is complete and a map of starts/ends is generated, begin sentence extraction.
- Record "likely" starts and "unlikely" starts until a "likely" end or "unlikely" end is reached. When the end is reached use either the "likely" start or "unlikely" start detected earlier as the sentence start.

## Start Evaluation Process

Consists of:

- Run through the start evaluators (e.g. Name, Suspension etc) and if an evaluator returns true, conclude the sentence start is "unlikely", otherwise conclude the sentence start is "likely". Add the start to a map. If the start is likely, also record the previous position as a likely sentence end.

## End Evaluation Process

Consists of:

- Run through the end evaluators (e.g. Acronym, URL etc) and if an evaluator returns true, conclude the sentence end is "unlikely", otherwise conclude the sentence end is "likely". Add the end to a map. If the end is likely, also record the next position as a likely sentence start.


## Evaluators

Evaluators are classes that take a character buffer and a position in the buffer and check whether the position constitutes a sentence start/end (depending on whether the evaluator is evaluating a sentence start or end). For example, there is a default evaluator called "Name" that checks the word extracted from the index against a table of known names. If it finds the name, it returns true and the algorithm records the position as an "unlikely" sentence start.

## Sentence Extraction

The texts available on the internet are difficult to process programmatically because of inconsistency in punctuation (e.g. Project Gutenberg eTexts). It is advisable to examine texts before extraction to see if any pre processing can be done. This can increase accuracy significantly.

An example (see **TextParserTest.test_GenSentencesWiki()**)


        Used to extract sentences from text documents .
        Has been used successfully on Project Gutenberg texts .
        Basically consists of the following steps :
        Extract all text from document , removing excessive whitespace ( e.g. multiple spaces between words ) into a character buffer .
        For each word in the character buffer :
        If the first character of a word ( has a whitespace previously ) is uppercase begin the Start Evaluation Process .
        If the character is a sentence end ( e.g. ' . ',' ! ' etc ) run the End Evaluation Process .
        Once the evaluation process is complete and a map of startsends is generated , begin sentence extraction .
        Record " likely " starts and " unlikely " starts until a " likely " end or " unlikely " end is reached .
        When the end is reached use either the " likely " start or " unlikely " start detected earlier as the sentence start .
        Run through the start evaluators ( e.g. Name , Suspension etc ) and if an evaluator returns true , conclude the sentence start is " unlikely ", otherwise conclude the sentence start is " likely " .
        Add the start to a map .
        If the start is likely , also record the previous position as a likely sentence end .
        Run through the end evaluators ( e.g. Acroynm , URL etc ) and if an evaluator returns true , conclude the sentence end is " unlikely ", otherwise conclude the sentence end is " likely " .
        Add the end to a map .
        If the end is likely , also record the next position as a likely sentence start .
        Evaluators are classes that take a character buffer and a position in the buffer and check whether the position constitutes a sentence startend ( depending on whether the evaluator is evaluating a sentence start or end ) .
        For example , there is a default evaluator called " Name " that checks the word extracted from the index against a table of known names .
        If it finds the name , it returns true and the algorithm records the position as an " unlikely " sentence start .
        The texts available on the internet are difficult to process programmatically because of inconsistency in punctuation ( e.g. Project Gutenberg eTexts ) .
        It is advisable to examine texts before extraction to see if any pre processing can be done .
        This can increase accuracy significantly .

#### The same text split into sentences using the python NLTK Punkt parser


        Used to extract sentences from text documents .
        Has been used successfully on Project Gutenberg texts .
        Basically consists of the following steps :
        Extract all text from document , removing excessive whitespace ( e.g.
        multiple spaces between words ) into a character buffer .
        For each word in the character buffer :
        If the first character of a word ( has a whitespace previously ) is uppercase begin the Start Evaluation Process .
        If the character is a sentence end ( e.g.
        ' .
        ',' !
        ' etc ) run the End Evaluation Process .
        Once the evaluation process is complete and a map of startsends is generated , begin sentence extraction .
        Record " likely " starts and " unlikely " starts until a " likely " end or " unlikely " end is reached .
        When the end is reached use either the " likely " start or " unlikely " start detected earlier as the sentence start .
        Run through the start evaluators ( e.g.
        Name , Suspension etc ) and if an evaluator returns true , conclude the sentence start is " unlikely ", otherwise conclude the sentence start is " likely " .
        Add the start to a map .
        If the start is likely , also record the previous position as a likely sentence end .
        Run through the end evaluators ( e.g.
        Acroynm , URL etc ) and if an evaluator returns true , conclude the sentence end is " unlikely ", otherwise conclude the sentence end is " likely " .
        Add the end to a map .
        If the end is likely , also record the next position as a likely sentence start .
        Evaluators are classes that take a character buffer and a position in the buffer and check whether the position constitutes a sentence startend ( depending on whether the evaluator is evaluating a sentence start or end ) .
        For example , there is a default evaluator called " Name " that checks the word extracted from the index against a table of known names .
        If it finds the name , it returns true and the algorithm records the position as an " unlikely " sentence start .
        The texts available on the internet are difficult to process programmatically because of inconsistency in punctuation ( e.g.
        Project Gutenberg eTexts ) .
        It is advisable to examine texts before extraction to see if any pre processing can be done .
        This can increase accuracy significantly .
