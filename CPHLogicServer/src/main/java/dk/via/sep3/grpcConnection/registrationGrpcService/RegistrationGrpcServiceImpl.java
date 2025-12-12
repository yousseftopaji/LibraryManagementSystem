package dk.via.sep3.grpcConnection.registrationGrpcService;


import dk.via.sep3.CreateUserRequest;
import dk.via.sep3.CreateUserResponse;
import dk.via.sep3.DTOUser;
import dk.via.sep3.UserServiceGrpc;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.mapper.registrationMapper.RegistrationMapper;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RegistrationGrpcServiceImpl implements RegistrationGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(RegistrationGrpcService.class);
  private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;
  private final RegistrationMapper registrationMapper;

  public RegistrationGrpcServiceImpl(ManagedChannel channel, RegistrationMapper registrationMapper)
  {
    this.userServiceStub = UserServiceGrpc.newBlockingStub(channel);
    this.registrationMapper = registrationMapper;
  }

  @Override
  public User register(User user)
  {
    logger.info("Sending gRPC request to register user: {}",
        user.getUsername());

    // Build CreateUserRequest using DTOUser from proto
    DTOUser dtoUser = DTOUser.newBuilder().setUsername(user.getUsername())
        .setName(user.getName()).setPassword(user.getPassword())
        .setEmail(user.getEmail()).setPhoneNumber(user.getPhoneNumber())
        .setRole("Reader").build();

    CreateUserRequest request = CreateUserRequest.newBuilder().setUser(dtoUser)
        .build();

    CreateUserResponse response = userServiceStub.createUser(request);

    if (!response.getSuccess())
    {
      throw new RuntimeException(
          "Failed to register user: " + response.getMessage());
    }

    logger.info("User registered successfully: {}", user.getUsername());
    return registrationMapper.mapDTOUserToDomain(response.getUser());
  }
}
