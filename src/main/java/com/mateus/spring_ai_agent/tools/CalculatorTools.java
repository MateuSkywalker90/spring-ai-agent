package com.mateus.spring_ai_agent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component 
public class CalculatorTools {
    
    @Tool(description = "Calculate and return the sum of two provided decimal numbers")
    public String add(@ToolParam(description = "First number") double a, @ToolParam(description = "Second number") double b) {
        System.out.println(">>> Tool called: add");
        return String.valueOf(a + b);
    }

}
