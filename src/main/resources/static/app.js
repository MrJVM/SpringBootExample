var stompClient = null;


function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
//    var socket = new SockJS('/patient');
    var socket = new SockJS($("#stream").val())
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/public', onMessageReceived);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendForm() {
      var users =JSON.parse($("#users").val());
      var s = JSON.stringify(   {
                                'apiVersion': $("#apiList").val(),
                                'streamName': $("#stream").val(),
                                'externalClientId': $("#extClientId").val(),
                                'users': users
                                });
    stompClient.send("/app/new.patient",{}, s);
    $("#greetings").html("Success!");
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function showResponse(message) {
    $("#res").append("<tr><td  style='color:red;'>" + message + "</td></tr>");
}

const onMessageReceived = (payload) => {
    const message = payload.body;
    showResponse(message);
}

$(function () {
    $("#conversation").hide();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendForm(); });
});