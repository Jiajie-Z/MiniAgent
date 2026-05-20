package com.jagent.log;

public class AgentRunNotFoundException extends RuntimeException {
    public AgentRunNotFoundException(String runId) {
        super("Unknown runId: " + runId);
    }
}
