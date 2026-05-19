package com.jagent.llm;

import com.jagent.agent.AgentContext;
import com.jagent.agent.AgentDecision;
import com.jagent.agent.AgentStep;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RuleBasedChatModel implements ChatModel {
    private static final Pattern ADDITION_PATTERN = Pattern.compile("(\\d+)\\s*\\+\\s*(\\d+)");

    @Override
    public AgentDecision decide(AgentContext context) {
        if (!context.steps().isEmpty()) {
            AgentStep lastStep = context.steps().get(context.steps().size() - 1);
            return AgentDecision.finish(
                    "已经拿到工具返回结果，可以生成最终回答。",
                    "Tool " + lastStep.toolName() + " returned: " + lastStep.observation()
            );
        }

        String input = context.userInput();
        Matcher additionMatcher = ADDITION_PATTERN.matcher(input);
        if (additionMatcher.find()) {
            return AgentDecision.callTool(
                    "用户需要计算加法，应该调用计算器工具。",
                    "calculator",
                    additionMatcher.group(1) + "+" + additionMatcher.group(2)
            );
        }

        if (input.contains("几点") || input.contains("时间")) {
            return AgentDecision.callTool(
                    "用户询问当前时间，应该调用时间工具。",
                    "time",
                    ""
            );
        }

        return AgentDecision.finish("不需要调用工具，可以直接回答。", "我目前只能演示时间查询和简单加法。");
    }
}
