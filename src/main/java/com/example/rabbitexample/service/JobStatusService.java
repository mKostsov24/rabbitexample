package com.example.rabbitexample.service;

import com.example.rabbitexample.rabbite.dto.JobStatusDto;
import com.example.rabbitexample.rabbite.model.StatusType;
import com.example.rabbitexample.rabbite.service.RabbitMQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobStatusService {
    private final RabbitMQService rabbitMQService;
    private static final String MAX_ATTEMPTS = "2";

    public void attemptCheckJob(JobStatusDto jobStatusDto) {
        int attemptCount = jobStatusDto.getAttemptCount();
        if (attemptCount < Integer.parseInt(MAX_ATTEMPTS)) {
            jobStatusDto.setAttemptCount(attemptCount + 1);
            rabbitMQService.sendJobStatusToRabbitMQExchange(jobStatusDto);
        } else {
            rabbitMQService.sendSucceedToRabbitMQExchange(String.valueOf(StatusType.SUCCESS));
            log.info("SUCCESS");
        }
    }
}