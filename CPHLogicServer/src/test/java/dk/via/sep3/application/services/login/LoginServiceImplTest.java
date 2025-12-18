package dk.via.sep3.application.services.login;

import dk.via.sep3.application.domain.User;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.security.IPasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceImplTest {

  private UserGrpcService userGrpcService;
  private IPasswordEncoderService passwordEncoderService;
  private LoginServiceImpl loginService;

  @BeforeEach
  void setUp() {
    userGrpcService = mock(UserGrpcService.class);
    passwordEncoderService = mock(IPasswordEncoderService.class);
    loginService = new LoginServiceImpl(userGrpcService, passwordEncoderService);
  }

  // ------------------------------------------------------------
  // login() - success
  // ------------------------------------------------------------

  @Test
  void login_validCredentials_returnsUser() {
    User inputUser = new User();
    inputUser.setUsername("john");
    inputUser.setPassword("rawPassword");

    User dbUser = new User();
    dbUser.setUsername("john");
    dbUser.setPassword("hashedPassword");

    when(userGrpcService.getUserByUsername("john")).thenReturn(dbUser);
    when(passwordEncoderService.matches("rawPassword", "hashedPassword"))
        .thenReturn(true);

    User result = loginService.login(inputUser);

    assertEquals("john", result.getUsername());
    verify(userGrpcService).getUserByUsername("john");
    verify(passwordEncoderService).matches("rawPassword", "hashedPassword");
  }

  // ------------------------------------------------------------
  // login() - validation failures
  // ------------------------------------------------------------

  @Test
  void login_emptyUsername_throwsException() {
    User user = new User();
    user.setUsername("");
    user.setPassword("password");

    assertThrows(IllegalArgumentException.class,
        () -> loginService.login(user));
  }

  @Test
  void login_emptyPassword_throwsException() {
    User user = new User();
    user.setUsername("john");
    user.setPassword("");

    assertThrows(IllegalArgumentException.class,
        () -> loginService.login(user));
  }

  // ------------------------------------------------------------
  // login() - authentication failures
  // ------------------------------------------------------------

  @Test
  void login_userNotFound_throwsException() {
    User user = new User();
    user.setUsername("john");
    user.setPassword("password");

    when(userGrpcService.getUserByUsername("john")).thenReturn(null);

    assertThrows(IllegalArgumentException.class,
        () -> loginService.login(user));
  }

  @Test
  void login_wrongPassword_throwsException() {
    User user = new User();
    user.setUsername("john");
    user.setPassword("wrongPassword");

    User dbUser = new User();
    dbUser.setUsername("john");
    dbUser.setPassword("hashedPassword");

    when(userGrpcService.getUserByUsername("john")).thenReturn(dbUser);
    when(passwordEncoderService.matches("wrongPassword", "hashedPassword"))
        .thenReturn(false);

    assertThrows(IllegalArgumentException.class,
        () -> loginService.login(user));
  }
}
