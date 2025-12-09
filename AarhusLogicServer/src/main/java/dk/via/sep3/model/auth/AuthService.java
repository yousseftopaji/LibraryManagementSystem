package dk.via.sep3.model.auth;

import dk.via.sep3.shared.login.LoginRequest;
import dk.via.sep3.shared.login.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}

