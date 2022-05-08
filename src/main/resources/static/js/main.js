'use strict';

var msgSender = document.querySelector('#msgSender');
var msgType = document.querySelector('#msgType');
var msgContent = document.querySelector('#msgContent');
var msgStatus = document.querySelector('#msgStatus');

var stompClient = null;
var username = null;

function connect()
{
    var socket = new SockJS('/stomp');

    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}


function onConnected()
{
	msgStatus.textContent = 'Connected';
    msgStatus.style.color = 'green';
    
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/ws.register", {}, JSON.stringify({username: 'user_xyz'}) );

}


function onError(error)
{
    msgStatus.textContent = 'Unable to connect. Please refresh this page to try again!';
    msgStatus.style.color = 'red';
}


function onMessageReceived(payload)
{
	console.log("onMessageReceived() " + payload);
	
    var message = JSON.parse(payload.body);

	msgTime.textContent = message.time;
	msgType.textContent = message.type;
    msgContent.textContent = message.content;
    
    msgStatus.textContent = 'Connected. Received msg';
    msgStatus.style.color = 'green';
}


connect();

