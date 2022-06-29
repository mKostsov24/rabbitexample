package com.example.rabbitexample.rabbite.util;

public class RabbitMQConstants {

    public static final String ROUTING_KEY_T_INPUT = "rk.t.input";
    public static final String ROUTING_KEY_N_OUTPUT = "rk.n.output";

    public static final String CONTENT_TYPE_T_INPUT = "application/rabbitmq.t.input";
    public static final String CONTENT_TYPE_N_OUTPUT = "application/rabbitmq.n.output";
    public static final String CONTENT_TYPE_CHECK_JOB_STATUS = "application/rabbitmq.check_job_status";

    public static final String ROUTING_KEY_CHECK_JOB_STATUS = "rk.check_job_status";
    public static final String ROUTING_KEY_CHECK_JOB_STATUS_DL = "rk.check_job_status.dl";
    public static final String CHECK_JOB_STATUS_DL_EXCHANGE = "dl-exchange";



    private RabbitMQConstants() {
    }
}
