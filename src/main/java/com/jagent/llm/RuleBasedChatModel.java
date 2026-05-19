package com.jagent.llm;

import com.jagent.agent.AgentContext;
import com.jagent.agent.AgentDecision;
import com.jagent.agent.AgentStep;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleBasedChatModel implements ChatModel {
    private static final Pattern ADDITION_PATTERN = Pattern.compile("(\\d+)\\s*\\+\\s*(\\d+)");
    private final ReActDecisionParser parser = new ReActDecisionParser();

    @Override
    public AgentDecision decide(String prompt, AgentContext context) {
        return parser.parse(generateReActResponse(context));
    }

    private String generateReActResponse(AgentContext context) {
        if (!context.steps().isEmpty()) {
            AgentStep lastStep = context.steps().get(context.steps().size() - 1);
            return """
                    Thought: I have received the tool observation and can now answer the user.
                    Final Answer: Tool %s returned: %s
                    """.formatted(lastStep.toolName(), lastStep.observation());
        }

        String input = context.userInput();
        Matcher additionMatcher = ADDITION_PATTERN.matcher(input);
        if (additionMatcher.find()) {
            return """
                    Thought: The user is asking for an addition calculation, so I should use the calculator tool.
                    Action: calculator
                    Action Input: %s+%s
                    """.formatted(additionMatcher.group(1), additionMatcher.group(2));
        }

        if (input.contains("几点") || input.contains("时间")) {
            return """
                    Thought: The user is asking for the current time, so I should use the time tool.
                    Action: time
                    Action Input:
                    """;
        }

        return """
                Thought: This request does not require a tool supported by the current demo.
                Final Answer: I can currently demonstrate time lookup and simple addition.
                """;
    }
}
