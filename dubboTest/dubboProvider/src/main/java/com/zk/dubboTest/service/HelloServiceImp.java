package com.zk.dubboTest.service;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

@Service
@Component
public class HelloServiceImp implements HelloService {
    @Override
    public String sayHello() {
        return "hello dubbo";
    }
}
