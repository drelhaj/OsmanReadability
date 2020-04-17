package com.qcri.farasa.diacritize;

import com.qcri.farasa.segmenter.ArabicUtils;
import static com.qcri.farasa.segmenter.ArabicUtils.openFileForReading;
import static com.qcri.farasa.segmenter.ArabicUtils.openFileForWriting;
import com.qcri.farasa.pos.Clitic;
import com.qcri.farasa.pos.FarasaPOSTagger;
import com.qcri.farasa.pos.Sentence;
import com.qcri.farasa.segmenter.Farasa;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kareemdarwish
 */
public class RecoverCaseEnding
{
    Farasa farasaSegmenter = null;
    FarasaPOSTagger farasaPOS = null;
    DiacritizeText dt = null;
    // public static TMap<String, String> wordsWithSingleDiacritizations = new THashMap<String, String>();
    
    // for SVM Training
    public static HashMap<String, Double> hmDiacritic = new HashMap<String, Double>();
    public static HashMap<String, Double> hmWord = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPrefix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmPrefixPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmSuffix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmSuffixPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmTemplate = new HashMap<String, Double>();
    public static HashMap<String, Double> hmStem = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmStemPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmLastLetter = new HashMap<String, Double>();
    public static HashMap<String, Double> hmGenderNumber = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPrevPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenNextPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenWord = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenPrevWord = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenNextWord = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenStem = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenPrevStem = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenNextStem = new HashMap<String, Double>(5000000);
    public static HashMap<String, Double> hmDiacriticGivenStemPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPrevStemPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenNextStemPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenGenderNumber = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPrevGenderNumber = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenNextGenderNumber = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPrefix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenSuffix = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenPrefixPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenSuffixPOS = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenTemplate = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenLastLetter = new HashMap<String, Double>();
    
    public static HashMap<String, Double> hmCurrentPrevPOSAndPrevDiacritic = new HashMap<String, Double>();
    public static HashMap<String, Double> hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic = new HashMap<String, Double>(5000000);
    
    public static HashMap<String, Double> hmCurrent2PrevPOS = new HashMap<String, Double>(100000);
    public static HashMap<String, Double> hmDiacriticGivenCurrent2PrevPOS = new HashMap<String, Double>(5000000);
    
    public static HashMap<String, Double> hmCurrentPrevWord = new HashMap<String, Double>(1000000);
    public static HashMap<String, Double> hmDiacriticGivenCurrentPrevWord = new HashMap<String, Double>(5000000);
    
        public static HashMap<String, Double> hmCurrentPrevNextPOS = new HashMap<String, Double>(100000);
    public static HashMap<String, Double> hmDiacriticGivenCurrentPrevNextPOS = new HashMap<String, Double>(5000000);

    
    private static ArrayList<Double> model = new ArrayList<Double>();
    public static String modelVals = "1:-0.040844474 2:-0.052953929 3:-0.0032482333 4:0.071600951 5:-0.0012433092 6:0.1287304 7:0.045270093 8:-0.0017858743 9:0.043382626 10:-0.039660402 11:-0.041277859 12:-0.017437568 13:-0.024551863 14:-0.024105994 15:-0.023908934 16:-0.016090138 17:-0.020217065 18:-0.017220551 19:-0.022402365 20:-0.025735673 21:-0.025581017 22:0.038178787 23:0.22242694 24:0.11088069 25:0.11440907";
    
    public RecoverCaseEnding(DiacritizeText diacritizer, String dataDir)
    {
        dt = diacritizer;
        farasaSegmenter = diacritizer.farasaSegmenter;
        farasaPOS = diacritizer.farasaPOSTagger;        
        for (String s : modelVals.split(" +")) {
            model.add(Double.parseDouble(s.substring(s.indexOf(":") + 1)));
        }
    }
    
    public SentenceClass createCaseEndingTrainingData(String line) throws ClassNotFoundException, Exception
    {

        SentenceClass sc = new SentenceClass();
        ArrayList<String> segmentedWords = farasaSegmenter.segmentLine(line);
        // ArrayList<String> head = new ArrayList<String>();
        Sentence sentence = farasaPOS.tagLine(segmentedWords);
        int pos = 0;
        /*
            word
            stem 
            stemPOS 
            prefix
            prefixPOS 
            suffix
            suffixPOS
            Suff
            firstLetter
            lastLetter 
            template
            genderNumberTag
            diacritic
        */
        String word = ""; String wordPOS = ""; String stem = ""; String stemPOS = "Y"; String prefix = ""; String prefixPOS = ""; String suffix = ""; String suffixPOS = "";
        String firstLetter = "#"; String lastLetter = "#"; String stemTemplate = "Y"; String lastVerb = "VerbNotSeen"; String genderNumber = "Y";
        for (int i = 0; i < sentence.clitics.size(); i++)
        {
            Clitic clitic = sentence.clitics.get(i);
            if (clitic.position.equals("B") && word.length() > 0 && !word.equals("S") && !word.equals("E"))
            {
                // empty existing aggregators and reset
                if (prefix.trim().length() == 0)
                {
                    prefix = "noPrefixFound";
                    prefixPOS = "Y";
                }
                if (suffix.trim().length() == 0)
                {
                    suffix = "noSuffixFound";
                    suffixPOS = "Y";
                }
                if (stem.trim().length() == 0)
                    stem = "#";
                
                word = word.replace("++", "+");
                stem = stem.replace("++", "+");
                
                if (genderNumber.trim().length() == 0)
                    genderNumber = "#";
                sc.addWord(new WordClass(word, stem, wordPOS, stemPOS, stemTemplate, pos, genderNumber, prefix, prefixPOS, suffix, suffixPOS, lastVerb));
                word = ""; wordPOS = ""; stem = ""; stemPOS = "Y"; prefix = ""; prefixPOS = ""; suffix = ""; suffixPOS = "";
                firstLetter = "#"; lastLetter = "#"; stemTemplate = "Y"; genderNumber = "Y";
                pos++;
            }

            if (clitic.surface.startsWith("+"))
            {
                if (clitic.guessPOS.equals("NSUFF")
                        ||
                        (sentence.clitics.size() > i + 1 && sentence.clitics.get(i+1).guessPOS.equals("NSUFF") && sentence.clitics.get(i+1).position.equals("I"))
                        ) // attach to stem
                {
                    if (stem.length() > 0)
                        stemPOS += "+";
                    stem += clitic.surface;
                    stemPOS += clitic.guessPOS;
                }
                else
                {
                    suffix += clitic.surface;
                    if (suffixPOS.length() > 0)
                        suffixPOS += "+";
                    suffixPOS += clitic.guessPOS;
                }
            }
            else if (clitic.surface.endsWith("+"))
            {
                prefix += clitic.surface;
                if (prefixPOS.length() > 0)
                    prefixPOS += "+";
                prefixPOS += clitic.guessPOS;
            }
            else if (!clitic.surface.equals("S"))
            {
                genderNumber = clitic.genderNumber;
                if (!stem.equals("#"))
                    stem = clitic.surface;
                stemPOS = clitic.guessPOS;
                if (clitic.guessPOS.equals("V"))
                    lastVerb = clitic.surface;
                stemTemplate = clitic.template;
                firstLetter = clitic.surface.substring(0,1);
                lastLetter = clitic.surface.substring(clitic.surface.length() - 1);
            }
            if (!clitic.surface.equals("S") && !clitic.surface.equals("E"))
            {
                word += clitic.surface;
                if (wordPOS.length() > 0)
                    wordPOS += "+";
                wordPOS += clitic.guessPOS;
            }
        }
        if (word.trim().length() > 0 && !word.equals("S") && !word.equals("E"))
        {
            if (prefix.trim().length() == 0)
                {
                    prefix = "#";
                    prefixPOS = "Y";
                }
                if (suffix.trim().length() == 0)
                {
                    suffix = "#";
                    suffixPOS = "Y";
                }
                
                sc.addWord(new WordClass(word, stem, wordPOS, stemPOS, stemTemplate, pos, genderNumber, prefix, prefixPOS, suffix, suffixPOS, lastVerb));
        }
        sc.addWord(new WordClass("E", "E", "E", "E", "Y", 0, "Y", "#", "#", "#", "#", "#"));
        return sc;
    }
    
