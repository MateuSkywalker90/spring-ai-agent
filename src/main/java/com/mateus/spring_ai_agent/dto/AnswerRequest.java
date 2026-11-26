package com.mateus.spring_ai_agent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerRequest (

        @NotBlank(message = "Answer cannot be empty")
        @Size(max = 2000, message = "Answer cannot exceed 2000 characters") 
        String answer
) {}
