package dk.via.sep3.controller.exceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
    logger.warn("Resource not found: {}", ex.getMessage());
    return new ResponseEntity<>(
        new ErrorResponse("Resource not found", "RESOURCE_NOT_FOUND", Instant.now().toString(), ex.getMessage()),
        HttpStatus.NOT_FOUND
    );
  }

  @ExceptionHandler(BusinessRuleViolationException.class)
  public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(BusinessRuleViolationException ex) {
    logger.warn("Business rule violation: {}", ex.getMessage());
    return new ResponseEntity<>(
        new ErrorResponse("Business rule violated", "BUSINESS_RULE_VIOLATION", Instant.now().toString(), ex.getMessage()),
        HttpStatus.CONFLICT
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
    logger.warn("Invalid argument: {}", ex.getMessage());
    return new ResponseEntity<>(
        new ErrorResponse("Invalid input provided", "INVALID_INPUT", Instant.now().toString(), ex.getMessage()),
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
    logger.warn("Illegal state: {}", ex.getMessage());
    return new ResponseEntity<>(
        new ErrorResponse("Business constraint violated", "BUSINESS_CONSTRAINT", Instant.now().toString(), ex.getMessage()),
        HttpStatus.CONFLICT
    );
  }

  @ExceptionHandler(GrpcCommunicationException.class)
  public ResponseEntity<ErrorResponse> handleGrpcCommunication(GrpcCommunicationException ex) {
    logger.error("gRPC communication error: {}", ex.getMessage(), ex);
    return new ResponseEntity<>(
        new ErrorResponse("Service temporarily unavailable", "SERVICE_UNAVAILABLE", Instant.now().toString(), ex.getMessage()),
        HttpStatus.SERVICE_UNAVAILABLE
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
    logger.error("Unexpected error: {}", ex.getMessage(), ex);
    return new ResponseEntity<>(
        new ErrorResponse("An unexpected error occurred", "INTERNAL_ERROR", Instant.now().toString(), ex.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR
    );
  }

  public record ErrorResponse(String message, String errorCode, String timestamp, String details){}
}