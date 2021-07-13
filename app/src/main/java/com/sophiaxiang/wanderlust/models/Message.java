package com.sophiaxiang.wanderlust.models;

import java.util.Date;

public class Message {
    private String messageText;
    private String messageSender;
    private String messageRecipient;
    private long messageTime;

    public Message(String messageText, String messageSender, String messageRecipient) {
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

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageSender() {
        return messageSender;
    }

    public void setMessageSender(String messageUser) {
        this.messageSender = messageUser;
    }

    public String getMessageRecipient() {
        return messageRecipient;
    }

    public void setMessageRecipient(String messageUser) {
        this.messageRecipient = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
