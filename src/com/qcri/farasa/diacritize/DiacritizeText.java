/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qcri.farasa.diacritize;

import com.qcri.farasa.segmenter.ArabicUtils;
import static com.qcri.farasa.segmenter.ArabicUtils.openFileForReading;
import com.qcri.farasa.pos.FarasaPOSTagger;
import com.qcri.farasa.segmenter.Farasa;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kareemdarwish
 */
public class DiacritizeText
{

    private static String kenlmDir = ""; 
    private static String dataDirectory = ""; 
    private static Process process = null;
    private static Process process2ndLM = null;
    private static BufferedReader brLM = null;
    private static BufferedWriter bwLM = null;
    private static BufferedReader brLM2ndLM = null;
    private static BufferedWriter bwLM2ndLM = null;
    private static TMap<String, String> candidatesUnigram = new THashMap<String, String>();
    private static final HashMap<String, Integer> hPrefixes = new HashMap<String, Integer>();
    private static final HashMap<String, Integer> hSuffixes = new HashMap<String, Integer>();
    private static HashMap<String, String> diacritizedPrefixes = new HashMap<String, String>();
    private static HashMap<String, String> diacritizedSuffixes = new HashMap<String, String>();
    public static TMap<String, Integer> seenWordsMap = new THashMap<String, Integer>();
    
    public static TMap<String, String> bigramsWithSingleDiacritizations = new THashMap<String, String>();
    public RecoverCaseEnding rce = null;
    public Farasa farasaSegmenter = null;
    public FarasaPOSTagger farasaPOSTagger = null;

    public static TMap<String, String> defaultDiacritizationBasedOnTemplateProbability = new THashMap<String, String>();

    public void trainAndTestSVMCaseEnding(String filename) throws Exception
    {
        rce.train(filename);
        rce.generateSVM(filename);
    }
    
    public DiacritizeText(String dir, String lmFile1, String lmFile2, String dictionaryFile, Farasa fr, FarasaPOSTagger frPOS) throws IOException, FileNotFoundException, ClassNotFoundException, InterruptedException
    {
    	String os = System.getProperty("os.name");
    	String queryType = "";
    	if(os.toLowerCase().contains("win"))
    		queryType = "query.exe";
    	else
    		queryType = "query";
    	
        farasaSegmenter = fr;
        farasaPOSTagger = frPOS;
        if (!dir.endsWith("/"))
            dir = dir + "/";
        kenlmDir = dir;
        dataDirectory = dir;

        if (lmFile1.trim().length() > 0)
        {
        	
        	
            String[] args =
            {
                kenlmDir + queryType, // "-b",
                lmFile1
            };

            process = new ProcessBuilder(args).start();
            brLM = new BufferedReader(new InputStreamReader(process.getInputStream()));
            bwLM = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        }

        if (lmFile2.trim().length() > 0)
        {
        	
            String[] args2ndLM =
            {
                kenlmDir + queryType, // "-b",
                lmFile2
            };

            process2ndLM = new ProcessBuilder(args2ndLM).start();
            brLM2ndLM = new BufferedReader(new InputStreamReader(process2ndLM.getInputStream()));
            bwLM2ndLM = new BufferedWriter(new OutputStreamWriter(process2ndLM.getOutputStream()));
        }

        candidatesUnigram = loadCandidates(dictionaryFile);

        for (String prefixe : com.qcri.farasa.segmenter.ArabicUtils.prefixes)
        {
            hPrefixes.put(prefixe, 1);
        }
        for (String suffixe : com.qcri.farasa.segmenter.ArabicUtils.suffixes)
        {
            hSuffixes.put(suffixe, 1);
        }

        diacritizedPrefixes.put(ArabicUtils.buck2utf8("w"), ArabicUtils.buck2utf8("wa"));
        diacritizedPrefixes.put(ArabicUtils.buck2utf8("s"), ArabicUtils.buck2utf8("sa"));
        diacritizedPrefixes.put(ArabicUtils.buck2utf8("f"), ArabicUtils.buck2utf8("fa"));
        diacritizedPrefixes.put(ArabicUtils.buck2utf8("k"), ArabicUtils.buck2utf8("ka"));
        diacritizedPrefixes.put(ArabicUtils.buck2utf8("b"), ArabicUtils.buck2utf8("bi"));
        diacritizedPrefixes.put(ArabicUtils.buck2utf8("l"), ArabicUtils.buck2utf8("li"));
        diacritizedPrefixes.put(ArabicUtils.buck2utf8("ll"), ArabicUtils.buck2utf8("lilo"));
        diacritizedPrefixes.put(ArabicUtils.buck2utf8("Al"), ArabicUtils.buck2utf8("Aalo"));

        diacritizedSuffixes.put(ArabicUtils.buck2utf8("hmA"), ArabicUtils.buck2utf8("humA"));
        diacritizedSuffixes.put(ArabicUtils.buck2utf8("km"), ArabicUtils.buck2utf8("kum"));
        diacritizedSuffixes.put(ArabicUtils.buck2utf8("hm"), ArabicUtils.buck2utf8("hum"));
        diacritizedSuffixes.put(ArabicUtils.buck2utf8("hn"), ArabicUtils.buck2utf8("hun"));

        BufferedReader brDiacritizationBasedOnTemplateProb = openFileForReading(dir + "diacritizedWords.dictionary.template.1.0");
        String line = "";
        while ((line = brDiacritizationBasedOnTemplateProb.readLine()) != null)
        {
            String[] parts = line.split("\t");
            if (parts.length == 2)
            {
                defaultDiacritizationBasedOnTemplateProbability.put(parts[0], parts[1]);
            }
        }

        rce = new RecoverCaseEnding(this, dataDirectory);
    }

