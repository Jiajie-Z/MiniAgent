package com.jagent.llm;

import com.jagent.agent.AgentContext;
import com.jagent.agent.AgentStep;

public class PromptBuilder {
    public String build(AgentContext context, String toolDescriptions) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("""
                You are a ReAct-style agent.
                Decide the next step by using exactly one of the following formats.

                To call a tool:
                Thought: <why this tool is needed>
                Action: <tool name>
                Action Input: <tool arguments>

                To finish:
                Thought: <why you can answer now>
                Final Answer: <answer to the user>

                Available tools:
                """);
        prompt.append(toolDescriptions).append(System.lineSeparator()).append(System.lineSeparator());
        prompt.append("User Input: ").append(context.userInput()).append(System.lineSeparator());

        if (!context.steps().isEmpty()) {
            prompt.append(System.lineSeparator()).append("Previous steps:").append(System.lineSeparator());
            for (AgentStep step : context.steps()) {
                prompt.append("Thought: ").append(step.thought()).append(System.lineSeparator());
                prompt.append("Action: ").append(step.toolName()).append(System.lineSeparator());
                prompt.append("Action Input: ").append(step.toolArguments()).append(System.lineSeparator());
                prompt.append("Observation: ").append(step.observation()).append(System.lineSeparator());
            }
        }

        return prompt.toString().trim();
    }
}
