package com.jagent.agent;

import com.jagent.llm.ChatModel;
import com.jagent.tool.Tool;
import com.jagent.tool.ToolRegistry;

public class AgentExecutor {
    private static final int DEFAULT_MAX_STEPS = 6;

    private final ChatModel chatModel;
    private final ToolRegistry toolRegistry;

    public AgentExecutor(ChatModel chatModel, ToolRegistry toolRegistry) {
        this.chatModel = chatModel;
        this.toolRegistry = toolRegistry;
    }

    public AgentResult run(String userInput) {
        AgentContext context = new AgentContext(userInput, DEFAULT_MAX_STEPS);

        for (int stepIndex = 1; stepIndex <= context.maxSteps(); stepIndex++) {
            AgentDecision decision = chatModel.decide(context);

            if (decision.state() == AgentState.FINISHED) {
                return AgentResult.finished(decision.finalAnswer(), context.steps());
            }

            Tool tool = toolRegistry.get(decision.toolName());
            String observation = tool.execute(decision.toolArguments());

            context.addStep(new AgentStep(
                    stepIndex,
                    decision.thought(),
                    decision.toolName(),
                    decision.toolArguments(),
                    observation
            ));
        }

        return AgentResult.failed("Agent reached max steps: " + context.maxSteps(), context.steps());
    }
}
