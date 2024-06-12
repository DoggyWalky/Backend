package com.doggyWalky.doggyWalky.dog.controller;


import com.doggyWalky.doggyWalky.dog.dto.OwnersDogResponse;
import com.doggyWalky.doggyWalky.dog.dto.ReadDogResponse;
import com.doggyWalky.doggyWalky.dog.dto.RegisterDogRequest;
import com.doggyWalky.doggyWalky.dog.dto.RegisterDogResponse;
import com.doggyWalky.doggyWalky.dog.service.DogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/dog")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;

    @PostMapping("/register")
    public ResponseEntity<RegisterDogResponse> registerDog(Principal principal, @RequestBody RegisterDogRequest registerDogRequest

    ) {
        return ResponseEntity.ok(dogService.registerDog(Long.valueOf(principal.getName()), registerDogRequest));
    }

    @GetMapping("/{dog-id}")
    public ResponseEntity<ReadDogResponse> findDogById(@PathVariable("dog-id") Long dogId) {
        return ResponseEntity.ok(dogService.findDogByDogId(dogId));
    }


    @GetMapping("/owners/{owner-id}/dogs")
    public ResponseEntity<List<OwnersDogResponse>> findDogsByOwnerId(@PathVariable("owner-id") Long memberId) {
        return ResponseEntity.ok(dogService.findDogByOwnerId(memberId));
    }

    @PatchMapping("/{dog-id}")
    public ResponseEntity<Void> updateDog(
            @PathVariable("dog-id") Long dogId,
            @RequestBody RegisterDogRequest updateRequest) {

        dogService.updateDog(dogId, updateRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{dog-id}")
    public ResponseEntity<Void> deleteDog(@PathVariable("dog-id") Long dogId) {
        dogService.deleteDog(dogId);
        return ResponseEntity.ok().build();
    }


}
