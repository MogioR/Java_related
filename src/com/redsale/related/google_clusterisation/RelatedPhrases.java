package com.redsale.related.google_clusterisation;

import stemmer.StemmerPorterRU;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;


public class RelatedPhrases {
    Integer windowOneSize = 5;
    Integer windowTwoSize = 30;
    Float pThreshold = 0.1f;
    Float sThreshold = 0.2f;
    Float iThreshold = 1.1f;
    Float relatedThreshold = 1.2f;

    Integer maxPhrasesInBank = 200000;
    Integer phrase_banks_count = 1;
    Integer phrase_current_bank = 0;

    public Integer documentsCount = 0;
    public Dictionary<String, Phrase> phrases = new Hashtable<>();
    public ArrayList<Phrase> goodPhrases = new ArrayList<>();
    public ArrayList<ArrayList<Float>> g_matrix;

    public RelatedPhrases() {

    }

    public void addDocument(Integer document, String text) throws IOException {
        // Text normalisation
        text = text.replaceAll("[^\\da-zA-Zа-яёА-ЯЁ .!?]", " "); // Del wrong cymbals
        text = textDelFreeNum(text); // Del free num
        text = textWordsSeparate(text); // Separate words
        text = text.replaceAll("[\\s]{2,}", " "); // Del free spaces

        // Text processing
        String[] sentences = text.split("[.!?]");
        int wordsCount = 0;
        for(String sentence : sentences) {
            sentence = StemmerPorterRU.stem(sentence);
            String[] words = sentence.strip().split(" ");
            for (int i = 0; i < words.length; i++) {
                for (int j = 1; j < windowOneSize; j++) {
                    // Gen new phrase
                    String phrase_text = "";
                    for (int l = 0; l < j && i + l < words.length; l++)
                        phrase_text = phrase_text.concat(" " + words[i + l]);

                    // Add phrase to base
                    if (phrase_text.length() > 0) {
                        Phrase buf = phrases.get(phrase_text);
                        if (buf == null)
                            buf = new Phrase(phrase_text, document, wordsCount, false);
                        else
                            buf.add(document, wordsCount, false);
                        phrases.put(phrase_text, buf);

                        if(phrases.size() > maxPhrasesInBank) {
                            FileOutputStream fout = new FileOutputStream(
                                    "banks\\bank_"+(phrase_banks_count-1) + ".bin");
                            ObjectOutputStream oos = new ObjectOutputStream(fout);
                            oos.writeObject(phrases);
                            phrases = new Hashtable<>();
                            phrase_banks_count += 1;
                        }
                    }
                }
                wordsCount += 1;
            }
        }
        documentsCount += 1;
    }

    private String textWordsSeparate(String text) {
        String[] buf = text.split("(?=([a-z][A-Z])|([а-яё][А-ЯЁ]))");
        String newText = buf[0];
        for(int i = 1; i<buf.length; i++) {
            newText = newText.concat(String.valueOf(buf[i].charAt(0)) + ' ');
            for(int j = 1; j < buf[i].length(); j ++)
                newText = newText.concat(String.valueOf(buf[i].charAt(j)));
        }
        return newText;
    }

    private String textDelFreeNum(String text) {
        String new_text = text.replaceAll("\\s[0-9]*\\s", " ");
        while (!new_text.equals(text)) {
            text = new_text;
            new_text = text.replaceAll("\\s[0-9]*\\s", " ");
        }
        return new_text;
    }

    public void markGoods() {
        Enumeration<Phrase> phrases = this.phrases.elements();
        while (phrases.hasMoreElements()) {
            Phrase phrase = phrases.nextElement();
            if(Float.valueOf(phrase.p) / documentsCount>= pThreshold &&
                    Float.valueOf(phrase.s)/documentsCount >= sThreshold) {
                phrase.e = Float.valueOf(phrase.p) / documentsCount;
                goodPhrases.add(phrase);
            }
        }
    }

    public void generateGMatrix() {

    }
}
