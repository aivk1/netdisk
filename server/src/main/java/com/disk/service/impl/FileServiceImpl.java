package com.disk.service.impl;

import com.disk.context.BaseContext;
import com.disk.entity.FileMessage;
import com.disk.mapper.FileMapper;
import com.disk.service.FileService;
import com.disk.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private AliOssUtil aliOssUtil;
    @Override
    @Transactional
    public String save(String folderPath, MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        name = String.valueOf(BaseContext.getCurrentId()) + "/" + folderPath + "/" + name;
        String filePath = aliOssUtil.upload(file.getBytes(), name);
        FileMessage fileMessage = FileMessage.builder()
                .userId(BaseContext.getCurrentId())
                .fileName(name)
                .filePath(filePath)
                .fileSize(file.getSize()/(1024.0 * 1024.0))
                .uploadTime(LocalDateTime.now())
                .build();
        fileMapper.insert(fileMessage);
        return filePath;
    }

    @Override
    public void deleteByIds(List<Long> ids){
        List<FileMessage> fileMessages = fileMapper.selectByIds(ids);
        for(FileMessage fileMessage:fileMessages){
            aliOssUtil.delete(fileMessage.getFileName());
        }
        fileMapper.deleteByIds(ids);
    }

    public List<FileMessage> selectByFolderPath(String folderPath){
        return fileMapper.selectByFolderPath(folderPath);
    }

}
