package dk.via.sep3.model.registration;

import dk.via.sep3.grpcConnection.registrationGrpcService.RegistrationGrpcService;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.utils.validation.Validator;
import dk.via.sep3.security.IPasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServiceImplTest {

  private RegistrationGrpcService grpc;
  private Validator validator;
  private IPasswordEncoderService passwordEncoder;
  private RegistrationServiceImpl service;

  @BeforeEach
  void setup() {
    grpc = mock(RegistrationGrpcService.class);
    validator = mock(Validator.class);
    passwordEncoder = mock(IPasswordEncoderService.class);

    service = new RegistrationServiceImpl(grpc, validator, passwordEncoder);
  }


  @Test
  void register_callsAllValidators() {
    User user = new User();
    user.setName("John Doe");
    user.setEmail("john@example.com");
    user.setPhoneNumber("12345678");
    user.setUsername("john123");
    user.setPassword("secret");

    when(passwordEncoder.encode("secret")).thenReturn("hashed");

    User returned = new User();
    returned.setUsername("john123");

    when(grpc.register(any(User.class))).thenReturn(returned);

    service.register(user);

    verify(validator).validateFullName("John Doe");
    verify(validator).validateEmail("john@example.com");
    verify(validator).validatePhoneNumber("12345678");
    verify(validator).validateUsername("john123");
    verify(validator).validatePassword("secret");
  }


  @Test
  void register_hashesPasswordBeforeGrpcCall() {
    User user = new User();
    user.setName("John Doe");
    user.setEmail("john@example.com");
    user.setPhoneNumber("12345678");
    user.setUsername("john123");
    user.setPassword("secret");

    when(passwordEncoder.encode("secret")).thenReturn("hashedPASS");

    User returned = new User();
    returned.setUsername("john123");

    when(grpc.register(any(User.class))).thenReturn(returned);

    service.register(user);

    // ensure password is replaced with hashed value BEFORE sending to gRPC
    assertEquals("hashedPASS", user.getPassword());
  }


  @Test
  void register_callsGrpcWithHashedPasswordUser() {
    User user = new User();
    user.setName("John Doe");
    user.setEmail("john@example.com");
    user.setPhoneNumber("12345678");
    user.setUsername("john123");
    user.setPassword("secret");

    when(passwordEncoder.encode("secret")).thenReturn("hashedPASS");

    User expectedResponse = new User();
    expectedResponse.setUsername("john123");

    when(grpc.register(any(User.class))).thenReturn(expectedResponse);

    User result = service.register(user);

    assertNotNull(result);
    assertEquals("john123", result.getUsername());

    // Verify gRPC received a user with hashed password
    verify(grpc, times(1)).register(argThat(u ->
        u.getPassword().equals("hashedPASS")
    ));
  }
}
