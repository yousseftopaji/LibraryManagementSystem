package dk.via.sep3.mapper.registrationMapper;

import dk.via.sep3.DTOs.registration.CreateRegisterDTO;
import dk.via.sep3.application.domain.User;
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
    user.setRole("Reader"); // Set default role for new registrations
    return user;
  }
}
