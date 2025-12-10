package dk.via.sep3.model.auth;

import dk.via.sep3.controller.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.security.PasswordService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserGrpcService userGrpcService;
    private final PasswordService passwordService;

    public AuthServiceImpl(UserGrpcService userGrpcService, PasswordService passwordService) {
        this.userGrpcService = userGrpcService;
        this.passwordService = passwordService;
    }
    @Override
    public User login(User request) {
        // validate inputs
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
           throw new BusinessRuleViolationException("Username and Password must be provided");
        }
        User user = userGrpcService.getUserByUsername(request.getUsername());

        if (user == null) {
           throw new BusinessRuleViolationException("User not found");
        }
        System.out.println(user.getUsername());
        boolean matches = passwordService.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            throw new BusinessRuleViolationException("Wrong password");
        }
        return user;
    }
}
