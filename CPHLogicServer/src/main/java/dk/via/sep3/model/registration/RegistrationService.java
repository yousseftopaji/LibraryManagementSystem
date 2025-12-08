package dk.via.sep3.model.registration;

import dk.via.sep3.shared.registration.CreateRegisterDTO;
import dk.via.sep3.shared.registration.RegisterDTO;

public interface RegistrationService
{
  RegisterDTO register(CreateRegisterDTO createRegisterDTO);
}