package com.pathfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PathFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PathFinderApplication.class, args);
    }

}

