package dk.via.sep3.controller.exceptionHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<String> handleConflict(IllegalStateException e) {
    return ResponseEntity.status(409).body(e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Void> handleServerError(Exception e) {
    return ResponseEntity.status(500).build();
  }
}