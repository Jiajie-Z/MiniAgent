package com.jagent.agent;

@FunctionalInterface
public interface AgentEventListener {
    AgentEventListener NO_OP = event -> {
    };

    void onEvent(AgentEvent event);
}
