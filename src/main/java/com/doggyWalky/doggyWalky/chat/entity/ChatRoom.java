package com.doggyWalky.doggyWalky.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name="chatroom")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="room_id")
    private Long id;

    @Column(name="jobpost_id")
    private Long jobPostId;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="last_chat_id")
    private Long lastChatId;

    @Column(name="delete_at")
    private LocalDateTime deleteAt;

    public ChatRoom(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.deleteAt = null;
    }

    public void modifyLastMessage(Long lastChatId) {
        this.lastChatId = lastChatId;
    }

    public void deleteChatRoom() {
        this.deleteAt = LocalDateTime.now();
    }


}
