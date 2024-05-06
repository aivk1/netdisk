package com.disk.mapper;

import com.disk.entity.FileInfo;
import org.apache.ibatis.annotations.*;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface FileInfoMapper {
    List<FileInfo> selectFileList(FileInfo fileInfo);
    List<FileInfo> selectByRefProjectId(Long refProjectId);
    List<FileInfo> selectFileByIds(List<Long> ids);
    @Insert("insert into t_file_info( FILENAME, IDENTIFIER, TOTAL_SIZE, LOCATION, FILETYPE, REF_PROJECT_ID, UPLOAD_USER, UPLOAD_TIME)" +
            "values(" +
            "#{fileName}, #{identifier}," +
            "#{totalSize}, #{location}, #{fileType}," +
            "#{refProjectId}, #{uploadUser}, #{uploadTime})")
    void insert(FileInfo fileInfo);
    @Delete("delete from t_file_info where IDENTIFIER = #{identifier} and FILENAME = #{fileName} and UPLAD_USER = #{uploadUser}")
    Integer deleteByIdentifierAndFileName(FileInfo fileInfo);
    void deleteByIds(@Param("ids")List<Long> ids);
    void setFileStatusByIds(@Param("ids")List<Long> ids, Integer status);
}