    private static String correctLamAlefLamNoPrefixes (String input, boolean withDiacritics)
    {
        String output = "";
        if (ArabicUtils.removeDiacritics(input).startsWith("لال") || ArabicUtils.removeDiacritics(input).startsWith("ل+ال"))
        {
            int i = 0;
            while (!(ArabicUtils.removeDiacritics(input.substring(0, i)).replace("+", "").equals("لال")))
            {
                i++;
            }
            if (withDiacritics)
                output = ArabicUtils.buck2utf8("lilo") + input.substring(i).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
            else
                output = ArabicUtils.buck2utf8("ll") + input.substring(i).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
        }
        else
        {
            output = input;
        }
        return output;
    }
    
    private static String correctLamAlefLam (String input, boolean withDiacritics)
    {
        if (ArabicUtils.removeDiacritics(input).startsWith("لال"))
        {
            return correctLamAlefLamNoPrefixes(input, withDiacritics);
        }
        else if (ArabicUtils.removeDiacritics(input).startsWith("ولال") || ArabicUtils.removeDiacritics(input).startsWith("فلال"))
        {
            String firstLetter = input.substring(0, 1);
            input = input.substring(input.indexOf("ل"));
            if (withDiacritics)
            {
                firstLetter += ArabicUtils.buck2utf8("a");
            }
            return firstLetter + correctLamAlefLamNoPrefixes(input, withDiacritics);
        }
        else
        {
            return input;
        }
    }

    
    private static String combineDiacritizedWordWithCaseEnding(DiacritizeText dt, ArrayList<String> caseEndings, String[] diacritizedWords)
    {
        String output = "";
        if (caseEndings.size() != diacritizedWords.length)
            System.err.println();
        for (int i = 0; i < caseEndings.size(); i++)
        {
            if (!caseEndings.get(i).trim().startsWith("# 0") && !caseEndings.get(i).trim().startsWith("# 1.")) {
                String[] parts = caseEndings.get(i).split("[ \t]+");
                String word = parts[0];
                String stem = parts[1];
                String ending = parts[parts.length - 1];
                String dWord = correctLamAlefLam(diacritizedWords[i], true);
                Double endingScore = 0d;
                if (ending.contains("/"))
                {
                    endingScore = Double.parseDouble(ending.substring(ending.indexOf("/") + 1));
                    ending = ending.substring(0, ending.indexOf("/")).trim();
                }
                else
                {
                    System.err.println();
                }
                if (endingScore < 0.8 || ending.equals("null")) {
                    ending = "";
                }
//                if (dt.getwordsWithSingleDiacritizations().containsKey(word.trim().replace("+", "")))
//                {
//                    output += "§" + dt.getwordsWithSingleDiacritizations().get(word.trim().replace("+", "")) + "§ ";
//                }
                else if ((stem.trim().equals("#") && !word.endsWith("+ه")) || ending.trim().equals("null") || ending.trim().equals("Maad")) 
                {
                    output += dWord + " ";
                } else {
                    // get prefix
                    int pos = word.indexOf(stem);
                    if (pos == -1) {
                        if (stem.startsWith("#")) {
                            stem = stem.substring(1);
                            pos = word.indexOf(stem);
                        }
                    }
                // if (word.startsWith("ب"))
                    //     System.err.println();
                    String prefixPlusStem = "";
                    if (pos == -1)
                    {
                        if (stem.contains("+"))
                            stem = stem.substring(0, stem.indexOf("+"));
                        pos = word.indexOf(stem);
                        if (pos > -1){
                            prefixPlusStem = correctLamAlefLam((word.substring(0, pos) + stem).replace("+", ""), false);
                        }
                        else
                        {
                            prefixPlusStem = stem;
                            System.err.println(word + "\t" + stem);
                        }
                            
                    }
                    else
                        prefixPlusStem = correctLamAlefLam((word.substring(0, pos) + stem).replace("+", ""), false);
                    int j = 0;
                // if (ArabicUtils.removeDiacritics(dWord).contains("كما"))
                    //    System.err.println(dWord);
                    // get the position of the last letter in the diacritized word without diacritics
                    if (!ArabicUtils.removeDiacritics(dWord).equals(prefixPlusStem))
                        System.err.println();
                    while (!(ArabicUtils.removeDiacritics(dWord.substring(0, j)).equals(prefixPlusStem))) {
                        j++;
                    }
                    String fullDiacritization = "";
                    if (ending.equals("oi"))
                    {
                        if (stem.endsWith("+ين"))
                        {
                        // find the last diacritic before yn, 
                        // if it is null, then put a at the end of the word
                        // else put a i
                        
                            String stemWithoutYn = stem.substring(0, stem.indexOf("+ين"));
                            int positionInWord = 0;
                            for (int k = 0; k < stemWithoutYn.length(); k++)
                            {
                                positionInWord = dWord.indexOf(stemWithoutYn.substring(k, k + 1), positionInWord);
                            }
                            if (dWord.substring(positionInWord + 1, positionInWord + 2).matches("[" + ArabicUtils.buck2utf8("aiouNKF~") + "]"))
                            {
                                ending = "i";
                            }
                            else
                            {
                                ending = "a";
                            }
                            fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8(ending) + dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                        }
                        else
                        {
                            fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8("i") + dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                        }
                    }
                    else
                    {
                        if (!ending.isEmpty())
                        {
                            fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8(ending);
                            if (diacritizedSuffixes.containsKey(dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "")))
                            {
                                fullDiacritization += diacritizedSuffixes.get(dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", ""));
                            }
                            else
                            {
                                fullDiacritization += dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                            }
                            if (word.endsWith("+ه"))
                            {
                                if (ending.contains("i"))
                                {
                                    fullDiacritization += ArabicUtils.buck2utf8("i");
                                }
                                else
                                {
                                    fullDiacritization += ArabicUtils.buck2utf8("u");
                                }
                            }
                        }
                        else
                        {
                            if (word.contains("+ه"))
                            {
                                String tmpdWord = dWord.replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                                if (tmpdWord.endsWith("ه"))
                                {
                                    tmpdWord = tmpdWord.substring(0, tmpdWord.length() - 1);
                                    // find last diacritic
                                    if (tmpdWord.matches(".*[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+$"))
                                    {
                                        if (tmpdWord.endsWith(ArabicUtils.buck2utf8("i")))
                                            // ends with i
                                            ending = "i";
                                        else
                                            ending = "u";
                                        fullDiacritization = tmpdWord + "ه" + ArabicUtils.buck2utf8(ending);
                                    }
                                    else if (tmpdWord.endsWith("ي"))
                                    {
                                        ending = "i";
                                        fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8(ending) + dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                                    }
                                    else
                                    {
                                        fullDiacritization = tmpdWord + ArabicUtils.buck2utf8("a") + "ه" + ArabicUtils.buck2utf8("u");
                                    }
                                }
                                else
                                {
                                    fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8(ending) + dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                                }
                            }
                            else
                            {
                                fullDiacritization = dWord.substring(0, j) + ArabicUtils.buck2utf8(ending) + dWord.substring(j).replaceFirst("^[" + ArabicUtils.buck2utf8("aiouNKF~") + "]+", "");
                            }
                        }
                    }
                    output += fullDiacritization + " ";
                }
            }
        }
        // output = putBigramsWithSingleDiacritization(output, dt);
        return output.trim();
    }
    
