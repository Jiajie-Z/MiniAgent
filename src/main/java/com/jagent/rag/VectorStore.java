package com.jagent.rag;

import java.util.List;

public interface VectorStore {
    void clear();

    void add(DocumentChunk chunk);

    List<SearchResult> search(String query, int topK);
}
