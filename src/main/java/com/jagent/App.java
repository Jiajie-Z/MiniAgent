package com.jagent;

import com.jagent.agent.AgentExecutor;
import com.jagent.agent.AgentResult;
import com.jagent.agent.AgentStep;
import com.jagent.llm.RuleBasedChatModel;
import com.jagent.tool.ToolRegistry;
import com.jagent.tool.builtin.CalculatorTool;
import com.jagent.tool.builtin.TimeTool;

public class App {
    public static void main(String[] args) {
        ToolRegistry toolRegistry = new ToolRegistry();
        toolRegistry.register(new TimeTool());
        toolRegistry.register(new CalculatorTool());

        System.out.println("Available Tools:");
        System.out.println(toolRegistry.renderToolDescriptions());
        System.out.println();

        AgentExecutor executor = new AgentExecutor(new RuleBasedChatModel(), toolRegistry);
        runTask(executor, "Task 1", "现在几点了");
        runTask(executor, "Task 2", "帮我计算 12 + 30");
    }

    private static void runTask(AgentExecutor executor, String title, String userInput) {
        AgentResult result = executor.run(userInput);

        System.out.println(title + ": " + userInput);
        for (AgentStep step : result.steps()) {
            System.out.println("Thought: " + step.thought());
            System.out.println("Action: " + step.toolName());
            System.out.println("Action Input: " + step.toolArguments());
            System.out.println("Observation: " + step.observation());
        }
        System.out.println("Final Answer: " + result.finalAnswer());
        System.out.println();
    }
}
