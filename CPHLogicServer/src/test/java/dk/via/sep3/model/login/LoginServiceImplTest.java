package dk.via.sep3.model.login;

import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.security.IPasswordEncoderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceImplTest {

  private UserGrpcService userGrpc;
  private IPasswordEncoderService passwordEncoder;
  private LoginServiceImpl loginService;

  @BeforeEach
  void setup() {
    userGrpc = mock(UserGrpcService.class);
    passwordEncoder = mock(IPasswordEncoderService.class);
    loginService = new LoginServiceImpl(userGrpc, passwordEncoder);
  }


  // INVALID INPUT TESTS


  @Test
  void login_throwsIfUsernameIsEmpty() {
    User input = new User();
    input.setUsername("");
    input.setPassword("pass");

    assertThrows(IllegalArgumentException.class, () -> loginService.login(input));
  }

  @Test
  void login_throwsIfPasswordIsEmpty() {
    User input = new User();
    input.setUsername("john");
    input.setPassword("");

    assertThrows(IllegalArgumentException.class, () -> loginService.login(input));
  }


  // USER NOT FOUND


  @Test
  void login_throwsIfUserNotFound() {
    User input = new User();
    input.setUsername("john");
    input.setPassword("test");

    when(userGrpc.getUserByUsername("john")).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> loginService.login(input));
  }


  // PASSWORD INVALID

  @Test
  void login_throwsIfPasswordDoesNotMatch() {
    User input = new User();
    input.setUsername("john");
    input.setPassword("wrong");

    User dbUser = new User();
    dbUser.setUsername("john");
    dbUser.setPassword("hashed");

    when(userGrpc.getUserByUsername("john")).thenReturn(dbUser);
    when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> loginService.login(input));
  }


  // SUCCESSFUL LOGIN


  @Test
  void login_returnsUserOnSuccess() {
    User input = new User();
    input.setUsername("john");
    input.setPassword("correct");

    User dbUser = new User();
    dbUser.setUsername("john");
    dbUser.setPassword("hashed");

    when(userGrpc.getUserByUsername("john")).thenReturn(dbUser);
    when(passwordEncoder.matches("correct", "hashed")).thenReturn(true);

    User result = loginService.login(input);

    assertNotNull(result);
    assertEquals("john", result.getUsername());
    verify(userGrpc, times(1)).getUserByUsername("john");
    verify(passwordEncoder, times(1)).matches("correct", "hashed");
  }
}
