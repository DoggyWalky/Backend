package com.doggyWalky.doggyWalky.dog.dto;

import com.doggyWalky.doggyWalky.dog.entity.Dog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OwnersDogResponse {

    private Long dogId;

    private String name;

    @Setter
    private String fileImage;

    public OwnersDogResponse(Dog dog){
        this.dogId = dog.getDogId();
        this.name = dog.getName();
    }

}
