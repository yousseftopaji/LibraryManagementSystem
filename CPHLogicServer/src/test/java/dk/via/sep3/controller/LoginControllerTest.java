package dk.via.sep3.controller;

import dk.via.sep3.application.domain.User;
import dk.via.sep3.application.services.login.LoginService;
import dk.via.sep3.DTOs.login.LoginDTO;
import dk.via.sep3.DTOs.login.LoginResponseDTO;
import dk.via.sep3.mapper.loginMapper.LoginMapper;
import dk.via.sep3.security.IJwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {

  private LoginService loginService;
  private LoginMapper loginMapper;
  private IJwtTokenProvider jwtTokenProvider;
  private LoginController controller;

  private LoginDTO loginDTO;
  private User user;
  private LoginResponseDTO responseDTO;

  @BeforeEach
  void setup() {
    loginService = mock(LoginService.class);
    loginMapper = mock(LoginMapper.class);
    jwtTokenProvider = mock(IJwtTokenProvider.class);

    controller = new LoginController(loginService, loginMapper, jwtTokenProvider);

    //  Use existing constructor
    loginDTO = new LoginDTO("testUser", "password");

    user = mock(User.class);
    when(user.getUsername()).thenReturn("testUser");
    when(user.getRole()).thenReturn("READER");

    responseDTO = new LoginResponseDTO();
    responseDTO.setUsername("testUser");
    responseDTO.setToken("jwt-token");
  }

  @Test
  void login_returnsLoginResponseDTO() {
    when(loginMapper.mapLoginDTOToDomain(loginDTO)).thenReturn(user);
    when(loginService.login(user)).thenReturn(user);
    when(jwtTokenProvider.generateToken("testUser", "READER"))
        .thenReturn("jwt-token");
    when(loginMapper.mapDomainToLoginResponse("jwt-token", user))
        .thenReturn(responseDTO);

    ResponseEntity<LoginResponseDTO> response = controller.login(loginDTO);

    assertEquals(200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals("testUser", response.getBody().getUsername());
    assertEquals("jwt-token", response.getBody().getToken());

    verify(loginMapper).mapLoginDTOToDomain(loginDTO);
    verify(loginService).login(user);
    verify(jwtTokenProvider).generateToken("testUser", "READER");
    verify(loginMapper).mapDomainToLoginResponse("jwt-token", user);
  }
}
