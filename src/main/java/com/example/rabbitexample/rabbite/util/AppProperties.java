package com.example.rabbitexample.rabbite.util;

public class AppProperties {

    public static final String RABBITMQ_URI = "${rabbitmq.uri}";
    public static final String RABBITMQ_EXCHANGE_GENERAL ="${rabbitmq.exchange.general}";
    public static final String RABBITMQ_DELAY = "${rabbitmq.delay}";

    private AppProperties() {
    }

}
