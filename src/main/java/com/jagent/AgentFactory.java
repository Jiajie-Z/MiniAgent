package com.jagent;

import com.jagent.agent.AgentExecutor;
import com.jagent.llm.RuleBasedChatModel;
import com.jagent.rag.InMemoryVectorStore;
import com.jagent.rag.KnowledgeBaseLoader;
import com.jagent.rag.Retriever;
import com.jagent.rag.SimpleEmbeddingModel;
import com.jagent.rag.TextSplitter;
import com.jagent.rag.VectorStore;
import com.jagent.tool.ToolRegistry;
import com.jagent.tool.builtin.CalculatorTool;
import com.jagent.tool.builtin.RagSearchTool;
import com.jagent.tool.builtin.TimeTool;

public class AgentFactory {
    public ToolRegistry createToolRegistry() {
        ToolRegistry toolRegistry = new ToolRegistry();
        toolRegistry.register(new TimeTool());
        toolRegistry.register(new CalculatorTool());
        toolRegistry.register(new RagSearchTool(createRetriever()));
        return toolRegistry;
    }

    public AgentExecutor createAgentExecutor(ToolRegistry toolRegistry) {
        return new AgentExecutor(new RuleBasedChatModel(), toolRegistry);
    }

    private Retriever createRetriever() {
        VectorStore vectorStore = new InMemoryVectorStore(new SimpleEmbeddingModel());
        TextSplitter splitter = new TextSplitter(500);

        for (var document : new KnowledgeBaseLoader("knowledge").load()) {
            splitter.split(document).forEach(vectorStore::add);
        }

        return new Retriever(vectorStore, 3);
    }
}
