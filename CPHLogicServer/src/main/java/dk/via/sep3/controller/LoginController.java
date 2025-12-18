package dk.via.sep3.controller;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.login.LoginService;
import dk.via.sep3.security.IJwtTokenProvider;
import dk.via.sep3.DTOs.login.LoginDTO;
import dk.via.sep3.DTOs.login.LoginResponseDTO;
import dk.via.sep3.mapper.loginMapper.LoginMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginController
{
  private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
  private final LoginService loginService;
  private final LoginMapper loginMapper;
  private final IJwtTokenProvider jwtTokenProvider;

  public LoginController(LoginService loginService, LoginMapper loginMapper, IJwtTokenProvider jwtTokenProvider)
  {
    this.loginService = loginService;
    this.loginMapper = loginMapper;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
  public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO)
  {
    logger.info("Login attempt for user: {}", loginDTO.getUsername());

    // Map DTO to domain
    User user = loginMapper.mapLoginDTOToDomain(loginDTO);

    // Perform login (validates credentials and returns user from DB)
    User authenticatedUser = loginService.login(user);

    // Generate JWT token
    String token = jwtTokenProvider.generateToken(authenticatedUser.getUsername(), authenticatedUser.getRole());

    // Build response
    LoginResponseDTO response = loginMapper.mapDomainToLoginResponse(token, authenticatedUser);

    logger.info("User logged in successfully: {}", response.getUsername());
    return ResponseEntity.ok(response);

  }
}
