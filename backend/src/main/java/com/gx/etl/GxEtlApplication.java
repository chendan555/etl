package com.gx.etl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.gx.etl.**.mapper")
@EnableAsync
public class GxEtlApplication {

    public static void main(String[] args) {
        SpringApplication.run(GxEtlApplication.class, args);
    }
}
