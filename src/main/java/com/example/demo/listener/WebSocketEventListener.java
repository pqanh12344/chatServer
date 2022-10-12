package com.example.demo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.demo.model.ChatMessage;
import com.example.demo.model.ChatMessage.MessageType;

public class WebSocketEventListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);
	
	@Autowired
	private SimpMessageSendingOperations messageSendingOperations;
	
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent event) {
		LOGGER.info("Received a new websocket connection!");
	}
	
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		
		String username = (String) headerAccessor.getSessionAttributes().get("username");
		
		if(username != null) {
			LOGGER.info("User Disconnect: " + username);
			
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setMessageType(MessageType.LEAVE);
			chatMessage.setSender(username);
			
			messageSendingOperations.convertAndSend("/topic/publicChatRoom", chatMessage);
			
		}
	}

}
