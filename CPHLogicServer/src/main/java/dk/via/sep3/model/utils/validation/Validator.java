package dk.via.sep3.model.utils.validation;

public interface Validator
{
  void validateUser(String username);
  void validateFullName(String fullName);
  void validateEmail(String email);
  void validatePhoneNumber(String phoneNumber);
  void validateUsername(String username);
  void validatePassword(String password);
}