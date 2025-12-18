package dk.via.sep3.mapper.registrationMapper;

import dk.via.sep3.application.domain.User;
import dk.via.sep3.DTOs.registration.CreateRegisterDTO;

public interface RegistrationMapper
{
  User mapCreateRegisterDTOToDomain(CreateRegisterDTO createRegisterDTO);
}
