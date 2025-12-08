package dk.via.sep3.model.utils.validation;

import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import org.springframework.stereotype.Service;

@Service
public class ValidatorImpl implements Validator
{
    private final UserGrpcService userGrpcService;

    public ValidatorImpl(UserGrpcService userGrpcService)
    {
        this.userGrpcService = userGrpcService;
    }

    @Override public void validateUser(String username)
    {
        if (userGrpcService.getUserByUsername(username) == null)
        {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
    }

  @Override
  public void validateFullName(String fullName) {
    if (fullName == null || fullName.trim().isEmpty()) {
      throw new IllegalArgumentException("Full name cannot be empty");
    }
    if (fullName.length() < 2) {
      throw new IllegalArgumentException("Full name must be at least 2 characters");
    }
  }

  @Override
  public void validateEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      throw new IllegalArgumentException("Email cannot be empty");
    }
    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      throw new IllegalArgumentException("Invalid email format");
    }
  }

  @Override
  public void validatePhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      throw new IllegalArgumentException("Phone number cannot be empty");
    }
    if (!phoneNumber.matches("^\\d{8,}$")) {
      throw new IllegalArgumentException("Phone number must contain at least 8 digits");
    }
  }

  @Override
  public void validateUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }
    if (username.length() < 3) {
      throw new IllegalArgumentException("Username must be at least 3 characters");
    }
    // Check if username already exists
    if (userGrpcService.getUserByUsername(username) != null) {
      throw new IllegalArgumentException("Username already in use");
    }
  }

  @Override
  public void validatePassword(String password) {
    if (password == null || password.trim().isEmpty()) {
      throw new IllegalArgumentException("Password cannot be empty");
    }
    if (password.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters");
    }
  }


}