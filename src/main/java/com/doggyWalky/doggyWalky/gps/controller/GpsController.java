package com.doggyWalky.doggyWalky.gps.controller;

import com.doggyWalky.doggyWalky.gps.dto.request.GpsRequestDto;
import com.doggyWalky.doggyWalky.gps.dto.response.GpsResponseDto;
import com.doggyWalky.doggyWalky.gps.repository.GpsRepository;
import com.doggyWalky.doggyWalky.gps.service.GpsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GpsController {

    private final GpsService gpsService;

    private final JobLauncher jobLauncher;
    private final Job gpsJob;

    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper objectMapper;

    @PostMapping("/gps")
    public ResponseEntity saveGps(@RequestBody List<GpsRequestDto> gpsList) {
        gpsService.saveGpsList(gpsList);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/gpsByBatch")
    public ResponseEntity saveGpsByBatch(@RequestBody List<GpsRequestDto> gpsList) throws  Exception{
        String jsonData = objectMapper.writeValueAsString(gpsList);
        redisTemplate.opsForValue().set("gpsDataKey", jsonData);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("dataIdentifier", "gpsDataKey")
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(gpsJob, jobParameters);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/gps/jobpost/{jobpostId}")
    public ResponseEntity getGpsList(@PathVariable Long jobpostId) {
        List<GpsResponseDto> gpsList = gpsService.getGpsList(jobpostId);
        return new ResponseEntity(gpsList, HttpStatus.OK);
    }
}
