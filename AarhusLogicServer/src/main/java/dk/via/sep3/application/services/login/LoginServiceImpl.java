package dk.via.sep3.application.services.login;

import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.application.domain.User;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.security.PasswordService;
import org.springframework.stereotype.Service;

/**
 * Implementation of login/authentication logic.
 *
 * @author Group 7
 */
@Service public class LoginServiceImpl implements LoginService
{
  private final UserGrpcService userGrpcService;
  private final PasswordService passwordService;

  public LoginServiceImpl(UserGrpcService userGrpcService,
      PasswordService passwordService)
  {
    this.userGrpcService = userGrpcService;
    this.passwordService = passwordService;
  }

  @Override public User login(User request)
  {

    if (request == null || request.getUsername() == null
        || request.getPassword() == null || request.getUsername().isEmpty()
        || request.getPassword().isEmpty())
    {
      throw new BusinessRuleViolationException(
          "Username and Password must be provided");
    }

    User user = userGrpcService.getUserByUsername(request.getUsername());

    if (user == null)
    {
      throw new BusinessRuleViolationException("User not found");
    }

    boolean matches = passwordService.matches(request.getPassword(),
        user.getPassword());

    if (!matches)
    {
      throw new BusinessRuleViolationException("Wrong password");
    }

    return user;
  }
}