    public SentenceClass putCaseEnding(String line) throws Exception
    {

        line = line.replace("+", "XplusY");
        line = line.replace(";", "XsemicolonY");
        line = line.replace("-", "XdashY");
        line = line.replace("_", "XunderscoreY");
        SentenceClass sentence = createCaseEndingTrainingData(line);
        ArrayList<String> words = ArabicUtils.tokenizeWithoutProcessing(line);
        // diacritized word in to sentence
        if (sentence.words.size() == words.size() + 2)
        {
            for (int i = 1; i < sentence.words.size() - 1; i++)
            {
                sentence.words.get(i).wordDiacritizedWOCase = words.get(i-1);
            }
            sentence = guessCaseEnding(sentence);
            sentence = putCaseEnding(sentence);
            for (int i = 1; i < sentence.words.size() - 1; i++)
            {
                if (sentence.words.get(i).word.equals("XdashY"))
                {
                    sentence.words.get(i).word = "-";
                    sentence.words.get(i).wordDiacritizedWOCase = "-";
                    sentence.words.get(i).wordFullyDiacritized = "-";
                }
                else if (sentence.words.get(i).word.equals("XsemicolonY"))
                {
                    sentence.words.get(i).word = ";";
                    sentence.words.get(i).wordDiacritizedWOCase = ";";
                    sentence.words.get(i).wordFullyDiacritized = ";";
                }
                else if (sentence.words.get(i).word.equals("XplusY"))
                {
                    sentence.words.get(i).word = "+";
                    sentence.words.get(i).wordDiacritizedWOCase = "+";
                    sentence.words.get(i).wordFullyDiacritized = "+";
                }
                else if (sentence.words.get(i).word.equals("XunderscoreY"))
                {
                    sentence.words.get(i).word = "_";
                    sentence.words.get(i).wordDiacritizedWOCase = "_";
                    sentence.words.get(i).wordFullyDiacritized = "_";
                }
            }
        }
        else
        {
            System.err.println("ERROR:" + line);
        }
        
        return sentence;
    }
        
    private String getWordStem(String word) throws IOException
    {
        String s = farasaSegmenter.segmentLine(word).get(0);
        s = farasaSegmenter.getProperSegmentation(s);
        return (" " + s + " ").split(" +")[1].trim();
    }
    
