package dk.via.sep3.model.login;

import dk.via.sep3.shared.login.LoginDTO;
import dk.via.sep3.shared.login.LoginResponseDTO;

public interface LoginService
{
  LoginResponseDTO login(LoginDTO loginDTO);
}
