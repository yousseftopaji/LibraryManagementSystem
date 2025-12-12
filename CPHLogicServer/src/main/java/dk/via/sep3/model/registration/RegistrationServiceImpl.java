package dk.via.sep3.model.registration;

import dk.via.sep3.grpcConnection.registrationGrpcService.RegistrationGrpcService;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.utils.validation.Validator;
import dk.via.sep3.security.IPasswordEncoderService;
import org.springframework.stereotype.Service;

@Service public class RegistrationServiceImpl implements RegistrationService
{
  private final RegistrationGrpcService registrationGrpcService;
  private final Validator validator;
  private final IPasswordEncoderService passwordEncoderService;

  public RegistrationServiceImpl(
      RegistrationGrpcService registrationGrpcService, Validator validator, IPasswordEncoderService passwordEncoderService)
  {
    this.registrationGrpcService = registrationGrpcService;
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
    return registrationGrpcService.register(user);
  }
}
