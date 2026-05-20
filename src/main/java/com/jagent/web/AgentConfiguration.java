package com.jagent.web;

import com.jagent.AgentFactory;
import com.jagent.agent.AgentExecutor;
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
}
