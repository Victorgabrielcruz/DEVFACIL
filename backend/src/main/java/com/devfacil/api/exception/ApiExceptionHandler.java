package com.devfacil.api.exception;

import com.devfacil.api.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleNotFound(RecursoNaoEncontradoException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErroResponse.of(exception.getMessage()));
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResponse> handleConflict(ConflitoException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErroResponse.of(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(MethodArgumentNotValidException exception) {
        String field = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " invalido")
                .orElse("requisicao invalida");
        return ResponseEntity.badRequest().body(ErroResponse.of(field));
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErroResponse> handleBadRequest(Exception exception) {
        return ResponseEntity.badRequest().body(ErroResponse.of("requisicao invalida"));
    }
}
