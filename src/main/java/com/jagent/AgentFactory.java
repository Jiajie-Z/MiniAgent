package com.jagent;

import com.jagent.agent.AgentExecutor;
import com.jagent.llm.RuleBasedChatModel;
import com.jagent.rag.Document;
import com.jagent.rag.InMemoryVectorStore;
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

        for (Document document : builtInDocuments()) {
            splitter.split(document).forEach(vectorStore::add);
        }

        return new Retriever(vectorStore, 3);
    }

    private Document[] builtInDocuments() {
        return new Document[]{
                new Document("mini-agent-overview", """
                        MiniAgent is a lightweight Java Agent Runtime. It supports a ReAct-style Thought, Action, Observation, Final Answer loop, tool registration, multi-step task execution, SSE event streaming, execution logs, unified REST API responses, input validation, and Docker deployment.
                        """),
                new Document("mini-agent-tools", """
                        MiniAgent provides a Tool interface and ToolRegistry. Built-in tools include time lookup, simple calculator, and rag_search. Tool errors are converted into Observation messages so the Agent can continue reasoning or explain failures.
                        """),
                new Document("mini-agent-rag", """
                        The RAG module contains document chunks, a text splitter, a simple embedding model, an in-memory vector store, and a retriever. The rag_search tool exposes retrieval results to the Agent as an Observation.
                        """)
        };
    }
}
