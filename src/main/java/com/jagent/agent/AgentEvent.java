package com.jagent.agent;

public record AgentEvent(
        AgentEventType type,
        int stepIndex,
        String message
) {
    public static AgentEvent started(String userInput) {
        return new AgentEvent(AgentEventType.STARTED, 0, userInput);
    }

    public static AgentEvent thinking(int stepIndex, String thought) {
        return new AgentEvent(AgentEventType.THINKING, stepIndex, thought);
    }

    public static AgentEvent toolCalling(int stepIndex, String toolName, String toolArguments) {
        return new AgentEvent(AgentEventType.TOOL_CALLING, stepIndex, toolName + "(" + toolArguments + ")");
    }

    public static AgentEvent observation(int stepIndex, String observation) {
        return new AgentEvent(AgentEventType.OBSERVATION, stepIndex, observation);
    }

    public static AgentEvent finished(String finalAnswer) {
        return new AgentEvent(AgentEventType.FINISHED, 0, finalAnswer);
    }

    public static AgentEvent failed(String message) {
        return new AgentEvent(AgentEventType.FAILED, 0, message);
    }
}
