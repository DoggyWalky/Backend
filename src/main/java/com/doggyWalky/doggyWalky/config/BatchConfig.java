package com.doggyWalky.doggyWalky.config;

import com.doggyWalky.doggyWalky.batch.CustomJobIncrementer;
import com.doggyWalky.doggyWalky.batch.processor.GpsProcessor;
import com.doggyWalky.doggyWalky.batch.writer.GpsWriter;
import com.doggyWalky.doggyWalky.gps.dto.request.GpsRequestDto;
import com.doggyWalky.doggyWalky.gps.entity.Gps;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@EnableBatchProcessing
//@EnableConfigurationProperties(BatchProperties.class)
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final RedisTemplate<String,String> redisTemplate;

    private final ObjectMapper objectMapper;

    private final GpsWriter gpsWriter;

    private final GpsProcessor gpsProcessor;

    private final StepExecutionListener GpsStepListener;

    private final JobExecutionListener GpsJobListener;


    @Bean
    public Job myJob(Step step) {
        return new JobBuilder("gpsJob", this.jobRepository)
                .incrementer(new CustomJobIncrementer())
                .listener(GpsJobListener)
                .start(step)
                .build();
    }

    @Bean
    public Step step() throws JsonProcessingException {
        return new StepBuilder("gpsStep", this.jobRepository)
                .<GpsRequestDto, Gps>chunk(100, platformTransactionManager)
                .reader(itemReader(null))
                .processor(gpsProcessor)
                .writer(gpsWriter)
                .taskExecutor(taskExecutor())
                .build();
    }
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(5); // 동시에 실행할 최대 스레드 수
        return asyncTaskExecutor;
    }


    @Bean
    @StepScope
    public ItemReader<GpsRequestDto> itemReader(@Value("#{jobParameters[dataIdentifier]}") String dataIdentifier) throws JsonProcessingException {
        // dataIdentifier를 사용하여 실제 데이터 로딩
        if (dataIdentifier == null) {
            throw new IllegalArgumentException("Job parameter 'dataIdentifier' is required");
        }
        List<GpsRequestDto> inputData = loadDataByIdentifier(dataIdentifier);
        if (inputData == null || inputData.isEmpty()) {
            throw new IllegalArgumentException("No data found for identifier: " + dataIdentifier);
        }
        return new ListItemReader<>(inputData);
    }

    private List<GpsRequestDto> loadDataByIdentifier(String identifier) throws JsonProcessingException {
        // 식별자를 사용하여 저장소에서 데이터 로딩 로직 구현
        // 예: 파일 시스템, 데이터베이스, 또는 다른 저장소에서 데이터 로딩
        List<GpsRequestDto> data = objectMapper.readValue(redisTemplate.opsForValue().get(identifier), new TypeReference<List<GpsRequestDto>>() {
        });
        return data;
    }

}
