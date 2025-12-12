package dk.via.sep3.model.utils.validation;

import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component("emailValidator")
public class EmailValidator implements Validator<String> {
    private static final Pattern EMAIL = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    @Override
    public void validate(String email) {
        if (email == null || !EMAIL.matcher(email).matches()) {
            throw new BusinessRuleViolationException("Invalid email format");
        }
    }
}
