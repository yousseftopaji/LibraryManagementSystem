package dk.via.sep3.controller;

import dk.via.sep3.DTOs.login.LoginRequestDTO;
import dk.via.sep3.DTOs.login.LoginResponseDTO;
import dk.via.sep3.mapper.userMapper.UserMapper;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.login.LoginService;
import dk.via.sep3.security.JwtUtil;
import dk.via.sep3.DTOs.auth.RegisterResponseDTO;
import dk.via.sep3.DTOs.registration.RegistrationDTO;
import dk.via.sep3.model.register.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication endpoints: registration and login.
 *
 * <p>Delegates registration and authentication logic to the appropriate services and
 * returns DTOs containing user and token information.
 */
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

  /**
   * Register a new user in the system.
   *
   * @param req registration payload as {@link RegistrationDTO}; must not be null
   * @return {@link RegisterResponseDTO} containing created user information
   * @throws dk.via.sep3.exceptionHandler.BusinessRuleViolationException if registration fails due to business rules
   */
  @PostMapping("/register") public ResponseEntity<RegisterResponseDTO> register(
      @RequestBody RegistrationDTO req)
  {
    User user = userMapper.mapRegistrationDTOToDomain(req);

    User registeredUser = registerService.register(user);

    RegisterResponseDTO registerResponseDTO = userMapper.mapDomainToRegisterResponseDTO(
        registeredUser);

    return new ResponseEntity<>(registerResponseDTO, HttpStatus.CREATED);
  }

  /**
   * Authenticate a user and return a JWT token.
   *
   * @param request the {@link LoginRequestDTO} with credentials; must not be null
   * @return {@link LoginResponseDTO} containing JWT token and username
   * @throws dk.via.sep3.exceptionHandler.BusinessRuleViolationException if authentication fails
   */
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
