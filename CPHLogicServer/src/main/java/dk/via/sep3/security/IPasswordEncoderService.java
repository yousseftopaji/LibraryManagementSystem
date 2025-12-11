package dk.via.sep3.security;

public interface IPasswordEncoderService
{
  String encode(String password);
  boolean matches(String rawPassword, String encodedPassword);
}
