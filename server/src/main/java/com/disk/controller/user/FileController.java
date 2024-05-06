package com.disk.controller.user;

import com.disk.dto.FileInfoDTO;
import com.disk.dto.FolderDTO;
import com.disk.entity.ChunkInfo;
import com.disk.entity.FileInfo;
import com.disk.entity.FileMessage;
import com.disk.exception.FileAccessException;
import com.disk.result.ChunkResult;
import com.disk.result.Result;
import com.disk.service.ChunkInfoService;
import com.disk.service.FileInfoService;
import com.disk.utils.AliOssUtil;
import com.disk.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
//    @Autowired
//    private FileService fileService;
    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private ChunkInfoService chunkInfoService;
    @Autowired
    private FileInfoService fileInfoService;

    /**
     * 创建文件夹API
     * @param folderDTO
     * @return
     * @throws FileAccessException
     */
    @PostMapping("/createFolder")
    @CacheEvict(value = "netDiskFile", key = "#folderDTO.getFilePathId()")
    public Result createFolder(@RequestBody FolderDTO folderDTO) throws FileAccessException {
        fileInfoService.createFolder(folderDTO);
        return Result.success();
    }

    /**
     * 获取文件夹内的内容
     * @param folderId
     * @return
     */
    @GetMapping("/getFolderContent")
    @Cacheable(value = "netDiskFile", key = "#folderId")
    public Result getFolderContent(@RequestParam Long folderId){
        return Result.success(fileInfoService.selectFileList(folderId));
    }

    /**
     * 校检文件
     * @param chunkInfo
     * @param response
     * @return
     */
    @GetMapping("/chunk")
    public ChunkResult checkChunk(ChunkInfo chunkInfo, HttpServletResponse response){
        log.info("校验文件：{}", chunkInfo);
        return chunkInfoService.checkChunkState(chunkInfo, response);
    }

    /**
     * 上传文件块
     * @param chunkInfo
     * @return
     */
    @PostMapping("/chunk")
    public Result<Integer> uploadChunk(ChunkInfo chunkInfo){
        return Result.success(chunkInfoService.uploadFile(chunkInfo));
    }

    /**
     * 整合文件chunk
     * @param fileInfoDTO
     * @param response
     * @return
     */
    @PostMapping("/mergeFile")
    public HttpServletResponse mergeFile(@RequestBody FileInfoDTO fileInfoDTO, HttpServletResponse response){
        return fileInfoService.mergeFile(fileInfoDTO, response);
    }

    /**
     * 查询接口
     * @param folder
     * @return
     */
    @GetMapping("/selectFileList")
    public Result<List<FileInfo>> selectFileList(@RequestParam String folder){
        log.info("查询文件列表：{}", folder);
        return Result.success(fileInfoService.selectFileList(folder));
    }

    /**
     * 删除接口
     * @param
     * @return
     */
    @DeleteMapping("/deleteFile")
    public Result deleteFile(@RequestBody List<Long> ids){
        return Result.success(fileInfoService.deleteFile(ids));
    }

    /**
     * 下载文件
     * @param location
     * @param fileName
     * @param response
     * @param request
     */
    @GetMapping("/download")
    public void download(@RequestParam String location,
                        @RequestParam String fileName,
                         HttpServletResponse response,
                         HttpServletRequest request){
        System.out.println("location:"+location);
        System.out.println("fileName:"+fileName);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        OutputStream os = null;
        try{
            bis = new BufferedInputStream(new FileInputStream(location+"\\"+fileName));
            os = response.getOutputStream();
            bos = new BufferedOutputStream(os);
            ServletUtils.setFileDownloadHeader(request, response, fileName);
            int byteRead = 0;
            byte[] buffer = new byte[8192];
            while((byteRead=bis.read(buffer, 0, 8192))!=-1){
                bos.write(buffer, 0, byteRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                bos.flush();
                bis.close();
                os.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("/downloadMultiple")
    public ResponseEntity<Resource> downloadMultipleFiles(
            @RequestParam List<Long> ids) {

        try {
            List<FileInfo> fileInfos = fileInfoService.selectFileList(ids);
            // 检查所有文件是否存在
            for(FileInfo fileInfo:fileInfos){
                File file = new File(fileInfo.getLocation(), fileInfo.getFileName());
                if(!file.exists()){
                    return ResponseEntity.notFound().build();
                }
            }

            // 文件打包逻辑
            // 这里使用Java的zip库作为示例，实际应用中可以使用更高效的库
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(bos);

            for(FileInfo fileInfo:fileInfos){
                File file = new File(fileInfo.getLocation(), fileInfo.getFileName());
                FileInputStream fis = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(fileInfo.getFileName());
                zipOut.putNextEntry(zipEntry);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, len);
                }
                fis.close();
            }
            zipOut.close();

            // 将ZIP文件转换为Resource
            byte[] zipBytes = bos.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(zipBytes);
            Resource resource = new InputStreamResource(bis);

            // 设置响应头信息
            String zipFileName = "download.zip"; // 可以自定义
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + zipFileName)
                            .contentLength(zipBytes.length)
                            .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 图片展示url
     * @param fileId
     * @param response
     * @param request
     * @return
     */
    @GetMapping("/image/{fileId}")
    public ResponseEntity<Void> getAloneFileContent(@PathVariable("fileId") Long fileId, HttpServletResponse response, HttpServletRequest request){
        FileInfo fileInfo = fileInfoService.getAloneFile(fileId);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        OutputStream os = null;
        int byteRead = 0;
        byte[] buffer = new byte[8192];
        if(fileInfo==null || !new File(fileInfo.getLocation() + File.separator + fileInfo.getFileName()).exists()){
            return ResponseEntity.notFound().build();
        }
        response.setContentType(fileInfo.getFileType());
        try{
            bis = new BufferedInputStream(new FileInputStream(fileInfo.getLocation()+File.separator+fileInfo.getFileName()));
            os = response.getOutputStream();
            bos = new BufferedOutputStream(os);
            while((byteRead=bis.read(buffer, 0, 8192))!=-1){
                bos.write(buffer, 0, byteRead);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                bos.flush();
                bis.close();
                os.close();
                bos.close();
                return ResponseEntity.noContent().build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 视频播放url
     * @param fileId
     * @param response
     * @return
     */
    @GetMapping(value = "/video/{fileId}", produces = "application/octet-stream")
    public ResponseEntity<Void> streamFile(@PathVariable("fileId") Long fileId, HttpServletResponse response) {
        FileInfo fileInfo = fileInfoService.getAloneFile(fileId);
        if (fileInfo == null || !new File(fileInfo.getLocation() + File.separator + fileInfo.getFileName()).exists()) {
            return ResponseEntity.notFound().build();
        }

        try (
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileInfo.getLocation() + File.separator + fileInfo.getFileName()));
            OutputStream os = response.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os)
        ) {
            response.setContentType(fileInfo.getFileType()); // 根据文件类型设置Content-Type
            byte[] buffer = new byte[8192];
            int byteRead;
            while ((byteRead = bis.read(buffer, 0, 8192)) != -1) {
                bos.write(buffer, 0, byteRead);
            }
            bos.flush();
            return ResponseEntity.ok().build(); // 或者使用其他适当的成功响应
        } catch (IOException e) {
            log.error("文件传输失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
