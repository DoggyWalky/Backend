package com.doggyWalky.doggyWalky.file.repository;

import com.doggyWalky.doggyWalky.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
