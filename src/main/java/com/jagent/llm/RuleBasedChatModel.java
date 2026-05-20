package com.jagent.llm;

import com.jagent.agent.AgentContext;
import com.jagent.agent.AgentDecision;
import com.jagent.agent.AgentStep;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleBasedChatModel implements ChatModel {
    private static final Pattern ADDITION_PATTERN = Pattern.compile("(\\d+)\\s*\\+\\s*(\\d+)");
    private static final Pattern SUBTRACTION_PATTERN = Pattern.compile("(\\d+)\\s*-\\s*(\\d+)");
    private final ReActDecisionParser parser = new ReActDecisionParser();

    @Override
    public AgentDecision decide(String prompt, AgentContext context) {
        return parser.parse(generateReActResponse(context));
    }

    private String generateReActResponse(AgentContext context) {
        AgentStep failedStep = findFailedStep(context);
        if (failedStep != null) {
            return """
                    Thought: The tool call failed, so I should explain the failure to the user.
                    Final Answer: I could not complete the task because %s
                    """.formatted(failedStep.observation());
        }

        String input = context.userInput();
        Matcher additionMatcher = ADDITION_PATTERN.matcher(input);
        Matcher subtractionMatcher = SUBTRACTION_PATTERN.matcher(input);

        if (hasRagRequest(input) && !hasExecutedTool(context, "rag_search")) {
            return """
                    Thought: The user is asking about knowledge base content, so I should search the local RAG knowledge base.
                    Action: rag_search
                    Action Input: %s
                    """.formatted(input);
        }

        if (hasTimeRequest(input) && !hasExecutedTool(context, "time")) {
            return """
                    Thought: The user asks for the current time, and I have not called the time tool yet.
                    Action: time
                    Action Input:
                    """;
        }

        if (additionMatcher.find() && !hasExecutedToolWithArguments(context, "calculator",
                additionMatcher.group(1) + "+" + additionMatcher.group(2))) {
            return """
                    Thought: The user asks for an addition calculation, and I still need to call the calculator tool for it.
                    Action: calculator
                    Action Input: %s+%s
                    """.formatted(additionMatcher.group(1), additionMatcher.group(2));
        }

        if (subtractionMatcher.find() && !hasExecutedToolWithArguments(context, "calculator",
                subtractionMatcher.group(1) + "-" + subtractionMatcher.group(2))) {
            return """
                    Thought: The user is asking for a subtraction calculation, but I will try the calculator tool and observe the result.
                    Action: calculator
                    Action Input: %s-%s
                    """.formatted(subtractionMatcher.group(1), subtractionMatcher.group(2));
        }

        if (!context.steps().isEmpty()) {
            return """
                    Thought: I have completed all tool calls required by the user and can summarize the observations.
                    Final Answer: %s
                    """.formatted(summarizeObservations(context));
        }

        return """
                Thought: This request does not require a tool supported by the current demo.
                Final Answer: I can currently demonstrate time lookup and simple addition.
                """;
    }

    private boolean hasTimeRequest(String input) {
        String normalizedInput = input.toLowerCase();
        return input.contains("几点") || input.contains("时间") || normalizedInput.contains("time");
    }

    private boolean hasRagRequest(String input) {
        String normalizedInput = input.toLowerCase();
        return input.contains("知识库")
                || normalizedInput.contains("knowledge")
                || normalizedInput.contains("miniagent")
                || input.contains("支持什么功能")
                || input.contains("功能");
    }

    private boolean hasExecutedTool(AgentContext context, String toolName) {
        return context.steps().stream().anyMatch(step -> step.toolName().equals(toolName));
    }

    private boolean hasExecutedToolWithArguments(AgentContext context, String toolName, String arguments) {
        return context.steps().stream()
                .anyMatch(step -> step.toolName().equals(toolName) && step.toolArguments().equals(arguments));
    }

    private AgentStep findFailedStep(AgentContext context) {
        return context.steps().stream()
                .filter(step -> step.observation().startsWith("Tool Error:"))
                .findFirst()
                .orElse(null);
    }

    private String summarizeObservations(AgentContext context) {
        StringBuilder answer = new StringBuilder();
        for (AgentStep step : context.steps()) {
            if (!answer.isEmpty()) {
                answer.append(" ");
            }
            answer.append("Tool ")
                    .append(step.toolName())
                    .append(" returned: ")
                    .append(step.observation())
                    .append(".");
        }
        return answer.toString();
    }
}
