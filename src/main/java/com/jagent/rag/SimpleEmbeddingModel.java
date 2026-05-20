package com.jagent.rag;

import java.util.Locale;

public class SimpleEmbeddingModel implements EmbeddingModel {
    private static final int DEFAULT_DIMENSION = 128;
    private final int dimension;

    public SimpleEmbeddingModel() {
        this(DEFAULT_DIMENSION);
    }

    public SimpleEmbeddingModel(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public Embedding embed(String text) {
        double[] vector = new double[dimension];
        for (String token : tokenize(text)) {
            if (token.isBlank()) {
                continue;
            }
            int index = Math.floorMod(token.hashCode(), dimension);
            vector[index] += 1.0;
        }

        normalize(vector);
        return new Embedding(vector);
    }

    @Override
    public int dimension() {
        return dimension;
    }

    private String[] tokenize(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{IsHan}\\p{Alnum}]+", " ")
                .trim()
                .split("\\s+");
    }

    private void normalize(double[] vector) {
        double sum = 0;
        for (double value : vector) {
            sum += value * value;
        }

        if (sum == 0) {
            return;
        }

        double norm = Math.sqrt(sum);
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] / norm;
        }
    }
}
