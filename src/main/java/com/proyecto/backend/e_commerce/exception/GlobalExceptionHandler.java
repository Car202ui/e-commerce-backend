package com.proyecto.backend.e_commerce.exception;

public class GlobalExceptionHandler extends RuntimeException {
  public GlobalExceptionHandler(String message) {
    super(message);
  }
}
