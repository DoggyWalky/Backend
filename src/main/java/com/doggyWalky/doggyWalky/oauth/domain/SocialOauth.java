package com.doggyWalky.doggyWalky.oauth.domain;

import org.springframework.http.ResponseEntity;

public interface SocialOauth {

    ResponseEntity<String> requestAccessToken(String code);
}
