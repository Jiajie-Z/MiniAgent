package com.jagent.rag;

import java.util.Arrays;

public record Embedding(double[] values) {
    public Embedding {
        values = Arrays.copyOf(values, values.length);
    }

    @Override
    public double[] values() {
        return Arrays.copyOf(values, values.length);
    }

    public int dimension() {
        return values.length;
    }

    public double cosineSimilarity(Embedding other) {
        if (dimension() != other.dimension()) {
            throw new IllegalArgumentException("Embedding dimensions must match.");
        }

        double dot = 0;
        double leftNorm = 0;
        double rightNorm = 0;
        double[] otherValues = other.values;

        for (int i = 0; i < values.length; i++) {
            dot += values[i] * otherValues[i];
            leftNorm += values[i] * values[i];
            rightNorm += otherValues[i] * otherValues[i];
        }

        if (leftNorm == 0 || rightNorm == 0) {
            return 0;
        }
        return dot / (Math.sqrt(leftNorm) * Math.sqrt(rightNorm));
    }
}
