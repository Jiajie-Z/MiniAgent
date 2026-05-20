package com.jagent.rag;

import java.util.List;

public class Retriever {
    private final VectorStore vectorStore;
    private final int topK;

    public Retriever(VectorStore vectorStore, int topK) {
        this.vectorStore = vectorStore;
        this.topK = topK;
    }

    public List<SearchResult> retrieve(String query) {
        return vectorStore.search(query, topK);
    }
}