    public ArrayList<String> createCaseEndingCRFInput(ArrayList<String> lines) throws ClassNotFoundException, Exception
    {
        ArrayList<String> output = new ArrayList<String>();
       
        /*
            word
            stem 
            stemPOS 
            prefix
            prefixPOS 
            suffix
            suffixPOS
            Suff
            firstLetter
            lastLetter 
            template
            genderNumberTag
            diacritic
        */
        String lastSeenVerb = "";
        int lastSeenVerbLoc = -1;
        int currentPosInSentence = 0;
        
        ArrayList<String> wordParts = new ArrayList<String>();
        for (int i = 0; i < lines.size(); i++)
        {
            if (lines.get(i).trim().equals("-"))
            {
                if (wordParts.size() > 0)
                {
                    currentPosInSentence++;
                    // get word
                    String word = "";
                    for (String s : wordParts)
                    {
                        if (word.length() > 0)
                        {
                            word += "+";
                        }
                        word += s.replaceFirst("\\/.*", "");
                    }
                    // get stem
                    // if (word.startsWith("ب"))
                    //    System.err.println();
                    String PrefixStemSuffix = getWordStem(word);
                    if (!PrefixStemSuffix.contains(";"))
                        PrefixStemSuffix = "#;" + PrefixStemSuffix + ";#";
                    if (PrefixStemSuffix.endsWith(";"))
                        PrefixStemSuffix = PrefixStemSuffix + "#";
                    if (PrefixStemSuffix.startsWith(";"))
                        PrefixStemSuffix = "#" + PrefixStemSuffix;
                    if (PrefixStemSuffix.contains(";;"))
                        PrefixStemSuffix = PrefixStemSuffix.replace(";;", ";#;");

                    String stem = PrefixStemSuffix.substring(PrefixStemSuffix.indexOf(";") + 1, 
                            PrefixStemSuffix.lastIndexOf(";")).trim(); // .replaceFirst(".*?;", "").replaceFirst(";.*?", "");
                    String template = "Y"; // template of verbs only
                    // get stemPOS
                    String stemPOS = "";
                    // get genderNumber
                    String genderNumberTag = "O";
                    if (stem.equals("#"))
                    {
                        stemPOS = "Y";
                    }
                    else
                    {
                        for (String s : wordParts)
                        {
                            if (s.startsWith(stem + "/"))
                            {
                                stemPOS = s.replaceFirst(".*\\/", "").trim();
                                if (stemPOS.contains("-"))
                                {
                                    genderNumberTag = stemPOS.substring(stemPOS.indexOf("-") + 1).trim();
                                    stemPOS = stemPOS.substring(0, stemPOS.indexOf("-")).trim();
                                }
                                if (stemPOS.trim().equals("V") || stemPOS.trim().startsWith("V+"))
                                {
                                    template = s.substring(s.indexOf("/") + 1, s.lastIndexOf("/"));
                                }
                            }
                        }
                    }

                    // attach if NSUFF to stem and stemPOS
                    // get prefix and suffix POS
                    String prefixPOS = "";
                    String suffixPOS = "";
                    // get prefix & suffix
                    String prefix = ""; // PrefixStemSuffix.replaceFirst(";.*", "");
                    String suffix = ""; // PrefixStemSuffix.replaceFirst(".*;", "");
                    
                    // add verb feature
                    String lastVerb = "VerbNotSeen";
                    if (stemPOS.equals("V"))
                    {
                        // this is a verb
                        lastSeenVerb = stem;
                        lastSeenVerbLoc = currentPosInSentence;
                    }
                    else if (lastSeenVerbLoc != -1 && currentPosInSentence - lastSeenVerbLoc < 7)
                    {
                        lastVerb = lastSeenVerb;
                    }
                    
                    int positionInPrefixStemSuffix = 0;
                    for (String ss : wordParts)
                    {
                        if (ss.startsWith(stem + "/"))
                        {
                            positionInPrefixStemSuffix = 2;
                        } 
                        else if (ss.contains("NSUFF"))
                        {
                            stem += "+" + ss.replaceFirst("\\/.*", "").trim();
                            stemPOS += "+NSUFF";
                        }
                        else if (positionInPrefixStemSuffix == 0)
                        {
                            prefixPOS += ss.replaceFirst(".*\\/", "").trim() + "+";
                            prefix += ss.substring(0, ss.indexOf("/")).trim() + "+";
                        }
                        else if (positionInPrefixStemSuffix == 2)
                        {
                            suffixPOS += "+" + ss.replaceFirst(".*\\/", "").trim();
                            suffix += "+" + ss.substring(0, ss.indexOf("/")).trim();
                        }
                    }



                    // correct prefix and suffix if empty

                    if (prefix.contains("#") || prefix.isEmpty())
                    {
                        prefixPOS = "Y";
                        prefix = "#";
                    }
                    if (suffix.contains("#") || suffix.isEmpty())
                    {
                        suffixPOS = "Y";
                        suffix = "#";
                    }

                    String Suff = "#"; // if word has NSUFF -- show surface form
                    if (stem.contains("+"))
                        Suff = stem.substring(stem.indexOf("+"));

                    // first and last letter
                    String firstLetter = stem.substring(0, 1);
                    String lastLetter = stem.substring(stem.length() - 1);

                    output.add(word + "\t" + stem + "\t" + stemPOS + "\t" + 
                                prefix + "\t" + prefixPOS + "\t" + 
                                suffix + "\t" + suffixPOS + "\t" + 
                                Suff + "\t" + firstLetter + "\t" + lastLetter + "\t" + 
                                template + "\t" + genderNumberTag + "\t" + lastVerb);
                    wordParts.clear();
                }
            }
            else
            {
                wordParts.add(lines.get(i));
            }
        }
        
        return output;
    }

    private double logWithBase(double x, double base) {
        return Math.log(x) / Math.log(base);
    }
    
    private double getFeatureValueSmoothed(HashMap<String, Double> map, HashMap<String, Double> mapNorm, String word, String tag, double maxVal)
    {
        // get total seen count
        double score = -10d;
        if (map.containsKey(word + "\t" + tag))
        {
            score = map.get(word + "\t" + tag);
            if (mapNorm.get(word) > maxVal)
            {
                return score;
            }
            else
            {
                score = logWithBase(mapNorm.get(word)/maxVal, 10) + score;
                if (score < -10)
                    score = -10;
                return score;
            }
            
        }
        return -10d;
    }
    
    private String getDiacriticFromWord(String word, String suffix)
    {
        String output = word;
        for (int i = suffix.length() - 1; i >= 0; i--)
        {
            int pos = output.lastIndexOf(suffix.substring(i, i+1));
            if (pos == -1)
                System.err.println(word + "\t" + suffix);
            output = output.substring(0, pos);
        }
        String diacritics = "";
        while (output.length() > 0 && output.substring(output.length() - 1).matches("[" + ArabicUtils.buck2utf8("aiou~NKF") + "]+"))
        {
            diacritics = output.substring(output.length() - 1) + diacritics;
            output = output.substring(0, output.length() - 1);
        }
        return diacritics;
    }
    
