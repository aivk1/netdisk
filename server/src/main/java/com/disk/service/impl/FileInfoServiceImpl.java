package com.disk.service.impl;

import java.io.File;

import com.disk.constant.StatusConstant;
import com.disk.context.BaseContext;
import com.disk.dto.FileInfoDTO;
import com.disk.dto.FolderDTO;
import com.disk.entity.ChunkInfo;
import com.disk.entity.FileInfo;
import com.disk.mapper.ChunkInfoMapper;
import com.disk.mapper.FileInfoMapper;
import com.disk.mapper.RecycleFileInfoMapper;
import com.disk.service.FileInfoService;
import com.disk.utils.FileInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FileInfoServiceImpl implements FileInfoService {
    @Value("${base.file_path}")
    private String uploadFolder;
    @Autowired
    private ChunkInfoMapper chunkInfoMapper;
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private RecycleFileInfoMapper recycleFileInfoMapper;

    @Transactional
    @Override
    public HttpServletResponse mergeFile(FileInfoDTO fileInfoDTO, HttpServletResponse response) {
        //refProjectId作为virtualLocationId
        FileInfo fileInfo = FileInfo.builder()
                .fileName(fileInfoDTO.getName())
                .identifier(fileInfoDTO.getUniqueIdentifier())
                .fileType(fileInfoDTO.getFileType())
                .refProjectId(fileInfoDTO.getRefProjectId())
                .uploadUser(4l)
                .refProjectId(fileInfoDTO.getRefProjectId())
//                .uploadUser(BaseContext.getCurrentId())
                .uploadTime(LocalDateTime.now())
                .build();
        Long fileSize = getFileSize(fileInfo);
        fileInfo.setTotalSize(fileSize);
        fileInfo.setTotalSizeName(fileSize);


        response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        String fileName = fileInfo.getFileName();
        String file = uploadFolder +
                File.separator+ "4" +
                File.separator + fileInfo.getRefProjectId() +
                File.separator + fileInfo.getIdentifier() +
                File.separator + fileName;
        String folder = uploadFolder +
                File.separator + "4" +
                File.separator + fileInfo.getRefProjectId() +
                File.separator + fileInfo.getIdentifier();
        Integer fileSuccess = FileInfoUtils.merge(file, folder, fileName);
        fileInfo.setLocation(folder);


        if(fileSuccess == HttpServletResponse.SC_OK){
            ChunkInfo chunkInfo = ChunkInfo.builder()
                    .identifier(fileInfo.getIdentifier())
                    .filename(fileInfo.getFileName())
                    .build();
            chunkInfoMapper.delete(chunkInfo);
            fileInfoMapper.insert(fileInfo);
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else if(fileSuccess == HttpServletResponse.SC_MULTIPLE_CHOICES){
            List<FileInfo> tfList = fileInfoMapper.selectFileList(fileInfo);
            if(tfList.size()==0){
                fileInfoMapper.insert(fileInfo);
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else{
                for(FileInfo info : tfList){
                    if(info.getRefProjectId().equals(fileInfo.getRefProjectId())){
                        fileInfoMapper.deleteByIdentifierAndFileName(info);
                        fileInfoMapper.insert(fileInfo);
                        response.setStatus(HttpServletResponse.SC_MULTIPLE_CHOICES);
                    }
                }
            }
        }else{
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        return response;
    }

    @Override
    public List<FileInfo> selectFileList(Long refProjectId) {
        return fileInfoMapper.selectByRefProjectId(refProjectId);
    }

    @Override
    public void createFolder(FolderDTO folderDTO){
        FileInfo fileInfo = FileInfo.builder()
                .identifier("/")
                .totalSize(0l)
                .fileName(folderDTO.getFolderName())
                .refProjectId(folderDTO.getFilePathId())
                .uploadUser(4l)
//                .uploadUser(BaseContext.getCurrentId())
                .fileType("folder")
                .uploadTime(LocalDateTime.now())
                .location("/")
                .build();
        fileInfoMapper.insert(fileInfo);
    }

    @Override
    public List<FileInfo> selectFileList(String folder) {
//        List<FileInfo> result = fileInfoMapper.selectByLocation(folder);
        return null;
    }

    @Override
    @Transactional
    public Integer deleteFile(List<Long> ids) {
        List<Long> res = new ArrayList<Long>();
        List<FileInfo> fileInfos = fileInfoMapper.selectFileByIds(ids);
        if(fileInfos!=null&&fileInfos.size()!=0&&fileInfos.get(0).getUploadUser()!=4l){
            return -1;
        }
        deleteFolder(fileInfos, res);
//        recycleFileInfoMapper.insert(res, BaseContext.getCurrentId());
//        recycleFileInfoMapper.insert(res);
        if(res!=null&&res.size()!=0) {
//            fileInfoMapper.deleteByIds(res);
            fileInfoMapper.setFileStatusByIds(res, StatusConstant.FILE_RECYCLE);
        }
        return 1;
//        return fileInfoMapper.deleteByIds(BaseContext.getCurrentId(), ids)
    }

    public FileInfo getAloneFile(Long fileId){
        List<Long> ids = new ArrayList<>();
        ids.add(fileId);
        List<FileInfo> fileInfos = fileInfoMapper.selectFileByIds(ids);
        if(fileInfos!=null&&fileInfos.size()!=0&&fileInfos.get(0).getUploadUser()!=4l)
//        if(fileInfos!=null&&fileInfos.size()!=0&&fileInfos.get(0).getUploadUser()!= BaseContext.getCurrentId())
        {
            return null;
        }
        return fileInfos.get(0);
    }

    public List<FileInfo> selectFileList(List<Long> ids) {
        return fileInfoMapper.selectFileByIds(ids);
    }

    /**
     * 获取文件大小
     * @param fileInfo
     * @return
     */
    protected Long getFileSize(FileInfo fileInfo) {
        Long fileSize = 0L;
        List<ChunkInfo> chunkInfos = chunkInfoMapper.selectChunks(fileInfo.getIdentifier(), fileInfo.getFileName());
        if(chunkInfos!=null && chunkInfos.size()!=0){
            for(ChunkInfo chunkInfo : chunkInfos){
                fileSize += chunkInfo.getCurrentChunkSize();
            }
        }
        return fileSize;
    }

    /**
     * 生成正确的文件名
     * @param fileInfos
     * @param folderName
     * @return
     */
    protected String checkNameMultiply(List<FileInfo> fileInfos, String folderName){
        int res = 0;
        for(FileInfo fileInfo:fileInfos){
            String tmpFileName = fileInfo.getFileName();
            if(tmpFileName.lastIndexOf('(')!=-1){
                if(folderName.equals(tmpFileName.substring(tmpFileName.lastIndexOf('(')))){
                    res+=1;
                }
            }
        }
        return folderName + "(" + res + ")";
    }

    /**
     * 用于删除文件时递归删除
     * @param fileInfos
     * @param res
     */
    protected void deleteFolder(List<FileInfo> fileInfos, List<Long> res){
        for(FileInfo fileInfo:fileInfos){
            if(fileInfo.getFileType().equals("folder")){
                 List<FileInfo> fileInfoList = fileInfoMapper.selectByRefProjectId(fileInfo.getId());
                if(fileInfoList!=null && fileInfoList.size()!=0){
                    deleteFolder(fileInfoList, res);
                }
            }
            res.add(fileInfo.getId());
        }
    }
}
