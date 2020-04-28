package com.example.question2.model;

public class ResponseMessage {

    private String messageId;
    private String message;

    public ResponseMessage(String msg) {
        this.message = msg;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}