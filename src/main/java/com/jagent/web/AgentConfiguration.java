package com.jagent.web;

import com.jagent.AgentFactory;
import com.jagent.agent.AgentExecutor;
import com.jagent.log.AgentRunLogRepository;
import com.jagent.log.AgentRunLogService;
import com.jagent.log.InMemoryAgentRunLogRepository;
import com.jagent.tool.ToolRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentConfiguration {
    @Bean
    public AgentFactory agentFactory() {
        return new AgentFactory();
    }

    @Bean
    public ToolRegistry toolRegistry(AgentFactory agentFactory) {
        return agentFactory.createToolRegistry();
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
