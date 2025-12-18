package dk.via.sep3.model.validation;

import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.model.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ValidatorImpl implements Validator
{
  private static final Logger logger = LoggerFactory.getLogger(ValidatorImpl.class);
    private final UserGrpcService userGrpcService;

    public ValidatorImpl(UserGrpcService userGrpcService)
    {
        this.userGrpcService = userGrpcService;
    }

    @Override public void validateUser(String username)
    {
        if (userGrpcService.getUserByUsername(username) == null)
        {
            throw new BusinessRuleViolationException("User not found with username: " + username);
        }
    }

  @Override
  public void validateFullName(String fullName) {
    if (fullName == null || fullName.trim().isEmpty()) {
      throw new BusinessRuleViolationException("Full name cannot be empty");
    }
    if (fullName.length() < 2) {
      throw new BusinessRuleViolationException("Full name must be at least 2 characters");
    }
  }

  @Override
  public void validateEmail(String email) {
    if (email == null || email.trim().isEmpty()) {
      throw new BusinessRuleViolationException("Email cannot be empty");
    }
    if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      throw new BusinessRuleViolationException("Invalid email format");
    }
  }

  @Override
  public void validatePhoneNumber(String phoneNumber) {
    logger.info("Validating phone number: '{}'", phoneNumber);
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
      throw new BusinessRuleViolationException("Phone number cannot be empty");
    }
    if (!phoneNumber.matches("^\\d{8,}$")) {
      logger.warn("Phone number validation failed for: '{}'. Pattern requires 8+ digits only.", phoneNumber);
      throw new BusinessRuleViolationException("Phone number must contain at least 8 digits");
    }
  }

  @Override
  public void validateUsername(String username) {
    if (username == null || username.trim().isEmpty()) {
      throw new BusinessRuleViolationException("Username cannot be empty");
    }
    if (username.length() < 3) {
      throw new BusinessRuleViolationException("Username must be at least 3 characters");
    }
    // Check if username already exists
    User user = userGrpcService.getUserByUsername(username);

    if (user != null) {
      throw new BusinessRuleViolationException("Username already exists");
    }
  }

  @Override
  public void validatePassword(String password) {
    if (password == null || password.trim().isEmpty()) {
      throw new BusinessRuleViolationException("Password cannot be empty");
    }
    if (password.length() < 8) {
      throw new BusinessRuleViolationException("Password must be at least 8 characters");
    }
  }
}