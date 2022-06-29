package com.example.rabbitexample.rabbite.reciver;

import com.example.rabbitexample.rabbite.dto.JobStatusDto;
import com.example.rabbitexample.service.JobStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class CheckJobStatusMessageReceiver {
    public static final String RECEIVE_MESSAGE_METHOD_NAME = "receiveMessage";
    private final JobStatusService jobStatusService;


    public void receiveMessage(JobStatusDto jobStatusDto) {
        if (jobStatusDto == null) {
            log.info("Empty message");
            return;
        }

        log.info("Received CheckJobStatusMessageReceiver from RabbitMQ: {}", jobStatusDto);
        try {
            jobStatusService.attemptCheckJob(jobStatusDto);
        } catch (Exception e) {
            log.error(String.format("Exception during handling check  job status object, jobStatusDto = %s", jobStatusDto), e);
        }
    }
}
