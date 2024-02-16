package com.doggyWalky.doggyWalky.file.repository;

import com.doggyWalky.doggyWalky.file.entity.FileInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

    @Query("select fi from FileInfo fi where fi.file.id = :fileId and fi.deletedAt = 0")
    Optional<FileInfo> findFileInfo(@Param("fileId") Long fileId);

    @Query("select fi from FileInfo fi where fi.tableName = :tableName and fi.tableKey = :tableKey and fi.deletedAt = 0")
    List<FileInfo> findFileInfos(@Param("tableName") String tableName, @Param("tableKey") Long tableKey);
}
