package com.doggyWalky.doggyWalky.jobpost.scheduler;

import com.doggyWalky.doggyWalky.file.dto.schedule.DeletedFileInfo;
import com.doggyWalky.doggyWalky.file.entity.File;
import com.doggyWalky.doggyWalky.jobpost.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class JobPostCleanUpScheduler {

    private final JobPostRepository jobPostRepository;

    /**
     * 소프트 삭제된 지 3개월이 지난 게시글은 정기적으로 스케줄러를 통해 삭제됩니다.
     * 매일 자정에 스케줄링이 동작하게 됩니다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanUpExpiredJobPosts() {
        // 삭제해야할 파일 정보 찾기
        // Mysql RDS 인스턴스 타임존 설정을 UTC로 해서 쿼리상으로 KST 시간으로 변환해줬다
        log.info("소프트 삭제 후 3개월 지난 게시글 정기 삭제 스케줄러 동작");
        List<Long> deletedJobPostIdList = jobPostRepository.findDeletedJobPost();
        log.info("삭제 대상 파일 Info size: " + deletedJobPostIdList.size());

        jobPostRepository.batchDeleteJobPost(deletedJobPostIdList);

    }
}
