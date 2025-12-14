package dk.via.sep3.grpcConnection.registrationGrpcService;

import dk.via.sep3.CreateUserRequest;
import dk.via.sep3.CreateUserResponse;
import dk.via.sep3.DTOUser;
import dk.via.sep3.UserServiceGrpc;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.mapper.registrationMapper.RegistrationMapper;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationGrpcServiceImplTest {

  private UserServiceGrpc.UserServiceBlockingStub stub;
  private RegistrationMapper mapper;
  private RegistrationGrpcServiceImpl service;

  @BeforeEach
  void setUp() throws Exception {
    stub = mock(UserServiceGrpc.UserServiceBlockingStub.class);
    mapper = mock(RegistrationMapper.class);

    ManagedChannel channel = mock(ManagedChannel.class);
    service = new RegistrationGrpcServiceImpl(channel, mapper);

    //  Inject mocked stub via reflection
    Field stubField = RegistrationGrpcServiceImpl.class
        .getDeclaredField("userServiceStub");
    stubField.setAccessible(true);
    stubField.set(service, stub);
  }





  @Test
  void register_success_returnsMappedUser() {
    // ---- Input domain user ----
    User inputUser = new User();
    inputUser.setUsername("john");
    inputUser.setName("John Doe");
    inputUser.setPassword("hashed");
    inputUser.setEmail("john@test.com");
    inputUser.setPhoneNumber("12345678");

    // ---- DTOUser returned from gRPC ----
    DTOUser dtoUser = DTOUser.newBuilder()
        .setUsername("john")
        .setName("John Doe")
        .setRole("READER")
        .build();

    CreateUserResponse response = CreateUserResponse.newBuilder()
        .setUser(dtoUser)
        .setSuccess(true)
        .build();

    User mappedUser = new User();
    mappedUser.setUsername("john");
    mappedUser.setRole("READER");

    when(stub.createUser(any(CreateUserRequest.class))).thenReturn(response);
    when(mapper.mapDTOUserToDomain(dtoUser)).thenReturn(mappedUser);

    // ---- ACT ----
    User result = service.register(inputUser);

    // ---- ASSERT ----
    assertNotNull(result);
    assertEquals("john", result.getUsername());
    assertEquals("READER", result.getRole());

    // ---- VERIFY ----
    verify(stub).createUser(any(CreateUserRequest.class));
    verify(mapper).mapDTOUserToDomain(dtoUser);
  }


  // register() — failure


  @Test
  void register_failure_throwsRuntimeException() {
    User inputUser = new User();
    inputUser.setUsername("john");

    CreateUserResponse response = CreateUserResponse.newBuilder()
        .setSuccess(false)
        .setMessage("Username already exists")
        .build();

    when(stub.createUser(any(CreateUserRequest.class))).thenReturn(response);

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> service.register(inputUser));

    assertTrue(ex.getMessage().contains("Failed to register user"));
  }
}
