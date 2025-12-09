package dk.via.sep3.model.auth;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.grpcConnection.userGrpcService.UserGrpcService;
import dk.via.sep3.security.PasswordService;
import dk.via.sep3.security.JwtUtil;
import dk.via.sep3.shared.login.LoginRequest;
import dk.via.sep3.shared.login.LoginResponse;
import dk.via.sep3.shared.user.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserGrpcService userGrpcService;
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserGrpcService userGrpcService, PasswordService passwordService, JwtUtil jwtUtil) {
        this.userGrpcService = userGrpcService;
        this.passwordService = passwordService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // validate inputs
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            return new LoginResponse(null, false, "Invalid request", null);
        }

        User user = userGrpcService.getUserByUsername(request.getUsername());
        if (user == null) {
            return new LoginResponse(null, false, "Invalid username or password", null);
        }

        boolean matches = passwordService.matches(request.getPassword(), user.getPassword());
        if (!matches) {
            return new LoginResponse(null, false, "Invalid username or password", null);
        }

        String token = jwtUtil.generateToken(user.getUsername());

        // Map domain user to DTO for response
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());

        return new LoginResponse(token, true, "Login successful", dto);
    }
}
