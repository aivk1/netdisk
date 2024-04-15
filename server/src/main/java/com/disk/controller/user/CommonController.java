package com.disk.controller.user;


import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectListing;
import com.disk.dto.EmailDTO;
import com.disk.dto.VerificationDTO;
import com.disk.result.Result;
import com.disk.service.VerificationService;
import com.disk.utils.AliOssUtil;
import com.disk.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@Slf4j
public class CommonController {
    @Autowired
    private VerificationService verificationService;
    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/email")
    public Result getEmail(@RequestBody EmailDTO emailDTO){
        verificationService.createVerification(emailDTO.getEmail());
        return Result.success();
    }
    @PostMapping("/verify")
    public Result verify(@RequestBody VerificationDTO verificationDTO){
        if(verificationService.verify(verificationDTO)){
            return Result.success();
        }
        return Result.error("验证失败，验证码错误或未发送验证码");
    }
    @GetMapping("/downFile/{fileName}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> downFile(@PathVariable String fileName){
        OSSObject ossObject = aliOssUtil.fileDownload(fileName);

        InputStream inputStream  = ossObject.getObjectContent();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        headers.add("Content-Type", ossObject.getObjectMetadata().getContentType());

        InputStreamResource resource = new InputStreamResource(inputStream);
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(ossObject.getObjectMetadata().getContentType()))
                .body(resource);
    }
}
