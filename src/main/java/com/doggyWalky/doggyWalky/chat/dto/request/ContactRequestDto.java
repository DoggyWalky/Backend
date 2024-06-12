package com.doggyWalky.doggyWalky.chat.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequestDto {

    private Long receiverId;

    private Long jobPostId;

}
