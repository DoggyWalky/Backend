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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
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

    @PostMapping("/gps-by-batch/{post-id}")
    public ResponseEntity saveGpsByBatch(@RequestBody List<GpsRequestDto> gpsList,
                                         @PathVariable("post-id") Long postId) throws  Exception{
        String jsonData = objectMapper.writeValueAsString(gpsList);
        redisTemplate.opsForValue().set("gpsDataKey:"+postId, jsonData);
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("dataIdentifier", "gpsDataKey:"+postId)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(gpsJob, jobParameters);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/gps/job-post/{job-post-id}")
    public ResponseEntity getGpsList(@PathVariable("job-post-id") Long jobPostId,@PageableDefault(size = 100,sort="coordinateTime", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable) {
        Page<GpsResponseDto> gpsList = gpsService.getGpsList(jobPostId, pageable);
        return new ResponseEntity(gpsList, HttpStatus.OK);
    }
}
