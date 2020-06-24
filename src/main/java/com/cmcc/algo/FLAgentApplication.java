package com.cmcc.algo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cmcc.algo.mapper")
public class FLAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(FLAgentApplication.class, args);
    }
}
