package dk.via.sep3.model.users;

import dk.via.sep3.model.domain.User;
import dk.via.sep3.shared.user.UserDTO;

public interface UserService
{
    User getUserByUsername(String username);


}
