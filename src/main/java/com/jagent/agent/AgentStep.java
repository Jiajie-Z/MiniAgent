package com.jagent.agent;

public record AgentStep(
        int index,
        String thought,
        String toolName,
        String toolArguments,
        String observation,
        long durationMs
) {
}
