package com.disk.service;


import com.disk.entity.FileInfo;

import java.util.List;

public interface RecycleBinService {
    /**
     * 获得所有删除的文件
     * @param userId
     * @return
     */
    public List<FileInfo> getAllFileInfo(Long userId);

    /**
     * 恢复所选的文件 到根目录
     * @param ids
     */
    public void recoverFile(List<Long> ids);

    /**
     * 删除所选文件
     * @param ids
     */
    public void deleteFile(List<Long> ids);
}
