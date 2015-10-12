package org.project.osman.process;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.lang.StringUtils;
import edu.stanford.nlp.ling.HasWord;


/**
 * 
 * @author Mahmoud El-Haj
 * dr.melhaj@gmail.com
 *
 */
public class OsmanReadability {

  	protected static PrintWriter writer;

  	/**
  	 * loadData loads the Arabic dictionaries needed by Stanford tekonizer
  	 * and sentence splitter.
  	 */
	@SuppressWarnings("unused")
	public void loadData(){

		ArabicWordSegmenters.loadLangDictionaries();
		
		 // this is your print stream, store the reference 
		// (this will hide system.err printed by Stanford Tekonizer)
	  	PrintStream err = System.err;

	  	// now make all writes to the System.err stream silent 
	  	System.setErr(new PrintStream(new OutputStream() {
	  	    public void write(int b) {
	  	    }
	  	}));
	  	
	  	

	}

	
// calculate OSMAN Arabic Readability
public double calculateOsman(String text) {

	return 140.835 - (1.015 * wordsPerSentence(text)) - (20.18143403 * (percentComplexWords(text)+syllablesPerWords(text)+faseehPerWords(text)+percentLongWords(text)));
	
}	


	
	// calculate Arabic ARI Readability 
	public double calculateArabicARI(String text) {
		
		return 4.71 *
				( (double)countChars(text) ) / countWords(text)
			+ 0.5 *
				wordsPerSentence(text)
			- 21.43;	
		
	}	
	
	
	// calculate Arabic LIX Readability 
	public double calculateArabicLIX(String text) {
		
		return wordsPerSentence(text) + (countLongWords(text)*100.0) / countWords(text);
		
	}
		
	// calculate Arabic Fog Readability 
	public double calculateArabicFog(String text)
	{
		return (wordsPerSentence(text) + percentComplexWords(text)) * 0.4;
	}


	// calculate Arabic Flesch Ease Readability
	public double calculateArabicFlesch(String text)
	{
		return 206.835 - (1.015 * wordsPerSentence(text)) - (84.6 * syllablesPerWords(text));
	}


	// calculate Arabic Flesch Kincaid (grade) Readability 
	public double calculateArabicKincaid(String text)
	{
		return ((0.39 * wordsPerSentence(text)) + (11.8 * syllablesPerWords(text))) - 15.59;
	}
	
	
	/**
	 * Helper method computing
	 * <p>
	 * ( NumberOfSyllables / NumberOfWords )
	 * 
	 * @return the overall ratio of syllables per word computed from the text statistics.
	 */
	protected double syllablesPerWords(String text)
	{
		return ((double) (countSyllables(text)) / countWords(text));
	}
	
	
	/**
	 * Helper method computing
	 * <p>
	 * ( NumberOfFaseeh / NumberOfWords )
	 * 
	 * @return the overall ratio of Faseeh chars per word computed from the text statistics.
	 */
	protected double faseehPerWords(String text)
	{
		return ((double) (countFaseeh(text))) / countWords(text);
	}
	
	
	/**
	 * Helper method computing 
	 * <p>
	 * ( NumberOfComplexWords / NumberOfWords ) * 100
	 * 
	 * @return percentage of complex words computed from the text statistics.
	 */
	protected double percentComplexWords(String text)
	{
		return (((double) countComplexWords(text)) / countWords(text));
	}
	
	
	/**
	 * Helper method computing 
	 * <p>
	 * ( NumberOfLongWords / NumberOfWords ) * 100
	 * 
	 * @return percentage of long words computed from the text statistics.
	 */
	protected double percentLongWords(String text)
	{
		return (((double) countLongWords(text)) / countWords(text));
	}
	
	/**
	 * Helper method computing 
	 * <p>
	 * NumberOfWords / NumberOfSentences
	 * 
	 * @return words per sentence computed from the text statistics.
	 */
	double wordsPerSentence(String text)
	{
		if((double)countSentences(text)==0){
		
		return ((double) countWords(text)) /1;
		}else{
			
			return ((double) countWords(text)) / countSentences(text);
			}
		}
	
	
	
	//removes diacritics from Arabic text
	protected String removeTashkeel(String text){
		
		String noTashkeel = text.replace("\u064E","")
				.replace("\u064B","").replace("\u064F","")
				.replace("\u064C","").replace("\u0650","")
				.replace("\u064D","").replace("\u0651","")
				.replace("\u0652","").replace("\u0653","")
				.replace("\u0657","").replace("\u0658","");
		
		return noTashkeel;
	}
	
	
	//count number of syllables in a word
	protected int countSyllables(String text){
	
		int s1 = StringUtils.countMatches(text, "\u064E");//fatHa
		int s2 = StringUtils.countMatches(text, "\u064B");//tanween fatH
		int s3 = StringUtils.countMatches(text, "\u064F");//damma
		int s4 = StringUtils.countMatches(text, "\u064C");//tanween dam
		int s5 = StringUtils.countMatches(text, "\u0650");//kasra
		int s6 = StringUtils.countMatches(text, "\u064D");//tanween kasr
		int s7 = StringUtils.countMatches(text, "\u0651");//shadda (this is doubled as it's a double sound)
				
		int syllables = s1+s2+s3+s4+s5+s6+(s7*2);
		
	return syllables;
		
}

