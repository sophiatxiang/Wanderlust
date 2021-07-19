package com.sophiaxiang.wanderlust.models;

import java.util.List;

public class Chat {
    private String chatId;
    private String otherUserName;
    private String lastMessage;
    private String otherUserId;
    private String currentUserId;
    private long lastMessageTime;

    public Chat(String chatId, String otherUserName, String otherUserId, String currentUserId, long lastMessageTime) {
        this.chatId = chatId;
        this.otherUserName = otherUserName;
        this.otherUserId = otherUserId;
        this.currentUserId = currentUserId;
        this.lastMessageTime = lastMessageTime;
    }

    public Chat() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof Chat) return false;
        Chat chat = (Chat) o;
        return this.getChatId().equals(chat.getChatId());
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }
}
