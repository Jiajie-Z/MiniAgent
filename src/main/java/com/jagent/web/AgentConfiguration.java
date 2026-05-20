package com.jagent.web;

import com.jagent.AgentFactory;
import com.jagent.agent.AgentExecutor;
import com.jagent.log.AgentRunLogRepository;
import com.jagent.log.AgentRunLogService;
import com.jagent.log.InMemoryAgentRunLogRepository;
import com.jagent.rag.EmbeddingModel;
import com.jagent.rag.InMemoryVectorStore;
import com.jagent.rag.PgVectorStore;
import com.jagent.rag.Retriever;
import com.jagent.rag.SimpleEmbeddingModel;
import com.jagent.rag.TextSplitter;
import com.jagent.rag.VectorStore;
import com.jagent.tool.ToolRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AgentConfiguration {
    @Bean
    public AgentFactory agentFactory() {
        return new AgentFactory();
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return new SimpleEmbeddingModel();
    }

    @Bean
    public VectorStore vectorStore(
            @Value("${miniagent.rag.store:in-memory}") String storeType,
            JdbcTemplate jdbcTemplate,
            EmbeddingModel embeddingModel
    ) {
        if ("pgvector".equalsIgnoreCase(storeType)) {
            return new PgVectorStore(jdbcTemplate, embeddingModel);
        }
        return new InMemoryVectorStore(embeddingModel);
    }

    @Bean
    public Retriever retriever(
            AgentFactory agentFactory,
            VectorStore vectorStore,
            @Value("${miniagent.rag.top-k:3}") int topK,
            @Value("${miniagent.rag.chunk-size:500}") int chunkSize
    ) {
        agentFactory.loadKnowledgeBase(vectorStore, new TextSplitter(chunkSize));
        return new Retriever(vectorStore, topK);
    }

    @Bean
    public ToolRegistry toolRegistry(AgentFactory agentFactory, Retriever retriever) {
        return agentFactory.createToolRegistry(retriever);
    }

    @Bean
    public AgentExecutor agentExecutor(AgentFactory agentFactory, ToolRegistry toolRegistry) {
        return agentFactory.createAgentExecutor(toolRegistry);
    }

    @Bean
    public AgentRunLogRepository agentRunLogRepository() {
        return new InMemoryAgentRunLogRepository();
    }

    @Bean
    public AgentRunLogService agentRunLogService(AgentExecutor agentExecutor, AgentRunLogRepository repository) {
        return new AgentRunLogService(agentExecutor, repository);
    }
}
