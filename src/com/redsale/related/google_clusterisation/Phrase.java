package com.redsale.related.google_clusterisation;

import java.io.Serializable;
import java.util.*;

public class Phrase implements Serializable {
    public String text;
    public Integer p;
    public Integer s;
    public Integer m;
    public Float e;
    public Boolean predicts;
    public Set<Integer> canPredict;
    public Map<Integer, List<Integer>> positionInDocuments;
    public Map<Integer, List<Integer>> documentsIndex;

    public Phrase(String text) {
        this.text = text;
        this.p = 0;
        this.s = 0;
        this.m = 0;
        this.e = 0F;
        this.predicts = false;
        this.positionInDocuments = new HashMap<>();
    }

    public void add(Integer document, Integer position, Boolean isInteresting) {
        List<Integer> documentArray = positionInDocuments.get(document);
        if(documentArray == null)
            documentArray = new LinkedList<>();
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
        Set<Integer> duplicate_documents = new HashSet<>(positionInDocuments.keySet());
        duplicate_documents.retainAll(other.positionInDocuments.keySet());

        for(Integer document : duplicate_documents) {
            List<Integer> bufPositionInDocuments = this.positionInDocuments.get(document);
            bufPositionInDocuments.addAll(other.positionInDocuments.get(document));
            this.positionInDocuments.put(document, bufPositionInDocuments);
        }

        // other documents processing
        Set<Integer> unique_documents = new HashSet<>(other.positionInDocuments.keySet());
        unique_documents.retainAll(duplicate_documents);

        for(Integer document : unique_documents) {
            this.positionInDocuments.put(document, other.positionInDocuments.get(document));
        }
    }

    public String toString() {
        return text + " " + s.toString() + " " + p.toString() + " " + e.toString();
    }
}
