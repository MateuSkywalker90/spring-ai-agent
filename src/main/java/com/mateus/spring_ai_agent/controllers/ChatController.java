package com.mateus.spring_ai_agent.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.mateus.spring_ai_agent.tools.DateTimeTools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
public class ChatController {
    
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder, DateTimeTools dateTimeTools) {
        this.chatClient = builder
        .defaultSystem("You are a helpful assistant. Use tools when you need real-time information")
        .defaultTools(dateTimeTools)
        .build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String question) {
        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
    
}
