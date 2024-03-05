package com.doggyWalky.doggyWalky.chat.controller;

import com.amazonaws.Response;
import com.doggyWalky.doggyWalky.chat.dto.ChatRoomMessage;
import com.doggyWalky.doggyWalky.chat.dto.request.ContactRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ChatController {



    private final ChannelTopic topic;
    private final RedisTemplate redisPubTemplate;

    @PostMapping("/contact")
    public ResponseEntity contact(@RequestBody ContactRequestDto contactDto, Principal principal) {
        Long senderId = Long.parseLong(principal.getName());

        redisPubTemplate.convertAndSend(topic.getTopic(), ChatRoomMessage.createChatRoom(senderId,contactDto.getReceiverId(),contactDto.getJobPostId()));
        return new ResponseEntity(HttpStatus.OK);
    }
}
