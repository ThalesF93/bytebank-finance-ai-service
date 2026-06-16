package br.com.financeaiservice.infrastructure.exception;

public class KafkaProcessingException extends RuntimeException {
    public KafkaProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
