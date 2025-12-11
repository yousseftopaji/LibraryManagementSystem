package dk.via.sep3.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoderService implements IPasswordEncoderService
{
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  public String encode(String password)
  {
    return encoder.encode(password);
  }

  public boolean matches(String rawPassword, String encodedPassword)
  {
    return encoder.matches(rawPassword, encodedPassword);
  }
}
