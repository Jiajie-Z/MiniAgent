package com.jagent.rag;

import java.util.ArrayList;
import java.util.List;

public class TextSplitter {
    private final int maxChunkLength;

    public TextSplitter(int maxChunkLength) {
        this.maxChunkLength = maxChunkLength;
    }

    public List<DocumentChunk> split(Document document) {
        List<DocumentChunk> chunks = new ArrayList<>();
        String[] paragraphs = document.content().split("\\R\\s*\\R");

        int index = 1;
        for (String paragraph : paragraphs) {
            String normalized = paragraph.trim();
            if (normalized.isEmpty()) {
                continue;
            }

            int start = 0;
            while (start < normalized.length()) {
                int end = Math.min(start + maxChunkLength, normalized.length());
                String content = normalized.substring(start, end).trim();
                if (!content.isEmpty()) {
                    chunks.add(new DocumentChunk(document.id() + "-chunk-" + index, document.id(), content));
                    index++;
                }
                start = end;
            }
        }

        return chunks;
    }
}
