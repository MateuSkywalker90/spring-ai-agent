package com.mateus.spring_ai_agent.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class InterviewTrainerController {
    private final ChatClient chatClient;
    
    public InterviewTrainerController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultSystem(""" 
                    You are a Java interview trainer.
                        - Ask ONE Java interview question at a time.
                        - When the user answers, say if it is correct, explain briefly, and give a score from 0 to 10.
                        - Then ask the next question.""")
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @GetMapping("/trainer")
    public String trainer(@RequestParam String conversationId,
                          @RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    } 
}
