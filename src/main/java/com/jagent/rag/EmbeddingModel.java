package com.jagent.rag;

import java.util.Map;

public interface EmbeddingModel {
    Map<String, Double> embed(String text);
}
