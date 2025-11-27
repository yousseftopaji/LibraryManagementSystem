package dk.via.sep3.model.utils.validation;

import dk.via.sep3.grpcConnection.userPersistenceService.UserPersistenceService;
import org.springframework.stereotype.Service;

@Service
public class ValidatorImpl implements Validator
{
  private final UserPersistenceService userPersistenceService;

  public ValidatorImpl(UserPersistenceService userPersistenceService)
  {
    this.userPersistenceService = userPersistenceService;
  }

  @Override public void validateUser(String username)
  {
    if (userPersistenceService.getUserByUsername(username) == null)
    {
      throw new IllegalArgumentException("User not found with username: " + username);
    }
  }
}
