package com.rabbit.dto;

import org.springframework.validation.Errors;

public interface RabbitMessage {

    void validate(Errors errors);

}
