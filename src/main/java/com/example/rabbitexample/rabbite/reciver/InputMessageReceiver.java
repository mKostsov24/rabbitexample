package com.example.rabbitexample.rabbite.reciver;

import com.example.rabbitexample.rabbite.dto.JobStatusDto;
import com.example.rabbitexample.rabbite.dto.TaskDto;
import com.example.rabbitexample.rabbite.service.RabbitMQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class InputMessageReceiver {
    public static final String RECEIVE_MESSAGE_METHOD_NAME = "receiveMessage";

    private final RabbitMQService rabbitMQService;

    public void receiveMessage(TaskDto taskDto) {
        if (taskDto == null) {
            log.info("Empty message");
            return;
        }

        log.info("Received InputMessageReceiver from RabbitMQ: {}", taskDto);
        try {
            log.info("Start check");
            JobStatusDto jobStatusDto = new JobStatusDto();
            jobStatusDto.setAttemptCount(0);
            rabbitMQService.sendJobStatusToRabbitMQExchange(jobStatusDto);
        } catch (Exception e) {
            log.error(String.format("Exception, taskDto = %s", taskDto), e);
        }
    }
}