	//count number of faseeh indicators in a word
	protected int countFaseeh(String text){
		
		int s8 = StringUtils.countMatches(text, "\u0626");//Æ hamza 3ala nabira
		int s9 = StringUtils.countMatches(text, "\u0621");//Á hamza 3ala satr
		int s10 = StringUtils.countMatches(text, "\u0624");//Ä hamza 3ala waw 
		int s11 = StringUtils.countMatches(text, "\u0648\u0627 ");//æÇ waw wa alef
		int s12 = StringUtils.countMatches(text, "\u0648\u0646 ");//æä waw wa noon (jam3 mothakar)
		int s13 = StringUtils.countMatches(text, "\u0630");//Ð Thal (9th letter in Arabic alphabet)
		int s14 = StringUtils.countMatches(text, "\u0638");//Ù  DHaA (17th letter in Arabic alphabet)
		
		int faseeh = s8+s9+s10+s11+s12+s13+s14;
		
	return faseeh;
		
}
	
	//count number of characters in a word excluding digits and spaces
	protected int countChars(String text){	
		text = removeTashkeel(text);
		return text.replaceAll("\\d"," ").replace(" ","").trim().length();
			
		}
	
	
	//count number of words using Stanford Arabic word Segmenter.
	protected int countWords(String text){	
	List<HasWord> arabicList;
		arabicList = ArabicWordSegmenters.runArabicSegmenter(text);
		int wordCount = arabicList.size();
	
		return wordCount;
		
	}

	//count number of complex words in text, those are words with more than or equal to 7 syllables.
	protected int countComplexWords(String text){
	int complexWords = 0;
	String[] words = text.split(" ");//we don't need an accurate word segmentation here as non-word object does not contain syllables.
	
	for(int i=0;i<words.length;i++){
		int ss1 = StringUtils.countMatches(words[i], "\u064E");//fatHa
		int ss2 = StringUtils.countMatches(words[i], "\u064B");//tanween fatH
		int ss3 = StringUtils.countMatches(words[i], "\u064F");//dhamma
		int ss4 = StringUtils.countMatches(words[i], "\u064C");//tanween dhamm
		int ss5 = StringUtils.countMatches(words[i], "\u0650");//kasra
		int ss6 = StringUtils.countMatches(words[i], "\u064D");//tanween kasr
		int ss7 = StringUtils.countMatches(words[i], "\u0651");//shaddah (this is doubled as it's a double sound)
		
		if(ss1+ss2+ss3+ss4+ss5+ss6+(ss7*2) >= 7){
			complexWords++;
		}
		
	}
		
		return complexWords;
	}
	

	//count number of long words in text, those are words with more than 7 characters.
	protected int countLongWords(String text){	
	List<HasWord> arabicList = null;
	text = removeTashkeel(text);
	arabicList = ArabicWordSegmenters.runArabicSegmenter(text);
	int longWords = 0;
	for (HasWord element : arabicList) {
		if(element.toString().length()>7)
			++longWords;
		
	}
	return longWords;
	}
		

	//add diacritics (Tashkeel) to Arabic text.
	public  String addTashkeel (String text) throws InterruptedException, IOException {
			int fileCounter = 0;
		    String tmpFiles ="tempFiles";

		    new File(tmpFiles).mkdirs();
		    
		    String inputFile=tmpFiles+File.separator+(fileCounter++)+".txt";
			writer = new PrintWriter(inputFile, "UTF-8");
			writer.println(text);
			writer.flush();
			
			String outputFile=tmpFiles+File.separator+(fileCounter++)+".txt";

			Runtime.getRuntime().exec ("cmd /c mishkal-console -f "+inputFile+" > "+outputFile);
			
			
			File outputFILE = new File(outputFile);
			 while(!outputFILE.renameTo(outputFILE)) {
			        // Cannot read from file, windows still working on it.
			        Thread.sleep(10);
			    }

			@SuppressWarnings("resource")
			String content = new Scanner(new File(outputFile)).useDelimiter("\\Z").next();
			if(content.indexOf("cache checked word")>-1)
			content =  content.substring(0, content.indexOf("cache checked word")).trim();
			//force encode output to be UTF-8 format  
			String tashkeelUTF8 = new String(content.getBytes("UTF-8"));
			
	return tashkeelUTF8;
	}
	
	
	//count number of sentences using Stanford tokenizer (Sentence Splitter).
	 public int countSentences(String text){

/*	      Properties props = new Properties();
	      props.put("annotators", "tokenize, ssplit");
	      StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	      // read some text in the text variable

	      // create an empty Annotation just with the given text
	      Annotation document = new Annotation(text);

	      // run all Annotators on this text
	      pipeline.annotate(document);

	      // these are all the sentences in this document
	      // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	      List<CoreMap> sentences = document.get(SentencesAnnotation.class);  
   	      String lines[] = new String[sentences.size()];
	      */    

		 //This is a sentence splitter that work with paragraph and sentence breaks. You can otherwise use
		 //stanford's sentnece splitter by uncommenting the code above. Note you'll need the following import statements:
		 //import java.util.Properties;
		 //import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
		 //import edu.stanford.nlp.pipeline.Annotation;
		 //import edu.stanford.nlp.pipeline.StanfordCoreNLP;
		 //import edu.stanford.nlp.util.CoreMap;
	    String lines[] = text.split("\\r?\\n");
	      
		return lines.length;

}
	 
	 
	
}
