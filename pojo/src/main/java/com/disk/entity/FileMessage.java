package com.disk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileMessage implements Serializable {
    private Long id;
    private Long userId;
    private String url;
    private String filePath;
    private String fileName;
    private Long filePathId;
    private LocalDateTime uploadTime;
    private Integer fileType;
    private Double fileSize;
}
