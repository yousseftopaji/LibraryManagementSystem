package dk.via.sep3.model.registration;

import dk.via.sep3.model.domain.User;

public interface RegistrationService
{
  User register(User user);
}