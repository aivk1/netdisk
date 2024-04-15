package com.disk.service.impl;

import com.disk.constant.FileTypeConstant;
import com.disk.constant.MessageConstant;
import com.disk.context.BaseContext;
import com.disk.dto.FileDTO;
import com.disk.dto.FolderDTO;
import com.disk.entity.FileMessage;
import com.disk.mapper.FileMapper;
import com.disk.service.FileService;
import com.disk.utils.AliOssUtil;
import com.disk.utils.FileUtil;
import com.disk.vo.FileMessagesVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private FileUtil fileUtil;
    @Override
    @Transactional
    public String save(Long filePathId, MultipartFile file) throws IOException {
        //获取文件id为fileDTO.getFilePathId的文件路径（包括此文件）
        FileMessage temp = getFilePathById(filePathId);
        String name = file.getOriginalFilename();
        String folderPath = temp.getFilePath() + "/" + name;

        //获取文件类型，经过双重杨正确认其文件类型
        Integer fileType = fileUtil.getFileTypeByExtension(file.getOriginalFilename());
        String url = aliOssUtil.upload(file.getBytes(), folderPath);
        if(fileType==-1||fileType!=fileUtil.getFileType(file)){
            throw new FileUploadException(MessageConstant.UPLOAD_FILE_ERROR);
        }
        FileMessage fileMessage = FileMessage.builder()
                .userId(BaseContext.getCurrentId())
                .url(url)
                .fileName(name)
                .filePath(folderPath)
                .filePathId(filePathId)
                .fileSize(file.getSize()/(1024.0 * 1024.0))
                .fileType(fileType)
                .uploadTime(LocalDateTime.now())
                .build();
        fileMapper.insert(fileMessage);
        return url;
    }

    public List<FileMessage> selectByIds(List<Long> ids){
        return fileMapper.selectByIds(ids);
    }

    public void createFolder(FolderDTO folderDTO){
        FileMessage temp = getFilePathById(folderDTO.getFilePathId());
//        String url = aliOssUtil.createFolder(temp.getFilePath()+"/"+folderDTO.getFolderName());
        FileMessage fileMessage = FileMessage.builder()
                .userId(BaseContext.getCurrentId())
                .url("")
                .fileType(FileTypeConstant.FOLDER_TYPE)
                .fileName(folderDTO.getFolderName())
                .filePath(temp.getFilePath()+"/"+folderDTO.getFolderName())
                .filePathId(folderDTO.getFilePathId())
                .uploadTime(LocalDateTime.now())
                .fileSize(0.0)
                .build();
        fileMapper.insert(fileMessage);
    }

    @Override
    public void deleteByIds(List<Long> ids){
        List<FileMessage> fileMessages = fileMapper.selectByIds(ids);
        List<Long> fileIds = new ArrayList<>();
        for(FileMessage fileMessage:fileMessages){
            if(fileMessage.getFileType()==0){//为文件夹类型
                aliOssUtil.deleteFolderAndContent(fileMessage.getFilePath());
            }
            else{
                aliOssUtil.delete(fileMessage.getFilePath());
            }
            fileIds.add(fileMessage.getId());
        }
        fileMapper.deleteByIdsOrFilePathIds(ids);

    }

    public List<FileMessage> selectFolderContentById(Long folderId){
        List<FileMessage> fileMessages = fileMapper.selectFolderContentById(folderId);
        for(FileMessage fileMessage:fileMessages){
            if(fileMessage.getFileType()==0){
                fileMessage.setFileSize(this.getFolderSize(fileMessage.getId()));
            }
        }

        return fileMessages;
    }

    public FileMessage selectByUserIdAndUploadTime(Long userId){
        return fileMapper.selectByUserIdAndUploadTime(userId);
    }
    private FileMessage getFilePathById(Long filePathId){
        List<Long> ids = new ArrayList<>();
        ids.add(filePathId);
        List<FileMessage> fileMessages = fileMapper.selectByIds(ids);
        FileMessage fileMessage = fileMessages==null||fileMessages.size()==0?null:fileMessages.get(0);
        return fileMessage;
    }
    protected Double getFolderSize(Long id){
        Double result = 0.0;
        List<FileMessage> fileMessages = fileMapper.selectFolderContentById(id);
        for(FileMessage fileMessage:fileMessages){
            //文件夹类型
            if(fileMessage.getFileType()==0){
                result+=getFolderSize(fileMessage.getId());
            }
            else{
                result+=fileMessage.getFileSize();
            }
        }
        return result;
    }

}
