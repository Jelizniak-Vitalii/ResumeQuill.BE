package com.resumequill.app.common.handlers;

import com.resumequill.app.common.exceptions.UnauthorizedException;
import com.resumequill.app.common.models.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private ResponseEntity<ErrorResponse> throwError(HttpStatus status, String message) {
    return throwError(status, message, MediaType.APPLICATION_JSON);
  }

  private ResponseEntity<ErrorResponse> throwError(HttpStatus status, String message, MediaType type) {
    ErrorResponse errorResponse = new ErrorResponse(message);

    logger.error(message);

    return ResponseEntity
      .status(status)
      .contentType(type)
      .body(errorResponse);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(UnauthorizedException ex, WebRequest request) {
    return throwError(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    return throwError(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex, WebRequest request) {
    return throwError(HttpStatus.INTERNAL_SERVER_ERROR, "A null value caused an error: " + ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
    String errorMessage = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(error -> error.getField() + ": " + error.getDefaultMessage())
      .reduce("", (acc, error) -> acc + error + "; ");

    return throwError(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
    return throwError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
  }
}

