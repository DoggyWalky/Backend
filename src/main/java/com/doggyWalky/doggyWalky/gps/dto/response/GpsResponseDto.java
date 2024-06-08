package com.doggyWalky.doggyWalky.gps.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GpsResponseDto {

    private Long gpsId;

    private Double latitude;

    private Double longitude;

    private String coordinateTime;

    public GpsResponseDto(Long gpsId, Double latitude, Double longitude, LocalDateTime coordinateTime) {
        this.gpsId = gpsId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.coordinateTime = coordinateTime.toString().replace("T", " ");
    }
}
