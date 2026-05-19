package com.jagent;

import com.jagent.agent.AgentExecutor;
import com.jagent.agent.AgentResult;
import com.jagent.agent.AgentStep;
import com.jagent.llm.RuleBasedChatModel;
import com.jagent.tool.ToolRegistry;
import com.jagent.tool.builtin.CalculatorTool;
import com.jagent.tool.builtin.TimeTool;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        ToolRegistry toolRegistry = new ToolRegistry();
        toolRegistry.register(new TimeTool());
        toolRegistry.register(new CalculatorTool());

        System.out.println("Available Tools:");
        System.out.println(toolRegistry.renderToolDescriptions());
        System.out.println();

        AgentExecutor executor = new AgentExecutor(new RuleBasedChatModel(), toolRegistry);

        if (args.length > 0) {
            runTask(executor, String.join(" ", args));
            return;
        }

        runInteractive(executor);
    }

    private static void runInteractive(AgentExecutor executor) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("MiniAgent is ready. Type a task, or type exit to quit.");

        while (true) {
            System.out.print("> ");
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit") || userInput.equalsIgnoreCase("quit")) {
                System.out.println("Bye.");
                return;
            }

            if (userInput.isBlank()) {
                continue;
            }

            runTask(executor, userInput);
        }
    }

    private static void runTask(AgentExecutor executor, String userInput) {
        AgentResult result = executor.run(userInput);

        System.out.println("User Input: " + userInput);
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
