package com.doggyWalky.doggyWalky.dog.dto;


import com.doggyWalky.doggyWalky.dog.entity.Dog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDogResponse {

    private Long dogId;

    private String weight;

    private String description;

    private String name;

    public RegisterDogResponse(Dog dog) {
        this.dogId = dog.getDogId();
        this.weight = dog.getWeight();
        this.description = dog.getDescription();
        this.name = dog.getName();
    }


}
