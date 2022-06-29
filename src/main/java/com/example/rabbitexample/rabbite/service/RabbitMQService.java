package com.example.rabbitexample.rabbite.service;

import com.example.rabbitexample.rabbite.dto.JobStatusDto;
import com.example.rabbitexample.rabbite.util.AppProperties;
import com.example.rabbitexample.general.util.JsonUtil;
import com.example.rabbitexample.rabbite.util.RabbitMQConstants;
import lombok.extern.slf4j.Slf4j;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQService {

    @Value(AppProperties.RABBITMQ_EXCHANGE_GENERAL)
    private String generalExchangeName;

    private final RabbitTemplate rabbitTemplate;

    @Value(AppProperties.RABBITMQ_DELAY)
    private String delay;

    @Autowired
    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public boolean canRabbitMQConnectionBeEstablished() {
        try {
            return rabbitTemplate.getConnectionFactory().createConnection().isOpen();
        } catch (Exception e) {
            log.error("Error during RabbitMQ connection creation", e);
            return false;
        }
    }
    
    public void sendJobStatusToRabbitMQExchange(JobStatusDto rvtTranslateJobStatusDto) {
        if (rvtTranslateJobStatusDto == null) {
            return;
        }
        String data = JsonUtil.toJsonString(rvtTranslateJobStatusDto);
        sendMessageToExchange(RabbitMQConstants.CHECK_JOB_STATUS_DL_EXCHANGE, data, RabbitMQConstants.CONTENT_TYPE_CHECK_JOB_STATUS,
                RabbitMQConstants.ROUTING_KEY_CHECK_JOB_STATUS_DL, delay);
    }

    public void sendSucceedToRabbitMQExchange(String text) {
        if (text == null) {
            return;
        }
        String data = JsonUtil.toJsonString(text);
        sendMessageToExchange(generalExchangeName, data, RabbitMQConstants.CONTENT_TYPE_N_OUTPUT,
                RabbitMQConstants.ROUTING_KEY_N_OUTPUT);
    }

    private void sendMessageToExchange(String exchangeName, String messageData, String contentType, String routingKey) {
        sendMessageToExchange(exchangeName, messageData, contentType, routingKey, null);
    }

    private void sendMessageToExchange(String exchangeName,
                                       String messageData,
                                       String contentType,
                                       String routingKey,
                                       @Nullable String expiration) {
        try {
            log.info("Sending message to exchange {}: {}, contentType = {}, expiration = {}, routingKey ={}",
                    exchangeName, messageData, contentType, expiration, routingKey);
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType(contentType);
            messageProperties.setExpiration(expiration);
            Message message = new Message(messageData.getBytes(), messageProperties);
            rabbitTemplate.send(exchangeName, routingKey, message);
            log.info("Message was sent");
        } catch (Exception ex) {
            log.info("Error during sending message to exchange {}: {}, contentType = {}, expiration = {}, routingKey ={}",
                    exchangeName, messageData, contentType, expiration, routingKey, ex);
            log.error("FATAL ERROR: can't send message to RabbitMQ -> application will be shut down");
            System.exit(0);
        }
    }
}
