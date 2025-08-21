package com.proyecto.backend.e_commerce.exception;

import com.proyecto.backend.e_commerce.Dtos.ErrorDetailsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler{
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorDetailsDto> handleResourceAlreadyExistsException(ResourceAlreadyExistsException exception, WebRequest webRequest) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), exception.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest webRequest) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), "Error de validación", errors.toString());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDetailsDto> handleResourceNotFoundException(ResourceNotFoundException exception, WebRequest webRequest) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), exception.getMessage(), webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDetailsDto> handleBadCredentialsException(BadCredentialsException exception, WebRequest webRequest) {
        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), "Credenciales inválidas", webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED); // Devuelve un 401
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetailsDto> handleGlobalException(Exception exception, WebRequest webRequest) {
        // Usamos el logger para registrar el stack trace completo en la consola
        logger.error("Ocurrió una excepción no controlada: {}", exception.getMessage(), exception);

        ErrorDetailsDto errorDetails = new ErrorDetailsDto(new Date(), "Ocurrió un error interno en el servidor", webRequest.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
