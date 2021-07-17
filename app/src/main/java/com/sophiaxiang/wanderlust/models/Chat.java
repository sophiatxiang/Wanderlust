package com.sophiaxiang.wanderlust.models;

import java.util.List;

public class Chat {
    private String chatId;
    private User currentUser;
    private User otherUser;
    private List<ChatMessage> chatMessages;

    public Chat(String chatId, User currentUser, User otherUser, List<ChatMessage> chatMessages) {
        this.chatId = chatId;
        this.currentUser = currentUser;
        this.otherUser = otherUser;
        this.chatMessages = chatMessages;
    }

    public Chat() {

    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getOtherUser() {
        return otherUser;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    public List<ChatMessage> getMessages() {
        return chatMessages;
    }

    public void setMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

}
