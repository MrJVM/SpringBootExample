package com.example.question2.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.example.question2.model.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    // REST Requests
    @GetMapping("/patient")
    public Greeting getPatient(@RequestParam(value = "nameStream") String nameStream,
                               @RequestParam(value = "externalClientId") String externalClientId,
                               @RequestParam(value = "apiVersion") String apiVersion) {

        return new Greeting(counter.incrementAndGet(), "jsonResponse");
    }

    // Versioning can be added at the URL level , this would create many different endpoints with each API version change
    // I opted to use a query parameter instead see below
    @RequestMapping(value = { "/patient" },
            method = RequestMethod.POST,
            produces = "application/json",
            consumes = "application/json")
    @ResponseBody
    public String createPatient(@RequestParam(value = "nameStream") String nameStream,
                                  @RequestParam(value = "externalClientId") String externalClientId,
                                  @RequestBody List<User> users,
                                  @RequestParam(value = "apiVersion") String apiVersion) {

        StringBuilder sb = new StringBuilder();
        switch(apiVersion) {
            case "1.0":
                sb.append(buildJsonResponse(users));
                break;
            case "1.2":
                sb.append(buildJsonUnsupportedResponse(apiVersion));
                break;
            default:
                sb.append(buildJsonUnsupportedResponse(apiVersion));
        }

        return sb.toString();
    }

    private StringBuilder buildJsonResponse(List<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("{[");
        for (User u: users) {
            sb.append(u.toJSON());
        }
        sb.append("]}");
        return sb;
    }

    private String buildJsonUnsupportedResponse(String apiVersion) {
        return new ErrorMessage("Version " + apiVersion +" is not supported yet.").toJSON();
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
