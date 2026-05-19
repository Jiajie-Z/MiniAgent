package com.jagent.tool;

public interface Tool {
    String name();

    String description();

    String parametersSchema();

    String execute(String arguments);
}
