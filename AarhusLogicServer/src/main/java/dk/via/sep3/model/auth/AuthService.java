package dk.via.sep3.model.auth;

import dk.via.sep3.model.domain.User;

public interface AuthService {
    User login(User user);
}

