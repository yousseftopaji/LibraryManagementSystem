package dk.via.sep3.application.services.login;

import dk.via.sep3.application.domain.User;

public interface LoginService
{
    User login(User user);
}

