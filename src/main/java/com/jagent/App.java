package com.jagent;

import com.jagent.agent.AgentExecutor;
import com.jagent.llm.RuleBasedChatModel;
import com.jagent.tool.ToolRegistry;
import com.jagent.tool.builtin.CalculatorTool;
import com.jagent.tool.builtin.TimeTool;

public class App {
    public static void main(String[] args) {
        ToolRegistry toolRegistry = new ToolRegistry();
        toolRegistry.register(new TimeTool());
        toolRegistry.register(new CalculatorTool());

        AgentExecutor executor = new AgentExecutor(new RuleBasedChatModel(), toolRegistry);

        System.out.println("Task 1:");
        System.out.println(executor.run("现在几点了").finalAnswer());

        System.out.println();
        System.out.println("Task 2:");
        System.out.println(executor.run("帮我计算 12 + 30").finalAnswer());
    }
}
