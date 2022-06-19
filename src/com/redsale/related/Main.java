package com.redsale.related;
import com.redsale.related.google_clusterisation.RelatedPhrases;
import stemmer.StemmerPorterRU;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("resources/results_download.txt");
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);

        RelatedPhrases relatedPhrases = new RelatedPhrases();

        System.out.println("Add documents");
        long start = System.currentTimeMillis();
        String line = reader.readLine();
        int i = 0;
        while (line != null) {
            relatedPhrases.addDocument(i++, line);
            line = reader.readLine();
            System.out.println("Document: " + relatedPhrases.documentsCount + "\t" +
                    "Phrases: " + relatedPhrases.phrases.size());
        }

        long finish = System.currentTimeMillis();
        long elapsed = finish - start;
        System.out.println("Time: " + elapsed);

        System.out.println("Mark goods");
        relatedPhrases.markGoods();

        finish = System.currentTimeMillis();
        elapsed = finish - start;
        System.out.println("Time: " + elapsed);

        System.out.println("\nStats: ");
        System.out.println("Documents count: " + relatedPhrases.documentsCount);
        System.out.println("All phrases: " + relatedPhrases.phrases.size());
        System.out.println("Good phrases: " + relatedPhrases.goodPhrases.size());
    }
}
