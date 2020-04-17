/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qcri.farasa.diacritize;

import com.qcri.farasa.segmenter.ArabicUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import com.qcri.farasa.pos.FarasaPOSTagger;
import com.qcri.farasa.segmenter.Farasa;

/**
 *
 * @author kareemdarwish
 */
public class FarasaMain {
    private static String binDir = "FarasaData/";
    public static Farasa farasaSegmenter = null;
    public static FarasaPOSTagger farasaPOSTagger = null;
    static DiacritizeText dt = null;
    
    public static void main(String[] args) 
            throws FileNotFoundException, IOException, ClassNotFoundException, InterruptedException, Exception { 
        dt = new DiacritizeText(binDir, "", binDir + "all-text.txt.nocase.blm", binDir + "all-text.txt.nocase.dic", farasaSegmenter, farasaPOSTagger);
        farasaSegmenter = new Farasa();
        farasaPOSTagger = new FarasaPOSTagger(farasaSegmenter);
        
        System.out.println("Farasa Data Loaded successfuly");
        String text = "السلام عليكم ورحمة الله وبركاته";
        System.out.println(text);
        System.out.println(diacritizeText(text, dt, farasaSegmenter, farasaPOSTagger));

    }
    
    public static String diacritizeText(String text, DiacritizeText dt,Farasa farasaSegmenter,FarasaPOSTagger farasaPOSTagger) 
            throws FileNotFoundException, IOException, ClassNotFoundException, InterruptedException, Exception {
    	String diacritized = "";
    	
    	System.out.println(text);
    	text = ArabicUtils.removeDiacritics(text).replace("  ", " ");
	    diacritized = dt.diacritize(text);
        
	    return diacritized;
    }
}