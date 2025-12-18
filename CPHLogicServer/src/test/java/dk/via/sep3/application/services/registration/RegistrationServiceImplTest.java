package dk.via.sep3.application.services.registration;

import dk.via.sep3.application.domain.User;
import dk.via.sep3.application.services.validation.Validator;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.security.IPasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServiceImplTest {

  private UserGrpcService userGrpcService;
  private Validator validator;
  private IPasswordEncoderService passwordEncoderService;
  private RegistrationServiceImpl registrationService;

  @BeforeEach
  void setUp() {
    userGrpcService = mock(UserGrpcService.class);
    validator = mock(Validator.class);
    passwordEncoderService = mock(IPasswordEncoderService.class);

    registrationService = new RegistrationServiceImpl(
        userGrpcService, validator, passwordEncoderService
    );
  }

  // ------------------------------------------------------------
  // register() - success
  // ------------------------------------------------------------

  @Test
  void register_validUser_encodesPasswordAndCreatesUser() {
    User user = new User();
    user.setName("John Doe");
    user.setEmail("john@email.com");
    user.setPhoneNumber("12345678");
    user.setUsername("john");
    user.setPassword("plainPassword");

    when(passwordEncoderService.encode("plainPassword"))
        .thenReturn("hashedPassword");
    when(userGrpcService.createUser(any(User.class)))
        .thenReturn(user);

    User result = registrationService.register(user);

    assertEquals("hashedPassword", user.getPassword());
    assertEquals(user, result);

    verify(validator).validateFullName("John Doe");
    verify(validator).validateEmail("john@email.com");
    verify(validator).validatePhoneNumber("12345678");
    verify(validator).validateUsername("john");
    verify(validator).validatePassword("plainPassword");

    verify(passwordEncoderService).encode("plainPassword");
    verify(userGrpcService).createUser(user);
  }

  // ------------------------------------------------------------
  // register() - validation failure
  // ------------------------------------------------------------

  @Test
  void register_invalidUser_validationFails_doesNotCreateUser() {
    User user = new User();
    user.setName(""); // invalid
    user.setEmail("john@email.com");
    user.setPhoneNumber("12345678");
    user.setUsername("john");
    user.setPassword("password");

    doThrow(new IllegalArgumentException("Full name cannot be empty"))
        .when(validator).validateFullName("");

    assertThrows(IllegalArgumentException.class,
        () -> registrationService.register(user));

    verify(passwordEncoderService, never()).encode(any());
    verify(userGrpcService, never()).createUser(any());
  }
}
