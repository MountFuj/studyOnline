package com.zy.content.feignclient;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Administrator
 * @version 1.0
 * @description TODO
 * @date 2023/11/21 20:25
 */
public class MediaServiceClientFallback implements MediaServiceClient{
    @Override
    public String uploadFile(MultipartFile upload, String objectName) {
        return null;
    }
}
