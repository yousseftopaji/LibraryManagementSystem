package dk.via.sep3.model.users;

import dk.via.sep3.shared.user.UserDTO;
import dk.via.sep3.shared.registration.RegistrationDTO;

public interface UserService
{
    UserDTO getUserByUsername(String username);
    boolean usernameExists(String username);
    boolean register(RegistrationDTO registrationDTO);
}
