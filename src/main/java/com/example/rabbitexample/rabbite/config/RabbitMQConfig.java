package com.example.rabbitexample.rabbite.config;


import com.example.rabbitexample.rabbite.converter.InMessageConverter;
import com.example.rabbitexample.rabbite.converter.JobMessageConverter;
import com.example.rabbitexample.rabbite.reciver.CheckJobStatusMessageReceiver;
import com.example.rabbitexample.rabbite.reciver.InputMessageReceiver;
import com.example.rabbitexample.rabbite.util.AppProperties;
import com.example.rabbitexample.rabbite.util.RabbitMQConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class RabbitMQConfig {

    private static final String CREATED_QUEUE = "Created queue with name = {}";
    private static final String INPUT_QUEUE = "input_queue";
    private static final String OUTPUT_QUEUE = "output_queue";
    private static final String CHECK_STATUS_QUEUE = "check_status_queue";
    private static final String CHECK_JOB_STATUS_DEAD_LETTER_QUEUE = "check_status_dl_queue";

    @Value(AppProperties.RABBITMQ_URI)
    private String rabbitMqUri;

    @Value(AppProperties.RABBITMQ_EXCHANGE_GENERAL)
    private String generalExchangeName;


    @Bean
    public ConnectionFactory connectionFactory() throws URISyntaxException {
        return new CachingConnectionFactory(new URI(rabbitMqUri));
    }

    @Bean
    public AmqpAdmin amqpAdmin() throws URISyntaxException {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public DirectExchange generalExchange() {
        DirectExchange directExchange = new DirectExchange(generalExchangeName);
        directExchange.setShouldDeclare(true);
        return directExchange;
    }

    @Bean
    public Binding outputBinding() {
        return BindingBuilder
                .bind(outputQueue())
                .to(generalExchange())
                .with(RabbitMQConstants.ROUTING_KEY_N_OUTPUT);
    }

    @Bean
    public Queue outputQueue() {
        Queue queue = new Queue(OUTPUT_QUEUE, true, false, false);
        log.info(CREATED_QUEUE, queue.getName());
        return queue;
    }

    @Bean
    public Binding inputBinding() {
        return BindingBuilder
                .bind(inputQueue())
                .to(generalExchange())
                .with(RabbitMQConstants.ROUTING_KEY_T_INPUT);
    }

    @Bean
    public Queue inputQueue() {
        Queue queue = new Queue(INPUT_QUEUE, true, false, false);
        log.info(CREATED_QUEUE, queue.getName());
        return queue;
    }

    @Bean
    public MessageListenerAdapter inputListenerAdapter(InputMessageReceiver receiver) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, InputMessageReceiver.RECEIVE_MESSAGE_METHOD_NAME);
        messageListenerAdapter.setMessageConverter(inputMessageConverter());
        return messageListenerAdapter;
    }

    @Bean
    public InMessageConverter inputMessageConverter() {
        return new InMessageConverter();
    }

    @Bean
    public SimpleMessageListenerContainer inputContainer(@Qualifier("inputListenerAdapter") MessageListenerAdapter listenerAdapter) throws URISyntaxException {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(inputQueue().getName());
        container.setPrefetchCount(1);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public Queue jobStatusQueue() {
        Queue queue = new Queue(CHECK_STATUS_QUEUE, true, false, false);
        log.info(CREATED_QUEUE, queue.getName());
        return queue;
    }

    @Bean
    public Binding jobStatusBinding() {
        return BindingBuilder
                .bind(jobStatusQueue())
                .to(generalExchange())
                .with(RabbitMQConstants.ROUTING_KEY_CHECK_JOB_STATUS);
    }

    @Bean
    public MessageListenerAdapter jobStatusListenerAdapter(CheckJobStatusMessageReceiver receiver) {
        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(receiver, CheckJobStatusMessageReceiver.RECEIVE_MESSAGE_METHOD_NAME);
        messageListenerAdapter.setMessageConverter(jobStatusMessageConverter());
        return messageListenerAdapter;
    }

    @Bean
    public JobMessageConverter jobStatusMessageConverter() {
        return new JobMessageConverter();
    }

    @Bean
    public SimpleMessageListenerContainer jobStatusContainer(@Qualifier("jobStatusListenerAdapter") MessageListenerAdapter listenerAdapter) throws URISyntaxException {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(jobStatusQueue().getName());
        container.setPrefetchCount(1);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public Binding jobStatusDeadLetterBinding() {
        return BindingBuilder
                .bind(jobStatusDeadLetterQueue())
                .to(jobStatusDeadLetterExchange())
                .with(RabbitMQConstants.ROUTING_KEY_CHECK_JOB_STATUS_DL);
    }

    @Bean
    public Queue jobStatusDeadLetterQueue() {
        return createDeadLetterQueue(RabbitMQConstants.ROUTING_KEY_CHECK_JOB_STATUS, CHECK_JOB_STATUS_DEAD_LETTER_QUEUE);
    }
    @Bean
    public DirectExchange jobStatusDeadLetterExchange() {
        DirectExchange delayedExchange = new DirectExchange(RabbitMQConstants.CHECK_JOB_STATUS_DL_EXCHANGE);
        delayedExchange.setShouldDeclare(true);
        return delayedExchange;
    }

    private Queue createDeadLetterQueue(String deadLetterRoutingKey, String queueName) {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", generalExchangeName);
        args.put("x-dead-letter-routing-key", deadLetterRoutingKey);
        Queue queue = new Queue(queueName, true, false, false, args);
        log.info("Created dead letter queue with name = {}", queue.getName());
        return queue;
    }
}