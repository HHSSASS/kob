package com.kob.matchingsystem;

import com.kob.matchingsystem.service.impl.MatchingServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;

@RefreshScope
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class MatchingSystemApplication {
    public static void main(String[] args) {
        MatchingServiceImpl.matchingPool.start();
        SpringApplication.run(MatchingSystemApplication.class,args);
    }
}