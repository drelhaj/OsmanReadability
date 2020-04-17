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
public class WordClass {
    public String word = "";
    public String stem = "";
    public String wordDiacritizedWOCase = "";
    public String wordFullyDiacritized = "";
    public String POS = "";
    public String stemPOS = "";
    public String stemTemplate = "";
    public int position = 0;
    public String genderNumber = ""; // Gender: F/M, Number: S/D/P -- ex.: FS
    public String prefix = "";
    public String suffix = "";
    public String prefixPOS = "";
    public String suffixPOS = "";
    public String lastVerb = "";
    public String truthDiacritic = "";
    public String guessDiacritic = "";

    public WordClass(String w, String s, String wPOS, String sPOS, String sTemplate, int p, String sGN, String pre, String prePOS, String suf, String sufPOS, String lVerb)
    {
        word = w;
        stem = s;
        POS = wPOS;
        stemPOS = sPOS;
        stemTemplate = sTemplate;
        position = p;
        genderNumber = sGN;
        prefix = pre;
        prefixPOS = prePOS;
        suffix = suf;
        suffixPOS = sufPOS;
        lastVerb = lVerb;
    }


}
