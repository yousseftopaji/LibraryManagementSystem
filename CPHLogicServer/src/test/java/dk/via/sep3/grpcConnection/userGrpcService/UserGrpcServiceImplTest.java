package dk.via.sep3.grpcConnection.userGrpcService;

import dk.via.sep3.*;
import dk.via.sep3.application.domain.User;
import dk.via.sep3.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.mapper.userMapper.UserMapper;
import io.grpc.ManagedChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserGrpcServiceImplTest {

  private UserServiceGrpc.UserServiceBlockingStub userStub;
  private UserMapper userMapper;
  private UserGrpcServiceImpl service;

  @BeforeEach
  void setUp() {
    userStub = mock(UserServiceGrpc.UserServiceBlockingStub.class);
    userMapper = mock(UserMapper.class);

    ManagedChannel channel = mock(ManagedChannel.class);
    service = new UserGrpcServiceImpl(channel, userMapper);

    injectStub(service, userStub);
  }

  private void injectStub(UserGrpcServiceImpl service,
      UserServiceGrpc.UserServiceBlockingStub stub) {
    try {
      var field = UserGrpcServiceImpl.class.getDeclaredField("userStub");
      field.setAccessible(true);
      field.set(service, stub);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  // ---------------------------------------------------
  // getUserByUsername()
  // ---------------------------------------------------

  @Test
  void getUserByUsername_userExists_returnsMappedUser() {
    DTOUser dtoUser = DTOUser.newBuilder()
        .setUsername("john")
        .build();

    GetUserByUsernameResponse response =
        GetUserByUsernameResponse.newBuilder()
            .setUser(dtoUser)
            .build();

    when(userStub.getUserByUsername(any(GetUserByUsernameRequest.class)))
        .thenReturn(response);
    when(userMapper.mapDTOUserToDomain(dtoUser))
        .thenReturn(new User());

    User result = service.getUserByUsername("john");

    assertNotNull(result);
    verify(userMapper).mapDTOUserToDomain(dtoUser);
  }

  @Test
  void getUserByUsername_userNotFound_returnsNull() {
    GetUserByUsernameResponse response =
        GetUserByUsernameResponse.newBuilder().build();

    when(userStub.getUserByUsername(any(GetUserByUsernameRequest.class)))
        .thenReturn(response);

    User result = service.getUserByUsername("unknown");

    assertNull(result);
  }

  @Test
  void getUserByUsername_exception_returnsNull() {
    when(userStub.getUserByUsername(any(GetUserByUsernameRequest.class)))
        .thenThrow(RuntimeException.class);

    User result = service.getUserByUsername("john");

    assertNull(result);
  }

  // ---------------------------------------------------
  // createUser()
  // ---------------------------------------------------

  @Test
  void createUser_success_returnsMappedUser() {
    User domainUser = new User();
    DTOUser dtoUser = DTOUser.newBuilder()
        .setUsername("john")
        .build();

    CreateUserResponse response =
        CreateUserResponse.newBuilder()
            .setSuccess(true)
            .setUser(dtoUser)
            .setMessage("created")
            .build();

    when(userMapper.mapDomainToDTOUser(domainUser))
        .thenReturn(dtoUser);
    when(userStub.createUser(any(CreateUserRequest.class)))
        .thenReturn(response);
    when(userMapper.mapDTOUserToDomain(dtoUser))
        .thenReturn(new User());

    User result = service.createUser(domainUser);

    assertNotNull(result);
    verify(userStub).createUser(any(CreateUserRequest.class));
    verify(userMapper).mapDTOUserToDomain(dtoUser);
  }

  @Test
  void createUser_exception_throwsGrpcCommunicationException() {
    User domainUser = new User();
    DTOUser dtoUser = DTOUser.newBuilder().build();

    when(userMapper.mapDomainToDTOUser(domainUser))
        .thenReturn(dtoUser);
    when(userStub.createUser(any(CreateUserRequest.class)))
        .thenThrow(RuntimeException.class);

    assertThrows(GrpcCommunicationException.class,
        () -> service.createUser(domainUser));
  }
}
