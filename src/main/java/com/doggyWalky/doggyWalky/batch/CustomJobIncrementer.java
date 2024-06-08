package com.doggyWalky.doggyWalky.batch;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

public class CustomJobIncrementer extends RunIdIncrementer {

    @Override
    public JobParameters getNext(JobParameters parameters) {
        JobParameters params = super.getNext(parameters);
        return new JobParametersBuilder(params)
                .addLong("currentTimeMillis", System.currentTimeMillis())
                .toJobParameters();
    }
}