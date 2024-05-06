package com.disk.utils;

import com.disk.entity.ChunkInfo;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Slf4j
public class FileInfoUtils {
    /**
     * 创建路径
     * @param uploadFolder
     * @param chunkInfo
     * @return
     */
    public static String generatePath(String uploadFolder, ChunkInfo chunkInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append(uploadFolder).
                append('/').append(String.valueOf(4l))
                .append('/').append(chunkInfo.getRelativePath())
                .append('/').append(chunkInfo.getIdentifier());
        if(!Files.isWritable(Paths.get(sb.toString()))){
            log.info("路径不存在，新建路径:{}",sb.toString());
            try{
                Files.createDirectories(Paths.get(sb.toString()));
            } catch (IOException e) {
                log.error("路径创建失败:{}", sb.toString());
            }
        }
        return sb.append("/")
                .append(chunkInfo.getFilename())
                .append("-").
                append(chunkInfo.getChunkNumber()).
                toString();
    }

    /**
     * 合并文件
     * @param file
     * @param folder
     * @param fileName
     * @return
     */
    public static Integer merge(String file, String folder, String fileName) {
        Integer rlt = HttpServletResponse.SC_OK;
        try{
            if(fileExists(file)){
                rlt = HttpServletResponse.SC_MULTIPLE_CHOICES;
            }else{
                Files.createFile(Paths.get(file));
                Files.list(Paths.get(folder))
                        .filter(path -> !path.getFileName().toString().equals(fileName))
                        .sorted((o1, o2) -> {
                            String p1 = o1.getFileName().toString();
                            String p2 = o2.getFileName().toString();
                            int i1 = p1.lastIndexOf("-");
                            int i2 = p2.lastIndexOf("-");
                            return Integer.valueOf(p2.substring(i2)).compareTo(Integer.valueOf(p1.substring(i1)));
                        }).forEach(path -> {
                            try{
                                Files.write(Paths.get(file), Files.readAllBytes(path), StandardOpenOption.APPEND);
                            } catch (IOException e) {
                                log.error("删除文件失败：{}",e.getMessage());
                            }
                        });
            }
        }catch (IOException e){
            log.error("合并失败:{},{}",e.getMessage(),e);
            rlt = HttpServletResponse.SC_BAD_REQUEST;
        }
        return rlt;
    }

    /**
     * check file is existing
     * @param file
     * @return
     */
    public static boolean fileExists(String file){
        boolean flag = false;
        Path path = Paths.get(file);
        flag = Files.exists(path,new LinkOption[]{LinkOption.NOFOLLOW_LINKS});
        return flag;
    }
}
