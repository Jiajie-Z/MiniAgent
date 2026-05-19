package com.jagent.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AgentContext {
    private final String userInput;
    private final List<AgentStep> steps = new ArrayList<>();
    private final int maxSteps;

    public AgentContext(String userInput, int maxSteps) {
        this.userInput = userInput;
        this.maxSteps = maxSteps;
    }

    public String userInput() {
        return userInput;
    }

    public int maxSteps() {
        return maxSteps;
    }

    public List<AgentStep> steps() {
        return Collections.unmodifiableList(steps);
    }

    public void addStep(AgentStep step) {
        steps.add(step);
    }
}
