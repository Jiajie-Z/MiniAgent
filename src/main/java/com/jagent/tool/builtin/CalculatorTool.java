package com.jagent.tool.builtin;

import com.jagent.tool.Tool;

public class CalculatorTool implements Tool {
    @Override
    public String name() {
        return "calculator";
    }

    @Override
    public String description() {
        return "Calculate simple addition expressions, for example: 12+30.";
    }

    @Override
    public String parametersSchema() {
        return "A plain addition expression in the form <integer>+<integer>, for example: 12+30.";
    }

    @Override
    public String execute(String arguments) {
        String[] parts = arguments.split("\\+");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Only addition is supported now.");
        }

        int left = Integer.parseInt(parts[0].trim());
        int right = Integer.parseInt(parts[1].trim());
        return String.valueOf(left + right);
    }
}
