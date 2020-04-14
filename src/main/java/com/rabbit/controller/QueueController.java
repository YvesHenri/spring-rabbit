package com.rabbit.controller;

import com.rabbit.dto.Item;
import com.rabbit.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
public class QueueController {

    @Autowired
    private QueueService queueService;

    @RequestMapping("/size/{queueName}")
    public int retrieveQueueLength(@PathVariable String queueName) {
        return queueService.length(queueName);
    }

    @RequestMapping("/publish/item")
    public void publishPersonMessage(@RequestBody Item item) {
        queueService.send("item-exchange", "item", item);
    }

}
