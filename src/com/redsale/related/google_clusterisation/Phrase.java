package com.redsale.related.google_clusterisation;

import java.io.Serializable;
import java.util.*;

public class Phrase implements Serializable {
    String text;
    Integer p;
    Integer s;
    Integer m;
    Float e;
    Boolean predicts;
    Set<Integer> canPredict;
    Dictionary<Integer, List<Integer>> positionInDocuments;
    Dictionary<Integer, List<Integer>> documentsIndex;

    public Phrase(String text, Integer document, Integer position, Boolean isInteresting) {
        this.text = text;
        this.p = 1;
        this.s = 1;
        this.m = isInteresting ? 1 : 0;
        this.e = 0F;
        this.predicts = false;
        this.positionInDocuments = new Hashtable<>();
        this.positionInDocuments.put(document, new ArrayList<Integer>(position));
    }

    public void add(Integer document, Integer position, Boolean isInteresting) {
        List<Integer> documentArray = positionInDocuments.get(document);
        if(documentArray == null)
            documentArray = new ArrayList<Integer>(position);
        else
            documentArray.add(position);
        this.positionInDocuments.put(document, documentArray);

        p = positionInDocuments.size();
        s += 1;
        this.m = isInteresting ? m+1 : m;
    }

    public void sum(Phrase other) {
        this.s += other.s;
        this.m += other.s;

        // common documents processing
        List<Integer> duplicate_documents = Collections.list(this.positionInDocuments.keys());
        duplicate_documents.retainAll(Collections.list(other.positionInDocuments.keys()));

        for(Integer document : duplicate_documents) {
            List<Integer> bufPositionInDocuments = this.positionInDocuments.get(document);
            bufPositionInDocuments.addAll(other.positionInDocuments.get(document));
            this.positionInDocuments.put(document, bufPositionInDocuments);
        }

        // other documents processing
        List<Integer> unique_documents = Collections.list(other.positionInDocuments.keys());
        unique_documents.retainAll(duplicate_documents);

        for(Integer document : unique_documents) {
            this.positionInDocuments.put(document, other.positionInDocuments.get(document));
        }
    }

    public String toString() {
        return text + " " + s.toString() + " " + p.toString() + " " + e.toString();
    }
}
