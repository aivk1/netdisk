package com.disk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileMessage {
    private Long id;
    private Long userId;
    private String filePath;
    private String fileName;
    private LocalDateTime uploadTime;
    private double fileSize;
}
