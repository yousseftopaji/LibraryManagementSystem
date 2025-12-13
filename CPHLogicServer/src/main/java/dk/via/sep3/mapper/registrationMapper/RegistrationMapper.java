package dk.via.sep3.mapper.registrationMapper;

import dk.via.sep3.DTOUser;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.DTOs.registration.CreateRegisterDTO;

public interface RegistrationMapper
{
  User mapCreateRegisterDTOToDomain(CreateRegisterDTO createRegisterDTO);

  DTOUser mapDomainToDTOUser(User user);

  User mapDTOUserToDomain(DTOUser dtoUser);

//  RegisterDTO mapDomainToRegisterDTO(User user);
}
