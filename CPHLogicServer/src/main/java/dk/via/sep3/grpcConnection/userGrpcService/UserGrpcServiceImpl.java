package dk.via.sep3.grpcConnection.userGrpcService;

import dk.via.sep3.DTOUser;
import dk.via.sep3.GetUserByUsernameRequest;
import dk.via.sep3.GetUserByUsernameResponse;
import dk.via.sep3.UserServiceGrpc;
import dk.via.sep3.grpcConnection.loanGrpcService.LoanGrpcService;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import io.grpc.ManagedChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserGrpcServiceImpl implements UserGrpcService
{
  private static final Logger logger = LoggerFactory.getLogger(
      LoanGrpcService.class);
  private UserServiceGrpc.UserServiceBlockingStub userStub;

  public UserGrpcServiceImpl(ManagedChannel channel)
  {
    this.userStub = UserServiceGrpc.newBlockingStub(channel);
  }

  @Override public DTOUser getUserByUsername(String username)
  {
    logger.info("Validating if user exists with username: {}", username);
    try
    {
      GetUserByUsernameRequest request = GetUserByUsernameRequest
          .newBuilder().setUsername(username).build();

      GetUserByUsernameResponse response = userStub
          .getUserByUsername(request);

      logger.info("User found: {}", response.getUser());
      return response.getUser();
    }
    catch (Exception ex)
    {
      logger.error("Error fetching user by username: {}", username, ex);
      return null;
    }
  }
}
