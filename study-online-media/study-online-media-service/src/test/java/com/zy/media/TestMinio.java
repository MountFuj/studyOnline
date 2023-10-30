package com.zy.media;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @description minio测试
 * @date 2023/10/26 20:30
 */
@SpringBootTest(classes = TestMinio.class)
public class TestMinio {
    MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();
    @Test
    public void testUpload() throws Exception {
        UploadObjectArgs testbucket = UploadObjectArgs.builder()
                .bucket("testbucket")
                .filename("D:\\avator.png")
                .object("avator.png")
                .build();
        minioClient.uploadObject(testbucket);
    }

    @Test
    public void testDelete() throws Exception{
        RemoveObjectArgs testbucket = RemoveObjectArgs.builder()
                .bucket("testbucket")
                .object("avator.png")
                .build();
        minioClient.removeObject(testbucket);
    }

    @Test
    public void testDownload() throws Exception{
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket("testbucket")
                        .object("avator.png")
                        .build());
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D://avator.png"));
        IOUtils.copy(stream,fileOutputStream);
    }
}
