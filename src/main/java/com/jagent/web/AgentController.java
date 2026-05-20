package com.jagent.web;

import com.jagent.agent.AgentEvent;
import com.jagent.log.AgentRunLog;
import com.jagent.log.AgentRunLogService;
import com.jagent.log.AgentRunSummary;
import com.jagent.tool.ToolRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class AgentController {
    private final ToolRegistry toolRegistry;
    private final AgentRunLogService logService;

    public AgentController(ToolRegistry toolRegistry, AgentRunLogService logService) {
        this.toolRegistry = toolRegistry;
        this.logService = logService;
    }

    @GetMapping("/tools")
    public ApiResponse<String> tools() {
        return ApiResponse.success(toolRegistry.renderToolDescriptions());
    }

    @PostMapping("/agent/run")
    public ApiResponse<AgentRunLog> run(@RequestBody AgentRunRequest request) {
        return ApiResponse.success(logService.runAndLog(request.input()));
    }

    @GetMapping(value = "/agent/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@RequestParam String input) {
        SseEmitter emitter = new SseEmitter(0L);

        CompletableFuture.runAsync(() -> {
            try {
                logService.runAndLog(input, event -> sendEvent(emitter, event));
                emitter.complete();
            } catch (RuntimeException exception) {
                emitter.completeWithError(exception);
            }
        });

        return emitter;
    }

    @GetMapping("/runs")
    public ApiResponse<List<AgentRunSummary>> runs() {
        return ApiResponse.success(logService.findAllSummaries());
    }

    @GetMapping("/runs/{runId}")
    public ApiResponse<AgentRunLog> runLog(@PathVariable String runId) {
        return ApiResponse.success(logService.findById(runId));
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
