package dk.via.sep3.model.login;

import dk.via.sep3.model.domain.User;

public interface LoginService
{
    User login(User user);
}

