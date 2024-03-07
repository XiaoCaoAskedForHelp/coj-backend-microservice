package com.amos.cojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.amos.cojbackenduserservice.mapper")
@ComponentScan("com.amos")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.amos.cojbackendserviceclient.service"})
public class CojBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CojBackendUserServiceApplication.class, args);
    }

}
