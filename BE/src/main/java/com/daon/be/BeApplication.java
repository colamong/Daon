package com.daon.be;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
public class BeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeApplication.class, args);
    }

    @PostConstruct
    public void started() {
        // JVM 전체 타임존을 KST로 고정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
