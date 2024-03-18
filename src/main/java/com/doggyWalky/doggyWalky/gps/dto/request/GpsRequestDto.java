package com.doggyWalky.doggyWalky.gps.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class GpsRequestDto {

    private Long jobPostId;

    private Double latitude;

    private Double longitude;

    private long timestamp;

}
