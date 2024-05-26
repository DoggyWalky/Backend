package com.doggyWalky.doggyWalky.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class GpsJobListener implements JobExecutionListener {
    /**
     * 배치 작업(Job)의 시작 시점에 beforeJob 메서드가 호출됩니다.
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    /**
     * 모든 Step들이 작업을 완료한 후 즉, 배치 작업(Job)이 완료되는 시점에 afterJob 메서드가 호출됩니다.
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        JobExecutionListener.super.afterJob(jobExecution);
    }
}
