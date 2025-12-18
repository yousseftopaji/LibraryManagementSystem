package dk.via.sep3.controller;

import dk.via.sep3.DTOs.login.LoginRequestDTO;
import dk.via.sep3.DTOs.login.LoginResponseDTO;
import dk.via.sep3.mapper.userMapper.UserMapper;
import dk.via.sep3.application.domain.User;
import dk.via.sep3.application.services.login.LoginService;
import dk.via.sep3.security.JwtUtil;
import dk.via.sep3.DTOs.auth.RegisterResponseDTO;
import dk.via.sep3.DTOs.registration.RegistrationDTO;
import dk.via.sep3.application.services.register.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/auth") public class AuthController
{
  private final UserMapper userMapper;
  private final RegisterService registerService;
  private final JwtUtil jwtUtil;
  private final LoginService loginService;

  public AuthController(UserMapper userMapper, RegisterService registerService,
      JwtUtil jwtUtil, LoginService loginService)
  {
    this.userMapper = userMapper;
    this.registerService = registerService;
    this.jwtUtil = jwtUtil;
    this.loginService = loginService;
  }

  @PostMapping("/register") public ResponseEntity<RegisterResponseDTO> register(
      @RequestBody RegistrationDTO req)
  {
    User user = userMapper.mapRegistrationDTOToDomain(req);

    User registeredUser = registerService.register(user);

    RegisterResponseDTO registerResponseDTO = userMapper.mapDomainToRegisterResponseDTO(
        registeredUser);

    return new ResponseEntity<>(registerResponseDTO, HttpStatus.CREATED);
  }

  @PostMapping(value = "/login")
  public ResponseEntity<LoginResponseDTO> login(
      @RequestBody LoginRequestDTO request)
  {
    User user = userMapper.mapLoginRequestToDomain(request);

    User authenticatedUser = loginService.login(user);
    String token = jwtUtil.generateToken(authenticatedUser.getUsername(),
        authenticatedUser.getRole());

    LoginResponseDTO loginResponseDTO = new LoginResponseDTO(token, authenticatedUser.getUsername());
    return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);
  }
}
