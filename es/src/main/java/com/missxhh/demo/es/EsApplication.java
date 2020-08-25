package com.missxhh.demo.es;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.missxhh.demo.es.mapper")
public class EsApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsApplication.class, args);
    }
}
