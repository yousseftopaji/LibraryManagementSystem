package dk.via.sep3.shared.mapper.userMapper;

import dk.via.sep3.DTOUser;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.registration.RegistrationDTO;
import dk.via.sep3.shared.user.UserDTO;

public interface UserMapper
{
  User mapUserDTOToDomain(UserDTO userDTO);
  DTOUser mapDomainToDTOUser(User user);
  User mapDTOUserToDomain(DTOUser dtoUser);
  UserDTO mapDomainToUserDTO(User user);
  User mapRegistrationDTOToDomain(RegistrationDTO registrationDTO);
}
