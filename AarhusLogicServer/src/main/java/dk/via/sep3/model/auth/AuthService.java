package dk.via.sep3.model.auth;

import dk.via.sep3.shared.auth.LoginRequest;
import dk.via.sep3.shared.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}

