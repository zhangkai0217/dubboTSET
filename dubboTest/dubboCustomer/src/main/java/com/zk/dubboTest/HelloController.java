package com.zk.dubboTest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zk.dubboTest.service.HelloService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Reference
    private HelloService helloService;

    @RequestMapping("hello")
    public String sayHello(){
        return helloService.sayHello();
    }
}
