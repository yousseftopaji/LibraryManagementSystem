package dk.via.sep3.controller;

import dk.via.sep3.model.login.LoginService;
import dk.via.sep3.shared.login.LoginDTO;
import dk.via.sep3.shared.login.LoginResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController
{
  private final LoginService loginService;

  public LoginController(LoginService loginService)
  {
    this.loginService = loginService;
  }

  @PostMapping
  public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO)
  {
    try
    {
      LoginResponseDTO response = loginService.login(loginDTO);

      if (response.isSuccess())
      {
        return new ResponseEntity<>(response, HttpStatus.OK);
      }
      else
      {
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
      }
    }
    catch (IllegalArgumentException e)
    {
      // Validation failed
      return new ResponseEntity<>(new LoginResponseDTO(false, "Invalid username or password.", null), HttpStatus.BAD_REQUEST);
    }
    catch (Exception e)
    {
      // Unexpected error
      return new ResponseEntity<>(new LoginResponseDTO(false, "Invalid username or password.", null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
