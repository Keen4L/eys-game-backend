package com.eys.admin.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Favicon 控制器
 * 处理浏览器自动请求的网站图标，避免 404 错误刷屏日志
 *
 * @author EYS
 */
@Controller
public class FaviconController {

    /**
     * 返回网站图标
     * 优先返回静态资源目录下的 favicon.ico，如果不存在则返回 204 No Content
     */
    @GetMapping(value = "favicon.ico")
    @ResponseBody
    public ResponseEntity<Resource> favicon() {
        try {
            Resource resource = new ClassPathResource("static/favicon.ico");
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf("image/x-icon"))
                        .body(resource);
            }
        } catch (Exception ignored) {
            // 忽略异常，返回空响应
        }
        // 如果没有 favicon.ico 文件，返回 204 No Content
        return ResponseEntity.noContent().build();
    }
}
