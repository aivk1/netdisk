package com.disk.service;

import com.disk.dto.FileInfoDTO;
import com.disk.dto.FolderDTO;
import com.disk.entity.FileInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface FileInfoService {
    /**
     * 合并文件
     * @param fileInfoDTO
     * @param response
     * @return
     */
    HttpServletResponse mergeFile(FileInfoDTO fileInfoDTO, HttpServletResponse response);

    /**
     * 查询文件信息
     * @param folder
     * @return
     */
    List<FileInfo> selectFileList(String folder);

    /**
     * 根据所在虚拟文件路径查询文件内容
     * @param folderId
     * @return
     */
    List<FileInfo> selectFileList(Long folderId);

    /**
     * 创建文件夹
     * @param folderDTO
     */
    void createFolder(FolderDTO folderDTO);

    /**
     * 删除
     * @param ids
     * @return
     */
    Integer deleteFile(List<Long> ids);

    /**
     * 获取当个文件
     * @param fileId
     * @return
     */
     FileInfo getAloneFile(Long fileId);

    /**
     * 根据获取文件列表
     * @param ids
     * @return
     */
    List<FileInfo> selectFileList(List<Long> ids);
}
