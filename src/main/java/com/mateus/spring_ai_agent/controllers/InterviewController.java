package com.mateus.spring_ai_agent.controllers;

import java.util.UUID;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.mateus.spring_ai_agent.dto.AnswerRequest;
import com.mateus.spring_ai_agent.dto.Evaluation;
import com.mateus.spring_ai_agent.dto.FinalReport;
import com.mateus.spring_ai_agent.dto.Question;
import com.mateus.spring_ai_agent.dto.StartInterviewRequest;
import com.mateus.spring_ai_agent.dto.StartInterviewResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Interviews", description = "AI-powered Java interview sessions")
@RestController 
@RequestMapping("/interviews")
public class InterviewController {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public InterviewController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatMemory = chatMemory;
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

    @Operation(summary = "Start a new interview session", 
               description = "Initiates a new interview session with a unique ID and returns the first question."
               )
    @PostMapping 
    public StartInterviewResponse start(@Valid @RequestBody StartInterviewRequest request) {
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

    @Operation(summary = "Submit an answer", 
               description = "Submits an answer to the current interview question and returns the evaluation."
               )
    @PostMapping("/{interviewId}/answers")
    public Evaluation answer(@PathVariable String interviewId, 
                             @Valid @RequestBody AnswerRequest request) {
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

    @Operation(summary = "Get interview report", 
               description = "Retrieves the final report for a completed interview session."
               )
    @GetMapping("/{interviewId}/report")
    public FinalReport report(@PathVariable String interviewId) {
        if (chatMemory.get(interviewId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Interview not found: " + interviewId);
        }
        
        return chatClient.prompt()
                .user(""" 
                        The interview is over. Do not ask any more questions.
                        Based on all my answers in this interview, create a final report with:
                        - an overall score from 0 to 10
                        - a short summary of my performance
                        - my strengths
                        - the topics I need to improve
                        - specific study recommendations
                        """)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, interviewId))
                .call()
                .entity(FinalReport.class);
    }
    
}
