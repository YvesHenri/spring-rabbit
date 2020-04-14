package com.rabbit.config;

import com.rabbit.dto.RabbitMessage;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Configuration
public class RabbitConfig implements RabbitListenerConfigurer {

    @Value("${RABBIT_BROKER_PORT:5672}")
    private Integer rabbitBrokerPort;

    @Value("${RABBIT_BROKER_HOSTNAME:localhost}")
    private String rabbitBrokerHostname;

    @Value("${RABBIT_BROKER_USERNAME:guest}")
    private String rabbitBrokerUsername;

    @Value("${RABBIT_BROKER_PASSWORD:guest}")
    private String rabbitBrokerPassword;

    @Bean
    public Queue itemQueue() {
        return new Queue("item");
    }

    @Bean
    public Exchange itemExchange() {
        return new TopicExchange("item-exchange");
    }

    @Bean
    public Binding binding(Queue itemQueue, Exchange itemExchange) {
        return BindingBuilder.bind(itemQueue).to(itemExchange).with("item.#").noargs();
    }

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitBrokerHostname);
        connectionFactory.setUsername(rabbitBrokerUsername);
        connectionFactory.setPassword(rabbitBrokerPassword);
        connectionFactory.setPort(rabbitBrokerPort);
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory rabbitConnectionFactory) {
        return new RabbitAdmin(rabbitConnectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jacksonRabbitMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonRabbitMessageConverter);
        return template;
    }

    @Bean
    public RabbitTransactionManager rabbitTransactionManager(ConnectionFactory rabbitConnectionFactory) {
        return new RabbitTransactionManager(rabbitConnectionFactory);
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory rabbitConnectionFactory, MessageConverter jacksonRabbitMessageConverter) {
        SimpleRabbitListenerContainerFactory listenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        listenerContainerFactory.setMessageConverter(jacksonRabbitMessageConverter);
        listenerContainerFactory.setConnectionFactory(rabbitConnectionFactory);
        listenerContainerFactory.setConcurrentConsumers(4);
        listenerContainerFactory.setMaxConcurrentConsumers(32);
        return listenerContainerFactory;
    }

    @Bean
    public MessageHandlerMethodFactory rabbitMessageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setValidator(rabbitMessageValidator()); // @Valid support
        return messageHandlerMethodFactory;
    }

    @Bean
    public Validator rabbitMessageValidator() {
        return new Validator() {

            @Override
            public boolean supports(Class<?> clazz) {
                return RabbitMessage.class.isAssignableFrom(clazz);
            }

            @Override
            public void validate(Object target, Errors errors) {
                if (supports(target.getClass())) {
                    RabbitMessage.class.cast(target).validate(errors);
                }
            }

        };
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        //registrar.setMessageHandlerMethodFactory(rabbitMessageHandlerMethodFactory());
    }

}
