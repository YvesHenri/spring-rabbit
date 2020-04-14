package com.rabbit.dto;

import org.springframework.validation.Errors;

public class Item implements RabbitMessage {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void validate(Errors errors) {
//        errors.reject("ERROR1000", "This is the first error message");
    }

}
