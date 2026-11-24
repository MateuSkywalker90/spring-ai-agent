package com.mateus.spring_ai_agent.models;

public record InterviewQuestion(
    String topic,
    String question,
    String answer,
    String difficulty
) {}
