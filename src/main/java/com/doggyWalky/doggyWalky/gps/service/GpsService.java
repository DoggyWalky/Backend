package com.doggyWalky.doggyWalky.gps.service;

import com.doggyWalky.doggyWalky.gps.dto.request.GpsRequestDto;
import com.doggyWalky.doggyWalky.gps.dto.response.GpsResponseDto;
import com.doggyWalky.doggyWalky.gps.entity.Gps;
import com.doggyWalky.doggyWalky.gps.repository.GpsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GpsService {

    private final GpsRepository gpsRepository;


    /**
     * Geolocation API를 통해 받은 gps 정보 저장
     */
    public void saveGpsList(List<GpsRequestDto> gpsList) {
        List<Gps> gpsEntityList = gpsList.stream().map(gps -> {
            LocalDateTime coordinateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(gps.getTimestamp()), ZoneId.systemDefault());
            return new Gps(gps, coordinateTime);
        }).collect(Collectors.toList());

        gpsRepository.saveAll(gpsEntityList);
    }

    /**
     * jobPostId 조건 + coordinateTime에 대해 ASC 정렬 조건 페이지네이션 조회
     */
    public Page<GpsResponseDto> getGpsList(Long jobPostId, Pageable pageable) {
        return gpsRepository.findGpsListByJobPostId(jobPostId,pageable);
    }

}
