package dk.via.sep3.model.utils.validation;

import dk.via.sep3.controller.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.model.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ValidatorImplTest {

  private UserGrpcService userGrpcService;
  private ValidatorImpl validator;

  @BeforeEach
  void setup() {
    userGrpcService = mock(UserGrpcService.class);
    validator = new ValidatorImpl(userGrpcService);
  }



  @Test
  void validateUser_throwsWhenUserNotFound() {
    when(userGrpcService.getUserByUsername("john")).thenReturn(null);

    assertThrows(IllegalArgumentException.class,
        () -> validator.validateUser("john"));
  }

  @Test
  void validateUser_passesWhenUserExists() {
    when(userGrpcService.getUserByUsername("john")).thenReturn(new User());

    assertDoesNotThrow(() -> validator.validateUser("john"));
  }


  @Test
  void validateFullName_throwsWhenEmpty() {
    assertThrows(BusinessRuleViolationException.class,
        () -> validator.validateFullName(""));
  }

  @Test
  void validateFullName_throwsWhenTooShort() {
    assertThrows(IllegalArgumentException.class,
        () -> validator.validateFullName("A"));
  }

  @Test
  void validateFullName_validName() {
    assertDoesNotThrow(() -> validator.validateFullName("John Doe"));
  }


  @Test
  void validateEmail_throwsWhenEmpty() {
    assertThrows(IllegalArgumentException.class,
        () -> validator.validateEmail(""));
  }

  @Test
  void validateEmail_throwsWhenInvalidFormat() {
    assertThrows(IllegalArgumentException.class,
        () -> validator.validateEmail("invalid-email"));
  }

  @Test
  void validateEmail_validEmail() {
    assertDoesNotThrow(() -> validator.validateEmail("john@example.com"));
  }


  @Test
  void validatePhoneNumber_throwsWhenEmpty() {
    assertThrows(IllegalArgumentException.class,
        () -> validator.validatePhoneNumber(""));
  }

  @Test
  void validatePhoneNumber_throwsWhenLessThan8Digits() {
    assertThrows(IllegalArgumentException.class,
        () -> validator.validatePhoneNumber("12345"));
  }

  @Test
  void validatePhoneNumber_validNumber() {
    assertDoesNotThrow(() -> validator.validatePhoneNumber("12345678"));
  }



  @Test
  void validateUsername_throwsWhenEmpty() {
    assertThrows(IllegalArgumentException.class,
        () -> validator.validateUsername(""));
  }

  @Test
  void validateUsername_throwsWhenTooShort() {
    assertThrows(IllegalArgumentException.class,
        () -> validator.validateUsername("ab"));
  }

  @Test
  void validateUsername_throwsWhenAlreadyExists() {
    when(userGrpcService.getUserByUsername("john")).thenReturn(new User());

    assertThrows(IllegalArgumentException.class,
        () -> validator.validateUsername("john"));
  }

  @Test
  void validateUsername_validWhenNotInUse() {
    when(userGrpcService.getUserByUsername("newuser")).thenReturn(null);

    assertDoesNotThrow(() -> validator.validateUsername("newuser"));
  }

  @Test
  void validatePassword_throwsWhenEmpty() {
    assertThrows(IllegalArgumentException.class,
        () -> validator.validatePassword(""));
  }

  @Test
  void validatePassword_throwsWhenTooShort() {
    assertThrows(IllegalArgumentException.class,
        () -> validator.validatePassword("short"));
  }

  @Test
  void validatePassword_validPassword() {
    assertDoesNotThrow(() -> validator.validatePassword("password123"));
  }
}
