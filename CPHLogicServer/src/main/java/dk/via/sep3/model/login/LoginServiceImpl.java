package dk.via.sep3.model.login;

import dk.via.sep3.grpcConnection.loginGrpcService.LoginGrpcService;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.utils.validation.Validator;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
  private final LoginGrpcService authenticationGrpcService;
  private final Validator validator;

  public LoginServiceImpl(LoginGrpcService loginGrpcService, Validator validator) {
    this.authenticationGrpcService = loginGrpcService;
    this.validator = validator;
  }

  @Override
  public User login(User user) {
    // validate
    validator.validateUsername(user.getUsername());
    validator.validatePassword(user.getPassword());

    // Get user from gRPC (validates credentials and returns authenticated user)
    return authenticationGrpcService.login(user.getUsername(), user.getPassword());
  }
}
