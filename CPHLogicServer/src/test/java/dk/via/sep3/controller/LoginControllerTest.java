package dk.via.sep3.controller;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.login.LoginService;
import dk.via.sep3.security.IJwtTokenProvider;
import dk.via.sep3.shared.login.LoginDTO;
import dk.via.sep3.shared.login.LoginResponseDTO;
import dk.via.sep3.shared.mapper.loginMapper.LoginMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

  private LoginService loginService;
  private LoginMapper loginMapper;
  private IJwtTokenProvider jwtTokenProvider;
  private LoginController controller;

  @BeforeEach
  void setup() {
    loginService = mock(LoginService.class);
    loginMapper = mock(LoginMapper.class);
    jwtTokenProvider = mock(IJwtTokenProvider.class);

    controller = new LoginController(loginService, loginMapper, jwtTokenProvider);
  }

  @Test
  void login_validCredentials_returnsLoginResponseDTO() {
    // ----- INPUT DTO -----
    LoginDTO loginDTO = new LoginDTO("john", "password");

    // ----- DOMAIN USER (mapped from DTO) -----
    User domainUser = new User();
    domainUser.setUsername("john");
    domainUser.setPassword("password");

    // ----- AUTHENTICATED USER returned from LoginService -----
    User authenticatedUser = new User();
    authenticatedUser.setUsername("john");
    authenticatedUser.setRole("READER");

    // ----- JWT token -----
    String token = "jwt-token-example";

    // ----- RESPONSE DTO -----
    LoginResponseDTO responseDTO = new LoginResponseDTO();
    responseDTO.setUsername("john");
    responseDTO.setToken(token);

    // ----- MOCK BEHAVIOR -----
    when(loginMapper.mapLoginDTOToDomain(loginDTO)).thenReturn(domainUser);
    when(loginService.login(domainUser)).thenReturn(authenticatedUser);
    when(jwtTokenProvider.generateToken("john", "READER")).thenReturn(token);
    when(loginMapper.mapDomainToLoginResponse(token, authenticatedUser)).thenReturn(responseDTO);

    // ----- ACT -----
    ResponseEntity<LoginResponseDTO> response = controller.login(loginDTO);

    // ----- ASSERT -----
    assertEquals(200, response.getStatusCode().value());
    assertEquals("john", response.getBody().getUsername());
    assertEquals(token, response.getBody().getToken());

    // ----- VERIFY -----
    verify(loginMapper).mapLoginDTOToDomain(loginDTO);
    verify(loginService).login(domainUser);
    verify(jwtTokenProvider).generateToken("john", "READER");
    verify(loginMapper).mapDomainToLoginResponse(token, authenticatedUser);
  }
}
