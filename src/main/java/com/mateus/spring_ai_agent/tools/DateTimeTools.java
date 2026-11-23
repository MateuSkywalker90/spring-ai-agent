package com.mateus.spring_ai_agent.tools;

import java.time.LocalDateTime;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component 
public class DateTimeTools {

    @Tool(description = "Get the current date and time in the user's timezone")
    public String getCurrentDateTime() {
        System.out.println(">>> Tool called: getCurrentDateTime");
        return LocalDateTime.now().toString();
    }
}
