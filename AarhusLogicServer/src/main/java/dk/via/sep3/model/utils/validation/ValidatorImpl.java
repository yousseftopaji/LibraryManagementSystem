package dk.via.sep3.model.utils.validation;

import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service public class ValidatorImpl implements Validator
{
  private static final Logger logger = LoggerFactory.getLogger(
      ValidatorImpl.class);
  private final UserGrpcService userGrpcService;

  public ValidatorImpl(UserGrpcService userGrpcService)
  {
    this.userGrpcService = userGrpcService;
  }

  @Override public void validateUser(String username)
  {
    if (userGrpcService.getUserByUsername(username) == null)
    {
      logger.error("validateUser: User not found with username: {}",
          username);
      throw new IllegalArgumentException(
          "User not found with username: " + username);
    }
  }
}