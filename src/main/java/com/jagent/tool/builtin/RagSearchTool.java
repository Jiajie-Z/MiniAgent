package com.jagent.tool.builtin;

import com.jagent.rag.Retriever;
import com.jagent.rag.SearchResult;
import com.jagent.tool.Tool;

import java.util.List;

public class RagSearchTool implements Tool {
    private final Retriever retriever;

    public RagSearchTool(Retriever retriever) {
        this.retriever = retriever;
    }

    @Override
    public String name() {
        return "rag_search";
    }

    @Override
    public String description() {
        return "Search the local knowledge base and return relevant document chunks.";
    }

    @Override
    public String parametersSchema() {
        return "A natural language query for the local knowledge base.";
    }

    @Override
    public String execute(String arguments) {
        List<SearchResult> results = retriever.retrieve(arguments);
        if (results.isEmpty()) {
            return "No relevant knowledge found.";
        }

        StringBuilder observation = new StringBuilder();
        for (int i = 0; i < results.size(); i++) {
            SearchResult result = results.get(i);
            observation.append("Result ")
                    .append(i + 1)
                    .append(" (score=")
                    .append(String.format("%.3f", result.score()))
                    .append("): ")
                    .append(result.chunk().content());
            if (i < results.size() - 1) {
                observation.append(System.lineSeparator());
            }
        }
        return observation.toString();
    }
}
