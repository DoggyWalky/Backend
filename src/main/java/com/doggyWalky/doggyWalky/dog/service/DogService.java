package com.doggyWalky.doggyWalky.dog.service;

import com.doggyWalky.doggyWalky.dog.dto.RegisterDogRequest;
import com.doggyWalky.doggyWalky.dog.dto.RegisterDogResponse;
import com.doggyWalky.doggyWalky.dog.entity.Dog;
import com.doggyWalky.doggyWalky.dog.repository.DogRepository;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DogService {

    private final DogRepository dogRepository;

    private final MemberService memberService;

    public RegisterDogResponse registerDog(Long memberId, RegisterDogRequest dogRequest) {

        Member byMemberId = memberService.findByMemberId(memberId);

        Dog dog = dogRequest.toEntity(byMemberId);

        return new RegisterDogResponse(dogRepository.save(dog));

    }
}
