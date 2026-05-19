package com.jagent.tool.builtin;

import com.jagent.tool.Tool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeTool implements Tool {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String name() {
        return "time";
    }

    @Override
    public String description() {
        return "Get current local date time.";
    }

    @Override
    public String parametersSchema() {
        return "No parameters. Use an empty string.";
    }

    @Override
    public String execute(String arguments) {
        return LocalDateTime.now().format(FORMATTER);
    }
}
