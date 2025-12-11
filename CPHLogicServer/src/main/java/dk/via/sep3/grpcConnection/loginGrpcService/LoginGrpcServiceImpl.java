package dk.via.sep3.grpcConnection.loginGrpcService;

import dk.via.sep3.LoginRequest;
import dk.via.sep3.LoginResponse;
import dk.via.sep3.AuthenticationServiceGrpc;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.mapper.userMapper.UserMapper;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoginGrpcServiceImpl implements LoginGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(LoginGrpcService.class);
  private final AuthenticationServiceGrpc.AuthenticationServiceBlockingStub authStub;
  private final UserMapper userMapper;

  public LoginGrpcServiceImpl(ManagedChannel channel, UserMapper userMapper)
  {
    this.authStub = AuthenticationServiceGrpc.newBlockingStub(channel);
    this.userMapper = userMapper;
  }

  @Override
  public User login(String username, String password) {
    logger.info("Sending gRPC request to login user: {}", username);

    LoginRequest request = LoginRequest.newBuilder()
        .setUsername(username)
        .setPassword(password)
        .build();

    LoginResponse response = authStub.login(request);

    if (!response.getSuccess()) {
      throw new RuntimeException("Login failed: " + response.getMessage());
    }

    logger.info("User logged in successfully: {}", username);

    // Use UserMapper to convert gRPC DTOUser to domain User
    return userMapper.mapDTOUserToDomain(response.getUser());
  }
}

