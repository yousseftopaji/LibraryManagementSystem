package dk.via.sep3.application.services.registration;

import dk.via.sep3.application.domain.User;

public interface RegistrationService
{
  User register(User user);
}