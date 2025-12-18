package dk.via.sep3.exceptionHandler;

import dk.via.sep3.DTOs.error.ErrorResponseDTO;
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
  public ResponseEntity<ErrorResponseDTO> handleResourceNotFound(ResourceNotFoundException ex) {
    logger.warn("Resource not found: {}", ex.getMessage());
    // Stable public message; specific information goes into details
    return new ResponseEntity<>(
        new ErrorResponseDTO("Resource not found", "RESOURCE_NOT_FOUND", Instant.now().toString(), ex.getMessage()),
        HttpStatus.NOT_FOUND
    );
  }

  @ExceptionHandler(BusinessRuleViolationException.class)
  public ResponseEntity<ErrorResponseDTO> handleBusinessRuleViolation(BusinessRuleViolationException ex) {
    logger.warn("Business rule violation: {}", ex.getMessage());
    return new ResponseEntity<>(
        new ErrorResponseDTO("Business rule violated", "BUSINESS_RULE_VIOLATION", Instant.now().toString(), ex.getMessage()),
        HttpStatus.CONFLICT
    );
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
    logger.warn("Invalid argument: {}", ex.getMessage());
    return new ResponseEntity<>(
        new ErrorResponseDTO("Invalid input provided", "INVALID_INPUT", Instant.now().toString(), ex.getMessage()),
        HttpStatus.BAD_REQUEST
    );
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponseDTO> handleIllegalState(IllegalStateException ex) {
    logger.warn("Illegal state: {}", ex.getMessage());
    // Stable public message; put the specific reason in details
    return new ResponseEntity<>(
        new ErrorResponseDTO("Business constraint violated", "BUSINESS_CONSTRAINT", Instant.now().toString(), ex.getMessage()),
        HttpStatus.CONFLICT
    );
  }

  @ExceptionHandler(GrpcCommunicationException.class)
  public ResponseEntity<ErrorResponseDTO> handleGrpcCommunication(GrpcCommunicationException ex) {
    logger.error("gRPC communication error: {}", ex.getMessage(), ex);
    return new ResponseEntity<>(
        new ErrorResponseDTO("Service temporarily unavailable", "SERVICE_UNAVAILABLE", Instant.now().toString(), ex.getMessage()),
        HttpStatus.SERVICE_UNAVAILABLE
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDTO> handleGeneral(Exception ex) {
    logger.error("Unexpected error: {}", ex.getMessage(), ex);
    return new ResponseEntity<>(
        new ErrorResponseDTO("An unexpected error occurred", "INTERNAL_ERROR", Instant.now().toString(), ex.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR
    );
  }
}