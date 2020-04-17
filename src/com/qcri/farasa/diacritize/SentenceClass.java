/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qcri.farasa.diacritize;

import java.util.ArrayList;

/**
 *
 * @author kareemdarwish
 */
public class SentenceClass {
    public ArrayList<WordClass> words = null;

    public SentenceClass() {
        words = new ArrayList<WordClass>();
    }
    
    public void addWord(WordClass c)
    {
        if (words.size() == 0)
            words.add(new WordClass("S", "S", "S", "S", "Y", 0, "Y", "#", "#", "#", "#", "#"));
        words.add(c);
    }
    
    public void clear()
    {
        words = new ArrayList<WordClass>();
    }
}
