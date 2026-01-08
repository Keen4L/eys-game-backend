package com.eys.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 小程序API启动类
 *
 * @author EYS
 */
@SpringBootApplication
@ComponentScan(basePackages = { "com.eys" })
@MapperScan("com.eys.mapper")
public class EysAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(EysAppApplication.class, args);
        System.out.println("=============================================");
        System.out.println("       鹅鸭杀辅助工具 - 小程序API启动成功      ");
        System.out.println("       API文档: http://localhost:8081/doc.html");
        System.out.println("=============================================");
    }
}
