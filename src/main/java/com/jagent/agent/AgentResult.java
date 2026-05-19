package com.jagent.agent;

import java.util.List;

public record AgentResult(
        boolean success,
        String finalAnswer,
        List<AgentStep> steps
) {
    public static AgentResult finished(String finalAnswer, List<AgentStep> steps) {
        return new AgentResult(true, finalAnswer, List.copyOf(steps));
    }

    public static AgentResult failed(String message, List<AgentStep> steps) {
        return new AgentResult(false, message, List.copyOf(steps));
    }
}
