package com.disk.controller.user;

import com.disk.constant.MessageConstant;
import com.disk.context.BaseContext;
import com.disk.dto.FileDTO;
import com.disk.dto.FolderDTO;
import com.disk.entity.FileMessage;
import com.disk.exception.FileAccessException;
import com.disk.result.Result;
import com.disk.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    @Autowired
    private FileService fileService;
    

    /**
     * 文件上传接口
     * @param
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(Long filePathId, MultipartFile file){
        try {
            String filePath = fileService.save(filePathId, file);
            return Result.success(filePath);
        } catch (IOException e) {
            e.printStackTrace();

//            log.error("文件上传失败：{}",e);
        }
        return Result.error("文件上传失败");
    }

    /**
     * 文件删除接口
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(cacheNames = "netDiskFile", allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        fileService.deleteByIds(ids);
        return Result.success();
    }

    /**
     * 创建文件接口
     * @param folderDTO
     * @return
     */
    @PostMapping("/createFolder")
    @CacheEvict(cacheNames = "netDiskFile", allEntries = true)
    public Result createFolder(@RequestBody FolderDTO folderDTO){
        fileService.createFolder(folderDTO);
        return Result.success();
    }

    /**
     * 查找id为folderId的文件夹下的内容
     * @param folderId
     * @return
     */
    @GetMapping("/getFile/{folderId}")
    @Cacheable(cacheNames = "netDiskFile", key = "#folderId")
    public Result<List<FileMessage>> selectByFolderId(@PathVariable("folderId") Long folderId){
        List<Long> ids = new ArrayList<Long>();
        ids.add(folderId);
        List<FileMessage> fileMessages = fileService.selectByIds(ids);
        if(fileMessages==null||fileMessages.size()==0){
            throw new FileAccessException(MessageConstant.FILE_EXIST_ERROR);
        }
        else if(BaseContext.getCurrentId()!=fileMessages.get(0).getUserId()){
            throw new FileAccessException(MessageConstant.FILE_ACCESS_DENIED);
        }
        fileMessages = fileService.selectFolderContentById(folderId);
        return Result.success(fileMessages);
    }


}
