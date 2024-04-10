package com.doggyWalky.doggyWalky.gps.entity;

import com.doggyWalky.doggyWalky.gps.dto.request.GpsRequestDto;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
public class Gps {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="gps_id")
    private Long id;

    @Column(name="latitude")
    private Double latitude;

    @Column(name="longitude")
    private Double longitude;

    @Column(name="jobpost_id")
    private Long jobPostId;

    @Column(name = "coordinate_time")
    private LocalDateTime coordinate_time;


    public Gps(GpsRequestDto dto, LocalDateTime coordinate_time) {
        this.latitude = dto.getLatitude();
        this.longitude = dto.getLongitude();
        this.jobPostId = dto.getJobPostId();
        this.coordinate_time = coordinate_time;
    }

}
