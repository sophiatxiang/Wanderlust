package com.sophiaxiang.wanderlust.models;

import java.util.Date;

public class Message {
    private String messageText;
    private User messageSender;
    private User messageRecipient;
    private long messageTime;

    public Message(String messageText, User messageSender, User messageRecipient) {
        this.messageText = messageText;
        this.messageSender = messageSender;
        this.messageRecipient = messageRecipient;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public Message(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) { this.messageText = messageText; }

    public User getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(User messageUser) {
        this.messageSender = messageUser;
    }

    public User getMessageRecipient() {
        return messageRecipient;
    }

    public void setMessageRecipient(User messageUser) {
        this.messageRecipient = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
