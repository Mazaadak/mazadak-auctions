package com.mazadak.auctions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableScheduling
public class AuctionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionsApplication.class, args);
    }

}
