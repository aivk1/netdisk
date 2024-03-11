package com.disk.mapper;

import com.disk.entity.FileMessage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FileMapper {

    void insert(FileMessage fileMessage);
    void deleteByIds(List<Long> ids);
    List<FileMessage> selectByIds(List<Long> ids);
    @Select("select * from file_message where file_name={folderPath}")
    List<FileMessage> selectByFolderPath(String folderPath);

}
