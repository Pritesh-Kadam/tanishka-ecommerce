package com.tanishka.ecommerce.exception;

@SuppressWarnings("serial")
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
