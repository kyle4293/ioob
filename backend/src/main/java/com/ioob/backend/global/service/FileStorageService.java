package com.ioob.backend.global.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final String uploadDir = "/uploads/";

    public String saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
            return filePath.toString(); // 저장된 파일 경로 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
