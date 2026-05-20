package com.jagent.rag;

public record SearchResult(
        DocumentChunk chunk,
        double score
) {
}
