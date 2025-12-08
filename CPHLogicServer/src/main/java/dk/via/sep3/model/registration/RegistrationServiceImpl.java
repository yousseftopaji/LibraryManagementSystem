package dk.via.sep3.model.registration;

import dk.via.sep3.grpcConnection.registrationService.RegistrationGrpcService;
import dk.via.sep3.model.utils.validation.Validator;
import dk.via.sep3.shared.registration.CreateRegisterDTO;
import dk.via.sep3.shared.registration.RegisterDTO;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl implements RegistrationService
{
  private final RegistrationGrpcService registrationGrpcService;
  private final Validator validator;

  public RegistrationServiceImpl(RegistrationGrpcService registrationGrpcService, Validator validator) {
    this.registrationGrpcService = registrationGrpcService;
    this.validator = validator;
  }


  @Override
  public RegisterDTO register(CreateRegisterDTO createRegisterDTO) {
    // Validate all fields
    validator.validateFullName(createRegisterDTO.getFullName());
    validator.validateEmail(createRegisterDTO.getEmail());
    validator.validatePhoneNumber(createRegisterDTO.getPhoneNumber());
    validator.validateUsername(createRegisterDTO.getUsername());
    validator.validatePassword(createRegisterDTO.getPassword());

    // Call gRPC service to register
    boolean success = registrationGrpcService.register(createRegisterDTO);

    if (success) {
      return new RegisterDTO(true, "Registration successful. You may now log in.");
    } else {
      return new RegisterDTO(false, "Username already in use.");
    }
  }
}