    public TMap<String, String> getbigramsWithSingleDiacritizations()
    {
        return bigramsWithSingleDiacritizations;
    }

    public ArrayList<String> tagWords(String inputText) throws InterruptedException, ClassNotFoundException, Exception
    {
        // ArrayList<String> output = tagger.tag(inputText, false, true);
        ArrayList<String> output = farasaSegmenter.segmentLine(inputText);
        // Sentence output = farasaPOSTagger.tagLine(segmentedWords);
        return output;
    }

    public void loadSeenWords() throws FileNotFoundException, IOException
    {
        BufferedReader br = openFileForReading("/work/CLASSIC/DIACRITIZE/all-text.txt.diacritized.full-tok.uni.count");
        String line = "";
        while ((line = br.readLine()) != null)
        {
            String[] parts = line.split("\t");
            if (parts[0].trim().length() > 0 && parts[1].matches("[0-9]+"))
            {
                parts[0] = standardizeDiacritics(parts[0].replace("+", "").replace("_", ""));
                seenWordsMap.put(parts[0], Integer.parseInt(parts[1]));
            }
        }
    }

    public String diacritize(String input) throws IOException, ClassNotFoundException, Exception
    {
        return diacritize(ArabicUtils.tokenize(input));
    }

    public String diacritize(ArrayList<String> input) throws IOException, ClassNotFoundException, Exception
    {
        HashMap<Integer, ArrayList<String>> latice = buildLaticeStem(input);
        SentenceClass sentence = rce.putCaseEnding(findBestPath(latice));
        sentence = correctLamAlefLam(sentence);
        String output = "";
        for (int i = 1; i < sentence.words.size() - 1; i++)
        {
            // if (!sentence.words.get(i).POS.equals("S") && !sentence.words.get(i).POS.equals("E"))
            {
                output += standardizeDiacritics(sentence.words.get(i).wordFullyDiacritized) + " ";
            }
        }
        // return standardizeDiacritics(rce.putCaseEnding(findBestPath(latice))).replace("  ", " ").trim();
        return output.trim();
    }

    public String diacritizeLM(String input) throws IOException, ClassNotFoundException, Exception
    {
        HashMap<Integer, ArrayList<String>> latice = buildLaticeStem(ArabicUtils.tokenize(input));
        return findBestPath(latice).trim();
    }
    
    public static SentenceClass correctLamAlefLam(SentenceClass sentence)
    {
        for (int i = 0; i < sentence.words.size(); i++)
        {
            WordClass tWord = sentence.words.get(i);
            if (tWord.prefix.contains("ل+ال") || tWord.prefix.contains("لال"))
            {
                tWord.wordFullyDiacritized = tWord.wordFullyDiacritized.replaceFirst("لِا", "لِ");
            }
        }
        return sentence;
    }
    
