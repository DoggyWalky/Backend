package com.doggyWalky.doggyWalky.dog.controller;


import com.doggyWalky.doggyWalky.dog.dto.RegisterDogRequest;
import com.doggyWalky.doggyWalky.dog.dto.RegisterDogResponse;
import com.doggyWalky.doggyWalky.dog.service.DogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/dog")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;

    @PostMapping("/register")
    public ResponseEntity<RegisterDogResponse> registerDog(@AuthenticationPrincipal Principal principal, @RequestBody RegisterDogRequest registerDogRequest

    ) {
        return ResponseEntity.ok(dogService.registerDog(Long.valueOf(principal.getName()),registerDogRequest));
    }
}
