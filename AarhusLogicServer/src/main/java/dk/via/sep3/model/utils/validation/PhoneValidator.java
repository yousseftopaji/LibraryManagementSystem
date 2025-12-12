package dk.via.sep3.model.utils.validation;

import dk.via.sep3.controller.exceptionHandler.BusinessRuleViolationException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component("phoneValidator")
public class PhoneValidator implements Validator<String> {

    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9\\- ]{7,20}$");

    @Override
    public void validate(String phone) {
        if (phone == null || !PHONE.matcher(phone).matches()) {
            throw new BusinessRuleViolationException("Invalid phone format");
        }
    }
}
