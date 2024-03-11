package com.disk.service;

import com.disk.entity.FileMessage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    public String save(String folderPath, MultipartFile file) throws IOException;
    public void deleteByIds(List<Long> ids);
    public List<FileMessage> selectByFolderPath(String folderPath);
}
