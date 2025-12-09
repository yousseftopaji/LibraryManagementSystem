package dk.via.sep3.model.utils.validation;

import dk.via.sep3.controller.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.shared.registration.RegistrationDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component("passwordsMatchValidator")
public class PasswordsMatchValidator implements Validator<RegistrationDTO> {

    private final Validator<String> passwordValidator;

    public PasswordsMatchValidator(@Qualifier("passwordValidator") Validator<String> passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    @Override
    public void validate(RegistrationDTO dto) {
        if (dto == null) throw new BusinessRuleViolationException("Registration cannot be null");

        String pw = dto.getPassword();
        String confirm = dto.getConfirmPassword();

        if (!Objects.equals(pw, confirm)) {
            throw new BusinessRuleViolationException("password not same");
        }

        // Delegate to existing password policy validator
        passwordValidator.validate(pw);
    }
}
