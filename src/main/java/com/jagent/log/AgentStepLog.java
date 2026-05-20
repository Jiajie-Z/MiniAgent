package com.jagent.log;

import com.jagent.agent.AgentStep;

public record AgentStepLog(
        int index,
        String thought,
        String toolName,
        String toolArguments,
        String observation,
        long durationMs
) {
    public static AgentStepLog from(AgentStep step) {
        return new AgentStepLog(
                step.index(),
                step.thought(),
                step.toolName(),
                step.toolArguments(),
                step.observation(),
                step.durationMs()
        );
    }
}
