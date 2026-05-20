package com.jagent.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryAgentRunLogRepository implements AgentRunLogRepository {
    private final Map<String, AgentRunLog> logs = new LinkedHashMap<>();

    @Override
    public synchronized void save(AgentRunLog log) {
        logs.put(log.runId(), log);
    }

    @Override
    public synchronized List<AgentRunSummary> findAllSummaries() {
        List<AgentRunSummary> summaries = new ArrayList<>();
        for (AgentRunLog log : logs.values()) {
            summaries.add(AgentRunSummary.from(log));
        }
        Collections.reverse(summaries);
        return summaries;
    }

    @Override
    public synchronized Optional<AgentRunLog> findById(String runId) {
        return Optional.ofNullable(logs.get(runId));
    }
}
