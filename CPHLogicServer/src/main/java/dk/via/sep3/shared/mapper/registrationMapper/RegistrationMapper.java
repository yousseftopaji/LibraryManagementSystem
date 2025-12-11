package dk.via.sep3.shared.mapper.registrationMapper;

import dk.via.sep3.DTOUser;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.registration.CreateRegisterDTO;

public interface RegistrationMapper
{
  User mapCreateRegisterDTOToDomain(CreateRegisterDTO createRegisterDTO);

  DTOUser mapDomainToDTOUser(User user);

  User mapDTOUserToDomain(DTOUser dtoUser);

//  RegisterDTO mapDomainToRegisterDTO(User user);
}
