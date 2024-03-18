package com.doggyWalky.doggyWalky.batch.processor;

import com.doggyWalky.doggyWalky.gps.dto.request.GpsRequestDto;
import com.doggyWalky.doggyWalky.gps.entity.Gps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
public class GpsProcessor implements ItemProcessor<GpsRequestDto, Gps> {

    @Override
    public Gps process(GpsRequestDto gps) throws Exception {
        LocalDateTime coordinateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(gps.getTimestamp()), ZoneId.systemDefault());
        Gps gpsEntity = new Gps(gps,coordinateTime);
        log.info("GpsProcessor 호출");
        return gpsEntity;
    }
}
