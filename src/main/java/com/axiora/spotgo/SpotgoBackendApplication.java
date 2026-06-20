package com.axiora.spotgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpotgoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpotgoBackendApplication.class, args);
    }

}
