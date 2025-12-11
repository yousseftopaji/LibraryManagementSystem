package dk.via.sep3.model.utils.validation;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.registration.RegistrationDTO;
import dk.via.sep3.shared.user.UserDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RegistrationValidator {

    private final Validator<String> emailValidator;
    private final Validator<String> phoneValidator;
    private final Validator<String> passwordValidator;
    private final Validator<String> usernameValidator;

    public RegistrationValidator(
            @Qualifier("emailValidator") Validator<String> emailValidator,
            @Qualifier("phoneValidator") Validator<String> phoneValidator,
            @Qualifier("passwordValidator") Validator<String> passwordValidator,
            @Qualifier("usernameValidator") Validator<String> usernameValidator) {
        this.emailValidator = emailValidator;
        this.phoneValidator = phoneValidator;
        this.passwordValidator = passwordValidator;
        this.usernameValidator = usernameValidator;
    }

    public void validate(User user) {
        emailValidator.validate(user.getEmail());
        phoneValidator.validate(user.getPhoneNumber());
        passwordValidator.validate(user.getPassword());
        usernameValidator.validate(user.getUsername()
        );
    }
}
