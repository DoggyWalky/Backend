package com.doggyWalky.doggyWalky.jobpost.repository;

import com.doggyWalky.doggyWalky.file.dto.schedule.DeletedFileInfo;

import java.util.List;

public interface JobPostRepositoryCustom {

    List<Long> findDeletedJobPost();

    void batchDeleteJobPost(List<Long> jobPostList);
}