    public static String standardizeDiacritics(String word) {
        String diacrtics = "[\u064e\u064b\u064f\u064c\u0650\u064d\u0652\u0651]";
        String sokun = "\u0652";
        String fatha = "\u064e";
        word = word.replaceFirst("^" + diacrtics + "+", "");
        word = word.replace("َا", "ا").replace("ُو", "و").replace("ِي", "ي").replace("آَ", "آ");
        int pos = word.indexOf("و");
        while (pos > 0 && pos < word.length() - 1) {
            if (!word.substring(pos - 1, pos).matches(diacrtics) && word.substring(pos + 1, pos + 2).equals(sokun)) {
                word = word.substring(0, pos + 1) + word.substring(pos + 2);
            }
            pos = word.indexOf("و", pos + 1);
        }
        pos = word.indexOf("ي");
        while (pos > 0 && pos < word.length() - 1) {
            if (!word.substring(pos - 1, pos).matches(diacrtics) && word.substring(pos + 1, pos + 2).equals(sokun)) {
                word = word.substring(0, pos + 1) + word.substring(pos + 2);
            }
            pos = word.indexOf("ي", pos + 1);
        }
        pos = word.indexOf("ا");
        while (pos > 0 && pos < word.length() - 1) {
            if (!word.substring(pos - 1, pos).matches(diacrtics)
                    && (word.substring(pos + 1, pos + 2).equals(sokun) || word.substring(pos + 1, pos + 2).equals(fatha))) {
                word = word.substring(0, pos + 1) + word.substring(pos + 2);
            }
            pos = word.indexOf("ا", pos + 1);
        }
        if (word.startsWith("الْ")) {
            word = word.replaceFirst("الْ", "ال");
        }
        // word = word.replaceFirst(diacrtics + "+$", "");
        return word;
    }
    
    public String limitNumberOfChoices(String input, int max)
    {
        String output = ";";
        String[] parts = input.split(";");
        for (int i = 0; i <= Math.min(parts.length - 1, max); i++)
        {
            output += parts[i] + ";";
        }
        return output;
    }

