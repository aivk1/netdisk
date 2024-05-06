package com.disk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChunkInfo {
    private String id;//chunkId
    private Long chunkNumber;
    private Long chunkSize;
    private Long currentChunkSize;
    private String identifier;
    private String filename;
    private String relativePath;
    private Integer totalChunks;
    private Integer totalSize;
    private String fileType;
    private MultipartFile uploadFile;
}
