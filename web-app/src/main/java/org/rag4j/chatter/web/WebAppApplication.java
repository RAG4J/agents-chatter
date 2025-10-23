package org.rag4j.chatter.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.rag4j.chatter")
public class WebAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebAppApplication.class, args);
    }
}
