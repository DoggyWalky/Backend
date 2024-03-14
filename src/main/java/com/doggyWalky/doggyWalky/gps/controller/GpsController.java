package com.doggyWalky.doggyWalky.gps.controller;

import com.doggyWalky.doggyWalky.gps.dto.request.GpsRequestDto;
import com.doggyWalky.doggyWalky.gps.dto.response.GpsResponseDto;
import com.doggyWalky.doggyWalky.gps.repository.GpsRepository;
import com.doggyWalky.doggyWalky.gps.service.GpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GpsController {

    private final GpsService gpsService;

    @PostMapping("/gps")
    public ResponseEntity saveGps(@RequestBody List<GpsRequestDto> gpsList) {
        gpsService.saveGpsList(gpsList);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/gps/jobpost/{jobpostId}")
    public ResponseEntity getGpsList(@PathVariable Long jobpostId) {
        List<GpsResponseDto> gpsList = gpsService.getGpsList(jobpostId);
        return new ResponseEntity(gpsList, HttpStatus.OK);
    }
}
