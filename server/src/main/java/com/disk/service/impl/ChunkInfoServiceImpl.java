package com.disk.service.impl;

import com.disk.constant.MessageConstant;
import com.disk.constant.StatusConstant;
import com.disk.context.BaseContext;
import com.disk.entity.ChunkInfo;
import com.disk.entity.FileInfo;
import com.disk.mapper.ChunkInfoMapper;
import com.disk.mapper.FileInfoMapper;
import com.disk.result.ChunkResult;
import com.disk.service.ChunkInfoService;
import com.disk.utils.FileInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ChunkInfoServiceImpl implements ChunkInfoService {
    @Value("${base.file_path}")
    private String uploadFolder;
    @Autowired
    private ChunkInfoMapper chunkInfoMapper;
    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChunkResult checkChunkState(ChunkInfo chunkInfo, HttpServletResponse response) {
        ChunkResult result = new ChunkResult();
        //identifier是用来标识一个文件的身份标志，也能很好的解决不同用户之间文件同名的冲突
        String file = uploadFolder + File.separator + chunkInfo.getRelativePath() + File.separator+ chunkInfo.getIdentifier() + File.separator + chunkInfo.getFilename();
//        String file = null;
        if(FileInfoUtils.fileExists(file)){
            //检查原来的文件有没有被移动,如果移动了则插入一条新数据
            checkFileInOriginalPath(chunkInfo);

            result.setSkipUpload(true);
            result.setLocation(file);
            response.setStatus(HttpServletResponse.SC_OK);
            result.setMsg(MessageConstant.CHUNK_EXIST);
            return result;
        }
        List<Long> chunks = chunkInfoMapper.selectChunkNumbers(chunkInfo);
        if(chunks!=null&&chunks.size()>0){
            result.setSkipUpload(false);
            result.setUploadedChunks(chunks);
            response.setStatus(HttpServletResponse.SC_OK);
            result.setMsg(MessageConstant.CHUNK_EXIST);
        }
        return result;
    }

    @Override
    public Integer uploadFile(ChunkInfo chunk) {
        Integer apiRlt = HttpServletResponse.SC_OK;
        MultipartFile file = chunk.getUploadFile();
        chunk.setId(String.valueOf(this.getMaxChunkId()+1));
        try{
            byte[] bytes = file.getBytes();
            Path path = Paths.get(FileInfoUtils.generatePath(uploadFolder, chunk));
            Files.write(path, bytes);
            if(chunkInfoMapper.insert(chunk)<0){
                apiRlt = HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
            }
        } catch (Exception e) {
            log.error("写文件出错:{}",e.getMessage());
            apiRlt = HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
        }
        return apiRlt;
    }

    /**
     * 通过userId,refProjectId,identifier来确认文件有没有被移动，不在refProjectId的虚拟路径中则插入
     * @param chunk
     * @return
     */
    protected void checkFileInOriginalPath(ChunkInfo chunk){
        List<FileInfo> fileInfos = fileInfoMapper.selectFileList(FileInfo.builder()
                        .uploadUser(4l)
//                        .uploadUser(BaseContext.getCurrentId())
                        .identifier(chunk.getIdentifier())
                        .build());

        if(fileInfos!=null&&fileInfos.size()>0){
            for(FileInfo fileInfo:fileInfos){
                //文件还在当前的虚拟路径中
                if(fileInfo.getRefProjectId().equals(Long.valueOf(chunk.getRelativePath()))){
                    return;
                }
            }
            FileInfo fileInfo = fileInfos.get(0);
            fileInfo.setRefProjectId(Long.valueOf(chunk.getRelativePath()));
            fileInfo.setUploadTime(LocalDateTime.now());
            fileInfoMapper.insert(fileInfo);
        }
    }

    /**
     * 获得最大的ChunkId
     * @return
     */
    protected Long getMaxChunkId(){
        List<Long> chunkInfos = chunkInfoMapper.selectIds();
        if(chunkInfos!=null&&chunkInfos.size()>0){
            return chunkInfos.get(0);
        }
        return 0l;
    }
}
