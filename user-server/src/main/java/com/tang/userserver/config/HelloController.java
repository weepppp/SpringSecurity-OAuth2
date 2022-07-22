package com.tang.userserver.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weepppp 2022/7/22 19:57
 * 配置两个测试接口
 **/
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @GetMapping("/admin/hello")
    public String admin(){
        return "admin";
    }

}
