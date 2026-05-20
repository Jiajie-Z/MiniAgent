package com.jagent.web;

import com.jagent.agent.AgentEvent;
import com.jagent.agent.AgentExecutor;
import com.jagent.agent.AgentResult;
import com.jagent.tool.ToolRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class AgentController {
    private final AgentExecutor agentExecutor;
    private final ToolRegistry toolRegistry;

    public AgentController(AgentExecutor agentExecutor, ToolRegistry toolRegistry) {
        this.agentExecutor = agentExecutor;
        this.toolRegistry = toolRegistry;
    }

    @GetMapping("/tools")
    public String tools() {
        return toolRegistry.renderToolDescriptions();
    }

    @PostMapping("/agent/run")
    public AgentResult run(@RequestBody AgentRunRequest request) {
        return agentExecutor.run(request.input());
    }

    @GetMapping(value = "/agent/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String input) {
        SseEmitter emitter = new SseEmitter(0L);

        CompletableFuture.runAsync(() -> {
            try {
                agentExecutor.run(input, event -> sendEvent(emitter, event));
                emitter.complete();
            } catch (RuntimeException exception) {
                emitter.completeWithError(exception);
            }
        });

        return emitter;
    }

    private void sendEvent(SseEmitter emitter, AgentEvent event) {
        try {
            emitter.send(SseEmitter.event()
                    .name(event.type().name().toLowerCase())
                    .data(event));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to send SSE event.", exception);
        }
    }
}
