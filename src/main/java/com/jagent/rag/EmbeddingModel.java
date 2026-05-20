package com.jagent.rag;

public interface EmbeddingModel {
    Embedding embed(String text);

    int dimension();
}
