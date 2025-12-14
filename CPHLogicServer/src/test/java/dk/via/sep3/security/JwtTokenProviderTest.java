package dk.via.sep3.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

  private JwtTokenProvider tokenProvider;

  @BeforeEach
  void setUp() throws Exception {
    tokenProvider = new JwtTokenProvider();

    // Inject @Value fields manually
    setPrivateField("jwtSecret", "test-secret-key-test-secret-key-test-secret");
    setPrivateField("jwtExpirationMs", 3600000L);
  }

  private void setPrivateField(String fieldName, Object value) throws Exception {
    Field field = JwtTokenProvider.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(tokenProvider, value);
  }


  // generateToken()


  @Test
  void generateToken_returnsValidToken() {
    String token = tokenProvider.generateToken("user", "READER");

    assertNotNull(token);
    assertTrue(token.length() > 20);
  }


  // getUsernameFromToken()


  @Test
  void getUsernameFromToken_returnsCorrectUsername() {
    String token = tokenProvider.generateToken("user1", "ADMIN");

    String username = tokenProvider.getUsernameFromToken(token);

    assertEquals("user1", username);
  }


  // getRoleFromToken()


  @Test
  void getRoleFromToken_returnsCorrectRole() {
    String token = tokenProvider.generateToken("user2", "READER");

    String role = tokenProvider.getRoleFromToken(token);

    assertEquals("READER", role);
  }


  // validateToken()


  @Test
  void validateToken_validToken_returnsTrue() {
    String token = tokenProvider.generateToken("user3", "READER");

    assertTrue(tokenProvider.validateToken(token));
  }

  @Test
  void validateToken_invalidToken_returnsFalse() {
    assertFalse(tokenProvider.validateToken("invalid.token.value"));
  }
}
