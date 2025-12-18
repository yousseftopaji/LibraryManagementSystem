package dk.via.sep3.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private SecurityFilterChain securityFilterChain;

  @Test
  void contextLoads_securityConfigurationIsValid() {
    assertNotNull(applicationContext);
  }

  @Test
  void passwordEncoderBean_existsAndEncodesPassword() {
    assertNotNull(passwordEncoder);

    String rawPassword = "password123";
    String encoded = passwordEncoder.encode(rawPassword);

    assertNotEquals(rawPassword, encoded);
    assertTrue(passwordEncoder.matches(rawPassword, encoded));
  }

  @Test
  void securityFilterChainBean_exists() {
    assertNotNull(securityFilterChain);
  }
}
