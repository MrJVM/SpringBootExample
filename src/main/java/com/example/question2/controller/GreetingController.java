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
import org.springframework.web.util.HtmlUtils;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    //HTTP Requests
    @GetMapping("/patient")
    public Greeting getPatient(@RequestParam(value = "name") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @PostMapping("/patient")
    public Greeting createPatient(@RequestParam(value = "name") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    //Websockets
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
                jsonRes.append("<h3>").append(String.format("The following users have access to %s data:", input.getStreamName())).append("</h3>");
                jsonRes.append("<h3>"+input.getUsers()+"</h3>");
                break;
            case "1.2":
                jsonRes.append(input.getApiVersion()).append(" is still under construction.");
                //        final User[] users = input.getUsers();
                //        for (User u: users) {
                //            jsonRes.append(HtmlUtils.htmlEscape("<h3>"+u.toString()+"</h3>"));
                //        }
                break;
                default:
                    jsonRes.append(input.getApiVersion()+" is not a supported API version.");
        }


        return new ResponseMessage(jsonRes.toString());
    }

}
