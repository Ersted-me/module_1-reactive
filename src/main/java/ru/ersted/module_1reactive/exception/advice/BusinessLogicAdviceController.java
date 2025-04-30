package ru.ersted.module_1reactive.exception.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.ersted.module_1reactive.exception.NotFoundException;
import ru.ersted.module_1reactive.exception.dto.ExceptionResponse;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class BusinessLogicAdviceController {

    @ExceptionHandler(exception = NotFoundException.class)
    public Mono<ResponseEntity<ExceptionResponse>> handleException(NotFoundException ex, ServerWebExchange exchange) {
        log.error(ex.getMessage(), ex);
        return Mono.just(
                ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                ExceptionResponse.builder()
                                        .timestamp(LocalDateTime.now())
                                        .status(HttpStatus.NOT_FOUND.value())
                                        .error("Not found")
                                        .message(ex.getMessage())
                                        .path(exchange.getRequest().getPath().value())
                                        .build()
                        )
        );
    }

}
