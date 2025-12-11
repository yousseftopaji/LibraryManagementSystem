package dk.via.sep3.shared.mapper.loginMapper;

import dk.via.sep3.shared.login.LoginDTO;
import dk.via.sep3.shared.login.LoginResponseDTO;
import dk.via.sep3.model.domain.User;

public interface LoginMapper
{
  User mapLoginDTOToDomain(LoginDTO loginDTO);

  LoginResponseDTO mapDomainToLoginResponse(String token, User user);
}
