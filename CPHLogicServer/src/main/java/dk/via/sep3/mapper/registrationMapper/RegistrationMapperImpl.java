package dk.via.sep3.mapper.registrationMapper;

import dk.via.sep3.DTOUser;
import dk.via.sep3.DTOs.registration.CreateRegisterDTO;
import dk.via.sep3.model.domain.User;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapperImpl implements RegistrationMapper
{
  @Override
  public User mapCreateRegisterDTOToDomain(CreateRegisterDTO createRegisterDTO) {
    User user = new User();
    user.setName(createRegisterDTO.getFullName());
    user.setUsername(createRegisterDTO.getUsername());
    user.setEmail(createRegisterDTO.getEmail());
    user.setPhoneNumber(createRegisterDTO.getPhoneNumber());
    user.setPassword(createRegisterDTO.getPassword());
    return user;
  }

  @Override
  public DTOUser mapDomainToDTOUser(User user) {
    return DTOUser.newBuilder()
        .setUsername(user.getUsername())
        .setName(user.getName())
        .setEmail(user.getEmail())
        .setPhoneNumber(user.getPhoneNumber())
        .setPassword(user.getPassword())
        .setRole(user.getRole())
        .build();
  }

  @Override
  public User mapDTOUserToDomain(DTOUser dtoUser) {
    User user = new User();
    user.setUsername(dtoUser.getUsername());
    user.setName(dtoUser.getName());
    user.setEmail(dtoUser.getEmail());
    user.setPhoneNumber(dtoUser.getPhoneNumber());
    user.setPassword(dtoUser.getPassword());
    user.setRole(dtoUser.getRole());
    return user;
  }

//  @Override
//  public RegisterDTO mapDomainToRegisterDTO(User user) {
//    return new RegisterDTO(true, "User registered successfully");
//  }
}
