package com.zy;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Administrator
 * @version 1.0
 * @description content模块启动类
 * @date 2023/10/16 21:12
 */
@SpringBootApplication
@EnableSwagger2Doc
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
