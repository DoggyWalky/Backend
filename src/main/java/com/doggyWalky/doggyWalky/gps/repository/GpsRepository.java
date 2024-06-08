package com.doggyWalky.doggyWalky.gps.repository;

import com.doggyWalky.doggyWalky.gps.dto.response.GpsResponseDto;
import com.doggyWalky.doggyWalky.gps.entity.Gps;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GpsRepository extends JpaRepository<Gps, Long> {

    @Query("select new com.doggyWalky.doggyWalky.gps.dto.response.GpsResponseDto(g.id, g.latitude,g.longitude,g.coordinateTime) from Gps g where g.jobPostId = :jobPostId")
    Page<GpsResponseDto> findGpsListByJobPostId(@Param("jobPostId") Long jobPostId, Pageable pageable);
}
