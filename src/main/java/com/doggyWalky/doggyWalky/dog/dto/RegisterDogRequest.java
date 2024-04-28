package com.doggyWalky.doggyWalky.dog.dto;

import com.doggyWalky.doggyWalky.dog.entity.Dog;
import com.doggyWalky.doggyWalky.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterDogRequest {

    private String weight;

    private String description;

    private String name;

    private String kind;

    public Dog toEntity(Member member) {
        return Dog.builder()
                .weight(this.weight)
                .description(this.description)
                .name(this.name)
                .member(member)
                .kind(this.kind)
                .build();
    }

}
