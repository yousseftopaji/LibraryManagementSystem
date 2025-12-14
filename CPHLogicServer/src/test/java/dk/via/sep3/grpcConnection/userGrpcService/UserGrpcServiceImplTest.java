package dk.via.sep3.grpcConnection.userGrpcService;

import dk.via.sep3.DTOUser;
import dk.via.sep3.GetUserByUsernameRequest;
import dk.via.sep3.GetUserByUsernameResponse;
import dk.via.sep3.UserServiceGrpc;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.mapper.userMapper.UserMapper;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserGrpcServiceImplTest {

  private UserServiceGrpc.UserServiceBlockingStub stub;
  private UserMapper mapper;
  private UserGrpcServiceImpl service;

  @BeforeEach
  void setUp() throws Exception {
    stub = mock(UserServiceGrpc.UserServiceBlockingStub.class);
    mapper = mock(UserMapper.class);

    ManagedChannel channel = mock(ManagedChannel.class);
    service = new UserGrpcServiceImpl(channel, mapper);

    //  Inject mocked stub via reflection
    Field stubField = UserGrpcServiceImpl.class
        .getDeclaredField("userStub");
    stubField.setAccessible(true);
    stubField.set(service, stub);
  }


  // getUserByUsername() — user exists


  @Test
  void getUserByUsername_userExists_returnsMappedUser() {
    DTOUser dtoUser = DTOUser.newBuilder()
        .setUsername("john")
        .setName("John Doe")
        .setRole("READER")
        .build();

    GetUserByUsernameResponse response =
        GetUserByUsernameResponse.newBuilder()
            .setUser(dtoUser)
            .build();

    User mappedUser = new User();
    mappedUser.setUsername("john");
    mappedUser.setRole("READER");

    when(stub.getUserByUsername(any(GetUserByUsernameRequest.class)))
        .thenReturn(response);
    when(mapper.mapDTOUserToDomain(dtoUser)).thenReturn(mappedUser);

    User result = service.getUserByUsername("john");

    assertNotNull(result);
    assertEquals("john", result.getUsername());
    assertEquals("READER", result.getRole());
  }


  // getUserByUsername() — user not found


  @Test
  void getUserByUsername_userNotFound_returnsNull() {
    GetUserByUsernameResponse response =
        GetUserByUsernameResponse.newBuilder().build();

    when(stub.getUserByUsername(any(GetUserByUsernameRequest.class)))
        .thenReturn(response);

    User result = service.getUserByUsername("unknown");

    assertNull(result);
  }


  // getUserByUsername() — exception


  @Test
  void getUserByUsername_exception_returnsNull() {
    when(stub.getUserByUsername(any()))
        .thenThrow(new RuntimeException("grpc error"));

    User result = service.getUserByUsername("john");

    assertNull(result);
  }
}
