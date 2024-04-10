package com.doggyWalky.doggyWalky.dog.service;

import com.doggyWalky.doggyWalky.dog.dto.OwnersDogResponse;
import com.doggyWalky.doggyWalky.dog.dto.ReadDogResponse;
import com.doggyWalky.doggyWalky.dog.dto.RegisterDogRequest;
import com.doggyWalky.doggyWalky.dog.dto.RegisterDogResponse;
import com.doggyWalky.doggyWalky.dog.entity.Dog;
import com.doggyWalky.doggyWalky.dog.repository.DogRepository;
import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public ReadDogResponse findDogByDogId(Long dogId) {
        return new ReadDogResponse(findDogById(dogId));
    }

    public List<OwnersDogResponse> findDogByOwnerId(Long memberId){
        return dogRepository.findByMember(memberService.findByMemberId(memberId))
                .stream()
                .map(dog -> {

                    OwnersDogResponse ownersDogResponse = new OwnersDogResponse(dog);
                    //TODO 강아지 이미지 리턴
                    //ownersDogResponse.setFileImage();

                    return ownersDogResponse;


                })
                .collect(Collectors.toList());

    }

    public void updateDog(Long dogId, RegisterDogRequest updateRequest) {
        Dog dog = findDogById(dogId);
        dog.update(updateRequest.getName(), updateRequest.getKind(), updateRequest.getWeight(), updateRequest.getDescription());
        dogRepository.save(dog);
    }

    public void deleteDog(Long dogId) {
        Dog dog = findDogById(dogId);
        dogRepository.delete(dog);
    }


    private Dog findDogById(Long dogId) {
        return dogRepository.findById(dogId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.DOG_NOT_FOUND));
    }
}
