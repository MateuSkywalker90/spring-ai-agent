package com.mateus.spring_ai_agent.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.mateus.spring_ai_agent.models.InterviewQuestion;
import com.mateus.spring_ai_agent.models.InterviewQuiz;
import com.mateus.spring_ai_agent.tools.CalculatorTools;
import com.mateus.spring_ai_agent.tools.DateTimeTools;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Playground", description = "AI-powered Java interview sessions")
@RestController 
public class ChatController {
    
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder, 
                          DateTimeTools dateTimeTools, 
                          CalculatorTools calculatorTools, 
                          @Value("classpath:prompts/system-prompt.st") 
    Resource systemPrompt) {
        this.chatClient = builder
        .defaultSystem(systemPrompt)
        .defaultTools(dateTimeTools, calculatorTools)
        .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }

    @GetMapping("/interview")
    public InterviewQuestion interview(@RequestParam String topic) {
        return chatClient.prompt()
                .user("Generate an interview question about: " + 
                topic
                + ". Include a short correct answer and the difficulty (easy, medium or hard) ")
                .call()
                .entity(InterviewQuestion.class);
    
    }

    @GetMapping("/quiz")
    public InterviewQuiz quiz(@RequestParam String topic) {
        return chatClient.prompt()
                .user("""
                Generate 3 different Java interview questions about: %s.
                For each one, include a short correct answer and the difficulty (easy, medium or hard).
                """.formatted(topic))
                .call()
                .entity(InterviewQuiz.class);
    }
    
}
