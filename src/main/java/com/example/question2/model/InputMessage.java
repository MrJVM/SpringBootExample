package com.example.question2.model;

import java.util.List;

public class InputMessage {

    private final String apiVersion;
    private final String streamName;//Patients name
    private final String externalClientId;
    private final String usersJson;//Users allowed to access the stream?

    public InputMessage(String apiVersion, String streamName, String externalClientId, String users) {
        this.apiVersion = apiVersion;
        this.streamName = streamName;
        this.externalClientId = externalClientId;
        this.usersJson = users;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getStreamName() {
        return streamName;
    }

    public String getExternalClientId() {
        return externalClientId;
    }

    public String getUsers() {
        return usersJson;
    }
}
