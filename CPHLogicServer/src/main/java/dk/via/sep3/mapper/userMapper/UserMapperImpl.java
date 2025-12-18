package dk.via.sep3.mapper.userMapper;

import dk.via.sep3.DTOUser;
import dk.via.sep3.application.domain.User;
import dk.via.sep3.DTOs.user.UserDTO;
import org.springframework.stereotype.Service;

@Service public class UserMapperImpl implements UserMapper
{

  @Override public User mapUserDTOToDomain(UserDTO userDTO)
  {
    User user = new User();
    user.setUsername(userDTO.getUsername());
    user.setPassword(userDTO.getPassword());
    user.setName(userDTO.getName());
    user.setPhoneNumber(userDTO.getPhoneNumber());
    user.setEmail(userDTO.getEmail());
    user.setRole(userDTO.getRole());
    return user;
  }

  @Override public DTOUser mapDomainToDTOUser(User user)
  {
    return DTOUser.newBuilder()
        .setUsername(user.getUsername())
        .setPassword(user.getPassword())
        .setName(user.getName())
        .setPhoneNumber(user.getPhoneNumber())
        .setEmail(user.getPhoneNumber())
        .setRole(user.getRole())
        .build();
  }

  @Override public User mapDTOUserToDomain(DTOUser dtoUser)
  {
    return new User(dtoUser.getName(), dtoUser.getUsername(), dtoUser.getPassword(),
        dtoUser.getRole(), dtoUser.getPhoneNumber(), dtoUser.getEmail());
  }

  @Override public UserDTO mapDomainToUserDTO(User user)
  {
    UserDTO userDTO = new UserDTO();
    userDTO.setUsername(user.getUsername());
    userDTO.setPassword(user.getPassword());
    userDTO.setFullName(user.getName());
    userDTO.setPhoneNumber(user.getPhoneNumber());
    userDTO.setEmail(user.getEmail());
    userDTO.setRole(user.getRole());
    return userDTO;
  }
}
