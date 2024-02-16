package com.doggyWalky.doggyWalky.file.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BasicImage {

    BASIC_USER_IMAGE("https://doggywalky-image.s3.ap-northeast-2.amazonaws.com/basic_user_image.png"),
    BASIC_DOG_IMAGE("https://doggywalky-image.s3.ap-northeast-2.amazonaws.com/basic_dog_image.png"),
    BASIC_BOARD_IMAGE("https://doggywalky-image.s3.ap-northeast-2.amazonaws.com/basic_board_image.png");

    private String path;
}
