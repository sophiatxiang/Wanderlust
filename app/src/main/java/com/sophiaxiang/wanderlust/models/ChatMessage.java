package com.sophiaxiang.wanderlust.models;

import java.util.Date;

public class ChatMessage {
    private String messageText;
    private String messageSenderId;
    private long messageTime;

    public ChatMessage(String messageSender, String messageText) {
        this.messageSenderId = messageSender;
        this.messageText = messageText;
        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) { this.messageText = messageText; }

    public String getMessageSender() {
        return messageSenderId;
    }

    public void setMessageSender(String messageUser) {
        this.messageSenderId = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
