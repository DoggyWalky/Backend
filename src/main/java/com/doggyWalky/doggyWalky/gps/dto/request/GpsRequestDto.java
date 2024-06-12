package com.doggyWalky.doggyWalky.gps.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GpsRequestDto {

    private Long jobPostId;

    private Double latitude;

    private Double longitude;

    private long timestamp;

}
