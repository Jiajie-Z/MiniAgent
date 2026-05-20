package com.jagent.rag;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PgVectorStore implements VectorStore {
    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingModel embeddingModel;

    public PgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public void clear() {
        jdbcTemplate.update("DELETE FROM document_chunks");
    }

    @Override
    public void add(DocumentChunk chunk) {
        Embedding embedding = embeddingModel.embed(chunk.content());
        jdbcTemplate.update("""
                        INSERT INTO document_chunks (id, document_id, content, embedding)
                        VALUES (?, ?, ?, ?::vector)
                        ON CONFLICT (id) DO UPDATE SET
                            document_id = EXCLUDED.document_id,
                            content = EXCLUDED.content,
                            embedding = EXCLUDED.embedding
                        """,
                chunk.id(),
                chunk.documentId(),
                chunk.content(),
                toPgVector(embedding)
        );
    }

    @Override
    public List<SearchResult> search(String query, int topK) {
        String queryVector = toPgVector(embeddingModel.embed(query));
        return jdbcTemplate.execute((ConnectionCallback<List<SearchResult>>) connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute("SET ivfflat.probes = 100");
            }

            try (PreparedStatement statement = connection.prepareStatement("""
                    SELECT id, document_id, content, 1 - (embedding <=> ?::vector) AS score
                    FROM document_chunks
                    ORDER BY embedding <=> ?::vector
                    LIMIT ?
                    """)) {
                statement.setString(1, queryVector);
                statement.setString(2, queryVector);
                statement.setInt(3, topK);

                List<SearchResult> results = new ArrayList<>();
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        double score = resultSet.getDouble("score");
                        if (score <= 0) {
                            continue;
                        }
                        results.add(new SearchResult(
                                new DocumentChunk(
                                        resultSet.getString("id"),
                                        resultSet.getString("document_id"),
                                        resultSet.getString("content")
                                ),
                                score
                        ));
                    }
                }
                return results;
            }
        });
    }

    private String toPgVector(Embedding embedding) {
        double[] values = embedding.values();
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(values[i]);
        }
        return builder.append(']').toString();
    }
}
