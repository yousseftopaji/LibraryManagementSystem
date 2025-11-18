package dk.via.sep3.model.users;

import dk.via.sep3.shared.UserDTO;

public interface UserService
{
    UserDTO getUserByUsername(String username);
}
