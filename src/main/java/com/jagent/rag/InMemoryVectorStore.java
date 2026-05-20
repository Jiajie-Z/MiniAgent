package com.jagent.rag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InMemoryVectorStore implements VectorStore {
    private final EmbeddingModel embeddingModel;
    private final List<StoredChunk> chunks = new ArrayList<>();

    public InMemoryVectorStore(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public void clear() {
        chunks.clear();
    }

    @Override
    public void add(DocumentChunk chunk) {
        chunks.add(new StoredChunk(chunk, embeddingModel.embed(chunk.content())));
    }

    @Override
    public List<SearchResult> search(String query, int topK) {
        Embedding queryEmbedding = embeddingModel.embed(query);
        return chunks.stream()
                .map(chunk -> new SearchResult(chunk.chunk(), queryEmbedding.cosineSimilarity(chunk.embedding())))
                .filter(result -> result.score() > 0)
                .sorted(Comparator.comparingDouble(SearchResult::score).reversed())
                .limit(topK)
                .toList();
    }

    private record StoredChunk(DocumentChunk chunk, Embedding embedding) {
    }
}
