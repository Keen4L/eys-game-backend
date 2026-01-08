package com.eys.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 管理后台启动类
 *
 * @author EYS
 */
@SpringBootApplication
@ComponentScan(basePackages = { "com.eys" })
@MapperScan("com.eys.mapper")
public class EysAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(EysAdminApplication.class, args);
        System.out.println("=============================================");
        System.out.println("       鹅鸭杀辅助工具 - 管理后台启动成功       ");
        System.out.println("       API文档: http://localhost:8080/doc.html");
        System.out.println("=============================================");
    }
}
