package com.jagent.log;

import com.jagent.agent.AgentEventListener;
import com.jagent.agent.AgentExecutor;
import com.jagent.agent.AgentResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AgentRunLogService {
    private final AgentExecutor agentExecutor;
    private final AgentRunLogRepository repository;

    public AgentRunLogService(AgentExecutor agentExecutor, AgentRunLogRepository repository) {
        this.agentExecutor = agentExecutor;
        this.repository = repository;
    }

    public AgentRunLog runAndLog(String userInput) {
        return runAndLog(userInput, AgentEventListener.NO_OP);
    }

    public AgentRunLog runAndLog(String userInput, AgentEventListener listener) {
        String runId = UUID.randomUUID().toString();
        Instant startedAt = Instant.now();
        long startedNanos = System.nanoTime();

        AgentResult result = agentExecutor.run(userInput, listener);

        Instant finishedAt = Instant.now();
        long durationMs = (System.nanoTime() - startedNanos) / 1_000_000;
        List<AgentStepLog> steps = result.steps().stream()
                .map(AgentStepLog::from)
                .toList();

        AgentRunLog log = new AgentRunLog(
                runId,
                userInput,
                result.success(),
                result.finalAnswer(),
                startedAt,
                finishedAt,
                durationMs,
                steps
        );
        repository.save(log);
        return log;
    }

    public List<AgentRunSummary> findAllSummaries() {
        return repository.findAllSummaries();
    }

    public AgentRunLog findById(String runId) {
        return repository.findById(runId)
                .orElseThrow(() -> new AgentRunNotFoundException(runId));
    }
}
