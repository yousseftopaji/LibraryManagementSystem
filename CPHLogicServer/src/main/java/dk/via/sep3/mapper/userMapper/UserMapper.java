package dk.via.sep3.mapper.userMapper;

import dk.via.sep3.DTOUser;
import dk.via.sep3.application.domain.User;
import dk.via.sep3.DTOs.user.UserDTO;

public interface UserMapper
{
  User mapUserDTOToDomain(UserDTO userDTO);
  DTOUser mapDomainToDTOUser(User user);
  User mapDTOUserToDomain(DTOUser dtoUser);
  UserDTO mapDomainToUserDTO(User user);
}
