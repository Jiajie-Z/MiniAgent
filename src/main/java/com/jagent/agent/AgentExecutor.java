package com.jagent.agent;

import com.jagent.llm.ChatModel;
import com.jagent.llm.PromptBuilder;
import com.jagent.tool.Tool;
import com.jagent.tool.ToolRegistry;

public class AgentExecutor {
    private static final int DEFAULT_MAX_STEPS = 6;

    private final ChatModel chatModel;
    private final ToolRegistry toolRegistry;
    private final PromptBuilder promptBuilder;

    public AgentExecutor(ChatModel chatModel, ToolRegistry toolRegistry) {
        this.chatModel = chatModel;
        this.toolRegistry = toolRegistry;
        this.promptBuilder = new PromptBuilder();
    }

    public AgentResult run(String userInput) {
        return run(userInput, AgentEventListener.NO_OP);
    }

    public AgentResult run(String userInput, AgentEventListener listener) {
        AgentContext context = new AgentContext(userInput, DEFAULT_MAX_STEPS);
        listener.onEvent(AgentEvent.started(userInput));

        for (int stepIndex = 1; stepIndex <= context.maxSteps(); stepIndex++) {
            String prompt = promptBuilder.build(context, toolRegistry.renderToolDescriptions());
            AgentDecision decision = chatModel.decide(prompt, context);
            listener.onEvent(AgentEvent.thinking(stepIndex, decision.thought()));

            if (decision.state() == AgentState.FINISHED) {
                listener.onEvent(AgentEvent.finished(decision.finalAnswer()));
                return AgentResult.finished(decision.finalAnswer(), context.steps());
            }

            listener.onEvent(AgentEvent.toolCalling(stepIndex, decision.toolName(), decision.toolArguments()));
            String observation = executeToolSafely(decision);
            listener.onEvent(AgentEvent.observation(stepIndex, observation));

            context.addStep(new AgentStep(
                    stepIndex,
                    decision.thought(),
                    decision.toolName(),
                    decision.toolArguments(),
                    observation
            ));
        }

        String message = "Agent reached max steps: " + context.maxSteps();
        listener.onEvent(AgentEvent.failed(message));
        return AgentResult.failed(message, context.steps());
    }

    private String executeToolSafely(AgentDecision decision) {
        try {
            Tool tool = toolRegistry.get(decision.toolName());
            return tool.execute(decision.toolArguments());
        } catch (RuntimeException exception) {
            return "Tool Error: " + exception.getMessage();
        }
    }
}
