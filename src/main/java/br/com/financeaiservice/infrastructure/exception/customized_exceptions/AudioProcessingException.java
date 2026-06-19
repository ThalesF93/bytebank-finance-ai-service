package br.com.financeaiservice.infrastructure.exception.customized_exceptions;

import br.com.financeaiservice.infrastructure.exception.DefaultException;
import org.springframework.http.HttpStatus;

public class AudioProcessingException extends DefaultException {
    public AudioProcessingException(String message) {
        super("ERROR_PROCESSING_AUDIO", message, HttpStatus.CONFLICT);
    }
}
