package com.jagent;

import com.jagent.agent.AgentExecutor;
import com.jagent.agent.AgentEvent;
import com.jagent.agent.AgentResult;
import com.jagent.tool.ToolRegistry;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        AgentFactory agentFactory = new AgentFactory();
        ToolRegistry toolRegistry = agentFactory.createToolRegistry();

        System.out.println("Available Tools:");
        System.out.println(toolRegistry.renderToolDescriptions());
        System.out.println();

        AgentExecutor executor = agentFactory.createAgentExecutor(toolRegistry);

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
        AgentResult result = executor.run(userInput, App::printEvent);

        if (!result.success()) {
            System.out.println("Run failed: " + result.finalAnswer());
        }
        System.out.println();
    }

    private static void printEvent(AgentEvent event) {
        switch (event.type()) {
            case STARTED -> System.out.println("User Input: " + event.message());
            case THINKING -> System.out.println("Thought: " + event.message());
            case TOOL_CALLING -> System.out.println("Action: " + event.message());
            case OBSERVATION -> System.out.println("Observation: " + event.message());
            case FINISHED -> System.out.println("Final Answer: " + event.message());
            case FAILED -> System.out.println("Failed: " + event.message());
        }
    }
}
