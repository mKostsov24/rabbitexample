package com.example.rabbitexample.general.service;

import com.example.rabbitexample.rabbite.service.RabbitMQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
@Slf4j
@RequiredArgsConstructor
public class StartupService {

    private final RabbitMQService rabbitMQService;

    @PostConstruct
    public void init() {
        checkRabbitMQConnection();
    }

    private void checkRabbitMQConnection() {
        if (!rabbitMQService.canRabbitMQConnectionBeEstablished()) {
            log.error("FATAL ERROR: RabbitMQ connection can't be established");
        }
    }
}
