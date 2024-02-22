package com.doggyWalky.doggyWalky.file.repository;

import com.doggyWalky.doggyWalky.file.common.TableName;
import com.doggyWalky.doggyWalky.file.dto.response.FileResponseDto;
import com.doggyWalky.doggyWalky.file.entity.FileInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {


    @Query("select fi from FileInfo fi where fi.file.id = :fileId and fi.deletedAt is null")
    Optional<FileInfo> findFileInfo(@Param("fileId") Long fileId);

    Optional<FileInfo> findFileInfoByFileId(@Param("fileId") Long fileId);


    @Query("select fi from FileInfo fi where fi.tableName = :tableName and fi.tableKey = :tableKey and fi.deletedAt is null")
    List<FileInfo> findFileInfos(@Param("tableName") TableName tableName, @Param("tableKey") Long tableKey);


    @Query("select new com.doggyWalky.doggyWalky.file.dto.response.FileResponseDto(f.id,f.size,f.path,f.fileName,f.createdAt,fi.id) from FileInfo fi " +
            "join fi.file f where fi.tableName = :tableName and fi.tableKey =:tableKey and fi.deletedAt is null")
    List<FileResponseDto> findFilesByTableInfo(@Param("tableName") TableName tableName, @Param("tableKey") Long tableKey);
}