    public void train(String filename) throws FileNotFoundException, IOException, Exception
    {
        BufferedReader br = openFileForReading(filename);
        BufferedWriter bw = openFileForWriting(filename + ".case");

        String line = "";
        long wordCount = 0;
        while ((line = br.readLine()) != null)
        {
            if (line.trim().length() > 0)
            {
                line = line.replace("+", "plus");
                line = line.replace(";", "semicolon");
                SentenceClass training = createCaseEndingTrainingData(line);
                ArrayList<String> words = ArabicUtils.tokenizeWithoutProcessing(line);
                if (training.words.size() == words.size() + 2)
                {
                    for (int i = 0; i < training.words.size(); i++)
                    {
                        wordCount++;
                        if (String.valueOf(wordCount).endsWith("0000"))
                            System.err.println(wordCount);
                        WordClass tWord = training.words.get(i);
                        if (tWord.POS.equals("S") || tWord.POS.equals("E"))
                        {
                            incrementValGivenKey(hmWord, tWord.word, 1);
                            incrementValGivenKey(hmPOS, tWord.POS, 1);
                            incrementValGivenKey(hmStem, tWord.stem, 1);
                            incrementValGivenKey(hmStemPOS, tWord.stemPOS, 1);
                            incrementValGivenKey(hmTemplate, tWord.stemTemplate, 1);
                            incrementValGivenKey(hmPrefix, tWord.prefix, 1);
                            incrementValGivenKey(hmPrefixPOS, tWord.prefixPOS, 1);
                            incrementValGivenKey(hmSuffix, tWord.suffix, 1);
                            incrementValGivenKey(hmSuffixPOS, tWord.suffixPOS, 1);
                            incrementValGivenKey(hmGenderNumber, tWord.genderNumber, 1);
                            incrementValGivenKey(hmLastLetter, tWord.stem.substring(tWord.stem.length() - 1), 1);
                        }
                        else // if (!tWord.POS.equals("S") && !tWord.POS.equals("E"))
                        {
                            String word = tWord.word;
                            String stem = tWord.stem;
                            String diacritic = "";
                            if (stem.equals("#"))
                                diacritic = "#";
                            else
                            {
                                String suffix = tWord.suffix;
                                if (suffix == "noSuffixFound")
                                    suffix = "";
                                diacritic = ArabicUtils.utf82buck(getDiacriticFromWord(words.get(i-1), suffix.replace("+", "")));
                                if (diacritic.trim().length() == 0)
                                    diacritic = "#";
                                tWord.guessDiacritic = diacritic;
                                tWord.truthDiacritic = diacritic;
                            }
                            incrementValGivenKey(hmDiacritic, diacritic, 1);
                            incrementValGivenKey(hmWord, tWord.word, 1);
                            incrementValGivenKey(hmPOS, tWord.POS, 1);
                            incrementValGivenKey(hmStem, tWord.stem, 1);
                            incrementValGivenKey(hmStemPOS, tWord.stemPOS, 1);
                            incrementValGivenKey(hmTemplate, tWord.stemTemplate, 1);
                            incrementValGivenKey(hmPrefix, tWord.prefix, 1);
                            incrementValGivenKey(hmPrefixPOS, tWord.prefixPOS, 1);
                            incrementValGivenKey(hmSuffix, tWord.suffix, 1);
                            incrementValGivenKey(hmSuffixPOS, tWord.suffixPOS, 1);
                            incrementValGivenKey(hmGenderNumber, tWord.genderNumber, 1);
                            incrementValGivenKey(hmLastLetter, tWord.stem.substring(tWord.stem.length() - 1), 1);

                            String tD = "\t" + diacritic;

                            WordClass tPWord = training.words.get(i-1);
                            WordClass tNWord = training.words.get(i+1);

                            incrementValGivenKey(hmDiacriticGivenPOS, tWord.POS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrevPOS, tPWord.POS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenNextPOS, tNWord.POS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenWord, tWord.word + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrevWord, tPWord.word + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenNextWord, tNWord.word + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenStem, tWord.stem + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrevStem, tPWord.stem + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenNextStem, tNWord.stem + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenStemPOS, tWord.stemPOS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrevStemPOS, tPWord.stemPOS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenNextStemPOS, tNWord.stemPOS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenGenderNumber, tWord.genderNumber + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrevGenderNumber, tPWord.genderNumber  + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenNextGenderNumber, tNWord.genderNumber + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrefix, tWord.prefix + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenSuffix, tWord.suffix + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenPrefixPOS, tWord.prefixPOS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenSuffixPOS, tWord.suffixPOS + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenTemplate, tWord.stemTemplate + tD, 1);
                            incrementValGivenKey(hmDiacriticGivenLastLetter, tWord.stem.substring(tWord.stem.length() - 1) + tD, 1);

                            String tKey = tPWord.guessDiacritic + " " + tPWord.POS+ " " + tWord.POS;
                            incrementValGivenKey(hmCurrentPrevPOSAndPrevDiacritic, tKey, 1d);
                            incrementValGivenKey(hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic, tKey + tD, 1d);
                            
                            tKey = tPWord.word + " " + tWord.word;
                            incrementValGivenKey(hmCurrentPrevWord, tKey, 1d);
                            incrementValGivenKey(hmDiacriticGivenCurrentPrevWord, tKey + tD, 1d);
                            
                            if (i >= 2)
                                tKey = training.words.get(i-2).POS + " " + tPWord.POS + " " + tWord.POS;
                            else
                                tKey = tPWord.POS + " " + tWord.POS;
                            incrementValGivenKey(hmCurrent2PrevPOS, tKey, 1d);
                            incrementValGivenKey(hmDiacriticGivenCurrent2PrevPOS, tKey + tD, 1d);
                        }
                    }
                }
                training.clear();
            }
        }
        // normalize values
        normalizeHashMapVals(hmDiacriticGivenPOS, hmPOS);
        normalizeHashMapVals(hmDiacriticGivenPrevPOS, hmPOS);
        normalizeHashMapVals(hmDiacriticGivenNextPOS, hmPOS);
        normalizeHashMapVals(hmDiacriticGivenWord, hmWord);
        normalizeHashMapVals(hmDiacriticGivenPrevWord, hmWord);
        normalizeHashMapVals(hmDiacriticGivenNextWord, hmWord);
        normalizeHashMapVals(hmDiacriticGivenStem, hmStem);
        normalizeHashMapVals(hmDiacriticGivenPrevStem, hmStem);
        normalizeHashMapVals(hmDiacriticGivenNextStem, hmStem);
        normalizeHashMapVals(hmDiacriticGivenStemPOS, hmStemPOS);
        normalizeHashMapVals(hmDiacriticGivenPrevStemPOS, hmStemPOS);
        normalizeHashMapVals(hmDiacriticGivenNextStemPOS, hmStemPOS);
        normalizeHashMapVals(hmDiacriticGivenGenderNumber, hmGenderNumber);
        normalizeHashMapVals(hmDiacriticGivenPrevGenderNumber, hmGenderNumber);
        normalizeHashMapVals(hmDiacriticGivenNextGenderNumber, hmGenderNumber);
        normalizeHashMapVals(hmDiacriticGivenPrefix, hmPrefix);
        normalizeHashMapVals(hmDiacriticGivenSuffix, hmSuffix);
        normalizeHashMapVals(hmDiacriticGivenPrefixPOS, hmPrefixPOS);
        normalizeHashMapVals(hmDiacriticGivenSuffixPOS, hmSuffixPOS);
        normalizeHashMapVals(hmDiacriticGivenTemplate, hmTemplate);
        normalizeHashMapVals(hmDiacriticGivenLastLetter, hmLastLetter);
        normalizeHashMapVals(hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic, hmCurrentPrevPOSAndPrevDiacritic);
        normalizeHashMapVals(hmDiacriticGivenCurrent2PrevPOS, hmCurrent2PrevPOS);
        normalizeHashMapVals(hmDiacriticGivenCurrentPrevWord, hmCurrentPrevWord);

        // normalize diacritic
    //                for (String s : hmDiacritic.keySet())
    //                    hmDiacritic.put(s, hmDiacritic.get(s)/wordCount);
    }
    
