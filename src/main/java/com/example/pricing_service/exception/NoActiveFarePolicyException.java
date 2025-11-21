package com.example.pricing_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NoActiveFarePolicyException extends RuntimeException {
    public NoActiveFarePolicyException(String message) {
        super(message);
    }
}