package dk.via.sep3.application.services.validation;

import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("userValidator")
 public class UserValidator implements Validator<String>
{
  private static final Logger logger = LoggerFactory.getLogger(
      UserValidator.class);
  private final UserGrpcService userGrpcService;

  public UserValidator(UserGrpcService userGrpcService)
  {
    this.userGrpcService = userGrpcService;
  }

  @Override public void validate(String username)
  {
    if (userGrpcService.getUserByUsername(username) == null)
    {
      logger.error("validate: User not found with username: {}",
          username);
      throw new IllegalArgumentException(
          "User not found with username: " + username);
    }
  }
}