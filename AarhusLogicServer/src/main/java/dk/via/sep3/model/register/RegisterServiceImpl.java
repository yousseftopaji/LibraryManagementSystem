package dk.via.sep3.model.register;

import dk.via.sep3.controller.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.controller.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.model.domain.User;
import dk.via.sep3.model.utils.validation.RegistrationValidator;
import dk.via.sep3.security.PasswordService;
import dk.via.sep3.shared.registration.RegistrationDTO;
import org.springframework.stereotype.Service;


@Service
public class RegisterServiceImpl implements RegisterService {
    private final UserGrpcService userGrpcService;
    private final PasswordService passwordService;
    private final RegistrationValidator registrationValidator;


    public RegisterServiceImpl(UserGrpcService userGrpcService,
                               PasswordService passwordService,
                               RegistrationValidator registrationValidator) {
        this.userGrpcService = userGrpcService;
        this.passwordService = passwordService;
        this.registrationValidator = registrationValidator;
    }

    @Override
    public User register(User user) {
        if (user == null)
            throw new BusinessRuleViolationException("Registration cannot be null");

        // Delegate **all field validation** to your validators
        registrationValidator.validate(user);

        // Hash password
        String hashedPassword = passwordService.hash(user.getPassword());
        user.setPassword(hashedPassword);

        // Persist user
        try {
            User created = userGrpcService.createUser(user);

            if (created == null) {
                throw new GrpcCommunicationException("Internal Server Error. Please try again later.");
            }

            return created;

        } catch (Exception e) {
            throw new GrpcCommunicationException("Failed to create user", e);
        }
    }
}
