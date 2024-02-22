package com.doggyWalky.doggyWalky.file.controller;

import com.doggyWalky.doggyWalky.file.dto.request.FileRequestDto;
import com.doggyWalky.doggyWalky.file.dto.response.FileResponseDto;
import com.doggyWalky.doggyWalky.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping("/files")
    public ResponseEntity getFileList(@RequestBody FileRequestDto dto) {
        List<FileResponseDto> response = fileService.findFilesByTableInfo(dto, true);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/files")
    public ResponseEntity postFile(@RequestParam List<MultipartFile> files) {
        List<FileResponseDto> response = fileService.uploadFile(files);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
