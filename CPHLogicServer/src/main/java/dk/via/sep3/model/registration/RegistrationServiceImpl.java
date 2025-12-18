package dk.via.sep3.model.registration;

import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.validation.Validator;
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

  /**
   * Register a new user.
   *
   * This method validates user fields (name, email, phone, username and password),
   * hashes the password and delegates user creation to the gRPC user service.
   *
   * @param user the user to register; password should be plaintext and will be hashed
   * @return the created User object from the backend
   * @throws IllegalArgumentException if any validation fails
   */
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
