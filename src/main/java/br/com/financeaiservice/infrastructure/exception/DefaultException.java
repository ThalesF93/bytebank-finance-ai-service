package br.com.financeaiservice.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class DefaultException extends RuntimeException {
    private final String code;
    private final HttpStatus status;

    public DefaultException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;

    }

}
