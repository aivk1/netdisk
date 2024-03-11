package com.disk.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.management.ObjectName;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

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


    public void createFolder(String folderPath){
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 创建一个空对象，代表文件夹
        InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(0);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, folderPath, emptyContent, meta);

// 上传空对象到 Bucket 中
        ossClient.putObject(putObjectRequest);

// 关闭 OSSClient
        ossClient.shutdown();
    }

}
