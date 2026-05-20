package com.jagent.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgentRunRequest(
        @NotBlank(message = "input must not be blank")
        @Size(max = 1000, message = "input must be at most 1000 characters")
        String input
) {
}
