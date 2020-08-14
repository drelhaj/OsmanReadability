package org.project.osman.process;

import org.apache.commons.lang.StringUtils;

public class Syllables {
public int shortSyllables;
public int longSyllables;
public int stressSyllables;

/**
 * Class of the OSMAN Readability package for counting and detecting Arabic Short, Long and Stress Syllables.
 *
 * @author Mahmoud El-Haj
 * dr.melhaj@gmail.com
 * @version 1.0
 */
public Syllables(int shortSyl, int longSyl, int stressSyl){
	
	this.shortSyllables = shortSyl;
	this.longSyllables = longSyl;
	this.stressSyllables = stressSyl;
}

public int getShortSyllables(){
	return shortSyllables;
}

public int getLongSyllables(){
	return longSyllables;
}

public int getStressSyllables(){
	return stressSyllables;
}

	/**
	 * Counts short, long and stress syllables for Arabic text
	 * @param word
	 * @return
	 */
	public static Syllables countAllSyllables(String word){
		//fatha, damma, kasra
		Character[] tashkeel = {'\u064E','\u064F','\u0650'};

		int countLong = 0;
		int countShort = 0;
		int countStress = 0;
		
		for(int x = 0; x< tashkeel.length; x++){
		for(int i=0; i<word.length();i++){//fatHa
		if(word.charAt(i)==tashkeel[x]){
			if(i+1 < word.length() ){
		//to count long syllables we need to check if the character following is an alef, waw or yaaA.
			if(word.charAt(i+1)=='\u0627' || word.charAt(i+1)=='\u0648' || word.charAt(i+1)=='\u064a')
				countLong++;
			else
				countShort++;
		}
			else
			countShort++;
		}
		}
}

		//counts stress syllables, those tanween fatih, tanween damm, tanween kasr and shadda.
countStress = 	StringUtils.countMatches(word, "\u064B") + 
				StringUtils.countMatches(word, "\u064C") + 
				StringUtils.countMatches(word, "\u064D") + 
				StringUtils.countMatches(word, "\u0651");

if(countShort == 0){
	word = word.replace("\u0627","").replace("\u0649", "").replace("?", "").replace(".", "").replace("!", "").replace(",", "").replace(" ", "");
	int afterWordLength = word.length();
	countShort = afterWordLength-2;
}
		
		return new Syllables(countShort, countLong, countStress);
	}
	
	}
	
