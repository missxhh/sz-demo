package com.missxhh;

import com.cxytiandi.elasticjob.annotation.EnableElasticJob;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableElasticJob
public class ElasticJobThirdStarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElasticJobThirdStarterApplication.class, args);
    }
}
