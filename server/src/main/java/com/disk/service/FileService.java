package com.disk.service;

import com.disk.dto.FileDTO;
import com.disk.dto.FolderDTO;
import com.disk.entity.FileMessage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    public String save(Long filePathId, MultipartFile file) throws IOException;
    public void createFolder(FolderDTO folderDTO);
    public void deleteByIds(List<Long> ids);
    public List<FileMessage> selectFolderContentById(Long id);
    public List<FileMessage> selectByIds(List<Long> ids);
    public FileMessage selectByUserIdAndUploadTime(Long userId);
}
