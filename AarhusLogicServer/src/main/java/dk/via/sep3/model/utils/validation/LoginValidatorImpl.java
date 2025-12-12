package dk.via.sep3.model.utils.validation;

import dk.via.sep3.model.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LoginValidatorImpl implements LoginValidator {
    private final Validator<String> userValidator;


    public LoginValidatorImpl (  @Qualifier("userValidator") Validator<String> userValidator){
        this.userValidator = userValidator;
    }

    @Override
    public void validate(User user) {
    }
}
