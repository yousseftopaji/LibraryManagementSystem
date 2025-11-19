package dk.via.sep3.model.users;

import dk.via.sep3.shared.user.UserDTO;

public interface UserService
{
    UserDTO getUserByUsername(String username);
}
