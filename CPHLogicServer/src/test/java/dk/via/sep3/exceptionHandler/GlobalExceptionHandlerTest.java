package dk.via.sep3.exceptionHandler;

import dk.via.sep3.DTOs.error.ErrorResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  void handleResourceNotFound_returnsNotFound() {
    ResourceNotFoundException ex =
        new ResourceNotFoundException("Book not found");

    ResponseEntity<ErrorResponseDTO> response =
        handler.handleResourceNotFound(ex);

    assertEquals(404, response.getStatusCode().value());
    assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
    assertEquals("Book not found", response.getBody().getDetails());
  }

  @Test
  void handleBusinessRuleViolation_returnsConflict() {
    BusinessRuleViolationException ex =
        new BusinessRuleViolationException("Username already exists");

    ResponseEntity<ErrorResponseDTO> response =
        handler.handleBusinessRuleViolation(ex);

    assertEquals(409, response.getStatusCode().value());
    assertEquals("BUSINESS_RULE_VIOLATION", response.getBody().getErrorCode());
  }

  @Test
  void handleIllegalArgument_returnsBadRequest() {
    IllegalArgumentException ex =
        new IllegalArgumentException("Invalid input");

    ResponseEntity<ErrorResponseDTO> response =
        handler.handleIllegalArgument(ex);

    assertEquals(400, response.getStatusCode().value());
    assertEquals("INVALID_INPUT", response.getBody().getErrorCode());
  }

  @Test
  void handleIllegalState_returnsConflict() {
    IllegalStateException ex =
        new IllegalStateException("Loan already extended");

    ResponseEntity<ErrorResponseDTO> response =
        handler.handleIllegalState(ex);

    assertEquals(409, response.getStatusCode().value());
    assertEquals("BUSINESS_CONSTRAINT", response.getBody().getErrorCode());
  }

  @Test
  void handleGrpcCommunication_returnsServiceUnavailable() {
    GrpcCommunicationException ex =
        new GrpcCommunicationException("gRPC down");

    ResponseEntity<ErrorResponseDTO> response =
        handler.handleGrpcCommunication(ex);

    assertEquals(503, response.getStatusCode().value());
    assertEquals("SERVICE_UNAVAILABLE", response.getBody().getErrorCode());
  }

  @Test
  void handleGeneralException_returnsInternalServerError() {
    Exception ex = new Exception("Unexpected failure");

    ResponseEntity<ErrorResponseDTO> response =
        handler.handleGeneral(ex);

    assertEquals(500, response.getStatusCode().value());
    assertEquals("INTERNAL_ERROR", response.getBody().getErrorCode());
  }
}
