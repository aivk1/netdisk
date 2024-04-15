package com.disk.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.model.*;
import com.disk.exception.BaseException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.management.ObjectName;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    /**
     *
     * @param bytes
     * @param objectName
     * @return
     */
    public String upload(byte[] bytes, String objectName){
        OSS OSSClient =new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try{
            OSSClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        }catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (OSSClient != null) {
                OSSClient.shutdown();
            }
        }
        StringBuilder builder = new StringBuilder("https://");
        builder.append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);
        log.info("文件上传到：{}", builder);
        return builder.toString();
    }

    /**
     * 删除文件
     * @param objectName
     */
    public void delete(String objectName){
        OSS OSSClient =new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try{
            OSSClient.deleteObject(bucketName, objectName);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (OSSClient != null) {
                OSSClient.shutdown();
            }
        }
        log.info("文件已删除:{}", objectName);
    }


//    public String createFolder(String folderPath){
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//// 创建一个空对象，代表文件夹
//        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
//        ObjectMetadata meta = new ObjectMetadata();
//        meta.setContentLength(0);
//        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderPath, emptyContent, meta);
//
//// 上传空对象到 Bucket 中
//        ossClient.putObject(putObjectRequest);
//
//// 关闭 OSSClient
//        ossClient.shutdown();
//        StringBuilder builder = new StringBuilder("https://");
//        builder.append(bucketName)
//                .append(".")
//                .append(endpoint)
//                .append("/")
//                .append(folderPath);
//        return builder.toString();
//    }

    public void deleteFolderAndContent(String folderNamePrefix){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            this.deleteObjectWithPrefix(ossClient, bucketName, folderNamePrefix);
        }catch (OSSException OE){
            System.out.println(OE.getMessage());
            throw new BaseException(OE.getMessage());
        }catch (ClientException CE){
            System.out.println(CE.getMessage());
            throw new BaseException(CE.getMessage());
        }finally {
            if(ossClient!=null){
                ossClient.shutdown();
            }
        }
    }

    public void deleteObjectWithPrefix(OSS ossClient, String bucketName, String folderNamePrefix){
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        listObjectsRequest.setPrefix(folderNamePrefix);
        listObjectsRequest.setDelimiter("/");
        ObjectListing objectListing;
        do{
            objectListing = ossClient.listObjects(listObjectsRequest);
            List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
            for(OSSObjectSummary objectSummary:objectSummaries){
                String key = objectSummary.getKey();
                ossClient.deleteObject(bucketName, key);
                System.out.println("deleted " + key);

            }
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        }while(objectListing.isTruncated());
    }

    public OSSObject fileDownload(String fileName){
        try {
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            GetObjectRequest getObjectRequest  = new GetObjectRequest(bucketName, fileName);
            OSSObject ossObject = ossClient.getObject(getObjectRequest);

            return ossObject;
        }catch (Exception e){
            throw e;
        }
    }

}
