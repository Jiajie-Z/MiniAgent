package com.jagent.rag;

public record DocumentChunk(
        String id,
        String documentId,
        String content
) {
}
