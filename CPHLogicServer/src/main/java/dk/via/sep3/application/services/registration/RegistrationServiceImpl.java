package dk.via.sep3.application.services.registration;

import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.application.domain.User;
import dk.via.sep3.application.services.validation.Validator;
import dk.via.sep3.security.IPasswordEncoderService;
import org.springframework.stereotype.Service;

@Service public class RegistrationServiceImpl implements RegistrationService
{
  private final UserGrpcService userGrpcService;
  private final Validator validator;
  private final IPasswordEncoderService passwordEncoderService;

  public RegistrationServiceImpl(
      UserGrpcService userGrpcService, Validator validator, IPasswordEncoderService passwordEncoderService)
  {
    this.userGrpcService = userGrpcService;
    this.validator = validator;
    this.passwordEncoderService = passwordEncoderService;
  }

  @Override
  public User register(User user) {
    // Validate all fields
    validator.validateFullName(user.getName());
    validator.validateEmail(user.getEmail());
    validator.validatePhoneNumber(user.getPhoneNumber());
    validator.validateUsername(user.getUsername());
    validator.validatePassword(user.getPassword());

    // Hash the password before sending
    String hashedPassword = passwordEncoderService.encode(user.getPassword());
    user.setPassword(hashedPassword);

    // Call gRPC service to register
    return userGrpcService.createUser(user);
  }
}