    private double getFeatureValue(HashMap<String, Double> map, String key, double notFound)
    {
        if (map.containsKey(key))
            return map.get(key);
        else
            return notFound;
    }
    
    public SentenceClass guessCaseEnding(SentenceClass sentence)
    {
        for (int i = 0; i < sentence.words.size(); i++)
        {
            WordClass tWord = sentence.words.get(i);
            if (!tWord.POS.equals("S") && !tWord.POS.equals("E"))
            {
                String bestDiacritic = "";
                double bestScore = -10000000;
                for (String d : getPossibleDiacritics(sentence, i))
                {
                    ArrayList<Double> features = getFeatureValues(sentence, i, d);
                    double score = 0;
                    for (int fv = 0; fv < features.size(); fv++) {
                        score += features.get(fv) * model.get(fv);
                    }
                    if (bestScore < score) {
                        bestScore = score;
                        bestDiacritic = d;
                    }
                }
                tWord.guessDiacritic = bestDiacritic;
            }
        }
        return sentence;
    }
    
    public SentenceClass putCaseEnding(SentenceClass sentence)
    {
        for (int i = 0; i < sentence.words.size(); i++)
        {
            WordClass tWord = sentence.words.get(i);
            if (!tWord.POS.equals("S") && !tWord.POS.equals("E"))
            {
                if (tWord.guessDiacritic.equals("#"))
                {
                    tWord.wordFullyDiacritized = tWord.wordDiacritizedWOCase;
                }
                else
                {
                    String head = tWord.wordDiacritizedWOCase;
                    String tail = "";
                    String suffix = tWord.suffix.replace("noSuffixFound", "").replace("+", "");
                    for (int j = suffix.length() - 1; j >= 0; j--)
                    {
                        int pos = head.lastIndexOf(suffix.substring(j, j+1));
                        if (pos == -1)
                            System.err.println(tWord.wordDiacritizedWOCase + "\t" + suffix);
                        tail = head.substring(pos) + tail;
                        head = head.substring(0, pos);
                    }
                    tWord.wordFullyDiacritized = head.replaceFirst("[" + ArabicUtils.buck2utf8("aiou~NFK") + "]+$", "") + ArabicUtils.buck2utf8(tWord.guessDiacritic) + tail;
                }
            }
        }
        return sentence;
    }
    
    public void generateSVM(String filename) throws FileNotFoundException, IOException, InterruptedException, Exception {
        BufferedReader br = openFileForReading(filename);
        BufferedWriter bw = openFileForWriting(filename + ".svm.case");

        String line = "";
        long wordCount = 0;
        long qid = 0;
        while ((line = br.readLine()) != null)
        {
            if (line.trim().length() > 0)
            {
                line = line.replace("+", "plus");
                line = line.replace(";", "semicolon");
                SentenceClass training = createCaseEndingTrainingData(line);
                ArrayList<String> words = ArabicUtils.tokenizeWithoutProcessing(line);
                if (training.words.size() == words.size() + 2)
                {
                    for (int i = 0; i < training.words.size(); i++)
                    {
                        
                        wordCount++;
                        if (String.valueOf(wordCount).endsWith("0000"))
                            System.err.println(wordCount);
                        WordClass tWord = training.words.get(i);
                        if (!tWord.POS.equals("S") && !tWord.POS.equals("E"))
                        {
                            qid++;
                            String word = tWord.word;
                            String stem = tWord.stem;
                            String diacritic = "";
                            if (stem.equals("#"))
                                diacritic = "#";
                            else
                            {
                                String suffix = tWord.suffix;
                                if (suffix == "noSuffixFound")
                                    suffix = "";
                                diacritic = ArabicUtils.utf82buck(getDiacriticFromWord(words.get(i-1), suffix.replace("+", "")));
                                if (diacritic.trim().length() == 0)
                                    diacritic = "#";
                            }
                            tWord.truthDiacritic = diacritic;
                            tWord.guessDiacritic = diacritic;
                            WordClass tPWord = training.words.get(i-1);
                            WordClass tNWord = training.words.get(i+1);
                            ArrayList<String> possibleDiacritics = getPossibleDiacritics(training, i);
                            if (!possibleDiacritics.contains(diacritic))
                                possibleDiacritics.add(diacritic);
                            for (String d : possibleDiacritics)
                            {
                                // if (hmDiacritic.get(d) > 30)
                                {
                                    ArrayList<Double> features = getFeatureValues(training, i, d);// new ArrayList<Double>();
                                    int rank = 1;
                                    if (d.equals(tWord.truthDiacritic))
                                        rank = 2;
                                    bw.write(rank + " qid:" + qid);
                                    for (int k = 1; k <= features.size(); k++)
                                    {
                                        bw.write(" " + k + ":" + features.get(k - 1));
                                    }
                                    bw.write("\n");
                                }
                            }
                        }
                    }
                }
                training.clear();
            }
        }
        bw.close();
    }
    
