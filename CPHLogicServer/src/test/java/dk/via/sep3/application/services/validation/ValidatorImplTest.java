package dk.via.sep3.application.services.validation;

import dk.via.sep3.application.domain.User;
import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidatorImplTest {

  private UserGrpcService userGrpcService;
  private ValidatorImpl validator;

  @BeforeEach
  void setUp() {
    userGrpcService = mock(UserGrpcService.class);
    validator = new ValidatorImpl(userGrpcService);
  }

  // ------------------------------------------------------------
  // validateUser()
  // ------------------------------------------------------------

  @Test
  void validateUser_existingUser_passes() {
    when(userGrpcService.getUserByUsername("john"))
        .thenReturn(new User());

    assertDoesNotThrow(() -> validator.validateUser("john"));
  }

  @Test
  void validateUser_userNotFound_throwsException() {
    when(userGrpcService.getUserByUsername("john"))
        .thenReturn(null);

    assertThrows(BusinessRuleViolationException.class,
        () -> validator.validateUser("john"));
  }

  // ------------------------------------------------------------
  // validateFullName()
  // ------------------------------------------------------------

  @Test
  void validateFullName_valid_passes() {
    assertDoesNotThrow(() -> validator.validateFullName("John Doe"));
  }

  @Test
  void validateFullName_empty_throwsException() {
    assertThrows(BusinessRuleViolationException.class,
        () -> validator.validateFullName(""));
  }

  @Test
  void validateFullName_tooShort_throwsException() {
    assertThrows(BusinessRuleViolationException.class,
        () -> validator.validateFullName("J"));
  }

  // ------------------------------------------------------------
  // validateEmail()
  // ------------------------------------------------------------

  @Test
  void validateEmail_valid_passes() {
    assertDoesNotThrow(() -> validator.validateEmail("john@test.com"));
  }

  @Test
  void validateEmail_invalidFormat_throwsException() {
    assertThrows(BusinessRuleViolationException.class,
        () -> validator.validateEmail("john.com"));
  }

  // ------------------------------------------------------------
  // validatePhoneNumber()
  // ------------------------------------------------------------

  @Test
  void validatePhoneNumber_valid_passes() {
    assertDoesNotThrow(() -> validator.validatePhoneNumber("12345678"));
  }

  @Test
  void validatePhoneNumber_tooShort_throwsException() {
    assertThrows(BusinessRuleViolationException.class,
        () -> validator.validatePhoneNumber("123"));
  }

  // ------------------------------------------------------------
  // validateUsername()
  // ------------------------------------------------------------

  @Test
  void validateUsername_validAndNotExisting_passes() {
    when(userGrpcService.getUserByUsername("john"))
        .thenReturn(null);

    assertDoesNotThrow(() -> validator.validateUsername("john"));
  }

  @Test
  void validateUsername_existingUser_throwsException() {
    when(userGrpcService.getUserByUsername("john"))
        .thenReturn(new User());

    assertThrows(BusinessRuleViolationException.class,
        () -> validator.validateUsername("john"));
  }

  @Test
  void validateUsername_tooShort_throwsException() {
    assertThrows(BusinessRuleViolationException.class,
        () -> validator.validateUsername("jo"));
  }

  // ------------------------------------------------------------
  // validatePassword()
  // ------------------------------------------------------------

  @Test
  void validatePassword_valid_passes() {
    assertDoesNotThrow(() -> validator.validatePassword("password123"));
  }

  @Test
  void validatePassword_tooShort_throwsException() {
    assertThrows(BusinessRuleViolationException.class,
        () -> validator.validatePassword("pass"));
  }
}
