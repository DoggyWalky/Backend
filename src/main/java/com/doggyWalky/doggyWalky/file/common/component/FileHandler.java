package com.doggyWalky.doggyWalky.file.common.component;

import com.doggyWalky.doggyWalky.file.entity.File;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.stream.Collectors;

public interface FileHandler {
    File save(MultipartFile file);

    default List<File> saveAll(List<MultipartFile> multipartFileList) {
        return multipartFileList
                .parallelStream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    java.io.File download(String fileName);

    default List<java.io.File> downloadAll(List<String> fileNameList) {
        return fileNameList
                .parallelStream()
                .map(this::download)
                .collect(Collectors.toList());
    }

    void delete(String fileName);

    default void deleteAll(List<String> fileNameList) {
        fileNameList.parallelStream().forEach(this::delete);
    }
}
