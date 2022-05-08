package com.ismail.ws.api.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ismail.ws.api.model.ServerMessage;
import com.ismail.ws.api.model.MessageType;
import com.ismail.ws.api.model.RegisterMessage;

import lombok.extern.slf4j.Slf4j;

@Controller
@EnableScheduling
@Slf4j
public class ChatController
{
    private int updateCount = 0;

    @Autowired
    private SimpMessagingTemplate simpMsgTemplate;

    @MessageMapping("/ws.register")
    public void register(@Payload RegisterMessage regMessage, SimpMessageHeaderAccessor headerAccessor)
    {
        log.info("register() " + regMessage);

        // set username in the session attributes
        headerAccessor.getSessionAttributes().put("username", regMessage.getUsername());

    }


    @Scheduled(initialDelay = 2000, fixedRate = 2000)
    public void sendMessagesRegularly()
    {
        updateCount++;

        ServerMessage msg = ServerMessage.builder()
                .time(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss")))
                .type(MessageType.MSG)
                .content("price update " + updateCount)
                .build();

        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            String msgStr = objectMapper.writeValueAsString(msg);

            simpMsgTemplate.convertAndSend("/topic/public", msgStr);

        }
        catch (Exception e)
        {
            log.error("Error: " + e.getMessage(), e);
        }

    }

}
