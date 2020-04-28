package com.example.question2.model;

public class ErrorMessage {

    private final String message;

    public ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String toJSON() {
        return "{\n" +
                "message:'" + message + '\'' +
                '}';
    }
}
