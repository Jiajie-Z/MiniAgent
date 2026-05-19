package com.jagent.agent;

public class AgentDecision {
    private final AgentState state;
    private final String thought;
    private final String toolName;
    private final String toolArguments;
    private final String finalAnswer;

    private AgentDecision(AgentState state, String thought, String toolName, String toolArguments, String finalAnswer) {
        this.state = state;
        this.thought = thought;
        this.toolName = toolName;
        this.toolArguments = toolArguments;
        this.finalAnswer = finalAnswer;
    }

    public static AgentDecision callTool(String thought, String toolName, String toolArguments) {
        return new AgentDecision(AgentState.CALLING_TOOL, thought, toolName, toolArguments, null);
    }

    public static AgentDecision finish(String thought, String finalAnswer) {
        return new AgentDecision(AgentState.FINISHED, thought, null, null, finalAnswer);
    }

    public AgentState state() {
        return state;
    }

    public String thought() {
        return thought;
    }

    public String toolName() {
        return toolName;
    }

    public String toolArguments() {
        return toolArguments;
    }

    public String finalAnswer() {
        return finalAnswer;
    }
}
