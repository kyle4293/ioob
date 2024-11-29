package com.ioob.backend.global.controller;

import com.ioob.backend.domain.kanban.entity.TaskFile;
import com.ioob.backend.domain.kanban.repository.TaskFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final TaskFileRepository taskFileRepository;

    @Value("${file.upload.dir}")
    private String uploadDir;

    @GetMapping("/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable Long fileId) {
        try {
            TaskFile taskFile = taskFileRepository.findById(fileId)
                    .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

            Path filePath = Paths.get(uploadDir).resolve(taskFile.getFileUrl()).normalize();
            if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                throw new IllegalArgumentException("파일이 존재하지 않거나 읽을 수 없습니다.");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new IllegalArgumentException("리소스를 읽을 수 없습니다.");
            }

            String contentType = Files.probeContentType(filePath);
            boolean isImage = contentType != null && contentType.startsWith("image/");
            String encodedFileName = URLEncoder.encode(taskFile.getFileName(), StandardCharsets.UTF_8)
                    .replace("+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            isImage
                                    ? "inline; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName
                                    : "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName)
                    .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "application/octet-stream")
                    .body(resource);

        } catch (Exception e) {
            throw new RuntimeException("파일 처리 중 오류 발생: " + e.getMessage(), e);
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        try {
            TaskFile taskFile = taskFileRepository.findById(fileId)
                    .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

            Path filePath = Paths.get(uploadDir).resolve(taskFile.getFileUrl()).normalize();
            if (!Files.exists(filePath)) {
                throw new IllegalArgumentException("파일이 존재하지 않습니다.");
            }

            Files.delete(filePath);
            taskFileRepository.delete(taskFile);

            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            throw new RuntimeException("파일 삭제 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