    private TMap<String, String> loadCandidates(String filePath) throws FileNotFoundException, IOException, ClassNotFoundException
    {
        TMap<String, String> candidates = new THashMap<String, String>();
        
        String line = "";
        File file = new File(filePath + ".ser");
	if (file.exists()) file.delete();
        if (file.exists()) {
            ObjectInputStream ios = new ObjectInputStream(new FileInputStream(file));
            candidates = (THashMap) ios.readObject();
        } else {
            BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));
            while ((line = sr.readLine()) != null)
            {
                if (line.length() > 0)
                {
                    String[] lineParts = line.split("\t");
                    if (line.length() > 0 && lineParts.length > 0) // && Regex.IsMatch("^[0-9\\.\\-]$"))
                    {
                        candidates.put(lineParts[0], limitNumberOfChoices(lineParts[1], 100));
                    }
                }
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(candidates);
            oos.close();
        }
        return candidates;
    }

    private boolean checkIfAllChoicesHaveTheSameScore(ArrayList<String> unigramChoices) throws IOException
    {
        double bestScore = -1000;
        boolean sameScore = true;
        for (int i = 0; i < unigramChoices.size(); i++)
        { // s : paths) {
            // double finalScore = scoreUsingLM(unigramChoices.get(i));
            double finalScore = scoreUsingTwoLMs(unigramChoices.get(i));
            if (bestScore != finalScore && i > 0)
            {
                sameScore = false;
            }
            if (bestScore < finalScore)
            {
                bestScore = finalScore;
            }
        }
        return sameScore;
    }

    private String GetBestChoiceUnigram(ArrayList<String> unigramChoices) throws IOException
    {
        double bestScore = -1000;
        String bestChoice = "";
        for (int i = 0; i < unigramChoices.size(); i++)
        { // s : paths) {
            // double finalScore = scoreUsingLM(unigramChoices.get(i));
            double finalScore = scoreUsingTwoLMs(unigramChoices.get(i));
            if (bestScore < finalScore)
            {
                bestScore = finalScore;
                bestChoice = unigramChoices.get(i);
            }
        }
        return bestChoice;
    }

    private String getWordStem(String w, boolean withAffixes) throws InterruptedException, ClassNotFoundException, Exception
    {
        String lastDiacriticRegex = "[" + ArabicUtils.buck2utf8("aiou") + "]+$";
        // try stemming
        ArrayList<String> clitics = farasaSegmenter.segmentLine(w);// tagger.tag(w, true, false);
        String cliticSplit = "";
        for (int c = 0; c < clitics.size(); c++)
        {
            if (!clitics.get(c).equals("_"))
            {
                if (cliticSplit.trim().length() > 0)
                {
                    cliticSplit += "+";
                }
                cliticSplit += clitics.get(c);
            }
        }

        cliticSplit = getProperSegmentation(cliticSplit);
        cliticSplit = transferDiacriticsFromWordToSegmentedVersion(w, cliticSplit);
        // prefixes & stem & suffixes 
        if (withAffixes)
        {
            return cliticSplit;
        }
        else
        {
            if (cliticSplit.contains(";"))
            {
                String taMarbouta = "";
                if (cliticSplit.contains(";+ة") || cliticSplit.contains(";+ت"))
                {
                    if (!cliticSplit.endsWith(";+ت"))
                    {
                        taMarbouta = "ة";
                    }
                }
                String stem = cliticSplit.substring(cliticSplit.indexOf(";") + 1);
                stem = stem.substring(0, stem.indexOf(";"));
                stem = stem.replace(";", "");
                if (taMarbouta.length() > 0)
                {
                    stem += taMarbouta;
                }
                else
                {
                    stem = stem.replaceFirst(lastDiacriticRegex, "");
                }
                return stem;
            }
            else
            {
                return cliticSplit;
            }
        }
    }

    public static String transferDiacriticsFromWordToSegmentedVersion(String diacritizedWord, String stemmedWord)
    {
        boolean startsWithLamLam = false;
        boolean startsWithWaLamLam = false;
        boolean startsWithFaLamLam = false;
        if (
                (stemmedWord.startsWith("ل+ال") &&
                ArabicUtils.removeDiacritics(diacritizedWord).startsWith("لل")) ||
                (stemmedWord.startsWith("و+ل+ال") &&
                ArabicUtils.removeDiacritics(diacritizedWord).startsWith("ولل")) ||
                (stemmedWord.startsWith("ف+ل+ال") &&
                ArabicUtils.removeDiacritics(diacritizedWord).startsWith("فلل"))
                )
        {
            // startsWithLamLam = true;
            int posFirstLam = diacritizedWord.indexOf("ل", 0);
            int posSecondLam = diacritizedWord.indexOf("ل", posFirstLam + 1);
            diacritizedWord = diacritizedWord.substring(0, posSecondLam) + "ا" + diacritizedWord.substring(posSecondLam);
        }
        
        String output = "";
        stemmedWord = stemmedWord.replace(" ", "");
        stemmedWord = stemmedWord.replaceFirst("\\+$", "");
        if (diacritizedWord.equals(stemmedWord) || !stemmedWord.contains("+"))
            return diacritizedWord;
        
        int pos = 0;
        for (int i = 0; i < stemmedWord.length(); i++)
        {
            if (stemmedWord.substring(i, i+1).equals("+") || stemmedWord.substring(i, i+1).equals(";"))
            {
                {
                    output += stemmedWord.substring(i, i+1);
                }
            }
            else
            {
                int loc = diacritizedWord.indexOf(stemmedWord.substring(i, i+1), pos);
                if (loc >= 0)
                {
                    String diacritics = diacritizedWord.substring(pos, loc);
                    output += diacritics + stemmedWord.substring(i, i+1);
                    // add trailing diacritics
                    loc++;
                    while (loc < diacritizedWord.length() && diacritizedWord.substring(loc, loc + 1).matches("[" + 
                           com.qcri.farasa.segmenter.ArabicUtils.buck2utf8("aiouNKF~") + "]"))
                    {
                        output += diacritizedWord.substring(loc, loc + 1);
                        loc++;
                    }
                    pos = loc;
                }
                else
                {
                    // System.err.println(diacritizedWord + "\t" + stemmedWord);
                }
            }
        }
        return output;
    }
    
    private String checkIfOOVandGetMostLikelyUnigramSolution(ArrayList<String> unigramChoices) throws IOException, FileNotFoundException, ClassNotFoundException, Exception
    {

        String output = "***";
        if (unigramChoices.size() == 1)
        {
            return unigramChoices.get(0);
        }
        else
        {
            // check if they have same score
            boolean sameScore = checkIfAllChoicesHaveTheSameScore(unigramChoices);
            if (sameScore)
            {
                // System.err.println(unigramChoices.get(0));

                // attempt to stem and find the best solution
                ArrayList<String> stems = new ArrayList<String>();
                HashMap<String, String> stemToWordMap = new HashMap<String, String>();
                for (String s : unigramChoices)
                {
                    String stem = getWordStem(s, false);
                    stems.add(stem.replaceFirst("[" + ArabicUtils.buck2utf8("aiou") + "]+$", ""));
                    stemToWordMap.put(stem, s);
                }
//                if (ArabicUtils.removeDiacritics(stems.get(0)).equals("حياة"))
//                    System.err.println();
                if (checkIfAllChoicesHaveTheSameScore(stems))
                {
                    // System.err.println("**" + unigramChoices.get(0) + "\t" + stems.get(0));
                    // revert to the most commonly used template
                    if (defaultDiacritizationBasedOnTemplateProbability.containsKey(ArabicUtils.removeDiacritics(stems.get(0))))
                    {
                        output = defaultDiacritizationBasedOnTemplateProbability.get(ArabicUtils.removeDiacritics(stems.get(0)));
                        if (stemToWordMap.containsKey(output))
                        {
                            output = stemToWordMap.get(output);
                        }
                        else
                        {
                            // get original prefixes and suffixes
                            String stemWithPrefixesAndSuffixes = getWordStem(ArabicUtils.removeDiacritics(unigramChoices.get(0)), true);
                            if (stemWithPrefixesAndSuffixes.contains(";"))
                            {
                                String Prefixes = stemWithPrefixesAndSuffixes.replaceFirst(";.*", "");
                                String Suffixes = stemWithPrefixesAndSuffixes.replaceFirst(".*;", "");
                                if (Suffixes.startsWith("+ة"))
                                {
                                    Suffixes = Suffixes.substring(2);
                                }
                                else if (Suffixes.startsWith("+ت") && !Suffixes.endsWith("+ت"))
                                {
                                    Suffixes = Suffixes.substring(2);
                                    if (output.endsWith("ة"))
                                    {
                                        output = output.replace("ة", "ت");
                                    }
                                }
                                output = diacritizePrefixes(Prefixes) + output + diacritizeSuffixes(Suffixes, "");
                            }
                        }
                    }
                    System.err.println("**" + unigramChoices.get(0) + "\t" + stems.get(0) + "\tusing: " + output);
                }
                else
                {
                    String bestChoice = GetBestChoiceUnigram(stems);
                    output = stemToWordMap.get(bestChoice);
                }
            }
            return output;
        }
    }

    private String findBestPath(HashMap<Integer, ArrayList<String>> latice) throws IOException, Exception
    {
        String space = " +";
        HashMap<Integer, String> finalAnswer = new HashMap<Integer, String>();

        for (int i = 1; i <= latice.keySet().size() - 1; i++)
        {
            String sBase = "";
            finalAnswer.get(0);
            for (int j = 1; j < i; j++)
            {
                sBase += " " + finalAnswer.get(j);
            }

            ArrayList<String> paths = new ArrayList<String>();
//            if (checkIfOOVandGetMostLikelyUnigramSolution(latice.get(i)).equals("***"))
//            {
                // add options for current node
                for (String sol : latice.get(i))
                {
                    paths.add(sBase + " " + sol);
                }
//            }
//            else
//            {
//                paths.add(sBase + " " + checkIfOOVandGetMostLikelyUnigramSolution(latice.get(i)));
//            }

            ArrayList<String> pathsNext = new ArrayList<String>();
            // add options for next node
            for (String s : paths)
            {
                // System.err.println(i);
                for (String sol : latice.get(i + 1))
                {
                    pathsNext.add(s + " " + sol);
                }
            }

            // determine best option for current word
            // this would be done using the language model
            String bestPathOutput = findBestPathLM(pathsNext).trim();

            String[] bestPath = bestPathOutput.split(" +");

            if (bestPath.length == i + 1 || bestPath.length == i)
            { // + 2) {
                finalAnswer.put(i, bestPath[i - 1]);
            }
            else
            {
                System.err.println("ERROR");
            }
        }
        String sBest = ""; // finalAnswer.get(1);
        for (int k = 1; k <= finalAnswer.keySet().size(); k++)
        {
            sBest += " " + finalAnswer.get(k);
        }
        return sBest.replaceAll(" +", " ").trim();
    }

    private String correctLeadingLamAlefLam(String s)
    {
        if (s.startsWith("لال"))
        {
            s = "لل" + s.substring(3);
        }
        return s;
    }

    public double scoreUsingLM(String s) throws IOException
    {
        bwLM.write(s + "\n");
        bwLM.flush();
        String stemp = brLM.readLine();
        if (stemp.contains("Total:"))
        {
            stemp = stemp.replaceFirst(".*Total\\:", "").trim();
            stemp = stemp.replaceFirst("OOV.*", "").trim();
        }
        else
        {
            stemp = "-1000";
        }
        if (stemp.contains("inf"))
        {
            return -1000f;
        }

        double finalScore = Double.parseDouble(stemp);
        return finalScore;
    }

    public double scoreUsingTwoLMs(String s) throws IOException
    {
        String stemp = "0";
        if (bwLM != null && brLM != null)
        {
            bwLM.write(s + "\n");
            bwLM.flush();

            stemp = brLM.readLine();
            if (stemp.contains("Total:"))
            {
                stemp = stemp.replaceFirst(".*Total\\:", "").trim();
                stemp = stemp.replaceFirst("OOV.*", "").trim();
            }
            else
            {
                stemp = "-100";
            }
        }
        double firstScore = 0d;
        if (stemp.contains("inf"))
        {
            firstScore = -100f;
        }
        else
        {
            firstScore = Double.parseDouble(stemp);
        }

        String stemp2ndLM = "0";
        if (bwLM2ndLM != null && brLM2ndLM.readLine() != null)
        {
            bwLM2ndLM.write(s + "\n");
            bwLM2ndLM.flush();
            // System.err.println(s);
            stemp2ndLM = brLM2ndLM.readLine();
            if (stemp2ndLM.contains("Total:"))
            {
                stemp2ndLM = stemp2ndLM.replaceFirst(".*Total\\:", "").trim();
                stemp2ndLM = stemp2ndLM.replaceFirst("OOV.*", "").trim();
            }
            else
            {
                stemp2ndLM = "-100";
            }
        }
        double secondScore = 0d;
        if (stemp2ndLM.contains("inf"))
        {
            secondScore = -100f;
        }
        else
        {
            secondScore = Double.parseDouble(stemp2ndLM);
        }

        double finalScore = 0d;
        if (firstScore > -100 && secondScore > -100)
        {
            finalScore = 0.1 * firstScore + 0.9 * secondScore;
        }
        else
        {
            finalScore = Math.min(firstScore, secondScore);
        }

        return finalScore;
    }

    private String findBestPathLM(ArrayList<String> paths) throws IOException
    {
    	 return paths.get(0);
//        if (paths.size() == 1)
//        {
//            return paths.get(0);
//        }
//        else
//        {
//            double bestScore = -1000;
//            boolean sameScore = true;
//            String bestPath = "";
//            for (int i = 0; i < paths.size(); i++)
//            { // s : paths) {
//                // only score the last n words
//                String s = paths.get(i);
//                String ss = getTheTrailingNWords(s, 5);
//                // double finalScore = scoreUsingLM(ss);
//                double finalScore = scoreUsingTwoLMs(ss);
//                if (bestScore != finalScore && i > 0)
//                {
//                    sameScore = false;
//                }
//                if (bestScore < finalScore)
//                {
//                    bestScore = finalScore;
//                    bestPath = s;
//                }
//            }
//            // if (sameScore)
//            //    bestPath += "***";
//            return bestPath;
//        }
    }

    private String getTheTrailingNWords(String s, int n)
    {
        String ss = "";
        String[] parts = s.split(" +");
        if (parts.length <= n)
        {
            return s;
        }
        else
        {
            for (int i = parts.length - n; i < parts.length; i++)
            {
                ss += parts[i] + " ";
            }
        }
        return ss.trim();
    }

    private HashMap<Integer, ArrayList<String>> buildLatice(ArrayList<String> words)
    {
        HashMap<Integer, ArrayList<String>> latice = new HashMap<Integer, ArrayList<String>>();
        int i = 0;

        ArrayList<String> temp = new ArrayList<String>();
        // temp.add("<s>");
        temp.add(" ");
        i++;
        latice.put(i, temp);

        for (String w : words)
        {
            // if (bStem == false) {
            String norm = ArabicUtils.removeDiacritics(w); // correctLeadingLamAlefLam(normalizeFull(w));
            if (candidatesUnigram.containsKey(norm) && candidatesUnigram.get(norm).split(";").length > 0)
            {
                temp = new ArrayList<String>();
                for (String s : candidatesUnigram.get(norm).split(";"))
                {
                    if (s.length() > 0)
                    {
                        if (!temp.contains(s))
                        {
                            temp.add(s);
                        }
                    }
                }

                // if multiple candidates exist and one does not have diacritics, then remove it
                if (temp.size() > 1)
                {
                    ArrayList<String> ttemp = new ArrayList<String>(temp);
                    for (String t : temp)
                    {
                        //                    if (!t.matches(".*[ًٌٍُِّْ].*"))
                        {
                            // put phoney dicritics and see if they get removed
                            String tt = "";
                            for (int k = 0; k < t.length(); k++)
                            {
                                if (t.substring(k, k + 1).matches("[ايو]"))
                                {
                                    tt += t.substring(k, k + 1);
                                }
                                else
                                {
                                    if (k < t.length() - 1)
                                    {
                                        if (t.substring(k + 1, k + 2).matches("[ايو]"))
                                        {
                                            tt += t.substring(k, k + 1);
                                        }
                                        else
                                        {
                                            tt += t.substring(k, k + 1) + ArabicUtils.buck2utf8("a");
                                        }
                                    }
                                    else
                                    {
                                        tt += t.substring(k, k + 1) + ArabicUtils.buck2utf8("a");
                                    }
                                }
                            }
                            if (tt.matches(".*[ًٌٍَُِّْ].*"))
                            {
                                ttemp.remove(t);
                            }
                        }
                    }
                    if (ttemp.size() > 0 && temp.size() != ttemp.size())
                    {
                        temp = new ArrayList<String>(ttemp);
                    }
                }

                if (temp.size() > 0)
                {
                    latice.put(i, temp);
                }
            }
            else
            {
                temp = new ArrayList<String>();
                temp.add(w);
                latice.put(i, temp);
            }
            i++;
        }
        temp = new ArrayList<String>();
        // temp.add("</s>");
        temp.add(" ");
        latice.put(i, temp);

        return latice;
    }

    public String diacritizePrefixes(String prefixString)
    {
        String[] tmpP = prefixString.split("\\+");
        ArrayList<String> prefixes = new ArrayList<String>();
        for (String p : tmpP)
        {
            if (p.length() > 0)
            {
                prefixes.add(p);
            }
        }
        return diacritizePrefixes(prefixes);
    }

    public String diacritizePrefixes(ArrayList<String> prefixes)
    {
        String diacritizedWord = "";
        for (String p : prefixes)
        {
            if (p.length() > 0)
            {
                diacritizedWord += diacritizedPrefixes.get(p);
            }
        }
        return diacritizedWord;
    }

    public String diacritizeSuffixes(String suffixString, String caseEnding)
    {
        String[] tmpS = suffixString.split("\\+");
        ArrayList<String> suffixes = new ArrayList<String>();
        for (String s : tmpS)
        {
            if (s.length() > 0)
            {
                suffixes.add(s);
            }
        }
        return diacritizeSuffixes(suffixes, caseEnding);
    }

    public String diacritizeSuffixes(ArrayList<String> suffixes, String caseEnding)
    {
        String diacritizedWord = "";
        for (String p : suffixes)
        {
            if (p.length() > 0)
            {
                if (p.equals("ة") || p.equals("ت"))
                {
                    diacritizedWord += p + caseEnding;
                }
                else if (diacritizedSuffixes.containsKey(p))
                {
                    diacritizedWord += diacritizedSuffixes.get(p);
                }
                else
                {
                    diacritizedWord += p;
                }
            }
        }
        return diacritizedWord;
    }

    public HashMap<Integer, ArrayList<String>> buildLaticeStem(ArrayList<String> words) throws InterruptedException, ClassNotFoundException, Exception
    {
        HashMap<Integer, ArrayList<String>> latice = new HashMap<Integer, ArrayList<String>>();
        int i = 0;

        String[] diacritics =
        {
            ""
        }; // a", "i", "o", "u", "N", "K", "F", ""};

        ArrayList<String> temp = new ArrayList<String>();
        // temp.add("<s>");
        temp.add(" ");
        i++;
        latice.put(i, temp);

        for (String w : words)
        {
            if (w.length() > 0)
            {
                String norm = ArabicUtils.removeDiacritics(w); // correctLeadingLamAlefLam(normalizeFull(w));

                if (candidatesUnigram.containsKey(norm) && candidatesUnigram.get(norm).split(";").length > 0)
                {
                    temp = new ArrayList<String>();
                    for (String s : candidatesUnigram.get(norm).split(";"))
                    {
                        if (s.length() > 0)
                        {
                            if (!temp.contains(s))
                            {
                                for (String di : diacritics)
                                {
                                    if (!(di.matches("[NKF]") && s.endsWith("ّ")) && !temp.contains(s + ArabicUtils.buck2utf8(di))) // && seenWordsMap.containsKey(s)) // don't put tanween with shadda
                                    {
                                        temp.add(s + ArabicUtils.buck2utf8(di));
                                    }
                                }
                            }
                        }
                    }
                    if (temp.size() > 0)
                    {
                        latice.put(i, temp);
                    }

                }
                
                else
                {
                    // try stemming
                    ArrayList<String> clitics = farasaSegmenter.segmentLine(w);// tagger.tag(w, true, false);
                    String cliticSplit = "";
                    for (int c = 0; c < clitics.size(); c++)
                    {
                        if (!clitics.get(c).equals("_"))
                        {
                            if (cliticSplit.trim().length() > 0)
                            {
                                cliticSplit += "+";
                            }
                            cliticSplit += clitics.get(c);
                        }
                    }

                    cliticSplit = getProperSegmentation(cliticSplit);

                    // prefixes & stem & suffixes 
                    cliticSplit = cliticSplit.replace("ل+ال", "لل");

                    String[] tprefix = (" " + cliticSplit + " ").split(";")[0].trim().split("\\+");
                    ArrayList<String> prefixes = new ArrayList<String>();
                    for (String p : tprefix)
                    {
                        if (p.trim().length() > 0)
                        {
                            prefixes.add(p);
                        }
                    }

                    String[] tsuffix = (" " + cliticSplit + " ").split(";")[2].trim().split("\\+");
                    ArrayList<String> suffixes = new ArrayList<String>();
                    for (String p : tsuffix)
                    {
                        if (p.trim().length() > 0)
                        {
                            suffixes.add(p);
                        }
                    }

                    String stem = (" " + cliticSplit + " ").split(";")[1].trim();

                    // first suffix is ta marbouta, add it to the stem
                    if ((suffixes.size() > 0 && suffixes.get(0).equals("ة"))
                            || (suffixes.size() > 1 && suffixes.get(0).equals("ت")))
                    {
                        stem += "ة";
                        suffixes.remove(0);
                    }
                    else if (suffixes.size() > 0 && suffixes.get(0).equals("ات"))
                    {
                        stem += "ات";
                        suffixes.remove(0);
                    }

                    if (candidatesUnigram.containsKey(stem) && candidatesUnigram.get(stem).split(";").length > 0)
                    {

                        temp = new ArrayList<String>();
                        // we want to get the highest scoring stem and we omit the others
                        String topChoice = "";
                        double topChoiceScore = -1000d;
                        for (String s : candidatesUnigram.get(stem).split(";"))
                        {
                            if (s.trim().length() > 0)
                            {

                                double tmpScore = 0;

                                if (topChoiceScore < tmpScore)
                                {

                                    topChoiceScore = tmpScore;
                                    topChoice = s;
                                }
                            }
                        }
                        if (suffixes.size() > 0 && topChoice.endsWith("ة"))
                        {

                            topChoice = topChoice.substring(0, topChoice.length() - 1) + "ت";
                        }
                        String diacritizedWord = diacritizePrefixes(prefixes) + topChoice + diacritizeSuffixes(suffixes, "");
                        temp.add(diacritizedWord);


                        latice.put(i, temp);
                    }
                    else
                    {

                        temp = new ArrayList<String>();
                        temp.add(w);
                        latice.put(i, temp);
                    }
                }
            }
            else
            {

                temp = new ArrayList<>();
                temp.add("");
                latice.put(i, temp);
            }
            i++;
        }
        temp = new ArrayList<String>();
        // temp.add("</s>");
        
        temp.add(" ");
        latice.put(i, temp);

        return latice;
    }

    public String getProperSegmentation(String input)
    {
        if (hPrefixes.isEmpty())
        {
            for (int i = 0; i < ArabicUtils.prefixes.length; i++)
            {
                hPrefixes.put(ArabicUtils.prefixes[i], 1);
            }
        }
        if (hSuffixes.isEmpty())
        {
            for (int i = 0; i < ArabicUtils.suffixes.length; i++)
            {
                hSuffixes.put(ArabicUtils.suffixes[i], 1);
            }
        }
        String output = "";
        String[] word = input.split("\\+");
        String currentPrefix = "";
        String currentSuffix = "";
        int iValidPrefix = -1;
        while (iValidPrefix + 1 < word.length && hPrefixes.containsKey(word[iValidPrefix + 1]))
        {
            iValidPrefix++;
        }

        int iValidSuffix = word.length;

        while (iValidSuffix > Math.max(iValidPrefix, 0) && (hSuffixes.containsKey(word[iValidSuffix - 1])
                || word[iValidSuffix - 1].equals("_")))
        {
            iValidSuffix--;
        }

        for (int i = 0; i <= iValidPrefix; i++)
        {
            currentPrefix += word[i] + "+";
        }
        String stemPart = "";
        for (int i = iValidPrefix + 1; i < iValidSuffix; i++)
        {
            stemPart += word[i];
        }

        if (iValidSuffix == iValidPrefix)
        {
            iValidSuffix++;
        }

        for (int i = iValidSuffix; i < word.length && iValidSuffix != iValidPrefix; i++)
        {
            currentSuffix += "+" + word[i];
        }

        output = currentPrefix + ";" + stemPart + ";" + currentSuffix;
        return output.replace("++", "+");
    }

}
