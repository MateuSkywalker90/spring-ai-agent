package com.mateus.spring_ai_agent.dto;

public record Evaluation(
        int score,
        String feedback,
        String idealAnswer,
        String nextQuestion
) {}
