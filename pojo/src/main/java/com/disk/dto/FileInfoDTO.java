package com.disk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileInfoDTO {
    private String id;
    private String name;
    private String size;
    private String fileType;
    private boolean isFolder;
    private boolean isRoot;
    private String uniqueIdentifier;
    private String relativePath;
    private Long refProjectId;
}
