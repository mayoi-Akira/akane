package com.bot.akane.model.request;

import lombok.Data;

@Data
public class ChatRequest {
    private String groupId;
    private String messageId;
    private String userId;
    private String userInput;
   
    public String toString() {
        return "ChatRequest{groupId='" + groupId + "', messageId='" + messageId + "', userId='" + userId + "', userInput='" + userInput + "'}";
    }

}
