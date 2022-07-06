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

    public Integer documentsCount = 0;
    public Map<String, Phrase> phrases = new HashMap<>();
    public List<Phrase> goodPhrases = new LinkedList<>();
    public List<List<Float>> g_matrix = new ArrayList<>();

    public RelatedPhrases() {

    }

    public void addDocument(Integer document, String text) {
        // Text normalisation
        if (text.contains("%PDF") || text.contains("Word.Document"))
            return;

        // Text normalisation
        text = text.replaceAll("[^\\da-zA-Zа-яёА-ЯЁ .!?]", " "); // Del wrong cymbals
        text = textWordsSeparate(text); // Separate words
        text = textDelFreeNum(text); // Del free num
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
                    StringBuilder phrase_text = new StringBuilder();
                    for (int l = 0; l < j && i + l < words.length; l++)
                        phrase_text.append(" ").append(words[i + l]);

                    // Add phrase to base
                    if (phrase_text.length() > 0) {
                        String key = phrase_text.toString().strip();
                        Phrase buf = phrases.get(key);
                        if (buf == null)
                            buf = new Phrase(key);
                        buf.add(document, wordsCount, false);
                        phrases.put(key, buf);
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
        for (Phrase phrase : this.phrases.values()) {
            if(Float.valueOf(phrase.p) / documentsCount>= pThreshold &&
                    Float.valueOf(phrase.s)/documentsCount >= sThreshold) {
                phrase.e = Float.valueOf(phrase.p) / documentsCount;
                goodPhrases.add(phrase);
            }
        }
    }

    public void generateGMatrix() {
        g_matrix.clear();
        for (int i = 0; i < goodPhrases.size(); i++) {
            g_matrix.add(new ArrayList<>());
            Phrase phrase_one = goodPhrases.get(i);
            for (Phrase phrase_two : this.goodPhrases) {
                // Documents with a both of goods
                Set<Integer> duplicate_documents = new HashSet<>(phrase_one.positionInDocuments.keySet());
                duplicate_documents.retainAll(phrase_two.positionInDocuments.keySet());

                // Count of intersection
                int R = 0;
                for(Integer document : duplicate_documents) {
                    for(Integer pos_one : phrase_one.positionInDocuments.get(document))
                        for(Integer pos_two : phrase_one.positionInDocuments.get(document)) {
                            if (Math.abs(pos_one - pos_two) <= windowTwoSize)
                                R += 1;
                        }
                }
                // Add to matrix
                float E = phrase_one.e * phrase_two.e;
                float A = R / Float.valueOf(documentsCount);
                float I = A / E;
                g_matrix.get(i).add(I);
            }
        }
    }
}
