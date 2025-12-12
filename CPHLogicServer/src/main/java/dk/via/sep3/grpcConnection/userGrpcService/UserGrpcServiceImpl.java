package dk.via.sep3.grpcConnection.userGrpcService;

import dk.via.sep3.GetUserByUsernameRequest;
import dk.via.sep3.GetUserByUsernameResponse;
import dk.via.sep3.UserServiceGrpc;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.mapper.userMapper.UserMapper;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserGrpcServiceImpl implements UserGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(
      UserGrpcServiceImpl.class);
  private final UserServiceGrpc.UserServiceBlockingStub userStub;
  private final UserMapper userMapper;
  public UserGrpcServiceImpl(ManagedChannel channel, UserMapper userMapper)
  {
    this.userStub = UserServiceGrpc.newBlockingStub(channel);
    this.userMapper = userMapper;
  }

  @Override public User getUserByUsername(String username)
  {
    logger.info("Validating if user exists with username: {}", username);
    try
    {
      GetUserByUsernameRequest request = GetUserByUsernameRequest
          .newBuilder().setUsername(username).build();

      GetUserByUsernameResponse response = userStub
          .getUserByUsername(request);

      // Check if user exists in the response
      if (response.hasUser() && response.getUser().getUsername() != null
          && !response.getUser().getUsername().isEmpty())
      {
        logger.info("User found: {}", response.getUser());
        return userMapper.mapDTOUserToDomain(response.getUser());
      }
      else
      {
        logger.info("User not found with username: {}", username);
        return null;
      }
    }
    catch (Exception ex)
    {
      logger.error("Error fetching user by username: {}", username, ex);
      return null;
    }
  }
}
