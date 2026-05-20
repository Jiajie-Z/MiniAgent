package com.jagent.rag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class InMemoryVectorStore implements VectorStore {
    private final EmbeddingModel embeddingModel;
    private final List<StoredChunk> chunks = new ArrayList<>();

    public InMemoryVectorStore(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public void add(DocumentChunk chunk) {
        chunks.add(new StoredChunk(chunk, embeddingModel.embed(chunk.content())));
    }

    @Override
    public List<SearchResult> search(String query, int topK) {
        Map<String, Double> queryVector = embeddingModel.embed(query);
        return chunks.stream()
                .map(chunk -> new SearchResult(chunk.chunk(), cosineSimilarity(queryVector, chunk.embedding())))
                .filter(result -> result.score() > 0)
                .sorted(Comparator.comparingDouble(SearchResult::score).reversed())
                .limit(topK)
                .toList();
    }

    private double cosineSimilarity(Map<String, Double> left, Map<String, Double> right) {
        double dot = 0;
        for (Map.Entry<String, Double> entry : left.entrySet()) {
            dot += entry.getValue() * right.getOrDefault(entry.getKey(), 0.0);
        }

        double leftNorm = norm(left);
        double rightNorm = norm(right);
        if (leftNorm == 0 || rightNorm == 0) {
            return 0;
        }
        return dot / (leftNorm * rightNorm);
    }

    private double norm(Map<String, Double> vector) {
        double sum = 0;
        for (double value : vector.values()) {
            sum += value * value;
        }
        return Math.sqrt(sum);
    }

    private record StoredChunk(DocumentChunk chunk, Map<String, Double> embedding) {
    }
}
