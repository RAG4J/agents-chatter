package org.rag4j.chatter.eventbus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.rag4j.chatter")
public class EventBusApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventBusApplication.class, args);
    }
}
