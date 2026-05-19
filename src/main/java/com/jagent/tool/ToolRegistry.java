package com.jagent.tool;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ToolRegistry {
    private final Map<String, Tool> tools = new LinkedHashMap<>();

    public void register(Tool tool) {
        tools.put(tool.name(), tool);
    }

    public Tool get(String name) {
        Tool tool = tools.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("Unknown tool: " + name);
        }
        return tool;
    }

    public String renderToolDescriptions() {
        return tools.values().stream()
                .map(tool -> """
                        Tool: %s
                        Description: %s
                        Parameters: %s
                        """.formatted(tool.name(), tool.description(), tool.parametersSchema()).trim())
                .collect(Collectors.joining(System.lineSeparator() + System.lineSeparator()));
    }
}
