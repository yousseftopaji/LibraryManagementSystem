package dk.via.sep3.model.login;

import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.security.IPasswordEncoderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
  private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);
  private final UserGrpcService userGrpcService;
  private final IPasswordEncoderService passwordEncoderService;

  public LoginServiceImpl(UserGrpcService userGrpcService, IPasswordEncoderService passwordEncoderService) {
    this.userGrpcService = userGrpcService;
    this.passwordEncoderService = passwordEncoderService;
  }

  @Override
  public User login(User user) {
    logger.info("LoginService: Validating login request for user: {}", user.getUsername());

    // Validate that fields are not empty
    if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }
    if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be empty");
    }

    logger.info("LoginService: Fetching user from database: {}", user.getUsername());
    // Get user from database via gRPC
    User dbUser = userGrpcService.getUserByUsername(user.getUsername());

    if (dbUser == null) {
      logger.warn("LoginService: User not found: {}", user.getUsername());
      throw new IllegalArgumentException("Invalid username or password");
    }

    // Verify password locally
    logger.info("LoginService: Verifying password for user: {}", user.getUsername());
    boolean passwordMatches = passwordEncoderService.matches(user.getPassword(), dbUser.getPassword());

    if (!passwordMatches) {
      logger.warn("LoginService: Invalid password for user: {}", user.getUsername());
      throw new IllegalArgumentException("Invalid username or password");
    }

    logger.info("LoginService: User authenticated successfully: {}", dbUser.getUsername());
    return dbUser;
  }
}
