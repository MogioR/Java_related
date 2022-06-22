package com.redsale.related;
import com.redsale.related.google_clusterisation.RelatedPhrases;
import stemmer.StemmerPorterRU;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("resources/results_download_2.txt");
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
                    "Phrases: " + relatedPhrases.phrases.size() + "\t" +
                    "Progress: " + relatedPhrases.documentsCount/14828F);
        }

        long finish = System.currentTimeMillis();
        long elapsed = finish - start;
        System.out.println("Time: " + elapsed);

        System.out.println("Mark goods");
        relatedPhrases.markGoods();

        finish = System.currentTimeMillis();
        elapsed = finish - start;
        System.out.println("Time: " + elapsed);

        System.out.println("Generate g matrix");
        relatedPhrases.generateGMatrix();

        finish = System.currentTimeMillis();
        elapsed = finish - start;
        System.out.println("Time: " + elapsed);

        System.out.println("\nStats: ");
        System.out.println("Documents count: " + relatedPhrases.documentsCount);
        System.out.println("All phrases: " + relatedPhrases.phrases.size());
        System.out.println("Good phrases: " + relatedPhrases.goodPhrases.size());

        BufferedWriter writer = new BufferedWriter(new FileWriter("results/g_matrix.html", true));
        StringBuilder output = new StringBuilder();
        output.append("<table>");
        output.append("<tr>");
        output.append("<td>");
        output.append("</td>");
        for (i = 0; i < relatedPhrases.goodPhrases.size(); i++) {
            output.append("<td>");
            output.append(relatedPhrases.goodPhrases.get(i).text);
            output.append("</td>");
        }
        output.append("</tr>");
        writer.append(output);
        output.delete(0, output.length()-1);
        for (i = 0; i < relatedPhrases.goodPhrases.size(); i++) {
            output.append("<tr>");
            output.append("<td>");
            output.append(relatedPhrases.goodPhrases.get(i).text);
            output.append("</td>");
            for (int j = 0; j < relatedPhrases.goodPhrases.size(); j++) {
                output.append("<td>");
                output.append(relatedPhrases.g_matrix.get(i).get(j));
                output.append("</td>");
            }
            output.append("</tr>");
            writer.append(output);
            output.delete(0, output.length()-1);
        }
        output.append("</table>");
        writer.append(output);
        writer.close();
    }
}
