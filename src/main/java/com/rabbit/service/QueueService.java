package com.rabbit.service;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class QueueService {

    private static final String RABBIT_QUEUE_LENGTH_KEY = "QUEUE_MESSAGE_COUNT";

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public int length(String queueName) {
        Properties queueProperties = rabbitAdmin.getQueueProperties(queueName);
        Object queueMessageCountProperty = queueProperties.get(RABBIT_QUEUE_LENGTH_KEY);
        return new Integer(queueMessageCountProperty.toString());
    }

    public void send(String exchangeName, String topicName, Object data) {
        rabbitTemplate.convertAndSend(exchangeName, topicName, data);
    }

}
