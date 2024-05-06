package com.disk.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileInfo implements Serializable {
    private Long id;
    private String fileName;
    private  String identifier;
    private Long totalSize;
    private String totalSizeName;
    private String location;
    private String fileType;
    private Long refProjectId;
    private Long uploadUser;
    private LocalDateTime uploadTime;
    private Integer status;
    public void setTotalSizeName(Long totalSize){
        this.totalSize = totalSize;
        if(1024*1024 > this.totalSize && this.totalSize >= 1024 ) {
            this.totalSizeName = String.format("%.2f",this.totalSize.doubleValue()/1024) + "KB";
        }else if(1024*1024*1024 > this.totalSize && this.totalSize >= 1024*1024 ) {
            this.totalSizeName = String.format("%.2f",this.totalSize.doubleValue()/(1024*1024)) + "MB";
        }else if(this.totalSize >= 1024*1024*1024 ) {
            this.totalSizeName = String.format("%.2f",this.totalSize.doubleValue()/(1024*1024*1024)) + "GB";
        }else {
            this.totalSizeName = this.totalSize.toString() + "B";
        }
    }
}
