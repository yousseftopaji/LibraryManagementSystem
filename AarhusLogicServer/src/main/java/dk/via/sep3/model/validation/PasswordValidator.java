package dk.via.sep3.model.validation;

import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component("passwordValidator")
public class PasswordValidator implements Validator<String> {

    // Match an uppercase anywhere
    private static final Pattern UPPER = Pattern.compile(".*[A-Z].*");
    // Match a digit anywhere
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");

    @Override
    public void validate(String password) {
        if (password == null ||
                password.length() < 8 ||
                !UPPER.matcher(password).find() ||
                !DIGIT.matcher(password).find()) {
            throw new BusinessRuleViolationException(
                    "Password must be at least 8 characters and contain an uppercase and a digit."
            );
        }
    }
}
