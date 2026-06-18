package br.com.financeaiservice.infrastructure.exception.customized_exceptions;

public class KafkaProcessingException extends RuntimeException {
    public KafkaProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
