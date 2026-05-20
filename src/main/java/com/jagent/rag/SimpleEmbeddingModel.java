package com.jagent.rag;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SimpleEmbeddingModel implements EmbeddingModel {
    @Override
    public Map<String, Double> embed(String text) {
        Map<String, Double> vector = new HashMap<>();
        for (String token : tokenize(text)) {
            if (token.isBlank()) {
                continue;
            }
            vector.merge(token, 1.0, Double::sum);
        }
        return vector;
    }

    private String[] tokenize(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{IsHan}\\p{Alnum}]+", " ")
                .trim()
                .split("\\s+");
    }
}
