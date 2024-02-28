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
    private Long ChatRoomId;
    private Type type;

    public static ChatRoomMessage createChatRoom(Long senderId, Long receiverId) {
        return ChatRoomMessage
                .builder()
                .ChatRoomId(null)
                .senderId(senderId)
                .receiverId(receiverId)
                .type(Type.CREATE)
                .build();
    }
}