    private ArrayList<Double> getFeatureValues(SentenceClass sentence, int j, String tag)
    {
        ArrayList<Double> features = new ArrayList<Double>();
        WordClass tWord = sentence.words.get(j);
        WordClass tPWord = sentence.words.get(j - 1);
        WordClass tNWord = sentence.words.get(j + 1);
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPOS, hmPOS, tWord.POS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrevPOS, hmPOS, tPWord.POS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenNextPOS, hmPOS, tNWord.POS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenWord, hmWord, tWord.word, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrevWord, hmWord, tPWord.word, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenNextWord, hmWord, tNWord.word, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenStem, hmStem, tWord.stem, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrevStem, hmStem, tPWord.stem, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenNextStem, hmStem, tNWord.stem, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenStemPOS, hmStemPOS, tWord.stemPOS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrevStemPOS, hmStemPOS, tPWord.stemPOS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenNextStemPOS, hmStemPOS, tNWord.stemPOS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenGenderNumber, hmGenderNumber, tWord.genderNumber, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrevGenderNumber, hmGenderNumber, tPWord.genderNumber, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenNextGenderNumber, hmGenderNumber, tNWord.genderNumber, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrefix, hmPrefix, tWord.prefix, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenSuffix, hmSuffix, tWord.suffix, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenPrefixPOS, hmPrefixPOS, tWord.prefixPOS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenSuffixPOS, hmSuffixPOS, tWord.suffixPOS, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenTemplate, hmTemplate, tWord.stemTemplate, tag, 1000));
        features.add(getFeatureValueSmoothed(hmDiacriticGivenLastLetter, hmLastLetter, tWord.stem.substring(tWord.stem.length() - 1), tag, 1000));
        String tKey = tPWord.guessDiacritic + " " + tPWord.POS+ " " + tWord.POS;
        features.add(getFeatureValueSmoothed(hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic, hmCurrentPrevPOSAndPrevDiacritic, tKey, tag, 1000));
        
        tKey = tPWord.word + " " + tWord.word;
        features.add(getFeatureValueSmoothed(hmDiacriticGivenCurrentPrevWord, hmCurrentPrevWord, tKey, tag, 1000));
        
        tKey = tPWord.POS + " " + tWord.POS + " " + tNWord.POS;
        features.add(getFeatureValueSmoothed(hmDiacriticGivenCurrentPrevNextPOS, hmCurrentPrevNextPOS, tKey, tag, 1000));
        
        if (j >= 2) tKey = sentence.words.get(j-2).POS + " " + tPWord.POS + " " + tWord.POS;
        else tKey = tPWord.POS + " " + tWord.POS;
        features.add(getFeatureValueSmoothed(hmDiacriticGivenCurrent2PrevPOS, hmCurrent2PrevPOS, tKey, tag, 1000));
        return features;
    }

    public ArrayList<String> getPossibleDiacritics(SentenceClass sentence, int j)
    {
        ArrayList<String> possibleDiacritics = new ArrayList<String>();
        
        // add possible diacritics based on word and stem
        if (hmWord.containsKey(sentence.words.get(j).word) && hmWord.get(sentence.words.get(j).word) > 1000) // if a word appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (hmDiacriticGivenWord.containsKey(sentence.words.get(j).word + "\t" + d) && hmDiacriticGivenWord.get(sentence.words.get(j).word + "\t" + d) > -5.3)
                {
                    possibleDiacritics.add(d);
                }
            }
        }
        else if (hmStem.containsKey(sentence.words.get(j).stem) && hmStem.get(sentence.words.get(j).stem) > 1000) // if a stem appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (hmDiacriticGivenStem.containsKey(sentence.words.get(j).stem + "\t" + d) && hmDiacriticGivenStem.get(sentence.words.get(j).stem + "\t" + d) > -5.3)
                {
                    possibleDiacritics.add(d);
                }
            }
        }
        else // (possibleDiacritics.size() == 0)
        {
            if (sentence.words.get(j).stemPOS.equals("ADJ") || sentence.words.get(j).stemPOS.equals("NOUN"))
            {
                for (String dd : ("a;i;u;~a;~i;~u;N;K;F;~N;~K;~F;#").split(";"))
                    possibleDiacritics.add(dd);
            }
            else if (sentence.words.get(j).stemPOS.equals("V"))
            {
                for (String dd : ("a;o;u;#").split(";"))
                    possibleDiacritics.add(dd);
            }
            else
            {
                for (String d : hmDiacritic.keySet())
                {
                    if (hmDiacritic.get(d) > 100)
                        possibleDiacritics.add(d);
                }
            }
        }
        // remove impossible diacritics based on POS and stemPOS
        if (hmPOS.containsKey(sentence.words.get(j).POS) && hmPOS.get(sentence.words.get(j).POS) > 50) // if a POS appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (possibleDiacritics.contains(d) && (!hmDiacriticGivenPOS.containsKey(sentence.words.get(j).POS + "\t" + d))) // || hmDiacriticGivenPOS.get(sentence.words.get(j).POS + "\t" + d) <= -5.3))
                {
                    possibleDiacritics.remove(d);
                }
            }
        }
        if (hmStemPOS.containsKey(sentence.words.get(j).stemPOS) && hmStemPOS.get(sentence.words.get(j).stemPOS) > 50) // if a stemPOS appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (possibleDiacritics.contains(d) && (!hmDiacriticGivenStemPOS.containsKey(sentence.words.get(j).stemPOS + "\t" + d))) // || hmDiacriticGivenStemPOS.get(sentence.words.get(j).stemPOS + "\t" + d) <= -5.3))
                {
                    possibleDiacritics.remove(d);
                }
            }
        }
        // remove impossible diacritics based on prefix and last letter
        String lastletter = sentence.words.get(j).word.substring(sentence.words.get(j).word.length() - 1);
        if (hmLastLetter.containsKey(lastletter) && hmLastLetter.get(lastletter) > 50) // if a POS appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (possibleDiacritics.contains(d) && (!hmDiacriticGivenLastLetter.containsKey(lastletter + "\t" + d) || hmDiacriticGivenLastLetter.get(lastletter + "\t" + d) <= -5.3))
                {
                    possibleDiacritics.remove(d);
                }
            }
        }
        if (!sentence.words.get(j).prefix.equals("noPrefixFound") && hmPrefix.containsKey(sentence.words.get(j).prefix) && hmPrefix.get(sentence.words.get(j).prefix) > 50) // if a stemPOS appears more than 50 times, get possible diacritics
        {
            for (String d : hmDiacritic.keySet())
            {
                if (possibleDiacritics.contains(d) && (!hmDiacriticGivenPrefix.containsKey(sentence.words.get(j).prefix + "\t" + d) || hmDiacriticGivenPrefix.get(sentence.words.get(j).prefix + "\t" + d) <= -5.3))
                {
                    possibleDiacritics.remove(d);
                }
            }
        }
        
        if (hmWord.containsKey(sentence.words.get(j-1).word) && hmWord.get(sentence.words.get(j-1).word) > 1000)
        {
            for (String d : hmDiacritic.keySet())
            {
                if (!hmDiacriticGivenPrevWord.containsKey(sentence.words.get(j-1).word + "\t" + d))
                {
                    possibleDiacritics.remove(d);
                }
            }
        }
        
        // specify which sequences that are impossible to happen
        WordClass tWord = sentence.words.get(j);
        WordClass tPWord = sentence.words.get(j - 1);
        String tKey = tPWord.guessDiacritic + " " + tPWord.POS+ " " + tWord.POS;
        ArrayList<String> tmp = new ArrayList<String>();
        for (String d : possibleDiacritics)
        {
            if (!hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic.containsKey(tKey + "\t" + d) || hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic.get(tKey + "\t" + d) < -4.6)
            {
                // do nothing
            }
            else
            {
                tmp.add(d);
            }
        }
        if (tmp.size() > 0)
            possibleDiacritics = new ArrayList<String>(tmp);
        
        // specifically handle tanween with two simple rules
        // RULE 1: tanween is not allowed if the following word is followed with idafa
        
        if (!sentence.words.get(j).prefix.contains("ال") && sentence.words.get(j).POS.contains("NOUN") 
                && (sentence.words.get(j+1).prefix.contains("ال") || (sentence.words.get(j+1).POS.contains("NOUN") && sentence.words.get(j+1).POS.contains("PRON"))))
        {
            // remove tanween
            possibleDiacritics.remove("N");
            possibleDiacritics.remove("F");
            possibleDiacritics.remove("K");
        }
        // RULE 2: >n, <n, lkn would get a sokun only if followed by a verb.  Otherwise, it gets a shada-fatha
        if (sentence.words.get(j).prefix.matches("[فبلوك](أن|لكن)") 
                && sentence.words.get(j+1).stemPOS.equals("V"))
        {
            // remove tanween
            possibleDiacritics.remove("~a");
        }
        
        
        if (possibleDiacritics.size() == 0)
        {
            for (String d : hmDiacritic.keySet())
            {
                if (hmDiacritic.get(d) > 100)
                    possibleDiacritics.add(d);
            }
        }
        return possibleDiacritics;
    }
    
    public HashMap<String, Double> normalizeHashMapVals(HashMap<String, Double> fullHash, HashMap<String, Double> normHash)
    {
        for (String s : fullHash.keySet()) {
            String w = s.substring(0, s.indexOf("\t"));
            String p = s.substring(s.indexOf("\t") + 1);
            // if (hmPosGivenSuffix.containsKey(s) && hmSuffix.containsKey(w))
            {
                double score = Math.log(fullHash.get(s) / normHash.get(w));
                fullHash.put(s, score);
            }
        }
        return fullHash;
    }
    
    public HashMap<String,Double> incrementValGivenKey(HashMap<String,Double> input, String key, double increment)
    {
        if (!input.containsKey(key))
            input.put(key, increment);
        else
            input.put(key, input.get(key) + increment);
        return input;
    }
        
    public void serializeMap(String BinDir, String MapName, HashMap input) throws FileNotFoundException, IOException {
        FileOutputStream fos
                = new FileOutputStream(BinDir + "FDTdata." + MapName + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(input);
        oos.close();
        fos.close();
    }

    public HashMap deserializeMap(String BinDir, String MapName) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        FileInputStream fis = new FileInputStream(BinDir + "FDTdata." + MapName + ".ser");
         ObjectInputStream ois = new ObjectInputStream(fis);
         HashMap map = (HashMap) ois.readObject();
         ois.close();
         fis.close();
         return map;
    }
    
    public void serializeDataStructures(String binDir) throws IOException {
        serializeMap(binDir, "hmDiacritic", hmDiacritic);
        serializeMap(binDir, "hmWord", hmWord);
        serializeMap(binDir, "hmPOS", hmPOS);
        serializeMap(binDir, "hmPrefix", hmPrefix);
        serializeMap(binDir, "hmPrefixPOS", hmPrefixPOS);
        serializeMap(binDir, "hmSuffix", hmSuffix);
        serializeMap(binDir, "hmSuffixPOS", hmSuffixPOS);
        serializeMap(binDir, "hmTemplate", hmTemplate);
        serializeMap(binDir, "hmStem", hmStem);
        serializeMap(binDir, "hmStemPOS", hmStemPOS);
        serializeMap(binDir, "hmLastLetter", hmLastLetter);
        serializeMap(binDir, "hmGenderNumber", hmGenderNumber);
        serializeMap(binDir, "hmDiacriticGivenPOS", hmDiacriticGivenPOS);
        serializeMap(binDir, "hmDiacriticGivenPrevPOS", hmDiacriticGivenPrevPOS);
        serializeMap(binDir, "hmDiacriticGivenNextPOS", hmDiacriticGivenNextPOS);
        serializeMap(binDir, "hmDiacriticGivenWord", hmDiacriticGivenWord);
        serializeMap(binDir, "hmDiacriticGivenPrevWord", hmDiacriticGivenPrevWord);
        serializeMap(binDir, "hmDiacriticGivenNextWord", hmDiacriticGivenNextWord);
        serializeMap(binDir, "hmDiacriticGivenStem", hmDiacriticGivenStem);
        serializeMap(binDir, "hmDiacriticGivenPrevStem", hmDiacriticGivenPrevStem);        
        serializeMap(binDir, "hmDiacriticGivenNextStem", hmDiacriticGivenNextStem);
        serializeMap(binDir, "hmDiacriticGivenStemPOS", hmDiacriticGivenStemPOS);
        serializeMap(binDir, "hmDiacriticGivenPrevStemPOS", hmDiacriticGivenPrevStemPOS);
        serializeMap(binDir, "hmDiacriticGivenNextStemPOS", hmDiacriticGivenNextStemPOS);
        serializeMap(binDir, "hmDiacriticGivenGenderNumber", hmDiacriticGivenGenderNumber);
        serializeMap(binDir, "hmDiacriticGivenPrevGenderNumber", hmDiacriticGivenPrevGenderNumber);
        serializeMap(binDir, "hmDiacriticGivenNextGenderNumber", hmDiacriticGivenNextGenderNumber);
        serializeMap(binDir, "hmDiacriticGivenPrefix", hmDiacriticGivenPrefix);
        serializeMap(binDir, "hmDiacriticGivenSuffix", hmDiacriticGivenSuffix);
        serializeMap(binDir, "hmDiacriticGivenPrefixPOS", hmDiacriticGivenPrefixPOS);
        serializeMap(binDir, "hmDiacriticGivenSuffixPOS", hmDiacriticGivenSuffixPOS);
        serializeMap(binDir, "hmDiacriticGivenTemplate", hmDiacriticGivenTemplate);
        serializeMap(binDir, "hmDiacriticGivenLastLetter", hmDiacriticGivenLastLetter);
        serializeMap(binDir, "hmCurrentPrevPOSAndPrevDiacritic", hmCurrentPrevPOSAndPrevDiacritic);
        serializeMap(binDir, "hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic", hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic);
        serializeMap(binDir, "hmCurrent2PrevPOS", hmCurrent2PrevPOS);
        serializeMap(binDir, "hmDiacriticGivenCurrent2PrevPOS", hmDiacriticGivenCurrent2PrevPOS);
        
        serializeMap(binDir, "hmCurrentPrevWord", hmCurrentPrevWord);
        serializeMap(binDir, "hmDiacriticGivenCurrentPrevWord", hmDiacriticGivenCurrentPrevWord);
        
    }

        public void deserializeDataStructures(String binDir) throws IOException, FileNotFoundException, ClassNotFoundException {
        hmDiacritic = deserializeMap(binDir, "hmDiacritic");
        hmWord = deserializeMap(binDir, "hmWord");
        hmPOS = deserializeMap(binDir, "hmPOS");
        hmPrefix = deserializeMap(binDir, "hmPrefix");
        hmPrefixPOS = deserializeMap(binDir, "hmPrefixPOS");
        hmSuffix = deserializeMap(binDir, "hmSuffix");
        hmSuffixPOS = deserializeMap(binDir, "hmSuffixPOS");
        hmTemplate = deserializeMap(binDir, "hmTemplate");
        hmStem = deserializeMap(binDir, "hmStem");
        hmStemPOS = deserializeMap(binDir, "hmStemPOS");
        hmLastLetter = deserializeMap(binDir, "hmLastLetter");
        hmGenderNumber = deserializeMap(binDir, "hmGenderNumber");
        hmDiacriticGivenPOS = deserializeMap(binDir, "hmDiacriticGivenPOS");
        hmDiacriticGivenPrevPOS = deserializeMap(binDir, "hmDiacriticGivenPrevPOS");
        hmDiacriticGivenNextPOS = deserializeMap(binDir, "hmDiacriticGivenNextPOS");
        hmDiacriticGivenWord = deserializeMap(binDir, "hmDiacriticGivenWord");
        hmDiacriticGivenPrevWord = deserializeMap(binDir, "hmDiacriticGivenPrevWord");
        hmDiacriticGivenNextWord = deserializeMap(binDir, "hmDiacriticGivenNextWord");
        hmDiacriticGivenStem = deserializeMap(binDir, "hmDiacriticGivenStem");
        hmDiacriticGivenPrevStem = deserializeMap(binDir, "hmDiacriticGivenPrevStem");        
        hmDiacriticGivenNextStem = deserializeMap(binDir, "hmDiacriticGivenNextStem");
        hmDiacriticGivenStemPOS = deserializeMap(binDir, "hmDiacriticGivenStemPOS");
        hmDiacriticGivenPrevStemPOS = deserializeMap(binDir, "hmDiacriticGivenPrevStemPOS");
        hmDiacriticGivenNextStemPOS = deserializeMap(binDir, "hmDiacriticGivenNextStemPOS");
        hmDiacriticGivenGenderNumber = deserializeMap(binDir, "hmDiacriticGivenGenderNumber");
        hmDiacriticGivenPrevGenderNumber = deserializeMap(binDir, "hmDiacriticGivenPrevGenderNumber");
        hmDiacriticGivenNextGenderNumber = deserializeMap(binDir, "hmDiacriticGivenNextGenderNumber");
        hmDiacriticGivenPrefix = deserializeMap(binDir, "hmDiacriticGivenPrefix");
        hmDiacriticGivenSuffix = deserializeMap(binDir, "hmDiacriticGivenSuffix");
        hmDiacriticGivenPrefixPOS = deserializeMap(binDir, "hmDiacriticGivenPrefixPOS");
        hmDiacriticGivenSuffixPOS = deserializeMap(binDir, "hmDiacriticGivenSuffixPOS");
        hmDiacriticGivenTemplate = deserializeMap(binDir, "hmDiacriticGivenTemplate");
        hmDiacriticGivenLastLetter = deserializeMap(binDir, "hmDiacriticGivenLastLetter");
        hmCurrentPrevPOSAndPrevDiacritic = deserializeMap(binDir, "hmCurrentPrevPOSAndPrevDiacritic");
        hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic = deserializeMap(binDir, "hmDiacriticGivenCurrentPrevPOSAndPrevDiacritic");
        hmCurrent2PrevPOS = deserializeMap(binDir, "hmCurrent2PrevPOS");
        hmDiacriticGivenCurrent2PrevPOS = deserializeMap(binDir, "hmDiacriticGivenCurrent2PrevPOS");
        hmCurrentPrevWord = deserializeMap(binDir, "hmCurrentPrevWord");
        hmDiacriticGivenCurrentPrevWord = deserializeMap(binDir, "hmDiacriticGivenCurrentPrevWord");
        
        hmCurrentPrevNextPOS = deserializeMap(binDir, "hmCurrentPrevNextPOS");
        hmDiacriticGivenCurrentPrevNextPOS = deserializeMap(binDir, "hmDiacriticGivenCurrentPrevNextPOS"); 
    }

}
