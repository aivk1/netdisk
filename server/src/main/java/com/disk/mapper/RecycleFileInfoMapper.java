package com.disk.mapper;

import com.disk.entity.FileInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecycleFileInfoMapper {
//    void insert(List<Long> ids);
    List<FileInfo> selectALLFile(Long UserId);
    void recoverFile(List<Long> ids, Long originalFolderId);
    void delete(List<Long> ids);
    List<FileInfo> selectFileByIds(List<Long> ids);
}
