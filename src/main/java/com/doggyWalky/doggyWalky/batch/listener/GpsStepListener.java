package com.doggyWalky.doggyWalky.batch.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GpsStepListener implements StepExecutionListener {

    private final RedisTemplate<String,String> redisTemplate;

    /**
     * chunk 단위로 Step이 실행되고 각각의 Step이 끝났을 때 afterStep 메서드가 호출됩니다.
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
            log.info("Step 완료 후 처리");
            redisTemplate.delete("gpsDataKey");

        return stepExecution.getExitStatus();
    }
}
