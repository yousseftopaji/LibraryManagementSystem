package dk.via.sep3.model.utils.validation;

import dk.via.sep3.controller.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.model.domain.User;

public interface LoginValidator {
    void validate(User user);





}
