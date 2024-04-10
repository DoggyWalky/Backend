package com.doggyWalky.doggyWalky.dog.dto;

import com.doggyWalky.doggyWalky.dog.entity.Dog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReadDogResponse {

    private Long dogId;

    private String kind;

    private String weight;

    private String description;

    private String name;

    private LocalDateTime insertDate;

    public ReadDogResponse(Dog dog) {

        this.dogId = dog.getDogId();
        this.kind = dog.getKind();
        this.weight = dog.getWeight();
        this.description = dog.getDescription();
        this.name = dog.getName();
        this.insertDate = dog.getCreatedDate();
    }
}
