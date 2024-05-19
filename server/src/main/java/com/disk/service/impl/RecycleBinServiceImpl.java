package com.disk.service.impl;

import com.disk.context.BaseContext;
import com.disk.entity.FileInfo;
import com.disk.mapper.FileInfoMapper;
import com.disk.mapper.RecycleFileInfoMapper;
import com.disk.service.RecycleBinService;
import com.disk.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecycleBinServiceImpl implements RecycleBinService {
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private RecycleFileInfoMapper recycleFileInfoMapper;

    public List<FileInfo> getAllFileInfo(Long userId){
        return recycleFileInfoMapper.selectALLFile(userId);
    }

    public void recoverFile(List<Long> ids){
        List<FileInfo> fileInfos = fileInfoMapper.selectFileByIds(ids);
        FileInfo info = FileInfo.builder()
//                .uploadUser(BaseContext.getCurrentId())
                .uploadUser(4l)
                .build();
        List<FileInfo> fileInfoList = fileInfoMapper.selectFileList(info);
        info =  fileInfoList.get(0);
        recycleFileInfoMapper.recoverFile(ids, info.getId());
    }

    public void deleteFile(List<Long> ids){
        List<FileInfo> fileInfos = recycleFileInfoMapper.selectFileByIds(ids);
        recycleFileInfoMapper.delete(ids);
        for(int i=0;i<fileInfos.size();i++){
            if(!checkFileLocationIsUsing(fileInfos.get(i).getLocation(), fileInfos.get(i).getFileName())){
                FileUtil.deleteFileAndParent(fileInfos.get(0).getLocation()+fileInfos.get(0).getFileName());
            }
        }
    }

    /**
     * 检查location有没有被其他文件使用
     * @param location 文件的location
     * @param fileName 文件的文件名
     * @return
     */
    protected boolean checkFileLocationIsUsing(String location, String fileName){
        FileInfo fileInfo = FileInfo.builder()
                .location(location)
                .build();
        List<FileInfo> fileInfos = fileInfoMapper.selectFileList(fileInfo);
        return fileInfos!=null&&fileInfos.size()>0;
    }
}
