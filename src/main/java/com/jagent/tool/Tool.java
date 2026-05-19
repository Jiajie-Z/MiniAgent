package com.jagent.tool;

public interface Tool {
    String name();

    String description();

    String execute(String arguments);
}
