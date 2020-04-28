package com.example.question2.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.example.question2.model.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    // REST Requests
    @GetMapping("/patient")
    public Greeting getPatient(@RequestParam(value = "name") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    // Versioning can be added at the URL level , this would create many different endpoints with each API version change
    // I opted to use a query parameter instead see below
    @PostMapping("/patient")
    public String createPatient(@RequestParam(value = "nameStream") String nameStream,
                                  @RequestParam(value = "externalClientId") String externalClientId,
                                  @RequestParam(value = "users") List<User> users,
                                  @RequestParam(value = "apiVersion") String apiVersion) {

        StringBuilder sb = buildJsonResponse(users);
        switch(apiVersion) {
            case "1.0":
                buildJsonResponse(users);
                break;
            case "1.2":
                buildJsonResponseV2();
                break;
            default:
                sb.append(apiVersion).append(" is not a supported API version.");
        }

        return sb.toString();
    }

    private StringBuilder buildJsonResponse(List<User> users) {
        StringBuilder sb = new StringBuilder();
        for (User u: users) {
            sb.append("<h4>")
                    .append("UserId:").append(u.getId())
                    .append(" UserName:").append(u.getName())
                    .append("</h4>");
        }
        return sb;
    }

    private StringBuilder buildJsonResponseV2() {
        return new StringBuilder("<h2>Version two is not supported yet.</h2>");
    }


    // Websocket Approach
    // I decided to implement the websocket version of this too.
    // To test it please run the application server and go to localhost:8080 and use the form
    @MessageMapping("/hello.patient")
    @SendTo("/topic/public")
    public ResponseMessage greeting(InputMessage input) throws Exception {
        return new ResponseMessage("Hello client with id: " + input.getExternalClientId() + "!");
    }

    @MessageMapping("/new.patient")
    @SendTo("/topic/public")
    public ResponseMessage newPatient(InputMessage input) throws Exception {
        StringBuilder jsonRes = new StringBuilder();

        switch(input.getApiVersion()) {
            case "1.0":
                buildHtmlResponseV1(input, jsonRes);
                break;
            case "1.2":
                buildHtmlResponseV2(input, jsonRes);
                break;
            default:
                jsonRes.append(input.getApiVersion()).append(" is not a supported API version.");
        }

        return new ResponseMessage(jsonRes.toString());
    }

    private void buildHtmlResponseV2(InputMessage input, StringBuilder jsonRes) {
        jsonRes.append(input.getApiVersion()).append(" is still under construction.");
        //        final User[] users = input.getUsers();
        //        for (User u: users) {
        //            jsonRes.append(HtmlUtils.htmlEscape("<h3>"+u.toString()+"</h3>"));
        //        }
    }

    private void buildHtmlResponseV1(InputMessage input, StringBuilder jsonRes) {
        jsonRes.append("<h3>").append(String.format("The following users have access to %s data:",
                input.getStreamName())).append("</h3>");
        jsonRes.append("<h3>"+input.getUsers()+"</h3>");
    }

}
