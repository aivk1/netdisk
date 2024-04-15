package com.disk.utils;

import com.disk.constant.FileTypeConstant;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtil
{
    public static Integer getFileTypeByExtension(String fileName){
        String extension = "";
        if(fileName==null){
            return -1;
        }
        if(fileName.lastIndexOf(".") == -1){
            return FileTypeConstant.FOLDER_TYPE;
        }
        extension = fileName.substring(fileName.lastIndexOf(".")+1).toLowerCase();
        if(isImageExtension(extension)){
            return FileTypeConstant.IMAGE_TYPE;
        }
        else if(isVideoExtension(extension)){
            return FileTypeConstant.VIDEO_TYPE;
        }
        return -1;
    }

    public static Integer getFileType(MultipartFile file){
        String contentType = file.getContentType();
        if(isImageMimeType(contentType)){
            return FileTypeConstant.IMAGE_TYPE;
        }
        else if(isVideoMimeType(contentType)){
            return FileTypeConstant.VIDEO_TYPE;
        }
        return -1;
    }

    private static boolean isImageExtension(String extension){
        return "jpg".equals(extension) || "jpeg".equals(extension) || "png".equals(extension) ||
                "gif".equals(extension) || "bmp".equals(extension) || // 其他图片格式
                // ... 其他图片扩展名检查
                false;
    }

    private static boolean isVideoExtension(String extension){
        return "mp4".equals(extension) || "avi".equals(extension) || "mov".equals(extension) ||
                "wmv".equals(extension) || // 其他视频格式
                // ... 其他视频扩展名检查
                false;
    }

    private static boolean isImageMimeType(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    private static boolean isVideoMimeType(String mimeType) {
        return mimeType != null && (
                mimeType.startsWith("video/") ||
                        "application/x-mpegURL".equals(mimeType) || // HLS stream
                        "application/vnd.apple.mpegurl".equals(mimeType) // HLS stream for iOS
                // ... 其他视频MIME类型检查
        );
    }

}
