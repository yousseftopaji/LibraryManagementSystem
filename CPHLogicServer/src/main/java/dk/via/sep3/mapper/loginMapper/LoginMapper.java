package dk.via.sep3.mapper.loginMapper;

import dk.via.sep3.DTOs.login.LoginDTO;
import dk.via.sep3.DTOs.login.LoginResponseDTO;
import dk.via.sep3.model.domain.User;

public interface LoginMapper
{
  User mapLoginDTOToDomain(LoginDTO loginDTO);

  LoginResponseDTO mapDomainToLoginResponse(String token, User user);
}
