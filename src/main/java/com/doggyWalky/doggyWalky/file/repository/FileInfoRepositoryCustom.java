package com.doggyWalky.doggyWalky.file.repository;

import com.doggyWalky.doggyWalky.file.dto.schedule.DeletedFileInfo;

import java.util.List;

public interface FileInfoRepositoryCustom {

    List<DeletedFileInfo> findDeletedFileInfo();
}
