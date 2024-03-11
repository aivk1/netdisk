package com.disk.controller.user;

import com.disk.entity.FileMessage;
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
import java.util.List;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    @Autowired
    private FileService fileService;
    

    /**
     * 文件上传接口
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(String folderPath, MultipartFile file){
        try {
            String filePath = fileService.save(folderPath, file);
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

    @GetMapping("/{folderPath}")
    @Cacheable(cacheNames = "netDiskFile", key = "#folderPath")
    public Result<List<FileMessage>> selectByFolderPath(@PathVariable("folderPath") String folderPath){
        List<FileMessage> temp = fileService.selectByFolderPath(folderPath);
        return Result.success(temp);
    }


}
