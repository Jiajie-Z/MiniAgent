package com.jagent.llm;

import com.jagent.agent.AgentDecision;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReActDecisionParser {
    public AgentDecision parse(String content) {
        Map<String, String> fields = parseFields(content);
        String thought = fields.getOrDefault("Thought", "");

        if (fields.containsKey("Final Answer")) {
            return AgentDecision.finish(thought, fields.get("Final Answer"));
        }

        String action = fields.get("Action");
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Missing Action in model response.");
        }

        return AgentDecision.callTool(
                thought,
                action,
                fields.getOrDefault("Action Input", "")
        );
    }

    private Map<String, String> parseFields(String content) {
        Map<String, String> fields = new LinkedHashMap<>();
        String currentKey = null;
        StringBuilder currentValue = new StringBuilder();

        for (String line : content.split("\\R")) {
            String key = findKey(line);
            if (key != null) {
                saveField(fields, currentKey, currentValue);
                currentKey = key;
                currentValue = new StringBuilder(line.substring(key.length() + 1).trim());
                continue;
            }

            if (currentKey != null) {
                if (!currentValue.isEmpty()) {
                    currentValue.append(System.lineSeparator());
                }
                currentValue.append(line);
            }
        }

        saveField(fields, currentKey, currentValue);
        return fields;
    }

    private String findKey(String line) {
        for (String key : new String[]{"Thought", "Action", "Action Input", "Final Answer"}) {
            if (line.startsWith(key + ":")) {
                return key;
            }
        }
        return null;
    }

    private void saveField(Map<String, String> fields, String key, StringBuilder value) {
        if (key != null) {
            fields.put(key, value.toString().trim());
        }
    }
}
