package com.doggyWalky.doggyWalky.batch.writer;

import com.doggyWalky.doggyWalky.gps.entity.Gps;
import com.doggyWalky.doggyWalky.gps.repository.GpsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GpsWriter implements ItemWriter<Gps> {

    private final GpsRepository gpsRepository;


    @Override
    public void write(Chunk<? extends Gps> chunk) throws Exception {
        List<? extends Gps> items = chunk.getItems();
        gpsRepository.saveAll(items);
        log.info("GpsWriter 호출");
    }
}
