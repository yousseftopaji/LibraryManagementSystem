package dk.via.sep3.model.users;

import dk.via.sep3.controller.exceptionHandler.BusinessRuleViolationException;
import dk.via.sep3.controller.exceptionHandler.GrpcCommunicationException;
import dk.via.sep3.controller.exceptionHandler.ResourceNotFoundException;
import dk.via.sep3.shared.user.UserDTO;
import dk.via.sep3.shared.registration.RegistrationDTO;
import dk.via.sep3.grpcConnection.PersistenceClient;
import dk.via.sep3.security.PasswordService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements  UserService
{
    private final PersistenceClient persistenceClient;
    private final PasswordService passwordService;

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9\\- ]{7,20}$");
    private static final Pattern PASSWORD_UPPER = Pattern.compile(".*[A-Z].*");
    private static final Pattern PASSWORD_DIGIT = Pattern.compile(".*[0-9].*");

    public UserServiceImpl(PersistenceClient persistenceClient, PasswordService passwordService) {
        this.persistenceClient = persistenceClient;
        this.passwordService = passwordService;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        if (!StringUtils.hasText(username)) return null;
        return persistenceClient.getUserByUsername(username);
    }

    @Override
    public boolean usernameExists(String username) {
        if (!StringUtils.hasText(username)) return false;
        try {
            return persistenceClient.usernameExists(username);
        } catch (Exception e) {
            // In case of an error, we propagate to caller (could also return false)
            throw new RuntimeException("Failed to check username uniqueness", e);
        }
    }

    @Override
    public boolean register(RegistrationDTO registrationDTO) {
        // basic null/blank checks
        if (registrationDTO == null) throw new BusinessRuleViolationException("Registration cannot be null");
        if (!StringUtils.hasText(registrationDTO.getUsername())) throw new BusinessRuleViolationException("Username is required");
        if (!StringUtils.hasText(registrationDTO.getPassword())) throw new BusinessRuleViolationException("Password is required");
        if (!StringUtils.hasText(registrationDTO.getEmail())) throw new BusinessRuleViolationException("Email is required");
        if (!StringUtils.hasText(registrationDTO.getFullName())) throw new BusinessRuleViolationException("Full name is required");

        // email & phone validation
        if (!EMAIL.matcher(registrationDTO.getEmail()).matches()) throw new BusinessRuleViolationException("Invalid email format");
        if (!PHONE.matcher(registrationDTO.getPhone()).matches()) throw new BusinessRuleViolationException("Invalid phone format");

        // password policy
        String pwd = registrationDTO.getPassword();
        if (pwd.length() < 8 || !PASSWORD_UPPER.matcher(pwd).matches() || !PASSWORD_DIGIT.matcher(pwd).matches()) {
            throw new BusinessRuleViolationException("Password must be at least 8 characters and contain at least one uppercase letter and one number.");
        }

        // check uniqueness
        if (usernameExists(registrationDTO.getUsername())) {
            throw new BusinessRuleViolationException("Username already in use.");
        }

        // hash password
        registrationDTO.setPassword(passwordService.hash(registrationDTO.getPassword()));

        // delegate to persistence
        try {
            boolean ok = persistenceClient.createUser(registrationDTO);
            if (!ok) throw new GrpcCommunicationException("Internal server error. Please try again later.");
            return true;
        } catch (Exception e) {
            throw new BusinessRuleViolationException("Registration failed. Please try again later.", e);
        }
    }
}
