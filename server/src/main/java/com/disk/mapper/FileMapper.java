package com.disk.mapper;

import com.disk.annotation.AutoFill;
import com.disk.entity.FileMessage;
import com.disk.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileMapper {
    List<FileMessage> selectByIds(List<Long> ids);
    void insert(FileMessage fileMessage);
    void deleteByIds(List<Long> ids);
    List<FileMessage> selectFolderContentById(Long id);
    void deleteByFolderPath(List<String> folderPaths);
    void deleteByIdsOrFilePathIds(List<Long> ids);
    FileMessage selectByUserIdAndUploadTime(Long userId);
}
