package com.mateus.spring_ai_agent.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StartInterviewRequest(

        @NotBlank(message = "Level is required")
        @Pattern(regexp = "(?i)junior|mid|senior", message = "Level must be: junior, mid, senior")
        String level,

        @NotEmpty(message = "At least one topic is required")
        @Size(max = 5, message = "Maximum 5 topics allowed")
        List<@NotBlank(message = "Topic cannot be blank") String> topics
) {}
