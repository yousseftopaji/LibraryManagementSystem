package dk.via.sep3.model.registration;

import dk.via.sep3.shared.registration.CreateRegisterDTO;

public interface RegistrationService
{
  void register(CreateRegisterDTO createRegisterDTO);
}