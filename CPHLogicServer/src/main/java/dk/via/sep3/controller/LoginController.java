package dk.via.sep3.controller;

import dk.via.sep3.model.login.LoginService;
import dk.via.sep3.shared.login.LoginDTO;
import dk.via.sep3.shared.login.LoginResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/login") public class LoginController
{
  private final LoginService loginService;

  public LoginController(LoginService loginService)
  {
    this.loginService = loginService;
  }

  @PostMapping public ResponseEntity<LoginResponseDTO> login(
      @RequestBody LoginDTO loginDTO)
  {
    //map dto to domain
    LoginResponseDTO response = loginService.login(loginDTO);
    // map domain to dto

    //there is a global exception handler that returns appropriate responses for exceptions
    return new ResponseEntity<>(response, HttpStatus.OK);

  }
}
