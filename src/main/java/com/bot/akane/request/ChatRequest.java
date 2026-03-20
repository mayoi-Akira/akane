package com.bot.akane.request;

public class ChatRequest {
    private String groupId;
    private String userInput;
   
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getUserInput() { return userInput; }
    public void setUserInput(String userInput) { this.userInput = userInput; }

    public String toString() {
        return "ChatRequest{groupId='" + groupId + "', userInput='" + userInput + "'}";
    }

}
