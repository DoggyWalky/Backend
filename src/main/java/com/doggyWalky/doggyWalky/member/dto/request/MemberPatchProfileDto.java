package com.doggyWalky.doggyWalky.member.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberPatchProfileDto {

    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{1,15}$")
    private String nickName;

    @Size(min = 20, max = 500)
    private String description;

    private Long fileId;

    @Setter
    private String profileImage;

}
