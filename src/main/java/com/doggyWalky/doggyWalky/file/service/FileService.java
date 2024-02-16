package com.doggyWalky.doggyWalky.file.service;

import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.file.common.component.FileHandler;
import com.doggyWalky.doggyWalky.file.dto.request.FileRequestDto;
import com.doggyWalky.doggyWalky.file.dto.response.FileResponseDto;
import com.doggyWalky.doggyWalky.file.entity.File;
import com.doggyWalky.doggyWalky.file.entity.FileInfo;
import com.doggyWalky.doggyWalky.file.repository.FileInfoRepository;
import com.doggyWalky.doggyWalky.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileHandler fileHandler;

    private final FileRepository fileRepository;

    private final FileInfoRepository fileInfoRepository;



    // 파일 저장
    public List<FileResponseDto> uploadFile(List<MultipartFile> multipartFiles) {

        List<File> files = fileHandler.saveAll(multipartFiles);
        fileRepository.saveAll(files);

        return files
                .stream()
                .map(file -> FileResponseDto.response(file, true))
                .collect(Collectors.toList());

    }

    // 파일 1건 삭제 (file_info 테이블 소프트 삭제)
    public void delete(Long fileId) {
        FileInfo fileInfo = findFileInfoByFileId(fileId);
        fileInfo.deleteFileInfo();
    }

    // 파일 목록 삭제 (도메인 삭제시 사용, file_info 테이블 소프트 삭제)
    public void deleteFileList(FileRequestDto dto) {
        List<FileInfo> fileInfos = fileInfoRepository.findFileInfos(dto.getTableName().name(), dto.getTableKey());
        fileInfos.stream().forEach(fileInfo -> fileInfo.deleteFileInfo());
    }

    // 파일 목록 조회

    // 파일 정보 저장
    public void saveFileInfo(FileInfo fileInfo) {
        findFileInfoByFileId(fileInfo.getId());
        fileInfoRepository.save(fileInfo);
    }

    // 파일 정보 리스트 저장 (각 도메인 저장, 수정 시 사용)

    // 파일 조회

    // 파일 정보 조회

    // 파일 형식 체크

    // 파일 id로 파일 정보 조회
    private FileInfo findFileInfoByFileId(Long fileId) {
        return fileInfoRepository.findFileInfo(fileId).orElseThrow(() -> new ApplicationException(ErrorCode.FILE_NOT_FOUND));
    }

}
