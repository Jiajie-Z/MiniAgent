package com.jagent.llm;

import com.jagent.agent.AgentContext;
import com.jagent.agent.AgentDecision;

public interface ChatModel {
    AgentDecision decide(AgentContext context);
}
