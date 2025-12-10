package dk.via.sep3.model.registration;

import dk.via.sep3.controller.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.grpcConnection.registrationService.RegistrationGrpcService;
import dk.via.sep3.model.utils.validation.Validator;
import dk.via.sep3.shared.registration.CreateRegisterDTO;
import org.springframework.stereotype.Service;

@Service public class RegistrationServiceImpl implements RegistrationService
{
  private final RegistrationGrpcService registrationGrpcService;
  private final Validator validator;

  public RegistrationServiceImpl(
      RegistrationGrpcService registrationGrpcService, Validator validator)
  {
    this.registrationGrpcService = registrationGrpcService;
    this.validator = validator;
  }

  @Override public void register(CreateRegisterDTO createRegisterDTO)
  {
    // Validate all fields
    try
    {
      validator.validateFullName(createRegisterDTO.getFullName());
      validator.validateEmail(createRegisterDTO.getEmail());
      validator.validatePhoneNumber(createRegisterDTO.getPhoneNumber());
      validator.validateUsername(createRegisterDTO.getUsername());
      validator.validatePassword(createRegisterDTO.getPassword());
    }
    catch (IllegalArgumentException e)
    {
      throw new BusinessRuleViolationException(e.getMessage());
    }
    // Call gRPC service to register - will throw exception if fails
    registrationGrpcService.register(createRegisterDTO);
  }
}
