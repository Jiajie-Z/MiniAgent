package com.jagent;

import com.jagent.agent.AgentExecutor;
import com.jagent.llm.RuleBasedChatModel;
import com.jagent.tool.ToolRegistry;
import com.jagent.tool.builtin.CalculatorTool;
import com.jagent.tool.builtin.TimeTool;

public class AgentFactory {
    public ToolRegistry createToolRegistry() {
        ToolRegistry toolRegistry = new ToolRegistry();
        toolRegistry.register(new TimeTool());
        toolRegistry.register(new CalculatorTool());
        return toolRegistry;
    }

    public AgentExecutor createAgentExecutor(ToolRegistry toolRegistry) {
        return new AgentExecutor(new RuleBasedChatModel(), toolRegistry);
    }
}
