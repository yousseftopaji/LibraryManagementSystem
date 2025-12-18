package dk.via.sep3.application.services.validation;

import dk.via.sep3.application.domain.User;

public interface RegistrationValidator {
    void validate(User user);
}
