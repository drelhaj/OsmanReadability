package org.project.osman.process;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.international.arabic.process.ArabicSegmenter;
//import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;

public class ArabicWordSegmenters {

	private static Properties propsAR = new Properties();
	private static ArabicSegmenter segmenterAR;
	
	/**
	 * Load dictionaries needed for the segmentation process (should be run only once)
	 */
	public static void loadLangDictionaries(){
		//English, French, Russian and Spanish will be split using white space delimiter.
		//load Arabic dictionary
		segmenterAR = new ArabicSegmenter(propsAR);
		segmenterAR.loadSegmenter("data/arabic-segmenter-atb+bn+arztrain.ser.gz");


	}

	
/**
 * English, French, Spanish and Russian language word segmenters (splitter) using Stanford PTBTokenizer
 * @param text
 * @return ArrayList of tokenized words
 */
public static ArrayList<String> runEnFrEsRuSegmenter(String text){
	text = cleanText(text);
	ArrayList<String> tokens=new ArrayList<String>();
	PTBTokenizer<Word> tokenizer=PTBTokenizer.newPTBTokenizer(new BufferedReader(new StringReader(text)));
	while (tokenizer.hasNext()) {
		Word nextToken=tokenizer.next();
		String word = nextToken.toString();
		//only include tokens with word length > 1
		if(word.trim().length()>1)
		tokens.add(nextToken.toString());
		}
	//remove empty or null tokens
	tokens.removeAll(Arrays.asList("", null));
	return tokens;
}

/**
 * Arabic language word segmenter (splitter) using Stanford ArabicSegmenter
 * @param text
 * @param props
 * @return List<HasWord> of tokenized words
 */
public static String[] runArabicSegmenter(String text){
		text = cleanText(text);
	String[] words = text.split("\\s+");
/*	List<HasWord> segmentAR = segmenterAR.segment(text);
	segmentAR.removeAll(Arrays.asList("", null));
	segmentAR = removeOneCharItems(segmentAR);*/
	return words;
}




public static String cleanText(String text){
	text = text.replaceAll("\\d","").replaceAll("\\d","").replaceAll("�$","").replaceAll("\\p{Punct}+", "").replaceAll("�,��`","").trim().replaceAll(" +", " ");
	
	return text;
}

/**
 * Remove list entries with length < 2 (one character items)
 * @param segmentAR
 * @return
 */
public static <T> List<T> removeOneCharItems(List<T> segmentAR){
	
	for (int i = segmentAR.size() - 1; i >= 0; i--) {
	    if (segmentAR.get(i).toString().length()<2) {
	    	segmentAR.remove(i);
	    }
	}
	
	return segmentAR;
}


}