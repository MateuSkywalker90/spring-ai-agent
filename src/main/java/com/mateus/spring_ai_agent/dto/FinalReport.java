package com.mateus.spring_ai_agent.dto;

import java.util.List;

public record FinalReport(
        int overrallScore,          // 0 to 10
        String summary,             // brief overview of the interview
        List<String> strengths,     // list of candidate's strengths
        List<String> weakTopics,    // topics identified that need improvement
        List<String> studyRecommendations // what to study next
) {}
