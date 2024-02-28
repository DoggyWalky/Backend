package com.doggyWalky.doggyWalky.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatRoomMessage {

    private enum Type {
        UNVISIBLE, QUIT, CREATE
    }

    private Long senderId;
    private Long receiverId;
    private Long chatRoomId;
    private Long jobPostId;
    private Type type;

    public static ChatRoomMessage createChatRoom(Long senderId, Long receiverId,Long jobPostId) {
        return ChatRoomMessage
                .builder()
                .chatRoomId(null)
                .senderId(senderId)
                .receiverId(receiverId)
                .jobPostId(jobPostId)
                .type(Type.CREATE)
                .build();
    }

}
