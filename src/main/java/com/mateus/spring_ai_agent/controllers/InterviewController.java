package com.mateus.spring_ai_agent.controllers;

import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mateus.spring_ai_agent.dto.AnswerRequest;
import com.mateus.spring_ai_agent.dto.Evaluation;
import com.mateus.spring_ai_agent.dto.StartInterviewRequest;
import com.mateus.spring_ai_agent.dto.StartInterviewResponse;
import com.mateus.spring_ai_agent.dto.Question;

@RestController 
@RequestMapping("/interviews")
public class InterviewController {
    private final ChatClient chatClient;

    public InterviewController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient = builder
                .defaultSystem("""
                        You are a professional Java technical interviewer.
                        - Ask ONE question at a time.
                        - Adapt the questions to the candidate's level and topics.
                        - When evaluating an answer, be fair and honest.
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        }

    @PostMapping 
    public StartInterviewResponse start(@RequestBody StartInterviewRequest request) {
        String interviewId = UUID.randomUUID().toString();

        Question firstQuestion = chatClient.prompt()
                .user("""
                        Start a %s level Java interview about these topics: %s.
                        Ask the first question.
                        """.formatted(request.level(),
                    String.join(", ", request.topics())))
                        .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, interviewId))
                        .call()
                        .entity(Question.class);

        return new StartInterviewResponse(interviewId, firstQuestion.question());
    }

    @PostMapping("/{interviewId}/answers")
    public Evaluation answer(@PathVariable String interviewId, 
                             @RequestBody AnswerRequest request) {
        return chatClient.prompt()
                .user("""
                        My answer to your last question: %s

                        Evaluate my answer with a score from 0 to 10, feedback,
                        an ideal answer, and then the next question.
                        """.formatted(request.answer()))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, interviewId))
                .call()
                .entity(Evaluation.class);
    }
}
