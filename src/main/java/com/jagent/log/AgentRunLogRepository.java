package com.jagent.log;

import java.util.List;
import java.util.Optional;

public interface AgentRunLogRepository {
    void save(AgentRunLog log);

    List<AgentRunSummary> findAllSummaries();

    Optional<AgentRunLog> findById(String runId);
}
