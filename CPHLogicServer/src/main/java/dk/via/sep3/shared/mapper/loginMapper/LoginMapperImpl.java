package dk.via.sep3.shared.mapper.loginMapper;

import dk.via.sep3.shared.login.LoginDTO;
import dk.via.sep3.shared.login.LoginResponseDTO;
import dk.via.sep3.model.domain.User;
import org.springframework.stereotype.Component;

@Component
public class LoginMapperImpl implements LoginMapper
{
  @Override
  public User mapLoginDTOToDomain(LoginDTO loginDTO) {
    User user = new User();
    user.setUsername(loginDTO.getUsername());
    user.setPassword(loginDTO.getPassword());
    return user;
  }

  @Override
  public LoginResponseDTO mapDomainToLoginResponse(String token, User user) {
    return new LoginResponseDTO(token, user.getUsername(), user.getRole());
  }
}
