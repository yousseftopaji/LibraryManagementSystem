package dk.via.sep3.application.services.validation;

import dk.via.sep3.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.application.domain.User;
import org.springframework.stereotype.Component;

@Component("usernameValidator")
public class UsernameValidator implements Validator<String>{
    private final UserGrpcService userGrpcService;
    public UsernameValidator(UserGrpcService userGrpcService) {
        this.userGrpcService = userGrpcService;
    }

    @Override
    public void validate(String username) {
        //check whether the username already exists in the system
       User user = userGrpcService.getUserByUsername(username);
         if(user != null) {
                throw new BusinessRuleViolationException("Username already exists");
         }
    }
}
