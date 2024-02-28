package com.doggyWalky.doggyWalky.chat.controller;

import com.amazonaws.Response;
import com.doggyWalky.doggyWalky.chat.dto.ChatRoomMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ChatController {



    private final ChannelTopic topic;
    private final RedisTemplate redisPubTemplate;

    @PostMapping("/contact/{receiverId}")
    public ResponseEntity contact(@PathVariable("receiverId") Long receiverId, Principal principal) {
        Long senderId = Long.parseLong(principal.getName());

        redisPubTemplate.convertAndSend(topic.getTopic(), ChatRoomMessage.createChatRoom(senderId,receiverId));
        return new ResponseEntity(HttpStatus.OK);
    }
}
