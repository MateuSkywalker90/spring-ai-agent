package com.mateus.spring_ai_agent.dto;

import java.util.List;

public record StartInterviewRequest(
        String level,
        List<String> topics
) {}
