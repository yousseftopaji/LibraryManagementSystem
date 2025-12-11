package dk.via.sep3.model.utils.validation;

import dk.via.sep3.controller.exceptionHandler.BusinessRuleViolationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component("passwordValidator")
public class PasswordValidator implements Validator<String> {

    private static final Pattern UPPER = Pattern.compile(".[A-Z].");
    private static final Pattern DIGIT = Pattern.compile(".[0-9].");

    @Override
    public void validate(String password) {
        if (password == null ||
        password.length() < 8 ||
        !UPPER.matcher(password).matches() ||
                !DIGIT.matcher(password).matches()) {
            throw new BusinessRuleViolationException(
                    "Password must be at least 8 characters and contain an uppercase and a digit."
            );
        }
    }
}
