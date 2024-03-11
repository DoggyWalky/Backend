package com.doggyWalky.doggyWalky.file.repository;

import com.doggyWalky.doggyWalky.file.entity.File;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    @Query("delete from File f where f.id = :fileId")
    void hardDeleteFile(@Param("fileId") Long fileId);

    @Query("select f from File f where f.id NOT IN (select fi.file.id from FileInfo fi)")
    List<File> findFilesWithNoFileInfo();

}
