package com.jagent.log;

import java.time.Instant;

public record AgentRunSummary(
        String runId,
        String userInput,
        boolean success,
        String finalAnswer,
        Instant startedAt,
        long durationMs,
        int stepCount
) {
    public static AgentRunSummary from(AgentRunLog log) {
        return new AgentRunSummary(
                log.runId(),
                log.userInput(),
                log.success(),
                log.finalAnswer(),
                log.startedAt(),
                log.durationMs(),
                log.steps().size()
        );
    }
}
