package com.jagent.log;

import java.time.Instant;
import java.util.List;

public record AgentRunLog(
        String runId,
        String userInput,
        boolean success,
        String finalAnswer,
        Instant startedAt,
        Instant finishedAt,
        long durationMs,
        List<AgentStepLog> steps
) {
}
