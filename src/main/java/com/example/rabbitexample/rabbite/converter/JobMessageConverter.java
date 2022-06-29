package com.example.rabbitexample.rabbite.converter;

import com.example.rabbitexample.rabbite.dto.JobStatusDto;
import com.example.rabbitexample.rabbite.util.RabbitMQConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class JobMessageConverter implements MessageConverter {

    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        throw new UnsupportedOperationException("TranslationJobStatusMessageConverter.toMessage is not implemented");
    }

    @Override
    public JobStatusDto fromMessage(Message message) throws MessageConversionException {
        MessageProperties properties = message.getMessageProperties();
        if (properties == null) {
            throw new MessageConversionException("Message properties is not specified");
        }

        String contentType = properties.getContentType();
        if (!RabbitMQConstants.CONTENT_TYPE_CHECK_JOB_STATUS.equals(contentType)) {
            throw new MessageConversionException(String.format("Can't convert message with contentType = %s", contentType));
        }
        String content = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            return new ObjectMapper().readValue(content, JobStatusDto.class);
        } catch (Exception e) {
            throw new MessageConversionException("Exception during deserialization of RVT translate task object", e);
        }
    }
}
