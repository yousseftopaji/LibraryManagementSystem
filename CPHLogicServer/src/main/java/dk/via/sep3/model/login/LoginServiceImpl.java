package dk.via.sep3.model.login;

import dk.via.sep3.grpcConnection.loginService.AuthenticationGrpcService;
import dk.via.sep3.model.utils.validation.Validator;
import dk.via.sep3.shared.login.LoginDTO;
import dk.via.sep3.shared.login.LoginResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
  private final AuthenticationGrpcService authenticationGrpcService;
  private final Validator validator;

  public LoginServiceImpl(AuthenticationGrpcService authenticationGrpcService, Validator validator) {
    this.authenticationGrpcService = authenticationGrpcService;
    this.validator = validator;
  }

  @Override
  public LoginResponseDTO login(LoginDTO loginDTO) {
    validator.validateUsername(loginDTO.getUsername());
    validator.validatePassword(loginDTO.getPassword());

    String token = authenticationGrpcService.login(loginDTO.getUsername(), loginDTO.getPassword());
    return new LoginResponseDTO(token);
  }
}
