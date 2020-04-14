package com.rabbit.listener;

import com.rabbit.dto.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
public class QueueListener {

    private Logger logger = LogManager.getLogger(QueueListener.class);

    @RabbitListener(queues = "item")
    public void onItemMessage(@Valid Item item) throws InterruptedException {
        logger.info("Received an item: {}", item.getName());
//        Thread.sleep(15000);
    }

}
